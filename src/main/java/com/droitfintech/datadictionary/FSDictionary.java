package com.droitfintech.datadictionary;

import com.droitfintech.datadictionary.attribute.*;
import com.droitfintech.datadictionary.rule.DictionaryConstants;
import com.droitfintech.exceptions.DroitException;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.*;
import java.util.*;

/**
 * Created by christopherwhinds on 7/7/16.
 */
public class FSDictionary implements Dictionary {
    public static final String ATTR_FILE_NAME = "/attributes.csv";
    public static final String ENUM_FILE_NAME = "/enums.csv";
    public static final String TAXONOMY_FILE_NAME = "/taxonomy.csv";
    private static final String[] partyInstances = {"contraparty", "counterparty"};
    private static final String[] productInstances = {"trade"};
    private String droitDirectoryFullPath = null;
    private String clientDirectoryFullPath = null;
    private HashMap<String, Namespace> namespacesByName = new HashMap<String, Namespace>();
    private HashMap<String, Namespace> namespacesByInstanceName = new HashMap<String, Namespace>();
    private HashMap<String, Set<Attribute>> attributeTags = new HashMap<String, Set<Attribute>>();
    private HashMap<String, Set<String>> droitEnumValues = new HashMap<String, Set<String>>();
    private HashMap<String, Set<String>> clientEnumValues = new HashMap<String, Set<String>>();
    private TreeMap<String, TaxonomyPoint> taxonomyPoints = new TreeMap<String, TaxonomyPoint>();
    private TreeSet<String> tagValues = new TreeSet<String>();
    private static final Logger log = LoggerFactory.getLogger(FSDictionary.class);

    public FSDictionary() {
        String[] internalInstances = {"json_mapping"};
        defineNamespace(DictionaryConstants.SCOPE_DROIT, DictionaryConstants.NAMESPACE_PRODUCT, productInstances);
        defineNamespace(DictionaryConstants.SCOPE_DROIT, DictionaryConstants.NAMESPACE_PARTY, partyInstances);
        defineNamespace(DictionaryConstants.SCOPE_DROIT, DictionaryConstants.NAMESPACE_JSON_MAPPING, internalInstances);
    }


    public Attribute findAttribute(String scope, String namespace, String attribute) {
        Namespace ns = namespacesByName.get(scope + "." + namespace);
        if (ns == null) {
            return null;
        }
        return ns.getAttribute(attribute);
    }

    public Attribute findAttributeByInstanceName(String scope, String namespace, String attribute) {
        Namespace ns = namespacesByInstanceName.get(scope + "." + namespace);
        if (ns == null) {
            return null;
        }
        return ns.getAttribute(attribute);
    }


    public Collection<Attribute> findAttributesTagged(String tag) {
        return attributeTags.get(tag);
    }

    public Collection<String> getInstanceNames(String scopedNamespace) {
        Namespace ns = namespacesByName.get(scopedNamespace);
        if(ns != null)
            return ns.getInstanceNames();
        return new ArrayList<String>();
    }

    private Namespace ensureNamespace(String scope, String name) {
        String key = scope + "." + name;
        if (namespacesByName.get(key) == null) {
            Namespace ns = new Namespace(scope, name);
            namespacesByName.put(key, ns);
        }
        return namespacesByName.get(key);
    }

    public void tagAttribute(Attribute a, Tag t) {
        if(t == null || a== null)
            return;
        if (!a.getTags().contains(t)) {
            a.addTag(t);
        }
        String tag = t.getName();
        if (attributeTags.get(tag) == null) {
            attributeTags.put(tag, new HashSet<Attribute>());
        }
        attributeTags.get(tag).add(a);
    }


    public Attribute defineAttribute(String scope, String namespace, String name, Type type, Cardinality cardinality) {
        Namespace ns = ensureNamespace(scope, namespace);
        Attribute a = new Attribute(scope, name, type, cardinality);
        ns.addAttribute(a);
        for (Tag t : a.getTags()) {
            String tname = t.getName();
            Set<Attribute> attrs = attributeTags.get(tname);
            if (attrs == null) {
                attributeTags.put(tname, new HashSet<Attribute>());
            }
            attrs.add(a);
        }
        return a;
    }

    public void deleteAttribute(Attribute toDelete) {
        toDelete.getNamespace().removeAttribute(toDelete);
        Set<Tag> tags = toDelete.getTags();
        if(tags != null) {
            for(Tag tag :tags) {
                Set<Attribute> tagsAttributes = attributeTags.get(tag.getName());
                if(tagsAttributes != null) {
                    tagsAttributes.remove(toDelete);
                }
            }
        }
    }


    public void defineNamespace(String scope, String name, String[] instanceNames) {
        if (namespacesByName.containsKey(scope + "." + name)) {
            log.warn("Redefining namespace " + scope + "." + name);
        }
        Namespace ns = new Namespace(scope, name);
        ns.setInstances(instanceNames);
        namespacesByName.put(scope + "." + name, ns);
        for(int idx = 0; idx < instanceNames.length; idx++) {
            namespacesByInstanceName.put(scope + "." + instanceNames[idx], ns);
        }
    }


    public Collection<Namespace> listNamespaces() {
        return namespacesByName.values();
    }


    public Collection<String> listTags() {
        return attributeTags.keySet();
    }

    // for the WFE
    public Collection<Attribute> listAttributes(String scopeAndNamespace) {
        if (!namespacesByName.containsKey(scopeAndNamespace)) {
            return null;
        }
        Namespace ns = namespacesByName.get(scopeAndNamespace);
        return ns.getAttributes().values();
    }


    public Collection<Attribute> listAttributes(String scope, String namespace) {
        String key = scope + "." + namespace;
        if (!namespacesByName.containsKey(key)) {
            return null;
        }
        Namespace ns = namespacesByName.get(key);
        return ns.getAttributes().values();
    }


    public Collection<TaxonomyPoint> getTaxonomyPoints() {
        return taxonomyPoints.values();
    }

    private String concatValues(Collection<String> values) {
        if(values != null && !values.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String val : values) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(val);
            }
            return sb.toString();
        } else {
            return "";
        }
    }
    /**
     * Public only for test's. Would be better to derive a test class and make this protected here.
     * @param attrFile
     * @throws IOException
     */

    public void saveToFile(String scope, File attrFile) throws IOException {
        CSVWriter csv = new CSVWriter(new FileWriterWithEncoding(attrFile,"UTF-8"));
        String[] headings = {"namespace", "name", "type", "cardinality", "tags", "description", "validation", "validationError",
                "displayName", "source", "notes", "calculationFunction", "calculationFunctionParameters", "defaultFromField", "defaulting", "defaultValue",
                "Region", "Nation", "Province", "Regulator", "Regulation", "AssetType", "Mandate", "SubMandate"};
        csv.writeNext(headings);

        for (Namespace ns : namespacesByName.values()) {
            // Scope is either droit or client so we only write the attributes for
            // the requested scope. That's how we support separate attribute files for the droit snapshot and
            // the client snapshot.
            if(!scope.equals(ns.getScope()))
                continue;
            Collection<Attribute> attributes = ns.getAttributes().values();
            for (Attribute a : attributes) {
                Collection<String> tags = new HashSet<String>();
                for (Tag t : a.getTags()) {
                    tags.add(t.getId());
                }
                String[] row = new String[24];
                row[0] = a.getNamespace().getName();
                row[1] = a.getName();
                row[2] = String.valueOf(a.getType());
                row[3] = String.valueOf(a.getCardinality());
                row[4] = StringUtils.join(tags, ",");
                row[5] = a.getDescription();
                row[6] = a.getValidation();
                row[7] = a.getValidationError();
                row[8] = a.getDisplayName();
                row[9] = a.getSource();
                row[10] = a.getNotes();
                row[11] = a.getCalculationFunction();
                row[12] = a.getCalculationParameters().replaceAll("\"", "\\\"");
                row[13] = a.getLinkedDefaultField();
                row[14] = String.valueOf(a.getDefaultServiceLookup());
                row[15] = a.getDefaultValueAsString();
                TaxonomyPoint tp = a.getReferencingTaxonomyPoints();
                row[16] = concatValues(tp.getPointValues(TaxonomyPoint.REGION));
                row[17] = concatValues(tp.getPointValues(TaxonomyPoint.NATION));
                row[18] = concatValues(tp.getPointValues(TaxonomyPoint.PROVINCE));
                row[19] = concatValues(tp.getPointValues(TaxonomyPoint.REGULATOR));
                row[20] = concatValues(tp.getPointValues(TaxonomyPoint.REGULATION));
                row[21] = concatValues(tp.getPointValues(TaxonomyPoint.ASSET_TYPE));
                row[22] = concatValues(tp.getPointValues(TaxonomyPoint.MANDATE));
                row[23] = concatValues(tp.getPointValues(TaxonomyPoint.SUB_MANDATE));
                csv.writeNext(row);
            }
        }
        csv.flush();
        csv.close();
    }

    /**
     * Load up the data dictionary including handeling both the client entries and the underlying droit entries.
     * @param droitDirectoryFullPath
     * @param clientDirectoryFullPath
     */
    public void load(String droitDirectoryFullPath, String clientDirectoryFullPath) {
        this.droitDirectoryFullPath = droitDirectoryFullPath;
        this.clientDirectoryFullPath = clientDirectoryFullPath;
        File attributeFile = new File(droitDirectoryFullPath + ATTR_FILE_NAME);
        File enumFile = new File(droitDirectoryFullPath + ENUM_FILE_NAME);
        File taxonomyFile = new File(droitDirectoryFullPath + TAXONOMY_FILE_NAME);
        if(attributeFile.exists()) {
            readFromFile(DictionaryConstants.SCOPE_DROIT, attributeFile);
        }
        if(enumFile.exists()) {
            droitEnumValues = readEnumValues(enumFile);
        }
        if(taxonomyFile.exists()) {
            readTaxonomyPoints(taxonomyFile);
        }
        if(StringUtils.isNotBlank(clientDirectoryFullPath)) {
            defineNamespace(DictionaryConstants.SCOPE_CLIENT, DictionaryConstants.NAMESPACE_PRODUCT, productInstances);
            defineNamespace(DictionaryConstants.SCOPE_CLIENT,DictionaryConstants.NAMESPACE_PARTY, partyInstances);
            attributeFile = new File(clientDirectoryFullPath + ATTR_FILE_NAME);
            enumFile = new File(clientDirectoryFullPath + ENUM_FILE_NAME);
            if(attributeFile.exists()) {
                readFromFile(DictionaryConstants.SCOPE_CLIENT, attributeFile);
            }
            if(enumFile.exists()) {
                clientEnumValues = readEnumValues(enumFile);
            }
        }
    }

    public void readFromFile(String scope, File attributeFile) {
        try {
            CSVReader csv = new CSVReader(new InputStreamReader(new FileInputStream(attributeFile), "UTF-8"));
            String[] row, headers;
            Integer line = 0;

            if((headers = csv.readNext()) != null) { // skip headings
                while ((row = csv.readNext()) != null) {
                    try {
                        String namespace = row[0];
                        String name = row[1];
                        String ty = row[2];
                        String card = row[3];
                        String tag = row[4];
                        String description = row[5];
                        line++;

                        Type type = Type.valueOf(ty);
                        Cardinality cardinality = Cardinality.valueOf(card);
                        defineAttribute(scope, namespace, name, type, cardinality);

                        Attribute a = findAttribute(scope, namespace, name);
                        String[] tags = StringUtils.split(tag, ",");
                        for (String t : tags) {

                            Tag actualTag = Tag.getTagById(t);

                            if (actualTag == null) {
                                log.warn("Dictionary attribute '{}' was tagged with '{}' which is not a valid tag." +
                                        "  This attribute will not be tagged.  Continuing.", name, t);
                            } else {
                                tagAttribute(a, actualTag);
                            }
                        }

                        if (row.length > 6) {
                            a.setDescription(description);
                            a.setValidation(row[6]);
                            a.setValidationError(row[7]);
                            a.setDisplayName(row[8]);
                            a.setSource(row[9]);
                            a.setNotes(row[10]);
                        }
                        if (row.length > 11) {
                            a.setCalculationFunction(row[11]);
                            a.setCalculationParameters(row[12]);
                            a.setLinkedDefaultField(row[13]);
                            try {
                                a.setDefaultServiceLookup(DefaultServiceLookup.valueOf(row[14]));
                            } catch (Exception ignore) {
                            }
                        }
                        if (row.length > 15) {
                            a.setDefaultValueAsString(row[15]);
                        }
                        if (row.length > 23) {
                            TaxonomyPoint tp = new TaxonomyPoint("", Arrays.copyOfRange(headers,16,24),Arrays.copyOfRange(row,16,24));
                            a.setReferencingTaxonomyPoints(tp);
                        }
                    } catch (Exception ex) {
                        log.error("Line {} in data dict is invalid skipping, file {}", line, attributeFile.getPath() );
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new DroitException(e);
        } catch (IOException e) {
            throw new DroitException(e);
        }
    }


    public Namespace getNamespace(String scope, String namespace) {
        String key = scope + "." + namespace;
        return namespacesByName.get(key);
    }

    public Namespace getNamespaceByInstanceName(String scope, String name) {
        return namespacesByInstanceName.get(scope + "." + name);
    }

    public void setEnumeratedStringValues(String scope, String typeName, Collection<String> values) {
        if(DictionaryConstants.SCOPE_CLIENT.equals(scope)) {
            clientEnumValues.put(typeName, new TreeSet<String>(values));
            try {
                if(DictionaryConstants.SCOPE_CLIENT.equals(scope))
                    saveEnumValues(true);
            } catch (Exception ex) {
                log.error("Unable to save enumerated values:", ex);
                throw new DroitException(ex);
            }
        } else {
            droitEnumValues.put(typeName, new TreeSet<String>(values));
            try {
                if(DictionaryConstants.SCOPE_DROIT.equals(scope))
                    saveEnumValues(false);
            } catch (Exception ex) {
                log.error("Unable to save enumerated values:", ex);
                throw new DroitException(ex);
            }
        }

    }

    public Set<String> getEnumeratedStringValues(String scope, String typeName) {
        HashMap<String, Set<String>> enumValues;
        if(DictionaryConstants.SCOPE_CLIENT.equals(scope)) {
            enumValues = clientEnumValues;
        } else {
            enumValues = droitEnumValues;
        }
        if(enumValues.containsKey(typeName)) {
            return enumValues.get(typeName);
        }
        else {
            return new HashSet<String>();
        }
    }

    public Set<String> getTypeNames(String scope) {
        // return tree set to sort names
        if(DictionaryConstants.SCOPE_CLIENT.equals(scope)) {
            return new TreeSet<String>(clientEnumValues.keySet());
        } else {
            return new TreeSet<String>(droitEnumValues.keySet());
        }
    }

    public Set<String> getTagValues() {
        return Tag.getTagNames();
    }

    public void saveAttributeFile(boolean saveToClientSnapshot) throws IOException {
        File attrFile;
        if(saveToClientSnapshot)
            attrFile = new File(clientDirectoryFullPath + ATTR_FILE_NAME);
        else
            attrFile = new File(droitDirectoryFullPath + ATTR_FILE_NAME);
        if(attrFile.canWrite() || attrFile.createNewFile()) {
            if(saveToClientSnapshot) {
                saveToFile(DictionaryConstants.SCOPE_CLIENT, attrFile);
            }
            else {
                saveToFile(DictionaryConstants.SCOPE_DROIT, attrFile);
            }
        }
    }

    private void saveEnumValues(boolean saveToClientSnapshot) throws IOException {
        File enumFile;
        HashMap<String, Set<String>> enumValues;
        if(saveToClientSnapshot) {
            enumFile = new File(clientDirectoryFullPath + ENUM_FILE_NAME);
            enumValues = clientEnumValues;
        } else {
            enumFile = new File(droitDirectoryFullPath + ENUM_FILE_NAME);
            enumValues = droitEnumValues;
        }
        if(enumFile.canWrite() || enumFile.createNewFile()) {
            CSVWriter csv = new CSVWriter(new FileWriter(enumFile));
            String[] headings = {"typeName", "type", "value"};
            csv.writeNext(headings);
            for (String type : enumValues.keySet() ) {
                Collection<String> values = enumValues.get(type);
                for (String value : values) {
                    String[] row = new String[3];
                    row[0] = type;
                    row[1] = "enumeratedString";
                    row[2] = value;
                    csv.writeNext(row);
                }
            }
            csv.flush();
            csv.close();
        }
    }

    private HashMap<String, Set<String>> readEnumValues(File file) {
        HashMap<String, Set<String>> enumValues = new HashMap<String, Set<String>>();
        try {
            CSVReader csv = new CSVReader(new FileReader(file));
            String[] row;
            Integer line = 0;
            enumValues.clear();
            if(csv.readNext() != null) { // skip headings
                while ((row = csv.readNext()) != null) {
                    line++;
                    if(row.length > 2) {
                        Set<String> values = enumValues.get(row[0]);
                        if (values == null) {
                            values = new TreeSet<String>();
                            enumValues.put(row[0], values);
                            values.add(row[2]);
                        } else {
                            values.add(row[2]);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new DroitException(e);
        } catch (IOException e) {
            throw new DroitException(e);
        }
        return enumValues;
    }

    private void readTaxonomyPoints(File file) {
        ArrayList<TaxonomyPoint> points = new ArrayList<TaxonomyPoint>();
        try {
            CSVReader csv = new CSVReader(new FileReader(file));
            String[] row;
            String[] headerRow;
            Integer line = 0;
            if((headerRow = csv.readNext()) != null) { // skip headings
                int headerLength = headerRow.length;
                while ((row = csv.readNext()) != null) {
                    line++;
                    if(headerLength > 7) {
                        String tag = "";
                        int idx;
                        for(idx = 0; idx < 8 && idx < row.length; idx++) {
                            if(tag.length() > 0) {
                                tag += ":";
                            }
                            tag += row[idx];
                        }
                        taxonomyPoints.put(tag, new TaxonomyPoint(tag, headerRow, row));
                    }
                    else if(row.length > 1) {
                        taxonomyPoints.put(row[0], new TaxonomyPoint(row[0]));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new DroitException(e);
        } catch (IOException e) {
            throw new DroitException(e);
        }
    }
}
