package com.droitfintech.services.endpoints;

/**
 * Created by christopherwhinds on 6/23/16.
 */
public interface HttpServiceEndPoint extends ServiceEndPoint {

    void execute();
    Object getResponse();
    int getStatusCode();


}
