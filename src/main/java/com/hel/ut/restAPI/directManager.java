/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.restAPI;

import com.hel.ut.dao.RestAPIDAO;
import com.hel.ut.dao.transactionInDAO;
import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchUploads;
import com.hel.ut.model.batchdownloadactivity;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.directmessagesin;
import com.hel.ut.model.directmessagesout;
import com.hel.ut.model.hisps;
import com.hel.ut.model.mailMessage;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.service.userManager;
import com.hel.ut.service.zipFileManager;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.hel.ut.service.utilManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author chadmccue
 */
@Service
public class directManager {

    @Autowired
    private transactionOutDAO transactionOutDAO;
    
    @Autowired
    private transactionInDAO transactionInDAO;

    @Autowired
    private transactionOutManager transactionoutmanager;

    @Autowired
    private transactionInManager transactionInManager;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    @Autowired
    private zipFileManager zipFileManager;

    @Autowired
    private RestAPIDAO RestAPIDAO;

    @Autowired
    private utConfigurationManager configurationmanager;

    @Autowired
    private emailMessageManager emailManager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private userManager usermanager;
    
    @Autowired
    private utilManager utilmanager;

    @Resource(name = "myProps")
    private Properties myProps;
    
    public void sendmedAlliesTestMessage() throws Exception {
	
	final ClientConfig config = new DefaultClientConfig();
	
	TrustManager[] trustManager = new X509TrustManager[] { new X509TrustManager() {

	    @Override
	    public X509Certificate[] getAcceptedIssuers() {
		return null;
	    }

	    @Override
	    public void checkClientTrusted(X509Certificate[] certs, String authType) {

	    }

	    @Override
	    public void checkServerTrusted(X509Certificate[] certs, String authType) {

	    }
	}};
        
        /* Bearer Token */
        HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
	SSLContext ctx = SSLContext.getInstance("SSL");
	ctx.init(null, trustManager, null);
	config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hostnameVerifier, ctx));
        
        //Need to get the bearer access token
        final Client oAuthClient = Client.create(config);
        WebResource webResource = oAuthClient.resource("https://medalliesdirect.okta.com/oauth2/direct/v1/token");
        
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("grant_type", "client_credentials"); 
        queryParams.add("scope", "messaging.api"); 
        
        String encodedData = DatatypeConverter.printBase64Binary(("0oa5jls6uoTtVVf9h4h7" + ":" + "8LL5adcA99GgjN80n0SkdBdRVvrqLYFEKfzC2DGwji9ZN4RKyuoTAk3L4IE2f7NC").getBytes("UTF-8"));
        
        ClientResponse response = webResource.queryParams(queryParams)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Authorization", "Basic "+ encodedData)
        .post(ClientResponse.class);
        
        String accessToken = "";
        
        try {
            StringBuilder apiResponse = new StringBuilder();
	    apiResponse.append("Status: ").append(response.getStatus()).append(System.getProperty("line.separator"));
	    apiResponse.append("Response: ").append(System.getProperty("line.separator"));
	    apiResponse.append(response.getEntity(String.class)); 
            response.close();
	    oAuthClient.destroy();
            
            System.out.println(apiResponse);
            accessToken = apiResponse.substring(apiResponse.indexOf("access_token")+15, apiResponse.length());
            accessToken = accessToken.substring(0, accessToken.indexOf("\""));
	} 
	catch (ClientHandlerException | UniformInterfaceException ex) {
	    System.out.println("Error");
	    System.out.println(ex.getMessage());
	    oAuthClient.destroy();
	}
        
        if(!"".equals(accessToken)) {
            
            UUID uuid = UUID.randomUUID();
	
            UUID fileTitle = UUID.randomUUID();

            String attachmentContent = DatatypeConverter.printBase64Binary(("Test Content").getBytes("UTF-8"));
        

            JSONArray emailAttachmentListArray = new JSONArray();
            JSONObject emailAttachmentListObject = new JSONObject();
            emailAttachmentListObject.put("attachmentClass","text/xml");
            emailAttachmentListObject.put("attachmentContent", attachmentContent);
            emailAttachmentListObject.put("attachmentFileName",fileTitle.toString());

            emailAttachmentListArray.add(emailAttachmentListObject);

            JSONObject jsonObjectToSend = new JSONObject();
            jsonObjectToSend.put("emailAttachmentList", emailAttachmentListArray);
            jsonObjectToSend.put("toDirectAddress","L9093@direct.medalliesdirect.net");
            jsonObjectToSend.put("fromDirectAddress","healthelinktest@hl.medalliesdirect.org");
            jsonObjectToSend.put("messageId","urn:uuid:"+uuid);
            
            final Client client = Client.create(config);

            client.setConnectTimeout(120000);
            client.setReadTimeout(120000);

            String prodConnection = "https://api.medallies.com/messaging/dirmsg/send_ccda";
            String testConnection = "https://api.val.medallies.com/messaging/dirmsg/send_ccda";

            webResource = client.resource(testConnection);

            try {
                response = webResource.type(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer " + accessToken).post(ClientResponse.class, jsonObjectToSend.toString().replace("\\/", "/"));

                StringBuilder apiResponse = new StringBuilder();
                apiResponse.append("Status: ").append(response.getStatus()).append(System.getProperty("line.separator"));
                apiResponse.append("Response: ").append(System.getProperty("line.separator"));
                apiResponse.append(response.getEntity(String.class)); 
                
                response.close();
                client.destroy();
            } 
            catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Error");
                System.out.println(ex.getMessage());
                client.destroy();
            }
        }
    }
    
    /**
     * 
     * @param batchDownloadId
     * @param transportDetails
     * @param hispDetails
     * @param patientId
     * @param patientDOB
     * @param patientFirstname
     * @param patientLastname
     * @throws Exception 
     */
    @Async
    public void senddirectOutmedallies(Integer batchDownloadId, configurationTransport transportDetails, hisps hispDetails, String patientId,
	    String patientDOB, String patientFirstname, String patientLastname) throws Exception {
	
	try {
	    boolean clearRecords = false;
	    
	    Integer batchStatusId = 0;

	    if (transportDetails != null) {
		clearRecords = transportDetails.getclearRecords();
	    }
	    
	    // get the batch download details
	    batchDownloads batchDownloadDetails = transactionoutmanager.getBatchDetails(batchDownloadId);
	    
	    //get the associated batch upload details
	    batchUploads batchUploadDetails = transactionInManager.getBatchDetails(batchDownloadDetails.getBatchUploadId());
	    
	    Integer batchUploadId = batchUploadDetails.getId();
	    
	    if(batchUploadDetails.getAssociatedBatchId() > 0) {
		
		batchdownloadactivity ba = new batchdownloadactivity();
		ba.setActivity("Found original source batch for this reply message.  Souce batch upload batchId:" + batchUploadDetails.getAssociatedBatchId());
		ba.setBatchDownloadId(batchDownloadId);
		transactionOutDAO.submitBatchActivityLog(ba);
		
		batchUploadDetails = transactionInManager.getBatchDetails(batchUploadDetails.getAssociatedBatchId());
	    }
	    
	    boolean directAddressesFound = false;
	    
	    if(batchUploadDetails.getRecipientEmail() != null && batchUploadDetails.getSenderEmail() != null) {
		if(!"".equals(batchUploadDetails.getRecipientEmail()) && !"".equals(batchUploadDetails.getSenderEmail())) {
		    directAddressesFound = true;
		}
	    }
	    
	    if(directAddressesFound) {
                
                batchdownloadactivity ba = new batchdownloadactivity();
		ba.setActivity("Found the direct address to send message back to. Recipient Direct Address: " + batchUploadDetails.getSenderEmail());
		ba.setBatchDownloadId(batchDownloadId);
		transactionOutDAO.submitBatchActivityLog(ba);
                
		String directAPIURL = hispDetails.getHispAPIURL();
		String directAPIUsername = hispDetails.getHispAPIUsername();
		String directAPIPassword = hispDetails.getHispAPIPassword();
                
                ba = new batchdownloadactivity();
		ba.setActivity("Found the HISP API URL: " + hispDetails.getHispAPIURL());
		ba.setBatchDownloadId(batchDownloadId);
		transactionOutDAO.submitBatchActivityLog(ba);

		String fileName = null;

                int findExt = batchDownloadDetails.getOutputFileName().lastIndexOf(".");

                if (findExt >= 0) {
                    fileName = batchDownloadDetails.getOutputFileName();
                } else {
                    fileName = new StringBuilder().append(batchDownloadDetails.getOutputFileName()).append(".").append(transportDetails.getfileExt()).toString();
                }
                
                ba = new batchdownloadactivity();
		ba.setActivity("Output file name found, File Name: " + fileName);
		ba.setBatchDownloadId(batchDownloadId);
		transactionOutDAO.submitBatchActivityLog(ba);
	    
                //Submit the restAPImessageOut
                directmessagesout directMessageOut = new directmessagesout();
                directMessageOut.setConfigId(transportDetails.getconfigId());
                directMessageOut.setBatchDownloadId(batchDownloadId);
                directMessageOut.setBatchUploadId(batchDownloadDetails.getBatchUploadId());
                directMessageOut.setOrgId(batchDownloadDetails.getOrgId());
                directMessageOut.setOutputFileName(batchDownloadDetails.getOutputFileName());
                directMessageOut.setFromDirectAddress(batchUploadDetails.getRecipientEmail());
                directMessageOut.setToDirectAddress(batchUploadDetails.getSenderEmail());
                directMessageOut.setHispId(hispDetails.getId());

                String filelocation = transportDetails.getfileLocation().trim();

                File file = new File(myProps.getProperty("ut.directory.utRootDir") + transportDetails.getfileLocation().trim() + fileName);

                String responseMessage = "";
	    
                if (file.exists()) {
                    
                    ba = new batchdownloadactivity();
                    ba.setActivity("Output file found, File Name: " + file.getAbsolutePath());
                    ba.setBatchDownloadId(batchDownloadId);
                    transactionOutDAO.submitBatchActivityLog(ba);

                    final ClientConfig config = new DefaultClientConfig();

                    TrustManager[] trustManager = new X509TrustManager[] { new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {

                        }
                    }};

                    /* Bearer Token */
                    HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                    SSLContext ctx = SSLContext.getInstance("SSL");
                    ctx.init(null, trustManager, null);
                    config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hostnameVerifier, ctx));

                    //Need to get the bearer access token
                    final Client oAuthClient = Client.create(config);
                    WebResource webResource = oAuthClient.resource("https://medalliesdirect.okta.com/oauth2/direct/v1/token");

                    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
                    queryParams.add("grant_type", "client_credentials"); 
                    queryParams.add("scope", "messaging.api"); 

                    String encodedData = DatatypeConverter.printBase64Binary((hispDetails.getHispAPIUsername() + ":" + hispDetails.getHispAPIPassword()).getBytes("UTF-8"));

                    ClientResponse response = webResource.queryParams(queryParams)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic "+ encodedData)
                    .post(ClientResponse.class);
                    
                    ba = new batchdownloadactivity();
                    ba.setActivity("Encoded Data: " + encodedData);
                    ba.setBatchDownloadId(batchDownloadId);
                    transactionOutDAO.submitBatchActivityLog(ba);

                    String accessToken = "";

                    try {
                        StringBuilder apiResponse = new StringBuilder();
                        apiResponse.append("Status: ").append(response.getStatus()).append(System.getProperty("line.separator"));
                        apiResponse.append("Response: ").append(System.getProperty("line.separator"));
                        apiResponse.append(response.getEntity(String.class)); 

                        response.close();
                        oAuthClient.destroy();
                        
                        ba = new batchdownloadactivity();
                        ba.setActivity("MedAllies API Response Status: " + response.getStatus());
                        ba.setBatchDownloadId(batchDownloadId);
                        transactionOutDAO.submitBatchActivityLog(ba);

                        if(response.getStatus() != 200) {
                            accessToken = "";
                            
                            ba = new batchdownloadactivity();
                            ba.setActivity("MedAllies API Response: " + apiResponse);
                            ba.setBatchDownloadId(batchDownloadId);
                            transactionOutDAO.submitBatchActivityLog(ba);
                        }
                        else {
                            accessToken = apiResponse.substring(apiResponse.indexOf("access_token")+15, apiResponse.length());
                            accessToken = accessToken.substring(0, accessToken.indexOf("\""));
                        }
                    } 
                    catch (ClientHandlerException | UniformInterfaceException ex) {
                        response.close();
                        oAuthClient.destroy();

                        mailMessage mail = new mailMessage();
                        mail.setfromEmailAddress("notifications@health-e-link.net");

                        List<String> ccAddresses = new ArrayList<>();

                        String[] emails = transportDetails.getErrorEmailAddresses().trim().split(",");
                        List<String> emailAddressList = Arrays.asList(emails);

                        if(!emailAddressList.isEmpty()) {
                            mail.settoEmailAddress(emailAddressList.get(0).trim());
                            if(emailAddressList.size() > 1) {
                                for(Integer i = 1; i < emailAddressList.size(); i++) {
                                    if(!"".equals(emailAddressList.get(i).trim())) {
                                        ccAddresses.add(emailAddressList.get(i).trim());
                                    }
                                }
                            }
                        }

                        List<String> bccAddresses = new ArrayList<>();
                        bccAddresses.add("cmccue@health-e-link.net");

                        //build message
                        String message = "The following error occurred while authenticating with MedAllies. <br /><br />"+ ex.getMessage();
                        mail.setmessageBody(message);
                        mail.setmessageSubject("MedAllies Authentication Error");

                        if (!ccAddresses.isEmpty()) {
                            String[] ccEmailAddresses = new String[ccAddresses.size()];
                            ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
                            mail.setccEmailAddress(ccEmailAddresses);
                        }

                        if (!bccAddresses.isEmpty()) {
                            String[] bccEmailAddresses = new String[bccAddresses.size()];
                            bccEmailAddresses = ccAddresses.toArray(bccEmailAddresses);
                            mail.setBccEmailAddress(bccEmailAddresses);
                        }

                        emailManager.sendEmail(mail);
                    }

                    if(!"".equals(accessToken)) {
                        
                        InputStream fileInput = new FileInputStream(file);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInput));

                        String line;
                        StringBuilder jsonContent = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            jsonContent.append(line);
                        }

                        String jsonContentAsString = jsonContent.toString().replace("\\","\\\\");
                        String encodedContent = utilmanager.encodeStringToBase64Binary(jsonContentAsString);

                        JSONObject jsonObjectToSend = new JSONObject();

                        JSONArray emailAttachmentListArray = new JSONArray();
                        JSONObject emailAttachmentListObject = new JSONObject();
                        emailAttachmentListObject.put("attachmentClass","text/xml");
                        emailAttachmentListObject.put("attachmentContent", encodedContent);
                        emailAttachmentListObject.put("attachmentFileName",batchDownloadDetails.getUtBatchName()+".xml");

                        emailAttachmentListArray.add(emailAttachmentListObject);

                        jsonObjectToSend.put("emailAttachmentList", emailAttachmentListArray);

                        UUID uuid = UUID.randomUUID();
                        
                        jsonObjectToSend.put("asmEnabled",true);
                        jsonObjectToSend.put("fromDirectAddress",batchUploadDetails.getRecipientEmail());
                        jsonObjectToSend.put("messageId","urn:uuid:"+uuid);
                        jsonObjectToSend.put("toDirectAddress",batchUploadDetails.getSenderEmail());

                        if(patientFirstname != null && patientLastname != null && patientId != null && patientDOB != null) {
                            jsonObjectToSend.put("messageSubject","Patient: " + patientFirstname + " " + patientLastname + "; DOB: " + patientDOB);
                            jsonObjectToSend.put("patientId",patientId);
                            jsonObjectToSend.put("patientDOB",patientDOB);
                            jsonObjectToSend.put("patientFirstname",patientFirstname);
                            jsonObjectToSend.put("patientLastname",patientLastname);
                        }

                        final Client client = Client.create(config);

                        client.setConnectTimeout(120000);
                        client.setReadTimeout(120000);
                        webResource = client.resource(hispDetails.getHispAPIURL());

                        try {
                            //Save the sending JSON into the archives folder
                            String sentJSONFileName = batchDownloadDetails.getUtBatchName()+".json";
                            File sentJSONFile = new File(myProps.getProperty("ut.directory.utRootDir") + "medAlliesArchives/" + sentJSONFileName);
                            FileWriter writer = new FileWriter(sentJSONFile);
                            writer.write(jsonObjectToSend.toString().replace("\\/", "/"));
                            writer.close();

                            response = webResource.type("application/json").header("Authorization", "Bearer " + accessToken).post(ClientResponse.class, jsonObjectToSend.toString().replace("\\/", "/"));
                            directMessageOut.setResponseStatus(response.getStatus());

                            StringBuilder apiResponse = new StringBuilder();
                            apiResponse.append("Status: ").append(response.getStatus()).append(System.getProperty("line.separator"));
                            apiResponse.append("Response: ").append(System.getProperty("line.separator"));
                            apiResponse.append(response.getEntity(String.class)); 

                            responseMessage = apiResponse.toString();

                            directMessageOut.setResponseMessage(responseMessage);

                            jsonContentAsString = "";
                            
                            ba = new batchdownloadactivity();
                            ba.setActivity("MedAllies send message API Response Status: " + response.getStatus());
                            ba.setBatchDownloadId(batchDownloadId);
                            transactionOutDAO.submitBatchActivityLog(ba);

                            ba = new batchdownloadactivity();
                            ba.setActivity("MedAllies end message API Response: " + apiResponse);
                            ba.setBatchDownloadId(batchDownloadId);
                            transactionOutDAO.submitBatchActivityLog(ba);

                            if (response.getStatus() == 200) {
                                if (transportDetails.isWaitForResponse()) {
                                    batchStatusId = 59;
                                } 
                                else {
                                    batchStatusId = 28;
                                }
                                directMessageOut.setStatusId(2);
                            } 
                            else {
                                batchStatusId = 58;
                                directMessageOut.setStatusId(3);
                            }

                            response.close();
                            client.destroy();
                        } 
                        catch (ClientHandlerException | UniformInterfaceException ex) {
                            batchStatusId = 58;
                            directMessageOut.setStatusId(3);
                            responseMessage = ex.getMessage();
                            directMessageOut.setResponseMessage(responseMessage);

                            response.close();
                            client.destroy();
                        }
                    }
                    else {
                        batchStatusId = 58;
                        
                        directMessageOut.setResponseStatus(0);
                        directMessageOut.setStatusId(3);
                        directMessageOut.setResponseMessage("Could not connect to medAllies API to get the access token. Souce batch upload batchId:" + batchUploadDetails.getId());
		
                        ba = new batchdownloadactivity();
                        ba.setActivity("Could not connect to medAllies API to get the access token. Souce batch upload batchId:" + batchUploadDetails.getId());
                        ba.setBatchDownloadId(batchDownloadId);
                        transactionOutDAO.submitBatchActivityLog(ba);
                    }
                }
                else {
                    batchStatusId = 58;
                    directMessageOut.setResponseStatus(0);
                    directMessageOut.setStatusId(3);
                    directMessageOut.setResponseMessage("No File Sent because file (" + myProps.getProperty("ut.directory.utRootDir") + filelocation + fileName + ") was not Found");
                }

                transactionoutmanager.updateTargetBatchStatus(batchDownloadId, batchStatusId, "endDateTime");
                transactionOutDAO.insertDMMessage(directMessageOut);

                if(batchStatusId == 28) {
                    //Delete all transaction target tables
                    transactionInManager.deleteBatchTransactionTables(batchUploadId);
                    transactionOutDAO.deleteBatchDownloadTables(batchDownloadId);

                    //Send out email notification
                    if(transportDetails.getErrorEmailAddresses() != null) {
                        if(!"".equals(transportDetails.getErrorEmailAddresses().trim())) {
                            mailMessage mail = new mailMessage();
                            mail.setfromEmailAddress("notifications@health-e-link.net");

                            List<String> ccAddresses = new ArrayList<>();

                            String[] emails = transportDetails.getErrorEmailAddresses().trim().split(",");
                            List<String> emailAddressList = Arrays.asList(emails);

                            if(!emailAddressList.isEmpty()) {
                                mail.settoEmailAddress(emailAddressList.get(0).trim());
                                if(emailAddressList.size() > 1) {
                                    for(Integer i = 1; i < emailAddressList.size(); i++) {
                                        if(!"".equals(emailAddressList.get(i).trim())) {
                                            ccAddresses.add(emailAddressList.get(i).trim());
                                        }
                                    }
                                }
                            }

                            List<String> bccAddresses = new ArrayList<>();
                            bccAddresses.add("cmccue@health-e-link.net");

                            utConfiguration configDetails = configurationmanager.getConfigurationById(transportDetails.getconfigId());

                            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                            Date date = new Date();

                            //build message
                            String message = "A Community eConnect feedback report was sent to your organization on " + dateFormat.format(date) + " via direct messaging for feedback report configuration " + configDetails.getconfigName().trim() + ".";
                            mail.setmessageBody(message);
                            mail.setmessageSubject("New Community eConnect feedback report");

                            if (!ccAddresses.isEmpty()) {
                                String[] ccEmailAddresses = new String[ccAddresses.size()];
                                ccEmailAddresses = ccAddresses.toArray(ccEmailAddresses);
                                mail.setccEmailAddress(ccEmailAddresses);
                            }

                            if (!bccAddresses.isEmpty()) {
                                String[] bccEmailAddresses = new String[bccAddresses.size()];
                                bccEmailAddresses = ccAddresses.toArray(bccEmailAddresses);
                                mail.setBccEmailAddress(bccEmailAddresses);
                            }

                            emailManager.sendEmail(mail);
                        }
                    }
                }
            }
	    else {
		batchStatusId = 58;
		
		batchdownloadactivity ba = new batchdownloadactivity();
		ba.setActivity("No direct FROM or TO addresses were found  for the original source message. Souce batch upload batchId:" + batchUploadDetails.getId());
		ba.setBatchDownloadId(batchDownloadId);
		transactionOutDAO.submitBatchActivityLog(ba);
		
		ba = new batchdownloadactivity();
		ba.setActivity("Outbound batchId: " + batchDownloadId + " status was set to 58.");
		ba.setBatchDownloadId(batchDownloadId);
		transactionOutDAO.submitBatchActivityLog(ba);
		
		transactionoutmanager.updateTargetBatchStatus(batchDownloadId, batchStatusId, "endDateTime");
		
		//Delete all transaction target tables
		transactionInManager.deleteBatchTransactionTables(batchUploadId);
		transactionOutDAO.deleteBatchDownloadTables(batchDownloadId);
	    }
	}
	catch (Exception e) {
	    throw new Exception("Error occurred trying to send out a direct message to MedAllies. batch Download Id: " + batchDownloadId, e);
	}
    }
   
    public List<directmessagesin> getDirectMessagesInListPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	return transactionInDAO.getDirectMessagesInListPaged(fromDate,toDate,displayStart,displayRecords,searchTerm,sortColumnName,sortDirection);
    }

    public List<directmessagesout> getDirectMessagesOutListPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	return transactionOutDAO.getDirectMessagesOutListPaged(fromDate,toDate,displayStart,displayRecords,searchTerm,sortColumnName,sortDirection);
    }
}