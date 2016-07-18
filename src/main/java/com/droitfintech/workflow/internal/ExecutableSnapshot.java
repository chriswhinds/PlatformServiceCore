package com.droitfintech.workflow.internal;

import com.droitfintech.workflow.exceptions.WorkflowException;
import com.droitfintech.workflow.internal.repository.Module;
import com.droitfintech.workflow.internal.repository.Snapshot;
import com.droitfintech.workflow.internal.repository.VersionMetadata;
import com.droitfintech.workflow.repository.WorkflowRepositoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExecutableSnapshot {

	private static Logger _logger = LoggerFactory
			.getLogger(ExecutableSnapshot.class);
	private boolean isQuickStartup;

	private Map<String, ExecutableModule> moduleMap = new HashMap<String, ExecutableModule>();
	private VersionMetadata versionMetadata;
	ArrayList<String> clientModuleNames = new ArrayList<String>();

	/**
	 * Compile a snapshot
	 * 
	 * @param repository
	 */
	public ExecutableSnapshot(String snapshotId,
			WorkflowRepositoryService repository) {

		initQuickStart();

		_logger.debug("Building executable work flows for snapshot {}", snapshotId);
		Snapshot snapshot = repository.getSnapshot(snapshotId);
		for (String moduleRef : snapshot.getModuleRefs()) {

			if (this.isQuickStartup) {
				if (!StringUtils.startsWith(moduleRef, "cftc_sdr")) {
					_logger.warn("In quick startup mode.  Not loading workflow '{}'", moduleRef);
					continue;
				}
			}

			Module module = repository.getModuleForSnapshot(snapshot, moduleRef);
			_logger.debug("Loading executable module {}", moduleRef);
			if(repository.isClientId(moduleRef)) {
				clientModuleNames.add(module.getMetadata().getEntityName());
			}
			this.insert(module.createExecutable());
		}
		this.versionMetadata = snapshot.getMetadata();
		_logger.debug("Finished building executable work flows for snapshot {}, num client modules {}",
				snapshotId, clientModuleNames.size());
	}

	public Collection<String> getClientModuleNames() {
		return clientModuleNames;
	}
	
	public ExecutableSnapshot() {
		initQuickStart();
	}

	/**
	 * Retrieve a module from cache
	 * 
	 * @return
	 */
	public ExecutableModule getModule(String moduleName) {

		try {
			return moduleMap.get(moduleName);
		} catch (Exception e) {
			throw new WorkflowException("Unable to retrieve '" + moduleName
					+ "': " + e.getMessage() + ": " + e.getStackTrace()[0]);
		}
	}


	public void insert(ExecutableModule module) {
		moduleMap.put(module.getName(), module);
	}

	/**
	 * Retrieve list of all compiled modules in system
	 * 
	 * @return
	 */
	public List<String> getModuleNames() {

		ArrayList<String> list = new ArrayList<String>(moduleMap.keySet());
		Collections.sort(list);
		return list;
	}
	
	public VersionMetadata getVersionMetadata() {
		return this.versionMetadata;
	}

	private void initQuickStart() {
		this.isQuickStartup = System.getProperties().getProperty("com.droitfintech.workflow.quickStartup") != null;
	}
}
