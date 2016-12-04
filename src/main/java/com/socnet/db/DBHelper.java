/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socnet.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.jdbc3.JDBC3Savepoint;

/**
 *
 * @author fallgamlet
 */
public class DBHelper {
    public interface DBItem {
        public boolean save();
        public boolean load();
        public boolean fill();
        public String getUUID();
    }
    
//    public static Connection newConnection() throws ClassNotFoundException, SQLException {
//        Connection connection = null;
//        Class.forName("org.sqlite.JDBC");
//        connection = DriverManager.getConnection("jdbc:sqlite:data.db");
//        return connection;
//    }
    
    protected static final String DBUrl = "jdbc:sqlite:data.db";
    protected static final String DBLogin = "root";
    protected static final String DBPassword = "myPass";
    protected static final int DBVersion = 1;
    
    protected static final String TDBINFO = "dbinfo";
    protected static final String FKEY = "key";
    protected static final String FVALUE = "value";
    protected static final String KEY_VERSION = "version";
    
    public DBHelper() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        initDriver();
        getConnection();
    }
    
    protected static Driver mDriver;
    protected static Driver initDriver() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (mDriver == null) {
            mDriver = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
        }
        return mDriver;
    }

    protected Connection mConnection;
    public Connection getConnection() throws SQLException {
        if (mConnection == null || mConnection.isClosed()) {
            mConnection = DriverManager.getConnection(DBUrl, DBLogin, DBPassword);
        }
        if (!readInfo(mConnection)) {
            onCreate(mConnection);
        }
        if (getVersion() < DBVersion) {
            onUpgrade(mConnection, getVersion(), DBVersion);
            setVersion(mVersion);
            writeInfo(mConnection);
        }
        return mConnection;
    }
    
    public void close() {
        try {
            if (mConnection == null || !mConnection.isClosed()) {
                mConnection.close();
                mConnection = null;
            }
        } catch (SQLException ex) {
        }
    }
    
    private int mVersion=0;
    public int getVersion() { return mVersion; }
    private void setVersion(int val) { mVersion = val; }
    private void setVersion(String val) {
        int ver = 0;
        if (val != null) {
            ver = Integer.valueOf(val);
        }
        mVersion = ver; 
    }
    
    protected void initDatabase(Statement statement) throws SQLException {
        setVersion(DBVersion);
        String query = "create table if not exists "+TDBINFO+" ("+FKEY+" text primary key, "+FVALUE+" text default null)";
        statement.execute(query);
        writeInfoField(statement, KEY_VERSION, String.valueOf(getVersion()));
    }
    
    protected String readInfoField(Statement statement, String key) throws SQLException {
        String query = "select "+FVALUE+" from "+TDBINFO+" where "+FKEY+" = \'"+key+"\' limit 1";
        ResultSet cursor = statement.executeQuery(query);
        String value = cursor.next()? cursor.getString(1): null;
        cursor.close();
        return value;
    }
    
    protected boolean writeInfoField(Statement statement, String key, String value) throws SQLException {
        if (key == null) { return false; }
        if (value == null) { value = "null"; }
        String query = "insert or replace into "+TDBINFO+" ("+FKEY+","+FVALUE+") values (\'"+key+"\',\'"+value+"\')";
        int res = statement.executeUpdate(query);
        return res != -1;
    }
    
    protected boolean readInfo(Connection con) {
        boolean check = true;
        try (Statement statement = con.createStatement()) {
            setVersion(readInfoField(statement, KEY_VERSION));
        } catch (SQLException e) {
            check = false;
        }
        return check;
    }
    protected void readInfo(Statement statement) throws SQLException {
        setVersion(readInfoField(statement, KEY_VERSION));
    }
    
    protected void writeInfo(Connection con) throws SQLException {
        Statement statement = con.createStatement();
        writeInfo(statement);
        statement.close();
        con.commit();
    }
    protected void writeInfo(Statement statement) throws SQLException {
        writeInfoField(statement, KEY_VERSION, String.valueOf(getVersion()));
    }
    
    public void onCreate(Connection con) throws SQLException {
        if (con == null) { return; }
        Savepoint savepoint = con.setSavepoint("init_db");
        try {
            con.setAutoCommit(false);
            Statement statement = con.createStatement();
            initDatabase(statement);
            statement.close();
            con.commit();
        } catch (Exception e) {
            con.rollback(savepoint);
            throw e;
        }
        
    }
    
    public void onUpgrade(Connection con, int oldVersion, int newVersion) {
        if (con == null) { return; }
    }
    
    
}
