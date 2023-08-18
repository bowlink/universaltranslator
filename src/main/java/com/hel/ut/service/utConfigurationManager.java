package com.hel.ut.service;

import java.util.List;

import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.HL7Details;
import com.hel.ut.model.HL7ElementComponents;
import com.hel.ut.model.HL7Elements;
import com.hel.ut.model.HL7Segments;
import com.hel.ut.model.Macros;
import com.hel.ut.model.Organization;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationCCDElements;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionReceivers;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.configurationDataTranslations;
import com.hel.ut.model.configurationExcelDetails;
import com.hel.ut.model.configurationMessageSpecs;
import com.hel.ut.model.configurationSchedules;
import com.hel.ut.model.configurationTransport;
import com.hel.ut.model.configurationUpdateLogs;
import com.hel.ut.model.watchlist;
import com.hel.ut.model.watchlistEntry;
import java.io.File;
import java.util.Date;
import javax.servlet.http.HttpSession;

public interface utConfigurationManager {

    Integer createConfiguration(utConfiguration configuration);

    void updateConfiguration(utConfiguration configuration);

    utConfiguration getConfigurationById(int configId);

    List<utConfiguration> getConfigurationsByOrgId(int orgId, String searchTerm);

    List<utConfiguration> getActiveConfigurationsByOrgId(int orgId);

    utConfiguration getConfigurationByName(String configName, int orgId);

    List<utConfiguration> getConfigurations();

    List<utConfiguration> getLatestConfigurations(int maxResults);

    Long findTotalConfigs();

    Long getTotalConnections(int configId);

    void updateCompletedSteps(int configId, int stepCompleted);

    @SuppressWarnings("rawtypes")
    List getFileTypes();

    String getFileTypesById(int id);

    List<configurationDataTranslations> getDataTranslations(int configId);

    List<configurationDataTranslations> getDataTranslationsWithFieldNo(int configId, int categoryId);

    String getFieldName(int fieldId);

    void deleteDataTranslations(int configId, int categoryId);

    void saveDataTranslations(configurationDataTranslations translations);

    List<Macros> getMacrosByCategory(int categoryId);

    List<Macros> getMacros();

    Macros getMacroById(int macroId);

    List<configurationConnection> getAllConnections();

    List<configurationConnection> getLatestConnections(int maxResults);

    List<configurationConnection> getConnectionsByConfiguration(int configId, int userId);

    List<configurationConnection> getConnectionsByTargetConfiguration(int configId);

    Integer saveConnection(configurationConnection connection);

    configurationConnection getConnection(int connectionId);

    void updateConnection(configurationConnection connection);

    configurationSchedules getScheduleDetails(int configId);

    void saveSchedule(configurationSchedules scheduleDetails);

    configurationMessageSpecs getMessageSpecs(int configId);

    void updateMessageSpecs(configurationMessageSpecs messageSpecs, int transportDetailId, int fileType, boolean hasHeader, Integer fileLayout) throws Exception;

    List<utConfiguration> getActiveConfigurationsByUserId(int userId, int transportMethod) throws Exception;

    List<configurationConnectionSenders> getConnectionSenders(int connectionId);

    List<configurationConnectionReceivers> getConnectionReceivers(int connectionId);

    void saveConnectionSenders(configurationConnectionSenders senders);

    void saveConnectionReceivers(configurationConnectionReceivers receivers);

    void removeConnectionSenders(int connectionId);

    void removeConnectionReceivers(int connectionId);
    
    void removeConnection(int connectionId);

    List<CrosswalkData> getCrosswalkData(int cwId);

    HL7Details getHL7Details(int configId);

    List<HL7Segments> getHL7Segments(int hl7Id);

    List<HL7Elements> getHL7Elements(int hl7Id, int segmentId);

    List<HL7ElementComponents> getHL7ElementComponents(int elementId);

    void updateHL7Details(HL7Details details);

    void updateHL7Segments(HL7Segments segment);

    void updateHL7Elements(HL7Elements element);

    void updateHL7ElementComponent(HL7ElementComponents component);

    int saveHL7Details(HL7Details details);

    int saveHL7Segment(HL7Segments newSegment);

    int saveHL7Element(HL7Elements newElement);

    void saveHL7Component(HL7ElementComponents newcomponent);

    String getMessageTypeNameByConfigId(Integer configId);

    @SuppressWarnings("rawtypes")
    List getEncodings();

    void removeHL7ElementComponent(Integer componentId);

    void removeHL7Element(Integer elementId);

    void removeHL7Segment(Integer segmentId);

    List<configurationCCDElements> getCCDElements(Integer configId) throws Exception;

    void saveCCDElement(configurationCCDElements ccdElement) throws Exception;

    configurationCCDElements getCCDElement(Integer elementId) throws Exception;

    configurationExcelDetails getExcelDetails(Integer configId, Integer orgId) throws Exception;
    
    void updateExcelConfigDetails(Integer orgId, configurationMessageSpecs messageSpecs) throws Exception;
    
    Integer getFieldCrosswalkIdByFieldName (int configId, String fieldName) throws Exception;
    
    List<utConfiguration> getActiveConfigurationsByTransportType(int userId, List<Integer> transportMethods) throws Exception;
    
    @SuppressWarnings("rawtypes")
    List getZipTypes();
    
    @SuppressWarnings("rawtypes")
    List getrestAPITypes();
    
    List<configurationConnection> getConnectionsBySrcAndTargetConfigurations(int sourceConfigId, int targetConfigId);
    
    @SuppressWarnings("rawtypes")
    List getrestAPIFunctions(Integer orgId);
    
    List<watchlist> getDashboardWatchList() throws Exception;
    
    watchlist getDashboardWatchListById(int watchId) throws Exception;
    
    Integer saveDashboardWatchListEntry(watchlist watchListEntry);
    
    void updateDashboardWatchListEntry(watchlist watchListEntry);
    
    void CheckDashboardWatchList() throws Exception;
    
    List<watchlistEntry> getWatchListEntries(Date fromDate, Date toDate) throws Exception;
    
    void updateMessageSpecs(configurationMessageSpecs messageSpecs) throws Exception;
    
    void deleteWatchEntry(Integer watchId) throws Exception;
    
    List<watchlistEntry> getGenericWatchListEntries(Date fromDate, Date toDate) throws Exception;
    
    void insertDashboardWatchListEntry(watchlistEntry watchListEntry);
    
    watchlistEntry getWatchListEntry(Integer entryId) throws Exception;
    
    List<utConfiguration>  getAllActiveSourceConfigurations() throws Exception;
    
    List<configurationConnection> getConnectionsBySourceConfiguration(Integer configId);
    
    List<utConfiguration>  getAllSourceConfigurations() throws Exception;
    
    List<utConfiguration>  getAllTargetConfigurations() throws Exception;
    
    List getDataTranslationsForDownload (Integer configId) throws Exception;
  
    List getCrosswalksForDownload (Integer configId, boolean inUseOnly) throws Exception;
    
    StringBuffer printDetailsSection(utConfiguration configDetails, Organization orgDetails, String siteTimeZone) throws Exception;
    
    StringBuffer printTransportMethodSection(utConfiguration configDetails) throws Exception;
    
    StringBuffer printMessageSpecsSection(utConfiguration configDetails) throws Exception;
    
    StringBuffer printFieldSettingsSection(utConfiguration configDetails) throws Exception;
    
    StringBuffer printDataTranslationsSection(utConfiguration configDetails, String siteTimeZone) throws Exception;
    
    StringBuffer printConnectionDetails(utConfiguration srcconfigDetails,utConfiguration tgtconfigDetails) throws Exception;
    
    void updateConfigurationDirectories(List<Integer> configIds, String oldCleanURL, String newCleanURL) throws Exception;
    
    void generateMissingCrosswalk(String cleanURL, String fileName) throws Exception;
    
    void saveConfigurationUpdateLog(configurationUpdateLogs updateLog) throws Exception;
    
    void checkForUnusedFolders() throws Exception;
    
    List<configurationUpdateLogs> getConfigurationUpdateLogs(Integer configId) throws Exception;
    
    configurationUpdateLogs getConfigurationUpdateLog(Integer noteId) throws Exception;
    
    void updateConfigurationUpdateLog(configurationUpdateLogs updateLog) throws Exception;
    
    void deletConfigurationNote(Integer noteId) throws Exception;
    
    StringBuffer printConfigurationNotesSection(utConfiguration configDetails, String siteTimeZone) throws Exception;
    
    void deleteConfigurationFTPInformation(int transportId) throws Exception;
    
    StringBuffer exportConfigOrgSection(Organization orgDetails) throws Exception;
    
    StringBuffer exportConfigDetailsSection(utConfiguration configDetails) throws Exception;
    
    StringBuffer exportConfigTransportSection(HttpSession session,configurationTransport transportDetails) throws Exception;
    
    StringBuffer exportConfigMessageSpecSection(HttpSession session,Integer configId) throws Exception;
    
    StringBuffer exportConfigFieldsSection(Integer configId, Integer transportDetailsId) throws Exception;
    
    StringBuffer exportConfigSchedulingSection(Integer configId) throws Exception;
    
    StringBuffer exportOrgCrosswalks(Integer orgId, Integer configId) throws Exception;
    
    StringBuffer exportConnectionSrcDetails(utConfiguration configDetails,Organization orgDetails) throws Exception;
    
    StringBuffer exportConnectionTgtDetails(utConfiguration configDetails,Organization orgDetails) throws Exception;
    
    StringBuffer exportConnectionFields(Integer connectionId) throws Exception;
    
    File generateCrosswalkTempDownloadFile(Integer crosswalkId, Integer delim) throws Exception;
    
    configurationDataTranslations getDataTranslationById(Integer translationId) throws Exception;
    
    void executeSQLStatement(String sqlStatement) throws Exception;

List<configurationConnection> getAllConnectionsSingleQuery();
}
