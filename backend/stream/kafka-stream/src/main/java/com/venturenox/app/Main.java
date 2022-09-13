package com.venturenox.app;

import com.venturenox.app.Pipe;
import org.apache.log4j.PropertyConfigurator;
import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        // Configuring log4j via properties file
        Properties props_log = new Properties();
        try {
            // reading the log4j properties files
            props_log.load(new FileInputStream("/usr/app/log4j.properties"));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        // setting the properties in the log4j configurator
        PropertyConfigurator.configure(props_log);

        // Starting pipeline
        Pipe pipe = new Pipe();
        pipe.describe();
        pipe.start();

    }
}
