package com.hel.ut.dao.impl;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.model.CrosswalkData;
import com.hel.ut.model.HL7Details;
import com.hel.ut.model.HL7ElementComponents;
import com.hel.ut.model.HL7Elements;
import com.hel.ut.model.HL7Segments;
import com.hel.ut.model.Macros;
import com.hel.ut.model.configexceldetails;
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
import com.hel.ut.model.watchlist;
import com.hel.ut.model.watchlistEntry;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Repository;
import com.hel.ut.dao.utConfigurationDAO;
import com.hel.ut.dao.utConfigurationTransportDAO;
import com.hel.ut.model.configurationFormFields;
import com.hel.ut.model.configurationUpdateLogs;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Objects;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.SelectionQuery;

@Repository
public class utConfigurationDAOImpl implements utConfigurationDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private utConfigurationTransportDAO configurationTransportDAO;

    /**
     * The 'createConfiguration' function will create a new utConfiguration
     *
     * @Table	configurations
     *
     * @param	configuration	Will hold the utConfiguration object from the form
     *
     * @return The function will return the id of the created utConfiguration
     */
    @Override
    @Transactional(readOnly = false)
    public Integer createConfiguration(utConfiguration configuration) {
        sessionFactory.getCurrentSession().persist(configuration);
        return configuration.getId();
    }

    /**
     * The 'updateConfiguration' function will update a selected utConfiguration details
     *
     * @Table	configurations
     *
     * @param	configuration	Will hold the utConfiguration object from the field
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void updateConfiguration(utConfiguration configuration) {
        sessionFactory.getCurrentSession().merge(configuration);
    }

    /**
     * The 'getConfigurationById' function will return a utConfiguration based on the id passed in.
     *
     * @Table configurations
     *
     * @param	configId	This will hold the utConfiguration id to find
     *
     * @return	This function will return a single utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    public utConfiguration getConfigurationById(int configId) {
        return (utConfiguration) sessionFactory.getCurrentSession().get(utConfiguration.class, configId);
    }

    /**
     * The 'getConfigurationsByOrgId' function will return a list of configurations for the organization id passed in
     *
     * @param searchTerm
     * @Table configurations
     *
     * @param	orgId	This will hold the organization id to find
     *
     * @return	This function will return a list of utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getConfigurationsByOrgId(int orgId, String searchTerm) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<utConfiguration> criteria = builder.createQuery(utConfiguration.class);
        Root<utConfiguration> root = criteria.from(utConfiguration.class);
        
        Predicate whereClause = null;
        
        if (!"".equals(searchTerm)) {
             whereClause = builder.equal(root.get("orgId"), orgId);
        }
        else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = builder.equal(root.get("orgId"), orgId);
            predicates[1] = builder.equal(root.get("deleted"), false);

            whereClause = builder.and(predicates);
        }

        criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'getActiveConfigurationsByOrgId' function will return a list of active configurations for the organization id passed in
     *
     * @Table configurations
     *
     * @param	orgId	This will hold the organization id to find
     *
     * @return	This function will return a list of utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getActiveConfigurationsByOrgId(int orgId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<utConfiguration> criteria = builder.createQuery(utConfiguration.class);
        Root<utConfiguration> root = criteria.from(utConfiguration.class);

        Predicate[] predicates = new Predicate[3];
        predicates[0] = builder.equal(root.get("orgId"), orgId);
        predicates[1] = builder.equal(root.get("status"), true);
        predicates[2] = builder.equal(root.get("deleted"), false);

        Predicate whereClause = builder.and(predicates);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'getConfigurationByName' function will return a single utConfiguration based on the name passed in.
     *
     * @Table	configurations
     *
     * @param	configName	Will hold the utConfiguration name to search on
     *
     * @return	This function will return a single utConfiguration object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public utConfiguration getConfigurationByName(String configName, int orgId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<utConfiguration> criteria = builder.createQuery(utConfiguration.class);
        Root<utConfiguration> root = criteria.from(utConfiguration.class);
        
        Predicate whereClause = null;
        
        if(orgId > 0) {
            Predicate[] predicates = new Predicate[3];
            predicates[0] = builder.like(root.get("configname"), configName);
            predicates[1] = builder.equal(root.get("orgId"), orgId);
            predicates[2] = builder.equal(root.get("deleted"), false);

            whereClause = builder.and(predicates);
        }
        else {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = builder.like(root.get("configname"), configName);
            predicates[1] = builder.equal(root.get("deleted"), false);

            whereClause = builder.and(predicates);
        }

        criteria.where(whereClause);
        
        return (utConfiguration) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();
    }

    /**
     * The 'getConfigurations' function will return a list of the configurations in the system
     *
     * @Table	configurations
     *
     * @return	This function will return a list of configuration objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getConfigurations() {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from utConfiguration where deleted = 0 order by dateCreated desc");

        List<utConfiguration> configurationList = query.list();
        return configurationList;
    }

    /**
     * The 'getDataTranslations' function will return a list of data translations saved for the passed in utConfiguration/transport method.
     *
     * @param	configId	The id of the utConfiguration we want to return associated translations for.
     *
     * @return	This function will return a list of translations
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationDataTranslations> getDataTranslations(int configId) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationDataTranslations where configId = :configId order by processOrder asc");
        query.setParameter("configId", configId);

        return query.list();
    }

    /**
     * The 'totalConfigs' function will return the total number of active configurations in the system. This will be used for pagination when viewing the list of configurations
     *
     * @Table configurations
     *
     * @return This function will return the total configurations
     */
    @Override
    @Transactional(readOnly = true)
    public Long findTotalConfigs() {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("select count(id) as totalConfigs from utConfiguration where deleted = 0");

        Long totalConfigs = (Long) query.uniqueResult();

        return totalConfigs;
    }

    /**
     * The 'getLatestConfigurations' function will return a list of the latest configurations that have been added to the system and activated.
     *
     * @Table	organizations
     *
     * @param	maxResults	This will hold the value of the maximum number of results we want to send back to the page
     *
     * @return	This function will return a list of organization objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<utConfiguration> getLatestConfigurations(int maxResults) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from utConfiguration where deleted = 0 order by dateCreated desc");

        //Set the max results to display
        query.setMaxResults(maxResults);

        List<utConfiguration> configurationList = query.list();
        return configurationList;
    }

    /**
     * The 'updateCompletedSteps' function will update the steps completed for a passe in configurations. This column will be used to determine when you can activate a utConfiguration and when you can access certain steps in the utConfiguration creation process.
     *
     * @param	configId	This will hold the id of the utConfiguration to update stepCompleted	This will hold the completed step number
     * @param stepCompleted
     */
    @Override
    @Transactional(readOnly = false)
    public void updateCompletedSteps(int configId, int stepCompleted) {

        Query query = sessionFactory.getCurrentSession().createNativeQuery("UPDATE configurations set stepsCompleted = :stepCompleted where id = :configId", String.class)
        .setParameter("stepCompleted", stepCompleted)
        .setParameter("configId", configId);

        query.executeUpdate();
    }

    /**
     * The 'getFileTypes' function will return a list of available file types
     *
     * @return 
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getFileTypes() {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT id, fileType FROM ref_fileTypes where active = 1 order by id asc", String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("fileType", StandardBasicTypes.STRING);

        return query.getResultList();
    }

    /**
     * The 'getFileTypesById' function will return the fileType by the id passed in
     *
     * @param id The fileTypeId
     *
     * @table ref_fileTypes
     *
     * @return This function will return the file type
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public String getFileTypesById(int id) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT fileType FROM ref_fileTypes where id = :id", String.class)
        .setParameter("id", id);

        String fileType = (String) query.uniqueResult();

        return fileType;
    }

    /**
     * The 'getFieldName' function will return the name of a field based on the fieldId passed in. This is used for display purposes to show the actual field lable instead of a field name.
     *
     * @param fieldId	This will hold the id of the field to retrieve
     *
     * @Return This function will return a string (field name)
     */
    @Override
    @Transactional(readOnly = true)
    public String getFieldName(int fieldId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT fieldDesc FROM configurationFormFields where id = :fieldId", String.class)
        .setParameter("fieldId", fieldId);

        String fieldName = (String) query.uniqueResult();

        return fieldName;
    }

    /**
     * The 'deleteDataTranslations' function will remove all data translations for the passed in utConfiguration / transport method.
     *
     * @param	configId	The id of the utConfiguration to remove associated translations
     * @param categoryId The id of the category
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteDataTranslations(int configId, int categoryId) {
        MutationQuery deleteTranslations = sessionFactory.getCurrentSession().createMutationQuery("delete from configurationDataTranslations where configId = :configId and categoryId = :categoryId");
        deleteTranslations.setParameter("configId", configId);
        deleteTranslations.setParameter("categoryId", categoryId);
        deleteTranslations.executeUpdate();
    }

    /**
     * The 'saveDataTranslations' function will save the submitted translations for the selected message type
     *
     * @param translations	the configurationDataTranslations object
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void saveDataTranslations(configurationDataTranslations translations) {
        sessionFactory.getCurrentSession().persist(translations);
    }

    /**
     * The 'getMacrosByCategory' function will return a list of available Pre and Post macros.
     *
     * @param categoryId
     * @return list of macros
     */
    @Override
    @Transactional(readOnly = true)
    public List<Macros> getMacrosByCategory(int categoryId) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from Macros where categoryId = :categoryId order by macroName asc");
        query.setParameter("categoryId", categoryId);

        List<Macros> macros = query.list();

        return macros;
    }

    /**
     * The 'getMacros' function will return a list of available system macros.
     *
     * @return list of macros
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Macros> getMacros() {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from Macros where categoryId = 1 order by macroName asc");
        return query.list();
    }

    /**
     * The 'getMacroById' function will return the macro details for the passed in macro id.
     *
     * @param macroId The value of the macro to retrieve details
     *
     * @return macros object
     */
    @Transactional(readOnly = true)
    @Override
    public Macros getMacroById(int macroId) {
        return (Macros) sessionFactory.getCurrentSession().get(Macros.class, macroId);
    }

    /**
     * The 'getAllConnections' function will return the list of utConfiguration connections in the system.
     *
     * @Table	configurationConnections
     *
     *
     * @return	This function will return a list of configurationConnection objects
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getAllConnections() {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationConnection order by dateCreated desc");

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'getLatestConnections' function will return the list of utConfiguration connections in the system.
     *
     * @Table	configurationConnections
     *
     * @param maxResults This will hold the value of the maximum number of results we want to send back to the list page
     *
     * @return	This function will return a list of configurationConnection objects
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getLatestConnections(int maxResults) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationConnection order by dateCreated desc");

        //Set the max results to display
        query.setMaxResults(maxResults);

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'getConnectionsByConfiguration' will return a list of target connections for a passed in utConfiguration;
     *
     * @param configId The id of the utConfiguration to search connections for.
     * @param userId
     *
     * @return This function will return a list of configurationConnection objects
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationConnection> getConnectionsByConfiguration(int configId, int userId) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationConnection where sourceConfigId = :configId");
        query.setParameter("configId", configId);

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'getConnectionsByTargetConfiguration' will return a list of target connections for a passed in utConfiguration;
     *
     * @param configId The id of the utConfiguration to search connections for.
     *
     * @return This function will return a list of configurationConnection objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getConnectionsByTargetConfiguration(int configId) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationConnection where targetConfigId = :configId");
        query.setParameter("configId", configId);

        List<configurationConnection> connections = query.list();
        return connections;
    }

    /**
     * The 'saveConnection' function will save the new connection
     *
     * @param connection The object holding the new connection
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public Integer saveConnection(configurationConnection connection) {
        sessionFactory.getCurrentSession().persist(connection);
        return connection.getId();
    }

    /**
     * The 'saveConnectionSenders' function will save the list of users selected to be authorized to send transactions for a utConfiguration connection.
     *
     * @table configurationConnectionSenders
     *
     * @param senders The configurationConnectionSenders object
     */
    @Override
    @Transactional(readOnly = false)
    public void saveConnectionSenders(configurationConnectionSenders senders) {
        sessionFactory.getCurrentSession().persist(senders);
    }

    /**
     * The 'saveConnectionReceivers' function will save the list of users selected to be authorized to receive transactions for a utConfiguration connection.
     *
     * @table configurationConnectionReceivers
     *
     * @param receivers The configurationConnectionSenders object
     */
    @Override
    @Transactional(readOnly = false)
    public void saveConnectionReceivers(configurationConnectionReceivers receivers) {
        sessionFactory.getCurrentSession().persist(receivers);
    }

    /**
     * The 'getConnection' function will return a connection based on the id passed in.
     *
     * @param connectionId
     * @Table configurationConnections
     *
     * @return	This function will return a single connection object
     */
    @Override
    @Transactional(readOnly = true)
    public configurationConnection getConnection(int connectionId) {
        return (configurationConnection) sessionFactory.getCurrentSession().get(configurationConnection.class, connectionId);
    }

    /**
     * The 'getConnectionSenders' function will return a list of authorized users who are set up to create new messages for the passed in connectionId
     *
     * @param connectionId The id of the utConfiguration connection to get a list of users
     *
     * @return This function will return a list of configurationConnectionSenders objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnectionSenders> getConnectionSenders(int connectionId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationConnectionSenders> criteria = builder.createQuery(configurationConnectionSenders.class);
        Root<configurationConnectionSenders> root = criteria.from(configurationConnectionSenders.class);

        Predicate whereClause = builder.equal(root.get("connectionId"), connectionId);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'getConnectionReceivers' function will return a list of authorized users who are set up to receive messages for the passed in connectionId
     *
     * @param connectionId The id of the utConfiguration connection to get a list of users
     *
     * @return This function will return a list of configurationConnectionSenders objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnectionReceivers> getConnectionReceivers(int connectionId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationConnectionReceivers> criteria = builder.createQuery(configurationConnectionReceivers.class);
        Root<configurationConnectionReceivers> root = criteria.from(configurationConnectionReceivers.class);

        Predicate whereClause = builder.equal(root.get("connectionId"), connectionId);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'removeConnectionSenders' function will remove the authorized senders for the passed in connectionId.
     *
     * @param connectionId The connection Id to remove senders for
     */
    @Override
    @Transactional(readOnly = false)
    public void removeConnectionSenders(int connectionId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("DELETE from configurationConnectionSenders where connectionId = :connectionId", String.class)
                .setParameter("connectionId", connectionId);

        query.executeUpdate();
    }

    /**
     * The 'removeConnectionReceivers' function will remove the authorized receivers for the passed in connectionId.
     *
     * @param connectionId The connection Id to remove receivers for
     */
    @Override
    @Transactional(readOnly = false)
    public void removeConnectionReceivers(int connectionId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("DELETE from configurationConnectionReceivers where connectionId = :connectionId", String.class)
                .setParameter("connectionId", connectionId);

        query.executeUpdate();
    }
    
    /**
     * The 'removeConnection' function will remove the connection for the passed in connectionId.
     *
     * @param connectionId The connection Id to remove receivers for
     */
    @Override
    @Transactional(readOnly = false)
    public void removeConnection(int connectionId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("DELETE from configurationConnections where Id = :connectionId", String.class)
                .setParameter("connectionId", connectionId);

        query.executeUpdate();
    }

    /**
     * The 'updateConnection' function will update the status of the passed in connection
     *
     * @param connection The object holding the connection
     */
    @Override
    @Transactional(readOnly = false)
    public void updateConnection(configurationConnection connection) {
        sessionFactory.getCurrentSession().merge(connection);
    }

    /**
     * The 'getScheduleDetails' function will return the details of the schedule for the passed in utConfiguration id and transport method.
     *
     * @param configId The id for the utConfiguration
     *
     * @return The function will return a configurationSchedules object containing the details for the schedule.
     */
    @Transactional(readOnly = true)
    @Override
    public configurationSchedules getScheduleDetails(int configId) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationSchedules where configId = :configId");
        query.setParameter("configId", configId);

        configurationSchedules scheduleDetails;
        
        if(query.list().size() > 0) {
            if (query.list().size() > 1) {
                scheduleDetails = (configurationSchedules) query.list().get(0);

                return scheduleDetails;
            } else {
                scheduleDetails = (configurationSchedules) query.uniqueResult();

                return scheduleDetails;
            }
        }
        else {
            scheduleDetails = new configurationSchedules();
            scheduleDetails.setConfigId(configId);
            scheduleDetails.setType(5);
            return scheduleDetails;
        }
    }

    /**
     * The 'saveSchedule' function will create or update the utConfiguration schedule passed in.
     *
     * @param scheduleDetails The object that holds the utConfiguration schedule
     */
    @Transactional(readOnly = false)
    @Override
    public void saveSchedule(configurationSchedules scheduleDetails) {
        /*configurationSchedules currScheduleDetails = getScheduleDetails(scheduleDetails.getConfigId());
        
        if(currScheduleDetails != null) {
            sessionFactory.getCurrentSession().merge(scheduleDetails);
        }
        else {
             sessionFactory.getCurrentSession().persist(scheduleDetails);
        }*/
        if (Objects.isNull(sessionFactory.getCurrentSession().find(configurationSchedules.class, scheduleDetails.getId()))) {
            sessionFactory.getCurrentSession().persist(scheduleDetails);
        } else {
            sessionFactory.getCurrentSession().merge(scheduleDetails);
        }
    }

    /**
     * The 'getMessageSpecs' function will return the message specs for the passing utConfiguration ID.
     *
     * @param configId The utConfiguration Id to find message specs for
     *
     * @return This function will return the configuartionMessageSpec object.
     */
    @Override
    @Transactional(readOnly = true)
    public configurationMessageSpecs getMessageSpecs(int configId) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationMessageSpecs where configId = :configId");
        query.setParameter("configId", configId);

        return (configurationMessageSpecs) query.uniqueResult();
    }
    
    /**
     * The 'clearMessageSpecFormFields' function will save/update the utConfiguration message specs.
     *
     * @param messageSpecs The object that will hold the values from the message spec form
     * @param transportDetailId
     * @param clearFields
     */
    @Transactional(readOnly = false)
    public void clearMessageSpecFormFields(configurationMessageSpecs messageSpecs, int transportDetailId) {

	//Delete the existing data translactions
	Query deleteTranslations = sessionFactory.getCurrentSession().createNativeQuery("DELETE from configurationDataTranslations where configId = :configId", String.class);
	deleteTranslations.setParameter("configId", messageSpecs.getConfigId());
	deleteTranslations.executeUpdate();

	//Delete the existing form fields
	Query deleteFields = sessionFactory.getCurrentSession().createNativeQuery("DELETE from configurationFormFields where configId = :configId and transportDetailId = :transportDetailId", String.class);
	deleteFields.setParameter("configId", messageSpecs.getConfigId());
	deleteFields.setParameter("transportDetailId", transportDetailId);
	deleteFields.executeUpdate();
    }

    /**
     * The 'updateMessageSpecs' function will save/update the utConfiguration message specs.
     *
     * @param messageSpecs The object that will hold the values from the message spec form
     * @param transportDetailId
     * @param clearFields
     */
    @Override
    @Transactional(readOnly = false)
    public void updateMessageSpecs(configurationMessageSpecs messageSpecs, int transportDetailId) {
        if (Objects.isNull(sessionFactory.getCurrentSession().find(configurationMessageSpecs.class, messageSpecs.getId()))) {
            sessionFactory.getCurrentSession().persist(messageSpecs);
        } else {
            sessionFactory.getCurrentSession().merge(messageSpecs);
        }
    }

    /**
     * The 'getActiveConfigurationsByUserId' function will return a list of configurations set up the passed in userId and passed in transport method
     *
     * @param userId The id of the logged in user
     * @param transportMethod The id of the transport method to find configurations 1 = File Upload 2 = ERG
     *
     * @return This function will return a list of ERG configurations.
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration> getActiveConfigurationsByUserId(int userId, int transportMethod) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationConnectionSenders> criteria = builder.createQuery(configurationConnectionSenders.class);
        Root<configurationConnectionSenders> root = criteria.from(configurationConnectionSenders.class);

        Predicate whereClause = builder.equal(root.get("userId"), userId);

        criteria.where(whereClause);
        
        List<configurationConnectionSenders> senderConnections = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();

        /* 
         Create an emtpy array that will hold the list of configurations associated to the
         found connections.
         */
        List<Integer> senderConfigList = new ArrayList<>();

        if (senderConnections.isEmpty()) {
            senderConfigList.add(0);
        } 
        else {
            CriteriaQuery<configurationConnection> connectionCriteria = builder.createQuery(configurationConnection.class);
            Root<configurationConnection> connectionRoot = connectionCriteria.from(configurationConnection.class);
            
            configurationConnection connectionDetails = null;
                    
            // Search the connections by connectionId to pull the sourceConfigId
            for (configurationConnectionSenders connection : senderConnections) {
                
                whereClause = builder.equal(root.get("id"), connection.getConnectionId());
                connectionCriteria.where(whereClause);
                
                connectionDetails = (configurationConnection) sessionFactory.getCurrentSession().createQuery(connectionCriteria).uniqueResult();
                
                if(connectionDetails != null) {
                    senderConfigList.add(connectionDetails.getSourceConfigId());
                }
                
                connectionDetails = null;
            }
        }

        /* 
         Query to get a list of all ERG configurations that the logged in
         user is authorized to create
         */
        List<Integer> ergConfigList = new ArrayList<>();
        
        CriteriaQuery<configurationTransport> transportCriteria = builder.createQuery(configurationTransport.class);
        Root<configurationTransport> transportnRoot = transportCriteria.from(configurationTransport.class);
        
        Predicate[] predicates = new Predicate[2];
        predicates[0] = builder.equal(root.get("transportMethodId"), transportMethod);
        predicates[1] = root.get("configId").in(senderConfigList);

        whereClause = builder.and(predicates);
        
        transportCriteria.where(whereClause);
        
        List<configurationTransport> ergConfigs = sessionFactory.getCurrentSession().createQuery(transportCriteria).getResultList();

        for (configurationTransport config : ergConfigs) {
            ergConfigList.add(config.getConfigId());
        }

        if (ergConfigList.isEmpty()) {
            ergConfigList.add(0);
        }

        /*
         Finally query the utConfiguration table to get all configurations in the authorized list
         of utConfiguration Ids.
         */
        CriteriaQuery<utConfiguration> configCriteria = builder.createQuery(utConfiguration.class);
        Root<utConfiguration> configRoot = configCriteria.from(utConfiguration.class);

        predicates = new Predicate[4];
        predicates[0] = builder.equal(root.get("status"), true);
        predicates[1] = builder.equal(root.get("deleted"), false);
        predicates[2] = builder.equal(root.get("sourceType"), 1);
        predicates[3] = root.get("id").in(ergConfigList);

        whereClause = builder.and(predicates);

        configCriteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(configCriteria).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationDataTranslations> getDataTranslationsWithFieldNo(int configId, int categoryId) {
        
        Query query = sessionFactory.getCurrentSession().createNativeQuery("select configurationDataTranslations.id, configurationDataTranslations.configId, configurationDataTranslations.fieldId, configurationDataTranslations.crosswalkId,"
        + "configurationDataTranslations.macroid, configurationDataTranslations.passClear,configurationDataTranslations.fieldA,configurationDataTranslations.fieldB,"
        + "configurationDataTranslations.constant1,configurationDataTranslations.constant2,configurationDataTranslations.processOrder,configurationDataTranslations.categoryId,"
        + "configurationDataTranslations.defaultValue,configurationDataTranslations.dateAdded,configurationDataTranslations.updatedByImport, "
        + "configurationFormFields.fieldNo, configurationFormFields.required as requiredField, configurationFormFields.fieldDesc "
        + "from configurationDataTranslations inner join "
        + "configurationFormFields on configurationFormFields.id = configurationDataTranslations.fieldId " 
        + "where configurationDataTranslations.configId = :configId "
        + "and configurationDataTranslations.categoryId = :categoryId "
        + "order by configurationDataTranslations.processorder asc;",configurationDataTranslations.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("fieldId", StandardBasicTypes.INTEGER)
        .addScalar("crosswalkId", StandardBasicTypes.INTEGER)
        .addScalar("macroid", StandardBasicTypes.INTEGER)
        .addScalar("passClear", StandardBasicTypes.INTEGER)
        .addScalar("fieldA", StandardBasicTypes.STRING)
        .addScalar("fieldB", StandardBasicTypes.STRING)
        .addScalar("constant1", StandardBasicTypes.STRING)
        .addScalar("constant2", StandardBasicTypes.STRING)
        .addScalar("processOrder", StandardBasicTypes.INTEGER)
        .addScalar("categoryId", StandardBasicTypes.INTEGER)
        .addScalar("defaultValue", StandardBasicTypes.STRING)
        .addScalar("dateAdded", StandardBasicTypes.TIMESTAMP)
        .addScalar("updatedByImport", StandardBasicTypes.BOOLEAN)
        .addScalar("fieldNo", StandardBasicTypes.INTEGER)
        .addScalar("requiredField", StandardBasicTypes.BOOLEAN)
        .addScalar("fieldDesc", StandardBasicTypes.STRING)     
        .setParameter("categoryId", categoryId)
        .setParameter("configId", configId);

        List<Object[]> results = query.getResultList();
        
        List<configurationDataTranslations> cdtList = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            configurationDataTranslations cdt = new configurationDataTranslations();
            cdt.setId((Integer) record[1]);
            cdt.setConfigId((Integer) record[2]);
            cdt.setFieldId((Integer) record[3]);
            cdt.setCrosswalkId((Integer) record[4]);
            cdt.setMacroId((Integer) record[5]);
            cdt.setPassClear((Integer) record[6]);
            cdt.setFieldA((String) record[7]);
            cdt.setFieldB((String) record[8]);
            cdt.setConstant1((String) record[9]);
            cdt.setConstant2((String) record[10]);
            cdt.setProcessOrder((Integer) record[11]);
            cdt.setCategoryId((Integer) record[12]); 
            cdt.setDefaultValue((String) record[13]);
            cdt.setDateAdded((Date) record[14]);
            cdt.setUpdatedByImport((boolean) record[15]);
            cdt.setFieldNo((Integer) record[16]);
            cdt.setRequiredField((boolean) record[17]);
            cdt.setFieldDesc((String) record[18]);
            cdtList.add(cdt);
        });

        return cdtList;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<CrosswalkData> getCrosswalkData(int cwId) {
        
        try {
            
            CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<CrosswalkData> criteria = builder.createQuery(CrosswalkData.class);
            Root<CrosswalkData> root = criteria.from(CrosswalkData.class);

            Predicate whereClause = builder.equal(root.get("crosswalkId"), cwId);

            criteria.where(whereClause);

            return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The 'getHL7Details' function will the HL7 details for the passed in utConfiguration.
     *
     * @Table configurationHL7Details
     *
     * @param	configId This will hold the utConfiguration id to find
     *
     * @return	This function will return a HL7Details object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public HL7Details getHL7Details(int configId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<HL7Details> criteria = builder.createQuery(HL7Details.class);
        Root<HL7Details> root = criteria.from(HL7Details.class);

        Predicate whereClause = builder.equal(root.get("configId"), configId);

        criteria.where(whereClause);

        HL7Details details = (HL7Details) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();

        if (details == null) {
            return null;
        } 
        else {
            return details;
        }
    }

    /**
     * The 'getHL7Segments' function will return the list of segments for a specific HL7 Message.
     *
     * @param hl7Id
     * @Table configurationHL7Segments
     *
     * @return This function will return a list of HL7Segment objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<HL7Segments> getHL7Segments(int hl7Id) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<HL7Segments> criteria = builder.createQuery(HL7Segments.class);
        Root<HL7Segments> root = criteria.from(HL7Segments.class);

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
    public List<HL7Elements> getHL7Elements(int hl7Id, int segmentId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<HL7Elements> criteria = builder.createQuery(HL7Elements.class);
        Root<HL7Elements> root = criteria.from(HL7Elements.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = builder.equal(root.get("hl7Id"), hl7Id);
        predicates[1] = builder.equal(root.get("segmentId"), segmentId);

        Predicate whereClause = builder.and(predicates);

        criteria.orderBy(builder.asc(root.get("displayPos"))).where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'getHL7ElementComponents' function will return any components associated to the passed in element id
     *
     * @param elementId
     *
     * @return This function will return a list of element component objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<HL7ElementComponents> getHL7ElementComponents(int elementId) {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<HL7ElementComponents> criteria = builder.createQuery(HL7ElementComponents.class);
        Root<HL7ElementComponents> root = criteria.from(HL7ElementComponents.class);

        Predicate whereClause = builder.equal(root.get("elementId"), elementId);

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
    public void updateHL7Details(HL7Details details) {
        sessionFactory.getCurrentSession().merge(details);

    }

    /**
     * The 'updateHL7Segments' function will update the segment passed to the function.
     *
     * @param segment The segment object to update
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Segments(HL7Segments segment) {
        sessionFactory.getCurrentSession().merge(segment);
    }

    /**
     * The 'updateHL7Elements' function will update the segment element passed to the function.
     *
     * @param element The segment element object to update.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7Elements(HL7Elements element) {
        sessionFactory.getCurrentSession().merge(element);
    }

    /**
     * The 'updateHL7ElementComponent' function will update the segment element components
     *
     * @param component The element component object to update.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateHL7ElementComponent(HL7ElementComponents component) {
        sessionFactory.getCurrentSession().merge(component);
    }

    /**
     * The 'saveHL7Details' function will save the new HL7 Segment
     *
     * @param newSegment The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Details(HL7Details details) {
        sessionFactory.getCurrentSession().persist(details);
        return details.getId();
    }

    /**
     * The 'saveHL7Segment' function will save the new HL7 Segment
     *
     * @param newSegment The object holding the new HL7 Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Segment(HL7Segments newSegment) {
        sessionFactory.getCurrentSession().persist(newSegment);
        return newSegment.getId();
    }

    /**
     * The 'saveHL7Element' function will save the new HL7 Segment Element
     *
     * @param newElement The object holding the new HL7 Element Object
     */
    @Override
    @Transactional(readOnly = false)
    public int saveHL7Element(HL7Elements newElement) {
        sessionFactory.getCurrentSession().persist(newElement);
        return newElement.getId();
    }

    /**
     * The 'saveHL7Component' function will save the new HL7 Element Component
     *
     * @param newcomponent The object holding the new HL7 Element Component Object
     */
    @Override
    @Transactional(readOnly = false)
    public void saveHL7Component(HL7ElementComponents newcomponent) {
        sessionFactory.getCurrentSession().persist(newcomponent);
    }

    /**
     * The 'getEncodings' function will return a list of available encodings
     *
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getEncodings() {
        try {
            Query query = sessionFactory.getCurrentSession().createNativeQuery("select id, encoding from ref_encoding order by id asc", String.class)
            .addScalar("id", StandardBasicTypes.INTEGER)
            .addScalar("encoding", StandardBasicTypes.STRING);   
            return query.list();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("getEncodings - " + ex.getCause());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void removeHL7ElementComponent(Integer componentId) {
        MutationQuery deleteComponent = sessionFactory.getCurrentSession().createMutationQuery("delete from HL7ElementComponents where id = :componentId");
        deleteComponent.setParameter("componentId", componentId);
        deleteComponent.executeUpdate();
    }

    @Override
    @Transactional(readOnly = false)
    public void removeHL7Element(Integer elementId) {
        MutationQuery deleteComponents = sessionFactory.getCurrentSession().createMutationQuery("delete from HL7ElementComponents where elementId = :elementId");
        deleteComponents.setParameter("elementId", elementId);
        deleteComponents.executeUpdate();

        Query deleteElement = sessionFactory.getCurrentSession().createQuery("delete from HL7Elements where id = :elementId");
        deleteElement.setParameter("elementId", elementId);
        deleteElement.executeUpdate();
    }

    @Override
    @Transactional(readOnly = false)
    public void removeHL7Segment(Integer segmentId) {
        Query deleteComponents = sessionFactory.getCurrentSession().createNativeQuery("delete from configurationhl7elementvalues where elementId in (select id from configurationhl7elements where segmentId = :segmentId)", String.class);
        deleteComponents.setParameter("segmentId", segmentId);
        deleteComponents.executeUpdate();

        MutationQuery deleteElement = sessionFactory.getCurrentSession().createMutationQuery("delete from HL7Elements where segmentId = :segmentId");
        deleteElement.setParameter("segmentId", segmentId);
        deleteElement.executeUpdate();

        MutationQuery deleteSegment = sessionFactory.getCurrentSession().createMutationQuery("delete from HL7Segments where id = :segmentId");
        deleteSegment.setParameter("segmentId", segmentId);
        deleteSegment.executeUpdate();
    }

    /**
     * The 'getCCDElements' function will return the CCD elements for the passed in utConfiguration.
     *
     * @Table configurationCCDElements
     *
     * @param	configId This will hold the utConfiguration id to find
     *
     * @return	This function will return a configurationCCDElements object
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<configurationCCDElements> getCCDElements(Integer configId) throws Exception {

        Query query = sessionFactory.getCurrentSession().createNativeQuery(
        "select configurationCCDElements.*, configurationFormFields.fieldDesc as fieldLabel "
        + "from configurationCCDElements LEFT OUTER JOIN configurationFormFields on "
        + "configurationFormFields.configId = configurationCCDElements.configId and configurationFormFields.fieldNo = configurationCCDElements.fieldValue"
        + " where configurationCCDElements.configId = :configId order by id asc",configurationCCDElements.class)
        .setParameter("configId", configId);

        List<configurationCCDElements> elements = query.list();

        return elements;
    }

    /**
     * The 'getCCDElement' function will return the configurationCCDElement object for the passed in elementId
     *
     * @param elementId The id of the selected element.
     *
     * @return This function will return a single configurationCCDElement
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public configurationCCDElements getCCDElement(Integer elementId) throws Exception {
        return (configurationCCDElements) sessionFactory.
                getCurrentSession().
                get(configurationCCDElements.class, elementId);
    }

    /**
     * The 'saveCCDElement' function will save the new CCD element.
     *
     * @param ccdElement This will hold the new ccdElement object
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = false)
    public void saveCCDElement(configurationCCDElements ccdElement) throws Exception {
        if (Objects.isNull(sessionFactory.getCurrentSession().find(configurationCCDElements.class, ccdElement.getId()))) {
            sessionFactory.getCurrentSession().persist(ccdElement);
        } else {
            sessionFactory.getCurrentSession().merge(ccdElement);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public configurationExcelDetails getExcelDetails(Integer configId, Integer orgId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationExcelDetails> criteria = builder.createQuery(configurationExcelDetails.class);
        Root<configurationExcelDetails> root = criteria.from(configurationExcelDetails.class);

        Predicate[] predicates = new Predicate[2];
        predicates[0] = builder.equal(root.get("configId"), configId);
        predicates[1] = builder.equal(root.get("orgId"), orgId);

        Predicate whereClause = builder.and(predicates);

        criteria.where(whereClause);
        
        List<configurationExcelDetails> excelDetails = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();

        if (excelDetails.size() > 0) {
            return (configurationExcelDetails) excelDetails.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * The 'loadExcelContents' will take the contents of the uploaded excel template file and populate the corresponding utConfiguration form fields table.This function will split up the contents into the appropriate buckets.Buckets (1 - 4) will be separated by spacer rows with in the excel file.
     *
     * @param messageSpecs
     * @param transportDetailId
     * @param fileName	file name of the uploaded excel file.
     * @param hasHeader
     * @param fileLayout
     * @param dir	the directory of the uploaded file
     * @throws java.lang.Exception
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void loadExcelContents(configurationMessageSpecs messageSpecs, int transportDetailId, String fileName, String dir, boolean hasHeader, Integer fileLayout, String currentTemplateFileName) throws Exception {
        String errorMessage = "";
	List<String> templateFields = new ArrayList<>();
	
        try {
	    boolean configHasFields = false;
	    
	    //Check to see if form fields already exist
	    List<configurationFormFields> existingFormFields = configurationTransportDAO.getConfigurationFields(messageSpecs.getConfigId(),transportDetailId);
	    
	    if(existingFormFields != null) {
		if(!existingFormFields.isEmpty()) {
		    configHasFields = true;
		}
	    }
	    
	    utConfiguration configDetails = getConfigurationById(messageSpecs.getConfigId());
           
            //Set the initial value of the field number (0);
            Integer fieldNo = new Integer(0);

            //Create Workbook instance holding reference to .xlsx file
            OPCPackage pkg = null;
            XSSFWorkbook workbook = null;

            try {
                pkg = OPCPackage.open(new File(dir + fileName));
                workbook = new XSSFWorkbook(pkg);
            } 
            catch (Exception e1) {
                e1.printStackTrace();
                errorMessage = errorMessage + "<br/>" + e1.getMessage();
            }

            //Get first/desired sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);

	    //Iterate through each rows one by one
	    Iterator<Row> rowIterator = sheet.iterator();
	    
	    Integer rowCounter = 0;
	    
	    Integer colCounter = 1;
	    
	    String sqlStatement = "";
	    
	    Integer foundFieldId = 0;
	    
	    Query query = null;
	    
	    boolean fieldFound = false;
	    
	    //Parse Vertical template file layout
	    if(fileLayout == 2) {
		
		//Field Desc  | R/0/D | Sample Data | Use/Not Use | Validation
		
		//Check to make sure the sheet has no more than 3 columns
		Integer startRow = 0;
		Integer rowNumber = 0;
		
		if(hasHeader) {
		    startRow = 1;
		}

		Row row = sheet.getRow(startRow);
		
		if(row.getLastCellNum() > 5) {
		    messageSpecs.setTemplateFile(currentTemplateFileName);
		    updateMessageSpecs(messageSpecs, transportDetailId);
                    workbook.close();
		    throw new Exception("The uploaded template file had more than 5 columns, please choose horizontal layout or check your uploaded template file.");
		}
		else if(row.getLastCellNum() < 2) {
		    messageSpecs.setTemplateFile(currentTemplateFileName);
		    updateMessageSpecs(messageSpecs, transportDetailId);
                    workbook.close();
		    throw new Exception("The uploaded template file had only 1 column, please check your uploaded template file.");
		}
		else {
		    //update 4/14/2020 CM - No need to clear anything becauase we are looking for matches in the uploaded file
		    //and existing fields, if a match then we update values else we insert
		    
		    //Clear existing fields
		    //clearMessageSpecFormFields(messageSpecs,transportDetailId);
		    
		    while (rowIterator.hasNext()) {
			row = rowIterator.next();
			
			if(hasHeader && rowCounter == 0) {
			    row = rowIterator.next();
			    rowCounter++;
			}
			rowCounter++;

			//For each row, iterate through all the columns
			Iterator<Cell> cellIterator = row.cellIterator();
			boolean required = false;
			String requiredAsString = "";
			String defaultValue = "";
			String fieldDesc = "";
			String useNotUse = "";
			String validationVal = "";
			String sampleData = "";
			boolean useField = true;
			boolean hasDefault = false;
			
			//1 = none, 2 = Email, 3 = Phone, 4 = Date, 5 = Numeric, 6 = URL
			Integer validationId = 1; 

			//Increase the field number by 1
			fieldNo++;

			while (cellIterator.hasNext()) {
			    Cell cell = cellIterator.next();
			    
			    //Check the cell type and format accordingly
			    switch (cell.getColumnIndex()) {
				case 0:
				    fieldDesc = cell.getStringCellValue();

				    if("not used".equals(fieldDesc.toLowerCase()) || "skip".equals(fieldDesc.toLowerCase()) || "not in use".equals(fieldDesc.toLowerCase())) {
					useField = false;
				    }

				    break;
				case 1:
				    try {
					required = cell.getBooleanCellValue();
				    }
				    catch (Exception ex) {
					try {
					    requiredAsString = cell.getStringCellValue();
					    
					    if(configDetails.getType() == 1 && ("default".equals(requiredAsString.toLowerCase()) || "d".equals(requiredAsString.toLowerCase()))) {
                                                workbook.close();
						throw new Exception("The uploaded template file did not have a correct R/O value (" + requiredAsString + ") in row " + rowCounter + " column 2");
					    }
					    else {
						if("default".equals(requiredAsString.toLowerCase()) || "d".equals(requiredAsString.toLowerCase())) {
						    hasDefault = true;
						    required = false;
						}
						else if("r".equals(requiredAsString.toLowerCase()) || "true".equals(requiredAsString.toLowerCase()) || "t".equals(requiredAsString.toLowerCase())) {
						    required = true;
						}
						else if("o".equals(requiredAsString.toLowerCase()) || "false".equals(requiredAsString.toLowerCase()) || "f".equals(requiredAsString.toLowerCase())) {
						    required = false;
						}
						else {
						    //required = false;
						    if(configDetails.getType() == 1) {
                                                        workbook.close();
							throw new Exception("The uploaded template file did not have a correct R/O value (" + requiredAsString + ") in row " + rowCounter + " column 2");
						    }
						    else {
                                                        workbook.close();
							throw new Exception("The uploaded template file did not have a correct R/O/D value (" + requiredAsString + ") in row " + rowCounter + " column 2");
						    }
						}
					    }
					}
					catch (Exception e) {
					    messageSpecs.setTemplateFile(currentTemplateFileName);
					    updateMessageSpecs(messageSpecs, transportDetailId);
                                            workbook.close();
					    if(e.getMessage() != null && e.getMessage().contains("uploaded template")) {
						throw e;
					    }
					    else {
						if(configDetails.getType() == 1) {
						    throw new Exception("The uploaded template file did not have a correct R/O value in row " + rowCounter + " column 2");
						}
						else {
						    throw new Exception("The uploaded template file did not have a correct R/O/D value in row " + rowCounter + " column 2");
						}
					    }
					}
				    }
				    break;
				case 2: // Default Value
				    try {
					 sampleData = cell.getStringCellValue();
				    }
				    catch (Exception ex) {
					 try {
					    sampleData = String.valueOf((int) cell.getNumericCellValue());
					 }
					 catch(Exception e) {
					     sampleData = "";
					 } 
				    }

				    if(hasDefault) {
					defaultValue = sampleData;
				    }
				    break;

				case 3: // Use/Not Column (U/u=use, N/n=Not)
				    try {
					 useNotUse = cell.getStringCellValue();
					 if("use".equals(useNotUse.toLowerCase()) || "u".equals(useNotUse.toLowerCase()) || "yes".equals(useNotUse.toLowerCase()) || "y".equals(useNotUse.toLowerCase()) || "".equals(useNotUse.toLowerCase())) {
					     useField = true;
					 }
					 else if("not use".equals(useNotUse.toLowerCase()) || "n".equals(useNotUse.toLowerCase()) || "no".equals(useNotUse.toLowerCase())) {
					     useField = false;
					 }
					 else {
                                            workbook.close();
					    throw new Exception("The uploaded template file did not have a correct Use/Not Use value ("+useNotUse+") in row " + rowCounter + " column 4");
					 }
				    }
				    catch (Exception ex) {
					 messageSpecs.setTemplateFile(currentTemplateFileName);
					 updateMessageSpecs(messageSpecs, transportDetailId);
                                         workbook.close();
					 if(ex.getMessage() != null && ex.getMessage().contains("uploaded template")) {
					      throw ex;
					 }
					 else {
					     throw new Exception("The uploaded template file did not have a correct Use/Not Use value in row " + rowCounter + " column 4");
					 }
				    }
				    break;
				    
				case 4: // Validation column
				    try {
					 validationVal = cell.getStringCellValue();
					 if("x".equals(validationVal.toLowerCase()) || "none".equals(validationVal.toLowerCase()) || "no".equals(validationVal.toLowerCase()) || "".equals(validationVal.toLowerCase())) {
					     validationId = 1;
					 }
					 else if("email".equals(validationVal.toLowerCase()) || "e".equals(validationVal.toLowerCase())) {
					     validationId = 2;
					 }
					 else if("phone number".equals(validationVal.toLowerCase()) || "phone".equals(validationVal.toLowerCase()) || "p".equals(validationVal.toLowerCase())) {
					     validationId = 3;
					 }
					 else if("date".equals(validationVal.toLowerCase()) || "d".equals(validationVal.toLowerCase())) {
					     validationId = 4;
					 }
					 else if("numeric".equals(validationVal.toLowerCase()) || "number".equals(validationVal.toLowerCase()) || "integer".equals(validationVal.toLowerCase()) || "i".equals(validationVal.toLowerCase()) || "n".equals(validationVal.toLowerCase())) {
					     validationId = 5;
					 }
					 else if("url".equals(validationVal.toLowerCase()) || "web".equals(validationVal.toLowerCase()) || "website".equals(validationVal.toLowerCase()) || "web site".equals(validationVal.toLowerCase()) || "w".equals(validationVal.toLowerCase()) || "u".equals(validationVal.toLowerCase())) {
					     validationId = 6;
					 }
					 else {
					    workbook.close();
					    throw new Exception("The uploaded template file did not have a correct validation value ("+validationVal+") in row " + rowCounter + " column 5");
					 }
				    }
				    catch (Exception ex) {
					messageSpecs.setTemplateFile(currentTemplateFileName);
					updateMessageSpecs(messageSpecs, transportDetailId);
					workbook.close();
					if(ex.getMessage() != null && ex.getMessage().contains("uploaded template")) {
					    throw ex;
					}
					else {
					    throw new Exception("The uploaded template file did not have a correct validation value in row " + rowCounter + " column 5");
					}
				    }   
				     break;
				default:
				    break;
			    }
			}
			
			fieldFound = false;
			foundFieldId = 0;
			sqlStatement = "";
			
			templateFields.add(fieldDesc);
			
			if(configHasFields) {
			    for(configurationFormFields field : existingFormFields) {
				if(field.getFieldDesc().toLowerCase().equals(fieldDesc.toLowerCase())) {
				    foundFieldId = field.getId();
				    fieldFound = true;
				    sqlStatement = "UPDATE configurationFormFields set fieldNo = :fieldNo, validationType = :validationId, required = :required, useField = :useField, defaultValue = :defaultValue, sampleData = :sampleData "
					+ "where id = :fieldId";
				}
			    }
			}
			
			if(fieldFound && foundFieldId > 0) {
			    query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class)
                            .setParameter("fieldNo", fieldNo) 
                            .setParameter("validationId", validationId)
                            .setParameter("required", required)
                            .setParameter("useField", useField)
                            .setParameter("defaultValue", defaultValue)
                            .setParameter("sampleData", sampleData)
                            .setParameter("fieldId", foundFieldId);
			}
			else {
			    sqlStatement = "INSERT INTO configurationFormFields (configId, transportDetailId, fieldNo, fieldDesc, validationType, required, useField, defaultValue, sampleData)"
                            + " VALUES (:configId, :transportDetailId, :fieldNo, :fieldDesc, :validationId, :required, :useField, :defaultValue, :sampleData)";
			    
			    query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class)
                            .setParameter("configId", messageSpecs.getConfigId())
                            .setParameter("transportDetailId", transportDetailId)
                            .setParameter("fieldNo", fieldNo)
                            .setParameter("fieldDesc", fieldDesc)
                            .setParameter("validationId", validationId)
                            .setParameter("required", required)
                            .setParameter("useField", useField)
                            .setParameter("defaultValue", defaultValue)
                            .setParameter("sampleData", sampleData);
			}
			
			query.executeUpdate();
		    }
		}
	    }
	    else if(fileLayout == 1) {
                
		if(sheet.getLastRowNum() > 5) {
		    messageSpecs.setTemplateFile(currentTemplateFileName);
		    updateMessageSpecs(messageSpecs, transportDetailId);
                    workbook.close();
		    throw new Exception("The uploaded template file had more than 5 rows, please choose vertical layout or check your uploaded template file.");
		}
		else if(sheet.getLastRowNum() < 2) {
		    messageSpecs.setTemplateFile(currentTemplateFileName);
		    updateMessageSpecs(messageSpecs, transportDetailId);
                    workbook.close();
		    throw new Exception("The uploaded template file had less than 3 rows, please choose horizontal layout or check your uploaded template file.");
		}
		else {
		    Integer startRow = 0;
		    Integer rowNumber = 0;

		    if(hasHeader) {
			startRow = 1;
		    }

		    Row row = sheet.getRow(startRow);
		    int totalCols = row.getLastCellNum();

		    String fieldDesc = "";
		    String sampleData = "";
		    String defaultValue = "";
		    boolean required = false;
		    String requiredAsString = "";
		    boolean useField = true;
		    String useNotUse = "";
		    String validationVal = "";
		    Integer validationId = 1;

		    for(int colNumber = 0; colNumber<totalCols; colNumber++) {
			fieldDesc = "";
			defaultValue = "";
			required = false;
			requiredAsString = "";
			fieldNo = colNumber+1;
			useField = true;
			colCounter++;

			rowNumber = startRow;
			//Get the field name
			row = sheet.getRow(rowNumber);

			Cell cell = row.getCell(colNumber);
			fieldDesc = cell.getStringCellValue();

			if("not used".equals(fieldDesc.toLowerCase()) || "skip".equals(fieldDesc.toLowerCase()) || "not in use".equals(fieldDesc.toLowerCase())) {
			    useField = false;
			}

			//Get the sample data/default value
			rowNumber = rowNumber+1;
			
			try {
			    row = sheet.getRow(rowNumber);
			    cell = row.getCell(colNumber);
			
			    try {
				 sampleData = String.valueOf((double) cell.getNumericCellValue());

				 if(DateUtil.isCellDateFormatted(cell)) {
				     CellStyle cellstyle = cell.getCellStyle();
				     SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-d");

				     if(cellstyle.getDataFormatString().equals("m/d/yy")) {
					format = new SimpleDateFormat("M/d/yy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("mm/d/yy;@")) {
					format = new SimpleDateFormat("MM/d/yy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("yyyymmd;@")) {
					format = new SimpleDateFormat("yyyyMMd");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("[$-409]d\\-mmm;@")) {
					format = new SimpleDateFormat("d-MMM");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("[$-409]d\\-mmm\\-yy;@")) {
					format = new SimpleDateFormat("d-MMM-yy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("[$-409]mmm\\-yy;@")) {
					format = new SimpleDateFormat("MMM-yy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("[$-409]mmmm\\-yy;@")) {
					format = new SimpleDateFormat("MMMM-yy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("[$-409]mmmm\\ d\\,\\ yyyy;@")) {
					format = new SimpleDateFormat("MMMM d, yyyy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("m/d/yyyy;@")) {
					format = new SimpleDateFormat("M/d/yyyy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else if(cellstyle.getDataFormatString().equals("[$-409]d\\-mmm\\-yyyy;@")) {
					format = new SimpleDateFormat("d-MMM-yyyy");
					sampleData = format.format(cell.getDateCellValue());
				     }
				     else {
					 sampleData = format.format(cell.getDateCellValue());
				     }
				 }
				 else {
				    sampleData = cell.getStringCellValue();
				 }
			    }
			    catch (Exception ex1) {
				 try {
				     sampleData = cell.getDateCellValue().toString();
				 }
				 catch (Exception ex2) {
				     try {
					sampleData = cell.getStringCellValue(); 
				     }
				     catch(Exception ex3) {
					 sampleData = "";
				     } 
				 }
			     } 
			}
			catch (Exception cellIsNull) {
			    sampleData = "";
			}

			//Get the r/o/d value
			rowNumber = rowNumber+1;
			
			try {
			    row = sheet.getRow(rowNumber);
			    cell = row.getCell(colNumber);
			    required = cell.getBooleanCellValue();
			}
			catch (Exception ex) {
			    try {
				row = sheet.getRow(rowNumber);
				cell = row.getCell(colNumber);
				requiredAsString = cell.getStringCellValue();
                                
				if(configDetails.getType() == 1 && ("default".equals(requiredAsString.toLowerCase()) || "d".equals(requiredAsString.toLowerCase()))) {
                                    workbook.close();
				    throw new Exception("The uploaded template file did not have a correct R/O value (" + requiredAsString + ") in in row 3 column " + fieldNo);
				}
				else {
				    if("default".equals(requiredAsString.toLowerCase()) || "d".equals(requiredAsString.toLowerCase())) {
					defaultValue = sampleData;
					required = false;
				    }
				    else if("r".equals(requiredAsString.toLowerCase()) || "true".equals(requiredAsString.toLowerCase()) || "t".equals(requiredAsString.toLowerCase())) {
					required = true;
					defaultValue = "";
				    }
				    else if("o".equals(requiredAsString.toLowerCase()) || "false".equals(requiredAsString.toLowerCase()) || "f".equals(requiredAsString.toLowerCase())) {
					required = false;
					defaultValue = "";
				    }
				    else {
                                        if(configDetails.getType() == 1) {
                                            workbook.close();
					    throw new Exception("The uploaded template file did not have a correct R/O value ("+requiredAsString+") in row 3 column " + fieldNo); 
					}
					else {
                                            workbook.close();
					    throw new Exception("The uploaded template file did not have a correct R/O/D value ("+requiredAsString+") in row 3 column " + fieldNo);  
					}
				    }
				}
			    }
			    catch (Exception e) {
				messageSpecs.setTemplateFile(currentTemplateFileName);
				updateMessageSpecs(messageSpecs, transportDetailId);
                                workbook.close();
				if(e.getMessage() != null && e.getMessage().contains("uploaded template")) {
				     throw e;
				}
				else {
				    if(configDetails.getType() == 1) {
					throw new Exception("The uploaded template file did not have a correct R/O value in row 3 column " + fieldNo); 
				    }
				    else {
					throw new Exception("The uploaded template file did not have a correct R/O/D value in row 3 column " + fieldNo);
				    }
				}
			    }
			}
			
			//Get the Use/Not Use Row
			rowNumber = rowNumber+1;
			try {
			    row = sheet.getRow(rowNumber);
			    cell = row.getCell(colNumber);
			
			    useNotUse = cell.getStringCellValue();
			    if("use".equals(useNotUse.toLowerCase()) || "u".equals(useNotUse.toLowerCase()) || "yes".equals(useNotUse.toLowerCase()) || "y".equals(useNotUse.toLowerCase()) || "".equals(useNotUse.toLowerCase())) {
				useField = true;
			    }
			    else if("not use".equals(useNotUse.toLowerCase()) || "n".equals(useNotUse.toLowerCase()) || "no".equals(useNotUse.toLowerCase())) {
				useField = false;
			    }
			    else {
                                workbook.close();
				throw new Exception("The uploaded template file did not have a correct Use/Not Use value (" + useNotUse+ ") in row 4 column " + fieldNo);
			    }
			}
			catch (Exception ex) {
			    messageSpecs.setTemplateFile(currentTemplateFileName);
			    updateMessageSpecs(messageSpecs, transportDetailId);
                            workbook.close();
			    if(ex.getMessage() != null && ex.getMessage().contains("uploaded template")) {
				throw ex;
			    }
			    else {
			       throw new Exception("The uploaded template file did not have a correct Use/Not Use value in row 4 column " + fieldNo);
			    }
			}
			
			//Get the validation Id
			rowNumber = rowNumber + 1;
			try {
			    row = sheet.getRow(rowNumber);
			    cell = row.getCell(colNumber);
			
			    validationVal = cell.getStringCellValue();
			    
			    if("x".equals(validationVal.toLowerCase()) || "none".equals(validationVal.toLowerCase()) || "no".equals(validationVal.toLowerCase()) || "".equals(validationVal.toLowerCase())) {
				validationId = 1;
			    }
			    else if("email".equals(validationVal.toLowerCase()) || "e".equals(validationVal.toLowerCase())) {
				validationId = 2;
			    }
			    else if("phone number".equals(validationVal.toLowerCase()) || "phone".equals(validationVal.toLowerCase()) || "p".equals(validationVal.toLowerCase())) {
				validationId = 3;
			    }
			    else if("date".equals(validationVal.toLowerCase()) || "d".equals(validationVal.toLowerCase())) {
				validationId = 4;
			    }
			    else if("numeric".equals(validationVal.toLowerCase()) || "number".equals(validationVal.toLowerCase()) || "integer".equals(validationVal.toLowerCase()) || "i".equals(validationVal.toLowerCase()) || "n".equals(validationVal.toLowerCase())) {
				validationId = 5;
			    }
			    else if("url".equals(validationVal.toLowerCase()) || "web".equals(validationVal.toLowerCase()) || "website".equals(validationVal.toLowerCase()) || "web site".equals(validationVal.toLowerCase()) || "w".equals(validationVal.toLowerCase()) || "u".equals(validationVal.toLowerCase())) {
				validationId = 6;
			    }
			    else {
                                workbook.close();
				throw new Exception("The uploaded template file did not have a correct validation value (" + validationVal + ") in row 5 column " + fieldNo);
                            }
		       }
		       catch (Exception ex) {
			   messageSpecs.setTemplateFile(currentTemplateFileName);
			   updateMessageSpecs(messageSpecs, transportDetailId);
                           workbook.close();
			   if(ex.getMessage() != null && ex.getMessage().contains("uploaded template")) {
				throw ex;
			   }
			   else {
			       throw new Exception("The uploaded template file did not have a correct validation value in row 5 column " + fieldNo);
			   }
		       }      

			fieldFound = false;
			foundFieldId = 0;
			sqlStatement = "";
			
			templateFields.add(fieldDesc);
			
			if(configHasFields) {
			    for(configurationFormFields field : existingFormFields) {
				if(field.getFieldDesc().toLowerCase().equals(fieldDesc.toLowerCase())) {
				    foundFieldId = field.getId();
				    fieldFound = true;
				    sqlStatement = "UPDATE configurationFormFields set fieldNo = :fieldNo, validationType = :validationId, required = :required, useField = :useField, defaultValue = :defaultValue, sampleData = :sampleData "
					+ "where id = :fieldId";
				}
			    }
			}
			
			if(fieldFound && foundFieldId > 0) {
			    query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class)
                            .setParameter("fieldNo", fieldNo) 
                            .setParameter("validationId", validationId)
                            .setParameter("required", required)
                            .setParameter("useField", useField)
                            .setParameter("defaultValue", defaultValue)
                            .setParameter("sampleData", sampleData.replace(".0", ""))
                            .setParameter("fieldId", foundFieldId);
			}
			else {
			    sqlStatement = "INSERT INTO configurationFormFields (configId, transportDetailId, fieldNo, fieldDesc, validationType, required, useField, defaultValue, sampleData)"
                            + " VALUES (:configId, :transportDetailId, :fieldNo, :fieldDesc, :validationId, :required, :useField, :defaultValue, :sampleData)";
			    
			    query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class)
                            .setParameter("configId", messageSpecs.getConfigId())
                            .setParameter("transportDetailId", transportDetailId)
                            .setParameter("fieldNo", fieldNo)
                            .setParameter("fieldDesc", fieldDesc)
                            .setParameter("validationId", validationId)
                            .setParameter("required", required)
                            .setParameter("useField", useField)
                            .setParameter("defaultValue", defaultValue)
                            .setParameter("sampleData", sampleData.replace(".0", ""));
			}
			query.executeUpdate();
		    }
		}
	    }
	    
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = errorMessage + "<br/>" + e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = errorMessage + "<br/>" + e.getMessage();
        }

        /**
         * throw error message here because want to make sure file stream is closed *
         */
        if (!errorMessage.equalsIgnoreCase("")) {
            throw new Exception(errorMessage);
        }
	
	//Clear out fields that were not found in the file.
	List<configurationFormFields> formFields = configurationTransportDAO.getConfigurationFields(messageSpecs.getConfigId(),transportDetailId);
	
	if(!formFields.isEmpty() && !templateFields.isEmpty()) {
	    String formFieldDesc = "";
	    boolean found = false;
	    Integer fieldId = 0;
	    Query removeQuery;
	    for(configurationFormFields formField : formFields) {
		found = false;
		formFieldDesc = formField.getFieldDesc().trim().toLowerCase();
		fieldId = formField.getId();
		
		for(String templateField : templateFields) {
		    if(templateField.toLowerCase().trim().equals(formFieldDesc)) {
			found = true;
		    }
		}
		
		if(!found && fieldId > 0) {
		    removeQuery = sessionFactory.getCurrentSession().createNativeQuery("delete from configurationdatatranslations where fieldId = :fieldId", String.class)
		    .setParameter("fieldId", fieldId);
		    removeQuery.executeUpdate();
		    removeQuery = sessionFactory.getCurrentSession().createNativeQuery("delete from configurationFormFields where id = :fieldId", String.class)
		    .setParameter("fieldId", fieldId);
		    removeQuery.executeUpdate();
		}
	    }
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void updateExcelConfigDetails(Integer orgId, configurationMessageSpecs messageSpecs) throws Exception {
	
	//Delete existing entry
	MutationQuery deleteTranslations = sessionFactory.getCurrentSession().createMutationQuery("delete from configexceldetails where configId = :configId and orgId = :orgId");
        deleteTranslations.setParameter("configId", messageSpecs.getConfigId());
        deleteTranslations.setParameter("orgId", orgId);
        deleteTranslations.executeUpdate();
	
	//Insert new entry
	configexceldetails configexceldetails = new configexceldetails();
	configexceldetails.setConfigId(messageSpecs.getConfigId());
	configexceldetails.setOrgId(orgId);
	configexceldetails.setStartRow(messageSpecs.getExcelstartrow());
	configexceldetails.setDiscardLastRows(messageSpecs.getExcelskiprows());
	
	sessionFactory.getCurrentSession().persist(configexceldetails);
    }
    
    /**
     * The 'getZipTypes' function will return a list of available zip types
     *
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getZipTypes() {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT id, zipType FROM lu_ziptypes order by id asc", String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("zipType", StandardBasicTypes.STRING);        

        return query.getResultList();
    }
    
    /**
     * The 'getrestAPITypes' function will return a list of available zip types
     *
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getrestAPITypes() {
	Query query = sessionFactory.getCurrentSession().createNativeQuery("SELECT id, apiType FROM lu_restapitypes order by id asc", String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("apiType", StandardBasicTypes.STRING);           

	return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getConnectionsBySrcAndTargetConfigurations(int sourceConfigId, int targetConfigId) {
	
	SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationConnection where sourceConfigId = :sourceConfigId and targetConfigId = :targetConfigId and status = TRUE");
	query.setParameter("sourceConfigId", sourceConfigId);
	query.setParameter("targetConfigId", targetConfigId);

	List<configurationConnection> connections = query.list();
	return connections;
    }
    
    /**
     * The 'getrestAPIFunctions' function will return a list of available api functions
     *
     * @param orgId
     * @return 
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(readOnly = true)
    public List getrestAPIFunctions(Integer orgId) {
	
	String sql = "select id, functionName from lu_availablerestapifunctions where forOrgId = 0 or forOrgId = :orgId order by id asc";
	
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("functionName", StandardBasicTypes.STRING)  
	.setParameter("orgId", orgId);

        return query.getResultList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<watchlist> getDashboardWatchList() throws Exception {
	
	String sql = "select a.id, a.orgId, a.configId, a.expected, a.expectFirstFile, a.dateCreated, a.expectFirstFileTime, a.nextInsertDate, a.entryMessage,"
        + "b.orgName, c.configName, e.transportMethod "
        + "from dashboardwatchlist a left outer join "
        + "organizations b on a.orgId = b.id left outer join "
        + "configurations c on a.configId = c.id left outer join " 
        + "configurationtransportdetails d on a.configId = d.configId left outer join " 
        + "ref_transportmethods e on d.transportMethodId = e.id "
        + "order by a.dateCreated desc";
        
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,watchlist.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("orgId", StandardBasicTypes.INTEGER)    
        .addScalar("configId", StandardBasicTypes.INTEGER) 
        .addScalar("expected", StandardBasicTypes.STRING)              
        .addScalar("expectFirstFile", StandardBasicTypes.STRING)      
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)     
        .addScalar("expectFirstFileTime", StandardBasicTypes.STRING)     
        .addScalar("nextInsertDate", StandardBasicTypes.TIMESTAMP)   
        .addScalar("orgName", StandardBasicTypes.STRING)                
        .addScalar("entryMessage", StandardBasicTypes.STRING)        
        .addScalar("configName", StandardBasicTypes.STRING)
        .addScalar("transportMethod", StandardBasicTypes.STRING);
        
        List<Object[]> results = query.getResultList();
        
        List<watchlist> watchListEntries = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            watchlist watchListEntry = new watchlist();
            watchListEntry.setId((Integer) record[1]);
            watchListEntry.setOrgId((Integer) record[2]);
            watchListEntry.setConfigId((Integer) record[3]);
            watchListEntry.setExpected((String) record[4]);
            watchListEntry.setExpectFirstFile((String) record[5]);
            watchListEntry.setDateCreated((Date) record[6]);
            watchListEntry.setExpectFirstFileTime((String) record[7]);
            watchListEntry.setNextInsertDate((Date) record[8]);
            watchListEntry.setOrgName((String) record[9]);
            watchListEntry.setEntryMessage((String) record[10]);
            watchListEntry.setConfigName((String) record[11]);
            watchListEntry.setTransportMethod((String) record[12]);
            watchListEntries.add(watchListEntry);
        });
	
	return watchListEntries;
    }
    
    @Override
    @Transactional(readOnly = true)
    public watchlist getDashboardWatchListById(int watchId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<watchlist> criteria = builder.createQuery(watchlist.class);
        Root<watchlist> root = criteria.from(watchlist.class);
        
        Predicate whereClause = builder.equal(root.get("id"), watchId);

        criteria.where(whereClause);
        
        return (watchlist) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();
    }
    
    /**
     * The 'saveDashboardWatchListEntry' function will save the new watchlist
     *
     * @param watchListEntry The object holding the new watchlist
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public Integer saveDashboardWatchListEntry(watchlist watchListEntry) {
        sessionFactory.getCurrentSession().persist(watchListEntry);
        return watchListEntry.getId();
    }
    
    /**
     * The 'updateDashboardWatchListEntry' function will update the watchlist entry
     *
     * @param watchListEntry The object holding the watchlist
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void updateDashboardWatchListEntry(watchlist watchListEntry) {
        sessionFactory.getCurrentSession().merge(watchListEntry);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<watchlist> getDashboardWatchListToInsert() throws Exception {
	
	String sql = "SELECT a.*, IFNULL(c.messageTypeId, 0) as messageTypeId "
        + "FROM dashboardwatchlist a left outer join "
        + "configurations c on c.id = a.configId "
        + "where nextInsertDate <= now();";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,watchlist.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("orgId", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("expected", StandardBasicTypes.STRING)
        .addScalar("expectFirstFile", StandardBasicTypes.STRING)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("expectFirstFileTime", StandardBasicTypes.STRING)
        .addScalar("nextInsertDate", StandardBasicTypes.DATE)
        .addScalar("entryMessage", StandardBasicTypes.STRING)
        .addScalar("messageTypeId", StandardBasicTypes.INTEGER);
        
        List<Object[]> results = query.getResultList();
        
        List<watchlist> watchlist = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            watchlist watchlistEntry = new watchlist();
            watchlistEntry.setId((Integer) record[1]);
            watchlistEntry.setOrgId((Integer) record[2]);
            watchlistEntry.setConfigId((Integer) record[3]);
            watchlistEntry.setExpected((String) record[4]);
            watchlistEntry.setExpectFirstFile((String) record[5]);
            watchlistEntry.setDateCreated((Date) record[6]);
            watchlistEntry.setExpectFirstFileTime((String) record[7]);
            watchlistEntry.setNextInsertDate((Date) record[8]);
            watchlistEntry.setEntryMessage((String) record[9]);
            watchlistEntry.setMessageTypeId((Integer) record[10]);
            watchlist.add(watchlistEntry);
        });

        return watchlist;
    }
    
    /**
     * The 'insertDashboardWatchListEntry' function will save the new watchlist
     *
     * @param watchListEntry The object holding the new watchlist
     *
     * @return This function does not return anything.
     */
    @Override
    @Transactional(readOnly = false)
    public void insertDashboardWatchListEntry(watchlistEntry watchListEntry) {
        if (Objects.isNull(sessionFactory.getCurrentSession().find(watchlistEntry.class, watchListEntry.getId()))) {
            sessionFactory.getCurrentSession().persist(watchListEntry);
        } else {
            sessionFactory.getCurrentSession().merge(watchListEntry);
        }
    }
    
    /**
     * The 'getAllUploadedBatches' function will return a list of batches for the admin in the processing activities section.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of batch uploads
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<watchlistEntry> getWatchListEntries(Date fromDate, Date toDate) throws Exception {

	int firstResult = 0;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	String sql = "select a.*, b.transportMethodId, c.configName, o.orgName, e.transportMethod "
        + "from dashboardwatchlistentries a "
        + "inner join configurationtransportdetails b on b.configId = a.configId "
        + "inner join configurations c on c.id = a.configId "
        + "inner join organizations o on o.id = c.orgId "
        + "inner join ref_transportmethods e on e.id = b.transportMethodId "
        + "where a.dateCreated >= '" + sdf.format(fromDate) + " 00:00:00' and a.dateCreated < '" + sdf.format(toDate) + " 23:59:59' order by dateCreated desc";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,watchlistEntry.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("watchlistentryId", StandardBasicTypes.INTEGER)
        .addScalar("orgId", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("messageTypeId", StandardBasicTypes.INTEGER)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("watchListCompleted", StandardBasicTypes.BOOLEAN)
        .addScalar("transportMethodId", StandardBasicTypes.INTEGER)        
        .addScalar("configName", StandardBasicTypes.STRING)
        .addScalar("orgName", StandardBasicTypes.STRING)
        .addScalar("transportMethod", StandardBasicTypes.STRING);   
        
        List<Object[]> results = query.getResultList();
        
        List<watchlistEntry> watchlistEntries = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            watchlistEntry watchlist = new watchlistEntry();
            watchlist.setId((Integer) record[1]);
            watchlist.setOrgId((Integer) record[2]);
            watchlist.setConfigId((Integer) record[3]);
            watchlist.setMessageTypeId((Integer) record[4]);
            watchlist.setDateCreated((Date) record[6]);
            watchlist.setWatchListCompleted((Boolean) record[7]);
            watchlist.setTransportMethodId((Integer) record[8]);
            watchlist.setConfigName((String) record[9]);
            watchlist.setOrgName((String) record[10]);
            watchlist.setTransportMethod((String) record[11]);
            watchlistEntries.add(watchlist);
        });
        
        return watchlistEntries;
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deleteWatchEntry(Integer watchId) throws Exception {
	
	String sql = "delete from dashboardwatchlistentries where watchlistentryid = "+watchId+"; delete from dashboardwatchlist where id = "+watchId+";";
	
	Query deleteWatchEntry = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
        deleteWatchEntry.executeUpdate();
    }
    
    /**
     * The 'getGenericWatchListEntries' function will return a list of generic watch list entries.
     *
     * @param fromDate
     * @param toDate
     * @return This function will return a list of generic watch list entries
     * @throws Exception
     */
    @Override
    @Transactional(readOnly = true)
    public List<watchlistEntry> getGenericWatchListEntries(Date fromDate, Date toDate) throws Exception {

	int firstResult = 0;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	String sql = "select a.*, b.entryMessage "
        + "from dashboardwatchlistentries a inner join "
        + "dashboardwatchlist b on a.watchlistentryId = b.id "
        + "where a.orgId = 0 "
        + "and a.configId = 0 "
        + "and ((a.watchListCompleted = 0) or (a.dateCreated >= '" + sdf.format(fromDate) + " 00:00:00' and a.dateCreated < '" + sdf.format(toDate) + " 23:59:59')) "
        + "order by dateCreated desc";
        
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,watchlistEntry.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("watchlistentryId", StandardBasicTypes.INTEGER)
        .addScalar("orgId", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("messageTypeId", StandardBasicTypes.INTEGER)
        .addScalar("dateCreated", StandardBasicTypes.DATE)
        .addScalar("watchListCompleted", StandardBasicTypes.BOOLEAN)
        .addScalar("entryMessage", StandardBasicTypes.STRING);
        
        List<Object[]> results = query.getResultList();
        
        List<watchlistEntry> watchlistEntries = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            watchlistEntry watchlist = new watchlistEntry();
            watchlist.setId((Integer) record[1]);
            watchlist.setOrgId((Integer) record[2]);
            watchlist.setConfigId((Integer) record[3]);
            watchlist.setMessageTypeId((Integer) record[4]);
            watchlist.setDateCreated((Date) record[6]);
            watchlist.setWatchListCompleted((Boolean) record[7]);
            watchlist.setEntryMessage((String) record[8]);
            watchlistEntries.add(watchlist);
        });
        
        return watchlistEntries;
    }
    
    @Override
    @Transactional(readOnly = true)
    public watchlistEntry getWatchListEntry(Integer entryId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<watchlistEntry> criteria = builder.createQuery(watchlistEntry.class);
        Root<watchlistEntry> root = criteria.from(watchlistEntry.class);

        Predicate whereClause = builder.equal(root.get("id"), entryId);

        criteria.where(whereClause);
        
        return (watchlistEntry) sessionFactory.getCurrentSession().createQuery(criteria).uniqueResult();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration>  getAllActiveSourceConfigurations() throws Exception {
	
	String sql = "select a.id, a.configName, b.orgName, a.dateCreated, a.status, a.type, a.orgId, a.messageTypeId, a.stepsCompleted, a.threshold, a.configurationType, a.deleted "
        + "from configurations a inner join "
        + "organizations b on b.id = a.orgId "
        + "where a.type = 1 and a.deleted = 0 and a.status = 1";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utConfiguration.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("configName", StandardBasicTypes.STRING)
        .addScalar("orgName", StandardBasicTypes.STRING)
        .addScalar("stepsCompleted", StandardBasicTypes.INTEGER);
        
        List<Object[]> results = query.getResultList();
        
        List<utConfiguration> configs = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            utConfiguration config = new utConfiguration();
            config.setId((Integer) record[1]);
            config.setConfigname((String) record[2]);
            config.setOrgName((String) record[3]);
            config.setStepsCompleted((Integer) record[4]);
            configs.add(config);
        });
	
	return configs;
    }
    
    /**
     * The 'getConnectionsBySourceConfiguration' will return a list of source connections for a passed in utConfiguration;
     *
     * @param configId The id of the utConfiguration to search connections for.
     *
     * @return This function will return a list of configurationConnection objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getConnectionsBySourceConfiguration(Integer configId) {
        SelectionQuery query = sessionFactory.getCurrentSession().createSelectionQuery("from configurationConnection where sourceConfigId = :configId");
        query.setParameter("configId", configId);

        List<configurationConnection> connections = query.list();
        return connections;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration>  getAllSourceConfigurations() throws Exception {
	
	String sql = "select a.id, a.configName, b.orgName, b.helRegistryId, b.cleanURL as cleanOrgURL, IFNULL(c.id ,0) as transportDetailId, IFNULL(c.transportMethodId, 0) as transportMethodId,"
        + "a.dateCreated, (select dateCreated from configurationupdatelogs where configId = a.id order by id desc limit 1) as dateUpdated," 
        + "IFNULL(d.transportMethod,'N/A') as transportMethod,"
        + "IFNULL(e.type, 0) as scheduleType,"
        + "CASE WHEN f.id is null then 0 else 1 end as allowFTPLink,"
        + "CASE WHEN g.id is null then '' else g.directory end as fileDropLocation, "   
        + "a.status, a.type, a.orgId, a.messageTypeId, a.stepsCompleted, a.threshold, a.configurationType, a.deleted "
        + "from configurations a inner join "
        + "organizations b on b.id = a.orgId left outer join "
        + "configurationtransportdetails c on c.configId = a.id left outer join "
        + "ref_transportmethods d on d.id = c.transportMethodId left outer join "
        + "configurationschedule e on e.configId = a.id left outer join "
        + "rel_transportftpdetails f on f.transportId = c.id and f.method = 1 left outer join "
        + "rel_transportfiledropdetails g on g.transportId = c.id and g.method = 1 "
        + "where a.deleted = 0 and a.type = 1";
        
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utConfiguration.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("configName", StandardBasicTypes.STRING)
        .addScalar("orgName", StandardBasicTypes.STRING)
        .addScalar("helRegistryId", StandardBasicTypes.INTEGER)
        .addScalar("cleanOrgURL", StandardBasicTypes.STRING)
        .addScalar("transportDetailId", StandardBasicTypes.INTEGER)
        .addScalar("transportMethodId", StandardBasicTypes.INTEGER)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("dateUpdated", StandardBasicTypes.TIMESTAMP)
        .addScalar("transportMethod", StandardBasicTypes.STRING)
        .addScalar("scheduleType", StandardBasicTypes.INTEGER)
        .addScalar("allowFTPLink", StandardBasicTypes.BOOLEAN)
        .addScalar("fileDropLocation", StandardBasicTypes.STRING)
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .addScalar("type", StandardBasicTypes.INTEGER)
        .addScalar("orgId", StandardBasicTypes.INTEGER)
        .addScalar("messageTypeId", StandardBasicTypes.INTEGER)
        .addScalar("stepsCompleted", StandardBasicTypes.INTEGER)
        .addScalar("threshold", StandardBasicTypes.INTEGER)
        .addScalar("configurationType", StandardBasicTypes.INTEGER)
        .addScalar("deleted", StandardBasicTypes.BOOLEAN);    
        
        List<Object[]> results = query.getResultList();
        
        List<utConfiguration> configs = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            utConfiguration config = new utConfiguration();
            config.setId((Integer) record[1]);
            config.setConfigname((String) record[2]);
            config.setOrgName((String) record[3]);
            config.setHelRegistryId((Integer) record[4]);
            config.setCleanOrgURL((String) record[5]);
            config.setTransportDetailId((Integer) record[6]);
            config.setTransportMethodId((Integer) record[7]);
            config.setDateCreated((Date) record[8]);
            config.setDateUpdated((Date) record[9]);
            config.setTransportMethod((String) record[10]);
            config.setStatus((Boolean) record[14]);
            config.setType((Integer) record[15]);
            config.setOrgId((Integer) record[16]);
            config.setMessageTypeId((Integer) record[17]);
            config.setStepsCompleted((Integer) record[18]);
            config.setThreshold((Integer) record[19]);
            config.setConfigurationType((Integer) record[20]);
            config.setDeleted((Boolean) record[21]);
            configs.add(config);
        });
	
	return configs;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<utConfiguration>  getAllTargetConfigurations() throws Exception {
	
	String sql = "select a.id, a.configName, b.orgName, b.cleanURL as cleanOrgURL, IFNULL(c.id ,0) as transportDetailId, IFNULL(c.transportMethodId, 0) as transportMethodId,"
        + "a.dateCreated, (select dateCreated from configurationupdatelogs where configId = a.id order by id desc limit 1) as dateUpdated," 
        + "IFNULL(d.transportMethod,'N/A') as transportMethod,"
        + "IFNULL(e.type, 0) as scheduleType, "
         + "a.status, a.type, a.orgId, a.messageTypeId, a.stepsCompleted, a.threshold, a.configurationType, a.deleted "        
        + "from configurations a inner join "
        + "organizations b on b.id = a.orgId left outer join "
        + "configurationtransportdetails c on c.configId = a.id left outer join "
        + "ref_transportmethods d on d.id = c.transportMethodId left outer join "
        + "configurationschedule e on e.configId = a.id "
        + "where a.deleted = 0 and a.type = 2";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,utConfiguration.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("configName", StandardBasicTypes.STRING)
        .addScalar("orgName", StandardBasicTypes.STRING)
        .addScalar("cleanOrgURL", StandardBasicTypes.STRING)
        .addScalar("transportDetailId", StandardBasicTypes.INTEGER)
        .addScalar("transportMethodId", StandardBasicTypes.INTEGER)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("dateUpdated", StandardBasicTypes.TIMESTAMP)
        .addScalar("transportMethod", StandardBasicTypes.STRING)
        .addScalar("scheduleType", StandardBasicTypes.INTEGER)
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .addScalar("type", StandardBasicTypes.INTEGER)
        .addScalar("orgId", StandardBasicTypes.INTEGER)
        .addScalar("messageTypeId", StandardBasicTypes.INTEGER)
        .addScalar("stepsCompleted", StandardBasicTypes.INTEGER)
        .addScalar("threshold", StandardBasicTypes.INTEGER)
        .addScalar("configurationType", StandardBasicTypes.INTEGER)
        .addScalar("deleted", StandardBasicTypes.BOOLEAN);    
        
        List<Object[]> results = query.getResultList();
        
        List<utConfiguration> configs = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            utConfiguration config = new utConfiguration();
            config.setId((Integer) record[1]);
            config.setConfigname((String) record[2]);
            config.setOrgName((String) record[3]);
            config.setCleanOrgURL((String) record[4]);
            config.setTransportDetailId((Integer) record[5]);
            config.setTransportMethodId((Integer) record[6]);
            config.setDateCreated((Date) record[7]);
            config.setDateUpdated((Date) record[8]);
            config.setTransportMethod((String) record[9]);
            config.setStatus((Boolean) record[11]);
            config.setType((Integer) record[12]);
            config.setOrgId((Integer) record[13]);
            config.setMessageTypeId((Integer) record[14]);
            config.setStepsCompleted((Integer) record[15]);
            config.setThreshold((Integer) record[16]);
            config.setConfigurationType((Integer) record[17]);
            config.setDeleted((Boolean) record[18]);
            configs.add(config);
        });
	
	return configs;
    }
    
    @Override
    @Transactional(readOnly = false)
    public List getDTCWForDownload(String sqlStatement) throws Exception {
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class);

	List dataTranslations = query.list();
	
	return dataTranslations;
    }
    
    /**
     * The 'saveConfigurationUpdateLog' function will save the configuration update log entry
     *
     * @param updateLog	the configurationUpdateLogs object
     *
     */
    @Override
    @Transactional(readOnly = false)
    public void saveConfigurationUpdateLog(configurationUpdateLogs updateLog) {
        sessionFactory.getCurrentSession().persist(updateLog);
    }
    
    /**
     * The 'getLastConfigUpdateLog' function will return the last update log for the passed in configuration id.
     *
     * @Table	configurationUpdateLogs
     *
     * @param	configId
     *
     * @return	This function will return the latest configurationUpdateLogs object
     */
    @Override
    @Transactional(readOnly = true)
    public configurationUpdateLogs getLastConfigUpdateLog(Integer configId) {
        
       CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationUpdateLogs> criteria = builder.createQuery(configurationUpdateLogs.class);
        Root<configurationUpdateLogs> root = criteria.from(configurationUpdateLogs.class);

        Predicate whereClause = builder.equal(root.get("configId"), configId);

        criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        
        List<configurationUpdateLogs> logs = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        
        if (logs.isEmpty()) {
            return null;
        } else {
	   configurationUpdateLogs lastLog = (configurationUpdateLogs) logs.get(0);
	    return lastLog;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<configurationUpdateLogs> getConfigurationUpdateLogs(Integer configId) throws Exception {
	
	String sql = "select a.id, a.configId, a.userId, a.dateCreated, a.updateMade, concat(b.firstName, ' ',b.lastName) as usersName " 
        + "from configurationupdatelogs a "
        + "inner join users b on b.id = a.userId "
        + "where a.configId = :configId "
        + "order by a.dateCreated desc";
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,configurationUpdateLogs.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("configId", StandardBasicTypes.INTEGER)
        .addScalar("userId", StandardBasicTypes.INTEGER)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)
        .addScalar("updateMade", StandardBasicTypes.STRING)
        .addScalar("usersName", StandardBasicTypes.STRING);
	
	query.setParameter("configId", configId);
        
        List<Object[]> results = query.getResultList();
        
        List<configurationUpdateLogs> configLogs = new ArrayList<>();
        
        results.stream().forEach((record) -> {
            configurationUpdateLogs configLog = new configurationUpdateLogs();
            configLog.setId((Integer) record[1]);
            configLog.setConfigId((Integer) record[2]);
            configLog.setUserId((Integer) record[3]);
            configLog.setDateCreated((Date) record[4]);
            configLog.setUpdateMade((String) record[5]);
            configLog.setUsersName((String) record[6]);
            configLogs.add(configLog);
        });
	
	return configLogs;
    }
    
    @Override
    @Transactional(readOnly = true)
    public configurationUpdateLogs getConfigurationUpdateLog(Integer noteId) throws Exception {
	
	String sql = "select * from configurationUpdateLogs where id = :noteId";
	
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sql,configurationUpdateLogs.class)
        .setParameter("noteId", noteId);
	
	if(!query.list().isEmpty()) {
	    configurationUpdateLogs configurationLog = (configurationUpdateLogs) query.list().get(0);
	    return configurationLog;
	}
	else {
	    return null;
	}
    }
    
    @Override
    @Transactional(readOnly = false)
    public void updateConfigurationUpdateLog(configurationUpdateLogs updateLog) {
	
	String sql = "update configurationUpdateLogs set updateMade = :note where id = :noteId";
	Query updateConfigurationNote = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	updateConfigurationNote.setParameter("noteId",updateLog.getId());
	updateConfigurationNote.setParameter("note",updateLog.getUpdateMade());
	updateConfigurationNote.executeUpdate();
    }
    
    @Override
    @Transactional(readOnly = false)
    public void deletConfigurationNote(Integer noteId) throws Exception {
	
	String sql = "delete from configurationUpdateLogs where id = :noteId";
	
	Query deleteConfigurationNote = sessionFactory.getCurrentSession().createNativeQuery(sql, String.class);
	deleteConfigurationNote.setParameter("noteId",noteId);
	
        deleteConfigurationNote.executeUpdate();
    }
    
    /**
     * The 'deleteConfigurationFTPInformation' function will remove the authorized receivers for the passed in connectionId.
     *
     * @param transportId
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteConfigurationFTPInformation(int transportId) {
        Query query = sessionFactory.getCurrentSession().createNativeQuery("DELETE from rel_transportftpdetails where transportId = :transportId", String.class)
	.setParameter("transportId", transportId);

        query.executeUpdate();
    }
    
    @Override
    @Transactional(readOnly = true)
    public configurationDataTranslations getDataTranslationById(Integer translationId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<configurationDataTranslations> criteria = builder.createQuery(configurationDataTranslations.class);
        Root<configurationDataTranslations> root = criteria.from(configurationDataTranslations.class);

        Predicate whereClause = builder.equal(root.get("id"), translationId);

        criteria.where(whereClause);
        
        List<configurationDataTranslations> dataTranslations = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();

        if (!dataTranslations.isEmpty()) {
            return (configurationDataTranslations) dataTranslations.get(0);
        } else {
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = false)
    public void executeSQLStatement(String sqlStatement) throws Exception {
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class);
        query.executeUpdate();
    }

/**
     * The 'getAllConnectionsSingleQuery' function will return the list of utConfiguration connections in the system.
     *
     * @Table	configurationConnections
     *
     *
     * @return	This function will return a list of configurationConnection objects
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<configurationConnection> getAllConnectionsSingleQuery() {
        
        String sqlStatement = "select a.id, a.sourceConfigId, a.targetConfigId, a.status, a.dateCreated, b.configName as sourceConfigName, b.type as sourceConfigType, "
        + "c.configName as targetConfigName, c.type as targetConfigType,"
        + "(select orgName from organizations o left outer join registries.registries r on r.id = o.helRegistryId where o.id = (select orgId from configurations where id = a.sourceConfigId)) as sourceOrgName,"
        + "(select r.registryName as srcSystem from organizations o left outer join registries.registries r on r.id = o.helRegistryId where o.id = (select orgId from configurations where id = a.sourceConfigId)) as srcSystem,"
        + "(select orgName from organizations o left outer join registries.registries r on r.id = o.helRegistryId where o.id = (select orgId from configurations where id = a.targetConfigId)) as targetOrgName,"
        + "(select r.registryName as tgtSystem from organizations o left outer join registries.registries r on r.id = o.helRegistryId where o.id = (select orgId from configurations where id = a.targetConfigId)) as tgtSystem,"
        + "(select transportMethod from ref_transportmethods where id = (select transportMethodId from configurationtransportdetails where configId = a.sourceConfigId)) as sourceTransportMethod,"
        + "(select transportMethod from ref_transportmethods where id = (select transportMethodId from configurationtransportdetails where configId = a.targetConfigId)) as targetTransportMethod "
        + "from configurationconnections a inner join "
        + "configurations b on b.id = a.sourceConfigId inner join "
        + "configurations c on c.id = a.targetConfigId "
        + "where b.deleted = 0 and c.deleted = 0 "
        + "order by a.dateCreated desc";
        
        Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement,configurationConnection.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("sourceConfigId", StandardBasicTypes.INTEGER)        
        .addScalar("targetConfigId", StandardBasicTypes.INTEGER)               
        .addScalar("status", StandardBasicTypes.BOOLEAN)
        .addScalar("dateCreated", StandardBasicTypes.TIMESTAMP)        
        .addScalar("sourceConfigName", StandardBasicTypes.STRING)
        .addScalar("sourceConfigType", StandardBasicTypes.INTEGER)         
        .addScalar("targetConfigName", StandardBasicTypes.STRING)   
        .addScalar("targetConfigType", StandardBasicTypes.INTEGER)
        .addScalar("sourceOrgName", StandardBasicTypes.STRING)
        .addScalar("srcSystem", StandardBasicTypes.STRING)
        .addScalar("targetOrgName", StandardBasicTypes.STRING)
        .addScalar("tgtSystem", StandardBasicTypes.STRING)        
        .addScalar("sourceTransportMethod", StandardBasicTypes.STRING)
        .addScalar("targetTransportMethod", StandardBasicTypes.STRING);
                
        List<Object[]> results = query.getResultList();

        List<configurationConnection> connections = new ArrayList<>();

        results.stream().forEach((record) -> {
            configurationConnection connection = new configurationConnection();
            connection.setId((Integer) record[1]);
            connection.setSourceConfigId((Integer) record[2]);
            connection.setTargetConfigId((Integer) record[3]);
            connection.setStatus((boolean) record[4]);
            connection.setDateCreated((Date) record[5]);
            connection.setSourceConfigName((String) record[6]);
            connection.setSourceConfigType((Integer) record[7]);
            connection.setTargetConfigName((String) record[8]);
            connection.setTargetConfigType((Integer) record[9]);
            connection.setSourceOrgName((String) record[10]);
            connection.setSrcSystem((String) record[11]);
            connection.setTargetOrgName((String) record[12]);
            connection.setTgtSystem((String) record[13]);
            connection.setSourceTransportMethod((String) record[14]);
            connection.setTargetTransportMethod((String) record[15]);
            connections.add(connection);
        });

        return connections;
    }
    
    @Override
    @Transactional(readOnly = false)
    public List getCrosswalksForExport(String sqlStatement) throws Exception {
	
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class)
        .addScalar("id", StandardBasicTypes.INTEGER)
        .addScalar("name", StandardBasicTypes.STRING)
        .addScalar("fileDelimiter", StandardBasicTypes.STRING)
        .addScalar("fileName", StandardBasicTypes.STRING)
        .addScalar("orgId", StandardBasicTypes.INTEGER)
        .addScalar("sourceValue", StandardBasicTypes.STRING)
        .addScalar("targetValue", StandardBasicTypes.STRING)
        .addScalar("descValue", StandardBasicTypes.STRING);
        
        List<Object[]> dataTranslations = query.getResultList();
	
	return dataTranslations;
    }
    
    @Override
    @Transactional(readOnly = false)
    public List getDTForDownload(String sqlStatement) throws Exception {
        
	Query query = sessionFactory.getCurrentSession().createNativeQuery(sqlStatement, String.class)
        .addScalar("configName", StandardBasicTypes.STRING)        
        .addScalar("processOrder", StandardBasicTypes.INTEGER)
        .addScalar("fieldDesc", StandardBasicTypes.STRING)
        .addScalar("macroId", StandardBasicTypes.INTEGER)
        .addScalar("macroName", StandardBasicTypes.STRING)
        .addScalar("crosswalkId", StandardBasicTypes.INTEGER)
        .addScalar("crosswalkname", StandardBasicTypes.STRING)
        .addScalar("passClear", StandardBasicTypes.STRING)
        .addScalar("fieldA", StandardBasicTypes.STRING)
        .addScalar("fieldB", StandardBasicTypes.STRING)
        .addScalar("constant1", StandardBasicTypes.STRING)
        .addScalar("constant2", StandardBasicTypes.STRING)
        .addScalar("fieldNo", StandardBasicTypes.INTEGER);
        
        List<Object[]> dataTranslations = query.getResultList();
	
	return dataTranslations;
    }
}