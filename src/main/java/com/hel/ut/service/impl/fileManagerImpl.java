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
import org.springframework.stereotype.Service;
import com.hel.ut.service.fileManager;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class fileManagerImpl implements fileManager {

    public String encodeFileToBase64Binary(File file) throws IOException {
        try {
            byte[] fileBytes = fileToBytes(file);
            String encodedString = Base64.getEncoder().encodeToString(fileBytes);

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
            
            byte[] decoded = Base64.getDecoder().decode(bytes);
            
            String decodedString = null;
            
            if(new String(bytes).startsWith("//")) {
                decodedString = new String(decoded, StandardCharsets.UTF_16);
            }
            else if(new String(bytes).startsWith("/")) {
                decodedString = new String(decoded, StandardCharsets.UTF_16);
            }
            else {
                decodedString = new String(decoded);
            }
            
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
            
            Path path = Paths.get(file.getAbsolutePath());
            
            byte[] fileBytes = Files.readAllBytes(path);
            
            return fileBytes;

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
	
        byte[] fileAsBytes = fileToBytes(new File(sourceFile));
	byte[] decodedBytes = Base64.getDecoder().decode(fileAsBytes);
	
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
	
	boolean isEncoded = false;
	
	String fileContents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        
        boolean base64DecoderTest = false;
        boolean base64CommonsCodecTest = false;
        boolean base64RegExTest = false;
        
        if(fileContents == null) {
            return false;
        }
        else {
            if("".equals(fileContents)) {
                return false;
            }
            else {
                
                try {
                    Base64.getDecoder().decode(fileContents);
                    base64DecoderTest =  true;
                } catch (IllegalArgumentException e) {
                    base64DecoderTest =  false;
                }
                
                try {
                    base64CommonsCodecTest = org.apache.commons.codec.binary.Base64.isBase64(fileContents);
                } catch (IllegalArgumentException e) {
                    base64CommonsCodecTest =  false;
                }
                
                base64RegExTest = fileContents.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
                
                Integer testWeight = 0;
                
                if(base64DecoderTest) {
                    testWeight += 1;
                }
                
                if(base64CommonsCodecTest) {
                    testWeight += 1;
                }
                
                if(base64RegExTest) {
                    testWeight += 1;
                }
                
                if(testWeight > 1) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
    }
}
