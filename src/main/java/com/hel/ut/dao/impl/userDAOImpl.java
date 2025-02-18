package com.hel.ut.dao.impl;

import java.util.List;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.dao.userDAO;
import com.hel.ut.model.utUser;
import com.hel.ut.model.utUserActivity;
import com.hel.ut.model.utConfiguration;
import com.hel.ut.model.configurationConnection;
import com.hel.ut.model.configurationConnectionSenders;
import com.hel.ut.model.utUserLogin;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.query.SelectionQuery;
import org.hibernate.type.StandardBasicTypes;

/**
 * The userDAOImpl class will implement the DAO access layer to handle updates for organization system users
 *
 *
 * @author chadmccue
 *
 */
@Repository
public class userDAOImpl implements userDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * The 'createUser" function will create the new system user and save the user.
     *
     * @Table	users
     *
     * @param	user	This will hold the user object from the form
     *
     * @return the function will return the id of the new user
     *
     */
    @Override
    @Transactional(readOnly = false)
    public Integer createUser(utUser user) {
        
        sessionFactory.getCurrentSession().persist(user);
        return user.getId();
    }

    /**
     * The 'updateUser' function will update the selected user with the changes entered into the form.
     *
     * @param	user	This will hold the user object from the user form
     *
     * @return the function does not return anything
     */
    @Override
    @Transactional(readOnly = false)
    public void updateUser(utUser user) {
        sessionFactory.getCurrentSession().merge(user);
    }

    /**
     * The 'getUserById' function will return a single user object based on the userId passed in.
     *
     * @param	userId	This will be used to find the specifc user
     *
     * @return	The function will return a user object
     */
    @Override
    @Transactional(readOnly = true)
    public utUser getUserById(int userId) {
        return (utUser) sessionFactory.getCurrentSession().get(utUser.class, userId);
    }

    /**
     * The 'getUsersByOrganization' function will return users based on the orgId passed in
     *
     * @param orgId The organization id to find users for
     *
     * @return The function will return a list of user objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<utUser> getUsersByOrganization(int orgId) {

        List<Integer> OrgIds = new ArrayList<>();
        OrgIds.add(orgId);
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<utUser> criteria = builder.createQuery(utUser.class);
        Root<utUser> root = criteria.from(utUser.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = builder.equal(root.get("status"), true);
        predicates[1] = root.get("orgId").in(OrgIds);

        Predicate whereClause = builder.and(predicates);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'getUserByUserName' function will return a single user object based on a username passed in.
     *
     * @param	username	This will used to query the username field of the users table
     *
     * @return	The function will return a user object
     */
    @Override
    @Transactional(readOnly = true)
    public utUser getUserByUserName(String username) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<utUser> criteria = builder.createQuery(utUser.class);
        Root<utUser> root = criteria.from(utUser.class);

        Predicate whereClause = builder.equal(root.get("username"), username);

        criteria.where(whereClause);
        
        return (utUser) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();
    }

    /**
     * The 'findTotalLogins' function will return the total number of logins for a user.
     *
     * @param	userId	This will be the userid used to find logins
     *
     * @return	The function will return a number of logins
     */
    @Override
    @Transactional(readOnly = true)
    public Long findTotalLogins(int userId) {

        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("select count(id) as totalLogins from utUserLogin where userId = :userId");
        query.setParameter("userId", userId);

        Long totalLogins = (Long) query.uniqueResult();

        return totalLogins;
    }

    /**
     * The 'setLastLogin' function will be called upon a successful login. It will save the entry into the rel_userLogins table.
     *
     * @param username	This will be the username of the person logging in.
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void setLastLogin(String username) {
        Query q1 = sessionFactory.getCurrentSession().createNativeQuery("insert into REL_USERLOGINS (userId)" + " select id from users where username = :username", String.class);
        q1.setParameter("username", username);
        q1.executeUpdate();
    }

    /**
     * The 'getOrganizationContact' function will return a user based on the organization id passed in and the mainContact parameter;
     *
     * @param orgId
     * @param mainContact
     * @orgId The id of the organization to search a user on
     * @mainContact The value of the contact type to return (1 = Primary, 2 = Secondary)
     *
     * @return The function will return a user object
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getOrganizationContact(int orgId, int mainContact) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from utUser where orgId = :orgId and mainContact = :mainContact");
        query.setParameter("orgId", orgId);
        query.setParameter("mainContact", mainContact);

        return query.list();
    }

    /**
     * The 'getUserByIdentifier' function will try to location a user based on the identifier passed in.
     *
     * @param identifier The value that will be used to find a user.
     *
     * @return The function will return a user object
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getUserByIdentifier(String identifier) {

        String sql = ("select id from users where lower(email) = '" + identifier + "' or lower(username) = '" + identifier + "' or lower(concat(concat(firstName,' '),lastName)) = '" + identifier + "'");

        Query findUser = sessionFactory.getCurrentSession().createNativeQuery(sql, Integer.class);

        if (findUser.list().size() > 1) {
            return null;
        } 
        else {
            if (findUser.uniqueResult() == null) {
                return null;
            } else {
                return (Integer) findUser.uniqueResult();
            }
        }
    }

    /**
     * The 'getUserByResetCode' function will try to location a user based on the a reset code
     *
     * @param resetCode The value that will be used to find a user.
     *
     * @return The function will return a user object
     */
    @Override
    @Transactional(readOnly = true)
    public utUser getUserByResetCode(String resetCode) {

        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from utUser where resetCode = :resetCode");
        query.setParameter("resetCode", resetCode);

        if (query.list().size() > 1) {
            return null;
        } else {
            if (query.uniqueResult() == null) {
                return null;
            } else {
                return (utUser) query.uniqueResult();
            }
        }
    }

    /**
     * The 'insertUserLog' function will take a userActivity and insert the information into the database
     *
     * @param userActivity
     * @userActivity An activity of the user
     */
    @Override
    @Transactional(readOnly = false)
    public void insertUserLog(utUserActivity userActivity) {
        try {
            sessionFactory.getCurrentSession().persist(userActivity);
        } 
        catch (HibernateException ex) {
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public utUserActivity getUAById(Integer uaId) {
        try {
            Query query = sessionFactory.getCurrentSession().createNativeQuery("select * from userActivity where id = :uaId",utUserActivity.class)
            .setParameter("uaId", uaId);
            List<utUserActivity> uaList = query.list();
            if (!uaList.isEmpty()) {
                return uaList.get(0);
            }
        } 
        catch (HibernateException ex) {
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getUserByTypeByOrganization(int orgId) {
        try {
            SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from utUser where orgId = :orgId and status = 1 order by userType");
            query.setParameter("orgId", orgId);
            List<utUser> users = query.list();
            return users;
        } catch (HibernateException ex) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getSendersForConfig(List<Integer> configIds) {
        try {
            String sql = ("select * from users where status = 1 and id in (select userId from configurationconnectionsenders where connectionId in "
                    + " (select id from configurationconnections "
                    + " where sourceConfigId in ( :configId))) order by userType;");

            Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
            .setParameterList("configId", configIds);

            List<utUser> users = query.list();

            return users;

        } catch (HibernateException ex) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getOrgUsersForConfig(List<Integer> configIds) {
        try {
            String sql = ("select * from users where status = 1 and orgId in (select orgId from configurations where id "
            + " in ( :configId ) "
            + " and status = 1) order by userType;");

            Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
            .setParameterList("configId", configIds);

            List<utUser> users = query.list();

            return users;

        } catch (HibernateException ex) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getUserConnectionListSending(Integer configId) {
        try {
            String sql = ("select * from users where status = 1 and Id in (select userId from configurationconnectionsenders where sendEmailAlert = 1 and connectionId "
                    + " in (select id from configurationconnections where sourceConfigId = :configId))");

            Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
            .setParameter("configId", configId);

            List<utUser> users = query.list();

            return users;

        } catch (HibernateException ex) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getUserConnectionListReceiving(Integer configId) {
        try {
            String sql = ("select * from users where status = 1 and Id in (select userId from configurationconnectionreceivers where sendEmailAlert = 1 and connectionId "
                    + " in (select id from configurationconnections where targetConfigId = :configId))");

            Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
            .setParameter("configId", configId);

            List<utUser> users = query.list();

            return users;

        } catch (HibernateException ex) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getAllUsers() {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from utUser");

        List<utUser> userList = query.list();
        return userList;
    }

    @Override
    @Transactional(readOnly = true)
    public void updateUserActivity(utUserActivity userActivity) {
        try {
            sessionFactory.getCurrentSession().merge(userActivity);
        } 
        catch (HibernateException ex) {
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<String> getUserRoles(utUser user) {
        try {
            String sql = ("select r.role from users u inner join userRoles r on u.roleId = r.id where u.status = 1 and u.username = :userName");

            Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
            query.setParameter("userName", user.getUsername());
            List<String> roles = query.list();

            return roles;

        } catch (HibernateException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateUserOnly(utUser user) throws Exception {
        sessionFactory.getCurrentSession().merge(user);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<utUser> getUsersByStatuRolesAndOrg(boolean status, List<Integer> rolesToExclude, List<Integer> orgs, boolean include) throws Exception {
        String sql = ("select users.*, orgName from users, organizations "
                + " where users.status = :status and users.orgId = organizations.id");

        if (!rolesToExclude.isEmpty()) {
            sql = sql + " and roleId not in (:rolesToExclude)";
        }
        if (!orgs.isEmpty()) {
            sql = sql + " and orgId ";
            if (!include) {
                sql = sql + " not ";
            }
            sql = sql + " in (:orgs)";
        }
        sql = sql + " order by orgName, username";
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
        .setParameter("status", status);
        
        if (!rolesToExclude.isEmpty()) {
            query.setParameterList("rolesToExclude", rolesToExclude);
        }
        if (!orgs.isEmpty()) {
            query.setParameterList("orgs", orgs);
        }

        List<utUser> users = query.list();

        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getUserAllowedTargets(int userId, List<configurationConnectionSenders> connections) throws Exception {
        
        List<Integer> orgList = new ArrayList<>();

        if (connections == null || connections.isEmpty()) {
            orgList.add(0);
        } 
        else {
            
            CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<configurationConnection> criteria = builder.createQuery(configurationConnection.class);
            Root<configurationConnection> root = criteria.from(configurationConnection.class);
            
            CriteriaQuery<utConfiguration> targetconfigurationQuery = builder.createQuery(utConfiguration.class);
            Root<utConfiguration> targetRoot = targetconfigurationQuery.from(utConfiguration.class);
            
            Predicate whereClause = null;
            
            for (configurationConnectionSenders userConnection : connections) {
                
                whereClause = builder.equal(root.get("id"), userConnection.getConnectionId());
                criteria.where(whereClause);

                configurationConnection connectionInfo = (configurationConnection) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();
                
                if(connectionInfo != null) {
                    whereClause = builder.equal(targetRoot.get("id"), connectionInfo.gettargetConfigId());
                    targetconfigurationQuery.where(whereClause);
                    
                    utConfiguration targetconfigDetails = (utConfiguration) sessionFactory.getCurrentSession().createQuery(targetconfigurationQuery).uniqueResult();
                    
                    if(targetconfigDetails != null) {
                         /* Add the target org to the target organization list */
                        orgList.add(targetconfigDetails.getOrgId());
                    }
                }
            }
        }

        return orgList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getUserAllowedMessageTypes(int userId, List<configurationConnectionSenders> connections) throws Exception {
        
        List<Integer> messageTypeList = new ArrayList<>();

        if (connections == null || connections.isEmpty()) {
            messageTypeList.add(0);
        } 
        else {
            
            CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<configurationConnection> criteria = builder.createQuery(configurationConnection.class);
            Root<configurationConnection> root = criteria.from(configurationConnection.class);
            
            CriteriaQuery<utConfiguration> sourceconfigurationQuery = builder.createQuery(utConfiguration.class);
            Root<utConfiguration> sourceRoot = sourceconfigurationQuery.from(utConfiguration.class);
            
            Predicate whereClause = null;
            
            for (configurationConnectionSenders userConnection : connections) {
                
                whereClause = builder.equal(root.get("id"), userConnection.getConnectionId());
                criteria.where(whereClause);
                
                configurationConnection connectionInfo = (configurationConnection) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();
                
                if(connectionInfo != null) {
                    whereClause = builder.equal(sourceRoot.get("id"), connectionInfo.getsourceConfigId());
                    sourceconfigurationQuery.where(whereClause);
                    
                    utConfiguration configDetails = (utConfiguration) sessionFactory.getCurrentSession().createQuery(sourceconfigurationQuery).uniqueResult();
                    
                    if(configDetails != null) {
                        /* Add the message type to the message type list */
                        messageTypeList.add(configDetails.getMessageTypeId());
                    }
                }
            }
        }

        return messageTypeList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<configurationConnectionSenders> configurationConnectionSendersByUserId(int userId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationConnectionSenders> criteria = builder.createQuery(configurationConnectionSenders.class);
        Root<configurationConnectionSenders> root = criteria.from(configurationConnectionSenders.class);

        Predicate whereClause = builder.equal(root.get("userId"), userId);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }
    
    @Override
    @Transactional(readOnly = false)
    public void loguserout(int userId) throws Exception {
	
	SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from utUserLogin where userId = :userId order by id desc");
        query.setParameter("userId", userId);
	
	List<utUserLogin> logins = query.list();
	
	if(logins != null) {
            if(!logins.isEmpty()) {
                Date logoutDate = new Date();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                utUserLogin lastLogin = (utUserLogin) logins.get(0);

                Query q1 = sessionFactory.getCurrentSession().createNativeQuery("update rel_userlogins set dateLoggedOut = '" +formatter.format(logoutDate)+ "' where id = " + lastLogin.getId(), String.class);
                q1.executeUpdate();
            }
	}
    }
    
    /**
     * The 'getUsersByOrganizationWithLogins' function will return users based on the orgId passed in
     *
     * @param orgId The organization id to find users for
     *
     * @return The function will return a list of user objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<utUser> getUsersByOrganizationWithLogins(int orgId) {

        List<Integer> OrgIds = new ArrayList<>();
        OrgIds.add(orgId);
	
	String sql = "select a.id, a.firstName, a.lastName, a.status, b.role as roleType," 
        + "(select dateCreated from rel_userlogins where userId = a.id order by dateCreated desc limit 1) as dateLastLoggedIn,"
        + "(select TIMESTAMPDIFF(MINUTE,dateCreated,dateLoggedOut) as totalTimeLoggedIn from rel_userlogins where userId = a.id order by dateCreated desc limit 1) as totalTimeLoggedIn," 
        + "(select count(id) from rel_userlogins where userId = a.id) as totalLogins " 
        + "from users a inner join userroles b on a.roleId = b.id " 
        + "where a.orgId in ("+OrgIds.toString().replace("[", "").replace("]", "")+")";
	
	 Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("firstName", StandardBasicTypes.STRING)
        .addScalar("lastName", StandardBasicTypes.STRING)
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .addScalar("dateLastLoggedIn", StandardBasicTypes.TIMESTAMP)
        .addScalar("totalTimeLoggedIn", StandardBasicTypes.INTEGER)
        .addScalar("totalLogins", StandardBasicTypes.INTEGER)
        .addScalar("roleType", StandardBasicTypes.STRING);

        List<utUser> userList = query.list();

        return userList;
    }
    
    /**
     * The 'getUserLogins' function will return users based on the orgId passed in
     *
     *
     * @param userId
     * @return The function will return a list of user objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<utUserLogin> getUserLogins(int userId) {

	String sql = "select dateCreated,IFNULL(TIMESTAMPDIFF(MINUTE,dateCreated,dateLoggedOut),0) as totalTimeLoggedIn " 
        + "from rel_userlogins where userId = " + userId + " order by dateCreated desc";
	
	 Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUserLogin.class)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("totalTimeLoggedIn", StandardBasicTypes.INTEGER);

        List<utUserLogin> userLogins = query.list();

        return userLogins;
    }
    
    /**
     * The 'getAllUsersByOrganization' function will return users based on the orgId passed in
     *
     * @param orgId The organization id to find users for
     *
     * @return The function will return a list of user objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<utUser> getAllUsersByOrganization(int orgId) {

        List<Integer> OrgIds = new ArrayList<>();
        OrgIds.add(orgId);
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<utUser> criteria = builder.createQuery(utUser.class);
        Root<utUser> root = criteria.from(utUser.class);

        Predicate whereClause = root.get("orgId").in(OrgIds);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }
    
    /**
     * The 'getSuccessEmailSendersForConfig' function will return a list of users that are selected to receive an alert when
     * a message is sent out of the IL.
     * 
     * @param targetConfigId
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public List<utUser> getSuccessEmailSendersForConfig(Integer targetConfigId) {
        try {
            String sql = ("select * from users where status = 1 and id in (select userId from configurationconnectionreceivers where sendEmailAlert = 1 and connectionId in "
            + " (select id from configurationconnections "
            + " where targetConfigId = :targetConfigId)) order by userType;");

           Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
           .setParameter("targetConfigId", targetConfigId);
                   
           List<utUser> users = query.list();

           return users;

        } catch (HibernateException ex) {
            return null;
        }
    }
    
    /**
     * The 'getSuccessEmailReceiversForConfig' function will return a list of users that are selected to receive an alert when
     * a message is sent out of the IL.
     * 
     * @param targetConfigId
     * @return 
     */
    @Override
    @Transactional(readOnly = true)
    public List<utUser> getSuccessEmailReceiversForConfig(Integer targetConfigId) {
        try {
            String sql = ("select * from users where status = 1 and id in (select userId from configurationconnectionsenders where sendEmailAlert = 1 and connectionId in "
            + " (select id from configurationconnections "
            + " where targetConfigId = :targetConfigId)) order by userType;");

           Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utUser.class)
           .setParameter("targetConfigId", targetConfigId);
                   
           List<utUser> users = query.list();

           return users;

        } catch (HibernateException ex) {
            return null;
        }
    }
}