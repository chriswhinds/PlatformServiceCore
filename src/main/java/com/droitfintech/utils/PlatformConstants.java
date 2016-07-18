package com.droitfintech.utils;

/**
 * Single place for all platform constants
 * Created by christopherwhinds on 6/21/16.
 */
public interface PlatformConstants {

    String SERVICE_BOOTSTRAP_CONFIG = "bootstrap.config";
    String MICRO_SERVICE_BOOTSTRAP_CONFIG = "microservice.bootstrap.config";

    String SERVICE_OVERRIDE_CONFIG = "config.Override.config";
    String SERVICE_LOGGING_CONFIG = "logging.config";

    String SERVICE_IMPLEMENTATION_CLASS = "service class name";
    String PLATFORM_SERVICE_IMPLEMENTATION_CLASS = "service class name";



    String SERVICE_NAME = "service name";
    String SERVICE_INSTANACE_ID = "instance id";


    String SERVICE_END_POINTS_MAP = "service end points";

    String PLATFORM_END_POINTS_MAP = "platform end points";

    String SERVICE_PROVIDER_END_POINTS_MAP ="service provider end points";

    // Command And control
    String SERVICE_END_POINT_NAME       = "name";
    String SERVICE_END_POINT_HOST_NAME  = "host";
    String SERVICE_END_POINT_PORT       = "port";


    String SERVICE_PROPERTIES = "service properties";
    String BOOTSTRAP_MODE = "bootstrap mode";

    enum BootstrapMode { platform , peer };

    String HTTP_SERVICE_HOST        = "service.host";
    String HTTP_SERVICE_PORT        = "service.port";
    String HTTP_SERVICE_BASE        = "service.base.url";
    String HTTP_GETQUERY_PARMS      = "service.get.parms";

    String CONFIGURATION_SERVICE_GETCONFIG_URL = "/CONFSVC/svccall/getconfig?";
    String RENDEZVOUS_SERVICE_GETPEERS_URL     = "/RENDSVC/svccall/getpeers?";




}
