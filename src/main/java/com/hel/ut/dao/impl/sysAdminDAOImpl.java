package com.hel.ut.dao.impl;

import java.util.List;
import java.util.Properties;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.dao.sysAdminDAO;
import com.hel.ut.dao.UtilitiesDAO;
import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.Macros;
import com.hel.ut.model.custom.LookUpTable;
import com.hel.ut.model.custom.TableData;
import com.hel.ut.model.lutables.lu_ProcessStatus;
import com.hel.ut.model.MoveFilesLog;
import com.hel.ut.model.mainHL7Details;
import com.hel.ut.model.mainHL7Elements;
import com.hel.ut.model.mainHL7Segments;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * @see com.hel.ut.dao.sysAdminDAO
 * @author gchan
 */
@Repository
public class sysAdminDAOImpl implements sysAdminDAO {

    @Autowired
    private UtilitiesDAO udao;

    @Autowired
    private SessionFactory sessionFactory;

    @Resource(name = "myProps")
    private Properties myProps;

    
    /**
     * this method takes the table name and searchTerm (if there is one) and return the data in the table
     *
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<TableData> getDataList(String utTableName, String searchTerm) {

        String sql = "select id, displayText, description, "
        + " isCustom as custom, status as status, dateCreated as dateCreated from "
        + utTableName + " where (displayText like :searchTerm or description like :searchTerm) order by id";
        
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,TableData.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("displayText", StandardBasicTypes.STRING)
        .addScalar("description", StandardBasicTypes.STRING)
        .addScalar("custom", StandardBasicTypes.BOOLEAN)
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .addScalar("dateCreated", StandardBasicTypes.DATE)
        .setParameter("searchTerm", searchTerm);

        List<TableData> dataList = query.list();
        
        return dataList;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findTotalDataRows(String utTableName) {
        String sql = "select count(id) as rowCount from " + utTableName;
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class).addScalar("rowCount", StandardBasicTypes.INTEGER);
        Integer rowCount = (Integer) query.list().get(0);

        return rowCount;
    }

    @Override
    @Transactional(readOnly = true)
    public LookUpTable getTableInfo(String urlId) {

        LookUpTable lookUpTable = new LookUpTable();
        Query query = sessionFactory.getCurrentSession().createNativeQuery(""
        + "select utTableName, "
        + "displayText as displayName, "
        + "urlId, description, "
        + "dateCreated from lookUpTables where urlId = :urlId",LookUpTable.class)
        .addScalar("utTableName", StandardBasicTypes.STRING)
        .addScalar("displayName", StandardBasicTypes.STRING)
        .addScalar("urlId", StandardBasicTypes.STRING)
        .addScalar("description", StandardBasicTypes.STRING)
        .setParameter("urlId", urlId);

        if (query.list().size() == 1) {
            lookUpTable = (LookUpTable) query.list().get(0);
        }

        return lookUpTable;
    }

    /**
     * this method deletes the data item in the table*
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteDataItem(String utTableName, int id) {
        String sql = "delete from " + utTableName + " where id = :id";
        
        Query deleteTable = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .setParameter("id", id);
        
        try {
            deleteTable.executeUpdate();
            return true;
        } catch (Throwable ex) {
            System.err.println("deleteDataItem failed." + ex);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TableData getTableData(Integer id, String utTableName) {
        //we create sql, we transform
        TableData tableData = new TableData();
        
        String sql = "select id, displayText, description, isCustom as custom, status from " + utTableName + " where id = :id";
        
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,TableData.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("displayText", StandardBasicTypes.STRING)
        .addScalar("description", StandardBasicTypes.STRING)
        .addScalar("custom", StandardBasicTypes.BOOLEAN)
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .setParameter("id", id);

        if (query.list().size() == 1) {
            tableData = (TableData) query.list().get(0);
        }
        
        return tableData;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean updateTableData(TableData tableData, String utTableName) {
        boolean updated = false;
        String sql = "update " + utTableName+ " set displayText = :displayText, description = :description, status = :status, isCustom = :isCustom where id = :id";
        
        Query updateData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
        .addScalar("displayText", StandardBasicTypes.STRING)
        .addScalar("description", StandardBasicTypes.STRING)
        .addScalar("isCustom", StandardBasicTypes.BOOLEAN)
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .setParameter("displayText", tableData.getDisplayText())
        .setParameter("description", tableData.getDescription())
        .setParameter("isCustom", tableData.isCustom())
        .setParameter("status", tableData.isStatus())
        .setParameter("id", tableData.getId());
        
        try {
            updateData.executeUpdate();
            updated = true;
        } catch (Throwable ex) {
            System.err.println("update table data failed." + ex);
        }
        
        return updated;
    }

    @Override
    @Transactional(readOnly = false)
    public void createTableDataHibernate(TableData tableData, String utTableName) {

        String sql = "insert into " + utTableName + " (displayText, description, isCustom, status) values (:displayText, :description, :isCustom, :status)";
        
        Query insertData = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
        .addScalar("displayText", StandardBasicTypes.STRING)
        .addScalar("description", StandardBasicTypes.STRING)
        .addScalar("isCustom", StandardBasicTypes.BOOLEAN)
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .setParameter("displayText", tableData.getDisplayText())
        .setParameter("description", tableData.getDescription())
        .setParameter("isCustom", tableData.isCustom())
        .setParameter("status", tableData.isStatus());
        
        try {
            insertData.executeUpdate();
        } catch (Throwable ex) {
            System.err.println("insert table data failed." + ex);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Macros> getMarcoList(String searchTerm) {

        Query query = sessionFactory.getCurrentSession().createQuery("from Macros where "
                + "macro_short_name like :searchTerm OR macro_name like :searchTerm "
                + "order by categoryId, macro_short_name asc");
        query.setParameter("searchTerm", "%"+searchTerm+"%");

        return query.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Long findTotalMacroRows() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalMacros from Macros");
        Long totalMacros = (Long) query.uniqueResult();
        return totalMacros;
    }

    @Override
    @Transactional(readOnly = true)
    public Long findtotalHL7Entries() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalHL7 from mainHL7Details");
        Long totalHL7Entries = (Long) query.uniqueResult();
        return totalHL7Entries;
    }

    @Override
    @Transactional(readOnly = true)
    public Long findtotalNewsArticles() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalArticles from newsArticle");
        Long totalNewsArticles = (Long) query.uniqueResult();
        return totalNewsArticles;
    }

    /**
     * this method deletes the macro in the table*
     */
    @Override
    @Transactional(readOnly = false)
    public boolean deleteMacro(int id) {
        Query deletMarco = sessionFactory.getCurrentSession().createQuery("delete from Macros where id = :macroId)");
        deletMarco.setParameter("macroId", id);
        deletMarco.executeUpdate();
        try {
            deletMarco.executeUpdate();
            return true;
        } catch (Throwable ex) {
            System.err.println("delete macro failed." + ex);
            return false;
        }
    }

    /**
     * this method adds a macro*
     */
    @Override
    @Transactional(readOnly = false)
    public void createMacro(Macros macro) {
        try {
            sessionFactory.getCurrentSession().save(macro);
        } catch (Throwable ex) {
            System.err.println("create macro failed." + ex);

        }
    }

    @Override
    @Transactional(readOnly = false)
    public boolean updateMacro(Macros macro) {
        try {
            sessionFactory.getCurrentSession().update(macro);
            return true;
        } catch (Throwable ex) {
            System.err.println("update macro failed." + ex);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void createProcessStatus(lu_ProcessStatus lu) {
        try {
            sessionFactory.getCurrentSession().save(lu);
        } catch (Throwable ex) {
            System.err.println("create ProcessStatus failed." + ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public lu_ProcessStatus getProcessStatusById(int id) throws Exception {
        try {
            return (lu_ProcessStatus) sessionFactory.getCurrentSession().get(lu_ProcessStatus.class, id);
        } catch (Throwable ex) {
            System.err.println("get ProcessStatus failed." + ex);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateProcessStatus(lu_ProcessStatus lu) {
        try {
            sessionFactory.getCurrentSession().update(lu);
        } catch (Throwable ex) {
            System.err.println("update ProcessStatus failed." + ex);
        }
    }


    /**
     * The 'getHL7List' function will return the list of saved hl7 standard versions.
     */
    @Override
    @Transactional(readOnly = true)
    public List<mainHL7Details> getHL7List() throws Exception {

        Query query = sessionFactory.getCurrentSession().createQuery("from mainHL7Details order by id desc");

        List<mainHL7Details> HL7List = query.list();
        return HL7List;
    }

    /**
     * The 'getHL7Details' function will the HL7 details for the passed in hl7 version.
     *
     * @Table HL7Specs
     *
     * @param	hl7Id This will hold the id to find
     *
     * @return	This function will return a HL7Details object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public mainHL7Details getHL7Details(int hl7Id) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<mainHL7Details> criteria = builder.createQuery(mainHL7Details.class);
        Root<mainHL7Details> root = criteria.from(mainHL7Details.class);

        Predicate whereClause = builder.equal(root.get("id"), hl7Id);

        criteria.where(whereClause);
        
        mainHL7Details HL7Details = (mainHL7Details) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();

        if (HL7Details == null) {
            return null;
        } else {
            return HL7Details;
        }
    }

    /**
     * The 'getHL7Segments' function will return the list of segments for a specific HL7 Message.
     *
     * @Table configurationHL7Segments
     *
     * @return This function will return a list of HL7Segment objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<mainHL7Segments> getHL7Segments(int hl7Id) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<mainHL7Segments> criteria = builder.createQuery(mainHL7Segments.class);
        Root<mainHL7Segments> root = criteria.from(mainHL7Segments.class);

        Predicate whereClause = builder.equal(root.get("hl7Id"), hl7Id);

        criteria.orderBy(builder.asc(root.get("displayPos"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'getHL7Elements' function will return the list of elements for a specific HL7 Message segment.
     *
     * @Table configurationHL7Elements
     *
     * @return This function will return a list of HL7Elements objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<mainHL7Elements> getHL7Elements(int hl7Id, int segmentId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<mainHL7Elements> criteria = builder.createQuery(mainHL7Elements.class);
        Root<mainHL7Elements> root = criteria.from(mainHL7Elements.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = builder.equal(root.get("hl7Id"), hl7Id);
        predicates[1] = builder.equal(root.get("segmentId"), segmentId);

        Predicate whereClause = builder.and(predicates);

        criteria.orderBy(builder.asc(root.get("displayPos"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * the 'updateHL7Details' funciton will update/save the details of the HL7 message
     *
     * @param details The Hl7 details object
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Details(mainHL7Details details) {
        sessionFactory.getCurrentSession().saveOrUpdate(details);
    }

    /**
     * The 'updateHL7Segments' function will update the segment passed to the function.
     *
     * @param segment The segment object to update
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Segments(mainHL7Segments segment) {
        sessionFactory.getCurrentSession().update(segment);
    }

    /**
     * The 'updateHL7Elements' function will update the segment element passed to the function.
     *
     * @param element The segment element object to update.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Elements(mainHL7Elements element) {
        sessionFactory.getCurrentSession().update(element);
    }

    /**
     * The 'createHL7' function will save the new HL7 Segment
     *
     * @param HL7Details The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false)
    public int createHL7(mainHL7Details HL7Details) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(HL7Details);

        return lastId;
    }

    /**
     * The 'saveHL7Segment' function will save the new HL7 Segment
     *
     * @param newSegment The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Segment(mainHL7Segments newSegment) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(newSegment);

        return lastId;
    }

    /**
     * The 'saveHL7Element' function will save the new HL7 Segment Element
     *
     * @param newElement The object holding the new HL7 Element Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Element(mainHL7Elements newElement) {
        Integer lastId;

        lastId = (Integer) sessionFactory.getCurrentSession().save(newElement);

        return lastId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<lu_ProcessStatus> getAllProcessStatus() throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<lu_ProcessStatus> criteria = builder.createQuery(lu_ProcessStatus.class);
        Root<lu_ProcessStatus> root = criteria.from(lu_ProcessStatus.class);

        Predicate whereClause = builder.equal(root.get("status"), true);

        criteria.orderBy(builder.asc(root.get("category")),builder.asc(root.get("displayText"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<lu_ProcessStatus> getAllHistoryFormProcessStatus() throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<lu_ProcessStatus> criteria = builder.createQuery(lu_ProcessStatus.class);
        Root<lu_ProcessStatus> root = criteria.from(lu_ProcessStatus.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = builder.equal(root.get("status"), true);
        predicates[1] = root.get("id").in(new Integer[]{17, 31, 21, 14, 11, 9, 16, 20, 15, 25, 29, 8, 3, 23, 37, 33, 19, 12, 10});

        Predicate whereClause = builder.and(predicates);

        criteria.orderBy(builder.asc(root.get("category")),builder.asc(root.get("displayText"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long findTotalUsers() throws Exception {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalUsers from User");
        Long totalUsers = (Long) query.uniqueResult();
        return totalUsers;
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public List<MoveFilesLog> getMoveFilesLog(Integer statusId) throws Exception {
            
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<MoveFilesLog> criteria = builder.createQuery(MoveFilesLog.class);
        Root<MoveFilesLog> root = criteria.from(MoveFilesLog.class);
        
        if (statusId != null) {
            Predicate whereClause = builder.equal(root.get("statusId"), statusId);
            criteria.where(whereClause);
        }
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteMoveFilesLog(MoveFilesLog moveFileLog) throws Exception {
        Query deleteFields = sessionFactory.getCurrentSession().createQuery("delete from MoveFilesLog where id = :moveFilePathId");
        deleteFields.setParameter("moveFilePathId", moveFileLog.getId());
        deleteFields.executeUpdate();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long findTotalStandardCrosswalks() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalCrosswalks from crosswalks where orgId = 0");
        Long totalCrosswalks = (Long) query.uniqueResult();
        return totalCrosswalks;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Crosswalks> getStandardCrosswalks() throws Exception {
        Query query = sessionFactory.getCurrentSession().createQuery("from Crosswalks where orgId = 0 order by dateCreated desc");
        return query.list();
    }
}