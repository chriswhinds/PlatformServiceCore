package com.droitfintech.services;

import com.droitfintech.utils.PlatformConstants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by christopherwhinds on 6/21/16.
 */
@Path("svccall")
public class ServiceIOCtrl implements PlatformConstants , ServiceIOCtrlConstants {

    private static Logger logger = LoggerFactory.getLogger(ServiceBootstrap.class);

    public DroitPlatformService getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(DroitPlatformService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    private DroitPlatformService serviceImpl;

    private HashMap configurationProperties;

    private ServiceBootstrap bootstrapper ;


    private Server server;

    public void setBootstrapper(ServiceBootstrap bootstrapper) {
        this.bootstrapper = bootstrapper;
    }

    /**
     * Default Contsructor

     */
    public ServiceIOCtrl() {
    }

    public HashMap getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(HashMap configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    /**
     * Start the IO Control Service Impl
     */
    public void start() {
        try {

            logger.info("IOCTRL Starting Embedded Jetty ");
            ResourceConfig config = new ResourceConfig();
            config.packages(SERVICE_PACKAGES );
            ServletHolder servlet = new ServletHolder(new ServletContainer(config));
            InetSocketAddress hostAddress = getControlHostaddress();
            server = new Server(hostAddress);
            ServletContextHandler context = new ServletContextHandler(server,SERVICE_CONTEXT_BASE );
            context.addServlet(servlet, SERVICE_CONTEXT_BASE);
            //Start the server
            server.start();
            logger.info("IOCTRL Stared Embedded Jetty successfully");

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
    private InetSocketAddress getControlHostaddress() throws ConfigurationException{
        if(!configurationProperties.containsKey(PLATFORM_END_POINTS_MAP) )
            throw new ContainerException("Cnngigration does not contain any end point configuation:");
        ArrayList<HashMap> serviceEndPointsList = (ArrayList<HashMap>)configurationProperties.get(PLATFORM_END_POINTS_MAP);
        String hostName = "";
        String port     = "";
        Iterator<HashMap> listIterator = serviceEndPointsList.iterator();
        while(listIterator.hasNext()){
            HashMap endPointItem = listIterator.next();
            if(endPointItem.get(SERVICE_END_POINT_NAME).equals("cmdctrl")){
                hostName =  (String)endPointItem.get(SERVICE_END_POINT_HOST_NAME);
                port     =  (String)endPointItem.get(SERVICE_END_POINT_PORT);
            }
        }
        InetSocketAddress hostAddress = new InetSocketAddress(hostName, Integer.parseInt(port) );
        return hostAddress;
    }


    /**
     * Start the Service Impl
     */
    @GET
    @Path("start")
    public void serviceStart() {
        serviceImpl.start();
    }

    /**
     * Stop the Service Impl
     */
    @GET
    @Path("stop")
    public void stop(){
        logger.info("IOCTRL Stopping ");
        serviceImpl.stop();
        logger.info("IOCTRL Stop complete");
    }

    /***
     * ReStart the Service Impl
     */
    @GET
    @Path("restart")
    public void reStart(){
        logger.info("IOCTRL Restarting down");
        serviceImpl.reStart();
        logger.info("IOCTRL Restart complete");
    }

    /**
     * Kill the service and shutdown exit the jvm to the OS
     */
    @GET
    @Path("kill")
    public void kill(){
        logger.info("IOCTRL Shutting down");
        try {
            serviceImpl.stop();
            server.stop();
            server.destroy();
            bootstrapper.signalShutdown();

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("IOCTRL Shutdown complete");
    }


}
