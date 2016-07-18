package com.droitfintech.workflow.service;


import com.codahale.metrics.MetricRegistry;
import com.droitfintech.config.DecisionsConfiguration;
import com.droitfintech.exceptions.DroitException;

import com.droitfintech.services.RequestContext;
import com.droitfintech.utils.BucketedMap;
import com.droitfintech.fx.FxConverter;

import com.droitfintech.workflow.internal.ExecutableSnapshot;
import com.droitfintech.workflow.internal.WorkflowCache;
import com.droitfintech.workflow.internal.repository.Snapshot;
import com.droitfintech.workflow.internal.repository.VersionMetadata;
import com.droitfintech.workflow.internal.repository.WorkflowEvaluator;
import com.droitfintech.workflow.repository.WorkflowRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

public class WorkflowService  {
	
	private Logger _logger = LoggerFactory.getLogger(this.getClass());

	private String liveSnapshotId = "";
	private ArrayList<String> webToolWhiteListedModules = new ArrayList<String>(100);
	private ArrayList<String> etradingWhiteListedModules = new ArrayList<String>(100);
	private ArrayList<String> batchReportWhiteListedModules = new ArrayList<String>(100);
	// Map of session ID's do development snapshots. These are set up by the workflow editor to override the live snapshot
	// for specific web user sessions by the @WorkflowController class. 
	private HashMap<String, String> devSnapshots = new HashMap<String, String>();
	private boolean strictAttributeChecking = true;
	private String clientName = "";
	private boolean collectEscalations;
	// Dependencies

	public WorkflowRepositoryService getRepository() {
		return repository;
	}


	private WorkflowRepositoryService repository;

	private WorkflowCache executableSnapshotCache;

    private FxConverter fxConverter;

	private DecisionsConfiguration decisionsConfiguration;
	private MetricRegistry _metricRegistry;

	public void setWorkflowCache(WorkflowCache executableSnapshotCache) {
		this.executableSnapshotCache = executableSnapshotCache;
	}

	@PostConstruct
	public void initialize() {

		_logger.info("Initializing WorkflowService");














		// check for dependency statuses
		DroitException.assertThat(repository != null,"Instance of WorkflowRepositoryService was not set.");

		// check for dependency statuses
		DroitException.assertNotNull(executableSnapshotCache, "Workflow Cache");

		//TODO: We need to implement the new license file do we can reference it here and load twice.
		// Right now commenting this out will result in a failure of the escalationEmailService initialization.

		refresh();
	}
	
	public boolean isInitialized() {

		return (!(liveSnapshotId.length() == 0 ));
	}

	/**
	 * Set up the three sets of whitelist licensed modules. If a client name is supplied all modules inside it are
	 * added to the droit licensed modules. When the license file is moved to droit-code this call can be eliminated and the
	 * license info read directly here.
	 * @param clientName
	 * @param webToolWhiteListedModules
	 * @param batchReportWhiteListedModules
	 * @param etradingWhiteListedModules
	 */
	public void initializeLicensedModules(String clientName, Collection<String> webToolWhiteListedModules,
										  Collection<String> batchReportWhiteListedModules, Collection<String> etradingWhiteListedModules) {
		// Save the licensed module list so we can combine it with client modules later. These can be removed
		// and passed in to the process call since the merge is done at execution time now not at load time as was
		// originally intended. Load time failed to consider per session snapshots with changing client module lists.
		this.webToolWhiteListedModules.addAll(webToolWhiteListedModules);
		this.batchReportWhiteListedModules.addAll(batchReportWhiteListedModules);
		this.etradingWhiteListedModules.addAll(etradingWhiteListedModules);
		if(clientName != null && !clientName.isEmpty())
			this.clientName = clientName;
			repository.refresh(clientName); {
			liveSnapshotId = repository.getLiveSnapshot();

			if(repository.isClientId(liveSnapshotId)) {
				executableSnapshotCache.refresh(); // just empties the cache
				executableSnapshotCache.getSnapshot(liveSnapshotId);
			}
		}

	}

	/**
	 * Run a payload against the snapshot currently in production. Compiles the
	 * snapshot if not already cached.
	 * 
	 * @param payload
	 * @return
	 */
	public BucketedMap process(Date asOfDate, String moduleName,BucketedMap payload) {
		return process("NA", moduleName, payload);
	}

	//NOT IMPLEMENTED HERE , THIS IS GOINT TO BE REMOVED
	/**
	 * Run a payload against the snapshot currently in production or the users per session snapshot. Compiles the
	 * snapshot if not already cached. This version is used by the GUI to allow 
	 * overriding of the live snapshot by a development one. 
	 * 
	 * @param payload
	 * @return
	 */
	public BucketedMap process(RequestContext context,
							   BucketedMap payload) {
	   /*
		String snapshotId = devSnapshots.containsKey(context.getSessionId()) ? devSnapshots.get(context.getSessionId()) : liveSnapshotId;

		Collection<String> moduleList = null;
		//This could be made a bit more efficient by removing the embedded calls to process a single workflow.
		// Also the list of licensed workflows can be passed in since we now add the client ones if deeded from the executable snapshot.
		switch(context.getRequestType() ) {
			case BATCHREPORT:
				moduleList = new TreeSet<>(batchReportWhiteListedModules);
				break;
			case ETRADING:
				moduleList = new TreeSet<>(etradingWhiteListedModules);
				break;
			case WEBTOOL:
			default:
				moduleList = new TreeSet<>(webToolWhiteListedModules);
				break;
		}
		ExecutableSnapshot eSnapshot = executableSnapshotCache.getSnapshot(snapshotId);
		moduleList.addAll(eSnapshot.getClientModuleNames());
		for(String moduleName : moduleList) {
			process(snapshotId, moduleName, payload);
		}
		*/
		return payload;
	}


	/**
	 * Run a payload against an arbitrary snapshot. Compiles the snapshot if not
	 * already cached.
	 * 
	 * @param payload
	 * @param snapshotId
	 * @return
	 */
	public BucketedMap process(String snapshotId, String moduleName, BucketedMap payload) {

		//Timer.Context timer = null;

		//if (decisionsConfiguration.isGranularDecisionMetricsEnabled())	 {
		//	timer = getMetricRegistry().timer(MetricRegistry.name("RegMandate", snapshotId, moduleName)).time();
		//}

		_logger.debug("START Running Workflow with snapshot '{}', module name '{}'", snapshotId, moduleName);
		_logger.trace("Payload for workflow run: {}",payload);

		payload.setValue("metadata", "workflowSnapshotId", snapshotId);

		WorkflowEvaluator decision = new WorkflowEvaluator(payload,
					                                      executableSnapshotCache.getSnapshot(snapshotId),
                                                          fxConverter,
				                                          strictAttributeChecking,
			                                              collectEscalations);

		BucketedMap allResults = decision.getAll(moduleName);

		_logger.debug("END Running Workflow with snapshot '{}', module name '{}'", snapshotId, moduleName);
		_logger.trace("Results for workflow run: {}", allResults);

		//if (timer != null) {
		//	timer.stop();
		//}

		return allResults;
	}


	private void refresh() {
		_logger.info("Refreshing WorkflowService");
		executableSnapshotCache.refresh();
		repository.refresh(clientName);
		liveSnapshotId = repository.getLiveSnapshot();
		// force a reload of the executable version of the live snapshot
		executableSnapshotCache.getSnapshot(liveSnapshotId);
	}

	public void setRepository(WorkflowRepositoryService repository) {
		this.repository = repository;
	}

	/**
	 * @return the strictAttributeChecking
	 */
	public boolean isStrictAttributeChecking() {
		return strictAttributeChecking;
	}

	/**
	 * @param strictAttributeChecking
	 *            the strictAttributeChecking to set
	 */
	public void setStrictAttributeChecking(boolean strictAttributeChecking) {
		this.strictAttributeChecking = strictAttributeChecking;
	}

	/**
	 * Called from controller method used by workflow editor to link a dev snapshot
	 * to the logged in users session
	 *
	 * @param sessionId  the session id
	 * @param snapshotId the snapshot id
	 */
	public void addDevSnapshotMapping(String sessionId, String snapshotId) {
		Snapshot snapshot = repository.readSnapshotFile(snapshotId);
		if(VersionMetadata.DEV_STATUS.equals(snapshot.getMetadata().getStatus())) {
			// check for the case where snapshot went from test -> development
			if(repository.getLiveSnapshot().equals(snapshotId)) {
				String formerLiveId = repository.getLiveSnapshot();
				refresh(); // this reload the live snapshot
				_logger.info("Switched live snapshot from {} to {}", formerLiveId, repository.getLiveSnapshot());
			}
			devSnapshots.put(sessionId, snapshotId);
			executableSnapshotCache.reloadSnapshot(snapshotId);
			_logger.info("Set snapshot to {} for session {}", snapshotId, sessionId);
		} else {
			String formerLiveId = repository.getLiveSnapshot();
			refresh();
			_logger.info("Switched live snapshot from {} to {}", formerLiveId, repository.getLiveSnapshot());
		}
	}
	
	/**
	 * Called from controller method used by workflow editor to restore the session to the live snapshot.
	 * @param session Session id to restore to current live snapshot.
	 */
	public void removeDevSnaposhotMapping(String session) {
		devSnapshots.remove(session);
	}

	public boolean isCollectEscalations() {
		return collectEscalations;
	}

	public void setCollectEscalations(boolean collectEscalations) {
		this.collectEscalations = collectEscalations;
	}
	
	public MetricRegistry getMetricRegistry() {


		if (_metricRegistry == null) {
			synchronized (this) {
				if (_metricRegistry == null) {
					_metricRegistry = new MetricRegistry();
				}
			}
		}

		return _metricRegistry;

	}

	public void setMetricRegistry(MetricRegistry metricRegistry) {

		this._metricRegistry = metricRegistry;
	}

	public void setDecisionsConfiguration(DecisionsConfiguration decisionsConfiguration) {
		this.decisionsConfiguration = decisionsConfiguration;
	}

    public FxConverter getFxConverter() {
        return fxConverter;
    }

    public void setFxConverter(FxConverter fxConverter) {
        this.fxConverter = fxConverter;
    }

	public void reloadDataDictionarty() {
		repository.refresh(clientName);
	}
}