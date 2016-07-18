package com.droitfintech.utils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by christopherwhinds on 7/7/16.
 */
public class BucketedMap implements Serializable {

    private static final long serialVersionUID = 1L;

    // LinkedHashMap preserves evaluation order
    private Map<String, Map<String, Object>> underlyingMap = new LinkedHashMap<String, Map<String, Object>>();

    /**
     * Creates an empty bucketed map.
     */
    public BucketedMap() {
    }

    /**
     * Creates a clone of an existing bucketed map.
     */
    public BucketedMap(BucketedMap clone) {
        for (String bucket : clone.getBuckets()) {
            this.setAllValuesForBucket(bucket,
                    clone.getAllValuesForBucket(bucket));
        }
    }

    /**
     * Sets a value for a given key on a given bucket.
     */
    public void setValue(String bucket, String key, Object value) {

        if (!underlyingMap.containsKey(bucket)) {
            underlyingMap.put(bucket, new LinkedHashMap<String, Object>());
        }
        underlyingMap.get(bucket).put(key, value);
    }

    /**
     * Gets a value for a given key on a given bucket.  Returns null if the value is not found, or, if the bucket is not found.
     */
    public Object getValue(String bucket, String key) {

        Map<String, Object> catData = (Map<String, Object>) underlyingMap
                .get(bucket);
        if (catData != null) {
            Object param = null;
            param = catData.get(key);
            return param;
        }
        return null;
    }

    /**
     * Sets all values for a given bucket.
     */
    public void setAllValuesForBucket(String bucket, Map<String, Object> map) {
        underlyingMap.put(bucket, map);
    }

    /**
     * Gets all values for a given bucket.  Returns null if the bucket is not found.
     */
    public Map<String, Object> getAllValuesForBucket(String bucket) {
        return underlyingMap.get(bucket);
    }

    /**
     * gets all bucket names.
     */
    public Set<String> getBuckets() {
        return underlyingMap.keySet();
    }

    /**
     * Returns the underlying map data structure.
     * @return the underlying map
     */
    public Map<String, Map<String, Object>> getUnderlyingMap() {
        return this.underlyingMap;
    }

    /**
     * Replaces the underlying map data structure.
     */
    public void setUnderlyingMap(Map<String, Map<String, Object>> data) {
        this.underlyingMap = data;
    }

    @Override
    public String toString() {
        return "BucketedMap [" + underlyingMap + "]";
    }

    public boolean containsBucket(String bucketName) {
        return underlyingMap.containsKey(bucketName);
    }

    public boolean containsValue(String bucketName, String key) {

        boolean answer = false;

        if (containsBucket(bucketName)) {
            Map<String, Object> bucket = this.getAllValuesForBucket(bucketName);
            answer = bucket.containsKey(key);
        }
        return answer;
    }
}
