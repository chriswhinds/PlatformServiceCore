package com.droitfintech.services.adeptservice;

/**
 * Created by christopherwhinds on 7/5/16.
 */
public interface AdeptServiceConstants {

    String SERVICE_CONTEXT_BASE =  "/ADEPTSVC/*";
    String SERVICE_PACKAGES     =  "com.droit.services";
    String SERVICE_ENDPOINT_REF =  "adeptsvc";

    String HIBERNATE_PARMS          = "hibernate";
    String UNIT_NAME                = "persistenceUnitName";
    String PACKAGES_TO_SCAN         = "packageToScan";
    String JPA_PROPERTIES           = "jpaDialect";
    String JPA_DIALECT              = "hibernate.dialect";

    String JPA_VENDOR_ADAPTOR_PARMS = "jpaVendorAdapter";
    String JPA_VENDOR_SHOWSQL       = "jpaVendorAdapter";
    String JPA_VENDOR_GENERATE_DDL  = "jpaVendorAdapter";
    String JPA_VENDOR_DB_PLATFORMS = "jpaVendorAdapter";

    String DROIT_REPOSITORY_LOCATION = "droit.repository.location";
    String CLIENT_REPOSITORY_LOCATION = "client.repository.location";



}
