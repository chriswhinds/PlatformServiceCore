package com.droitfintech.services;

import java.util.HashMap;

/**
 * Created by christopherwhinds on 6/21/16.
 */
public interface DroitPlatformService {

    void start();
    void stop();
    void reStart();

    void setConfiguration(HashMap properties);

}
