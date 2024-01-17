package com.hel.ut.dao;

import com.hel.ut.model.CrosswalkData;
import java.util.List;

import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.validationType;
import org.springframework.stereotype.Repository;

@Repository
public interface messageTypeDAO {

    @SuppressWarnings("rawtypes")
    List getInformationTables();

    @SuppressWarnings("rawtypes")
    List getAllTables();

    @SuppressWarnings("rawtypes")
    List getTableColumns(String tableName);

    @SuppressWarnings("rawtypes")
    List getValidationTypes();

    String getValidationById(int id);

    @SuppressWarnings("rawtypes")
    List getDelimiters();

    Long getTotalFields(int messageTypeId);

    List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId);

    Integer createCrosswalk(Crosswalks crosswalkDetails);

    Long checkCrosswalkName(String name, int orgId);

    double findTotalCrosswalks(int orgId);

    Crosswalks getCrosswalk(int cwId);

    @SuppressWarnings("rawtypes")
    List getCrosswalkData(int cwId);

    String getFieldName(int fieldId);

    String getCrosswalkName(int cwId);

    String getDelimiterChar(int id);

    List<validationType> getValidationTypes1();

    void executeSQLStatement(String sqlStmt);
    
    void updateCrosswalk(Crosswalks crosswalkDetails);
    
    List getCrosswalksWithData(Integer orgId);
    
    List<Crosswalks> getCrosswalksForConfig(int page, int maxCrosswalks, int orgId, int configId, boolean inUseOnly);
    
    List getCrosswalksWithDataByFileName(Integer orgId, String fileName);
    
    List getConfigCrosswalksWithData(Integer orgId, Integer configId);
    
    String getDelimiterById(int id);
    
    void saveCrosswalkData(CrosswalkData cwData);
    
    Crosswalks getCrosswalkByNameAndOrg(String cwName, Integer orgId, String fileName);
    
    List<Crosswalks> getCrosswalksForConfigAndOrg(Integer orgId, Integer configId);
    
    List<Crosswalks> getCrosswalksForConfigToBeCopied(Integer configId, Integer newOrgId);
    
    List getConfigCrosswalksWithDataForPrint(Integer configId);
    
    List getConfigCrosswalkDownloadWithData(Integer crosswalkId);
    
    String checkIfCWIsInUse(Integer crosswalkId);
}
