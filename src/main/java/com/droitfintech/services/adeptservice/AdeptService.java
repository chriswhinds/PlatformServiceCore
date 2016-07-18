package com.droitfintech.services.adeptservice;

import com.droitfintech.services.ConfigurationException;
import com.droitfintech.services.DroitPlatformService;
import com.droitfintech.utils.PlatformConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by christopherwhinds on 7/6/16.
 */
public class AdeptService implements DroitPlatformService, PlatformConstants, AdeptServiceConstants{

    private static Logger logger = LoggerFactory.getLogger(AdeptService.class);
    private HashMap microServiceProperties;
    private String serviceName;
    private Server server;
    private AdeptServiceCore adeptServiceCore = new AdeptServiceCore();

    public void start() {
        try {
            //Initialize AdeptServiceCode
            logger.info( serviceName + " Starting Initalizing Service Core");
            adeptServiceCore.setConfigurationProperties(microServiceProperties);
            adeptServiceCore.initialize();
            logger.info( serviceName + " Completed Initalizing Service Core");
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

    public void stop() {

    }

    public void reStart() {

    }

    public void setConfiguration(HashMap properties) {
        microServiceProperties = properties;
        serviceName = (String) microServiceProperties.get(SERVICE_NAME);
    }

    /**
     * Constract a InetSocketAddress
     * @return InetSocketAddress
     */
    private InetSocketAddress getMicroServiceHostaddress() throws ConfigurationException {
        if(!microServiceProperties.containsKey(SERVICE_END_POINTS_MAP) )
            throw new ContainerException("Configration does not contain any end point configuation:");
        ArrayList<HashMap> serviceEndPointsList = (ArrayList<HashMap>) microServiceProperties.get(SERVICE_END_POINTS_MAP);
        String hostName = "";
        String port     = "";
        Iterator<HashMap> listIterator = serviceEndPointsList.iterator();
        while(listIterator.hasNext()){
            HashMap endPointItem = listIterator.next();
            if(endPointItem.get(SERVICE_END_POINT_NAME).equals(SERVICE_ENDPOINT_REF)){
                hostName =  (String)endPointItem.get(SERVICE_END_POINT_HOST_NAME);
                port     =  (String)endPointItem.get(SERVICE_END_POINT_PORT);
            }
        }
        InetSocketAddress hostAddress = new InetSocketAddress(hostName, Integer.parseInt(port) );
        return hostAddress;
    }


    /**
     *  Get a Trade decision using trade , counterParty and ContraParty
     * @param trade
     * @param contraparty
     * @param counterparty
     * @return
     */
    @POST
    @Path("getDecision")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDecision(  @FormParam("trade") String trade,
                                  @FormParam("contraparty") String contraparty,
                                  @FormParam("counterparty") String counterparty) {
        try {

            logger.info(serviceName +"-GetDecesion invoked " );

            ObjectMapper mapper = new ObjectMapper();
            HashMap tradeMap = mapper.readValue(trade.getBytes() ,HashMap.class);
            HashMap contrapartyMap = mapper.readValue(contraparty.getBytes() ,HashMap.class);
            HashMap counterpartyMap = mapper.readValue(counterparty.getBytes() ,HashMap.class);
            HashMap decisionResult = (HashMap) adeptServiceCore.processTrade(tradeMap,contrapartyMap , counterpartyMap );
            String result = mapper.writeValueAsString(decisionResult);
            logger.info(serviceName +"-GetDecesion invoked " );
            return Response
                        .ok(result, MediaType.APPLICATION_JSON)
                        .build();


        } catch (Exception e) {
            logger.error("Exception Occured -",e);
        }
        throw new WebApplicationException(500);

    }



}
