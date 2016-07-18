package com.droitfintech.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.nio.file.Path;
import java.util.HashMap;

import com.droitfintech.exceptions.DroitException;

public class H2DataSourceFactoryImpl implements DataSourceFactoryConstants {

    private static Logger log = LoggerFactory.getLogger(H2DataSourceFactoryImpl.class);

    /**
     * Create a new H2 Datasource
     * @param properties
     * @return
     */
    public static DataSource createDataSource(HashMap properties) {
        DataSource datasource = null;
        try {
             datasource = _createDataSource(properties);
        } catch (PropertyVetoException e) {
            throw new DroitException("Couldn't load Market Logic data", e);
        }
        return datasource;
    }

    /**
     * Create a new H2 Datasource with from the properties provided
     * @param properties
     * @return
     * @throws PropertyVetoException
     */
    private static DataSource _createDataSource(HashMap properties) throws PropertyVetoException {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass( (String)properties.get(DRIVER_CLASSS_NAME) );
        ds.setJdbcUrl(createJdbcUrl(properties));
        ds.setUser("");
        ds.setPassword("");
        ds.setInitialPoolSize(Integer.parseInt( (String)properties.get(INITIAL_POOL_SIZE)));
        ds.setAcquireIncrement(Integer.parseInt((String)properties.get(ACQUIRED_INCREMENT)));
        ds.setMaxPoolSize(Integer.parseInt((String)properties.get(MAXPOOL_SIZE)));
        ds.setMinPoolSize(Integer.parseInt((String)properties.get(MINPOOL_SIZE)));
        ds.setMaxIdleTimeExcessConnections(Integer.parseInt((String)properties.get(MAX_IDLE_TIME)));
        ds.setPreferredTestQuery(PREFERED_TEST_QUERY);
        ds.setCheckoutTimeout(Integer.parseInt((String)properties.get(CHECKPOINT_TIMEOUT)));
        ds.setTestConnectionOnCheckin(Boolean.parseBoolean((String)properties.get(TEST_CONNECTIO_ON_CHECKIN)));
        ds.setIdleConnectionTestPeriod(Integer.parseInt((String)properties.get(IDLE_CONNECTION_TEST_PERIOD)));
        return ds;

    }

    /***
     * Create a jdbc COnnection Url from the Properties provided
     * @param properties
     * @return
     */
    private static String createJdbcUrl(HashMap properties) {
        String dbCOnnectionUrl = (String)properties.get( CONNECTION_URL );
        Path h2DataPath = FilesystemDataLoader.getAbsolutePath(DB_LOCATION);
        String dbpath = h2DataPath.toString();
        dbpath = StringUtils.remove(dbpath, ".mv.db");
        dbCOnnectionUrl.replace("$$$",dbpath);
        log.info("Loading Market Logic data from path {}", dbpath);
        return dbCOnnectionUrl;
    }
}