package com.droitfintech.services.endpoints;

/**
 * Created by christopherwhinds on 6/23/16.
 */
public interface FiledropServiceEndPoint extends ServiceEndPoint{

    void start();
    void stop();
    void registerMessageHandler(Object messageHandler);

}
