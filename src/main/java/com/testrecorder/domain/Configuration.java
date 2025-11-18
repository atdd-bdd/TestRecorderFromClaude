package com.testrecorder.domain;

import java.io.*;
import java.util.Properties;

public class Configuration {
    private String rootFilePath;
    private boolean useTestDoubleForDateTime;
    private boolean useTestDoubleForRunner;
    private String valueTestDoubleForDateTime;
    private String valueTestDoubleForRunner;
    private boolean formNotCloseOnExit;
    private String databaseURL;
    private String databaseJDBCDriver;
    private String databasePassword;
    private String databaseUserID;

    public Configuration() {
        // Default values
        this.rootFilePath = "target\\";
        this.useTestDoubleForDateTime = false;
        this.useTestDoubleForRunner = false;
        this.valueTestDoubleForDateTime = "";
        this.valueTestDoubleForRunner = "";
        this.formNotCloseOnExit = false;
        this.databaseURL = "jdbc:hsqldb:hsql://localhost";
        this.databaseJDBCDriver = "org.hsqldb.jdbcDriver";
        this.databasePassword = "";
        this.databaseUserID = "SA";
    }

    // Getters and Setters
    public String getRootFilePath() {
        return rootFilePath;
    }

    public void setRootFilePath(String rootFilePath) {
        this.rootFilePath = rootFilePath;
    }

    public boolean isUseTestDoubleForDateTime() {
        return useTestDoubleForDateTime;
    }

    public void setUseTestDoubleForDateTime(boolean useTestDoubleForDateTime) {
        this.useTestDoubleForDateTime = useTestDoubleForDateTime;
    }

    public boolean isUseTestDoubleForRunner() {
        return useTestDoubleForRunner;
    }

    public void setUseTestDoubleForRunner(boolean useTestDoubleForRunner) {
        this.useTestDoubleForRunner = useTestDoubleForRunner;
    }

    public String getValueTestDoubleForDateTime() {
        return valueTestDoubleForDateTime;
    }

    public void setValueTestDoubleForDateTime(String valueTestDoubleForDateTime) {
        this.valueTestDoubleForDateTime = valueTestDoubleForDateTime;
    }

    public String getValueTestDoubleForRunner() {
        return valueTestDoubleForRunner;
    }

    public void setValueTestDoubleForRunner(String valueTestDoubleForRunner) {
        this.valueTestDoubleForRunner = valueTestDoubleForRunner;
    }

    public boolean isFormNotCloseOnExit() {
        return formNotCloseOnExit;
    }

    public void setFormNotCloseOnExit(boolean formNotCloseOnExit) {
        this.formNotCloseOnExit = formNotCloseOnExit;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getDatabaseJDBCDriver() {
        return databaseJDBCDriver;
    }

    public void setDatabaseJDBCDriver(String databaseJDBCDriver) {
        this.databaseJDBCDriver = databaseJDBCDriver;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public String getDatabaseUserID() {
        return databaseUserID;
    }

    public void setDatabaseUserID(String databaseUserID) {
        this.databaseUserID = databaseUserID;
    }

    public void save(String filename) throws IOException {
        Properties props = new Properties();
        props.setProperty("rootFilePath", rootFilePath);
        props.setProperty("useTestDoubleForDateTime", String.valueOf(useTestDoubleForDateTime));
        props.setProperty("useTestDoubleForRunner", String.valueOf(useTestDoubleForRunner));
        props.setProperty("valueTestDoubleForDateTime", valueTestDoubleForDateTime);
        props.setProperty("valueTestDoubleForRunner", valueTestDoubleForRunner);
        props.setProperty("formNotCloseOnExit", String.valueOf(formNotCloseOnExit));
        props.setProperty("databaseURL", databaseURL);
        props.setProperty("databaseJDBCDriver", databaseJDBCDriver);
        props.setProperty("databasePassword", databasePassword);
        props.setProperty("databaseUserID", databaseUserID);

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            props.store(fos, "Test Recorder Configuration");
        }
    }

    public void load(String filename) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filename)) {
            props.load(fis);
        }

        this.rootFilePath = props.getProperty("rootFilePath", this.rootFilePath);
        this.useTestDoubleForDateTime = Boolean.parseBoolean(props.getProperty("useTestDoubleForDateTime", "false"));
        this.useTestDoubleForRunner = Boolean.parseBoolean(props.getProperty("useTestDoubleForRunner", "false"));
        this.valueTestDoubleForDateTime = props.getProperty("valueTestDoubleForDateTime", this.valueTestDoubleForDateTime);
        this.valueTestDoubleForRunner = props.getProperty("valueTestDoubleForRunner", this.valueTestDoubleForRunner);
        this.formNotCloseOnExit = Boolean.parseBoolean(props.getProperty("formNotCloseOnExit", "false"));
        this.databaseURL = props.getProperty("databaseURL", this.databaseURL);
        this.databaseJDBCDriver = props.getProperty("databaseJDBCDriver", this.databaseJDBCDriver);
        this.databasePassword = props.getProperty("databasePassword", this.databasePassword);
        this.databaseUserID = props.getProperty("databaseUserID", this.databaseUserID);
    }
}
