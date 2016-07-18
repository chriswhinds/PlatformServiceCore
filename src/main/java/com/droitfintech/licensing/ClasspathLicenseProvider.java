package com.droitfintech.licensing;

import java.io.InputStream;


import com.droitfintech.exceptions.DroitException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ClasspathLicenseProvider implements LicenseProvider {

    private String filename;

    public ClasspathLicenseProvider() {
        super();
        this.filename = "droit-license.json";
    }

    public ClasspathLicenseProvider(String filename) {
        super();
        this.filename = filename;
    }

    public License retrieveLicense() {

        InputStream licenseStream = this.getClass().getClassLoader().getResourceAsStream(filename);
        if (licenseStream == null) {
            throw new DroitException("Unable to find '" + filename + "' on classpath");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(licenseStream, License.class);
        }
        catch (Exception e) {
            throw new DroitException("Unable to deserialize '" + filename + "'", e);
        }
    }
}
