/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service;

import com.hel.ut.model.Organization;
import com.hel.ut.model.batchUploads;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monitorjbl.xlsx.StreamingReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.Resource;

/**
 *
 * @author gchan
 */
@Service
public class excelToTxt {

    @Autowired
    private organizationManager organizationmanager;

    @Autowired
    private transactionInManager transactioninmanager;
    
    @Resource(name = "myProps")
    private Properties myProps;

    @SuppressWarnings("deprecation")
	public String TranslateXLSXtoTxt(String fileLocation, String excelFileName, batchUploads batch) throws Exception {

	Organization orgDetails = organizationmanager.getOrganizationById(batch.getOrgId());
	
	String directory = myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL() + "/loadFiles/";
	
	// Get the uploaded xlsx File
	directory = fileLocation;
	
	String excelFile = (excelFileName + ".xlsx");
	
	// Create the txt file that will hold the excel fields
	String newfileName = (excelFileName + ".txt");

	File newFile = new File(directory + newfileName);
	File inputFile = new File(directory + excelFile);
	
	if (newFile.exists()) {
	    try {
		if (newFile.exists()) {
		    int i = 1;
		    while (newFile.exists()) {
			int iDot = newfileName.lastIndexOf(".");
			newFile = new File(directory + newfileName.substring(0, iDot) + "(" + ++i + ")" + newfileName.substring(iDot));
		    }
		    newfileName = newFile.getName();
		    newFile.createNewFile();
		} 
                else {
		    newFile.createNewFile();
		}
	    } 
            catch (Exception e) {
		e.printStackTrace();
	    }
	} 
        else {
	    newFile.createNewFile();
	    newfileName = newFile.getName();
	}

	try {
	    FileWriter fw = new FileWriter(newFile);
	    InputStream is = new FileInputStream(inputFile);
	    Workbook workbook = StreamingReader.builder()
            .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
            .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
            .open(is);            // InputStream or File for XLSX file (required)

	    Sheet datatypeSheet = workbook.getSheetAt(0);

	    DataFormatter formatter = new DataFormatter();
	    int  writeRow = 0;
	    boolean hasFormulaCell = false;
	    boolean hasErrorCell = false;
	    String cellErrorLocation = "";
	    String formulaErrorLocation = "";
            
	    for(Row row : datatypeSheet) {
	    	String string = "";
		
	    	for(int cn=0; cn<row.getLastCellNum(); cn++) {
                    // If the cell is missing from the file, generate a blank one
                    // (Works by specifying a MissingCellPolicy)
                    Cell cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String text = "";
                    
                    //need to review cells for formula and reject entire file
                    if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
                        //formula
                        if (cell.getCellTypeEnum() == CellType.FORMULA) {			
                            hasFormulaCell = true;
                            text = "FORMULA";
                            int errorRow  = row.getRowNum()+1;
                            int errorCell = cn + 1;
                            formulaErrorLocation = "row " + errorRow + ", cell " + errorCell;
                        } 
                        else if (cell.getCellTypeEnum() == CellType.ERROR) {
                            hasErrorCell = true;
                            text = "CELL ERROR";
                            int errorRow  = row.getRowNum()+1;
                            int errorCell = cn + 1;
                            cellErrorLocation = "row " + errorRow + ", cell " + errorCell;
			 } 
                        else {
                            text = formatter.formatCellValue(cell);
                        }
                    } 
                    
                    //handle error cells
                    string = string + text.trim() + batch.getDelimChar();
                    if (hasFormulaCell || hasErrorCell) {
                        break;
                    }
		 }
                
                // check to see if row is blank
                String stringRemoveEmptyRows = string.replaceAll("(?m)^[ \t]*\r?\n", "");
                if (stringRemoveEmptyRows.trim().length() > 0) {
                    writeRow ++;
                    if (writeRow == 1) {
                        fw.write(stringRemoveEmptyRows);
                    } 
                    else {
                        fw.write(System.getProperty("line.separator") + stringRemoveEmptyRows);
                    }
                }
                if (hasFormulaCell || hasErrorCell) {
                    break;
                }
	    }

	    workbook.close();
	    is.close();
	    fw.close();
	    
	    if (hasFormulaCell) {
	    	newfileName = "Formula error in " + formulaErrorLocation;
	    	transactioninmanager.insertProcessingError(22, batch.getConfigId(), batch.getId(), 1, null, null, null, true, false, newfileName);
	    } 
            else if (hasErrorCell) {
	    	newfileName = "Cell error in " + cellErrorLocation;
	    	transactioninmanager.insertProcessingError(22, batch.getConfigId(), batch.getId(), 1, null, null, null, true, false, (newfileName + ". #DIV/0!, #N/A, #NAME?, #NULL!, #NUM!, #REF!, #VALUE! are invalid values."));
	    }
	} 
        catch (Exception ex) {
	    ex.printStackTrace();
	    newfileName = "ERRORERRORERROR";
	    PrintStream ps = new PrintStream(newFile);
	    ex.printStackTrace(ps);
	    ps.close();
	    transactioninmanager.insertProcessingError(5, batch.getConfigId(), batch.getId(), 1, null, null, null,true, false, ex.getStackTrace().toString());
	}
	return newfileName;
    }
}