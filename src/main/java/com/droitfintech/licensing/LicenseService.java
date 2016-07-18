package com.droitfintech.licensing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.droitfintech.datadictionary.attribute.Tag;
import com.droitfintech.model.FinMktInfraVersion;
import com.droitfintech.services.RequestContext;
import org.apache.commons.lang3.StringUtils;




public class LicenseService {

    private License license;

    private Set<String> licensedRegimes;


    private LicenseProvider licenseProvider;


    public void initialize() {

        license = licenseProvider.retrieveLicense();

        // This is a stopgap solution until we revamp our licensing.  It can be made faster but it's not used
        // during trade processing so leaving alone for now.  We have no standardized way of referring to a regime
        // so this adds both the prefix and the Tag's name for that prefix into the list.
        licensedRegimes = new HashSet<String>();

        for(String moduleName : license.getWorkflowWhitelist().get(RequestContext.ApplicationType.WEBTOOL)) {

            String modulePrefix = StringUtils.substringBefore(moduleName, "_");
            licensedRegimes.add(modulePrefix);

            Tag tag = Tag.getTagById(modulePrefix);

            if (tag != null) {
                licensedRegimes.add(tag.getName());
            }

            // more hacky stuff, since Tags *mostly* refer to regimes but not always.  Again needs to be revisited
            // ones we really figure out how we're going to use tags.
            licensedRegimes.add("deprecated");
            licensedRegimes.add(Tag.getTagById("deprecated").getName());
            licensedRegimes.add("NONE");
            licensedRegimes.add(Tag.getTagById("NONE").getName());
            licensedRegimes.add("Common Attr");
            licensedRegimes.add(Tag.getTagById("Common Attr").getName());
        }
    }

    public List<String> getWorkflowWhitelist(RequestContext.ApplicationType context) {
        return license.getWorkflowWhitelist().get(context);
    }

    public List<FinMktInfraVersion.FinMktInfraVersionType> getMarketLogicWhitelist(RequestContext.ApplicationType context) {
        return license.getMarketLogicWhitelist().get(context);
    }

    public Set<String> getAssetClassWhitelist(RequestContext.ApplicationType context) {
        return license.getAssetClassWhitelist().get(context);
    }

    public String getClientName() {
        return license.getClientName();
    }

    public License.WorkflowEditorLicense getWorkflowEditingAllowed() {
        return license.getWorkflowEditingAllowed();
    }

    public boolean isLicensedForRegime(String regime) {
        return licensedRegimes.contains(regime);
    }

    public boolean isLicensedForDroitWorkflowEditing() {
        return License.WorkflowEditorLicense.ALL == license.getWorkflowEditingAllowed();
    }
}
