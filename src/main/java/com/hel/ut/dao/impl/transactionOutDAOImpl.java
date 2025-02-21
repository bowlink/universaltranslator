/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.dao.impl;

import com.hel.ut.dao.transactionOutDAO;
import com.hel.ut.model.*;
import com.hel.ut.model.custom.ConfigOutboundForInsert;
import com.hel.ut.model.custom.batchErrorSummary;
import com.hel.ut.service.transactionInManager;
import com.hel.ut.service.utConfigurationTransportManager;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.SelectionQuery;

/**
 *
 * @author chadmccue
 */
@Repository
public class transactionOutDAOImpl implements transactionOutDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private transactionInManager transactionInManager;
    
    @Autowired
    private utConfigurationTransportManager configurationTransportManager;

    //list of final status - these records we skip
    private List<Integer> transRELId = Arrays.asList(11, 12, 13, 16, 18, 20);

    private SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //list of final status - these batches are considered generated
    private List<Integer> batchFinalStatuses = Arrays.asList(28, 41, 59);
    
    private int processingSysErrorId = 5;

    /**
     * The 'submitBatchDownload' function will submit the new batch.
     *
     * @param batchDownload The object that will hold the new batch info
     *
     * @table batchDownloadd
     *
     * @return This function returns the batchId for the newly inserted batch
     */
    @Override
    @Transactional(readOnly = false)
    public Integer submitBatchDownload(batchDownloads batchDownload) {
        sessionFactory.getCurrentSession().persist(batchDownload);
        return batchDownload.getId();
    }

    /**
     * The 'findMergeableBatch' function will check for any batches created for the target org that are mergable and have not yet been picked up or viewed.
     *
     * @param orgId The id of the organization to look for.
     *
     * @return This function will return the id of a mergeable batch or 0 if no batches are found.
     */
    @Override
    @Transactional(readOnly = true)
    public int findMergeableBatch(int orgId) {
	SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("select id FROM batchDownloads where orgId = :orgId and mergeable = 1 and (statusId = 23 OR statusId = 28)");
	query.setParameter("orgId", orgId);

	Integer batchId = (Integer) query.uniqueResult();

	if (batchId == null) {
	    batchId = 0;
	}

	return batchId;
    }

    /**
     * The 'getInboxBatches' will return a list of received batches for the logged in user.
     *
     * @param userId The id of the logged in user trying to view received batches
     * @param orgId The id of the organization the user belongs to
     * @param fromDate
     * @param toDate
     *
     * @return The function will return a list of received batches
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("UnusedAssignment")
    public List<batchDownloads> getInboxBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	return findInboxBatches(userId, orgId, 0, 0, fromDate, toDate);
    }

    /**
     * The 'findInboxBatches' will return a list of received batches for the logged in user.
     *
     * @param userId The id of the logged in user trying to view received batches
     * @param orgId The id of the organization the user belongs to
     * @param fromOrgId
     * @param messageTypeId
     * @param fromDate
     * @param toDate
     *
     * @return The function will return a list of received batches
     * @throws java.lang.Exception
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("UnusedAssignment")
    public List<batchDownloads> findInboxBatches(int userId, int orgId, int fromOrgId, int messageTypeId, Date fromDate, Date toDate) throws Exception {
	int firstResult = 0;

	// Get a list of connections the user has access to
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationConnectionReceivers> criteria = builder.createQuery(configurationConnectionReceivers.class);
        Root<configurationConnectionReceivers> root = criteria.from(configurationConnectionReceivers.class);

        Predicate whereClause = builder.equal(root.get("userId"), userId);

        criteria.where(whereClause);
        
        List<configurationConnectionReceivers> userConnections = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();

	List<Integer> messageTypeList = new ArrayList<>();
	List<Integer> sourceOrgList = new ArrayList<>();

	if (userConnections.isEmpty()) {
	    messageTypeList.add(0);
	    sourceOrgList.add(0);
	} 
	else {
            
            CriteriaQuery<configurationConnection> connectionCriteria = builder.createQuery(configurationConnection.class);
            Root<configurationConnection> connectionRoot = connectionCriteria.from(configurationConnection.class);
            
            CriteriaQuery<utConfiguration> configCriteria = builder.createQuery(utConfiguration.class);
            Root<utConfiguration> configRoot = configCriteria.from(utConfiguration.class);
            
	    for (configurationConnectionReceivers userConnection : userConnections) {
                
                whereClause = builder.equal(connectionRoot.get("id"), userConnection.getConnectionId());
                connectionCriteria.where(whereClause);
                
                configurationConnection connectionInfo = (configurationConnection) sessionFactory.getCurrentSession().createQuery(connectionCriteria).uniqueResult();
                
                if(connectionInfo != null) {
                    // Get the message type for the utConfiguration 
                    whereClause = builder.equal(configRoot.get("id"), connectionInfo.getTargetConfigId());
                    configCriteria.where(whereClause);
                    
                    utConfiguration configDetails = (utConfiguration) sessionFactory.getCurrentSession().createQuery(configCriteria).uniqueResult();
                    
                    if(configDetails != null) {
                        if (messageTypeId == 0) {
                             messageTypeList.add(configDetails.getMessageTypeId());
                        }
                        else {
                             if (messageTypeId == configDetails.getMessageTypeId()) {
                                 messageTypeList.add(configDetails.getMessageTypeId());
                             }
                        }
                    }
                    
                    whereClause = builder.equal(configRoot.get("id"), connectionInfo.getSourceConfigId());
                    configCriteria.where(whereClause);
                    
                    utConfiguration sourceconfigDetails = (utConfiguration) sessionFactory.getCurrentSession().createQuery(configCriteria).uniqueResult();
                    
                    if(sourceconfigDetails != null) {
                        if (fromOrgId == 0) {
                            sourceOrgList.add(sourceconfigDetails.getOrgId());
                        }
                        else {
                            if (fromOrgId == sourceconfigDetails.getOrgId()) {
                                sourceOrgList.add(sourceconfigDetails.getOrgId());
                            }
                        }
                    }
                }
	    }
	}

	if (messageTypeList.isEmpty()) {
	    messageTypeList.add(0);
	}
        
        CriteriaQuery<batchDownloads> batchDownloadsCriteria = builder.createQuery(batchDownloads.class);
        Root<batchDownloads> batchDownloadsRoot = batchDownloadsCriteria.from(batchDownloads.class);
        
        
        Predicate transportMethodId = builder.equal(batchDownloadsRoot.get("transportMethodId"), 2);
        Predicate statusId1 = builder.notEqual(batchDownloadsRoot.get("statusId"), 29); // Submission Processed Errored
        Predicate statusId2 = builder.notEqual(batchDownloadsRoot.get("statusId"), 30); // Submission Processed Errored
        Predicate statusId3 = builder.notEqual(batchDownloadsRoot.get("statusId"), 32); // Submission Processed Errored
        
        Predicate fromDateSearch = null;
        if (!"".equals(fromDate) && fromDate != null) {
            fromDateSearch = builder.greaterThanOrEqualTo(batchDownloadsRoot.get("dateCreated"), fromDate);
        }
        
        Predicate toDateSearch = null;
        if (!"".equals(toDate) && toDate != null) {
            toDateSearch = builder.lessThan(batchDownloadsRoot.get("dateCreated"), toDate);
        }
        
        if(fromDateSearch != null && toDateSearch != null) {
            whereClause = builder.and(transportMethodId,statusId1,statusId2,statusId3,fromDateSearch,toDateSearch);
        }
        else if(fromDateSearch != null && toDateSearch == null) {
            whereClause = builder.and(transportMethodId,statusId1,statusId2,statusId3,fromDateSearch);
        }
        else if(fromDateSearch == null && toDateSearch != null) {
            whereClause = builder.and(transportMethodId,statusId1,statusId2,statusId3,toDateSearch);
        }
        else {
            whereClause = builder.and(transportMethodId,statusId1,statusId2,statusId3);
        }

        batchDownloadsCriteria.orderBy(builder.desc(batchDownloadsRoot.get("dateCreated"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(batchDownloadsCriteria).getResultList();
    }

    /**
     * The 'getAllBatches' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @param batchName
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getAllBatches(Date fromDate, Date toDate, String batchName) throws Exception {

	int firstResult = 0;
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<batchDownloads> criteria = builder.createQuery(batchDownloads.class);
        Root<batchDownloads> root = criteria.from(batchDownloads.class);
	
	if (!"".equals(batchName)) {
            Predicate whereClause = builder.equal(root.get("utBatchName"), batchName);

            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);

            return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}
	else {
            Predicate rest1 = null;
	    Predicate rest2 = null;

	    if (fromDate != null) {
		if (!"".equals(fromDate)) {

		    if (toDate != null) {
			if (!"".equals(toDate)) {
                            rest1 = builder.and(builder.greaterThanOrEqualTo(root.get("dateCreated"), fromDate),builder.lessThan(root.get("dateCreated"), toDate));
                            rest2 = builder.and(builder.greaterThanOrEqualTo(root.get("startDateTime"), fromDate),builder.lessThan(root.get("startDateTime"), toDate));
			}
			else {
			    rest1 = builder.greaterThanOrEqualTo(root.get("dateCreated"), fromDate);
			    rest2 = builder.greaterThanOrEqualTo(root.get("startDateTime"), fromDate);
			}
		    }
		    else {
			rest1 = builder.greaterThanOrEqualTo(root.get("dateCreated"), fromDate);
                        rest2 = builder.greaterThanOrEqualTo(root.get("startDateTime"), fromDate);
		    }
		}
	    }
	    else {
		if (toDate != null) {
		    if (!"".equals(toDate)) {
			rest1 = builder.lessThan(root.get("dateCreated"), toDate);
			rest2 = builder.lessThan(root.get("startDateTime"), toDate);
		    }
		}
	    }
            
            Predicate dateSearch = builder.or(rest1,rest2);
            
            Predicate whereClause = builder.and(builder.ge(root.get("totalRecordCount"), 0),dateSearch);
            
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);

            return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}
    }

    /**
     * The 'getBatchDetails' function will return the batch details for the passed in batch id.
     *
     * @param batchId The id of the batch to return.
     * @return 
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = true)
    public batchDownloads getBatchDetails(int batchId) throws Exception {
	return (batchDownloads) sessionFactory.getCurrentSession().get(batchDownloads.class, batchId);
    }

    /**
     * The 'getBatchDetailsByBatchName' will return a batch by name
     *
     * @param batchName The name of the batch to search form.
     *
     * @return This function will return a batchUpload object
     */
    @Override
    @Transactional(readOnly = true)
    public batchDownloads getBatchDetailsByBatchName(String batchName) throws Exception {
	SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from batchDownloads where utBatchName = :batchName");
	query.setParameter("batchName", batchName);

	if (query.list().size() > 1) {
	    return null;
	} 
	else {
	    return (batchDownloads) query.uniqueResult();
	}
    }

    /**
     * The 'getTransactionRecords' function will return the transaction TARGET records for the passed in transactionId.
     *
     * @param batchId
     * @param configId
     * @param totalFields
     * @return 
     * @throws java.lang.Exception 
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<transactionOutRecords> getTransactionRecords(Integer batchId, Integer configId, Integer totalFields) throws Exception {
	
	//totalFields = totalFields + 10;
	
	String sql = "select ";
		
	for (int i = 1; i <= totalFields; i++) {
	    sql += "f" + i + ",";
	}	
	sql += "id FROM transactiontranslatedout_" + batchId + " order by id asc";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, transactionOutRecords.class);

	List<transactionOutRecords> records = query.list();
	
	return records;
    }

    /**
     * The 'getInternalStatusCodes' function will query and return the list of active internal status codes that can be set to a message.
     *
     * @return This function will return a list of internal status codes
     */
    @Override
    @Transactional(readOnly = true)
    public List getInternalStatusCodes() {
	Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT id, displaytext FROM lu_internalMessageStatus order by displayText asc", String.class);
	return query.list();
    }

    /**
     * The 'updateTargetBatchStatus' function will update the status of the passed in batch downloadId
     *
     * @param batchDLId The id of the batch download
     * @param statusId The id of the new status
     * @param timeField
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = false)
    public void updateTargetBatchStatus(Integer batchDLId, Integer statusId, String timeField) throws Exception {

	String sql = "update batchDownloads set statusId = :statusId ";
	if (!timeField.equalsIgnoreCase("")) {
	    sql = sql + ", " + timeField + " = CURRENT_TIMESTAMP";
	} 
	else {
	    sql = sql + ", startDateTime = null, endDateTime = null";
	}
	sql = sql + " where id = :id ";
	
	Query updateData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
	.setParameter("statusId", statusId)
	.setParameter("id", batchDLId);
	
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("updateTargetBatchStatus failed." + ex);
	}
    }

    /**
     * The 'updateBatchOutputFileName' function will update the outputFileName with the finalized generated file name. This will contain the appropriate extension.
     *
     * @param batchId The id of the batch to update
     * @param fileName The new file name to update.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateBatchOutputFileName(int batchId, String fileName) {
	
	String sql = "update BatchDownloads set outputFileName = :fileName where id = :batchId";

	Query updateData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
	.setParameter("batchId", batchId)
	.setParameter("fileName", fileName);

	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("update Batch outputfile name failed." + ex);
	}
    }

    /**
     * The 'getMaxFieldNo' function will return the max field number for the passed in utConfiguration.
     *
     * @param configId The id of the utConfiguration to find out how many fields it has
     *
     * @return This function will return the max field number.
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = true)
    public int getMaxFieldNo(int configId) throws Exception {

	String sql = "select max(fieldNo) as maxFieldNo from configurationFormFields where configId = :configId and useField = 1";

	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
        .addScalar("maxFieldNo", StandardBasicTypes.INTEGER)
	.setParameter("configId", configId);

	return (Integer) query.list().get(0);
    }

    /**
     * The 'getdownloadableBatches' will return a list of received batches for the logged in user that are ready to be downloaded
     *
     * @param userId The id of the logged in user trying to view downloadable batches
     * @param orgId The id of the organization the user belongs to
     * @param fromDate
     * @param toDate
     *
     * @return The function will return a list of downloadable batches
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("UnusedAssignment")
    public List<batchDownloads> getdownloadableBatches(int userId, int orgId, Date fromDate, Date toDate) throws Exception {
	int firstResult = 0;

	/* Get a list of connections the user has access to */
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationConnectionReceivers> criteria = builder.createQuery(configurationConnectionReceivers.class);
        Root<configurationConnectionReceivers> root = criteria.from(configurationConnectionReceivers.class);

        Predicate whereClause = builder.equal(root.get("userId"), userId);

        criteria.where(whereClause);
        
        List<configurationConnectionReceivers> userConnections = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();

	List<Integer> messageTypeList = new ArrayList<>();
	List<Integer> sourceOrgList = new ArrayList<>();

	if (userConnections.isEmpty()) {
	    messageTypeList.add(0);
	    sourceOrgList.add(0);
	} 
        else {
            CriteriaQuery<configurationConnection> connectionCriteria = builder.createQuery(configurationConnection.class);
            Root<configurationConnection> connectionRoot = connectionCriteria.from(configurationConnection.class);
            
            CriteriaQuery<utConfiguration> configCriteria = builder.createQuery(utConfiguration.class);
            Root<utConfiguration> configRoot = configCriteria.from(utConfiguration.class);
            
            CriteriaQuery<configurationTransport> transportCriteria = builder.createQuery(configurationTransport.class);
            Root<configurationTransport> transportRoot = transportCriteria.from(configurationTransport.class);
            
	    for (configurationConnectionReceivers userConnection : userConnections) {
                
                whereClause = builder.equal(connectionRoot.get("id"), userConnection.getConnectionId());
                connectionCriteria.where(whereClause);
                
                configurationConnection connectionInfo = (configurationConnection) sessionFactory.getCurrentSession().createQuery(connectionCriteria).uniqueResult();
                
                if(connectionInfo != null) {
                    
                    whereClause = builder.equal(configRoot.get("id"), connectionInfo.getTargetConfigId());
                    configCriteria.where(whereClause);
                    
                    utConfiguration configDetails = (utConfiguration) sessionFactory.getCurrentSession().createQuery(configCriteria).uniqueResult();
                    
                    if(configDetails != null) {
                        
                        whereClause = builder.equal(transportRoot.get("configId"), configDetails.getId());
                        transportCriteria.where(whereClause);
                        
                        configurationTransport transportDetails = (configurationTransport) sessionFactory.getCurrentSession().createQuery(transportCriteria).uniqueResult();
                        
                        if(transportDetails != null) {
                            if (transportDetails.getTransportMethodId() == 1
                                || transportDetails.getTransportMethodId() == 3
                                || transportDetails.getTransportMethodId() == 5
                                || transportDetails.getTransportMethodId() == 6
                                || transportDetails.getTransportMethodId() == 9) {

                                messageTypeList.add(configDetails.getMessageTypeId());
                            }
                        }
                    }
                    
                    whereClause = builder.equal(configRoot.get("id"), connectionInfo.getSourceConfigId());
                    configCriteria.where(whereClause);
                    
                    utConfiguration sourceconfigDetails = (utConfiguration) sessionFactory.getCurrentSession().createQuery(configCriteria).uniqueResult();
                    
                    if(sourceconfigDetails != null) {
                        sourceOrgList.add(sourceconfigDetails.getOrgId());
                    }
                }
	    }
	}

	if (messageTypeList.isEmpty()) {
	    messageTypeList.add(0);
	}
        
        CriteriaQuery<batchDownloads> batchDownloadsCriteria = builder.createQuery(batchDownloads.class);
        Root<batchDownloads> batchDownloadsRoot = batchDownloadsCriteria.from(batchDownloads.class);
        
        Predicate[] transportPredicates = new Predicate[5];
        transportPredicates[0] = builder.equal(batchDownloadsRoot.get("transportMethodId"), 1);
        transportPredicates[1] = builder.equal(batchDownloadsRoot.get("transportMethodId"), 3);
        transportPredicates[2] = builder.equal(batchDownloadsRoot.get("transportMethodId"), 5);
        transportPredicates[3] = builder.equal(batchDownloadsRoot.get("transportMethodId"), 6);
        transportPredicates[4] = builder.equal(batchDownloadsRoot.get("transportMethodId"), 9);
        
        Predicate query1 = builder.or(transportPredicates);
        
        Predicate[] statusPredicates = new Predicate[3];
        statusPredicates[0] = builder.equal(batchDownloadsRoot.get("statusId"), 22);
        statusPredicates[1] = builder.equal(batchDownloadsRoot.get("statusId"), 23);
        statusPredicates[2] = builder.equal(batchDownloadsRoot.get("statusId"), 28);
        
        Predicate query2 = builder.or(statusPredicates);
        
        Predicate fromSearch = null;
	if (!"".equals(fromDate)) {
            fromSearch = builder.greaterThanOrEqualTo(batchDownloadsRoot.get("dateCreated"), fromDate);
	}

        Predicate toSearch = null;
	if (!"".equals(toDate)) {
            toSearch = builder.lessThan(batchDownloadsRoot.get("dateCreated"), toDate);
	}
        
        if(fromSearch != null && toSearch != null) {
            whereClause = builder.and(query1,query2,fromSearch,toSearch);
        }
        else if(fromSearch != null && toSearch == null) {
            whereClause = builder.and(query1,query2,fromSearch);
        }
        else if(fromSearch == null && toSearch != null) {
            whereClause = builder.and(query1,query2,toSearch);
        }
        else {
            whereClause = builder.and(query1,query2);
        }
        
         batchDownloadsCriteria.orderBy(builder.desc(batchDownloadsRoot.get("dateCreated"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(batchDownloadsCriteria).getResultList();
    }

    /**
     * The 'updateLastDownloaded' function will update the last downloaded field for the passed in batch
     *
     * @param batchId The id of the batch to update.
     * @throws java.lang.Exception
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void updateLastDownloaded(int batchId) throws Exception {

	String sql = "update BatchDownloads set lastDownloaded = CURRENT_TIMESTAMP where id = :batchId";

	Query updateData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
	.setParameter("batchId", batchId);

	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("update Batch last downloaded date failed." + ex);
	}
    }

    /**
     * The 'getScheduledConfigurations' function will return a list of configurations that have a Daily, Weekly or Monthly schedule setting
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationSchedules> getScheduledConfigurations() {

	SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationSchedules where type = 2 or type = 3 or type = 4");

	List<configurationSchedules> scheduledConfigList = query.list();
	return scheduledConfigList;
    }

    /**
     * The 'updateBatchStatus' function will update the status of the passed in batch
     *
     * @param batchId The id of the batch to update
     * @param statusId The status to update the batch to
     */
    @Override
    @Transactional(readOnly = false)
    public void updateBatchStatus(Integer batchId, Integer statusId) {

	String sql = "update batchDownloads set statusId = :statusId where id = :batchId";

	Query updateData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
	.setParameter("statusId", statusId)
	.setParameter("batchId", batchId);
	
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("updateBatch download Status failed." + ex);
	}
    }

    /**
     * The 'saveOutputRunLog' function will insert the latest run log for the batch.
     *
     * @param log The output run log to save.
     */
    @Override
    @Transactional(readOnly = false)
    public void saveOutputRunLog(targetOutputRunLogs log) throws Exception {
	sessionFactory.getCurrentSession().persist(log);
    }

    /**
     * The 'targetOutputRunLogs' function will return the latest output run log for the passed in utConfiguration Id
     *
     * @param configId = The utConfiguration to find the latest log.
     *
     * @return This function will return the latest log
     */
    @Override
    @Transactional(readOnly = true)
    public List<targetOutputRunLogs> getLatestRunLog(int configId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<targetOutputRunLogs> criteria = builder.createQuery(targetOutputRunLogs.class);
        Root<targetOutputRunLogs> root = criteria.from(targetOutputRunLogs.class);

        Predicate whereClause = builder.equal(root.get("configId"), configId);

        criteria.orderBy(builder.desc(root.get("lastRunTime"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    @Override
    @Transactional(readOnly = false)
    public Integer writeOutputToTextFile(configurationTransport transportDetails, Integer batchDownloadId, String filePathAndName, String fieldNos, Integer batchUploadId) {

	String sql = "";

	//If file type == JSON
	if (transportDetails.getFileType() == 12) {
	    sql = ("call getJSONForConfig(:batchConfigId, :batchDownloadId, :filePathAndName, :jsonWrapperElement);");
	}
	else {
	    if(transportDetails.isAddTargetFileHeaderRow()) {
		String configFieldHeadings = getConfigFieldHeadingsForOutput(transportDetails.getConfigId());
		if(configFieldHeadings != null) {
		    if(!"".equals(configFieldHeadings)) {
			sql += "SELECT " + configFieldHeadings + " UNION ALL ";
		    }
		}
	    }

	    sql += "SELECT " + fieldNos + " "
	    + "FROM transactionTranslatedOut_" + batchDownloadId + " "
	    + "where configId = " + transportDetails.getConfigId() + " and ";

	    if(transportDetails.getErrorHandling() == 4) {
		sql += "statusId in (9,14) ";
	    }
	    else {
		sql += "statusId = 9 ";
	    }
	    sql += "INTO OUTFILE  '" + filePathAndName + "' "
	    + "FIELDS TERMINATED BY '" + transportDetails.getDelimChar()+"' LINES TERMINATED BY '\\n';";
	}

	if (!"".equals(sql)) {
	    Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);

	    if (transportDetails.getFileType() == 12) {
		query.setParameter("batchConfigId", transportDetails.getConfigId());
		query.setParameter("batchDownloadId", batchDownloadId);
		query.setParameter("filePathAndName", filePathAndName);
		query.setParameter("jsonWrapperElement", transportDetails.getJsonWrapperElement());
	    }

	    try {
		query.list();
	    } catch (Exception ex) {}
	}

	return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false)
    public List<ConfigOutboundForInsert> setConfigOutboundForInsert(int configId, int batchDownloadId) throws Exception {
	
	String sql = ("call setSqlForOutboundConfig(:configId, :batchDownloadId);");
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,ConfigOutboundForInsert.class)
	.addScalar("batchDownloadId", StandardBasicTypes.INTEGER)
	.addScalar("fieldNos", StandardBasicTypes.STRING)
	.addScalar("saveToCols", StandardBasicTypes.STRING)
	.addScalar("saveToTableName", StandardBasicTypes.STRING)
	.addScalar("selectFields", StandardBasicTypes.STRING)
	.addScalar("updateFields", StandardBasicTypes.STRING)
	.addScalar("configId", StandardBasicTypes.INTEGER)
	.setParameter("configId", configId)
	.setParameter("batchDownloadId", batchDownloadId);

	List<ConfigOutboundForInsert> configOutboundForInsertList = query.list();

	return configOutboundForInsertList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false)
    public String getConfigFieldsForOutput(Integer configId) throws Exception {
	String sql = ""
	+ "select group_concat('REPLACE(REPLACE(ifnull(F', fieldNo, ',\"\") , ''\\n'', ''''), ''\\r'', '''')' order by fieldNo asc) as fieldNos "
	+ " from configurationFormFields where configId = :configId";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	query.setParameter("configId", configId);
	List<String> fieldNos = query.list();
	if (query.list().size() == 1) {
	    return fieldNos.get(0);
	} else {
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void setSessionLength() throws Exception {
	Query query1 = sessionFactory.getCurrentSession().createNativeQuery("SET SESSION group_concat_max_len = 9999999;", String.class);
	query1.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public BigInteger getRejectedCount(String fromDate, String toDate) throws Exception {

	String sql = "select count(id) as totalReferrals "
	+ "from batchdownloads "
	+ "where (dateCreated >= '" + fromDate + "' and dateCreated < '" + toDate + "')  "
	+ "and statusId = 41";

	Query getRejectedCount = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);

	return (BigInteger) getRejectedCount.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public String getCustomXMLFieldsForOutput(Integer configId) throws Exception {
	String sql = "select group_concat('REPLACE(REPLACE(ifnull(F', fieldValue, ',\"\") , ''\\n'', ''''), ''\\r'', '''')' order by id asc) as fieldNos "
	+ " from configurationccdelements where configId = :configId  and fieldValue != ''";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	query.setParameter("configId", configId);
	List<String> fieldNos = query.list();
	if (query.list().size() == 1) {
	    return fieldNos.get(0);
	} else {
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = true)
    public List getOutputForCustomTargetFile(configurationTransport transportDetails, Integer batchDownloadId, String fieldNos, Integer batchUploadId) {
	
	String sql = "SELECT " + fieldNos
	    + " FROM transactiontranslatedout_"+batchDownloadId + " "
	    + "where configId = :configId and ";
	
	if(transportDetails.getErrorHandling() == 4) {
	    sql += "statusId in (9,14)";
	}
	else {
	    sql += "statusId = 9";
	}
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	query.setParameter("configId", transportDetails.getConfigId());
	try {
	    return query.list();
	} catch (Exception ex) {
	    return null;
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getBatchesByStatusIdsAndDate(Date fromDate,Date toDate, Integer fetchSize, List<Integer> statusIds) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<batchDownloads> criteria = builder.createQuery(batchDownloads.class);
        Root<batchDownloads> root = criteria.from(batchDownloads.class);
        
        Predicate statusSearch = root.get("statusId").in(statusIds);
        
        Predicate fromDateSearch = null;
        if (fromDate != null) {
	    if (!"".equals(fromDate)) {
                fromDateSearch = builder.greaterThanOrEqualTo(root.get("dateCreated"), fromDate);
	    }
	}
        
        Predicate toDateSearch = null;
        if (toDate != null) {
	    if (!"".equals(toDate)) {
                toDateSearch = builder.lessThan(root.get("dateCreated"), toDate);
	    }
	}
        
        Predicate whereClause = null;
        
        if(fromDateSearch != null && toDateSearch != null) {
            whereClause = builder.and(statusSearch,fromDateSearch,toDateSearch);
        }
        else if(fromDateSearch != null && toDateSearch == null) {
            whereClause = builder.and(statusSearch,fromDateSearch);
        }
        else if(fromDateSearch == null && toDateSearch != null) {
            whereClause = builder.and(statusSearch,toDateSearch);
        }
        else {
            whereClause = statusSearch;
        }

        criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        
        if (fetchSize > 0) {
            return sessionFactory.getCurrentSession().createQuery(criteria).setMaxResults(fetchSize).getResultList();
        }
        else {
            return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Integer insertRestApiMessage(RestAPIMessagesOut APIMessageOut) throws Exception {
        sessionFactory.getCurrentSession().persist(APIMessageOut);
        return APIMessageOut.getId();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getDLBatchesByStatusIds(List<Integer> statusIds) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<batchDownloads> criteria = builder.createQuery(batchDownloads.class);
        Root<batchDownloads> root = criteria.from(batchDownloads.class);

        Predicate whereClause = root.get("statusId").in(statusIds);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public batchDLRetry getBatchDLRetryByDownloadId(Integer batchDownloadId,Integer statusId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<batchDLRetry> criteria = builder.createQuery(batchDLRetry.class);
        Root<batchDLRetry> root = criteria.from(batchDLRetry.class);
        
        Predicate whereClause = null;
        
        if (statusId > 0) {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = builder.equal(root.get("batchDownloadId"), batchDownloadId);
            predicates[1] = builder.equal(root.get("fromStatusId"), statusId);

            whereClause = builder.and(predicates);
        }
        else {
            whereClause = builder.equal(root.get("batchDownloadId"), batchDownloadId);
        }

        criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        
        List<batchDLRetry> brList = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();

	if (brList.size() > 0) {
	    return brList.get(0);
	} else {
	    return null;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void saveBatchDLRetry(batchDLRetry br) throws Exception {
	sessionFactory.getCurrentSession().persist(br);
    }

    @Override
    @Transactional(readOnly = false)
    public void clearBatchDLRetry(Integer batchDownloadId) throws Exception {
	MutationQuery delBatch = sessionFactory.getCurrentSession().createMutationQuery("delete from batchDLRetry where batchDownloadId = :batchDownloadId");
	delBatch.setParameter("batchDownloadId", batchDownloadId);
	delBatch.executeUpdate();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRestAPIMessageByDownloadId(int batchDownloadId) throws Exception {
	MutationQuery deletNote = sessionFactory.getCurrentSession().createMutationQuery("delete from RestAPIMessagesOut where batchDownloadId = :batchDownloadId");
	deletNote.setParameter("batchDownloadId", batchDownloadId);
	deletNote.executeUpdate();
    }
   
    @Override
    @Transactional(readOnly = false)
    public void createTargetBatchTables(Integer batchDownloadId, Integer configId) throws Exception {
	try {

	    List<configurationFormFields> configFormFields = configurationTransportManager.getConfigurationFields(configId, 0);

	    Integer totalFields = 50;

	    if (configFormFields != null) {
		if (!configFormFields.isEmpty()) {
		    //totalFields = configFormFields.size() + 10;
                    totalFields = configFormFields.size();
		}
	    }

	    //Create the transactionoutrecords_batchDownloadId table
	    String transactionOutRecordsTable = "DROP TABLE IF EXISTS `transactionoutrecords_" + batchDownloadId + "`; CREATE TABLE `transactionoutrecords_" + batchDownloadId + "` (";
		
	    for (int i = 1; i <= totalFields; i++) {
		transactionOutRecordsTable += "F" + i + " text,";
	    }

	    transactionOutRecordsTable += "id int(11) NOT NULL AUTO_INCREMENT," 
	    + "batchDownloadId int(11) DEFAULT NULL," 
	    + "configId int(11) NOT NULL,"
	    + "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
	    + "transactionInRecordsId int(11) NOT NULL,"
	    + "PRIMARY KEY (`id`),"
	    + "KEY `torFK_idx` (`batchDownloadId`)"
	    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    Query query = sessionFactory.getCurrentSession().createNativeQuery(transactionOutRecordsTable, String.class);
	    query.executeUpdate();

	    //Create the transactiontranslatedout_batchDownloadId table
	    String transactionTranslatedOutTable = "DROP TABLE IF EXISTS `transactiontranslatedout_" + batchDownloadId + "`; CREATE TABLE `transactiontranslatedout_" + batchDownloadId + "` ("
	    + "id int(11) NOT NULL AUTO_INCREMENT,"
	    + "transactionOutRecordsId int(11) NOT NULL,"
	    + "configId int(11) NOT NULL,"
	    + "batchDownloadId int(11) DEFAULT NULL,"
	    + "statusId int(11) DEFAULT NULL,"
	    + "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
	    + "forCW text,";

	    for (int i = 1; i <= totalFields; i++) {
		transactionTranslatedOutTable += "F" + i + " text,";
	    }

	    transactionTranslatedOutTable += "PRIMARY KEY (`id`),"
	    + "UNIQUE KEY `transactionOutRecordsId_UNIQUE` (`transactionOutRecordsId`),"
	    + "KEY `ttoConfigId_idx` (`configId`,`batchDownloadId`),"
	    + "KEY `ttobatchId` (`batchDownloadId`)"
	    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createNativeQuery(transactionTranslatedOutTable, String.class);
	    query.executeUpdate();

	    //Create the transactionouterrors_batchDownloadId table
	    String transactionOutErrorsTable = "DROP TABLE IF EXISTS `transactionouterrors_" + batchDownloadId + "`; CREATE TABLE `transactionouterrors_" + batchDownloadId + "` ("
	    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
	    + "`batchDownloadId` int(11) NOT NULL,"
	    + "`configId` int(11) DEFAULT NULL,"
	    + "`transactionOutRecordsId` int(11) DEFAULT NULL,"
	    + "`fieldNo` int(11) DEFAULT NULL,"
	    + "`fieldLabel` varchar(255) DEFAULT NULL,"
	    + "`fieldValue` text DEFAULT NULL,"
	    + "`required` bit(1) DEFAULT NULL,"
	    + "`errorId` int(11) DEFAULT NULL,"
	    + "`cwId` int(11) DEFAULT NULL,"
	    + "`macroId` int(11) DEFAULT NULL,"
	    + "`validationTypeId` int(11) DEFAULT NULL,"
	    + "`stackTrace` text,"
	    + "`dateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
	    + "PRIMARY KEY (`id`),"
	    + "KEY `batchDownloadIdFK_idx` (`batchDownloadId`),"
	    + "KEY `configFFFKId_idx` (`fieldNo`),"
	    + "KEY `errorIdFK_idx` (`errorId`),"
	    + "KEY `toeVTFK_idx` (`validationTypeId`),"
	    + "KEY `toeMacroFK_idx` (`macroId`),"
	    + "KEY `toeCWIDFK_idx` (`cwId`),"
	    + "KEY `transOutId` (`transactionOutRecordsId`),"
	    + "CONSTRAINT `batchDownloadId_"+batchDownloadId+"_FK` FOREIGN KEY (`batchDownloadId`) REFERENCES `batchdownloads` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
	    + "CONSTRAINT `toeCWID_"+batchDownloadId+"_FK` FOREIGN KEY (`cwId`) REFERENCES `crosswalks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
	    + "CONSTRAINT `toeMacro_"+batchDownloadId+"_FK` FOREIGN KEY (`macroId`) REFERENCES `macro_names` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
	    + "CONSTRAINT `toeVT_"+batchDownloadId+"_FK` FOREIGN KEY (`validationTypeId`) REFERENCES `ref_validationtypes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION"
	    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createNativeQuery(transactionOutErrorsTable, String.class);
	    query.executeUpdate();

	    //Create the transactiontranslatedlistout_batchDownloadId table
	    String transactionTranslatedListOutTable = "DROP TABLE IF EXISTS `transactiontranslatedlistout_" + batchDownloadId + "`; CREATE TABLE `transactiontranslatedlistout_" + batchDownloadId + "` ("
	    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
	    + "`batchDownloadId` int(11) NOT NULL,"
	    + "`transactionOutRecordsId` int(11) NOT NULL,"
	    + "`concatKey` varchar(255) DEFAULT NULL,"
	    + "`inValue` text,"
	    + "`translatedValue` text,"
	    + "`translateIdToKeep` int(11) DEFAULT NULL,"
	    + "`fCol` int(11) DEFAULT NULL,"
	    + "`srcField` text,"
	    + "`fieldA` text,"
	    + "`fieldB` text,"
	    + "PRIMARY KEY (`id`),"
	    + "KEY `outConcatKey` (`concatKey`)"
	    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createNativeQuery(transactionTranslatedListOutTable, String.class);
	    query.executeUpdate();
	    
	    //Create the transactionoutjsontable_batchDownloadId table
	    String transactionOutJSONTable = "DROP TABLE IF EXISTS `transactionoutjsontable_" + batchDownloadId + "`; CREATE TABLE `transactionoutjsontable_" + batchDownloadId + "` ("
	    + "`id` int(11) NOT NULL AUTO_INCREMENT,"
	    + "`batchDownloadId` int(11) NOT NULL,"
	    + "`transactionOutRecordsId` int(11) NOT NULL,"
	    + "`configId` int(11) DEFAULT NULL,"
	    + "`jsonString` text,"
	    + "`dateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
	    + " PRIMARY KEY (`id`),"
	    + " KEY `ttojsonBatchId` (`batchDownloadId`),"
	    + " KEY `ttoJsonConfigId` (`configId`)"
	    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createNativeQuery(transactionOutJSONTable, String.class);
	    query.executeUpdate();
	    
	    //Create the transactionoutdetailauditerrors_batchUploadId table
	    String transactionoutdetailauditerrorsTable = "DROP TABLE IF EXISTS `transactionoutdetailauditerrors_" + batchDownloadId + "`; CREATE TABLE `transactionoutdetailauditerrors_" + batchDownloadId + "` (" 
	    + "`id` int(11) NOT NULL AUTO_INCREMENT," 
	    + "`batchDownloadId` int(11) NOT NULL," 
	    + "`configId` int(11) NOT NULL," 
	    + "`transactionOutRecordsId` int(11) NOT NULL," 
	    + "`fieldNo` int(11) NOT NULL," 
	    + "`fieldName` varchar(255) DEFAULT NULL," 
	    + "`errorId` int(11) NOT NULL," 
	    + "`errorDetails` varchar(255) DEFAULT NULL COMMENT 'This field is used to update cw name, validation type name, macro name'," 
	    + "`errorData` text," 
	    + "`reportField1Data` varchar(255) DEFAULT NULL," 
	    + "`reportField2Data` varchar(255) DEFAULT NULL," 
	    + "`reportField3Data` varchar(255) DEFAULT NULL," 
	    + "`reportField4Data` varchar(255) DEFAULT NULL," 
	    + "`transactionOutErrorId` int(11) DEFAULT '0'," 
	    + "`required` bit(1) DEFAULT NULL,"
	    + " PRIMARY KEY (`id`)," 
	    + " KEY `ttoauditKeyError_idx` (`batchDownloadId`)," 
	    + " CONSTRAINT `ttoauditErrorKey_"+batchDownloadId+"_FK` FOREIGN KEY (`batchDownloadId`) REFERENCES `batchDownloads` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION" 
	    +") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

	    query = sessionFactory.getCurrentSession().createNativeQuery(transactionoutdetailauditerrorsTable, String.class);
	    query.executeUpdate();
	    
	    // create tables to track dropped values from macros
	    String transactionoutmacrodroppedvalues = ""
	    + "drop table if exists transactionoutmacrodroppedvalues_" + batchDownloadId + ";"
	    + "CREATE TABLE transactionoutmacrodroppedvalues_" + batchDownloadId + " ( id int(11) NOT NULL AUTO_INCREMENT, batchDownloadId int(11) not null, transactionOutRecordsId int(11) NOT NULL, configId int(11) not null, fieldNo int(11) NOT NULL, fieldValue text, matchId varchar(255) NULL, crosswalkId int(11) not null default 0, PRIMARY KEY (id), KEY outDrop (matchId));";
	    
	    query = sessionFactory.getCurrentSession().createNativeQuery(transactionoutmacrodroppedvalues, String.class);
	    query.executeUpdate();
	    
	    String transactionoutmacrokeptvalues = ""
	    + "drop table if exists transactionoutmacrokeptvalues_" + batchDownloadId + ";"
	    + "CREATE TABLE transactionoutmacrokeptvalues_" + batchDownloadId + " ( id int(11) NOT NULL AUTO_INCREMENT, batchDownloadId int(11) not null, transactionOutRecordsId int(11) NOT NULL, configId int(11) not null, fieldNo int(11) NOT NULL, fieldValue text, matchId varchar(255) NULL, PRIMARY KEY (id), KEY inkept (matchId));";
	    
	    query = sessionFactory.getCurrentSession().createNativeQuery(transactionoutmacrokeptvalues, String.class);
	    query.executeUpdate();
	} 
	catch (Exception ex) {
	    System.err.println("Create Batch Download tables for batch (Id: " + batchDownloadId +") "+ ex.getCause());
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void loadTargetBatchTables(Integer batchDownloadId, Integer batchUploadId, Integer configId, Integer uploadConfigId) throws Exception {
	
	//List<configurationFormFields> configFormFields = configurationTransportManager.getConfigurationFieldsToCopy(configId);
	List<configurationconnectionfieldmappings> connectionFieldMappings = configurationTransportManager.getConnectionFieldMappings(configId,uploadConfigId);
	
	List<configurationTransport> handlingDetails = transactionInManager.getHandlingDetailsByBatch(batchUploadId);

	StringBuilder tableFields = new StringBuilder();
	
	connectionFieldMappings.forEach(field -> {
	    if(field.isUseField()) {
		tableFields.append("F").append(field.getFieldNo()).append(" text").append(",");
	    }
	});
	
        //Need to create the temp translated in table
        String temptransactionTranslatedInTable = "DROP TABLE IF EXISTS `temp_transactiontranslatedin_" + batchUploadId + "_"+ batchDownloadId + "`; CREATE TABLE `temp_transactiontranslatedin_" + batchUploadId + "_"+ batchDownloadId + "` ("
	+ "id int(11) NOT NULL AUTO_INCREMENT,"
	+ "transactionInRecordsId int(11) NOT NULL,"
	+ "configId int(11) NOT NULL,"
	+ "batchUploadId int(11) DEFAULT NULL,"
	+ "statusId int(11) DEFAULT NULL,"
	+ "dateCreated datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
	+ "forCW text," + tableFields;

        temptransactionTranslatedInTable += "PRIMARY KEY (`id`),"
	+ "UNIQUE KEY `temp_transactionInRecordsId_UNIQUE` (`transactionInRecordsId`)"
	+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

        Query query = sessionFactory.getCurrentSession().createNativeQuery(temptransactionTranslatedInTable, String.class);
        query.executeUpdate();
	
	StringBuilder selectFields = new StringBuilder();
	StringBuilder insertFields = new StringBuilder();
	
	connectionFieldMappings.forEach(configfield -> {
	    if(configfield.isUseField()) {
		if(configfield.getAssociatedFieldNo() == 0) {
		    //Check to see if field has a default value
		    if(configfield.getDefaultValue() != null) {
			if(!configfield.getDefaultValue().isEmpty()) {
			    selectFields.append("'").append(configfield.getDefaultValue()).append("',");
			}
			else {
			    selectFields.append("''").append(",");
			}
		    }
		    else {
			selectFields.append("''").append(",");
		    }
		}
		else {
		    selectFields.append("F").append(configfield.getAssociatedFieldNo()).append(",");
		}
		insertFields.append("F").append(configfield.getFieldNo()).append(",");
	    }
	});
	
	//Need to copy transaction in tables into temp table
	String sqlinsert = "INSERT INTO temp_transactiontranslatedin_"+batchUploadId+"_"+batchDownloadId+ " "
	+ "(id,transactionInRecordsId,configId,statusId," + insertFields;

	sqlinsert += "batchUploadId) SELECT id,transactionInRecordsId,configId,statusId,"+selectFields;
	sqlinsert+= "batchUploadId from transactiontranslatedin_"+batchUploadId;
	
	query = sessionFactory.getCurrentSession().createNativeQuery(sqlinsert, String.class);
	query.executeUpdate();
	
	try {
	    
	    String sql = "insert into transactionoutrecords_"+batchDownloadId+" "
	    + "("+insertFields;

	    sql+= "batchDownloadId, configId,transactionInRecordsId) ";

	    sql+= "select "+insertFields;
	    
	    //Source configuration error handling is set to pass through all transaction errors.
	    if(handlingDetails.get(0).getErrorHandling() == 4) {
		sql+= batchDownloadId + ","+configId +",transactionInRecordsId from temp_transactiontranslatedin_"+batchUploadId+"_"+batchDownloadId+" where statusId in (9,14);";
	    }
	    //Otherwise only send transactions that have zero errors
	    else {
		sql+= batchDownloadId + ","+configId +",transactionInRecordsId from temp_transactiontranslatedin_"+batchUploadId+"_"+batchDownloadId+" where statusId = 9;";
	    }
	    
	    query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	    query.executeUpdate();
	    
	    sql = "insert into transactiontranslatedout_"+batchDownloadId+" "
	    + "(statusId, configId, transactionOutRecordsId," + insertFields;

	    sql+= "batchDownloadId)";
	    sql+= "select 9, configId, id, " + insertFields;
	    sql+= "batchDownloadId from transactionoutrecords_"+batchDownloadId+";";
	    
	    query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	    query.executeUpdate();
            
            //Delete the temp table
            sql = "DROP TABLE IF EXISTS `temp_transactiontranslatedin_" + batchUploadId + "_" + batchDownloadId + "`;";
            query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
            query.executeUpdate();
	    
	} catch (Exception ex) {
	    System.err.println("loadTransactionTranslatedIn for Batch (id: "+batchUploadId+") "+ ex.getCause());
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deleteBatchUploadTables(Integer batchId) throws Exception {

	String deleteSQL = "";
	Query deleteQuery;
	deleteSQL += "DROP TABLE IF EXISTS `transactionindetailauditerrors_" + batchId + "`;";
	// need transactiontranslatedlistin_to populate some dropped values
	//deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistin_" + batchId + "`;";
	//deleteSQL += "DROP TABLE IF EXISTS `transactioninrecords_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninerrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninmacrodroppedvalues_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactioninmacrokeptvalues_" + batchId + "`;";
	
	deleteQuery = sessionFactory.getCurrentSession().createNativeQuery(deleteSQL, String.class);
	deleteQuery.executeUpdate();
    }
    
    @Override
    @Transactional(readOnly = false)
    public Integer clearBatchTransactionTables(Integer batchDownloadId) {
	
	String clearSQL = "";
	clearSQL += "truncate TABLE `transactionoutdetailauditerrors_" + batchDownloadId + "`;";
	clearSQL += "truncate TABLE `transactionouterrors_" + batchDownloadId + "`;";
	clearSQL += "truncate TABLE `transactiontranslatedlistout_" + batchDownloadId + "`;";
	clearSQL += "truncate TABLE `transactiontranslatedout_" + batchDownloadId + "`;";
	clearSQL += "truncate TABLE `transactionoutrecords_" + batchDownloadId + "`;";
	
	Query clearData = sessionFactory.getCurrentSession().createNativeQuery(clearSQL, String.class);

	try {
	    clearData.executeUpdate();
	    return 0;
	} catch (Exception ex) {
	    System.err.println("clearBatchTransactionTables_" + batchDownloadId + " " + ex.getCause());
	    return 1;
	}
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteBatchDownloadTables(Integer batchId) throws Exception {

	String deleteSQL = "";
	Query deleteQuery;
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutdetailauditerrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistout_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutrecords_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionouterrors_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutjsontable_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedout_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutmacrodroppedvalues_" + batchId + "`;";
	deleteSQL += "DROP TABLE IF EXISTS `transactionoutmacrokeptvalues_" + batchId + "`;";
	
	deleteQuery = sessionFactory.getCurrentSession().createNativeQuery(deleteSQL, String.class);
	deleteQuery.executeUpdate();
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deleteBatchDownloadTablesByBatchUpload(List<batchDownloads> batchDownloads) throws Exception {

	String deleteSQL = "";
	Query deleteQuery;
	
	if(batchDownloads != null) {
	    if(!batchDownloads.isEmpty()) {
		for(batchDownloads batch : batchDownloads) {
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutdetailauditerrors_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedlistout_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutrecords_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionouterrors_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutjsontable_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactiontranslatedout_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutmacrodroppedvalues_" + batch.getId() + "`;";
		    deleteSQL += "DROP TABLE IF EXISTS `transactionoutmacrokeptvalues_" + batch.getId() + "`;";
		    deleteSQL += "delete from batchdownloaddroppedvalues where batchdownloadId = " + batch.getId() + ";";
		}
		
		if(!"".equals(deleteSQL)) {
		    deleteQuery = sessionFactory.getCurrentSession().createNativeQuery(deleteSQL, String.class);
		    deleteQuery.executeUpdate();
		}
	    }
	}
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getDLBatchesByBatchUploadId(Integer batchUploadId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<batchDownloads> criteria = builder.createQuery(batchDownloads.class);
        Root<batchDownloads> root = criteria.from(batchDownloads.class);

        Predicate whereClause = builder.equal(root.get("batchUploadId"), batchUploadId);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getPendingResetBatches(Integer batchUploadId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<batchDownloads> criteria = builder.createQuery(batchDownloads.class);
        Root<batchDownloads> root = criteria.from(batchDownloads.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = builder.equal(root.get("batchUploadId"), batchUploadId);
        predicates[1] = builder.equal(root.get("statusId"), 66);

        Predicate whereClause = builder.and(predicates);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }
    
    /**
     * The 'submitBatchDownloadChanges' function will submit the batch changes.
     *
     * @param batchDownload The object that will hold the new batch info
     *
     * @table batchDownloads
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void submitBatchDownloadChanges(batchDownloads batchDownload) throws Exception {
	sessionFactory.getCurrentSession().merge(batchDownload);
    }
  
    @Override
    @Transactional(readOnly = true)
    public boolean chechForTransactionInTable(Integer batchUploadId) throws Exception {
	
	boolean tableFound = false;
	
	String tableNameToFind = "transactiontranslatedin_"+batchUploadId;
	
	String SQL_MASTER_TABLES = "SHOW TABLES IN universaltranslator";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(SQL_MASTER_TABLES, String.class);
	List<String> tableNames = query.list();
	
	if(!query.list().isEmpty()) {
	    for(String tableName : tableNames) {
		if(tableNameToFind.equals(tableName)) {
		    tableFound = true;
		}
	    }
	}
	
	return tableFound;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getAllSentBatchesPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	
	String dateSQLString = "";
	String dateSQLStringTotal = "";
	
	if(fromDate !=  null && toDate != null) {
	    if(!"".equals(fromDate)) {
		dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";
		dateSQLStringTotal += "dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";

		if(!"".equals(toDate)) {
		    dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
		    dateSQLStringTotal += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
		}
		else {
		    dateSQLString += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
		    dateSQLStringTotal += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
		}
	    }
	    else {
		if(!"".equals(toDate)) {
		    dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(toDate)+" 00:00:00' ";
		    dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
		}
		else {
		    dateSQLString += "a.id > 0";
		}
	    }
	}
	else {
	    dateSQLString += "a.id > 0";
	    dateSQLStringTotal = "utBatchName = '" + searchTerm + "'";
	}
	
	String sqlQuery = "select id, orgId, utBatchName, transportMethodId, outputFileName, totalRecordCount, totalErrorCount, configName, threshold, statusId, dateCreated,"
	+ "startDateTime, endDateTime, batchUploadId, deleted, lastDownloaded, userId, mergeable, configId, statusValue, endUserDisplayText, orgName, transportMethod, fromBatchName, fromBatchFile, totalMessages, srcOrgName "
	+ "FROM ("
	+ "select a.id, a.orgId, a.utBatchName, a.transportMethodId, a.outputFileName, a.totalRecordCount, a.totalErrorCount, b.configName, b.threshold,"
	+ "a.statusId, a.dateCreated, a.startDateTime, a.deleted, a.userId, a.lastDownloaded, a.mergeable, a.endDateTime, a.configId, a.batchUploadId, c.displayCode as statusValue, c.endUserDisplayText as endUserDisplayText, d.orgName, e.transportMethod, f.utBatchName as fromBatchName,"
	+ "case when f.transportMethodId = 5 THEN CONCAT(f.utBatchName,'.',SUBSTRING_INDEX(f.originalFileName,'.',-1)) "
	+ "when f.transportMethodId = 1 THEN CONCAT(f.utBatchName,'.',SUBSTRING_INDEX(f.originalFileName,'.',-1)) "
	+ "else '' end as fromBatchFile,"
	+ "(select count(id) as total from batchdownloads where "+dateSQLStringTotal+") as totalMessages, g.orgName as srcOrgName "
	+ "FROM batchdownloads a inner join "
	+ "configurations b on b.id = a.configId inner join "
	+ "lu_processstatus c on c.id = a.statusId inner join "
	+ "organizations d on d.id = a.orgId inner join "
	+ "ref_transportmethods e on e.id = a.transportMethodId inner join "
	+ "batchuploads f on f.id = a.batchUploadId inner join "
	+ "organizations g on g.id = f.orgId "
	+ "where " + dateSQLString + ") as inboundBatches ";
	
	if(!"".equals(searchTerm)){
	    sqlQuery += " where ("
	    + "id like '%"+searchTerm+"%' "
	    + "OR orgName like '%"+searchTerm+"%' "
	    + "OR configName like '%"+searchTerm+"%' "
	    + "OR utBatchName like '%"+searchTerm+"%' "
	    + "OR fromBatchName like '%"+searchTerm+"%' "
	    + "OR fromBatchFile like '%"+searchTerm+"%' "		    
	    + "OR statusValue like '%"+searchTerm+"%' "
	    + "OR transportMethod like '%"+searchTerm+"%'"
	    + "OR srcOrgName like '%"+searchTerm+"%'"
	    + ") ";
	}	
	
	if("errorRecordCount".equals(sortColumnName)) {
	    sortColumnName = "totalErrorCount";
	}
	else if("uploadType".equals(sortColumnName)) {
	    sortColumnName = "threshold";
	}
	
	sqlQuery += "order by "+sortColumnName+" "+sortDirection;
	
	if(displayRecords > 0) {
	    sqlQuery += " limit " + displayStart + ", " + displayRecords;
	}
	else {
	    sqlQuery += " limit " + displayStart+ ", 1000000";
	}
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlQuery,batchDownloads.class)
	.addScalar("id", StandardBasicTypes.INTEGER)
	.addScalar("orgId", StandardBasicTypes.INTEGER)
	.addScalar("utBatchName", StandardBasicTypes.STRING)
	.addScalar("transportMethodId", StandardBasicTypes.INTEGER)
	.addScalar("outputFileName", StandardBasicTypes.STRING)
	.addScalar("totalRecordCount", StandardBasicTypes.INTEGER)
	.addScalar("totalErrorCount", StandardBasicTypes.INTEGER)
	.addScalar("configName", StandardBasicTypes.STRING)
	.addScalar("statusId", StandardBasicTypes.INTEGER)
	.addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
	.addScalar("startDateTime", StandardBasicTypes.TIMESTAMP)
	.addScalar("endDateTime", StandardBasicTypes.TIMESTAMP)
	.addScalar("statusValue", StandardBasicTypes.STRING)
	.addScalar("orgName", StandardBasicTypes.STRING)
	.addScalar("transportMethod", StandardBasicTypes.STRING)
	.addScalar("fromBatchName", StandardBasicTypes.STRING)
	.addScalar("fromBatchFile", StandardBasicTypes.STRING)
	.addScalar("totalMessages", StandardBasicTypes.INTEGER)
	.addScalar("srcOrgName", StandardBasicTypes.STRING)
        .addScalar("endUserDisplayText", StandardBasicTypes.STRING)
        .addScalar("threshold", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("batchUploadId", StandardBasicTypes.INTEGER)
        .addScalar("deleted", StandardBasicTypes.BOOLEAN)
        .addScalar("mergeable", StandardBasicTypes.BOOLEAN)
        .addScalar("lastDownloaded", StandardBasicTypes.TIMESTAMP)
        .addScalar("userId", StandardBasicTypes.INTEGER);        
        
        List<Object[]> results = query.getResultList();
        
        List<batchDownloads> batchSentMessages = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            batchDownloads bDownload = new batchDownloads();
            bDownload.setId((Integer) record[1]);
            bDownload.setDateSubmitted((Date) record[10]);
            bDownload.setStartDateTime((Date) record[11]);
            bDownload.setEndDateTime((Date) record[12]);
            bDownload.setUtBatchName((String) record[3]);
            bDownload.setOrgName((String) record[14]);
            bDownload.setConfigName((String) record[8]);
            bDownload.setTransportMethod((String) record[15]);
            bDownload.setTotalRecordCount((Integer) record[6]);
            bDownload.setErrorRecordCount((Integer) record[7]);
            bDownload.setTotalMessages((Integer) record[18]);
            bDownload.setStatusId((Integer) record[9]);
            bDownload.setEndUserDisplayText((String) record[20]);
            bDownload.setThreshold((Integer) record[21]);
            
            batchSentMessages.add(bDownload);
        });
        
        return batchSentMessages;
    }
    
    @Override
    @Transactional(readOnly = false)
    public void insertDMMessage(directmessagesout newDirectMessageOut) throws Exception {
	sessionFactory.getCurrentSession().persist(newDirectMessageOut);
    }
    
    @Override
    @Transactional(readOnly = false)
    public void populateOutboundAuditReport(Integer configId, Integer batchDownloadId, Integer batchUploadId, Integer batchUploadConfigId) throws Exception {
	try {
	    String sql = "call populateOutboundAuditReport(:configId, :batchDownloadId, :batchUploadId, :batchUploadConfigId);";
	    Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	    query.setParameter("configId", configId);
	    query.setParameter("batchDownloadId", batchDownloadId);
	    query.setParameter("batchUploadId", batchUploadId);
	    query.setParameter("batchUploadConfigId", batchUploadConfigId);
	    query.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("populateOutboundAuditReport " + ex.getCause());
	    ex.printStackTrace();
	}   
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<batchErrorSummary> getBatchErrorSummary(int batchId) throws Exception {
	try {
	    String sql = ("select count(e.id) as totalErrors, e.errorId, c.displayText as errorDisplayText "
	    + "from batchdownloadauditerrors e "
	    + "inner join lu_errorcodes c on c.id = e.errorId "
	    + "where e.batchDownloadId = :batchId group by e.errorId");

	    Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,batchErrorSummary.class)
	    .addScalar("errorDisplayText", StandardBasicTypes.STRING)
	    .addScalar("errorId", StandardBasicTypes.INTEGER)
	    .addScalar("totalErrors", StandardBasicTypes.INTEGER);
	    query.setParameter("batchId", batchId);

	    List<batchErrorSummary> batchErrorSummaries = query.list();

	    return batchErrorSummaries;

	} catch (Exception ex) {
	    System.err.println("getBatchErrorSummary " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }
    
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<batchdownloadactivity> getBatchActivities(batchDownloads batchInfo) {
	
	String sql = " select * from batchdownloadactivity where batchDownloadId = :batchId order by id asc";

	try {
	    Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,batchdownloadactivity.class)
	    .setParameter("batchId", batchInfo.getId());
            
	    List<batchdownloadactivity> batchActivities = query.list();

	    return batchActivities;

	} catch (Exception ex) {
	    System.err.println("getBatchActivities " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
    }
    
    /**
     * 
     * @param ba 
     */
    @Override
    @Transactional(readOnly = false)
    public void submitBatchActivityLog(batchdownloadactivity ba) {
	sessionFactory.getCurrentSession().persist(ba);
    }
    
    /**
     * errorId = 1 is required field missing* we do not re-check REL records
     * @param cff
     * @param batchDownloadId
     * @return 
     */
    @Override
    @Transactional(readOnly = false)
    public Integer insertFailedRequiredFields(configurationFormFields cff, Integer batchDownloadId) {
	
	try {
	    String sql = "insert into transactionouterrors_"+batchDownloadId
	    + " (batchDownloadId, configId, transactionOutRecordsId, fieldNo, errorid, required) "
	    + "select " + batchDownloadId + ", " + cff.getConfigId() + ", transactionOutRecordsId, " + cff.getFieldNo()
	    + ",1,1 from transactiontranslatedout_"+batchDownloadId + " where configId = :configId "
	    + "and (F" + cff.getFieldNo()+" is null "
	    + "or length(trim(F" + cff.getFieldNo() + ")) = 0 "
	    + "or length(REPLACE(REPLACE(F" + cff.getFieldNo() + ", '\n', ''), '\r', '')) = 0) "
	    + "and configId = :configId and (statusId is null or statusId not in (:transRELId));";
	    
	    Query insertData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
	    .setParameter("configId", cff.getConfigId())
	    .setParameterList("transRELId", transRELId);
	    
	    insertData.executeUpdate();
	    
	    sql = "select count(id) as total from transactionouterrors_" + batchDownloadId + " where errorId = 1 and fieldNo = " + cff.getFieldNo();
	    Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class).addScalar("total", StandardBasicTypes.INTEGER);
	    
	    return (Integer) query.list().get(0);
	    
	} catch (Exception ex) {
	    System.err.println("insertFailedRequiredFields  failed for outbound batch - " + batchDownloadId + " " + ex.getCause());
	    ex.printStackTrace();
	    
	    return 9999999;
	}
    }
    
    /**
     * The 'getMissingRequiredField' function will query to see if the required field is missing
     *
     * @param batchDownloadId
     * @param configId
     * @param fieldNo
     * @return This function will return a list of internal status codes
     */
    @Transactional(readOnly = true)
    public List getMissingRequiredField(Integer batchDownloadId,Integer configId,Integer fieldNo) {
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT id FROM transactionouterrors_"+batchDownloadId+" where errorId = 1 and batchDownloadId = :batchDownloadId and configId = :configId and fieldNo = :fieldNo", String.class);
	query.setParameter("configId", configId);
	query.setParameter("batchDownloadId", batchDownloadId);
	query.setParameter("fieldNo", fieldNo);
	
	return query.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Integer getTotalErrors(Integer batchId) throws Exception {

	String sql = "select * from transactionouterrors_"+batchId + " where required = 1";

	Query getTotalErrors = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	
	if(getTotalErrors.list() == null) {
	    return 0;
	}
	else {
	    return getTotalErrors.list().size();
	}
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<directmessagesout> getDirectMessagesOutListPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	
	String dateSQLString = "";
	String dateSQLStringTotal = "";
	
	if(!"".equals(fromDate)) {
	    dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";
	    dateSQLStringTotal += "dateCreated between '"+mysqlDateFormat.format(fromDate)+" 00:00:00' ";
	    
	    if(!"".equals(toDate)) {
		dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
		dateSQLStringTotal += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
	    }
	    else {
		dateSQLString += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
		dateSQLStringTotal += "AND '"+mysqlDateFormat.format(fromDate)+" 23:59:59'";
	    }
	}
	else {
	    if(!"".equals(toDate)) {
		dateSQLString += "a.dateCreated between '"+mysqlDateFormat.format(toDate)+" 00:00:00' ";
		dateSQLString += "AND '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
	    }
	    else {
		dateSQLString += "a.id > 0";
	    }
	}
	
	String sqlQuery = "select id, statusName, orgName, dateCreated, configId, batchDownloadId, batchName, totalMessages "
	+ "from ("
	+ "select a.id, a.batchDownloadId, a.dateCreated, a.configId, IFNULL(b.orgName,\"\") as orgName,"
	+ "CASE WHEN a.statusId = 1 THEN 'To be processed' WHEN a.statusId = 2 THEN 'Processed' ELSE 'Rejected' END as statusName,"
	+ "IFNULL(c.utBatchName,\"\") as batchName,"
	+ "(select count(id) as total from directmessagesout where "+dateSQLStringTotal+") as totalMessages "
	+ "FROM directmessagesout a left outer join  "
	+ "organizations b on b.id = a.orgId inner join  "
	+ "batchdownloads c on c.id = a.batchDownloadId "
	+ "where " + dateSQLString + ") as messagesOut ";
	
	if(!"".equals(searchTerm)){
	    sqlQuery += " where ("
	    + "id like '%"+searchTerm+"%' "
	    + "OR configId like '%"+searchTerm+"%' "
	    + "OR orgName like '%"+searchTerm+"%' "
	    + "OR batchName like '%"+searchTerm+"%' "
	    + "OR statusName like '%"+searchTerm+"%' "
	    + "OR dateCreated like '%"+searchTerm+"%' "
	    + ") ";
	}	
	
	sqlQuery += "order by "+sortColumnName+" "+sortDirection;
	
	if(displayRecords > 0) {
	    sqlQuery += " limit " + displayStart + ", " + displayRecords;
	}
	else {
	    sqlQuery += " limit " + displayStart+ ", 1000000";
	}
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlQuery,directmessagesout.class)
	    .addScalar("id", StandardBasicTypes.INTEGER)
	    .addScalar("statusName", StandardBasicTypes.STRING)
	    .addScalar("orgName", StandardBasicTypes.STRING)
	    .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
	    .addScalar("batchDownloadId", StandardBasicTypes.INTEGER)
	    .addScalar("configId", StandardBasicTypes.INTEGER)
	    .addScalar("batchName", StandardBasicTypes.STRING)
	    .addScalar("totalMessages", StandardBasicTypes.INTEGER);
	
	List<directmessagesout> directmessagesout = query.list();
	
        return directmessagesout;
    }
    
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public directmessagesout getDirectAPIMessagesById(Integer directMessageId) {
	//1 if list of statusId is null, we get all
	try {
            
            CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<directmessagesout> criteria = builder.createQuery(directmessagesout.class);
            Root<directmessagesout> root = criteria.from(directmessagesout.class);

            Predicate whereClause = builder.equal(root.get("id"), directMessageId);

            criteria.where(whereClause);

            List<directmessagesout> directAPIMessages = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
            
	    if (!directAPIMessages.isEmpty()) {
		return directAPIMessages.get(0);
	    }
	} catch (Exception ex) {
	    System.err.println("getDirectAPIMessagesById " + ex.getCause());
	    ex.printStackTrace();
	    return null;
	}
	return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List getErrorReportField(Integer batchDownloadId) throws Exception {
	
	String sql = "select rptField1.rptLabel1 ,rptField2.rptLabel2,rptField3.rptLabel3,rptField4.rptLabel4 "
	+ "from batchdownloadauditerrors e inner join "
	+ "configurationmessagespecs cs on e.configId = cs.configId inner join "
	+ "("
	+ "select fieldDesc as rptLabel1, fieldNo, configId "
	+ "from configurationformfields"
	+ ") rptField1 on rptField1.fieldNo = cs.rptField1 and rptField1.configId = e.configId inner join "
	+ "("
	+ "select fieldDesc as rptLabel2, fieldNo, configId "
	+ "from configurationformfields"
	+ ") rptField2 on rptField2.fieldNo = cs.rptField2 and rptField2.configId = e.configId inner join "
	+ "("
	+ "select fieldDesc as rptLabel3, fieldNo, configId "
	+ "from configurationformfields"
	+ ") rptField3 on rptField3.fieldNo = cs.rptField3 and rptField3.configId = e.configId inner join "
	+ "("
	+ "select fieldDesc as rptLabel4, fieldNo, configId "
	+ "from configurationformfields"
	+ ") rptField4 on rptField4.fieldNo = cs.rptField4 and rptField4.configId = e.configId "
	+ "where e.batchDownloadId = :batchDownloadId limit 1";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	query.setParameter("batchDownloadId", batchDownloadId);

	return query.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloadDroppedValues> getBatchDroppedValues(Integer batchDownloadId) throws Exception {
	SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from batchDownloadDroppedValues where batchDownloadId = :batchDownloadId");
	query.setParameter("batchDownloadId", batchDownloadId);
	
	return query.list();
    }
    
    @Override
    @Transactional(readOnly = false)
    public void updateMissingRequiredFieldStatus(Integer batchDownloadId) throws Exception {
	String sql = "update transactiontranslatedout_"+batchDownloadId + " set statusId = 14 where transactionOutRecordsId "
			+ "in (select transactionOutRecordsId "
		+ " from transactionouterrors_"+batchDownloadId + " where errorId = 1)";
	
	Query updateData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	try {
	    updateData.executeUpdate();
	} catch (Exception ex) {
	    System.err.println("updateMissingRequiredFieldStatus failed." + ex);
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public Integer genericValidation(configurationFormFields cff, Integer validationTypeId, Integer batchDownloadId, String regEx) {

	String sql = "call insertValidationErrorsOutbound(:vtType, :fieldNo, :batchDownloadId, :configId, :transactionId, :isFieldRequired)";

	Query insertError = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	insertError.setParameter("vtType", cff.getValidationType());
	insertError.setParameter("fieldNo", cff.getFieldNo());
	insertError.setParameter("batchDownloadId", batchDownloadId);
	insertError.setParameter("configId", cff.getConfigId());
	insertError.setParameter("transactionId", 0);
	insertError.setParameter("isFieldRequired", cff.isRequired());

	try {
	    insertError.executeUpdate();
	    
	    sql = "select count(id) as total from transactionouterrors_" + batchDownloadId + " where errorId = 2 and fieldNo = " + cff.getFieldNo();
	    Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
            .addScalar("total", StandardBasicTypes.INTEGER);
	    
	    return (Integer) query.list().get(0);
	    
	} catch (Exception ex) {
	    System.err.println("genericValidation " + ex.getCause());
	    ex.printStackTrace();
	    transactionInManager.insertProcessingError(processingSysErrorId, cff.getConfigId(), batchDownloadId, cff.getFieldNo(),
		    null, null, validationTypeId, false, true, ("-" + ex.getCause().toString()));
	    return 0; //we return error count of 1 when error
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void clearBatchActivityLogTable(Integer batchDownloadId) {
	MutationQuery deletActivityLog = sessionFactory.getCurrentSession().createMutationQuery("delete from batchdownloadactivity where batchDownloadId = :batchDownloadId");
	deletActivityLog.setParameter("batchDownloadId", batchDownloadId);

	deletActivityLog.executeUpdate();
    }

    @Transactional(readOnly = true)
    public String getConfigFieldHeadingsForOutput(Integer configId) {
	String sql = "select group_concat(CONCAT(\"'\", fieldDesc, \"'\") order by fieldNo asc) as fieldHeadings "
	+ "from configurationFormFields where configId = " + configId;

	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	List<String> fieldHeadings = query.list();
	if (!fieldHeadings.isEmpty()) {
		return fieldHeadings.get(0);
	} else {
		return null;
	}
    }
    
    /**
     * getBatchesByOrgId - return uploaded batch info for specific orgId
     *
     * @param orgId
     * @return This function will return a list of batches.
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<batchDownloads> getBatchesByOrgId(Integer orgId) {
	
        try {
            CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<batchDownloads> criteria = builder.createQuery(batchDownloads.class);
            Root<batchDownloads> root = criteria.from(batchDownloads.class);

            Predicate whereClause = builder.equal(root.get("orgId"), orgId);

            criteria.where(whereClause);

            return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
            
	} 
        catch (Exception ex) {
	    System.err.println("/*** getBatchesByOrgId " + ex.getCause().getMessage());
	    return null;
	}
    }
    
    /**
     * The 'getTransactionRecords' function will return the transaction TARGET records for the passed in transactionId.
     *
     * @param batchId
     * @param configId
     * @param totalFields
     * @return 
     * @throws java.lang.Exception 
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<transactionOutRecords> getTransactionRecordsForFP(Integer batchId, Integer configId, Integer totalFields) throws Exception {
	
	//totalFields = totalFields + 10;
        
	String sql = "select ";
		
	for (int i = 1; i <= totalFields; i++) {
	    sql += "a.f" + i + ",";
	}	
	sql += "a.id, b.transactionInRecordsId from transactiontranslatedout_" + batchId + " a inner join "
	+ "transactionoutrecords_" + batchId + " b on b.id = a.transactionOutRecordsId "
	+ "order by a.id asc";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,transactionOutRecords.class);

	List<transactionOutRecords> records = query.list();
	
	return records;
    }
    
    @Override
    @Transactional(readOnly = false)
    public Integer writeFPOutputToTextFile(configurationTransport transportDetails, Integer batchDownloadId, String filePathAndName, String fieldNos) {

	String sql = "";

	if(transportDetails.isAddTargetFileHeaderRow()) {
	    String configFieldHeadings = getConfigFieldHeadingsForOutput(transportDetails.getConfigId());
	    if(configFieldHeadings != null) {
		if(!"".equals(configFieldHeadings)) {
		    sql += "SELECT " + configFieldHeadings + " UNION ALL ";
		}
	    }
	}

	sql += "SELECT " + fieldNos.replace("(F", "(a.F") + ", b.transactionInRecordsId "
	+ "FROM transactionTranslatedOut_" + batchDownloadId + " a inner join "
	+ "transactionoutrecords_" + batchDownloadId + " b on b.id = a.transactionOutRecordsId "
	+ "where a.configId = " + transportDetails.getConfigId() + " and ";

	if(transportDetails.getErrorHandling() == 4) {
	    sql += "a.statusId in (9,14) ";
	}
	else {
	    sql += "a.statusId = 9 ";
	}
	sql += "INTO OUTFILE  '" + filePathAndName + "' "
	+ "FIELDS TERMINATED BY '" + transportDetails.getDelimChar()+"' LINES TERMINATED BY '\\n';";
	
	if (!"".equals(sql)) {
	    
	    Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);

	    if (transportDetails.getFileType() == 12) {
		query.setParameter("batchConfigId", transportDetails.getConfigId());
		query.setParameter("batchDownloadId", batchDownloadId);
		query.setParameter("filePathAndName", filePathAndName);
		query.setParameter("jsonWrapperElement", transportDetails.getJsonWrapperElement());
	    }

	    try {
		query.list();
	    } catch (Exception ex) {}
	}

	return 0;
    }
}