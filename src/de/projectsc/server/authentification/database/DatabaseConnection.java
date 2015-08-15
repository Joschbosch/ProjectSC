/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification.database;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Connects to MySQL-database.
 *
 * @author David Scholz
 */
public class DatabaseConnection {
    
    private static final String CONNECTION_PROPERTIES_FILEPATH = "";
    
    private static final Log LOGGER = LogFactory.getLog(DatabaseConnection.class);
    
    private static DatabaseConnection instance = null;
    
    private static Connection connection = null;
    
    private String serverName = null;
    private int port = 0;
    private String username = null;
    private String password = null;
    private String databaseName = null;
    
    public DatabaseConnection() {
        super();
    }
    
    private void initConnection() {
        Properties properties = new Properties();
        
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(CONNECTION_PROPERTIES_FILEPATH));
            properties.load(stream);
            
            serverName = properties.getProperty("Servername");
            databaseName = properties.getProperty("Databasename");
            port = Integer.parseInt(properties.getProperty("Port"));
            username = properties.getProperty("Username");
            password = properties.getProperty("Password");
            
            MysqlDataSource ds = new MysqlDataSource();
            ds.setServerName(serverName);
            ds.setPortNumber(port);
            ds.setDatabaseName(databaseName);
            ds.setUser(username);
            ds.setPassword(password);
            
            connection = ds.getConnection();
            
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (SQLException e) {
            LOGGER.error(e);
        }
    }
    
    public void closeConnection() throws SQLException {
        connection.close();
    }
    
    
    public synchronized static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    

}
