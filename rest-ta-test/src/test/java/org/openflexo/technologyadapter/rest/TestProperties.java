package org.openflexo.technologyadapter.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TestProperties {

	public static void main(String[] args) {
		/*File providerFile = (File) getIODelegate().getSerializationArtefact();
		Properties properties = new Properties();
		try (FileInputStream input = new FileInputStream(providerFile)) {
			properties.load(input);
			adaptor = new FlexoOslcAdaptorConfiguration(properties.getProperty("catalogUri"));
			adaptor.setAccessToken(properties.getProperty("accessToken"));
			adaptor.setRequestTokenUrl(properties.getProperty("requestTokenUrl"));
			adaptor.setAuthorizationUrl(properties.getProperty("authorizationUrl"));
			adaptor.setConsumerKey(properties.getProperty("consumerKey"));
			adaptor.setConsumerSecret(properties.getProperty("consumerSecret"));
			adaptor.setAuthURL(properties.getProperty("authURL"));
			adaptor.setPassword(properties.getProperty("password"));
			adaptor.setLogin(properties.getProperty("login"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}*/

		Properties properties = new Properties();
		try {
			File tempFile = File.createTempFile("prout", "tutu");
			System.out.println("tempFile:" + tempFile);
			properties.setProperty("tutu", "zou");
			properties.setProperty("value", "42");
			FileOutputStream os = new FileOutputStream(tempFile);
			properties.store(os, "Le commentaire qui va bien");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
