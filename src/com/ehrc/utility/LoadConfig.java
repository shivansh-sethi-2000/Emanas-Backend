package com.ehrc.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.log4j.PropertyConfigurator;

public class LoadConfig {
	static Logger logger = LoggerFactory.getLogger(LoadConfig.class);
	
	public  String CONFIG_PATH = null;

	String username;
	static Properties prop = null;
	
	public static String getConfigValue(String key)
	{
		return prop.getProperty(key);
		
	}
	
	public static void setProperties(Properties prop1)
	{
		prop = prop1;
	}
	
	public static void editPropertyFile(String propertyName, String propertyValue) throws Exception {
		FileInputStream input = new FileInputStream("/Users/shivanshsethi/git/eManas_Backend/Backend/backend_config/config/credentials.properties");
		Properties properties = new Properties();
		properties.load(input);
		properties.setProperty(propertyName, propertyValue);
		properties.store(new FileOutputStream("/Users/shivanshsethi/git/eManas_Backend/Backend/backend_config/config/credentials.properties"), null);
		setProperties(properties);
	}
	
	
	public LoadConfig()
	{
		
		
		
	CONFIG_PATH = System.getProperty("org.iiitbehrc.hello");
//		CONFIG_PATH = "/home/ubuntu/covid_bmr_config/config";	
		//PropertyConfigurator.configure(CONFIG_PATH+"/"+"log4j"+strEnv+".properties");
		logger.info("config path.... :- "+CONFIG_PATH);
		Properties prop = new Properties();
		InputStream input = null;

		
		try {

			input = new FileInputStream(CONFIG_PATH+"/"+"credentials.properties");

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			LoadConfig.setProperties(prop);
		} catch (IOException ex) {
			logger.error("",ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("",e);
				}
			}
		}
	}
	


}
