package com.hel.ut.security;

import com.hel.ut.model.Organization;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.custom.searchParameters;
import com.hel.ut.service.organizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Set;

import com.hel.ut.service.userManager;
import java.util.List;
import javax.servlet.http.HttpSession;
import com.hel.ut.service.utConfigurationManager;
import com.registryKit.messenger.emailManager;
import java.util.Random;

public class CustomAuthenticationHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private userManager usermanager;
    
    @Autowired
    private emailManager emailmanager;

    @Autowired
    private organizationManager organizationManager;
    
    @Autowired
    private utConfigurationManager configurationManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        String userTargetUrl = "/profile";
	String adminTargetUrl = "/administrator";
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        /**
         * log the admin who logged in as user*
         */
        // System.out.println(request.getParameter("j_username"));
        //we log here 
        /* Need to get the userId */
        utUser userDetails = usermanager.getUserByUserName(authentication.getName());

        if (!request.getParameter("username").equalsIgnoreCase(authentication.getName())) {
            try {
                //log user activity
                utUser userLogDetails = usermanager.getUserByUserName(request.getParameter("username"));
                utUserActivity ua = new utUserActivity();
                ua.setUserId(userLogDetails.getId());
                ua.setFeatureId(0);
                ua.setAccessMethod("POST");
                ua.setPageAccess("/login");
                ua.setActivity("Login As User");
                ua.setActivityDesc("Login as user - " + userDetails.getUsername() + ".  Id - " + userDetails.getId());
                usermanager.insertUserLog(ua);
            } catch (Exception ex) {
                System.err.println("Login Handler = error logging user " + ex.getCause());
                ex.printStackTrace();
            }
        }

        if (roles.contains("ROLE_VALIDATE")) {
	    HttpSession session = request.getSession();

            searchParameters searchParameters = new searchParameters();

            /* Need to store the search session object */
            session.setAttribute("searchParameters", searchParameters);

            /* Need to store the user object in session */
            session.setAttribute("userDetails", userDetails);
	    
	    //Generate Two-Factor authentication code
	    Random r = new Random(System.currentTimeMillis());

	    Integer code = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
	    session.removeAttribute("2FactorCode");
	    session.setAttribute("2FactorCode", code);
	    
	    //Send generated two-factor code to users email address
	    try {
		emailmanager.sendTwoFactor(code, userDetails.getEmail(), "Health-e-Link Universal Translator");
		adminTargetUrl = "/authenticate";
	    }
	    catch (Exception ex) {
		session.removeAttribute("userDetails");
		session.removeAttribute("2FactorCode");
		session.invalidate();
		adminTargetUrl = "/login";
	    }

            getRedirectStrategy().sendRedirect(request, response, adminTargetUrl);
	}
	else if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_OPERATIONSSTAFF") || roles.contains("ROLE_SYSTEMADMIN")) {

            HttpSession session = request.getSession();

            searchParameters searchParameters = new searchParameters();

            /* Need to store the search session object */
            session.setAttribute("searchParameters", searchParameters);

            /* Need to store the user object in session */
            session.setAttribute("userDetails", userDetails);
	    
            getRedirectStrategy().sendRedirect(request, response, adminTargetUrl);
        } 
	else if (roles.contains("ROLE_USER")) {
            
            Organization orgDetails = organizationManager.getOrganizationById(userDetails.getOrgId());
            
            searchParameters searchParameters = new searchParameters();
            
            userDetails.setdateOrgWasCreated(orgDetails.getDateCreated());
            userDetails.setOrgType(orgDetails.getOrgType());
	    
	    /*Check to see if the user has access to download files or upload files*/
	    //Get a list of configurations
	    List<utConfiguration> configurations = configurationManager.getActiveConfigurationsByOrgId(userDetails.getOrgId());
	    
	    if(configurations != null) {
		if(configurations.size() > 0) {
		    for(utConfiguration config: configurations) {
			if(config.getType() == 1) {
			    userDetails.setUploadFiles(true);
			}
			else if(config.getType() == 2) {
			    userDetails.setDownloadFiles(true);
			}
		    }
		}
	    }
            
            HttpSession session = request.getSession();
            
            /* Need to store the user object in session */
            session.setAttribute("userDetails", userDetails);
            
            /* Need to store the search session object */
            session.setAttribute("searchParameters", searchParameters);
            
            getRedirectStrategy().sendRedirect(request, response, userTargetUrl);
	}
	else {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }
    }
}
