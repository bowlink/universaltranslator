package com.hel.ut.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hel.ut.dao.organizationDAO;
import com.hel.ut.model.Organization;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.reference.fileSystem;
import java.util.ArrayList;
import java.util.Properties;
import javax.annotation.Resource;
import org.hibernate.criterion.Order;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

/**
 * The organizationDAOImpl class will implement the DAO access layer to handle queries for an organization
 *
 *
 * @author chadmccue
 *
 */
@Repository
public class organizationDAOImpl implements organizationDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Resource(name = "myProps")
    private Properties myProps;

    /**
     * The 'createOrganziation' function will create a new organization
     *
     * @Table	organizations
     *
     * @param	organization	Will hold the organization object from the form
     *
     * @return The function will return the id of the created organization
     */
    @Override
    @Transactional(readOnly = false)
    public Integer createOrganization(Organization organization) {
        Integer lastId = (Integer) sessionFactory.getCurrentSession().save(organization);
        return lastId;
    }

    /**
     * The 'updateOrganization' function will update a selected organization
     *
     * @Table	organizations
     *
     * @param	organization	Will hold the organization object from the field
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void updateOrganization(Organization organization) {
        sessionFactory.getCurrentSession().update(organization);
    }

    /**
     * The 'getOrganizationById' function will return an organization based on organization id passed in.
     *
     * @Table organizations
     *
     * @param	orgId	This will hold the organization id to find
     *
     * @return	This function will return a single organization object
     */
    @Override
    @Transactional(readOnly = true)
    public Organization getOrganizationById(int orgId) {
        return (Organization) sessionFactory.getCurrentSession().get(Organization.class, orgId);
    }

    /**
     * The 'getOrganizationByName' function will return a single organization based on the name passed in.
     *
     * @Table	organizations
     *
     * @param	cleanURL	Will hold the 'clean' organization name from the url
     *
     * @return	This function will return a single organization object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Organization> getOrganizationByName(String cleanURL) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Organization.class);
        criteria.add(Restrictions.eq("cleanURL", cleanURL));
        return criteria.list();
    }

    /**
     * The 'findTotalOrgs' function will return the total number of organizations in the system. This will be used for pagination when viewing the list of organizations
     *
     * @Table organizations
     *
     * @return This function will return the total organizations
     */
    @Override
    @Transactional(readOnly = true)
    public Long findTotalOrgs() {
        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalOrgs from Organization where cleanURL <> ''");

        Long totalOrgs = (Long) query.uniqueResult();

        return totalOrgs;
    }

    /**
     * The 'getOrganizations' function will return a list of the organizations in the system
     *
     * @return 
     * @Table	organizations
     *
     * @Return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Organization> getOrganizations() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Organization where cleanURL <> '' order by orgName asc");

        List<Organization> organizationList = query.list();

        return organizationList;
    }

    /**
     * The 'getLatestOrganizations' function will return a list of the latest organizations added to the system.This function will only return active organizations.
     *
     * @return 
     * @Table	organizations
     *
     * @param	maxResults	This will hold the value of the maximum number of results we want to send back to the page
     *
     * @Return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Organization> getLatestOrganizations(int maxResults) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Organization where status = 1 order by dateCreated desc");

        //Set the max results to display
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        List<Organization> organizationList = query.list();
        return organizationList;
    }

    /**
     * The 'getAllActiveOrganizations' function will return all the active organizations in the system.The function will sort by organization name
     *
     * @return 
     * @Table	Organizations
     *
     * @Return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Organization> getAllActiveOrganizations() {
        Query query = sessionFactory.getCurrentSession().createQuery("from Organization where cleanURL <> '' and status = 1 order by orgName asc");

        List<Organization> organizationList = query.list();
        return organizationList;
    }

    /**
     * The 'findTotalUsers' function will return the total number of system users set up for a specific organization.
     *
     * @param orgId
     * @return 
     * @Table	users
     *
     * @Param	orgId	This will hold the organization id we want to search on
     *
     * @Return	This function will return the total number of users for the organization
     */
    @Override
    @Transactional(readOnly = true)
    public Long findTotalUsers(int orgId) {

        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalUsers from User where orgId = :orgId");
        query.setParameter("orgId", orgId);

        Long totalUsers = (Long) query.uniqueResult();

        return totalUsers;
    }

    /**
     * The 'findTotalConfigurations' function will return the total number of configurations set up for a specific organization.
     *
     * @param orgId
     * @return 
     * @Table	configurations
     *
     * @Param	orgId	This will hold the organization id we want to search on
     *
     * @Return	This function will return the total number of configurations for the organization
     */
    @Override
    @Transactional(readOnly = true)
    public Long findTotalConfigurations(int orgId) {

        Query query = sessionFactory.getCurrentSession().createQuery("select count(id) as totalConfigs from configuration where orgId = :orgId");
        query.setParameter("orgId", orgId);

        Long totalConfigs = (Long) query.uniqueResult();

        return totalConfigs;
    }

    /**
     * The 'getOrganizationUsers' function will return the list of users for a specific organization.
     *
     * @param orgId
     * @return 
     * @Table	users
     *
     * @Param	orgId	This will hold the organization id to search on page	This will hold the current page to view maxResults	This will hold the total number of results to return back to the list page
     *
     * @Return	This function will return a list of user objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utUser> getOrganizationUsers(int orgId) {

        Query query = sessionFactory.getCurrentSession().createQuery("from User where orgId = :orgId order by lastName asc, firstName asc");
        query.setParameter("orgId", orgId);

        return query.list();
    }

    /**
     * The 'deleteOrganization' function will remove the organization and all other entities associated to the organization.(Users, Providers, Brochures, Configurations, etc). When deleting users the function will also remove anything associated to the users (Logins, Access, etc).
     *
     * @param orgId
     * @Param	orgId	This will hold the organization that will be deleted
     *
     * @Return This function will not return any values.
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteOrganization(int orgId) {
        
        //Delete the logins for the users associated to the organization to be deleted.
        Query findUsers = sessionFactory.getCurrentSession().createQuery("from utUser where orgId = :orgId");
        findUsers.setParameter("orgId", orgId);

        List<utUser> users = findUsers.list();

        if (users.size() > 0) {
            try {
                Query deleteLogins = sessionFactory.getCurrentSession().createQuery("delete from utUserLogin where userId in (select id from utUser where orgId = :orgId");
                deleteLogins.setParameter("orgId", orgId);
                deleteLogins.executeUpdate();
            } catch (SQLGrammarException ex) {
                throw ex;
            }

            //Delete the user access entries for the users associated to the organization to be deleted.
            try {
                Query deleteuserFeatures = sessionFactory.getCurrentSession().createQuery("delete from utUserActivity where userId in (select id from utUser where orgId = :orgId");
                deleteuserFeatures.setParameter("orgId", orgId);
                deleteuserFeatures.executeUpdate();
            } catch (SQLGrammarException ex) {
                throw ex;
            }
        }

        //Delete all users associated to the organization
        try {
            Query deleteUser = sessionFactory.getCurrentSession().createQuery("delete from utUser where orgId = :orgId");
            deleteUser.setParameter("orgId", orgId);
            deleteUser.executeUpdate();
        } catch (SQLGrammarException ex) {
            throw ex;
        }

        //Get the organization cleanURL
        Organization orgDetails = getOrganizationById(orgId);

        //Delete the organization folder within bowlink
        try {
            fileSystem dir = new fileSystem();
            dir.deleteOrgDirectories(myProps.getProperty("ut.directory.utRootDir") + orgDetails.getcleanURL());

            //Delete the organization
            Query deleteOrg = sessionFactory.getCurrentSession().createQuery("delete from Organization where id = :orgId");
            deleteOrg.setParameter("orgId", orgId);
            deleteOrg.executeUpdate();
        } catch (SQLGrammarException ex) {
            throw ex;
        }
    }

    /**
     * The 'getAssociatedOrgs' function will return a list of organizations that are associated to the passed in orgId
     *
     * @param orgId The id of the organization to find associated orgs
     *
     * @return This function will return a list or organization objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<Organization> getAssociatedOrgs(int orgId) {

        // Get a list of configurations for the passed in org
        List<Integer> configs = new ArrayList<>();

        Criteria configurations = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
        configurations.add(Restrictions.eq("orgId", orgId));
        List<utConfiguration> orgConfigs = configurations.list();

        if (orgConfigs.isEmpty()) {
            configs.add(0);
        } else {
	    orgConfigs.forEach(config -> {
		configs.add(config.getId());
	    });
        }

        // Find all connections set up for the returned configurations
        List<Integer> targetOrgIds = new ArrayList<>();

        Criteria connections = sessionFactory.getCurrentSession().createCriteria(configurationConnection.class);
        connections.add(Restrictions.or(
	    Restrictions.in("sourceConfigId", configs),
	    Restrictions.in("targetConfigId", configs)
        ));
        List<configurationConnection> orgConnections = connections.list();

        // Find all organiations associated to the returend connections
        if (orgConnections.isEmpty()) {
            targetOrgIds.add(0);
        } 
	else {
            for (configurationConnection connection : orgConnections) {

                Criteria getSrcConfigDetails = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
                getSrcConfigDetails.add(Restrictions.eq("id", connection.getsourceConfigId()));

                utConfiguration srcconfigDetails = (utConfiguration) getSrcConfigDetails.uniqueResult();

                if (srcconfigDetails.getorgId() != orgId && !targetOrgIds.contains(srcconfigDetails.getorgId())) {
                    targetOrgIds.add(srcconfigDetails.getorgId());
                }

                Criteria getTgtConfigDetails = sessionFactory.getCurrentSession().createCriteria(utConfiguration.class);
                getTgtConfigDetails.add(Restrictions.eq("id", connection.gettargetConfigId()));

                utConfiguration TgtconfigDetails = (utConfiguration) getTgtConfigDetails.uniqueResult();

                if (TgtconfigDetails.getorgId() != orgId && !targetOrgIds.contains(TgtconfigDetails.getorgId())) {
                    targetOrgIds.add(TgtconfigDetails.getorgId());
                }
            }
        }

        Criteria orgs = sessionFactory.getCurrentSession().createCriteria(Organization.class);
        orgs.add(Restrictions.eq("status", true));
        orgs.add(Restrictions.eq("publicOrg", true));
        orgs.add(Restrictions.in("id", targetOrgIds));
        orgs.addOrder(Order.asc("orgName"));

        return orgs.list();
    }
    
    /**
     * 
     * @param displayStart
     * @param displayRecords
     * @param searchTerm
     * @param sortColumnName
     * @param sortDirection
     * @return
     * @throws Exception 
     */
    @Override
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsPaged(Integer displayStart, Integer displayRecords, String searchTerm, String sortColumnName, String sortDirection) throws Exception {
        
	if(displayRecords < 0) {
            displayRecords = 999999;
        }
        
        String query = "select id, orgName, address, address2, city, state, postalCode, fax, phone, dateCreated, cleanURL, orgType, helRegistryId, organizationType "
	    + "FROM (select id, orgName, address, address2, city, state, postalCode, fax, phone, dateCreated, cleanURL, orgType, helRegistryId, "
	    + "CASE WHEN helRegistryId > 0 THEN (select registryName from registries.registries where id = organizations.helRegistryId) "
		+ "ELSE '' "
	    + "END AS helRegistry, "
	    + "CASE WHEN orgType = 1 THEN 'Health Care Provider' "
		 + "WHEN orgType = 2 THEN 'Community Based Organization' "
		 + "WHEN orgType = 3 THEN 'Health Management Information System' "
		 + "WHEN orgType = 4 THEN 'Data Warehouse' "
		 + "ELSE 'Internal Health-e-link Registry' "
	    + "END AS organizationType "
	    + "from organizations) as orgs where cleanURL <> '' and cleanURL <> 'BowlinkTest' ";
	
	if(!"".equals(searchTerm)){
	    query += " and ("
            + "id like '%"+searchTerm+"%' "        
	    + "OR address like '%"+searchTerm+"%' "
	    + "OR address2 like '%"+searchTerm+"%' "
	    + "OR city like '%"+searchTerm+"%' "
	    + "OR state like '%"+searchTerm+"%' "
	    + "OR postalCode like '%"+searchTerm+"%' "
	    + "OR dateCreated like '%"+searchTerm+"%' "
	    + "OR orgName like '%"+searchTerm+"%'"
	    + "OR organizationType like '%"+searchTerm+"%'"
	    + "OR helRegistry like '%"+searchTerm+"%'"
	    + ") ";
	}	
		
        query += "order by "+sortColumnName+" "+sortDirection;
        query += " limit :displayStart , :displayRecords";
	
        Query q1 = sessionFactory.getCurrentSession().createSQLQuery(query);
        
        q1.setParameter("displayStart", displayStart);
        q1.setParameter("displayRecords", displayRecords);
	
        q1.setResultTransformer(Transformers.aliasToBean(Organization.class));

        return q1.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> getAgenciesForReport(Integer registryType) throws Exception {
	
	String sqlQuery = "select distinct a.id, a.orgName from organizations a inner join configurations b on b.orgId = a.id ";
	sqlQuery += "where b.messageTypeId = :registryType and b.status = 1 order by a.orgName asc";
	
	Query q1 = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        q1.setParameter("registryType", registryType);
	
        q1.setResultTransformer(Transformers.aliasToBean(Organization.class));

	return q1.list();
    }
    
    @Override
    @Transactional(readOnly = true)
    public  List<Organization> getAllActiveOrganizationsWithSystemName() throws Exception {
	
        String sqlQuery = "select a.*, case when r.registryName like '%CeC' then concat(r.registryName,' (eReferral)') else r.registryName end as helRegistry "
        + "from organizations a inner join "
        + "registries.registries r on r.id = a.helRegistryId "
        + "order by helRegistry asc, a.orgName asc";
        
        Query q1 = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        q1.setResultTransformer(Transformers.aliasToBean(Organization.class));
        
        return q1.list();
    }
}
