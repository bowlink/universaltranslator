/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import com.hel.ut.model.Organization;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.utUserActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author chadmccue
 */
@Service
public class fixedLengthFiletoTxt {

    @Autowired
    private organizationManager organizationmanager;
    
    @Autowired
    private userManager usermanager;
    
    @Autowired
    private utConfigurationManager configurationManager;
    
    @Resource(name = "myProps")
    private Properties myProps;

    public String translateFixedLengthFileToTxt(String fileLocation, String actualFileName, batchUploads batch) throws Exception {
	
	Organization orgDetails = organizationmanager.getOrganizationById(batch.getOrgId());
	
	String directory = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/templates/";
	
	String templatefileName;
	templatefileName = null;
	
	String newfileName;
	newfileName = null;
	
	if(batch.getConfigId() > 0) {
	    configurationMessageSpecs configurationParsingScript = configurationManager.getMessageSpecs(batch.getConfigId());
	    
	    if(configurationParsingScript.getParsingTemplate()!= null) {
		if(!"".equals(configurationParsingScript.getParsingTemplate())) {
		    templatefileName = configurationParsingScript.getParsingTemplate();
		}
		else {
		    templatefileName = orgDetails.getparsingTemplate();
		}
	    }
	    else {
		templatefileName = orgDetails.getparsingTemplate();
	    }
	}
	else {
	    templatefileName = orgDetails.getparsingTemplate();
	}
	
	if(templatefileName != null) {
	    
	    //log batch activity
	    utUserActivity ua = new utUserActivity();
	    ua.setUserId(0);
	    ua.setFeatureId(0);
	    ua.setAccessMethod("System");
	    ua.setActivity("Fixed length file parsing template found: " + directory + templatefileName);
	    ua.setBatchUploadId(batch.getId());
	    usermanager.insertUserLog(ua);
	    
	    URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file://" + directory + templatefileName)});
	    
	    Class cls = null;
	    
	    // Remove the .class extension
	    try {
		cls = loader.loadClass(templatefileName.substring(0, templatefileName.lastIndexOf('.')));
	    }
	    catch (ClassNotFoundException ex) {
		//log batch activity
		ua = new utUserActivity();
		ua.setUserId(0);
		ua.setFeatureId(0);
		ua.setAccessMethod("System");
		ua.setActivity("Error loading fixed length file parsing template: " + directory + templatefileName + " Error: " + ex.getMessage());
		ua.setBatchUploadId(batch.getId());
		usermanager.insertUserLog(ua);
	    }
	    
	    if(cls != null) {
		Constructor constructor = cls.getConstructor();
	    
		Object CCDObj = constructor.newInstance();

		Method myMethod = cls.getMethod("fixedLengthFileToTxt", new Class[]{String.class});

		//Get the submitted file
		directory = fileLocation;

		File newFile = new File(directory + actualFileName + "-parsed.txt");

		if (newFile.exists()) {
		    try {
			int i = 0;
			while (newFile.exists()) {
			    int iDot = newFile.getName().lastIndexOf(".");
			    newFile = new File(directory + newFile.getName().substring(0, iDot) + "-" + i++ + "" + newFile.getName().substring(iDot));
			}
			newfileName = newFile.getName();
			newFile.createNewFile();
			
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		} 
		else {
		    newFile.createNewFile();
		    newfileName = newFile.getName();
		}

		try {
		    FileWriter fw = new FileWriter(newFile, true);

		    String fileRecords = (String) myMethod.invoke(CCDObj, new Object[]{fileLocation + actualFileName + ".txt"});
		    if (fileRecords.equalsIgnoreCase("")) {
			newfileName = "FILE IS NOT TXT ERROR";
		    }

		    fw.write(fileRecords);
		    fw.close();
		} 
		catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
		    newfileName = "ERRORERRORERROR";
		    PrintStream ps = new PrintStream(newFile);
		    ex.printStackTrace(ps);
		    ps.close();

		    //log batch activity
		    ua = new utUserActivity();
		    ua.setUserId(0);
		    ua.setFeatureId(0);
		    ua.setAccessMethod("System");
		    ua.setActivity("Error using fixed length parsing Template. Error: " + ex.getMessage());
		    ua.setBatchUploadId(batch.getId());
		    usermanager.insertUserLog(ua);
		}
	    }
	}
	else {
	    newfileName = "ERRORERRORERROR";
	    
	    utUserActivity ua = new utUserActivity();
	    ua.setUserId(0);
	    ua.setFeatureId(0);
	    ua.setAccessMethod("System");
	    ua.setActivity("No fixed length parsing template was set up for configId: "+batch.getConfigId());
	    ua.setBatchUploadId(batch.getId());
	    usermanager.insertUserLog(ua);
	}
        
        return newfileName;
    }
}