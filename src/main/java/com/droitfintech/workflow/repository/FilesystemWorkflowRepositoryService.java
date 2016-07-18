package com.droitfintech.workflow.repository;

import au.com.bytecode.opencsv.CSVReader;
import com.droitfintech.exceptions.DroitException;

import com.droitfintech.utils.FilesystemDataLoader;
import com.droitfintech.datadictionary.DictionaryUpdateListener;

import com.droitfintech.datadictionary.FSDictionary;
import com.droitfintech.datadictionary.Dictionary;
import com.droitfintech.workflow.exceptions.WorkflowException;
import com.droitfintech.workflow.internal.*;
import com.droitfintech.workflow.internal.repository.Module;
import com.droitfintech.workflow.internal.repository.Snapshot;
import com.droitfintech.workflow.internal.repository.VersionMetadata;
import com.droitfintech.workflow.validation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FilesystemRepository (C) 2014 Droit Financial Technologies, LLC
 *
 * A fully-functioning workflow repository which uses the filesystem as its
 * backend. This may be used either as a Spring bean or a POJO.
 *
 * @author nathanbrei, barry cohen
 *
 */

public class FilesystemWorkflowRepositoryService implements WorkflowRepositoryService  {
	protected static final String VALID_MODULE_FILE_NAME_RE = "([a-zA-Z_0-9]+)\\.([a-zA-Z_0-9]+)\\.([0-9]+)\\.([0-9]+)\\.json$";
	private static final Logger _logger = LoggerFactory.getLogger(FilesystemWorkflowRepositoryService.class);
	public ObjectMapper mapper = new ObjectMapper();
	protected String clientName = "";
	protected String liveSnapshotId = "";
	protected Snapshot liveSnapshot;
	protected String latestDroitSnapshotId; //
	protected Map<String, Module> moduleCache = new HashMap<String, Module>();
	private Dictionary liveSnapshotDataDict = null;

	protected static String droitSnapshotsSubDirectory;
	protected static String clientSnapshotsSubDirectory;
	protected static final String LEGACY_SNAPSHOT = "snapshot.droit.20110716.1";
	protected final Pattern snapshotDirNameMatcher = Pattern.compile("^snapshot\\.([a-zA-Z_][a-zA-Z0-9_]*)\\.([0-9]+\\.[0-9]+)$"); //<sol>snapshot.<cust>,<major_version>.<minor_version><eol>
	private boolean isInitialized = false;

	private Set<DictionaryUpdateListener> updateListeners;

	private static class SnapshotNameComparator implements Comparator<String> {

		public int compare(String o1, String o2) {
			if(o1 == null) return -1;
			if(o2 == null) return 1;
			String[] s1Parts = o1.split("\\.");
			String[] s2Parts = o2.split("\\.");
			// add a space so droit snapshots sort before all client names
			if(s1Parts[1].equals("droit")) s1Parts[1] = " droit";
			if(s2Parts[1].equals("droit")) s2Parts[1] = " droit";
			// add 2 spaces so the legacy snapshot sorts first.
			if(LEGACY_SNAPSHOT.equals(o1)) s1Parts[1] = "  droit";
			if(LEGACY_SNAPSHOT.equals(o2)) s2Parts[1] = "  droit";
			int rc = s1Parts[1].compareTo(s2Parts[1]);
			if(rc == 0) {
				int i1 = Integer.parseInt(s1Parts[2]);
				int i2 = Integer.parseInt(s2Parts[2]);
				rc = i1 - i2;
			}
			if(rc == 0) {
				int i1 = Integer.parseInt(s1Parts[3]);
				int i2 = Integer.parseInt(s2Parts[3]);
				rc = i1 - i2;
			}
			return rc * -1; // reverse direction of sort
		}
	}



	public void initialize() {


		DroitException.assertNotNull(droitSnapshotsSubDirectory, "'sourceDir' directory property not set.");
		DroitException.assertNotNull(clientSnapshotsSubDirectory, "'clientSourceDit' directory property not set.");

		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		this.isInitialized = true;
	}

	public void refresh() {
		refresh(clientName);
	}

	public void refresh(String clientName) {
		this.clientName = clientName;
		_logger.info("Refreshing FilesystemRepository client name is {} ", clientName);
		Map<String,Snapshot> snapMap = getAllSnapshotsAvailable(clientName);
		for(String snapId : snapMap.keySet()) {
			Snapshot snapshot = snapMap.get(snapId);
			if(VersionMetadata.LIVE_STATUS.equals(snapshot.getMetadata().getStatus())
					|| VersionMetadata.TEST_STATUS.equals(snapshot.getMetadata().getStatus())) {
				liveSnapshotId = snapId;
				liveSnapshot = snapshot;
				if(isClientId(snapId)) {
					liveSnapshot = readSnapshotFile(snapId); // need to use read that merges with underlying droit snapshot.
					_logger.info("Loading client snapshot {}, and underlying droit snapshot {}", snapId, liveSnapshot.getMetadata().getParentVersionId());
				} else {
					latestDroitSnapshotId = snapId;
					_logger.info("Loading droit snapshot {}", clientName);
				}
				this.moduleCache = readSnapshotModules(liveSnapshot);
				liveSnapshotDataDict = null; // force data dictionary reload.
				getDataDictionaryForLiveSnapshot();
				break;
			}
		}
		// if we loaded a client snapshot we still need to find the latest droit snapshot.
		if(latestDroitSnapshotId == null) {
			for(String snapId : snapMap.keySet()) {
				Snapshot snapshot = snapMap.get(snapId);
				if(!isClientId(snapId) && (VersionMetadata.LIVE_STATUS.equals(snapshot.getMetadata().getStatus())
						|| VersionMetadata.TEST_STATUS.equals(snapshot.getMetadata().getStatus()))) {
					latestDroitSnapshotId = snapId;
					break;
				}
			}
		}
		_logger.info("Workflow service refresh complete.");
	}

	/**
	 * Get the list of available snapshots ordered so that the first one with a status other then development is the live snapshot.
	 * The snapshot returned for each client snapshot does not include the droit modules.
	 * @param clientId
	 * @return
	 */
	protected Map<String,Snapshot> getAllSnapshotsAvailable(String clientId) {
		TreeMap<String, Snapshot> snapMap = new TreeMap<String, Snapshot>(new SnapshotNameComparator());
		try {
			File snapshotDirDir = FilesystemDataLoader.loadDirectory(droitSnapshotsSubDirectory);
			for (File snapshotDir : snapshotDirDir.listFiles()) {
				String expr = snapshotDir.getName();
				Matcher matcher = snapshotDirNameMatcher.matcher(expr);
				if (matcher.find()) {
					if (matcher.start(1) > 0) {
						String custName = expr.substring(matcher.start(1), matcher.end(1));
						if( (StringUtils.isNotBlank(clientId) && clientId.matches(custName)) || "droit".equals(custName)) {
							String snapshotSource = readFileFullPath(snapshotDir.getAbsoluteFile().getAbsolutePath() + "/" + expr + ".json");
							try {
								snapMap.put(expr, mapper.readValue(snapshotSource, Snapshot.class));
							} catch (Exception e) {
								_logger.warn("Unable to load snapshot {}", expr);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			_logger.error("Could not load workflow definitions.  Can not open directory " + droitSnapshotsSubDirectory);
		}
		try {
			File snapshotDirDir = FilesystemDataLoader.loadDirectory(clientSnapshotsSubDirectory);
			for (File snapshotDir : snapshotDirDir.listFiles()) {
				String expr = snapshotDir.getName();
				Matcher matcher = snapshotDirNameMatcher.matcher(expr);
				if (matcher.find()) {
					if (matcher.start(1) > 0) {
						String custName = expr.substring(matcher.start(1), matcher.end(1));
						if( (StringUtils.isNotBlank(clientId) && clientId.matches(custName)) || "droit".equals(custName)) {
							String snapshotSource = readFileFullPath(snapshotDir.getAbsoluteFile().getAbsolutePath() + "/" + expr + ".json");
							try {
								snapMap.put(expr, mapper.readValue(snapshotSource, Snapshot.class));
							} catch (Exception e) {
								_logger.warn("Unable to load snapshot {}", expr);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			_logger.info("Not loading client snapshots.  None found in '{}'", clientSnapshotsSubDirectory);
		}
		try {
			File legacyFile = FilesystemDataLoader.loadFile(droitSnapshotsSubDirectory + "/snapshots/" + LEGACY_SNAPSHOT + ".json");
			String snapshotSource = readFileFullPath(legacyFile.getAbsoluteFile().getAbsolutePath());
			try {
				snapMap.put(LEGACY_SNAPSHOT, mapper.readValue(snapshotSource, Snapshot.class));
			} catch (Exception e) {
				_logger.warn("Unable to load snapshot {}", LEGACY_SNAPSHOT);
			}
		} catch (Exception ignore) {}
		return snapMap;
	}
	/**
	 * Return true if snapshot or module ID is a client managed one.
	 */

	public boolean isClientId(String snapshotOrModuleId) {
		String branch = getBranchFromId(snapshotOrModuleId);
		return branch != null && !branch.equals("droit");
	}

	private FSDictionary loadDictionary(String snapshotId) {
		FSDictionary newDict = new FSDictionary();
		if(isClientId(liveSnapshotId)) {
			newDict.load(getDirectoryPath(liveSnapshot.getMetadata().getParentVersionId()).toString(),
					getDirectoryPath(liveSnapshotId).toString());
		} else {
			newDict.load(getDirectoryPath(liveSnapshotId).toString(), null);
		}
		return newDict;
	}


	public Dictionary getDataDictionaryForLiveSnapshot() {
		if(liveSnapshotDataDict != null)
			return liveSnapshotDataDict;
		synchronized (this) {
			if(liveSnapshotDataDict == null) {
				_logger.info("Loading data dictionary for {} ", liveSnapshotId);
				FSDictionary newDict = loadDictionary(liveSnapshotId);
				liveSnapshotDataDict = newDict;
			}
		}

		notifyDictionaryUpdateListeners(liveSnapshotDataDict);

		return liveSnapshotDataDict;
	}

	private void notifyDictionaryUpdateListeners(Dictionary liveSnapshotDataDict) {
		if(updateListeners != null) {
			for (DictionaryUpdateListener listener : updateListeners) {
				listener.onDictionaryUpdate(liveSnapshotDataDict);
			}
		}
	}


	public synchronized void registerDictionaryListeners(DictionaryUpdateListener listener) {
		if (updateListeners == null)	 {
			updateListeners = new HashSet();
		}

		updateListeners.add(listener);
	}

	/**
	 * Method used to load only the modules that are referenced in a snapshot. Handles references to droit modules in
	 * a client snapshot. The keys in the returned map are the module ID's with client and version.
	 * @param snapshot
	 * @return
	 */
	public Map<String, Module> readSnapshotModules(Snapshot snapshot) {
		HashMap<String, Module> snapshotModuleCache = new HashMap<String, Module>();
		for(String moduleId : snapshot.getModuleRefs()) {
			if(snapshot.getMetadata().getBranch().equals(getBranchFromId(moduleId))) {
				snapshotModuleCache.put(moduleId, readModule(snapshot.getMetadata().getId(), moduleId));
			}
			else {
				snapshotModuleCache.put(moduleId, readModule(snapshot.getMetadata().getParentVersionId(), moduleId));
			}
		}
		return snapshotModuleCache;
	}

	/**
	 * Read all modules in the snapshot directory and return a map of names to modules.
	 * This DOES NOT directly set this.moduleCache so the editor derived class
	 * can use it to read multiple snapshots into local maps as needed.
	 * The keys in the returned map are the module ID's with client and version.
	 * @param snapshotIdToLoad
	 * @return
	 */
	public Map<String, Module> readAllModulesForSnapshot(String snapshotIdToLoad) {
		String modulesDir = getDirectoryPath(snapshotIdToLoad).toString();
		File moduleDir = new File(modulesDir);
		DroitException.assertThat(moduleDir.exists(),
				"Could not load workflow definitions.  Cannot find module directory at " + modulesDir);
		HashMap<String, Module> snapshotModuleCache = new HashMap<String, Module>();
		for (File moduleFile : moduleDir.listFiles()) {
			// Regex to restrict to <alpha_numeric>.<alpha_numeric>.<number>.<number>.json
			if(moduleFile.getName().matches(VALID_MODULE_FILE_NAME_RE) && !moduleFile.getName().startsWith("snapshot.")) {
				String moduleId = moduleFile.getName().replaceAll("\\.json", "");
				try {
					snapshotModuleCache.put(moduleId, readModuleFullPath(moduleFile.getPath()));
				}
				catch (Exception e) {
					_logger.error("Syntax error in module " + moduleId, e);
				}
			} else {
				_logger.info("Skipping module named " + moduleFile.getName());
			}
		}
		_logger.debug("Loaded modules from {} with {} modules",  modulesDir,  snapshotModuleCache.size());
		return snapshotModuleCache;
	}

	/**
	 * Load the latest version of a single module. Currently used in workflow validation and autocomplete to load and
	 * evaluate modules as they are referenced.
	 * @param snapshotIdToLoad
	 * @param modulePrefix
	 * @return
	 */
	public Module loadLatestModuleFor(String snapshotIdToLoad, String modulePrefix) {
		String modulesDir = getDirectoryPath(snapshotIdToLoad).toString();
		File moduleDir = new File(modulesDir);
		DroitException.assertThat(moduleDir.exists(),
				"Could not load workflow definitions.  Cannot find module directory at " + modulesDir);
		ArrayList<String> candidates = new ArrayList<String>();
		for (File moduleFile : moduleDir.listFiles()) {
			// Regex to restrict to <alpha_numeric>.<alpha_numeric>.<number>.<number>.json
			if(moduleFile.getName().matches(VALID_MODULE_FILE_NAME_RE) && !moduleFile.getName().startsWith("snapshot.")) {
				if(moduleFile.getName().startsWith(modulePrefix) ) {
					candidates.add(moduleFile.getName());
				}
			}
		}
		if(candidates.size() > 0) {
			Collections.sort(candidates,new SnapshotNameComparator());
			String fileNameFullPath = modulesDir + "/" + candidates.get(0);
			try {
				return readModuleFullPath(fileNameFullPath);
			}
			catch (Exception e) {
				throw new WorkflowException("Unable to load module " + candidates.get(0), e);
			}
		}
		return null;
	}

	/**
	 * This method is only called by the EscalationFileSystemService which needs to be rewritten . Part of that shoud be to use the cached
	 * workflows instead of reading them yet again.
	 * @param snapshotId
	 * @param moduleId
	 * @return
	 */
	public String readModuleAsString(String snapshotId, String moduleId){
		return readFileFullPath(getDirectoryPath(snapshotId).toString() + File.separator + moduleId + ".json");
	}

	protected String readFileFullPath(String filename) {
		StringBuffer msg = new StringBuffer();
		try {
			InputStream fileStream = new FileInputStream(filename);
			Reader input = new InputStreamReader(fileStream);
			BufferedReader bufRead = new BufferedReader(input);
			String line;
			line = bufRead.readLine();
			while (line != null) {
				msg.append(line + "\n");
				line = bufRead.readLine();
			}
			bufRead.close();
		} catch (Exception e) {
			throw new WorkflowException("Error reading file " + filename, e);
		}
		return msg.toString();
	}



	public String getLiveSnapshot() {
		return liveSnapshotId;
	}


	public Module getModule(String snapshotId, String moduleId) {

		if (liveSnapshot.getMetadata().getId().equals(snapshotId) && moduleCache.containsKey(moduleId)) {
			return moduleCache.get(moduleId);
		} else {
			return readModule(snapshotId, moduleId);
		}
	}


	public Module getModuleForSnapshot(Snapshot snapshot, String moduleId) {
		if(!snapshot.getMetadata().getBranch().equals(getBranchFromId(moduleId))) {
			return getModule(snapshot.getMetadata().getParentVersionId(), moduleId);
		} else {
			return getModule(snapshot.getMetadata().getId(), moduleId);
		}
	}

	/**
	 * Used from work flow editor service to get a fresh module to modify and add back with a new name/date/version
	 * @param moduleId
	 * @return
	 */
	public Module readModule(String snapshotId, String moduleId) {
		String moduleSource = getDirectoryPath(snapshotId).toString() + File.separator + moduleId + ".json";
		return readModuleFullPath(moduleSource);
	}

	/**
	 * Read a module from it's file using full path to the file on the local file system.
	 * Used from work flow editor service to get a fresh module to modify and add back with a new name/date/version
	 * @return
	 */
	public Module readModuleFullPath(String moduleFilePath) {
		if(!moduleFilePath.endsWith(".json"))
			moduleFilePath += ".json";
		String moduleSource = readFileFullPath(moduleFilePath);
		Module module;
		try {
			module = mapper.readValue(moduleSource, Module.class);
		} catch (Exception e) {
			throw new WorkflowException("Unable to read " + moduleFilePath, e);
		}
		if(module instanceof CsvModule) {
			if(StringUtils.isNotBlank( ((CsvModule) module).getCsvfile()) ) {
				String csvDir = moduleFilePath.substring(0, moduleFilePath.lastIndexOf("/"));
				csvDir += "/csv/" + ((CsvModule) module).getCsvfile();
				try {
					((CsvModule) module).setCsvData(getCsvModuleDataFullPath(csvDir));
				} catch (Exception ex) {
					_logger.warn("Unable to read csv file " + ((CsvModule) module).getCsvfile() + " for module " + moduleSource, ex);
				}
			}
		}
		return module;
	}



	public Snapshot getSnapshot(String snapshotId) {
		if(liveSnapshotId.equals(snapshotId))
			return liveSnapshot;
		else
			return readSnapshotFile(snapshotId);
	}


	public Snapshot readSnapshotFile(String snapshotId) {
		if(LEGACY_SNAPSHOT.equals(snapshotId)) {
			return readLegacySnapshotFile();
		}
		String filename = getDirectoryPath(snapshotId).toString() + File.separator + snapshotId + ".json";
		_logger.trace("Trying to load snapshot from file: " + filename);
		String snapshotSource = readFileFullPath(filename);

		Snapshot snapshot;
		try {
			snapshot = mapper.readValue(snapshotSource, Snapshot.class);
		} catch (Exception e) {
			throw new WorkflowException("Unable to load snapshot "
					+ snapshotId, e);
		}
		if(isClientSnapshot(snapshot)) {
			Snapshot underlyingDroitSnapshot = readSnapshotFile(snapshot.getMetadata().getParentVersionId());
			mergeDroitIntoClientSnapshot(snapshot.getModuleRefs(), underlyingDroitSnapshot.getModuleRefs());
		}
		return snapshot;
	}

	protected Snapshot readLegacySnapshotFile() {
		String filename = droitSnapshotsSubDirectory + File.separator + "snapshots"
				+ File.separator + LEGACY_SNAPSHOT + ".json";

		_logger.trace("Trying to load snapshot from file: " + filename);
		File snFile = FilesystemDataLoader.loadFile(filename);
		String snapshotSource = readFileFullPath(snFile.getPath());

		Snapshot snapshot;
		try {
			snapshot = mapper.readValue(snapshotSource, Snapshot.class);
			snapshot.getMetadata().setStatus(VersionMetadata.LIVE_STATUS);
		} catch (Exception e) {
			return null;
		}
		return snapshot;
	}

	public void setSourceDir(String directory) {
		this.droitSnapshotsSubDirectory = directory;
	}

	public void setClientSourceDir(String directory) {
		this.clientSnapshotsSubDirectory = directory;
	}

	public Path getDirectoryPath(String snapshotId) {
		String modulesSubdir = snapshotId.equals(LEGACY_SNAPSHOT) ? "modules" : snapshotId;
		Path path;
		try {
			path = FilesystemDataLoader.buildPath(clientSnapshotsSubDirectory + "/" + modulesSubdir);
			if (Files.isDirectory(path))
				return path;
		} catch (Exception ignore) { }
		return FilesystemDataLoader.buildPath(droitSnapshotsSubDirectory + "/" + modulesSubdir);
	}


	public boolean isInitialized() {
		return isInitialized;
	}

	public boolean isClientSnapshot(Snapshot snapshot) {
		return ! snapshot.getMetadata().getBranch().equalsIgnoreCase("droit");
	}

	public String getLatestDroitSnapshotId() {
		return latestDroitSnapshotId;
	}

	public Collection<String> getCsvFileListForModule(String snapshotId) {
		ArrayList<String> retList = new ArrayList<String>();
		String snapshotDirName = getDirectoryPath(snapshotId).toString() + "/csv";
		try {
			File snapshotDir = new File(snapshotDirName);
			if (snapshotDir.exists()) {
				for (File file : snapshotDir.listFiles()) {
					if (file.getName().endsWith(".csv")) {
						retList.add(file.getName());
					}
				}
			} else {
				_logger.info("could not read csv dir " + snapshotDirName);
			}
		} catch (Exception ex) {
			_logger.info("could not read csv dir " + snapshotDirName);
		}
		return retList;
	}

	public CsvModuleData getCsvModuleData(String snapshotId, String fileName) {
		return getCsvModuleDataFullPath(getDirectoryPath(snapshotId).toString() + "/csv/" + fileName);
	}

	public CsvModuleData getCsvModuleDataFullPath(String filePath) {
		CsvModuleData ret = new CsvModuleData();
		try {
			File file = new File(filePath);
			if(file.canRead()) {
				String[] headers = null;
				ArrayList<List<String>> dataArray = new ArrayList<List<String>>();
				CSVReader csv = new CSVReader(new FileReader(file));
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String[] fields;
				while ((fields = csv.readNext()) != null) {
					if(fields.length > 0) {
						if(headers == null) {
							headers = fields;
							ret.setHeaders(new ArrayList(Arrays.asList(headers)));
						} else {
							// Copy into real array list so we can truncate or extend.
							List<String> lineList = new ArrayList(Arrays.asList(fields));
							while(lineList.size() > headers.length) {
								lineList.remove(lineList.size()-1);
							}
							while(lineList.size() < headers.length) {
								lineList.add("");
							}
							dataArray.add(lineList);
						}
					}
				}
				reader.close();
				ret.setData(dataArray);
			}
		} catch (Exception ex ) {
			_logger.warn("Error reading csv module file " + filePath, ex);
		}
		return ret;
	}

	/**
	 * Validate that a new module does not duplicate any escalations in the existing snapshot module.
	 * Can also be called without newModule to fine existing dups in the snapshot.
	 * @param snapshot
	 * @param newModule
	 * @return
	 */
	public Map<String,String> checkForDuplicateEscalations(Snapshot snapshot, Module newModule) {
		GroovyValidator validator = new GroovyValidator(newModule, loadDictionary(snapshot.getMetadata().getId()), snapshot.getMetadata().getId(), new ModuleValidator(this, newModule));
		Map<String, String> escalationToModNameMap = new HashMap<String, String>();
		Map<String, String> dupEscalationMap = new HashMap<String, String>();
		Map<String, Module> snapshotModules = readSnapshotModules(snapshot);
		Collection<Module> moduleList = new ArrayList<Module>(snapshotModules.size());
		// if checking for dups in a new module make it the first one and remove any old version of the
		// module currently in the snapshot.
		if(newModule != null) {
			moduleList.add(newModule);
			for(Module module : snapshotModules.values()) {
				if(!newModule.getMetadata().getEntityName().equals(module.getMetadata().getEntityName())) {
					moduleList.add(module);
				}
			}
		} else {
			moduleList = snapshotModules.values();
		}
		boolean firstModule = true;
		for(Module module : moduleList) {
			if(module instanceof FlowchartModule && ! (module instanceof ParameterizedModule)) {
				FlowchartModule fm = (FlowchartModule)module;
				for(FlowchartNode node : fm.getNodes()) {
					if(StringUtils.isNotBlank(node.getExpr()) && node.getExpr().contains("escalation")) {
						SnipitResponse resp = validator.validateSnipit(node.getExpr());
						Map<String, Object> oMap = resp.getOutputMap();
						if(oMap != null) {
							Object esalation = oMap.get("escalation");
							if(esalation instanceof  String && StringUtils.isNotBlank((String)esalation)) {
								String eStr = (String)esalation;
								String modName = module.getMetadata().getId();
								if(escalationToModNameMap.containsKey(esalation)) {
									dupEscalationMap.put(eStr, modName + " " + escalationToModNameMap.get(eStr));
								} else {
									escalationToModNameMap.put(eStr, modName);
								}
							}
						}
					}
				}
			}
			if(firstModule && newModule != null) {
				// if first module is one we are looking for dupes against and it has no
				// escalations then no need to go further.
				if(escalationToModNameMap.isEmpty()) {
					return dupEscalationMap;
				}
			}
			firstModule = false;
		}
		return dupEscalationMap;
	}
	/**
	 *
	 * Given a module or snapshot id in the form <name>.<branch>.<major#>.<minor#> return <branch>
	 * @param id
	 * @return
	 */
	protected String getBranchFromId(String id) {
		String[]parts = id.split("\\.");
		return parts.length == 4 ? parts[1] : "";
	}

	/**
	 * Return just the name of the module without the branch and version.
	 * @param moduleId
	 * @return
	 */
	protected String moduleBaseName(String moduleId) {
		String[]parts = moduleId.split("\\.");
		return parts[0];
	}

	private void mergeDroitIntoClientSnapshot(List<String> destinationSnapshot, List<String> sourceSnapshot) {
		for(String moduleName : sourceSnapshot) {
			boolean exists = false;
			int firstDotIndex = moduleName.indexOf('.');
			if(firstDotIndex > 0) {
				String modNameMatch = moduleName.substring(0, firstDotIndex + 1);
				for (String key : destinationSnapshot) {
					if (key.startsWith(modNameMatch)) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					destinationSnapshot.add(moduleName);
				}
			}
		}
	}

	/**
	 * Find the latest version of a module in a droit or client snapshot
	 * @param snapshotId client or droit snapshot id.
	 * @param modulePrefix Module name including the trailing dot but without branch and version
	 * @return
	 */
	public Module findLatestModuleVersionInSnapshot(String snapshotId, String modulePrefix) {
		Module mod = loadLatestModuleFor(snapshotId, modulePrefix);
		if(mod == null && isClientId(snapshotId)) {
			Snapshot snapshot = readSnapshotFile(snapshotId);
			if(snapshot != null) {
				mod = loadLatestModuleFor(snapshot.getMetadata().getParentVersionId(), modulePrefix);
			}
		}
		return mod;
	}

	/**
	 * Perform full module validation returning errors, warnings and the full set of outputs the module produces.
	 * @param module
	 * @param dict
	 * @return
	 */
	public ValidationResponse validateModule(Module module, FSDictionary dict, String snapshotId) {
		ModuleValidator validator = new ModuleValidator(this, module);
		return validator.validateModule(module,dict, snapshotId);
	}
}
