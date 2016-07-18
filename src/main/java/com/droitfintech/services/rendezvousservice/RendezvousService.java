package com.droitfintech.services.rendezvousservice;

import com.droitfintech.services.ConfigurationException;
import com.droitfintech.services.DroitPlatformService;
import com.droitfintech.services.RegistrationException;
import com.droitfintech.utils.PlatformConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by christopherwhinds on 6/21/16.
 */
@Path("svccall")
public class RendezvousService implements DroitPlatformService, PlatformConstants , RendezvousServiceConstants {

    private static Logger logger = LoggerFactory.getLogger(RendezvousService.class);
    private HashMap microServiceProperties;
    private Server server;
    private String serviceName;

    private ConcurrentHashMap  <String,HashMap>registryActiverMicroServiceByName            =  new ConcurrentHashMap();
    private ConcurrentHashMap  <String,ConcurrentHashMap>registryActiveMicroServiceByGroup  =  new ConcurrentHashMap();

    /**
     * Start the embbeded Jetty Service with Jersey REST
     */
    public void start() {
        try {
            logger.info( serviceName + " Starting Embedded Jetty - s ");
            ResourceConfig config = new ResourceConfig();
            config.packages(SERVICE_PACKAGES);
            ServletHolder servlet = new ServletHolder(new ServletContainer(config));
            InetSocketAddress hostAddress = getMicroServiceHostaddress();
            server = new Server(hostAddress);
            ServletContextHandler context = new ServletContextHandler(server, SERVICE_CONTEXT_BASE);
            context.addServlet(servlet, SERVICE_CONTEXT_BASE);
            //Start the server
            server.start();
            logger.info(serviceName + " Embedded Jetty successfully");
        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * Constract a InetSocketAddress
     * @return InetSocketAddress
     */
    private InetSocketAddress getMicroServiceHostaddress() throws ConfigurationException{
        if(!microServiceProperties.containsKey(SERVICE_END_POINTS_MAP) )
            throw new ContainerException("Configration does not contain any end point configuation:");
        ArrayList<HashMap> serviceEndPointsList = (ArrayList<HashMap>) microServiceProperties.get(SERVICE_END_POINTS_MAP);
        String hostName = "";
        String port     = "";
        Iterator<HashMap> listIterator = serviceEndPointsList.iterator();
        while(listIterator.hasNext()){
            HashMap endPointItem = listIterator.next();
            if(endPointItem.get(SERVICE_END_POINT_NAME).equals("rendezvous")){
                hostName =  (String)endPointItem.get(SERVICE_END_POINT_HOST_NAME);
                port     =  (String)endPointItem.get(SERVICE_END_POINT_PORT);
            }
        }
        InetSocketAddress hostAddress = new InetSocketAddress(hostName, Integer.parseInt(port) );
        return hostAddress;
    }

    /**
     * Stop the Micro Service
     */
    public void stop() {
        logger.info( serviceName + " Stopping Embedded Jetty ");
        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Exception Occured",e);
        }
        logger.info( serviceName + " Stopping Embedded Jetty successfully");
    }

    /**
     * ReStart the Micro Service
     */
    public void reStart() {
        logger.info(serviceName + " ReStarting Embedded Jetty ");
        start();
        logger.info(serviceName + " ReStart  Embedded Jetty successfully ");
    }

    /**
     * Set the configuration properties
     * @param properties
     */
    public  void setConfiguration(HashMap properties){
        microServiceProperties = properties;
        serviceName = (String)microServiceProperties.get(SERVICE_NAME);

    }


    /**
     * Retgister a Service ot Service Group
     * @param servicedefinition
     * @return
     */
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(  @FormParam("servicedefinition") String servicedefinition ) {
        try {
            //Get the service info and register it in the Maps
            ObjectMapper mapper = new ObjectMapper();
            HashMap registeringServiceMap = mapper.readValue(servicedefinition,HashMap.class);
            //IF the service is part of a group register service with the group
            //If standalone register as standalone
            String groupName = (String)registeringServiceMap.get("servicecluster");
            if( StringUtils.isEmpty(groupName) ){
                registerStandaloneMicroservice(registeringServiceMap);
            } else
                registerGroupMicroservice(registeringServiceMap);
            return Response
                    .ok("Service - " + serviceName + " registered successfully" , MediaType.TEXT_PLAIN)
                    .build();
        } catch (Exception e) {
           logger.error("Exception Occured while registering a service",e);
            throw new WebApplicationException(500);
        }
    }

    /**
     * Register a standalone MicroService if a service already exist by that name
     * throw an exception and and exit
     * @param serviceDefinitionMap
     */
    private void registerStandaloneMicroservice(HashMap serviceDefinitionMap) throws RegistrationException {
        String serviceName = (String)serviceDefinitionMap.get("servicename");
        String instantanceId = (String)serviceDefinitionMap.get("instanceid");
        String serviceFullName = serviceName + "-" + instantanceId;
        logger.info("Registering Standalone Service - :" + serviceFullName  );
        if(registryActiverMicroServiceByName.containsKey(serviceFullName))
            throw new RegistrationException(" Registration Standalone service failed for Service/InstId ["+ serviceFullName +"already registered");
        registryActiverMicroServiceByName.put(serviceFullName,serviceDefinitionMap);
        logger.info("Registered Standalone Service - :" + serviceFullName + "in active service registry");
    }

    /**
     * Register a Group MicroService , group service registry is a map of maps or maps
     * @param serviceDefinitionMap
     */
    private void registerGroupMicroservice(HashMap serviceDefinitionMap) throws RegistrationException{
    String groupName = (String)serviceDefinitionMap.get("groupname");
        String serviceName = (String)serviceDefinitionMap.get("servicename");
        String instantanceId = (String)serviceDefinitionMap.get("instanceid");
        String serviceFullName = serviceName + "-" + instantanceId;
        logger.info("Registering Group Service - :" + groupName  + "- Service Name" + serviceFullName );
        if(registryActiveMicroServiceByGroup.containsKey(groupName)) {
            ConcurrentHashMap serviceGroupMap = registryActiveMicroServiceByGroup.get(groupName);
            if(serviceGroupMap.containsKey(serviceFullName))
                throw new RegistrationException(" Registration Group service failed for Service/InstId ["+ serviceFullName +"already registered within the group:" + groupName);
            else
                serviceGroupMap.put(serviceFullName,serviceDefinitionMap);
        } else {
              ConcurrentHashMap serviceGroupMap = new ConcurrentHashMap();
            serviceGroupMap.put(serviceFullName,serviceDefinitionMap);
        }
        registryActiverMicroServiceByName.put(serviceFullName,serviceDefinitionMap);
        logger.info("Registered Group Service - :" + groupName  + "- Service Name" + serviceFullName + "in active group service registry");
    }

    /***
     * Retrieve The Service group definition for a given service group name
     * @param servicegroup
     * @return
     */
    @GET
    @Path("getServiceGroup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegisterServices(@QueryParam("servicegroup") String servicegroup) {
        logger.info("Retrieve Registered Service Group - Name:" + servicegroup  );
        try {
            ConcurrentHashMap serviceGroupDefintionMap = null;
            if(registryActiveMicroServiceByGroup.containsKey(servicegroup)) {
                serviceGroupDefintionMap = registryActiveMicroServiceByGroup.get(servicegroup);
            }else{
                serviceGroupDefintionMap = new ConcurrentHashMap();
                serviceGroupDefintionMap.put("errormsg","No service group found by the group name:" + servicegroup);
            }
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writeValueAsString(serviceGroupDefintionMap);
            return Response
                    .ok(result, MediaType.APPLICATION_JSON)
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error("Exception Occured -",e);
        }
        throw new WebApplicationException(500);
    }

    /***
     * Retrieve the Service group for a given grouped service cluster
     * @param servicename
     * @param instanceId
     * @return
     */
    @GET
    @Path("getService")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistedService(@QueryParam("servicename")  String servicename,
                                       @QueryParam("instanceId")   String instanceId) {
        logger.info("Retrieve Registered Service - Service Name:" + servicename + "-" + instanceId );
        String serviceFullName = servicename + "-" + instanceId;
        try {
            HashMap serviceDefintionMap = null;
            if(registryActiverMicroServiceByName.containsKey(serviceFullName)) {
                serviceDefintionMap = registryActiverMicroServiceByName.get(serviceFullName);
            }else{
                serviceDefintionMap = new HashMap();
                serviceDefintionMap.put("errormsg","No service found by the name:" + serviceFullName);
            }
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writeValueAsString(serviceDefintionMap);
            return Response
                    .ok(result, MediaType.APPLICATION_JSON)
                    .build();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error("Exception Occured -",e);
        }
        throw new WebApplicationException(500);
    }

}
