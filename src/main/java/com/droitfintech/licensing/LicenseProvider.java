package com.droitfintech.licensing;

/**
 * LicenseProvider loads a license and verifies it.
 *
 * (C) 2014 Droit Financial Technologies, LLC
 * @author nathanbrei
 *
 */
public interface LicenseProvider {

    public License retrieveLicense();

}

