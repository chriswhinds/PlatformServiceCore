package com.droitfintech.utils;

/**
 * Created by christopherwhinds on 7/15/16.
 */
interface DataSourceFactoryConstants {

    String DB_LOCATION                 = "h2.dblocation";
    String DRIVER_CLASSS_NAME          = "h2.driverclass";
    String CONNECTION_URL              = "h2.urlPrefix";
    String INITIAL_POOL_SIZE           = "h2.initialPoolSize";
    String ACQUIRED_INCREMENT          = "h2.acquireIncrement";
    String CHECKPOINT_TIMEOUT          = "h2.checkoutTimeout";
    String MAXPOOL_SIZE                = "h2.maxPoolSize";
    String MINPOOL_SIZE                = "h2.minPoolSize";
    String MAX_IDLE_TIME               = "h2.maxIdleTimeExcessConnections";
    String PREFERED_TEST_QUERY         = "h2.preferredTestQuery";
    String TEST_CONNECTIO_ON_CHECKIN   = "h2.testConnectionOnCheckin";
    String IDLE_CONNECTION_TEST_PERIOD = "h2.idleConnectionTestPeriod";

}
