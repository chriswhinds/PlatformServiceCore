package com.droitfintech.services.configservice;

import com.droitfintech.services.ConfigurationException;
import com.droitfintech.services.DroitPlatformService;
import com.droitfintech.utils.PlatformConstants;

import org.apache.commons.io.IOUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by christopherwhinds on 6/21/16.
 */
@Path("svccall")
public class ConfigurationService implements DroitPlatformService, PlatformConstants , ConfigurationServiceConstants{
    private static Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    private HashMap microServiceProperties;
    private Server server;
    private String serviceName;

    public static String CONFIGURATION_SERVICE_FILE_REPO = "repository location";
    public void start() {
        try {
            logger.info( serviceName + " Starting Embedded Jetty ");
            ResourceConfig config = new ResourceConfig();
            config.packages(SERVICE_PACKAGES);
            ServletHolder servlet = new ServletHolder(new ServletContainer(config));
            InetSocketAddress hostAddress = getMicroServiceHostaddress();
            server = new Server(hostAddress);
            ServletContextHandler context = new ServletContextHandler(server, SERVICE_CONTEXT_BASE );
            context.addServlet(servlet, SERVICE_CONTEXT_BASE);
            //Start the server
            server.start();
            logger.info(serviceName + " Embedded Jetty successfully");
        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }finally{
            if (server!=null)
                server.destroy();
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
            if(endPointItem.get(SERVICE_END_POINT_NAME).equals("configsvc")){
                hostName =  (String)endPointItem.get(SERVICE_END_POINT_HOST_NAME);
                port     =  (String)endPointItem.get(SERVICE_END_POINT_PORT);
            }
        }
        InetSocketAddress hostAddress = new InetSocketAddress(hostName, Integer.parseInt(port) );
        return hostAddress;
    }


    /**
     * Stop the Service
     */
    public void stop() {
        logger.info( serviceName + " Stopping Embedded Jetty ");
        try {
            server.stop();
        } catch (Exception e) {
            logger.equals(e);
        }
        logger.info( serviceName + " Stopping Embedded Jetty successfully");
    }

    /**
     * Restart the Service
     */
    public void reStart() {
        logger.info( serviceName + " ReStarting Embedded Jetty ");
        start();
        logger.info( serviceName + " ReStart  Embedded Jetty successfully ");
    }

    public  void setConfiguration(HashMap properties){
        microServiceProperties = properties;
        serviceName = (String) microServiceProperties.get(SERVICE_NAME);
    }

    /**
     * Get the document repository location
     * @return
     */
    private String getRepositoryLocation(){
        if(!microServiceProperties.containsKey(SERVICE_PROPERTIES) )
            throw new ContainerException("Congigration does not contain any end point configuation:");
        HashMap serviceProperties = (HashMap) microServiceProperties.get(SERVICE_PROPERTIES);
        return (String)serviceProperties.get(CONFIGURATION_SERVICE_FILE_REPO);

    }
    /**
     * Get the Service Config file for this request by Service Name and instance Id
     * @param serviceName
     * @param instanceId
     * @return
     */
    @GET
    @Path("getconfig")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServiceConfiguration(@QueryParam("servicename") String  serviceName,
                                            @QueryParam("instanceId") String instanceId) {
        // find the Config Document for the service and instance id
        //Assemble the Name of the file based on the query parms on the call
        String repoLocal = getRepositoryLocation();
        StringBuilder docFileNameBuilder = new StringBuilder();
        docFileNameBuilder.append(repoLocal).append(File.separator).append(serviceName.toLowerCase()).append("_").append(instanceId).append(".yml");
        File serviceConfigFile = new File(docFileNameBuilder.toString());
        if (!serviceConfigFile.exists()) {
            throw new WebApplicationException(404);
        }
        byte[] docBuffer = new byte[(int)serviceConfigFile.length()];
        try {
            // create a byte array of the file in correct format
            //read the file into a byte array and set it in the content to send on the response
            FileInputStream targetFileStream = new FileInputStream(serviceConfigFile);
            IOUtils.readFully(targetFileStream,docBuffer);
        } catch (IOException e) {
            logger.error("Exception Occured:",e);
            throw new WebApplicationException(500);
        }
        return Response
                .ok(docBuffer, MediaType.TEXT_PLAIN)
                //.header("content-disposition","attachment; filename = doc.rtf")
                .build();

    }


}
