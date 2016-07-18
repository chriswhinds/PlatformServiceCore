package com.droitfintech.workflow.repository;


import com.droitfintech.datadictionary.Dictionary;
import com.droitfintech.datadictionary.DictionaryUpdateListener;
import com.droitfintech.workflow.internal.repository.Module;
import com.droitfintech.workflow.internal.repository.Snapshot;

public interface WorkflowRepositoryService  {
	/**String[]
	 * Reload the live snapshot cache.
	 */
	public void refresh();

	/**
	 * Reload the live droit and client snapshot cache.
	 */
	public void refresh(String clientId);

	/**
	 * Return the live snapshot from the cache or read one from disk if the ID does not match the live snapshot.
	 * @param snapshotId
	 * @return Snapshot object
	 */
	public Snapshot getSnapshot(String snapshotId);
	
	/**
	 * read a fresh copy of the requested snapshot from disk.
	 * @param snapshotId
	 * @return Snapshot object
	 */
	
	public Snapshot readSnapshotFile(String snapshotId);
	
	/**
	 * get a module from the cache if possible otherwise from disk. This does NOT support
	 * reading modules from the underlying droit snapshot unless it is the live snapshot.
	 * @param snapshotId
	 * @param moduleId
	 * @return
	 */
	public Module getModule(String snapshotId, String moduleId);

	/**
	 * get a module from the cache if possible otherwise from disk. This is the preferred method to call as it will
	 * load droit modules that are referenced by client snapshots even if the snapshot is not the live one..
	 * @param snapshot
	 * @param moduleId
	 * @return
	 */
	public Module getModuleForSnapshot(Snapshot snapshot, String moduleId);
	
	/**
	 * Get the ID of the snapshot currently cached. This may actually have a status of "test" rather then "live"
	 * @return
	 */
	public String getLiveSnapshot();

	/**
	 * Return true if snapshot or module ID is a client managed one.
	 */

	public boolean isClientId(String snapshotOrModuleId);

	public Dictionary getDataDictionaryForLiveSnapshot();

	void registerDictionaryListeners(DictionaryUpdateListener listener);
}
