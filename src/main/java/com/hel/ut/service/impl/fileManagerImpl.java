package com.hel.ut.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.hel.ut.service.fileManager;
import org.apache.commons.io.FilenameUtils;

@Service
public class fileManagerImpl implements fileManager {

    public String encodeFileToBase64Binary(File file) throws IOException {
        try {
            byte[] bytes = fileToBytes(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);

            return encodedString;
        } catch (Exception ex) {
            System.err.println("encodeFileToBase64Binary -" + ex.getLocalizedMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public String decodeFileToBase64Binary(File file) throws IOException {
        try {
            byte[] bytes = fileToBytes(file);
            byte[] decoded = Base64.decodeBase64(bytes);
            String decodedString = new String(decoded);
            return decodedString;
        } catch (Exception ex) {
            System.err.println("decodeFileToBase64Binary -" + ex.getLocalizedMessage());
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("resource")
    public byte[] fileToBytes(File file) throws IOException {
        try {
            InputStream is = new FileInputStream(file);

            long length = file.length();
            byte[] bytes = new byte[(int) length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

            is.close();
            return bytes;

        } catch (Exception ex) {
            System.err.println("fileToBytes -" + ex.getLocalizedMessage());
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("resource")
    public String readTextFile(String fileName) {
        try {
            String returnValue = "";
            FileReader file = null;
            String line = "";
            try {
                file = new FileReader(fileName);
                BufferedReader reader = new BufferedReader(file);
                while ((line = reader.readLine()) != null) {
                    returnValue += line + "\n";
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found");
            } catch (IOException e) {
                throw new RuntimeException("IO Error occured");
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return returnValue;
        } catch (Exception ex) {
            System.err.println("readTextFile -" + ex.getLocalizedMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public void writeFile(String strFileName, String strFile) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(strFileName))) {
            out.write(strFile);
            out.flush();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void decode(String sourceFile, String targetFile) throws Exception {
	byte[] fileAsBytes = loadFileAsBytesArray(sourceFile);
	byte[] decodedBytes;
	
	if(Base64.isArrayByteBase64(fileAsBytes)) {
	    decodedBytes = Base64.decodeBase64(fileAsBytes);
	}
	else {
	    decodedBytes = fileAsBytes;
	}
        writeByteArraysToFile(targetFile, decodedBytes);
    }
    
    @Override
    public void copyFile(String sourceFile, String targetFile) throws Exception {
	byte[] fileAsBytes = loadFileAsBytesArray(sourceFile);
	
        writeByteArraysToFile(targetFile, fileAsBytes);
    }

    public void writeByteArraysToFile(String fileName, byte[] content) throws IOException {

        File file = new File(fileName);
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
        writer.write(content);
        writer.flush();
        writer.close();

    }

    public byte[] loadFileAsBytesArray(String fileName) throws Exception {

        File file = new File(fileName);
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;

    }
    
    public boolean isFileBase64Encoded(File file, String delimiter) throws Exception {
	
	String fileExt = FilenameUtils.getExtension(file.getName());
	
	boolean isEncoded = false;
	
	if("txt".equals(fileExt) || "csv".equals(fileExt)) {
	    try {
		//Read the first line of the file
		BufferedReader brTest = new BufferedReader(new FileReader(file));
		String firstLineText = brTest.readLine().substring(0,10);
		
		String test = new String(Base64.decodeBase64(firstLineText));

		if(firstLineText.equals(Base64.encodeBase64String(test.getBytes()))) {
		    isEncoded = true;
		}
		else {
		    isEncoded = false;
		}
		brTest.close();
	    }
	    catch(IOException ex) {
		byte[] bytes = fileToBytes(file);
		isEncoded = Base64.isBase64(bytes);
	    }
	    catch(Exception ex) {
		byte[] bytes = fileToBytes(file);
		isEncoded = Base64.isBase64(bytes);
	    }
	    
	    //Try the full line and not a subset
	    if(!isEncoded) {
		try {
		    //Read the first line of the file
		    BufferedReader brTest = new BufferedReader(new FileReader(file));
		    String firstLineText = brTest.readLine();

		    String test = new String(Base64.decodeBase64(firstLineText));

		    if(firstLineText.equals(Base64.encodeBase64String(test.getBytes()))) {
			isEncoded = true;
		    }
		    else {
			isEncoded = false;
		    }
		    brTest.close();
		}
		catch(IOException ex) {
		    isEncoded = false;
		}
		catch(Exception ex) {
		    isEncoded = false;
		}
	    }
	    
	    //If false make sure file contains correct delim
	    if(!isEncoded && !"".equals(delimiter)) {
		 BufferedReader delimTest = new BufferedReader(new FileReader(file));
		 String firstLine = delimTest.readLine();
		 
		 if("tab".equals(delimiter)) {
		    if(!firstLine.contains("\t")) {
			isEncoded = true;
		    }  
		 }
		 else if("f".equals(delimiter)) {
		     isEncoded = true;
		 }
		 else {
		    if(!firstLine.contains(delimiter)) {
			isEncoded = true;
		    } 
		 }
		 delimTest.close();
	    }
	    
	    return isEncoded;
	}
	else {
	  byte[] bytes = fileToBytes(file);
	  return Base64.isBase64(bytes);
	}
    }
}
