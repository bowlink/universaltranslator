package com.hel.ut.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import com.hel.ut.model.Organization;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchUploads;
import com.hel.ut.service.organizationManager;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.utUser;
import com.hel.ut.reference.CountryList;
import com.hel.ut.reference.USStateList;
import com.hel.ut.reference.fileSystem;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.transactionOutManager;
import com.hel.ut.service.userManager;
import com.registryKit.registry.helRegistry;
import com.registryKit.registry.helRegistryManager;
import com.registryKit.registry.tiers.tierManager;
import com.registryKit.registry.tiers.tierOrganizationDetails;
import com.registryKit.registry.tiers.tiers;
import com.hel.ut.service.utConfigurationManager;
import com.hel.ut.service.utConfigurationTransportManager;
import com.registryKit.registry.tiers.programOrgHierarchy;
import com.registryKit.registry.tiers.programOrgHierarchyDetails;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;



/**
 * The adminOrgController class will handle all URL requests that fall inside of the '/administrator/organizations' url path.
 *
 * This path will be used when the administrator is managing and existing organization or creating a new organization
 *
 * @author chadmccue
 *
 */
@Controller
@RequestMapping("/administrator/organizations")
public class adminOrgContoller {

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private utConfigurationManager configurationmanager;

    @Autowired
    private utConfigurationTransportManager configurationTransportManager;
    
    @Autowired
    private helRegistryManager helregistrymanager;
    
    @Autowired
    private transactionInManager transactioninmanager;
    
    @Autowired
    private transactionOutManager transactionoutmanager;
    
    @Autowired
    private tierManager tiermanager;
    
    @Autowired
    private userManager userManager;
    
    @Resource(name = "myProps")
    private Properties myProps;
    

    /**
     * The private maxResults variable will hold the number of results to show per list page.
     */
    private static int maxResults = 20;

    /**
     * The '/list' GET request will serve up the existing list of organizations in the system
     *
     * @return	The organization page list
     *
     * @Objects	(1) An object containing all the found organizations (2) An object will be returned that hold the organiationManager so we can run some functions on each returned org in the list
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listOrganizations() throws Exception {
	
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/listOrganizations");

	//List<Organization> organizations = organizationManager.getOrganizations();
	//mav.addObject("organizationList",organizations);
	
        return mav;
    }
    
    @RequestMapping(value = "/ajax/getOrganizations", method = RequestMethod.GET)
    @ResponseBody
    public String getOrganizations(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
	
	Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
	Integer iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
        Integer iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
        String sortColumn = request.getParameter("iSortCol_0");
        String sortColumnName = request.getParameter("mDataProp_"+sortColumn);
        String searchTerm = request.getParameter("sSearch").toLowerCase();
        String sEcho = request.getParameter("sEcho");
        String sortDirection = request.getParameter("sSortDir_0");
        Integer totalRecords = 0;
	
	
	List<Organization> organizations = organizationManager.getOrganizationsPaged(iDisplayStart, iDisplayLength, searchTerm, sortColumnName, sortDirection);
	List<Organization> totalOrgs = organizationManager.getOrganizations();
	
	List<helRegistry> helRegistries = helregistrymanager.getAllActiveRegistries();
	
	for(Organization org : totalOrgs) {
	    if(!"bowlinktest".equals(org.getCleanURL().trim().toLowerCase())) {
		totalRecords++;
	    }
	    if(org.getHelRegistryId() > 0) {
		if(helRegistries != null) {
		    for(helRegistry reg : helRegistries) {
			if(reg.getId() == org.getHelRegistryId()) {
			    org.setHelRegistry(reg.getRegistryName());
			}
		    }
		}
	    }
	}
	
	for(Organization org : organizations) {
	    if(org.getHelRegistryId() > 0) {
		if(helRegistries != null) {
		    for(helRegistry reg : helRegistries) {
			if(reg.getId() == org.getHelRegistryId()) {
			    org.setHelRegistry(reg.getRegistryName());
			}
		    }
		}
	    }
	}
	
	jsonResponse.addProperty("sEcho", sEcho);
        jsonResponse.addProperty("iTotalRecords", totalRecords);
        jsonResponse.addProperty("iTotalDisplayRecords", totalRecords);
        jsonResponse.add("aaData", gson.toJsonTree(organizations));
        
        return jsonResponse.toString();
    }
    

    /**
     * The '/create' GET request will serve up the create new organization page
     *
     * @return	The create new organization form
     *
     * @Objects	(1) An object with a new organization
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView createOrganization() throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/organizationDetails");
        mav.addObject("organization", new Organization());

        //Get a list of states
        USStateList stateList = new USStateList();

        //Get the object that will hold the states
        mav.addObject("stateList", stateList.getStates());
        
        //Get a list of countries
        CountryList countryList = new CountryList();

        //Get the object that will hold the countries
        mav.addObject("countryList", countryList.getCountries());
	
	List<Organization> organizations = organizationManager.getAllActiveOrganizations();
	mav.addObject("organizationList",organizations);

        return mav;
    }

    /**
     * The '/create' POST request will submit the new organization once all required fields are checked, the system will also check to make sure the organziation name is not already in use.
     *
     * @param organization	The object holding the organization form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @param action	The variable that holds which button was pressed
     *
     * @return	Will return the organization list page on "Save & Close" Will return the organization details page on "Save" Will return the organization create page on error
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView saveNewOrganization(@Valid Organization organization, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        //Get a list of states
        USStateList stateList = new USStateList();
        
        //Get a list of countries
	CountryList countryList = new CountryList();
	
	if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/organizationDetails");
            //Get the object that will hold the states
            mav.addObject("stateList", stateList.getStates());
            mav.addObject("countryList", countryList.getCountries());
            return mav;
        }
       
        List<Organization> existing = organizationManager.getOrganizationByName(organization.getcleanURL());
        if (!existing.isEmpty()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/organizationDetails");
            mav.addObject("id", organization.getId());
            mav.addObject("existingOrg", "Organization " + organization.getOrgName() + " already exists.");
            //Get the object that will hold the states
            mav.addObject("stateList", stateList.getStates());
            mav.addObject("countryList", countryList.getCountries());
            return mav;
        }
	
        Integer id = organizationManager.createOrganization(organization);

        //Get the organization name that was just added
        Organization latestorg = organizationManager.getOrganizationById(id);

        redirectAttr.addFlashAttribute("savedStatus", "created");

        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView(new RedirectView(latestorg.getcleanURL() + "/"));
            return mav;
        } else {
            ModelAndView mav = new ModelAndView(new RedirectView("list"));
            return mav;
        }
    }

    /**
     * The '/{cleanURL}' GET request will display the clicked organization details page.
     *
     * @param cleanURL	The {clearnURL} will be the organizations name with spaces removed. This was set when the organization was created.
     * @param authentication
     *
     * @return	Will return the organization details page.
     *
     * @Objects	(1) The object containing all the information for the clicked org (2) The 'id' of the clicked org that will be used in the menu and action bar
     *
     * @throws Exception
     *
     */
    @RequestMapping(value = "/{cleanURL}", method = RequestMethod.GET)
    public ModelAndView viewOrganizationDetails(@PathVariable String cleanURL, Authentication authentication) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/organizationDetails");

        List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
        Organization orgDetails = organization.get(0);

        mav.addObject("id", orgDetails.getId());
        mav.addObject("selOrgType", orgDetails.getOrgType());
        mav.addObject("organization", orgDetails);

        //Get a list of states
        USStateList stateList = new USStateList();

        //Get the object that will hold the states
        mav.addObject("stateList", stateList.getStates());
        
        //Get a list of countries
        CountryList countryList = new CountryList();

        //Get the object that will hold the countries
        mav.addObject("countryList", countryList.getCountries());

	List<Organization> organizations = organizationManager.getAllActiveOrganizations();
	mav.addObject("organizationList",organizations);
	
	utUser userDetails = userManager.getUserByUserName(authentication.getName());
	
	boolean allowOrgDelete = false;
	
	if(userDetails.getRoleId() == 1) {
	    allowOrgDelete = true;
	}
	
	if(allowOrgDelete) {
	    //Check to see if this org has any submitted batches, if so don't allow delete option
	    List<batchUploads> orgBatchUploads = transactioninmanager.getBatchesByOrgId(orgDetails.getId());
	    
	    if(!orgBatchUploads.isEmpty()) {
		allowOrgDelete = false;
	    }
	    
	    if(allowOrgDelete) {
		//Check to see if this org has any submitted batches, if so don't allow delete option
		List<batchDownloads> orgBatchDownloads = transactionoutmanager.getBatchesByOrgId(orgDetails.getId());

		if(!orgBatchDownloads.isEmpty()) {
		    allowOrgDelete = false;
		}
	    }
	    
	    if(allowOrgDelete) {
		List<utConfiguration> orgConfigs = configurationmanager.getConfigurationsByOrgId(orgDetails.getId(), "");
		
		if(!orgConfigs.isEmpty()) {
		    allowOrgDelete = false;
		}
	    }
 	}
	
	mav.addObject("allowOrgDelete",allowOrgDelete);
	
        return mav;
    }

    /**
     * The '/{cleanURL}' POST request will handle submitting changes for the selected organization.
     *
     * @param organization	The object containing the organization form fields
     * @param result	The validation result
     * @param redirectAttr	The variable that will hold values that can be read after the redirect
     * @param action	The variable that holds which button was pressed
     *
     * @return	Will return the organization list page on "Save & Close" Will return the organization details page on "Save" Will return the organization create page on error
     *
     * @Objects	(1) The object containing all the information for the clicked org (2) The 'id' of the clicked org that will be used in the menu and action bar
     * @throws Exception
     */
    @RequestMapping(value = "/{cleanURL}", method = RequestMethod.POST)
    public ModelAndView updateOrganization(@Valid Organization organization, BindingResult result, RedirectAttributes redirectAttr, @RequestParam String action) throws Exception {

        //Get a list of states
        USStateList stateList = new USStateList();
        
        //Get a list of countries
        CountryList countryList = new CountryList();

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/administrator/organizations/organizationDetails");
            mav.addObject("id", organization.getId());
            mav.addObject("selOrgType", organization.getOrgType());
            //Get the object that will hold the states
            mav.addObject("stateList", stateList.getStates());
            mav.addObject("countryList", countryList.getCountries());
            return mav;
        }

        Organization currentOrg = organizationManager.getOrganizationById(organization.getId());
	
	boolean updatedName = false;
	boolean missingDirs = false;
	
	//Update the organization
	String orgCleanURL = organization.getOrgName().replace(" ", "");
	
	if(!orgCleanURL.equals(organization.getCleanURL())) {
	    organization.setcleanURL(orgCleanURL);
	}
	
        if (!currentOrg.getcleanURL().trim().equals(organization.getcleanURL().trim())) {
	    List<Organization> existing = organizationManager.getOrganizationByName(organization.getcleanURL());
	    updatedName = true;
            if (!existing.isEmpty()) {
                ModelAndView mav = new ModelAndView();
                mav.setViewName("/administrator/organizations/organizationDetails");
                mav.addObject("id", organization.getId());
                mav.addObject("selOrgType", organization.getOrgType());
                mav.addObject("existingOrg", "Organization " + organization.getOrgName().trim() + " already exists.");
                //Get the object that will hold the states
                mav.addObject("stateList", stateList.getStates());
                mav.addObject("countryList", countryList.getCountries());
                return mav;
            }
        }
	
	//Make sure the organization folder name exists
	String UTDirectory = myProps.getProperty("ut.directory.utRootDir");
	File directory = new File(UTDirectory.replace("/home/","/") + organization.getcleanURL());
	if (!directory.exists()) {
	    missingDirs = true;
	}

        organizationManager.updateOrganization(organization);
	
	//If updated name, need to check if any configurations are set up for this or
	if(updatedName || missingDirs) {
	    List<utConfiguration> configurations = configurationmanager.getActiveConfigurationsByOrgId(currentOrg.getId());
	    
	    if(configurations != null) {
		if(!configurations.isEmpty()) {
		    
		    List<Integer> configIds = configurations.stream().map(e -> e.getId()).collect(Collectors.toList());
		    
		    configurationmanager.updateConfigurationDirectories(configIds,currentOrg.getcleanURL().trim(),organization.getcleanURL().trim());
		}
	    }
	    
	    if(updatedName) {
		//Need to delete the old directory
		File oldDirectory = new File(UTDirectory.replace("/home/","/") + currentOrg.getcleanURL());
		if (directory.exists()) {
		    fileSystem filesystem = new fileSystem();
		    filesystem.deleteOrgDirectories(UTDirectory.replace("/home/","/") + currentOrg.getcleanURL());
		}
	    }
	}
	
        //This variable will be used to display the message on the details form
        redirectAttr.addFlashAttribute("savedStatus", "updated");

        //If the "Save" button was pressed 
        if (action.equals("save")) {
            ModelAndView mav = new ModelAndView(new RedirectView("../" + organization.getcleanURL() + "/"));
            return mav;
        } //If the "Save & Close" button was pressed.
        else {
            ModelAndView mav = new ModelAndView(new RedirectView("../list"));
            return mav;
        }
    }

    /**
     * The '/{cleanURL}/delete POST request will remove the clicked organization and anything associated to it.
     *
     * @param id	The variable that holds the id of the clicked organization
     * @param redirectAttr
     * @return 
     * @throws java.lang.Exception
     *
     * @Return	Will return the organization list page
     *
     */
    @RequestMapping(value = "/{cleanURL}/delete", method = RequestMethod.POST)
    public ModelAndView deleteOrganization(@RequestParam int id, RedirectAttributes redirectAttr) throws Exception {

        organizationManager.deleteOrganization(id);

        //This variable will be used to display the message on the details form
        redirectAttr.addFlashAttribute("savedStatus", "deleted");
        ModelAndView mav = new ModelAndView(new RedirectView("../list"));
        return mav;
    }

    /**
     * *********************************************************
     * ORGANIZATION CONFIGUARATION FUNCTIONS 
     ********************************************************
     */
    /**
     * The '/{cleanURL/configurations' GET request will display the list of configurations set up for the selected organization.
     *
     * @param cleanURL	The variable that holds the organization that is being viewed
     *
     * @return	Will return the organization utConfiguration list page
     *
     * @Objects	(1) An object that holds configurations found for the organization (2) The orgId used for the menu and action bar
     *
     * @throws Exception
     */
    @RequestMapping(value = "/{cleanURL}/configurations", method = RequestMethod.GET)
    public ModelAndView listOrganizationConfigs(@PathVariable String cleanURL) throws Exception {
	
	List<Organization> organization = organizationManager.getOrganizationByName(cleanURL);
        Organization orgDetails = organization.get(0);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/organizations/configurations");

        List<utConfiguration> configurations = configurationmanager.getConfigurationsByOrgId(orgDetails.getId(), "");

        mav.addObject("orgName", orgDetails.getOrgName());
        mav.addObject("id", orgDetails.getId());
        mav.addObject("selOrgType", orgDetails.getOrgType());
        mav.addObject("configs", configurations);

        configurationTransport transportDetails;

        for (utConfiguration config : configurations) {
            transportDetails = configurationTransportManager.getTransportDetails(config.getId());
            if (transportDetails != null) {
                config.settransportMethod(configurationTransportManager.getTransportMethodById(transportDetails.gettransportMethodId()));
            }
        }

        return mav;
    }

    /**
     * The '/getHELRegistries' GET request will return a list of Health-e-Link registries
     * 
     *
     * @return The function will return a list of active health-e-link registries.
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = {"/{cleanURL}/getHELRegistries", "/getHELRegistries"}, method = RequestMethod.GET)
    public @ResponseBody List<helRegistry> getHealtheLinkRegistries() throws Exception {
	
	List<helRegistry> helRegistries = helregistrymanager.getAllActiveRegistries();
	
        return helRegistries;
    }
    
    
    /**
     * The '/getHELRegistryOrganizations' GET request will return a list of registry organizations saved at the
     * last set up tier.
     *
     *
     * @param registryType
     * @param tierLevel
     * @return The function will return a list of organizations
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = {"/{cleanURL}/getHELRegistryOrganizations", "/getHELRegistryOrganizations"}, method = RequestMethod.GET)
    public @ResponseBody List<tierOrganizationDetails> getHELRegistryOrganizations(@RequestParam(value = "registryType", required = false) Integer registryType,
	@RequestParam(value = "tierLevel", required = false) Integer tierLevel) throws Exception {
	
	if(registryType == null) {
	    registryType = 1;
	}
        
	if(registryType == 1) {
	
	    tiers lastRegistryTier = tiermanager.getLastTier();

	    if(lastRegistryTier != null) {
		List<tierOrganizationDetails> tierOrganizations = tiermanager.getTierEntries(lastRegistryTier.getId());
                
		if(tierOrganizations != null) {
		    return tierOrganizations;
		}
		else {
		    return null;
		}
	    }
	    else {
		return null;
	    }
	}
	else {
            
	    //Get FP Tier orgs bt passed in Tier level (2 or 3)
	    programOrgHierarchy tierLevelOrgs = tiermanager.getsFPRegistryTierBydspPos(tierLevel);
	    
	    List<tierOrganizationDetails> tierOrgs = new ArrayList<>();
	    
	    if(tierLevelOrgs != null) {
		List<programOrgHierarchyDetails> fpOrgs = tiermanager.getFPRegistryTierEntries(tierLevelOrgs.getId());
                
		if(fpOrgs != null) {
		    
		    for(programOrgHierarchyDetails org : fpOrgs) {
			tierOrganizationDetails tierOrganization  = new tierOrganizationDetails();
			
			tierOrganization.setId(org.getId());
			tierOrganization.setName(org.getName());
			tierOrganization.setAddress(org.getAddress());
			tierOrganization.setAddress2(org.getAddress2());
			tierOrganization.setAltDisplayId(org.getAltDisplayId());
			tierOrganization.setCity(org.getCity());
			tierOrganization.setCounty(org.getCounty());
			tierOrganization.setDisplayId(org.getDisplayId());
			tierOrganization.setEmail(org.getEmail());
			tierOrganization.setZipCode(org.getZipCode());
			tierOrganization.setPhoneNumber(org.getPhoneNumber());
			tierOrganization.setPrimaryContactName(org.getPrimaryContactName());
			tierOrganization.setPrimaryContactEmail(org.getPrimaryContactEmail());
			tierOrganization.setPrimaryContactPhone(org.getPrimaryContactPhone());
			tierOrganization.setTechnicalContactName(org.getTechnicalContactName());
			tierOrganization.setTechnicalContactPhoneNumber(org.getTechnicalContactPhoneNumber());
			tierOrganization.setTechnicalContactEmail(org.getTechnicalContactEmail());
			
			tierOrgs.add(tierOrganization);
		    }
		}
	    }
	    
	    if(!tierOrgs.isEmpty()) {
		return tierOrgs;
	    }
	    else {
		return null;
	    }
	}
    }
    
    /**
     * The '/getHELRegistryOrganizationDetails' GET request will return the details of the selected registry
     * organization.
     *
     * @param selRegistryOrgId
     * @param registryType
     * @return The function will return the details of the selected organization
     * @throws java.lang.Exception
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = {"/{cleanURL}/getHELRegistryOrganizationDetails", "/getHELRegistryOrganizationDetails"}, method = RequestMethod.GET)
    public @ResponseBody tierOrganizationDetails getHELRegistryOrganizationDetails(
	@RequestParam(value = "selRegistryOrgId", required = true) Integer selRegistryOrgId,
	@RequestParam(value = "registryType", required = false) Integer registryType) throws Exception {
	
	if(registryType == null) {
	    registryType = 1;
	}
	
	tierOrganizationDetails orgDetails;
	
	if(registryType == 1) {
	    orgDetails = tiermanager.getTierEntryById(selRegistryOrgId);
	}
	else {
	    programOrgHierarchyDetails fpOrgDetails = tiermanager.getFPRegistryTierEntryById(selRegistryOrgId);
	    
	    orgDetails = new tierOrganizationDetails();
	    orgDetails.setId(fpOrgDetails.getId());
	    orgDetails.setName(fpOrgDetails.getName());
	    orgDetails.setAddress(fpOrgDetails.getAddress());
	    orgDetails.setAddress2(fpOrgDetails.getAddress2());
	    orgDetails.setCity(fpOrgDetails.getCity());
	    orgDetails.setState(fpOrgDetails.getState());
	    orgDetails.setZipCode(fpOrgDetails.getZipCode());
	    orgDetails.setPhoneNumber(fpOrgDetails.getPhoneNumber());
	    orgDetails.setPrimaryContactName(fpOrgDetails.getPrimaryContactName());
	    orgDetails.setPrimaryContactEmail(fpOrgDetails.getPrimaryContactEmail());
	    orgDetails.setPrimaryContactPhone(fpOrgDetails.getPrimaryContactPhone());
	    orgDetails.setTechnicalContactEmail(fpOrgDetails.getTechnicalContactEmail());
	    orgDetails.setTechnicalContactName(fpOrgDetails.getTechnicalContactName());
	    orgDetails.setTechnicalContactPhoneNumber(fpOrgDetails.getTechnicalContactPhoneNumber());
	}
	
	return orgDetails;
    }
    
    /**
     * The '/getAgenciesForReport' GET request will return a list of Health-e-Link registries based on the
     * passed in registry type
     *
     * @param registryType
     * @return The function will return a list of active health-e-link registries.
     * @throws java.lang.Exception
     */
    @RequestMapping(value = {"/getAgenciesForReport"}, method = RequestMethod.GET)
    public @ResponseBody ModelAndView getAgenciesForReport(@RequestParam(value = "registryType", required = true) Integer registryType) throws Exception {
	
	ModelAndView mav = new ModelAndView();
        mav.setViewName("/administrator/processing-activities/reportBuilder/agencyList");
	
	List<Organization> agencies = organizationManager.getAgenciesForReport(registryType);
	
	mav.addObject("agencies",agencies);
	
        return mav;
    }
}
