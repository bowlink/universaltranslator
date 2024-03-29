package com.hel.ut.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.Organization;
import com.hel.ut.model.utUser;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionReceivers;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.messageTypeManager;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.configurationconnectionfieldmappings;
import com.hel.ut.model.mailMessage;
import com.hel.ut.service.emailMessageManager;
import com.hel.ut.service.sysAdminManager;
import com.hel.ut.service.userManager;


import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.hel.ut.service.hispManager;
import com.hel.ut.service.impl.transactionInManagerImpl;

import javax.servlet.http.HttpSession;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.registryKit.registry.configurations.configurationManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/administrator/configurations/connections")

public class adminConfigConnectionController {

    @Autowired
    private utConfigurationManager utconfigurationmanager;

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private messageTypeManager messagetypemanager;

    @Autowired
    private userManager userManager;

    @Autowired
    private utConfigurationTransportManager utconfigurationTransportManager;

    @Autowired
    private sysAdminManager sysAdminManager;
    
    @Autowired
    private configurationManager registryconfigurationmanager;
    
    @Autowired
    private hispManager hispManager;
    
    @Autowired
    private emailMessageManager emailMessageManager;
    
    @Autowired
    ThreadPoolTaskExecutor executor;
    
    @Resource(name = "myProps")
    private Properties myProps;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(1024);
    }

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 20;


    /**
     * The '' function will handle displaying the utConfiguration connections screen.The function will pass the existing connection objects for the selected utConfiguration.
     *
     * @param session
     * @param authentication
     * @return 
     * @throws java.lang.Exception 
     * @Return the connection view and the following objects.
     *
     * organizations - list of available active organizations to connect to this list will not contain any currently associated organizations.
     *
     * connections - list of currently associated organizations
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView getConnections(HttpSession session,Authentication authentication) throws Exception {
	
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	
	Integer configId = 0;
	
	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connections");
        mav.addObject("id", configId);
        mav.addObject("mappings", session.getAttribute("configmappings"));
        mav.addObject("HL7", session.getAttribute("configHL7"));
        mav.addObject("CCD", session.getAttribute("configCCD"));
	mav.addObject("showAllConfigOptions",session.getAttribute("showAllConfigOptions"));

        // get a list of all connections in the sysetm
        List<configurationConnection> connections = utconfigurationmanager.getAllConnectionsSingleQuery();

        Long totalConnections = (long) 0;
	
	List<configurationConnection> currentConnections = new ArrayList<>();

        // Loop over the connections to get the utConfiguration details
        if (connections != null) {
	    
            for (configurationConnection connection : connections) {
		
		if(!connection.getSourceOrgName().toLowerCase().contains("bowlink")) {
		    
		    if (connection.getSourceTransportMethod().equals("File Upload") && connection.getSourceConfigType() == 2) {
			connection.setSourceTransportMethod("File Download");
		    } 
                    
                    if (connection.getTargetTransportMethod().equals("File Upload") && connection.getTargetConfigType() == 2) {
			connection.setTargetTransportMethod("File Download");
		    } 
		    
		    if(connection.getStatus() && ("admin".equalsIgnoreCase(userDetails.getFirstName()) || "grace".equalsIgnoreCase(userDetails.getFirstName()) || "chad".equalsIgnoreCase(userDetails.getFirstName()))) {
			connection.setAllowExport(true);
			mav.addObject("allowConnectionImport", true);
		    }
		    
		    currentConnections.add(connection);
		}
            }

            // Return the total list of connections
            totalConnections = (long) currentConnections.size();
        }

        mav.addObject("connections", currentConnections);

        // Set the variable to hold the number of completed steps for this utConfiguration
        mav.addObject("stepsCompleted", session.getAttribute("configStepsCompleted"));

        return mav;
    }

    /**
     * The '/details' function will handle displaying the create utConfiguration connection screen.
     *
     * @param id
     * @param authentication
     * @return This function will display the new connection overlay
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public ModelAndView connectionDetails(@RequestParam(value = "i", required = false) Integer id,Authentication authentication) throws Exception {
	
        utUser userDetails = userManager.getUserByUserName(authentication.getName());
	
	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connections/details");
	
	Integer connectionId = 0;
	Integer sourceOrgId = 0;
	Integer sourceConfigId = 0;
	Integer targetOrgId = 0;
	Integer targetConfigId = 0;
	
	if(id != null) {
	    configurationConnection connectionDetails = utconfigurationmanager.getConnection(id);
	    
	    sourceOrgId = utconfigurationmanager.getConfigurationById(connectionDetails.getsourceConfigId()).getorgId();
	    sourceConfigId = connectionDetails.getsourceConfigId();
	    
	    utConfiguration targetConfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
	    targetOrgId = targetConfigDetails.getorgId();
	    targetConfigId = connectionDetails.gettargetConfigId();
	    connectionId = connectionDetails.getId();
	    
	    if(connectionDetails.getStatus() && ("admin".equalsIgnoreCase(userDetails.getFirstName()) || "grace".equalsIgnoreCase(userDetails.getFirstName()) || "chad".equalsIgnoreCase(userDetails.getFirstName()))) {
		mav.addObject("allowExport", true);
	    }
	}
	
	mav.addObject("connectionId", connectionId);
	mav.addObject("sourceOrgId", sourceOrgId);
	mav.addObject("sourceConfigId", sourceConfigId);
	mav.addObject("targetOrgId", targetOrgId);
	mav.addObject("targetConfigId", targetConfigId);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizationsWithSystemName();
	
	List<Organization> sourceOrganizations = new ArrayList<>();
	List<Organization> targetOrganizations = new ArrayList<>();
	
	List<utConfiguration> sourceConfigurations = utconfigurationmanager.getAllSourceConfigurations();
	List<utConfiguration> targetConfigurations = utconfigurationmanager.getAllTargetConfigurations();
	
	boolean srcOrgFound = false;
	boolean tgtOrgFound = false;
	
	for(Organization org : organizations) {
	   srcOrgFound = false;
	   tgtOrgFound = false;
	   
	   if(!sourceConfigurations.isEmpty()) {
	       for(utConfiguration srcConfig : sourceConfigurations) {
		   if(srcConfig.getorgId() == org.getId()) {
		       if(sourceOrganizations.isEmpty()) {
			   sourceOrganizations.add(org);
		       }
		       else {
			   for(Organization srcOrg : sourceOrganizations) {
			       if(srcOrg.getId() == org.getId()) {
				   srcOrgFound = true;
			       }
			   }
			   
			   if(!srcOrgFound) {
			       sourceOrganizations.add(org);
			   }
		       }
		   }
	       } 
	       for(utConfiguration tgtConfig : targetConfigurations) {
		   if(tgtConfig.getorgId() == org.getId()) {
		       if(targetOrganizations.isEmpty()) {
			   targetOrganizations.add(org);
		       }
		       else {
			   for(Organization tgtOrg : targetOrganizations) {
			       if(tgtOrg.getId() == org.getId()) {
				   tgtOrgFound = true;
			       }
			   }
			   
			   if(!tgtOrgFound) {
			       targetOrganizations.add(org);
			   }
		       }
		   }
	       }
	   }
	}
	
	List<Organization> validSourceOrganizations = new ArrayList<>();
	for(Organization org : sourceOrganizations) {
	    
	    if(id == null) {
		if(!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase())) {
		    validSourceOrganizations.add(org);
		}
	    }
	    else {
		validSourceOrganizations.add(org);
	    }
	}
	
        mav.addObject("sourceOrganizations", validSourceOrganizations);
	
	
	List<Organization> validTargetOrganizations = new ArrayList<>();
	for(Organization org : targetOrganizations) {
	    
	    if(id == null) {
		if(!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase())) {
		    validTargetOrganizations.add(org);
		}
	    }
	    else {
		validTargetOrganizations.add(org);
	    }
	}
	
	mav.addObject("targetOrganizations", validTargetOrganizations);
	
        return mav;
    }

    /**
     * The '/editConnection' function will handle displaying the edit utConfiguration connection screen.
     *
     * @param connectionId The id of the clicked utConfiguration connection
     *
     * @return This function will display the edit connection overlay
     */
    @RequestMapping(value = "/editConnection", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView editConnectionForm(@RequestParam(value = "connectionId", required = true) int connectionId) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionDetails");

        configurationConnection connectionDetails = utconfigurationmanager.getConnection(connectionId);

        utConfiguration srcconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.getsourceConfigId());
        srcconfigDetails.setorgId(organizationmanager.getOrganizationById(srcconfigDetails.getorgId()).getId());
        connectionDetails.setsrcConfigDetails(srcconfigDetails);

        utConfiguration tgtconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
        tgtconfigDetails.setorgId(organizationmanager.getOrganizationById(tgtconfigDetails.getorgId()).getId());
        connectionDetails.settgtConfigDetails(tgtconfigDetails);

        
        mav.addObject("connectionDetails", connectionDetails);

        //Need to get a list of active organizations.
        List<Organization> organizations = organizationmanager.getAllActiveOrganizationsWithSystemName();
        
	List<Organization> sourceOrganizations = new ArrayList<>();
	List<Organization> targetOrganizations = new ArrayList<>();
	
	List<utConfiguration> sourceConfigurations = utconfigurationmanager.getAllSourceConfigurations();
	List<utConfiguration> targetConfigurations = utconfigurationmanager.getAllTargetConfigurations();
	
	for(Organization org : organizations) {
	   if(!sourceConfigurations.isEmpty()) {
	       for(utConfiguration srcConfig : sourceConfigurations) {
		   if(srcConfig.getorgId() == org.getId()) {
		       sourceOrganizations.add(org);
		   }
	       } 
	       for(utConfiguration tgtConfig : targetConfigurations) {
		   if(tgtConfig.getorgId() == org.getId()) {
		       targetOrganizations.add(org);
		   }
	       }
	   }
	}
	
        mav.addObject("sourceOrganizations", sourceOrganizations);
	mav.addObject("targetOrganizations", targetOrganizations);

        return mav;
    }

    /**
     * The '/getAvailableSendingContacts.do' function will return a list of users that are associated to the selected organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableSendingContacts.do", method = RequestMethod.GET)
    public @ResponseBody ModelAndView getAvailableSendingContacts(@RequestParam(value = "orgId", required = true) int orgId, @RequestParam(value = "connectionId", required = true) int connectionId) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionSendingContacts");

        Organization organizationDetails = organizationmanager.getOrganizationById(orgId);
	
	List<configurationConnectionSenders> sendingContacts = new ArrayList<>();
	
	//Get a list of saved sending contacts
	List<configurationConnectionSenders> savedSendingContacts = utconfigurationmanager.getConnectionSenders(connectionId);
	
	if(!"".equals(organizationDetails.getPrimaryContactEmail())) {
	    if(savedSendingContacts != null) {
		if(!savedSendingContacts.isEmpty()) {
		    boolean primaryEmailFound = false;
		    configurationConnectionSenders primarySendingContact = null;
		    
		    for(configurationConnectionSenders savedSendingContact : savedSendingContacts) {
			if(savedSendingContact.getEmailAddress().equals(organizationDetails.getPrimaryContactEmail())) {
			    primaryEmailFound = true;
			    savedSendingContact.setContactType("Primary");
			    primarySendingContact = savedSendingContact;
			}
		    }
		    
		    if(!primaryEmailFound) {
			configurationConnectionSenders sendingContact = new configurationConnectionSenders();
			sendingContact.setContactType("Primary");
			sendingContact.setConnectionId(connectionId);
			sendingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
			sendingContact.setSendEmailNotifications(false);
			sendingContacts.add(sendingContact);
		    }
		    else {
			if(primarySendingContact != null) {
			    sendingContacts.add(primarySendingContact);
			}
		    }
		}
		else {
		    configurationConnectionSenders sendingContact = new configurationConnectionSenders();
		    sendingContact.setContactType("Primary");
		    sendingContact.setConnectionId(connectionId);
		    sendingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		    sendingContact.setSendEmailNotifications(false);
		    sendingContacts.add(sendingContact);
		}
	    }
	    else {
		configurationConnectionSenders sendingContact = new configurationConnectionSenders();
		sendingContact.setContactType("Primary");
		sendingContact.setConnectionId(connectionId);
		sendingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		sendingContact.setSendEmailNotifications(false);
		sendingContacts.add(sendingContact);
	    }
	}
	
	
        mav.addObject("sendingContacts", sendingContacts);

        return mav;
    }

    /**
     * The '/getAvailableReceivingContacts.do' function will return a list of users that are associated to the selected organization.
     *
     * @param orgId The organization selected in the drop down
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableReceivingContacts.do", method = RequestMethod.GET)
    public @ResponseBody ModelAndView getAvailableReceivingContacts(@RequestParam(value = "orgId", required = true) int orgId,@RequestParam(value = "connectionId", required = true) int connectionId) {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connectionReceivingContacts");

        Organization organizationDetails = organizationmanager.getOrganizationById(orgId);
	
	List<configurationConnectionReceivers> receivingContacts = new ArrayList<>();
	
	//Get a list of saved receiving contacts
	List<configurationConnectionReceivers> savedReceivingContacts = utconfigurationmanager.getConnectionReceivers(connectionId);
	
	if(!"".equals(organizationDetails.getPrimaryContactEmail())) {
	    if(savedReceivingContacts != null) {
		if(!savedReceivingContacts.isEmpty()) {
		    boolean primaryEmailFound = false;
		    configurationConnectionReceivers primaryReceivingContact = null;
		    
		    for(configurationConnectionReceivers savedReceivingContact : savedReceivingContacts) {
			if(savedReceivingContact.getEmailAddress().equals(organizationDetails.getPrimaryContactEmail())) {
			    primaryEmailFound = true;
			    savedReceivingContact.setContactType("Primary");
			    primaryReceivingContact = savedReceivingContact;
			}
		    }
		    
		    if(!primaryEmailFound) {
			configurationConnectionReceivers receivingContact = new configurationConnectionReceivers();
			receivingContact.setContactType("Primary");
			receivingContact.setConnectionId(connectionId);
			receivingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
			receivingContact.setSendEmailNotifications(false);
			receivingContacts.add(receivingContact);
		    }
		    else {
			if(primaryReceivingContact != null) {
			    receivingContacts.add(primaryReceivingContact);
			}
		    }
		}
		else {
		    configurationConnectionReceivers receivingContact = new configurationConnectionReceivers();
		    receivingContact.setContactType("Primary");
		    receivingContact.setConnectionId(connectionId);
		    receivingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		    receivingContact.setSendEmailNotifications(false);
		    receivingContacts.add(receivingContact);
		}
	    }
	    else {
		configurationConnectionReceivers receivingContact = new configurationConnectionReceivers();
		receivingContact.setContactType("Primary");
		receivingContact.setConnectionId(connectionId);
		receivingContact.setEmailAddress(organizationDetails.getPrimaryContactEmail());
		receivingContact.setSendEmailNotifications(false);
		receivingContacts.add(receivingContact);
	    }
	}
	
        mav.addObject("receivingContacts", receivingContacts);

        return mav;
    }

    /**
     * The '/getAvailableConfigurations.do' function will return a list of utConfiguration that have been set up for the passed in organization.
     *
     * @param orgId The organization selected in the drop down
     * @param selectBoxId
     *
     * @return configurations The available configurations
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getAvailableConfigurations.do", method = RequestMethod.GET)
    public @ResponseBody
    List<utConfiguration> getAvailableConfigurations(@RequestParam(value = "orgId", required = true) Integer orgId,@RequestParam(value = "selectBoxId", required = true) String selectBoxId) throws Exception {

        List<utConfiguration> configurations = utconfigurationmanager.getActiveConfigurationsByOrgId(orgId);
	
	List<utConfiguration> availableConfigurations = new ArrayList<>();

        if (configurations != null) {
            for (utConfiguration configuration : configurations) {
                configurationTransport transportDetails = utconfigurationTransportManager.getTransportDetails(configuration.getId());

                configuration.setOrgName(organizationmanager.getOrganizationById(configuration.getorgId()).getOrgName());
		
                configuration.settransportMethod(utconfigurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
		
		if(configuration.getType() == 1 && "srcConfig".equals(selectBoxId)) {
		    availableConfigurations.add(configuration);
		}
		else if(configuration.getType() == 2 && "tgtConfig".equals(selectBoxId)) {
		    availableConfigurations.add(configuration);
		}
            }
        }
        return availableConfigurations;
    }

    /**
     * The '/addConnection.do' POST request will create the connection between the passed in organization and the utConfiguration.
     *
     * @param connectionDetails
     * @param srcEmailNotifications
     * @param tgtEmailNotifications
     * @param redirectAttr
     *
     * @return	The method will return a 1 back to the calling ajax function which will handle the page load.
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/addConnection.do", method = RequestMethod.POST)
    public ModelAndView addConnection(
            @ModelAttribute(value = "connectionDetails") configurationConnection connectionDetails,
            @RequestParam(value = "srcEmailNotifications", required = false) List<String> srcEmailNotifications,
            @RequestParam(value = "tgtEmailNotifications", required = false) List<String> tgtEmailNotifications,
            RedirectAttributes redirectAttr) throws Exception {

        Integer connectionId;

        if (connectionDetails.getId() == 0) {
            connectionDetails.setStatus(true);
            connectionId = utconfigurationmanager.saveConnection(connectionDetails);
            redirectAttr.addFlashAttribute("savedStatus", "created");
        } 
	else {
            connectionId = connectionDetails.getId();
            utconfigurationmanager.updateConnection(connectionDetails);

            /* Delete existing senders and receivers */
            utconfigurationmanager.removeConnectionSenders(connectionId);
            utconfigurationmanager.removeConnectionReceivers(connectionId);
            redirectAttr.addFlashAttribute("savedStatus", "updated");
        }
	
	if(srcEmailNotifications != null) {
	    if(!srcEmailNotifications.isEmpty()) {
		for (String srcEmail : srcEmailNotifications) {
		    configurationConnectionSenders senderInfo = new configurationConnectionSenders();
		    senderInfo.setConnectionId(connectionId);
		    senderInfo.setEmailAddress(srcEmail);
		    senderInfo.setSendEmailNotifications(true);
		    utconfigurationmanager.saveConnectionSenders(senderInfo);
		}
	    }
	}
	
	
	if(tgtEmailNotifications != null) {
	    if(!tgtEmailNotifications.isEmpty()) {
		for (String tgtEmail : tgtEmailNotifications) {
		    configurationConnectionReceivers receiverInfo = new configurationConnectionReceivers();
		    receiverInfo.setConnectionId(connectionId);
		    receiverInfo.setEmailAddress(tgtEmail);
		    receiverInfo.setSendEmailNotifications(true);
		    utconfigurationmanager.saveConnectionReceivers(receiverInfo);
		}
	    }
	}

        ModelAndView mav = new ModelAndView(new RedirectView("connections"));

        return mav;

    }

    /**
     * The '/changeConnectionStatus.do' POST request will update the passed in connection status.
     *
     * @param connectionId The id for the connection to update the status for
     * @param statusVal The new status for the connection
     *
     * @return The method will return a 1 back to the calling ajax function.
     */
    @RequestMapping(value = "/changeConnectionStatus.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Integer changeConnectionStatus(@RequestParam boolean statusVal, @RequestParam int connectionId) throws Exception {

        configurationConnection connection = utconfigurationmanager.getConnection(connectionId);
        connection.setStatus(statusVal);
        utconfigurationmanager.updateConnection(connection);

        return 1;
    }

    /**
     * The '/getAvailableSendingContacts.do' function will return a list of users that are associated to the selected organization.
     *
     * @param selConfigId
     * @param sourceConfigId
     * @param connectionId
     * @param section
     *
     * @return users The available users
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/getConfigurationDataElements", method = RequestMethod.GET)
    public @ResponseBody ModelAndView getConfigurationDataElements(
	    @RequestParam(value = "selConfigId", required = true) Integer selConfigId, 
	    @RequestParam(value = "sourceConfigId", required = true) Integer sourceConfigId, 
	    @RequestParam(value = "connectionId", required = true) Integer connectionId, 
	    @RequestParam(value = "section", required = true) String section) throws Exception {

        ModelAndView mav = new ModelAndView();
	
	List<configurationFormFields> configurationDataElements = utconfigurationTransportManager.getConfigurationFields(selConfigId, 0);
	
	if("src".equals(section)) {
	   mav.setViewName("/administrator/configurations/connections/sourceConfigurationDataElements"); 
	   mav.addObject("sourceConfigurationDataElements",configurationDataElements);
	   
	}
	else {
	    boolean showErrorField = false;
	    mav.setViewName("/administrator/configurations/connections/targetConfigurationDataElements");
	    List<configurationFormFields> sourceconfigurationDataElements = utconfigurationTransportManager.getConfigurationFields(sourceConfigId, 0);
	    
	    configurationTransport targetConfigTransportDetails = utconfigurationTransportManager.getTransportDetails(selConfigId);
	    
	    if(targetConfigTransportDetails.isPopulateInboundAuditReport()) {
		showErrorField = true;
	    }
	    
	    if(connectionId > 0) {
		List<configurationconnectionfieldmappings> fieldMappings = utconfigurationTransportManager.getConnectionFieldMappings(selConfigId, sourceConfigId);
		
		if(!fieldMappings.isEmpty()) {
		    for(configurationconnectionfieldmappings fieldMapping : fieldMappings) {
			for(configurationFormFields tgtDataElements : configurationDataElements) {
			    /*if(fieldMapping.getFieldNo() == tgtDataElements.getFieldNo() && !fieldMapping.isUseField()) {
				tgtDataElements.setUseField(false);
			    }*/
			    if(fieldMapping.getFieldNo() == tgtDataElements.getFieldNo()) {
				tgtDataElements.setMappedErrorField(fieldMapping.getPopulateErrorFieldNo());
				tgtDataElements.setMappedToField(fieldMapping.getAssociatedFieldNo());
			    }
			}
		    }
		}
	    }
	    mav.addObject("targetConfigurationDataElements",configurationDataElements);
	    mav.addObject("sourceconfigurationDataElements",sourceconfigurationDataElements);
	    mav.addObject("showErrorField",showErrorField);
	}
        
        return mav;
    }
    
    
    @RequestMapping(value = "/saveConnectionElementMappings", method = RequestMethod.POST)
    public @ResponseBody String saveConnectionElementMappings (
	    @RequestParam(value = "connectionId", required = true) Integer connectionId, 
	    @RequestParam(value = "sourceConfigId", required = true) Integer sourceConfigId, 
	    @RequestParam(value = "targetConfigId", required = true) Integer targetConfigId, 
	    @RequestParam(value = "mappedFields[]", required = true) String mappedFields,
	    @RequestParam(value = "mappedErrorFields[]", required = false) String mappedErrorFields) throws Exception {
	
	
	if(connectionId == 0) {
	    configurationConnection newConnection = new configurationConnection();
	    newConnection.setsourceConfigId(sourceConfigId);
	    newConnection.settargetConfigId(targetConfigId);
	    newConnection.setStatus(true);
	    
	    connectionId = utconfigurationmanager.saveConnection(newConnection);
	}
	else {
	    //delete existing mapped fields
	    utconfigurationTransportManager.deleteConnectionMappedFields(connectionId);
	}
	
	String[] mappedFieldsAsArray = mappedFields.split(",");
	
	for (int i = 0; i < mappedFieldsAsArray.length; i++) { 
	    configurationconnectionfieldmappings newFieldMapping = new configurationconnectionfieldmappings();
	    newFieldMapping.setConnectionId(connectionId);
	    newFieldMapping.setSourceConfigId(sourceConfigId);
	    newFieldMapping.setTargetConfigId(targetConfigId);
	    
	    String[] mappedDetails = mappedFieldsAsArray[i].split("\\|");
	    
	    newFieldMapping.setFieldNo(Integer.parseInt(mappedDetails[0]));
	    newFieldMapping.setFieldDesc(mappedDetails[1]);
	    newFieldMapping.setUseField(Boolean.parseBoolean(mappedDetails[2]));
	    
	    if(mappedDetails[3].contains("~")) {
		String[] matchingFieldDetails = mappedDetails[3].split("\\~");
		newFieldMapping.setAssociatedFieldNo(0);
		newFieldMapping.setDefaultValue(matchingFieldDetails[1]);
	    }
	    else {
		newFieldMapping.setAssociatedFieldNo(Integer.parseInt(mappedDetails[3]));
	    }
	    
	    
	    if(mappedErrorFields != null) {
		String[] mappedErrorFieldsAsArray = mappedErrorFields.split(",");
		newFieldMapping.setPopulateErrorFieldNo(Integer.parseInt(mappedErrorFieldsAsArray[i]));
	    }
	    else {
		newFieldMapping.setPopulateErrorFieldNo(Integer.parseInt(mappedDetails[3]));
	    }
	    
	    
	    utconfigurationTransportManager.saveConnectionFieldMapping(newFieldMapping);
	}
	
	
	return connectionId.toString();
    }

    
    /**
     * The '/deleteConnection.do' POST request will remove the passed in connection.
     *
     * @param connectionId The id for the connection to update the status for
     *
     * @return The method will return a 1 back to the calling ajax function.
     */
    @RequestMapping(value = "/deleteConnection.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer deleteConnection(@RequestParam Integer connectionId) throws Exception {
	
	utconfigurationmanager.removeConnectionReceivers(connectionId);
	utconfigurationmanager.removeConnectionSenders(connectionId);
	utconfigurationTransportManager.deleteConnectionMappedFields(connectionId);
	utconfigurationmanager.removeConnection(connectionId);

        return 1;
    }
    
    /**
     * The 'createConnectionPrintPDF.do' method will print the selected connection.
     * @param connectionId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createConnectionPrintPDF.do", method = RequestMethod.GET)
    @ResponseBody
    public String printConfiguration(@RequestParam int connectionId) throws Exception {
	
	configurationConnection connectionDetails = utconfigurationmanager.getConnection(connectionId);

        utConfiguration srcconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.getsourceConfigId());
        utConfiguration tgtconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
        
	String connectionDetailFile = "/tmp/connectionId-" + connectionId + ".txt";
	String connectionPrintFile = "/tmp/UT-connection-" + connectionId + ".pdf";
	
	File detailsFile = new File(connectionDetailFile);
	detailsFile.delete();
	
	File printFile = new File(connectionPrintFile);
	printFile.delete();
	
	Document document = new Document(PageSize.A4);
	
	StringBuffer reportBody = new StringBuffer();
	
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(connectionDetailFile, true)));
	out.println("<html><body>");
	
	reportBody.append(utconfigurationmanager.printConnectionDetails(srcconfigDetails,tgtconfigDetails));
	
	out.println(reportBody.toString());
	
	out.println("</body></html>");
	
	out.close();
	
	FileOutputStream os = new FileOutputStream(connectionPrintFile);
	PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
			  
	document.open();
			    
	XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

	//replace with actual code to generate html info
	//we get image location here 
	FileInputStream fis = new FileInputStream(connectionDetailFile);
	worker.parseXHtml(pdfWriter, document, fis);

	fis.close();
	document.close();
	pdfWriter.close();
	os.close();
	
	File connectionDetailsFile = new File(connectionDetailFile);
	connectionDetailsFile.delete();

	return "UT-connection-" + connectionId;
    }
    
    @RequestMapping(value = "/printConfig/{file}", method = RequestMethod.GET)
    public void printConfig(@PathVariable("file") String file,HttpServletResponse response) throws Exception {
	
	File connectionPrintFile = new File ("/tmp/" + file + ".pdf");
	InputStream is = new FileInputStream(connectionPrintFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".pdf\"");
	FileCopyUtils.copy(is, response.getOutputStream());
	
	is.close();

	//Delete the file
	connectionPrintFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
    /**
     * The 'createConnectionExportFile.do' method will create the export file for the selected ut connection.
     * @param session
     * @param connectionId
     * @return 
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createConnectionExportFile.do", method = RequestMethod.GET)
    @ResponseBody
    public String createConnectionExportFile(HttpSession session, @RequestParam Integer connectionId) throws Exception {
	
	configurationConnection connectionDetails = utconfigurationmanager.getConnection(connectionId);

        utConfiguration srcconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.getsourceConfigId());
	Organization srcorgDetails = organizationmanager.getOrganizationById(srcconfigDetails.getorgId());
	
        utConfiguration tgtconfigDetails = utconfigurationmanager.getConfigurationById(connectionDetails.gettargetConfigId());
	Organization tgtorgDetails = organizationmanager.getOrganizationById(tgtconfigDetails.getorgId());

	String connectionDetailFile = "/tmp/connectionExport-" + connectionDetails.getId() + ".txt";
	
	File detailsFile = new File(connectionDetailFile);
	detailsFile.delete();
	
	StringBuffer reportBody = new StringBuffer();
	
	//Create a session to hold the email message body.
	StringBuffer emailBodySB = new StringBuffer();
	emailBodySB.append("The connection has been successfully exported.<br /><br />");
	emailBodySB.append("Connection Id: ").append(connectionDetails.getId());
	emailBodySB.append("<br />Source Configuration Name: ").append(srcconfigDetails.getconfigName().trim());
	emailBodySB.append("<br />Target Configuration Name: ").append(tgtconfigDetails.getconfigName().trim());
	session.setAttribute("emailBody", emailBodySB);
	
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(connectionDetailFile, true)));
	
	reportBody.append(utconfigurationmanager.exportConnectionSrcDetails(srcconfigDetails,srcorgDetails));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportConnectionTgtDetails(tgtconfigDetails,tgtorgDetails));
	reportBody.append(System.getProperty("line.separator"));
	reportBody.append(utconfigurationmanager.exportConnectionFields(connectionId));
	
	out.println(reportBody.toString());
	
	out.close();
	
	//Check to see if an email needs to be sent
	emailBodySB = (StringBuffer) session.getAttribute("emailBody");
	
	if(!"".equals(emailBodySB.toString())) {
	    mailMessage mail = new mailMessage();
	    mail.settoEmailAddress(myProps.getProperty("admin.email"));
	    mail.setfromEmailAddress("support@health-e-link.net");
	    mail.setmessageSubject("UT Connection Import Script has been Created");
	    mail.setmessageBody(emailBodySB.toString());
	    emailMessageManager.sendEmail(mail);
	    
	    //Delete email body
	    session.removeAttribute("emailBody");
	}
	
	return "connectionExport-"+connectionDetails.getId();
    }

    @RequestMapping(value = "/printConnectionExport/{file}", method = RequestMethod.GET)
    public void printConnectionExport(@PathVariable("file") String file,HttpServletResponse response) throws Exception {
	
	File connectionExportFile = new File ("/tmp/" + file + ".txt");
	InputStream is = new FileInputStream(connectionExportFile);

	response.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".txt\"");
	FileCopyUtils.copy(is, response.getOutputStream());
	
	is.close();

	//Delete the file
	connectionExportFile.delete();

	 // close stream and return to view
	response.flushBuffer();
    } 
    
    /**
     * The 'connectionImportUpload' GET request will return modal for uploading a connection import script.
     *
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/connectionImportUpload", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView connectionImportUpload(HttpSession session) throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/configurations/connections/connectionImportFile");
	mav.addObject("expectedExt", "txt");
        return mav;
    }
    
    /**
     * The '/submitConnectionImportFile' function will be used to upload a new connection import script.
     *
     * @param importConnectionFile
     * @param session
     * @return 
     * @throws java.lang.Exception 
     */
    @RequestMapping(value = "/submitConnectionImportFile", method = RequestMethod.POST)
    public @ResponseBody 
    int submitConnectionImportFile(@RequestParam(value = "importConnectionFile", required = true)  MultipartFile importConnectionFile, HttpSession session) throws Exception {

	Integer returnVal = 1;
	
	String fileName = importConnectionFile.getOriginalFilename();
	
	try {
	    
	    Path filepath = Paths.get("/tmp/", importConnectionFile.getOriginalFilename());

	    try (OutputStream os = Files.newOutputStream(filepath)) {
		os.write(importConnectionFile.getBytes());
	    }
	   
	    BufferedReader br = null;
	    String strLine = "";
	    try {
	    
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream("/tmp/"+fileName), "UTF-8"));
		while (((strLine = reader.readLine()) != null) && reader.getLineNumber() <= 1){
		    if(!strLine.contains("[srcconfig")) {
			returnVal = 2;
		    }
		}
	        reader.close();
	    } catch (FileNotFoundException e) {
		returnVal = 0;
	    } catch (IOException e) {
		 returnVal = 0;
	    }
	    
	} catch (IOException e) {
	    returnVal = 0;
	}
	
	if(returnVal == 1) {
	    executor.execute(new Runnable() {
		@Override
		public void run() {
		    try {
			loadConnectionImportFile(fileName,session);
		    } catch (Exception ex) {
			Logger.getLogger(adminConfigConnectionController.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    });
	}
	
	return returnVal;
    }
    
    /**
     * 
     * @param fileName
     * @param session
     * @throws Exception 
     */
    public void loadConnectionImportFile(String fileName, HttpSession session) throws Exception {
	
	BufferedReader br = null;
	String strLine = "";
	 
	try {
	    //Need to check if the connection already exists for the source config and target config
	    boolean connectionFound = false;
	    String[] strArrayValues;
	    String srcOrgName = "";
	    String srcConfigName = "";
	    String tgtOrgName = "";
	    String tgtConfigName = "";
	    StringBuilder emailBody = new StringBuilder();
	    
	    LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream("/tmp/"+fileName), "UTF-8"));
	    
	    while (((strLine = reader.readLine()) != null)){
		 strLine = strLine.replace("[", "").replace("]", "");
		 strArrayValues = strLine.split("\\|", -1);
		 
		 if(strArrayValues[0].equals("srcconfig")) {
		     srcConfigName = strArrayValues[1];
		     srcOrgName = strArrayValues[2];
		 }
		 else if(strArrayValues[0].equals("tgtconfig")) {
		     tgtConfigName = strArrayValues[1];
		     tgtOrgName = strArrayValues[2];
		 }
	    }
	    reader.close();
	    
	    if(!"".equals(srcConfigName) && !"".equals(tgtConfigName)) {
		connectionFound = checkIfConnectionExists(srcConfigName,tgtConfigName);
	    }
	    
	    if(!connectionFound) {
		reader = new LineNumberReader(new InputStreamReader(new FileInputStream("/tmp/"+fileName), "UTF-8"));
		Integer srcConfigId = 0;
		Integer tgtConfigId = 0;
		Integer connectionId = 0;
		boolean connectionImportSuccessful = true;

		while (((strLine = reader.readLine()) != null)){
		    strLine = strLine.replace("[", "").replace("]", "");
		    strArrayValues = strLine.split("\\|", -1);

		    if(strArrayValues[0].equals("srcconfig")) {
			try {
			    srcConfigName = strArrayValues[1];
			    srcOrgName = strArrayValues[2];
			    srcConfigId = findConnectionConfigDetails(srcConfigName,srcOrgName,1);
			}
			catch (Exception ex) {
			    connectionImportSuccessful = false;
			    break;
			}

			if(srcConfigId == 0) {
			    break;
			}
		    }
		    else if(strArrayValues[0].equals("tgtconfig")) {
			try {
			    tgtConfigName = strArrayValues[1];
			    tgtOrgName = strArrayValues[2];
			    tgtConfigId = findConnectionConfigDetails(tgtConfigName,tgtOrgName,2);
			}
			catch (Exception ex) {
			    connectionImportSuccessful = false;
			    break;
			}

			if(tgtConfigId == 0) {
			    break;
			}
		    }
		    else if(strArrayValues[0].equals("fieldmapping")) {

			if(connectionId == 0 && srcConfigId > 0 && tgtConfigId > 0) {
			    connectionId = processImportConnection(srcConfigId,tgtConfigId);
			}

			try {
			    processImportConnectionField(strArrayValues,connectionId,srcConfigId,tgtConfigId);
			}
			catch (Exception ex) {
			    processImportConnectionError("Adding Fields",connectionId,srcOrgName,tgtOrgName,srcConfigName,tgtConfigName,ex);
			    connectionImportSuccessful = false;
			    break;
			}
		    }
		}
		reader.close();

		String emailSubject = "Configuration Import has been Completed";
		
		Organization srcOrgDetails = null;
		Organization tgtOrgDetails = null;
		
		if(organizationmanager.getOrganizationByName(srcOrgName) != null) {
		    if(!organizationmanager.getOrganizationByName(srcOrgName).isEmpty()) {
			srcOrgDetails = organizationmanager.getOrganizationByName(srcOrgName).get(0);
		    }
		}
		if(organizationmanager.getOrganizationByName(tgtOrgName) != null) {
		    if(!organizationmanager.getOrganizationByName(tgtOrgName).isEmpty()) {
			tgtOrgDetails = organizationmanager.getOrganizationByName(tgtOrgName).get(0);
		    }
		}
		
		if(srcOrgDetails == null) {
		    emailSubject = "Connection Import was Not Successful";

		    emailBody.append("The following source organization was not found.");
		    emailBody.append("<br /><br />");
		    emailBody.append("Organization Name: ").append(srcOrgName);
		    emailBody.append("<br />Configuration Name: ").append(srcConfigName);
		    emailBody.append("<br/><br />Reason<br />The source organization was not found on the system.");

		    mailMessage mail = new mailMessage();
		    mail.settoEmailAddress(myProps.getProperty("admin.email"));
		    mail.setfromEmailAddress("support@health-e-link.net");
		    mail.setmessageSubject(emailSubject);
		    mail.setmessageBody(emailBody.toString());
		    emailMessageManager.sendEmail(mail);
		}
		else if(tgtOrgDetails == null) {
		    emailSubject = "Connection Import was Not Successful";

		    emailBody.append("The following target organization was not found.");
		    emailBody.append("<br /><br />");
		    emailBody.append("Organization Name: ").append(tgtOrgName);
		    emailBody.append("<br />Configuration Name: ").append(tgtConfigName);
		    emailBody.append("<br/><br />Reason<br />The target organization was not found on the system.");

		    mailMessage mail = new mailMessage();
		    mail.settoEmailAddress(myProps.getProperty("admin.email"));
		    mail.setfromEmailAddress("support@health-e-link.net");
		    mail.setmessageSubject(emailSubject);
		    mail.setmessageBody(emailBody.toString());
		    emailMessageManager.sendEmail(mail);
		}
		else if(srcConfigId == 0) {

		    emailSubject = "Connection Import was Not Successful";

		    emailBody.append("The following connection import has failed.");
		    emailBody.append("<br /><br />");
		    emailBody.append("Organization Name: ").append(srcOrgDetails.getOrgName());
		    emailBody.append("<br />Configuration Name: ").append(srcConfigName);
		    emailBody.append("<br/><br />Reason<br />The source configuration was not found on the system.");

		    mailMessage mail = new mailMessage();
		    mail.settoEmailAddress(myProps.getProperty("admin.email"));
		    mail.setfromEmailAddress("support@health-e-link.net");
		    mail.setmessageSubject(emailSubject);
		    mail.setmessageBody(emailBody.toString());
		    emailMessageManager.sendEmail(mail);
		}
		else if(tgtConfigId == 0) {

		    emailSubject = "Connection Import was Not Successful";

		    emailBody.append("The following connection import has failed.");
		    emailBody.append("<br /><br />");
		    emailBody.append("Organization Name: ").append(tgtOrgDetails.getOrgName());
		    emailBody.append("<br />Configuration Name: ").append(tgtConfigName);
		    emailBody.append("<br/><br />Reason<br />The target configuration was not found on the system.");

		    mailMessage mail = new mailMessage();
		    mail.settoEmailAddress(myProps.getProperty("admin.email"));
		    mail.setfromEmailAddress("support@health-e-link.net");
		    mail.setmessageSubject(emailSubject);
		    mail.setmessageBody(emailBody.toString());
		    emailMessageManager.sendEmail(mail);
		}
		else if(connectionImportSuccessful) {
		    emailBody.append("The following connection import has been completed and is now available.");
		    emailBody.append("<br /><br />");
		    emailBody.append("Source Organization Name: ").append(srcOrgDetails.getOrgName());
		    emailBody.append("<br />Source Configuration Name: ").append(srcConfigName);
		    emailBody.append("<br /><br />Target Organization Name: ").append(tgtOrgDetails.getOrgName());
		    emailBody.append("<br />Target Configuration Name: ").append(tgtConfigName);
		    emailBody.append("<br /><br />Connection Id: ").append(connectionId);

		    //Turn the configuration on
		    configurationConnection connectionDetails = utconfigurationmanager.getConnection(connectionId);
		    connectionDetails.setStatus(true);
		    utconfigurationmanager.updateConnection(connectionDetails);

		    mailMessage mail = new mailMessage();
		    mail.settoEmailAddress(myProps.getProperty("admin.email"));
		    mail.setfromEmailAddress("support@health-e-link.net");
		    mail.setmessageSubject(emailSubject);
		    mail.setmessageBody(emailBody.toString());
		    emailMessageManager.sendEmail(mail);
		}
	    }
	    else {
		emailBody.append("The following connection import has failed because a connection already exists for the source and target configuration in the import file.");
		emailBody.append("<br /><br />");
		emailBody.append("Source Organization Name: ").append(srcOrgName);
		emailBody.append("<br />Source Configuration Name: ").append(srcConfigName);
		emailBody.append("<br /><br />Target Organization Name: ").append(tgtOrgName);
		emailBody.append("<br />Target Configuration Name: ").append(tgtConfigName);

		mailMessage mail = new mailMessage();
		mail.settoEmailAddress(myProps.getProperty("admin.email"));
		mail.setfromEmailAddress("support@health-e-link.net");
		mail.setmessageSubject("The imported connection already exists.");
		mail.setmessageBody(emailBody.toString());
		emailMessageManager.sendEmail(mail);
	    }
	} catch (FileNotFoundException e) {
	    Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, e);
	} catch (IOException e) {
	     Logger.getLogger(transactionInManagerImpl.class.getName()).log(Level.SEVERE, null, e);
	}
    }
    
    /**
     * The 'findConnectionConfigDetails' method will search the system to make sure the configuration details passed in exist and active.
     * 
     * @param configName 
     * @param orgCleanURL
     * @param type
     * 
     * @return The method will return the source configuration id
     */
    public Integer findConnectionConfigDetails(String configName, String orgCleanURL, Integer type) {
	
	Integer configId = 0;
	
	List<Organization> orgs = organizationmanager.getOrganizationByName(orgCleanURL);
	
	if(!orgs.isEmpty()) {
	    Integer orgId = orgs.get(0).getId();
	    
	    List<utConfiguration> orgConfigs = utconfigurationmanager.getConfigurationsByOrgId(orgId,"");
	    
	    if(!orgConfigs.isEmpty()) {
		for(utConfiguration config : orgConfigs) {
		    if(config.getconfigName().trim().equals(configName) && !config.isDeleted() && config.getStatus() && config.getType() == type) {
			configId = config.getId();
			break;
		    }
		}
	    }
	}
	
	return configId;
    }
    
    /**
     * The 'processImportConnectionField' method will process the connection field mappings from the connection import script.
     * 
     * @param strArrayValues 
     * @param connectionId
     * @param srcConfigId 
     * @param tgtConfigId 
     * 
     */
    public void processImportConnectionField(String[] strArrayValues, Integer connectionId, Integer srcConfigId, Integer tgtConfigId) {
	
	configurationconnectionfieldmappings fieldMapping = new configurationconnectionfieldmappings();
	fieldMapping.setConnectionId(connectionId);
	fieldMapping.setSourceConfigId(srcConfigId);
	fieldMapping.setTargetConfigId(tgtConfigId);
	fieldMapping.setFieldNo(Integer.parseInt(strArrayValues[5]));
	fieldMapping.setFieldDesc(strArrayValues[6]);
	if(strArrayValues[7].equals("true")) {
	    fieldMapping.setUseField(true);
	}
	else {
	    fieldMapping.setUseField(false);
	}
	fieldMapping.setAssociatedFieldNo(Integer.parseInt(strArrayValues[8]));
	
	if (!"null".equals(strArrayValues[9])) {
	    fieldMapping.setPopulateErrorFieldNo(Integer.parseInt(strArrayValues[9]));
	}
	if (!"null".equals(strArrayValues[10])) {
	    fieldMapping.setDefaultValue(strArrayValues[10]);
	}
	
	try {
	    utconfigurationTransportManager.saveConnectionFieldMapping(fieldMapping);
	}
	catch (Exception ex) {}
    }
   
    /**
     * The 'processImportConnection' method will add the new connection to the system.
     * 
     * @param srcConfigId
     * @param tgtConfigId
     * 
     * @return The method will return the new connection id
     */
    public Integer processImportConnection(Integer srcConfigId, Integer tgtConfigId) {
	
	Integer connectionId = 0;
	
	configurationConnection newConnection = new configurationConnection();
	newConnection.setsourceConfigId(srcConfigId);
	newConnection.settargetConfigId(tgtConfigId);
	newConnection.setStatus(false);
	
	connectionId = utconfigurationmanager.saveConnection(newConnection);
	
	return connectionId;
    }
    
    
    /**
     * 
     * @param importSection
     * @param connectionId
     * @param srcOrgName
     * @param tgtOrgName
     * @param srcConfigName
     * @param tgtConfigName
     * @param ex 
     */
    public void processImportConnectionError(String importSection, Integer connectionId, String srcOrgName, String tgtOrgName, String srcConfigName, String tgtConfigName, Exception ex) throws Exception {
	
	Organization srcOrgDetails = organizationmanager.getOrganizationByName(srcOrgName).get(0);
	Organization tgtOrgDetails = organizationmanager.getOrganizationByName(tgtOrgName).get(0);

	//back out the configuration import details
	String backOutConnectionSQL = "";
	
	backOutConnectionSQL += "delete from configurationconnectionfieldmappings where connectionId = " + connectionId + ";";
	backOutConnectionSQL += "delete from configurationconnections where id = " + connectionId + ";";
	
	if(!"".equals(backOutConnectionSQL)) {
	    messagetypemanager.executeSQLStatement(backOutConnectionSQL);
	}
	
	String emailSubject = "Connection Import has been Backed out due to an error.";
	StringBuilder emailBody = new StringBuilder();

	emailBody.append("The following connection import has failed.");
	emailBody.append("<br /><br />");
	emailBody.append("Source Organization Name: ").append(srcOrgDetails.getOrgName());
	emailBody.append("<br />Source Configuration Name: ").append(srcConfigName);
	emailBody.append("<br />Target Organization Name: ").append(tgtOrgDetails.getOrgName());
	emailBody.append("<br />Target Configuration Name: ").append(tgtConfigName);
	emailBody.append("<br/><br />Reason for failure:<br />").append(ExceptionUtils.getStackTrace(ex));

	mailMessage mail = new mailMessage();
	mail.settoEmailAddress(myProps.getProperty("admin.email"));
	mail.setfromEmailAddress("support@health-e-link.net");
	mail.setmessageSubject(emailSubject);
	mail.setmessageBody(emailBody.toString());
	emailMessageManager.sendEmail(mail);
    }
    
    /**
     * The 'checkIfConnectionExists' method will search the system to make sure the connection between passed in source configuration and target
     * configuration does not exists.
     * 
     * @param srcConfigName 
     * @param tgtConfigName
     * 
     * @return The method will return the source configuration id
     */
    public boolean checkIfConnectionExists(String srcConfigName, String tgtConfigName) {
	
	boolean connectionExists = false;
	
	utConfiguration srcConfigDetails = utconfigurationmanager.getConfigurationByName(srcConfigName,0);
	utConfiguration tgtConfigDetails = utconfigurationmanager.getConfigurationByName(tgtConfigName,0);
	
	if(srcConfigDetails != null && tgtConfigDetails != null) {
	    List<configurationConnection> srcTgtConnections = utconfigurationmanager.getConnectionsBySrcAndTargetConfigurations(srcConfigDetails.getId(),tgtConfigDetails.getId());
	    
	    if(!srcTgtConnections.isEmpty()) {
		connectionExists = true;
	    }
	}
	
	return connectionExists;
    }
}
