package com.droitfintech.services;

import com.droitfintech.services.endpoints.HttpGetServiceEndPointImpl;
import com.droitfintech.utils.PlatformConstants;
import org.apache.log4j.xml.DOMConfigurator;
import org.glassfish.jersey.server.ContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service Bootstrap class
 * Created by christopherwhinds on 6/21/16.
 */
public class ServiceBootstrap implements PlatformConstants {

    private static Logger logger = LoggerFactory.getLogger(ServiceBootstrap.class);
    private HashMap bootStrapConfig;
    private HashMap microServicBootStrapConfig = new HashMap();

    private HashMap serviceOverrideConfig;
    private HashMap microServiceConfig;
    private ServiceIOCtrl ioCtrl;
    private Lock shutdownLock = null;
    private Condition signalShutdown = null;
    private DroitPlatformService microService;
    /**
     * Contructor
     */
    public ServiceBootstrap() {
        //set up the locks , shutdown signals
        shutdownLock = new ReentrantLock();
        signalShutdown = shutdownLock.newCondition();
    }
    /**
     * Run the service activities
     */
    void runService(){
        //Get the Log4J config Location and configure the logging
        String loggingConfigLocation = null;
        if(System.getProperties().containsKey(SERVICE_LOGGING_CONFIG)){
        loggingConfigLocation = System.getProperty(SERVICE_LOGGING_CONFIG);
        } else {
            // No Logging config specified exit now
            System.exit(100);
        }
        //DOMConfigurator is used to configure logger from xml configuration file
        DOMConfigurator.configure(loggingConfigLocation);
        logger.info("Service Daemon Bootstrap staring up...");
        //Get the Service Config
        try {
            loadBootstrapConfiguration();
            loadMicrosServiceConfiguration();
            createControlService();
            createService();
            //Wait For Service Kill signal
            asyncWait();
        } catch (ConfigurationException e) {
            logger.error("Exception occured:",e);
        } catch (InterruptedException e) {
            logger.error("Exception occured:",e);
        }
        logger.info("Service Daemon Bootstrap shutting down ..");
    }

    /**
     * Create the low leve Command ControlService
     */
    private void createControlService() {
        ioCtrl = new ServiceIOCtrl();
        ioCtrl.setConfigurationProperties(bootStrapConfig);
        ioCtrl.setBootstrapper(this);
        ioCtrl.start();
    }


    /***
     * Create a platform or peer service.
     */
    private void createService() throws ConfigurationException {
        try {
            BootstrapMode mode = Enum.valueOf(BootstrapMode.class,(String)getServiceProperties().get(BOOTSTRAP_MODE));
            switch(mode) {
                case platform:
                    createPlatformUtilityMicroService();
                    break;
                case peer:
                    createMicroService();
                    break;
                default:
                    throw new ConfigurationException("Invalid Config Mode Must be: platform or peer");
            }
        } catch (ConfigurationException e) {
            logger.error("Exception occured:",e);
        }
    }



    /***
     * Get this service's service properties from the config
     * @return
     */
    private HashMap getServiceProperties(){
        if(!bootStrapConfig.containsKey(SERVICE_PROPERTIES) )
            throw new ContainerException("Configration does not contain any end point configuation:");
        return (HashMap)bootStrapConfig.get(SERVICE_PROPERTIES);
    }

    /**
     * Find and endpoint defintion from the map by name
     * @param endPointName
     * @return
     */
    private HashMap getEndPointDefintions(String endPointName) {

        if (!bootStrapConfig.containsKey(PLATFORM_END_POINTS_MAP))
            throw new ContainerException(" Configration does not contain any end point configuation:");
        ArrayList<HashMap> serviceEndPointsList = (ArrayList<HashMap>) bootStrapConfig.get(PLATFORM_END_POINTS_MAP);
        Iterator<HashMap> listIterator = serviceEndPointsList.iterator();
        while (listIterator.hasNext()) {
            HashMap endPointItem = listIterator.next();
            if (endPointItem.get(SERVICE_END_POINT_NAME).equals(endPointName)) {
                return endPointItem;
            }
        }
       return null;

    }
    /**
     * Create the service implementation from the defintions fetched from config , fetch the config from the Configuration Service
     * instanciate the class and set the
     */
    private void createMicroService() throws ConfigurationException {
        String serviceImplementationClazz = (String) microServicBootStrapConfig.get(SERVICE_IMPLEMENTATION_CLASS);;
        try {
            //Fetch the Platform Service Config from the Configuration Service
            HashMap confServiceEndPointDef = getEndPointDefintions("configsvc");
            String hostName    = (String)confServiceEndPointDef.get("host");
            String port        = (String)confServiceEndPointDef.get("port");
            String serviceName = (String) microServicBootStrapConfig.get(SERVICE_NAME);
            String instanceId  = (String) microServicBootStrapConfig.get(SERVICE_INSTANACE_ID);
            //Build the Service Url to call
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("http://").append(hostName).append(":").append(port).append("/").append(CONFIGURATION_SERVICE_GETCONFIG_URL);
            urlBuilder.append("serviceName=").append(serviceName).append("&");
            urlBuilder.append("instanceId=").append(instanceId);
            HashMap parms = new HashMap();
            parms.put("url",urlBuilder.toString());
            //Execute the call to the Config service
            HttpGetServiceEndPointImpl httpGetEIP = new HttpGetServiceEndPointImpl();
            httpGetEIP.initialize(parms);
            httpGetEIP.execute();
            if(httpGetEIP.getStatusCode() == 200 ) {
                logger.info("Service Bootstrap creating Micros service implementaion ");
                Yaml yaml = new Yaml();
                microServiceConfig = (HashMap) yaml.load(new String((byte[])httpGetEIP.getResponse()));
                microService = (DroitPlatformService) Class.forName(serviceImplementationClazz).newInstance();
                microService.setConfiguration(microServiceConfig);
                microService.start();
                logger.info("Service Bootstrap created Micro service for Service Name: " + (String) microServicBootStrapConfig.get(SERVICE_NAME) + " - Service Impl Class: " +  serviceImplementationClazz );
            }
        } catch (Exception e) {
            logger.error("Exception Occured:", e);
            logger.info("Service Bootstrap failed to Micro service for Service Name: " + (String) microServicBootStrapConfig.get(SERVICE_NAME) + " - Service Impl Class: " +  serviceImplementationClazz );

        }
    }

    /**
     * Create the service implementation
     */
    private void createPlatformUtilityMicroService() throws ConfigurationException {
        logger.info("Service Bootstrap creating service implementaion ");
        if(!bootStrapConfig.containsKey(SERVICE_PROPERTIES) )
            throw new ContainerException("Congigration does not contain any end point configuation:");
        HashMap serviceProperties = (HashMap)bootStrapConfig.get(SERVICE_PROPERTIES);
        String serviceImplementationClazz = null;
        if(serviceProperties.containsKey(PLATFORM_SERVICE_IMPLEMENTATION_CLASS)){
            serviceImplementationClazz = (String)serviceProperties.get(PLATFORM_SERVICE_IMPLEMENTATION_CLASS);
        } else {
            throw new ConfigurationException("Service Implementation Class Name missing from configuration");
        }
        try {
            Object serviceImpl = Class.forName(serviceImplementationClazz);
            microService = (DroitPlatformService)serviceImpl;
            microService.setConfiguration(bootStrapConfig);
            microService.start();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("Service Bootstrap created service for Service Name: " + (String)bootStrapConfig.get(SERVICE_NAME) + " - Service Impl Class: " +  serviceImplementationClazz );
    }

    /**
     * Load the platform BootStrap configuration # COULD BE REFACTORED
     * @throws ConfigurationException
     */
    private void loadMicrosServiceConfiguration() throws ConfigurationException{
        logger.info("Service Daemon loading Microservice Bootstrap config from -D parms ...");
        if(System.getProperties().containsKey(MICRO_SERVICE_BOOTSTRAP_CONFIG)){
            logger.info("MicroService Config Parms in effect" + System.getProperties().containsKey(MICRO_SERVICE_BOOTSTRAP_CONFIG));
            String[] microserviceBootstrapParms = ((String)System.getProperty(MICRO_SERVICE_BOOTSTRAP_CONFIG)).split(",");
            microServicBootStrapConfig.put(SERVICE_NAME,microserviceBootstrapParms[0]);
            microServicBootStrapConfig.put(SERVICE_INSTANACE_ID,microserviceBootstrapParms[1]);

        } else {
            throw new ConfigurationException("Service Bootstrap failed - no boot starp configuration specified on command line : -DBootstrap.config=filename.yml");
        }
        logger.info("Service Daemon loaded Microservice Bootstrap config from -D parms ...");
    }

    /**
     * Load the BootStrap configuration  # COULD BE REFACTORED
     * @throws ConfigurationException
     */
    private void loadBootstrapConfiguration() throws ConfigurationException{
        logger.info("Service Daemon Bootstrap loading bootstrap config yml ...");
        String bootstrapConfigLocation = null;
        if(System.getProperties().containsKey(SERVICE_BOOTSTRAP_CONFIG)){
            bootstrapConfigLocation = System.getProperty(SERVICE_BOOTSTRAP_CONFIG);
        } else {
            throw new ConfigurationException("Service Boostrap failed - no boot starp configuration specified on command line : -DBootstrap.config=filename.yml");
        }
        try{
            Yaml yaml = new Yaml();
            bootStrapConfig = (HashMap) yaml.load(new FileInputStream(bootstrapConfigLocation));
        } catch (FileNotFoundException e) {
            throw new ConfigurationException("Service Boostrap failed - File not found at location specified on command line : -DBootstrap.config=" + bootstrapConfigLocation,e);
        }
        logger.info("Service Daemon Bootstrap completed loading bootstrap config yml ...");
    }

    /**
     * Runtime  Bootstrap
     * @param args
     */
    public static void main(String[] args )
    {
        ServiceBootstrap bootstrap = new ServiceBootstrap();
        bootstrap.runService();
    }

    /**
     * Wait Async for notifoication of shutdown
     * @throws InterruptedException
     */
    public void asyncWait() throws InterruptedException {
        shutdownLock.lock();
        try {
            signalShutdown.await(); // releases lock and waits until signalShutdown() is called
        } finally {
            shutdownLock.unlock();
        }
    }

    /**
     * Notify of a shutdown by service Ctrl
     */
    public void signalShutdown() {

        shutdownLock.lock();
        try {
            signalShutdown.signal(); //Signal Shutdown from another thread
        } finally {
            shutdownLock.unlock();
        }
    }
}
