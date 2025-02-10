package com.hel.ut.dao.impl;

import com.hel.ut.dao.RestAPIDAO;
import java.util.Date;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.model.RestAPIMessagesIn;
import com.hel.ut.model.RestAPIMessagesOut;
import com.hel.ut.model.batchDownloads;
import com.hel.ut.model.batchUploads;
import java.text.SimpleDateFormat;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;

/**
 * The WebServicesDAOImpl class will implement the DAO access layer to handle updates for web services messages
 *
 *
 * @author gchan
 *
 */
@Repository
public class RestAPIDAOImpl implements RestAPIDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    private SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesIn> getRestAPIMessagesInList(Date fromDate, Date toDate,Integer fetchSize, String batchName) throws Exception {

        Integer batchUploadId = 0;
	
	if(!"".equals(batchName)) {
            
            CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<batchUploads> criteria = builder.createQuery(batchUploads.class);
            Root<batchUploads> root = criteria.from(batchUploads.class);

            Predicate whereClause = builder.equal(root.get("utBatchName"), batchName);

            criteria.where(whereClause);

            List<batchUploads> uploads = sessionFactory.getCurrentSession().createQuery(criteria).setMaxResults(1).getResultList();
	    
	    if(!uploads.isEmpty()) {
		batchUploads batchUploadDetails = (batchUploads) uploads.get(0);
		batchUploadId = batchUploadDetails.getId();
	    }
	}
	
	String sqlQuery = "select id, orgId, statusId, errorId, dateCreated, batchUploadId, configId "
		    + "from restapimessagesin "
		    + "where id > 0";
	
	if(!"".equals(fromDate)) {
	    sqlQuery += " and dateCreated >= '"+mysqlDateFormat.format(fromDate)+" 00:00:00'";
	}
	
	 if (!"".equals(toDate)) {
            sqlQuery += " and dateCreated < '"+mysqlDateFormat.format(toDate)+" 23:59:59'";
        }
	 
	if(batchUploadId > 0) {
	    sqlQuery += " and batchUploadId = '"+batchUploadId+"'";
	}
	 
	sqlQuery += " order by dateCreated desc";
	
	if(fetchSize > 0) {
	    sqlQuery += " limit " + fetchSize;
	}
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlQuery,RestAPIMessagesIn.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("orgId", StandardBasicTypes.INTEGER)
        .addScalar("statusId", StandardBasicTypes.INTEGER)
        .addScalar("errorId", StandardBasicTypes.INTEGER)
        .addScalar("dateCreated", StandardBasicTypes.DATE)
        .addScalar("batchUploadId", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER);

	List<RestAPIMessagesIn> apimessagesin = query.list();
	
        return apimessagesin;

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public RestAPIMessagesIn getRestAPIMessagesIn(Integer messageId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<RestAPIMessagesIn> criteria = builder.createQuery(RestAPIMessagesIn.class);
        Root<RestAPIMessagesIn> root = criteria.from(RestAPIMessagesIn.class);

        Predicate whereClause = builder.equal(root.get("id"), messageId);

        criteria.where(whereClause);
        
        List<RestAPIMessagesIn> apiMessageList = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        
        if (apiMessageList.size() == 1) {
            return apiMessageList.get(0);
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesOut> getRestAPIMessagesOutList(Date fromDate, Date toDate,Integer fetchSize, String batchName) throws Exception {

        Integer batchDownloadId = 0;
	
	if(!"".equals(batchName)) {
            
            CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<batchDownloads> criteria = builder.createQuery(batchDownloads.class);
            Root<batchDownloads> root = criteria.from(batchDownloads.class);

            Predicate whereClause = builder.equal(root.get("utBatchName"), batchName);

            criteria.where(whereClause);

            List<batchDownloads> downloads = sessionFactory.getCurrentSession().createQuery(criteria).setMaxResults(1).getResultList();
	    
	    if(!downloads.isEmpty()) {
		batchDownloads batchDownloadDetails = (batchDownloads) downloads.get(0);
		batchDownloadId = batchDownloadDetails.getId();
	    }
	}
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<RestAPIMessagesOut> criteria = builder.createQuery(RestAPIMessagesOut.class);
        Root<RestAPIMessagesOut> root = criteria.from(RestAPIMessagesOut.class);
        
        Predicate fromDateSearch = null;
        Predicate toDateSearch = null;
        Predicate batchDownloadIdSearch = null;
        Predicate whereClause = null;
        
        if (!"".equals(fromDate)) {
            fromDateSearch = builder.greaterThanOrEqualTo(root.get("dateCreated"), fromDate);
        }
        
        if (!"".equals(toDate)) {
            toDateSearch = builder.lessThan(root.get("dateCreated"), toDate);
        }
        
        if(batchDownloadId > 0) {
            batchDownloadIdSearch = builder.equal(root.get("batchDownloadId"), batchDownloadId);
        }
        
        if(fromDateSearch != null && toDateSearch != null && batchDownloadIdSearch != null) {
            whereClause = builder.and(fromDateSearch,toDateSearch,batchDownloadIdSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch != null && toDateSearch != null && batchDownloadIdSearch == null) {
            whereClause = builder.and(fromDateSearch,toDateSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch != null && toDateSearch == null && batchDownloadIdSearch != null) {
            whereClause = builder.and(fromDateSearch,batchDownloadIdSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch == null && toDateSearch != null && batchDownloadIdSearch != null) {
            whereClause = builder.and(toDateSearch,batchDownloadIdSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch != null && toDateSearch == null && batchDownloadIdSearch == null) {
            whereClause = builder.and(fromDateSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch == null && toDateSearch != null && batchDownloadIdSearch == null) {
            whereClause = builder.and(toDateSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch == null && toDateSearch == null && batchDownloadIdSearch != null) {
            whereClause = builder.and(batchDownloadIdSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        
        List<RestAPIMessagesOut> apiOutMessages = null;
                
        if (fetchSize > 0) {
            apiOutMessages = sessionFactory.getCurrentSession().createQuery(criteria).setMaxResults(fetchSize).getResultList();
        }
        else {
            apiOutMessages = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        }
        
        return apiOutMessages;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesIn> getRestAPIMessagesInListPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	
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
	
	
	String sqlQuery = "select id, statusName, errorDisplayText, orgName, dateCreated, configId, batchUploadId, batchName, totalMessages "
		+ "from ("
		+ "select a.id, a.batchUploadId, a.dateCreated, a.configId, c.orgName,"
		+ "CASE WHEN a.statusId = 1 THEN 'To be processed' WHEN a.statusId = 2 THEN 'Processed' ELSE 'Rejected' END as statusName,"
		+ "IFNULL(b.displayText, \"N/A\") as errorDisplayText, IFNULL(d.utBatchName,\"\") as batchName,"
		+ "(select count(id) as total from restapimessagesin where "+dateSQLStringTotal+") as totalMessages "
		+ "FROM restapimessagesin a left outer join "
		+ "lu_errorCodes b on b.id = a.errorId inner join "
		+ "organizations c on c.id = a.orgId left outer join  "
		+ "batchuploads d on d.id = a.batchUploadId "
		+ "where " + dateSQLString + ") as messagesIn ";
	
	if(!"".equals(searchTerm)){
	    sqlQuery += " where ("
	    + "id like '%"+searchTerm+"%' "
	    + "OR configId like '%"+searchTerm+"%' "
	    + "OR orgName like '%"+searchTerm+"%' "
	    + "OR batchName like '%"+searchTerm+"%' "
	    + "OR statusName like '%"+searchTerm+"%' "
	    + "OR dateCreated like '%"+searchTerm+"%' "
	    + "OR errorDisplayText like '%"+searchTerm+"%'"
	    + ") ";
	}	
	
	sqlQuery += "order by "+sortColumnName+" "+sortDirection;
	if(displayRecords > 0) {
	    sqlQuery += " limit " + displayStart + ", " + displayRecords;
	}
	else {
	    sqlQuery += " limit " + displayStart+ ", 1000000";
	}
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlQuery,RestAPIMessagesIn.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("statusName", StandardBasicTypes.STRING)
        .addScalar("errorDisplayText", StandardBasicTypes.STRING)
        .addScalar("orgName", StandardBasicTypes.STRING)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("batchUploadId", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("batchName", StandardBasicTypes.STRING)
        .addScalar("totalMessages", StandardBasicTypes.INTEGER);
	
	List<RestAPIMessagesIn> apimessagesin = query.list();
	
        return apimessagesin;

    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<RestAPIMessagesOut> getRestAPIMessagesOutListPaged(Date fromDate, Date toDate, Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
	
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
	
	
	String sqlQuery = "select id, statusName, errorDisplayText, orgName, dateCreated, configId, batchDownloadId, batchName, totalMessages "
		+ "from ("
		+ "select a.id, a.batchDownloadId, a.dateCreated, a.configId, c.orgName,"
		+ "CASE WHEN a.statusId = 1 THEN 'To be processed' WHEN a.statusId = 2 THEN 'Processed' ELSE 'Rejected' END as statusName,"
		+ "IFNULL(b.displayText, \"N/A\") as errorDisplayText, IFNULL(d.utBatchName,\"\") as batchName,"
		+ "(select count(id) as total from restapimessagesout where "+dateSQLStringTotal+") as totalMessages "
		+ "FROM restapimessagesout a left outer join "
		+ "lu_errorCodes b on b.id = a.errorId inner join "
		+ "organizations c on c.id = a.orgId left outer join  "
		+ "batchdownloads d on d.id = a.batchDownloadId "
		+ "where " + dateSQLString + ") as messagesIn ";
	
	if(!"".equals(searchTerm)){
	    sqlQuery += " where ("
	    + "id like '%"+searchTerm+"%' "
	    + "OR configId like '%"+searchTerm+"%' "
	    + "OR orgName like '%"+searchTerm+"%' "
	    + "OR batchName like '%"+searchTerm+"%' "
	    + "OR statusName like '%"+searchTerm+"%' "
	    + "OR dateCreated like '%"+searchTerm+"%' "
	    + "OR errorDisplayText like '%"+searchTerm+"%'"
	    + ") ";
	}	
	
	sqlQuery += "order by "+sortColumnName+" "+sortDirection;
	if(displayRecords > 0) {
	    sqlQuery += " limit " + displayStart + ", " + displayRecords;
	}
	else {
	    sqlQuery += " limit " + displayStart+ ", 1000000";
	}
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlQuery,RestAPIMessagesOut.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("statusName", StandardBasicTypes.STRING)
        .addScalar("errorDisplayText", StandardBasicTypes.STRING)
        .addScalar("orgName", StandardBasicTypes.STRING)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("batchDownloadId", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("batchName", StandardBasicTypes.STRING)
        .addScalar("totalMessages", StandardBasicTypes.INTEGER);
	
	List<RestAPIMessagesOut> apimessagesout = query.list();
	
        return apimessagesout;

    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public RestAPIMessagesOut getRestAPIMessagesOut(Integer messageId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<RestAPIMessagesOut> criteria = builder.createQuery(RestAPIMessagesOut.class);
        Root<RestAPIMessagesOut> root = criteria.from(RestAPIMessagesOut.class);

        Predicate whereClause = builder.equal(root.get("id"), messageId);

        criteria.where(whereClause);
        
        List<RestAPIMessagesOut> apiMessageList = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        
        if (apiMessageList.size() == 1) {
            return apiMessageList.get(0);
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public RestAPIMessagesIn getRestAPIMessagesInByBatchId(Integer batchId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<RestAPIMessagesIn> criteria = builder.createQuery(RestAPIMessagesIn.class);
        Root<RestAPIMessagesIn> root = criteria.from(RestAPIMessagesIn.class);

        Predicate whereClause = builder.equal(root.get("batchUploadId"), batchId);

        criteria.where(whereClause);
        
        List<RestAPIMessagesIn> apiMessageList = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        
        if (apiMessageList.size() == 1) {
            return apiMessageList.get(0);
        } else {
            return null;
        }
    }
}