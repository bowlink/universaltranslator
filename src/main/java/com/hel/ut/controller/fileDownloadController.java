package com.hel.ut.controller;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hel.ut.model.Organization;
import com.hel.ut.model.custom.searchParameters;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.service.fileManager;
import com.hel.ut.service.organizationManager;
import com.hel.ut.service.userManager;
import com.hel.ut.service.utConfigurationManager;

import java.io.File;
import java.util.Properties;
import javax.annotation.Resource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/FileDownload")
public class fileDownloadController {

    @Autowired
    private organizationManager organizationManager;

    @Autowired
    private userManager usermanager;

    @Autowired
    private fileManager filemanager;
    
    @Autowired
    private utConfigurationManager utconfigurationmanager;

    @Resource(name = "myProps")
    private Properties myProps;

    /**
     * Size of a byte buffer to read/write file This decrypts the file and for users to download
     */
    private static final int BUFFER_SIZE = 4096;

    @RequestMapping(value = "/downloadFile.do", method = RequestMethod.GET)
    public ModelAndView downloadFile(HttpServletRequest request, Authentication authentication,
	@RequestParam String filename,
	@RequestParam String foldername,
	@RequestParam(value = "orgId", required = false) Integer orgId,
	@RequestParam(value = "utBatchName", required = false) String utBatchName,
	@RequestParam(value = "fromPage", required = false) String fromPage,
	@RequestParam(value = "utBatchId", required = false) String utBatchId,
	@RequestParam(value = "cwId", required = false) Integer cwId,
	@RequestParam(value = "delim", required = false) Integer delim,
	HttpServletResponse response, RedirectAttributes redirectAttr, HttpSession session) throws Exception {
	
	String desc = "";
	
	//Check to see if foldername is Base64 encoded
	if("config".equals(fromPage) && Base64.isBase64(foldername)) {
	    byte[] decodedBytes = Base64.decodeBase64(foldername);
	    foldername = new String(decodedBytes);
	}
	
	try {

	    utUser userDetails = usermanager.getUserByUserName(authentication.getName());

	    //tracking 
	    utUserActivity ua = new utUserActivity();
	    ua.setUserId(userDetails.getId());
	    ua.setAccessMethod(request.getMethod());
	    ua.setPageAccess("/downloadFile.do"); 
	    if(cwId != null) {
		ua.setActivity("Downloaded Crosswalk");
		desc = "Crowsswalk Id: " + cwId;
	    }
	    else {
		ua.setActivity("Downloaded File");
		desc = foldername + filename;
		if (orgId != null) {
		    desc = orgId.toString() + "_" + foldername + filename;
		}
	    }
	    ua.setActivityDesc(desc);
	    usermanager.insertUserLog(ua);
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.err.println("Error tracking file downloaded " + desc);

	}
	OutputStream outputStream = null;
	InputStream in = null;
	ServletContext context = request.getServletContext();
	String errorMessage = "";
	
	boolean fileExists = false;

	try {
	    
	    if(cwId != null) {
		File f = utconfigurationmanager.generateCrosswalkTempDownloadFile(cwId, delim);
		
		in = new FileInputStream(f.getAbsolutePath());

		downloadfile(f, in, "text/plain", f.getName(), "", response, outputStream);

		return null;
	    }
	    else {
		String directory;
	    
	    Organization organization = null;
	    String cleanURL = "";

	    if (orgId != null && orgId > 0) {
		organization = organizationManager.getOrganizationById(orgId);
		cleanURL = organization.getcleanURL();
		
		if("archivesOut".equals(foldername)) {
		    directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/output files/";
		}
		else if ("input files".equals(foldername)) {
		     directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/input files/";
		}
		else {
		    directory = myProps.getProperty("ut.directory.utRootDir") + foldername + "/";
		}
	    } else {
		directory = myProps.getProperty("ut.directory.utRootDir") + foldername + "/";
	    }

	    String mimeType = "";
	    String actualFileName = "";

	    File f = new File(directory + filename);
	   
	    if(!f.exists() && "archivesIn".equals(foldername) && !"".equals(cleanURL)) {
		directory = myProps.getProperty("ut.directory.utRootDir") + cleanURL + "/input files/";
		filename = filename.replace("archive_","encoded_");
		f = new File(directory + filename);
	    }
	    if(!f.exists() && "archivesOut".equals(foldername)) {
		 directory = myProps.getProperty("ut.directory.utRootDir") + foldername + "/";
		 f = new File(directory + filename);
		 if(!f.exists()) {
		    f = new File(directory + utBatchId + ".txt");
		 }
	    }
	    else if(!f.exists() && foldername.contains("/crosswalks")) {
		directory = myProps.getProperty("ut.directory.utRootDir") + "libraryFiles/crosswalks/";
		f = new File(directory + filename);
		
		if(!f.exists()) {
		    directory = myProps.getProperty("ut.directory.utRootDir") + "libraryFiles/";
		    f = new File(directory + filename);
		}
	    }
	    
	    if (utBatchName != null || utBatchId != null) {
		
		if (!f.exists() && (utBatchName != null && !"".equals(utBatchName))) {
		    
		    f = new File(directory + utBatchName);
		    
		    if (f.exists()) {
			fileExists = true;
			mimeType = context.getMimeType(directory + utBatchName);
			//we don't know when a file is encoding or decoding without having to do queries, it will be easy to try to decode first
			in = new FileInputStream(directory + utBatchName);
			
			actualFileName = utBatchName;
		    } 
		    else if (!f.exists() && "txt".equals(FilenameUtils.getExtension(utBatchName))) {
			utBatchName = utBatchName.replace("txt", FilenameUtils.getExtension(filename));

			f = new File(directory + utBatchName);
			if (f.exists()) {
			    fileExists = true;
			    mimeType = context.getMimeType(directory + utBatchName);
			    in = new FileInputStream(directory + utBatchName);
			    
			    actualFileName = utBatchName;
			}
		    }
		    else if (!f.exists() && "".equals(FilenameUtils.getExtension(utBatchName))) {
			utBatchName = utBatchName + ".txt";

			f = new File(directory + utBatchName);
			if (f.exists()) {
			    fileExists = true;
			    mimeType = context.getMimeType(directory + utBatchName);
			    in = new FileInputStream(directory + utBatchName);
			    
			    actualFileName = utBatchName;
			}
		    }
		} 
		else if (!f.exists() && (utBatchId != null && !"".equals(utBatchId))) {
		    
		    f = new File(directory + utBatchId);
		    
		    if (f.exists()) {
			fileExists = true;
			mimeType = context.getMimeType(directory + utBatchId);
			//we don't know when a file is encoding or decoding without having to do queries, it will be easy to try to decode first
			in = new FileInputStream(directory + utBatchId);
			
			actualFileName = utBatchId;
		    } 
		    else if (!f.exists() && "txt".equals(FilenameUtils.getExtension(utBatchName))) {
			utBatchId = utBatchName.replace("txt", FilenameUtils.getExtension(filename));

			f = new File(directory + utBatchId);
			if (f.exists()) {
			    fileExists = true;
			    mimeType = context.getMimeType(directory + utBatchId);
			    in = new FileInputStream(directory + utBatchId);
			    
			    actualFileName = utBatchId;
			}
		    }
		    else if (!f.exists() && "".equals(FilenameUtils.getExtension(utBatchId))) {
			utBatchId = utBatchId + ".txt";

			f = new File(directory + utBatchId);
			if (f.exists()) {
			    fileExists = true;
			    mimeType = context.getMimeType(directory + utBatchId);
			    in = new FileInputStream(directory + utBatchId);
			    
			    actualFileName = utBatchId;
			}
		    }
		} 
		else {
		    if(f.exists()) {
			fileExists = true;
			mimeType = context.getMimeType(directory + filename);
			in = new FileInputStream(directory + filename);
		    
			actualFileName = filename;
		    }
		}
	    } 
	    else {
		if(f.exists()) {
		    fileExists = true;
		    mimeType = context.getMimeType(directory + filename);
		    in = new FileInputStream(directory + filename);

		    actualFileName = filename;
		}
	    }
	    
	    if(fileExists) {
		downloadfile(f, in, mimeType, actualFileName, directory, response, outputStream);
		return null;

	    }
	    else {
		
		redirectAttr.addFlashAttribute("error", "missing");
		ModelAndView mav = null;
		if(fromPage != null) {
		    if(!"".equals(fromPage)) {
			if("inbound".equals(fromPage)) {
			   searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters"); 
			   searchParameters.setsearchTerm(FilenameUtils.removeExtension(f.getName()).replace("archive_", "").replace("encoded_", "").replace("_dec", ""));
			   mav = new ModelAndView(new RedirectView("/administrator/processing-activity/inbound")); 
			}
			else if("outbound".equals(fromPage)) {
			   searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters"); 
			   searchParameters.setsearchTerm(FilenameUtils.removeExtension(f.getName()).replace("archive_", "").replace("encoded_", "").replace("_dec", ""));
			   mav = new ModelAndView(new RedirectView("/administrator/processing-activity/outbound")); 
			}
			else if("invalidin".equals(fromPage)) {
			   searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters"); 
			   searchParameters.setsearchTerm(FilenameUtils.removeExtension(f.getName()).replace("archive_", "").replace("encoded_", "").replace("_dec", ""));
			   mav = new ModelAndView(new RedirectView("/administrator/processing-activity/invalidIn")); 
			}
			else if("rejected".equals(fromPage)) {
			   searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters"); 
			   searchParameters.setsearchTerm(FilenameUtils.removeExtension(f.getName()).replace("archive_", "").replace("encoded_", "").replace("_dec", ""));
			   mav = new ModelAndView(new RedirectView("/administrator/processing-activity/rejected")); 
			}
			else if("invalidOut".equals(fromPage)) {
			   searchParameters searchParameters = (searchParameters) session.getAttribute("searchParameters"); 
			   searchParameters.setsearchTerm(FilenameUtils.removeExtension(f.getName()).replace("archive_", "").replace("encoded_", "").replace("_dec", ""));
			   mav = new ModelAndView(new RedirectView("/administrator/processing-activity/invalidOut")); 
			}
			else if("inboundAudit".equals(fromPage)) {
			   mav = new ModelAndView(new RedirectView("/administrator/processing-activity/inbound/auditReport/"+utBatchId)); 
			}
			else if("outboundAudit".equals(fromPage)) {
			   mav = new ModelAndView(new RedirectView("/administrator/processing-activity/outbound/auditReport/"+utBatchId)); 
			}
			else if("config".equals(fromPage)) {
			   mav = new ModelAndView(new RedirectView("/administrator/configurations/translations"));
			   
			   //Check to see if the file is a crosswalk, if it is lets create the crosswalk
			   if(foldername != null) {
			       if(!"".equals(foldername)) {
				   if(foldername.contains("crosswalks")) {
					utconfigurationmanager.generateMissingCrosswalk(foldername.split("/")[0],filename);
					
					directory = myProps.getProperty("ut.directory.utRootDir") + foldername.split("/")[0] + "/crosswalks/";
					
					f = new File(directory + filename);
					
					mimeType = context.getMimeType(directory + filename);
					in = new FileInputStream(directory + filename);

					actualFileName = filename;
					
					downloadfile(f, in, mimeType, actualFileName, directory, response, outputStream);

					return null;
				   }
			       }
			   }
			}
			else if("messagespec".equals(fromPage)) {
			   mav = new ModelAndView(new RedirectView("/administrator/configurations/messagespecs")); 
			}
			else if("transport".equals(fromPage)) {
			   mav = new ModelAndView(new RedirectView("/administrator/configurations/transport")); 
			}
			else if("crosswalks".equals(fromPage)) {
			   mav = new ModelAndView(new RedirectView("/administrator/sysadmin/crosswalks")); 
			   
			   //Check to see if the file is a crosswalk, if it is lets create the crosswalk
			   if(foldername != null) {
			       if(!"".equals(foldername)) {
				    utconfigurationmanager.generateMissingCrosswalk(foldername,filename);

				    directory = myProps.getProperty("ut.directory.utRootDir") + foldername + "/crosswalks/";

				    f = new File(directory + filename);

				    mimeType = context.getMimeType(directory + filename);
				    in = new FileInputStream(directory + filename);

				    actualFileName = filename;

				    downloadfile(f, in, mimeType, actualFileName, directory, response, outputStream);

					return null;
				   }
			       }
			    }
			}
		    }

		    if(mav == null) {
			mav = new ModelAndView(new RedirectView("/administrator/processing-activity/inbound/"));
		    }
		    return mav;
		}
	    }
	    
	} catch (FileNotFoundException e) {
	    errorMessage = errorMessage + "<br/>" + e.getMessage();

	} catch (IOException e) {
	    errorMessage = e.getMessage();
	} finally {
	    if (null != in) {
		try {
		    in.close();
		} catch (IOException e) {
		    errorMessage = errorMessage + "<br/>" + e.getMessage();
		}
	    }
	}

	/**
	 * throw error message here because want to make sure file stream is closed *
	 */
	if (!errorMessage.equalsIgnoreCase("")) {
	    throw new Exception(errorMessage);
	}
	
	return null;
    }
    
    private void downloadfile(File f, InputStream in, String mimeType, String actualFileName, String directory, HttpServletResponse response, OutputStream outputStream) throws Exception {
	
	if (mimeType == null) {
	    // set to binary type if MIME mapping not found
	    mimeType = "application/octet-stream";
	}
	response.setContentType(mimeType);

	byte[] buffer = new byte[BUFFER_SIZE];
	int bytesRead = 0;

	response.setHeader("Content-Transfer-Encoding", "binary");
	response.setHeader("Content-Disposition", "attachment;filename=\"" + actualFileName + "\"");

	outputStream = response.getOutputStream();
	
	try {
	    boolean isBase64Encoded = false;
	    
	    if(!"".equals(directory)) {
		//Check if the file is base64 Encoded
		isBase64Encoded = filemanager.isFileBase64Encoded(new File(directory + actualFileName),"");
	    }

	    if(isBase64Encoded) {
		byte[] fileAsBytes = filemanager.loadFileAsBytesArray(directory + actualFileName);
		byte[] decodedBytes = Base64.decodeBase64(fileAsBytes);
		String decodedString = new String(decodedBytes);
		response.setContentLength((int) decodedString.length());
                
                if(actualFileName.contains(".xls") || actualFileName.contains(".xlsx")) {
                    outputStream.write(decodedBytes);
                }
                else {
                    outputStream.write(decodedString.getBytes());
                }
		in.close();
		outputStream.close();
	    }
	    else {
		response.setContentLength((int) f.length());
		while (0 < (bytesRead = in.read(buffer))) {
		    outputStream.write(buffer, 0, bytesRead);
		}
		in.close();
		outputStream.close();
	    }
	}
	catch (Exception ex) {
	    response.setContentLength((int) f.length());
	    while (0 < (bytesRead = in.read(buffer))) {
		outputStream.write(buffer, 0, bytesRead);
	    }
	    in.close();
	    outputStream.close();
	}
    }
}

