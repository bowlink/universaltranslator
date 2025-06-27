package com.hel.ut.dao.impl;

import java.util.List;
import java.util.Properties;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.dao.messageTypeDAO;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.Crosswalks;
import com.hel.ut.model.validationType;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.SelectionQuery;
import org.hibernate.type.StandardBasicTypes;

/**
 * The brochureDAOImpl class will implement the DAO access layer to handle updates for organization brochures
 *
 *
 * @author chadmccue
 *
 */
@Repository
public class messageTypeDAOImpl implements messageTypeDAO {

    @Resource(name = "myProps")
    private Properties myProps;

    @Autowired
    private SessionFactory sessionFactory;

    
    /**
     * The 'findTotalCrosswalks' function will return the total number of generic crosswalks in the system
     *
     * @param orgId Will pass the orgId this will help determine if I want all crosswalks or generic system only crosswalks
     * @return 
     *
     * @Table	crosswalks
     *
     *
     * @Return	This function will return the total number of generic crosswalks set up in the system
     */
    @Override
    @Transactional(readOnly = true)
    public double findTotalCrosswalks(int orgId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Crosswalks> criteria = builder.createQuery(Crosswalks.class);
        Root<Crosswalks> root = criteria.from(Crosswalks.class);
        
        Predicate whereClause = null;
        
        if (orgId == 0) {
             whereClause = builder.equal(root.get("orgId"), 0);
        }
        else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = builder.equal(root.get("orgId"), 0);
            predicates[1] = builder.equal(root.get("orgId"), orgId);
            
            whereClause = builder.or(predicates);
        }

        criteria.where(whereClause);
        
        List<Crosswalks> croswalks = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();

        double totalCrosswalks = (double) croswalks.size();

        return totalCrosswalks;
    }

    /**
     * The 'getValidationTypes' function will return a list of available field validation types
     *
     * @return 
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getValidationTypes() {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT id, validationType FROM ref_validationTypes order by id asc", String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("validationType", StandardBasicTypes.STRING);
        return query.list();
    }

    /**
     * The 'getValidationById' function will return a validation by the passed in Id.
     *
     * @param id
     * @return 
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public String getValidationById(int id) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT validationType FROM ref_validationTypes where id = :id", String.class);
        query.setParameter("id", id);

        String validationType = (String) query.uniqueResult();

        return validationType;
    }

    /**
     * The 'getDelimiters' function will return a list of available file delimiters
     *
     * @return 
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List getDelimiters() {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT id, delimiter FROM ref_delimiters order by delimiter asc", String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("delimiter", StandardBasicTypes.STRING);       

        return query.getResultList();
    }

    /**
     * The 'getDelimiterChar' will return the actual character of the delimiter for the id passed into the function
     *
     * @param id	The id will hold the delimiter ID to retrieve its associated character
     * @return 
     *
     * @returns string
     */
    @Transactional(readOnly = true)
    @Override
    public String getDelimiterChar(int id) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT delimChar FROM ref_delimiters where id = :id", String.class);
        query.setParameter("id", id);

        String delimChar = (String) query.uniqueResult();

        return delimChar;
    }

    /**
     * The 'getCrosswalks' function will return the list of available crosswalks to associate a message types to.This function will only return crosswalks not associated to a specific organization.
     *
     * @param page	The current crosswalk page
     * @param	maxResults	The maximum number of crosswalks to return from each query
     * @param	orgId	The organization id (default 0)
     * @return 
     *
     * @Table	crosswalks
     *
     * @Return	This function will return a list of crosswalks
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Crosswalks> getCrosswalks(int page, int maxResults, int orgId) {

        SelectionQuery query;

        if (orgId == 0) {
            query = sessionFactory.getCurrentSession().createSelectionQuery("from Crosswalks where orgId = 0 order by name asc");
        } else {
            query = sessionFactory.getCurrentSession().createSelectionQuery("from Crosswalks where (orgId = 0 or orgId = :orgId) order by name asc");
            query.setParameter("orgId", orgId);
        }

        int firstResult = 0;

        //Set the parameters for paging
        //Set the page to load
        if (page > 1) {
            firstResult = (maxResults * (page - 1));
        }
        query.setFirstResult(firstResult);

        //Set the max results to display
        //If 0 is passed then we want all crosswalks
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        return query.list();
    }

    /**
     *
     * @param name
     * @param orgId
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public Long checkCrosswalkName(String name, int orgId) {
        SelectionQuery query;

        if (orgId > 0) {
            query = sessionFactory.getCurrentSession().createSelectionQuery("select count(id) as total from Crosswalks where name = :name and orgId = :orgId");
            query.setParameter("name", name);
            query.setParameter("orgId", orgId);
        } else {
            query = sessionFactory.getCurrentSession().createSelectionQuery("select count(id) as total from Crosswalks where name = :name");
            query.setParameter("name", name);
        }

        Long cwId = (Long) query.uniqueResult();

        return cwId;
    }

    /**
     * The 'createCrosswalk" function will create the new crosswalk
     *
     * @Table	crosswalks
     *
     * @param	crosswalkDetails	This will hold the crosswalk object from the form
     *
     * @return The function will return the id of the new crosswalk
     *
     */
    @Override
    @Transactional(readOnly = false)
    public Integer createCrosswalk(Crosswalks crosswalkDetails) {
        sessionFactory.getCurrentSession().persist(crosswalkDetails);
        return crosswalkDetails.getId();
    }

    /**
     * The 'getCrosswalk' function will return a single crosswalk object based on the id passed in.
     *
     * @param	cwId	This will be id to find the specific crosswalk
     *
     * @return	The function will return a crosswalk object
     */
    @Override
    @Transactional(readOnly = true)
    public Crosswalks getCrosswalk(int cwId) {
        return (Crosswalks) sessionFactory.getCurrentSession().get(Crosswalks.class, cwId);
    }

    /**
     * The 'getDelimiters' function will return a list of available file delimiters
     *
     * @param	cwId	This will be the id of the crosswalk to return the associated data elements for
     *
     * @return	The function will return a list of data objects for the crosswalk
     *
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public List<CrosswalkData> getCrosswalkData(int cwId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT * FROM rel_crosswalkData where crosswalkId = :crosswalkid order by id asc", CrosswalkData.class);
        query.setParameter("crosswalkid", cwId);

        return query.getResultList();
    }

    /**
     * The 'getFieldName' function will return the name of a field based on the fieldId passed in.This is used for display purposes to show the actual field lable instead of a field name.
     *
     * @param fieldId	This will hold the id of the field to retrieve
     * @return 
     *
     * @Return This function will return a string (field name)
     */
    @Override
    @Transactional(readOnly = true)
    public String getFieldName(int fieldId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT fieldDesc FROM messageTypeFormFields where id = :fieldId", String.class)
	.setParameter("fieldId", fieldId);

        String fieldName = (String) query.uniqueResult();

        return fieldName;
    }

    /**
     * The 'getCrosswalkName' function will return the name of a crosswalk based on the id passed in.
     *
     * @param cwId	This will hold the id of the crosswalk to retrieve
     * @return 
     *
     * @Return This function will return a string (crosswalk name).
     */
    @Override
    @Transactional(readOnly = true)
    public String getCrosswalkName(int cwId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Crosswalks> criteria = builder.createQuery(Crosswalks.class);
        Root<Crosswalks> root = criteria.from(Crosswalks.class);

        Predicate whereClause = builder.equal(root.get("id"), cwId);

        criteria.where(whereClause);
        
        Crosswalks cwDetails = (Crosswalks) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();

        String cwName = "";
	
	if(cwDetails != null) {
	    if (cwDetails.getOrgId() > 0) {
		cwName = cwDetails.getName() + " (Org Specific)";
	    } else {
		cwName = cwDetails.getName();
	    }
	}
        
        return cwName;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<validationType> getValidationTypes1() {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from validationType order by id asc");
        return query.list();
    }
    
    @Transactional(readOnly = false)
    @Override
    public void executeSQLStatement(String sqlStmt) {
	if(sqlStmt != null) {
	    if(!"".equals(sqlStmt)) {
		//Need to insert all the fields into the crosswalk data Fields table
		Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlStmt, String.class);
		query.executeUpdate();
	    }
	}
    }
    
    /**
     * The 'updateCrosswalk" function will update the existing crosswalk
     *
     * @Table	crosswalks
     *
     * @param	crosswalkDetails	This will hold the crosswalk object from the form
     *
     */
    @Transactional(readOnly = false)
    @Override
    public void updateCrosswalk(Crosswalks crosswalkDetails) {
        sessionFactory.getCurrentSession().merge(crosswalkDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List getCrosswalksWithData(Integer orgId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("select a.name,  b.sourceValue, b.targetValue, b.descValue, a.id, a.fileDelimiter from crosswalks a inner join rel_crosswalkdata b on b.crosswalkId = a.id where a.orgId = :orgId order by a.name asc", String.class);
        query.setParameter("orgId", orgId);

        return query.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Crosswalks> getCrosswalksForConfig(int page, int maxCrosswalks, int orgId, int configId, boolean inUseOnly) {
	
	String sql = "select distinct a.id, a.name, a.fileDelimiter, a.fileName, a.dateCreated, a.orgId, a.lastUpdated,"
        + "IFNULL((select id from configurationdatatranslations where configId = :configId and crosswalkId = a.id LIMIT 1),0) as dtsId "
	+ "from crosswalks a ";
	
	if(inUseOnly) {
	    sql += "inner join configurationdatatranslations b on (b.crosswalkid = a.id or (b.macroId in (129,160,177,195,199,201) and b.constant1 = a.id)) and b.configId = :configId ";
            sql += "where a.orgId = ";
	}	  
        else {
            sql += "where a.id not in (select crosswalkId from configurationdatatranslations where configId = :configId) ";
            sql += "and a.id not in (select constant1 from configurationdatatranslations where macroId in (129,160,177,195,199,201) and configId = :configId) ";
            sql += "and a.orgId = ";
        }
	
	if(orgId > 0) {
	    sql += ":orgId or a.orgId = 0 order by a.orgId desc, a.name asc";
	}
	else {
	    sql += "0 order by a.name asc";
	}
        
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,Crosswalks.class)
	.setParameter("orgId", orgId)
	.setParameter("configId", configId);
        
	List<Crosswalks> crosswalks = query.getResultList();
        
        return crosswalks;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List getCrosswalksWithDataByFileName(Integer orgId, String fileName) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("select b.sourceValue, b.targetValue, b.descValue, c.delimiter, c.delimChar from crosswalks a inner join rel_crosswalkdata b on b.crosswalkId = a.id inner join ref_delimiters c on c.id = a.fileDelimiter where a.orgId = :orgId and a.fileName = :fileName order by b.id asc", String.class);
        query.setParameter("orgId", orgId);
	query.setParameter("fileName", fileName);

        return query.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List getConfigCrosswalksWithData(Integer orgId, Integer configId) {
	
	String sql = "select a.name,  b.sourceValue, b.targetValue, b.descValue, a.id, a.fileDelimiter, a.dateCreated, ifnull(a.lastUpdated,a.dateCreated) as lastUpdated "
	+ "from crosswalks a inner join "
	+ "rel_crosswalkdata b on b.crosswalkId = a.id "
	+ "where a.orgId = :orgId "
	+ "and ((a.id in (select crosswalkId from configurationdatatranslations where configId = :configId)) OR (a.id in (select constant1 from configurationdatatranslations where configId = :configId))) "
	+ "order by a.name asc";
	
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
        query.setParameter("orgId", orgId);
	query.setParameter("configId", configId);

        return query.list();
    }
    
    /**
     * The 'getDelimiterById' will return the actual character of the delimiter for the id passed into the function
     *
     * @param id	The id will hold the delimiter ID to retrieve its associated character
     * @return 
     *
     * @returns string
     */
    @Transactional(readOnly = true)
    @Override
    public String getDelimiterById(int id) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT delimiter FROM ref_delimiters where id = :id", String.class);
        query.setParameter("id", id);

        String delimChar = (String) query.uniqueResult();

        return delimChar;
    }
    
    @Override
    @Transactional(readOnly = false)
    public void saveCrosswalkData(CrosswalkData cwData) {
	 sessionFactory.getCurrentSession().persist(cwData);
    }
    
    /**
     * The 'getCrosswalkByNameAndOrg(String cwName, Integer orgId) {' function will check the system to see if the generic crosswalk already exists
     *
     * @param cwName
     * @param orgId
     * @param fileName
     * @return 
     *
     * @Return This function will return a string (crosswalk name).
     */
    @Override
    @Transactional(readOnly = true)
    public Crosswalks getCrosswalkByNameAndOrg(String cwName, Integer orgId, String fileName) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Crosswalks> criteria = builder.createQuery(Crosswalks.class);
        Root<Crosswalks> root = criteria.from(Crosswalks.class);

        Predicate[] predicates = null;
        
        if(orgId == 0) {
            predicates = new Predicate[2];
            predicates[0] = builder.equal(root.get("orgId"), orgId);
            predicates[1] = builder.equal(root.get("name"), cwName);
        }
        else {
            predicates = new Predicate[3];
            predicates[0] = builder.equal(root.get("orgId"), orgId);
            predicates[1] = builder.like(root.get("name"), "%"+cwName);
            predicates[2] = builder.equal(root.get("fileName"), fileName);
        }

        Predicate whereClause = builder.and(predicates);

        criteria.where(whereClause);
        
        List<Crosswalks> crosswalks = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	
	if(crosswalks.size() > 0) {
	    Crosswalks cwDetails = (Crosswalks) crosswalks.get(0);
	    return cwDetails;
	}
	else {
	    return null;
	}
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Crosswalks> getCrosswalksForConfigAndOrg(Integer orgId, Integer configId) {
	
	String sql = "select distinct * from crosswalks a where orgId = :orgId and id in (select crosswalkid from configurationdatatranslations where configId = :configId);";

	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,Crosswalks.class)
	.setParameter("orgId", orgId)
	.setParameter("configId", configId);
	
	List<Crosswalks> crosswalks = query.list();
	
        return crosswalks;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Crosswalks> getCrosswalksForConfigToBeCopied(Integer configId, Integer newOrgId) {
	
	String sql = "select distinct a.* "
	+ "from crosswalks a "
	+ "inner join configurationdatatranslations b on (b.crosswalkid = a.id or (b.macroId in (129,160,177,195,199,201) and (b.constant1 = a.id or b.constant2 = a.id))) and b.configId = :configId "
	+ "where a.orgId > 0 and a.orgId != :newOrgId order by a.name asc";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,Crosswalks.class)
	.addScalar("id", StandardBasicTypes.INTEGER)
	.addScalar("orgId", StandardBasicTypes.INTEGER)
	.addScalar("fileDelimiter", StandardBasicTypes.INTEGER)	
	.addScalar("name", StandardBasicTypes.STRING)
	.addScalar("fileName", StandardBasicTypes.STRING)	
	.addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
	.addScalar("lastUpdated", StandardBasicTypes.TIMESTAMP)	
	.setParameter("configId", configId)
	.setParameter("newOrgId", newOrgId);
	
	List<Crosswalks> crosswalks = query.list();
	
        return crosswalks;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List getConfigCrosswalksWithDataForPrint(Integer configId) {
	
	String sql = "select a.name, b.sourceValue, b.targetValue, b.descValue, a.id, a.fileDelimiter, a.dateCreated, ifnull(a.lastUpdated,a.dateCreated) as lastUpdated "
	+ "from crosswalks a inner join rel_crosswalkdata b on b.crosswalkId = a.id inner join " 
	+ "configurationdatatranslations c on (c.crosswalkid = a.id or (c.macroId in (129,160,177,195,199,201) and (c.constant1 = a.id or c.constant2 = a.id))) and c.configId = :configId "
	+ "order by a.name asc";
	
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
        .addScalar("name", StandardBasicTypes.STRING)
        .addScalar("sourceValue", StandardBasicTypes.STRING)	
        .addScalar("targetValue", StandardBasicTypes.STRING)	
        .addScalar("descValue", StandardBasicTypes.STRING)	
        .addScalar("id", StandardBasicTypes.INTEGER)        
        .addScalar("fileDelimiter", StandardBasicTypes.STRING)    
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("lastUpdated", StandardBasicTypes.TIMESTAMP)
	.setParameter("configId", configId);
        
        return query.getResultList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List getConfigCrosswalkDownloadWithData(Integer crosswalkId) {
	
	String sql = "select b.sourceValue, b.targetValue, b.descValue, c.delimiter, c.delimChar "
	+ "from crosswalks a inner join "
	+ "rel_crosswalkdata b on b.crosswalkId = a.id inner join "
	+ "ref_delimiters c on c.id = a.fileDelimiter "
	+ "where a.id = :crosswalkId";
	
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
        .addScalar("sourceValue", StandardBasicTypes.STRING)	
        .addScalar("targetValue", StandardBasicTypes.STRING)	
        .addScalar("descValue", StandardBasicTypes.STRING)	
        .addScalar("delimiter", StandardBasicTypes.STRING)        
        .addScalar("delimChar", StandardBasicTypes.STRING)    
	.setParameter("crosswalkId", crosswalkId);     
	
        return query.getResultList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public String checkIfCWIsInUse(Integer crosswalkId) {
        
        String inUse = "0";
        
	String sql = "select group_concat(configId) from configurationdatatranslations where crosswalkId = :crosswalkId or (macroId in (129,160,177,195,199,201) and (constant1 = :crosswalkId or constant2 = :crosswalkId))";
	
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
        query.setParameter("crosswalkId", crosswalkId);
        
        if(!query.list().isEmpty()) {
            if(query.list().get(0) == null) {
                inUse = "0";
            }
            else {
                inUse = query.list().get(0).toString();
            }
        }
        
        return inUse;
    }
}