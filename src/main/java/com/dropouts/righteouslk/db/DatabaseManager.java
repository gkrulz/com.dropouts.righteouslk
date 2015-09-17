package com.dropouts.righteouslk.db;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Padmaka on 4/3/2015.
 */
public class DatabaseManager {
    private static String NODE;
    private Cluster cluster;
    private static DatabaseManager instance;
    private static Object lock = new Object();
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class);

    static {
        Properties properties = new Properties();
        try {
            properties.load(DatabaseManager.class.getClassLoader().getResourceAsStream("databaseConfig.properties"));
            NODE = properties.getProperty("NODE");
            System.out.println(NODE);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private DatabaseManager() {
        BasicConfigurator.configure();
        cluster = Cluster.builder().addContactPoint(NODE).build();
    }

    public static DatabaseManager getInstance(){
        synchronized (lock){
            if(instance == null){
                instance = new DatabaseManager();
            }
        }
        return instance;
    }

    public Session getSession(){
        return this.cluster.connect();
    }

    public void closeCluster(){
        this.cluster.close();
    }
}
