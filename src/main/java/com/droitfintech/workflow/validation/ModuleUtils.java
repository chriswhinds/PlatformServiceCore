package com.droitfintech.workflow.validation;

import com.droitfintech.workflow.internal.ParameterizedModule;
import com.droitfintech.workflow.internal.groovy.GroovyParser;
import com.droitfintech.workflow.internal.groovy.ReferenceCollector;
import com.droitfintech.workflow.internal.repository.Module;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for parsing code in workflow modules
 * Created by barry on 8/26/15.
 */
public class ModuleUtils {

    public static final Pattern flowchartExpressionDdotVariableMatcher  = Pattern.compile("d\\.([a-zA-Z_][a-zA-Z0-9_]*)\\.([a-zA-Z_][a-zA-Z0-9_]*)"); //d.<module>.<variable> capture module and variable
    public static final Pattern flowchartExpressionItDotVariableMatcher  = Pattern.compile("it\\.([a-zA-Z_][a-zA-Z0-9_]*)"); //it.<variable> capture variable
    public static final Pattern flowchartExpressionItDotDotVariableMatcher  = Pattern.compile("(it\\.[a-zA-Z_][a-zA-Z0-9_]*)\\.([a-zA-Z_][a-zA-Z0-9_]*)");
    public static final Pattern tableExpressionVariableMatcher = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\.([a-zA-Z_][a-zA-Z0-9_]*)$"); // <start of string><module>.<variable><end of string> capture module
    public static final Pattern paramModuleParamAsMapPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*d\\.([a-zA-Z_][a-zA-Z0-9_]*)[,\\s\\]]");

    /**
     * Add results through this method to filter out function names the regulat expressions pick up.
     * @param result
     * @param de
     */
    private static void addElementToResult(TreeSet<DecisionElement> result, DecisionElement de) {
        if(!de.getVariableName().equals("containsKey")) {
            result.add(de);
        }
    }

    public static TreeSet<DecisionElement> getReferencedVariables(String expression, Module module) {
        TreeSet<DecisionElement> elementsFound = new TreeSet<DecisionElement>();
        if(StringUtils.isBlank(expression))
            return elementsFound;
        ReferenceCollector refCollector = new ReferenceCollector();
        try {
            for (ReferenceCollector.Reference ref : refCollector.getReferences(GroovyParser.parse(new ByteArrayInputStream(expression.getBytes())))) {
                DecisionElement de = new DecisionElement(ref.getLhs(), ref.getRhs());
                addElementToResult(elementsFound, de);
            }
        } catch (Exception ignore) { }

        // Note the 2 modules to test parameters with are jfsa_party_scope and sfc_scope.
        // These are going away so I did not bother to convert it to the AST based parsing (class ReferenceCollector)
        if(module instanceof ParameterizedModule) {
            Matcher matcher = flowchartExpressionDdotVariableMatcher.matcher(expression);
            // Parameters can be defined as either a single element or as a groovy map of elements
            // first check for the map form [ x : d.y, a : d.b ]
            ParameterizedModule pModule = (ParameterizedModule)module;
            boolean parametersAreMap = false;
            HashMap<String, String> paramMap = new HashMap<String, String>();
            for(String paramExpression : pModule.getParams().values()) {
                Matcher paramMatcher = paramModuleParamAsMapPattern.matcher(paramExpression);
                while(paramMatcher.find()) {
                    parametersAreMap = true;
                    String itName = paramExpression.substring(paramMatcher.start(1), paramMatcher.end(1));
                    String itValue = paramExpression.substring(paramMatcher.start(2), paramMatcher.end(2));
                    paramMap.put("it." + itName, itValue);
                }
            }
            if(parametersAreMap) {
                matcher = flowchartExpressionItDotDotVariableMatcher.matcher(expression);
                while (matcher.find()) { // there can be many d.<module>.<property> references in the expression.
                    if (matcher.start(1) >= 0) {
                        String itParamName = expression.substring(matcher.start(1), matcher.end(1));
                        String variableName = expression.substring(matcher.start(2), matcher.end(2));
                        if(paramMap.containsKey(itParamName)) {
                            DecisionElement de = new DecisionElement(paramMap.get(itParamName), variableName);
                            addElementToResult(elementsFound, de);
                        }
                    }
                }
            } else {
                for(String itName : pModule.getParams().keySet()) {
                    String[] parts = pModule.getParams().get(itName).split("\\.");
                    if(parts.length == 2) {
                        paramMap.put(itName, parts[1]);
                    }
                }
                matcher = flowchartExpressionItDotVariableMatcher.matcher(expression);
                while (matcher.find()) { // there can be many it.<property> references in the expression.

                        String variableName = expression.substring(matcher.start(1), matcher.end(1));
                        for(String decisionParamName : paramMap.values()) {
                            DecisionElement de = new DecisionElement(decisionParamName, variableName);
                            addElementToResult(elementsFound, de);
                        }

                }
            }
        }
        return elementsFound;
    }

    public static TreeSet<DecisionElement> getReferencedTableModuleVariables(Collection<String> rowValues,
                                                                             TreeSet<DecisionElement> previousMatches) {
        TreeSet<DecisionElement> elementsFound = (previousMatches != null ? previousMatches : new TreeSet<DecisionElement>());
        for(String variable : rowValues) {
            Matcher matcher = tableExpressionVariableMatcher.matcher(variable);
            if (matcher.find()) {
                String instanceName = variable.substring(matcher.start(1), matcher.end(1));
                String variableName = variable.substring(matcher.start(2), matcher.end(2));
                DecisionElement de = new DecisionElement(instanceName, variableName);
                elementsFound.add(de);
            }
        }
        return elementsFound;
    }

    /**
     *
     * Given a module or snapshot id in the form <name>.<branch>.<major#>.<minor#> return <branch>
     * @param id
     * @return
     */
    public static String getBranchFromId(String id) {
        String[]parts = id.split("\\.");
        return parts.length == 4 ? parts[1] : "";
    }

    public static String getTaxonomy(Module module) {
        // For some reason this code was very inefficient so I replaced it with module.getTaxonomy()
//        String ret = "";
//        List<String> tags = module.getTags();
//        if(tags != null && tags.size() > 0) {
//            ret = tags.get(0);
//            if(ret == null || "parent".equals(ret)) {
//                ret = "";
//            }
//        }
        return module.getTaxonomy();
    }

    public static boolean isInModulesTaxonomy(Module module, Module otherModule) {
        String modTaxonomy = getTaxonomy(module);
        if(StringUtils.isNotBlank(modTaxonomy)) {
            return isInModulesTaxonomy(modTaxonomy, getTaxonomy(otherModule));
        }
        return true;
    }

    private static boolean hasIntersection(String s1, String s2) {
        if(StringUtils.isNotBlank(s1) && StringUtils.isNotBlank(s2)) {
            String s1c = StringUtils.strip(s1.toLowerCase());
            String s2c = StringUtils.strip(s2.toLowerCase());
            if (s1c.contains(",") || s2c.contains(",")) {
                HashSet s1Set = new HashSet();
                HashSet s2Set = new HashSet();
                String[] s1Parts = s1c.split(",");
                for (int idx = 0; idx < s1Parts.length; idx++) {
                    s1Set.add(StringUtils.strip(s1Parts[idx]));
                }
                String[] s2Parts = s2c.split(",");
                for (int idx = 0; idx < s2Parts.length; idx++) {
                    s2Set.add(StringUtils.strip(s2Parts[idx]));
                }
                return !Collections.disjoint(s1Set, s2Set);
            }
            return s1c.equals(s2c);
        }
        return false;
    }


    public static Boolean isInModulesTaxonomy(String modTaxonomy, String otherModulesTaxonomy) {
        if(StringUtils.isNotBlank(modTaxonomy)) {
            if (modTaxonomy.equals(otherModulesTaxonomy)) {
                return true;
            } else {
                if(StringUtils.isNotBlank(otherModulesTaxonomy) ) {
                    String[] modParts = modTaxonomy.split(":");
                    String[] otherModParts = otherModulesTaxonomy.split(":");
                    for(int idx = 0; idx < otherModParts.length; idx++) {
                        if(!"ALL".equals(otherModParts[idx]) && !hasIntersection(modParts[idx],otherModParts[idx])) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
