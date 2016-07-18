package com.droitfintech.workflow.internal;
/**
 * Maintains a cash of snapshots in their executable (compiled) form. 
 * Since this class performs caching it is assumed to be a singleton on the application.
 * Snapshots are indexed by their ID which is the snapshot file name without the ".json" extension.
 */


import com.droitfintech.exceptions.DroitException;

import com.droitfintech.workflow.repository.WorkflowRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.PostConstruct;
import java.util.HashMap;

public class WorkflowCache {

	private final String SYSTEM_PROPERTY_DISABLE_CACHING = "com.droitfintech.workflow.service.disableCache";

	private static Logger _logger = LoggerFactory.getLogger(WorkflowCache.class);

	private HashMap<String, ExecutableSnapshot> snapshots = new HashMap<String, ExecutableSnapshot>();


	private WorkflowRepositoryService repository;

	private boolean isInitialized = true;

	private boolean areWorkflowsDisabled = false;


	public void initialize() {
		
		// This doesn't work yet but I *really* want to fix it at some point
		//areWorkflowsDisabled = System.getProperty(SYSTEM_PROPERTY_DISABLE_CACHING) != null;

		_logger.info("Initializing WorkflowCache");
		DroitException.assertNotNull(repository, "Worklow Repository Service not set on Workflow Cache instance");
		//InitializationHelper.assertIsInitialized(repository);

		this.isInitialized = true;
	}

	/**
	 * Remove all currently loaded and compiled snapshots AND call refresh on
	 * the underlying repository that loads the raw JSON data. This has the
	 * effect of causing the underlying JSON reader to reload the default
	 * snapshot ID.
	 */
	public synchronized void refresh() {
		_logger.info("Refreshing WorkflowCache");
		snapshots = new HashMap<String, ExecutableSnapshot>();
		StatelessScript.clearGCLCache();
		System.gc(); // cheep attempt to fee up memory from old groovy classes.
	}

	/*
	 * Get a snapshot by ID. This will read and compile the snapshot from JSON
	 * if it does not currently exist in the cache.
	 */
	public synchronized ExecutableSnapshot getSnapshot(String snapshotId) {
		if (!snapshots.containsKey(snapshotId)) {
//			_logger.info("Loading all workflows...");
			ExecutableSnapshot snapshot = new ExecutableSnapshot(snapshotId, repository);
			snapshots.put(snapshotId, snapshot);
			_logger.info("Finished loading all workflows");
		}
		return snapshots.get(snapshotId);
	}

	/**
	 * Reload and recompile a single snapshot from JSON form. This is currently
	 * used when per user session development snapshots are reloaded from the
	 * work flow editor.
	 * 
	 * @param snapshotId
	 *            - Id in string form
	 *            "<snapshot.<branch>.<major_num>.<minor_num>"
	 * @return
	 */
	public ExecutableSnapshot reloadSnapshot(String snapshotId) {
		snapshots.remove(snapshotId);
		return getSnapshot(snapshotId);
	}



	public boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * Manually set the JSON repository reader which is SPRING AutoWired by
	 * default.
	 * 
	 * @param repository
	 *            - Instance of @WorkflowRepositoryService or derived class.
	 */
	public void setRepository(WorkflowRepositoryService repository) {
		this.repository = repository;
	}

}