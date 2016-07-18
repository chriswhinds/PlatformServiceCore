package com.droitfintech.services.endpoints;

import com.droitfintech.services.configservice.ConfigurationService;
import com.droitfintech.utils.PlatformConstants;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by christopherwhinds on 6/23/16.
 */
public class HttpGetServiceEndPointImpl extends HttpServiceEndPointImpl implements HttpGetServiceEndPoint,PlatformConstants{
    private static Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    public void doGet() {
        try {

            //Build the calling URL
            //From Service Base , Service Method Name and calling Query parms



            String url = "http://www.google.com/search?q=httpClient";


            HttpGet request = new HttpGet(url);
            // add request header
            response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            responseObject = result.toString();
        } catch (IOException e) {
            logger.error("Exception has occured",e);
        }
    }

    /**
     * Proxy the call to do the HTTP GET REQUEST , called from the Object user
     */
    public void execute() {
        doGet();
    }
}
