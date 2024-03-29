package com.hel.ut.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.hel.ut.dao.messageTypeDAO;
import com.hel.ut.dao.organizationDAO;
import com.hel.ut.dao.utConfigurationDAO;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.Organization;
import com.hel.ut.model.validationType;
import com.hel.ut.reference.fileSystem;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;

@Service
public class messageTypeManagerImpl implements messageTypeManager {

    @Autowired
    private messageTypeDAO messageTypeDAO;

    @Autowired
    private organizationDAO organizationDAO;
    
    @Autowired
    private utConfigurationDAO utConfigurationDAO;
    
    @Resource(name = "myProps")
    private Properties myProps;
    
    @Override
    public double findTotalCrosswalks(int orgId) {
        return messageTypeDAO.findTotalCrosswalks(orgId);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getInformationTables() {
        return messageTypeDAO.getInformationTables();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getAllTables() {
        return messageTypeDAO.getAllTables();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getTableColumns(String tableName) {
        return messageTypeDAO.getTableColumns(tableName);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getValidationTypes() {
        return messageTypeDAO.getValidationTypes();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String getValidationById(int id) {
        return messageTypeDAO.getValidationById(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getDelimiters() {
        return messageTypeDAO.getDelimiters();
    }

    @Override
    public Long getTotalFields(int messageTypeId) {
        return messageTypeDAO.getTotalFields(messageTypeId);
    }

    @Override
    public List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId) {
        return messageTypeDAO.getCrosswalks(page, maxResults, orgId);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getCrosswalkData(int cwId) {
        return messageTypeDAO.getCrosswalkData(cwId);
    }

    @Override
    public String getFieldName(int fieldId) {
        return messageTypeDAO.getFieldName(fieldId);
    }

    @Override
    public String getCrosswalkName(int cwId) {
        return messageTypeDAO.getCrosswalkName(cwId);
    }

    @Override
    public Long checkCrosswalkName(String name, int orgId) {
        return messageTypeDAO.checkCrosswalkName(name, orgId);
    }

    @Override
    public Integer createCrosswalk(Crosswalks crosswalkDetails) throws Exception {
        Integer lastId = null;
        String cleanURL = null;
	
	if(crosswalkDetails.getFile() != null) {
	    MultipartFile file = crosswalkDetails.getFile();
	    String fileName = file.getOriginalFilename();

	    InputStream inputStream = null;
	    OutputStream outputStream = null;
	    String directory;

	    if (crosswalkDetails.getOrgId() > 0) {
		Organization orgDetails = organizationDAO.getOrganizationById(crosswalkDetails.getOrgId());
		cleanURL = orgDetails.getcleanURL();
		directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/crosswalks/";
	    } else {
		//Set the directory to save the uploaded message type template to
		 directory = myProps.getProperty("ut.directory.utRootDir") + "libraryFiles/crosswalks/";
	    }

	    File newFile = null;
	    newFile = new File(directory + fileName);
	    
	    try {
		if (!newFile.exists()) {
		    newFile.createNewFile();
		}

		inputStream = file.getInputStream();
		outputStream = new FileOutputStream(newFile);
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) {
		    outputStream.write(bytes, 0, read);
		}
		outputStream.close();

		//Set the filename to the original file name
		crosswalkDetails.setfileName(fileName);

	    } catch (IOException e) {
		e.printStackTrace();
		throw new Exception(e);
	    }
	    
	    String delimChar = "|";

	    if (crosswalkDetails.getFileDelimiter() > 0) {
		delimChar = messageTypeDAO.getDelimiterChar(crosswalkDetails.getFileDelimiter());
		String delimiter = messageTypeDAO.getDelimiterById(crosswalkDetails.getFileDelimiter());
		if(!"".equals(delimiter)) {
		    if("tab".equals(delimiter)) {
			delimChar = "\t";
		    }
		}
	    } 

	    //Check to make sure the file contains the selected delimiter
	    //Set the directory that holds the crosswalk files
	    fileSystem dir = new fileSystem();
	    int delimCount = (Integer) dir.checkFileDelimiter(directory, fileName, delimChar);

	    if (delimCount > 0) {
		//Submit the new message type to the database
		lastId = (Integer) messageTypeDAO.createCrosswalk(crosswalkDetails);

		//Call the function that will load the content of the crosswalk text file
		//into the rel_crosswalkData table
		loadCrosswalkContents(lastId, fileName, delimChar, cleanURL);

		return lastId;
	    } else {
		//Need to delete the file
		newFile.delete();

		//Need to return an error
		return 0;
	    }
	}
	else {
	    lastId = (Integer) messageTypeDAO.createCrosswalk(crosswalkDetails);
	    return lastId;
	}
    }
    
    @Override
    public Integer uploadNewFileForCrosswalk(Crosswalks crosswalkDetails) throws Exception {
        Integer lastId = null;
        String cleanURL = null;
	
	MultipartFile file = crosswalkDetails.getFile();
        String fileName = file.getOriginalFilename();

        InputStream inputStream = null;
        OutputStream outputStream = null;
        
	String directory;

        if (crosswalkDetails.getOrgId() > 0) {
            Organization orgDetails = organizationDAO.getOrganizationById(crosswalkDetails.getOrgId());
            cleanURL = orgDetails.getcleanURL();
	     directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/crosswalks/";
        } else {
            //Set the directory to save the uploaded message type template to
	    directory = myProps.getProperty("ut.directory.utRootDir") + "libraryFiles/crosswalks/";
        }

        File newFile = null;
        newFile = new File(directory + fileName);

        try {
            inputStream = file.getInputStream();

            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();

            //Set the filename to the original file name
            crosswalkDetails.setfileName(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
	
	String delimChar = "|";

	if (crosswalkDetails.getFileDelimiter() > 0) {
	    delimChar = messageTypeDAO.getDelimiterChar(crosswalkDetails.getFileDelimiter());
	    String delimiter = messageTypeDAO.getDelimiterById(crosswalkDetails.getFileDelimiter());
	    if(!"".equals(delimiter)) {
		if("tab".equals(delimiter)) {
		    delimChar = "\t";
		}
	    }
	} 
	
        //Check to make sure the file contains the selected delimiter
        //Set the directory that holds the crosswalk files
	fileSystem dir = new fileSystem();
        int delimCount = (Integer) dir.checkFileDelimiter(directory, fileName, delimChar);

        if (delimCount > 0) {
            //Submit the new message type to the database
            messageTypeDAO.updateCrosswalk(crosswalkDetails);

            //Call the function that will load the content of the crosswalk text file
            //into the rel_crosswalkData table
            loadCrosswalkContents(crosswalkDetails.getId(), fileName, delimChar, cleanURL);

            return crosswalkDetails.getId();
        } else {
            //Need to delete the file
            newFile.delete();

            //Need to return an error
            return 0;
        }
    }

    @Override
    public Crosswalks getCrosswalk(int cwId) {
        return messageTypeDAO.getCrosswalk(cwId);
    }

    /**
     * The 'loadCrosswalkContents' will take the contents of the uploaded text template file and populate the rel_crosswalkData table.
     *
     * @param id id: value of the latest added crosswalk
     * @param fileName	fileName: file name of the uploaded text file.
     * @param delim	delim: the delimiter used in the file
     * @param cleanURL
     * @throws java.lang.Exception
     *
     */
    public void loadCrosswalkContents(int id, String fileName, String delim, String cleanURL) throws Exception {

        //Set the directory that holds the crosswalk files
	String directory;

        if (cleanURL == null) {
	    directory = myProps.getProperty("ut.directory.utRootDir") + "libraryFiles/crosswalks/";
        } else {
	    directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/crosswalks/";
        }
	
	executeSQLStatement("delete from rel_crosswalkData where crosswalkId = "+id);
	
	String sql = ("LOAD DATA LOCAL INFILE '" + directory + fileName + "' INTO TABLE rel_crosswalkData fields terminated by '" + delim + "' "
		+ " optionally ENCLOSED BY '\"' ESCAPED BY '\\b' LINES TERMINATED BY '\\n' "
		+ "(sourceValue,targetValue,descValue)  set crosswalkId = " + id + ";");

	executeSQLStatement(sql);

    }

    // this does the same thing as getValidationTypes except putting result in an object
    //TODO need to combine and test and replace getValidationTypes
    @Override
    public List<validationType> getValidationTypes1() {
        return messageTypeDAO.getValidationTypes1();
    }

    @Override
    public String getDelimiterChar(int delimId) {
        return messageTypeDAO.getDelimiterChar(delimId);
    }
    
    @Override
    public List<Crosswalks> getCrosswalksForConfig(int page, int maxCrosswalks, int orgId, int configId, boolean inUseOnly) {
	return messageTypeDAO.getCrosswalksForConfig(page, maxCrosswalks, orgId, configId, inUseOnly);
    }
    
    @Override
    public void deleteCrosswalk(Integer cwId) throws Exception {
        
	Crosswalks crosswalkDetails = getCrosswalk(cwId);

	String directory;

        if (crosswalkDetails.getOrgId() > 0) {
            Organization orgDetails = organizationDAO.getOrganizationById(crosswalkDetails.getOrgId());
            String cleanURL = orgDetails.getcleanURL();
	    directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/crosswalks/";
        } else {
            //Set the directory to save the uploaded message type template to
	     directory = myProps.getProperty("ut.directory.utRootDir") + "libraryFiles/";
        }

        File newFile = null;
        newFile = new File(directory + crosswalkDetails.getfileName());

        try {
            if (newFile.exists()) {
		newFile.delete();
		executeSQLStatement("delete from rel_crosswalkData where crosswalkId = "+cwId);
		executeSQLStatement("delete from crosswalks where Id = "+cwId);
            }
            else {
                executeSQLStatement("delete from rel_crosswalkData where crosswalkId = "+cwId);
		executeSQLStatement("delete from crosswalks where Id = "+cwId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    
    @Override
    public List getConfigCrosswalksWithData(Integer orgId, Integer configId) {
	return getConfigCrosswalksWithData(orgId,configId);
    }
    
    @Override
    public void saveCrosswalkData(CrosswalkData cwData) {
	messageTypeDAO.saveCrosswalkData(cwData);
    }
    
    @Override
    public void executeSQLStatement(String sqlStatement) {
        messageTypeDAO.executeSQLStatement(sqlStatement);
    }
    
    @Override
    public Crosswalks getCrosswalkByNameAndOrg(String cwName, Integer orgId, String fileName) {
	return messageTypeDAO.getCrosswalkByNameAndOrg(cwName,orgId,fileName);
    }
    
    @Override
    public void moveCWForConfigToNewOrg(Integer newOrgId, Integer currOrgId, Integer configId, String oldOrgCleanURL, String newOrgCleanURL) throws Exception {

	//Get a list of Crosswalks for the new configuration
	List<Crosswalks> existingCWs = messageTypeDAO.getCrosswalksForConfigToBeCopied(configId, newOrgId);
	
	if(existingCWs != null) {
	    if(!existingCWs.isEmpty()) {
		Integer newCWID = 0;
		for(Crosswalks cw : existingCWs) {
		    if(cw.getName().contains("_")) {
			Crosswalks newCW = new Crosswalks();
			
			String[] nameVals = cw.getName().split("_");
			String cwName = nameVals[1];
			String newCWName = configId+"_"+cwName;
		    
			newCW.setName(newCWName);
			newCW.setOrgId(newOrgId);
			newCW.setFileDelimiter(cw.getFileDelimiter());
			newCW.setfileName(cw.getfileName());
			
			newCWID = messageTypeDAO.createCrosswalk(newCW);
			
			//Get data for old CW
			List<CrosswalkData> cwData = utConfigurationDAO.getCrosswalkData(cw.getId());
			
			if(!cwData.isEmpty()) {
			    for(CrosswalkData data : cwData) {
				CrosswalkData newCWData = new CrosswalkData();
				newCWData.setCrosswalkId(newCWID);
				newCWData.setDescValue(data.getDescValue());
				newCWData.setSourceValue(data.getSourceValue());
				newCWData.setTargetValue(data.getTargetValue());
				
				messageTypeDAO.saveCrosswalkData(newCWData);
			    }
			}
			
			//Check if file exists if so copy to the new org folder
			File cwFile = new File(myProps.getProperty("ut.directory.utRootDir") + oldOrgCleanURL + "/crosswalks/" + cw.getfileName());
			File newOrgCWFile = new File(myProps.getProperty("ut.directory.utRootDir") + newOrgCleanURL + "/crosswalks/" + cw.getfileName());

			if(cwFile.exists() && !newOrgCWFile.exists()) {
			    FileUtils.copyFile(new File(myProps.getProperty("ut.directory.utRootDir") + oldOrgCleanURL + "/crosswalks/" + cw.getfileName()), new File(myProps.getProperty("ut.directory.utRootDir") + newOrgCleanURL + "/crosswalks/" + cw.getfileName()));
			}
			
			//Update the data transation CW id to the new id
			messageTypeDAO.executeSQLStatement("update configurationdatatranslations set crosswalkId = " + newCWID + " where configId = " + configId + " and crosswalkId = " + cw.getId());
			messageTypeDAO.executeSQLStatement("update configurationdatatranslations set constant1 = " + newCWID + " where configId = " + configId + " and macroId in (129,160,177,195,199,201) and constant1 = '" + cw.getId() + "'");
			messageTypeDAO.executeSQLStatement("update configurationdatatranslations set constant2 = " + newCWID + " where configId = " + configId + " and macroId in (129,160,177,195,199,201) and constant2 = '" + cw.getId() + "'");
		    }
		}
	    }
	}
    }
    
    @Override
    public String checkIfCWIsInUse(Integer crosswalkId) {
	return messageTypeDAO.checkIfCWIsInUse(crosswalkId);
    }
}
