package com.droitfintech.services.endpoints;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.HashMap;

/**
 * Created by christopherwhinds on 6/23/16.
 */
abstract public class HttpServiceEndPointImpl implements HttpServiceEndPoint{
    protected HashMap properties;
    protected Object responseObject;
    protected HttpClient client;
    protected HttpResponse response;
    /**
     * Execute the operration abstract must be implemented by the derived class2
     */
    abstract public void execute();

    /**
     * Get the response object from the operation
     * @return
     */
    public Object getResponse() {
        return responseObject;
    }

    /**
     * INitialize the HTTP endpoint
     * @param properties
     * Note Must contain BASE URL , SERVICE CONTEXT[ Service Name, method ] , GET QueryParms value pairs or POST value pairs  ,
     */
    public void initialize(HashMap properties) {
        this.properties = properties;
        client = HttpClientBuilder.create().build();
    }

    /**
     * Get the last call http status
     * @return
     */
    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }
}
