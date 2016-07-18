package com.droitfintech.datadictionary.attribute;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by barry on 5/6/16.
 */
public class TaxonomyPoint {
    public static String REGION = "Region";
    public static String NATION = "Nation";
    public static String PROVINCE = "Province";
    public static String REGULATOR = "Regulator";
    public static String REGULATION = "Regulation";
    public static String ASSET_TYPE = "AssetType";
    public static String MANDATE = "Mandate";
    public static String SUB_MANDATE = "SubMandate";

    private String name;             // full name of point
    private Map<String, Set<String>> pointMap = new HashMap<String,Set<String>>();

    public TaxonomyPoint() {
        name = "";
    }

    public TaxonomyPoint(String name) {
        this.name = name;
        if(StringUtils.isNotBlank(name)) {
            String[] points = name.split(":");
                addPointValues(REGION,points[0]);
            if(points.length > 1)
                addPointValues(NATION,points[1]);
            if(points.length > 2)
                addPointValues(PROVINCE,points[2]);
            if(points.length > 3)
                addPointValues(REGULATOR,points[3]);
            if(points.length > 4)
                addPointValues(REGULATION,points[4]);
            if(points.length > 5)
                addPointValues(ASSET_TYPE,points[5]);
            if(points.length > 6)
                addPointValues(MANDATE,points[6]);
            if(points.length > 7)
                addPointValues(SUB_MANDATE,points[7]);
        }
    }

    public TaxonomyPoint(String name, String[] headers, String[] fields) {
        this.name = name;
        for(int hIdx = 0; hIdx < headers.length; hIdx++) {
            addPointValues(headers[hIdx], hIdx < fields.length ? fields[hIdx] : null);
        }
    }

    public TaxonomyPoint(Collection<TaxonomyPoint> mergeList) {
        for(TaxonomyPoint toMerge : mergeList) {
            for(String key : toMerge.pointMap.keySet()) {
                if(pointMap.containsKey(key)) {
                    pointMap.get(key).addAll(toMerge.pointMap.get(key));
                } else {
                    pointMap.put(key, toMerge.pointMap.get(key));
                }
            }
        }
    }

    private void addPointValues(String pointName, String valuesAsString) {
        if(StringUtils.isNotBlank(pointName)) {
            String[] values = StringUtils.isNotBlank(valuesAsString) ? valuesAsString.split(",") : null;
            if(values != null) {
                TreeSet<String> valuesCollection = new TreeSet<String>();
                for(int vIdx = 0; vIdx < values.length; vIdx++) {
                    valuesCollection.add(StringUtils.trim(values[vIdx]));
                }
                pointMap.put(pointName, valuesCollection);
            } else {
                pointMap.put(pointName, new TreeSet<String>());
            }
        }
    }

    public String getName() {
        return name;
    }

    public Collection<String> getPointValues(String pointName) {
        return pointMap.containsKey(pointName) ? pointMap.get(pointName) : new ArrayList<String>();
    }

    public Map<String, Set<String>> getPointMap() {
        return pointMap;
    }

    public void setPointMap(Map<String, Set<String>> pointMap) {
        this.pointMap = pointMap;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TaxonomyPoint) {
            TaxonomyPoint other = (TaxonomyPoint)obj;
            if(pointMap.keySet().size() != other.pointMap.keySet().size()) {
                return false;
            }
            for(String key : pointMap.keySet()) {
                if(!other.pointMap.containsKey(key) ||  !pointMap.get(key).containsAll(other.pointMap.get(key))) {
                    return false;
                }
            }
            return true; // same number of keys and points and values for the points.
        }
        return false;
    }
}
