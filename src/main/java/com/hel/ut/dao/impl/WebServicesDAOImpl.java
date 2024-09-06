package com.hel.ut.dao.impl;

import java.util.Date;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.dao.WebServicesDAO;
import com.hel.ut.model.WSMessagesIn;
import com.hel.ut.model.wsMessagesOut;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * The WebServicesDAOImpl class will implement the DAO access layer to handle updates for web services messages
 *
 *
 * @author gchan
 *
 */
@Repository
public class WebServicesDAOImpl implements WebServicesDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<WSMessagesIn> getWSMessagesInList(Date fromDate, Date toDate, Integer fetchSize) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<WSMessagesIn> criteria = builder.createQuery(WSMessagesIn.class);
        Root<WSMessagesIn> root = criteria.from(WSMessagesIn.class);
        
        Predicate fromDateSearch = null;
        Predicate toDateSearch = null;
        Predicate whereClause = null;
        
        if (!"".equals(fromDate)) {
            fromDateSearch = builder.greaterThanOrEqualTo(root.get("dateCreated"), fromDate);
        }
        
        if (!"".equals(toDate)) {
            toDateSearch = builder.lessThan(root.get("dateCreated"), toDate);
        }
        
        if(fromDateSearch != null && toDateSearch != null) {
            whereClause = builder.and(fromDateSearch,toDateSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch != null && toDateSearch == null) {
            whereClause = fromDateSearch;
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch == null && toDateSearch != null) {
            whereClause = toDateSearch;
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        
        if (fetchSize > 0) {
            return sessionFactory.getCurrentSession().createQuery(criteria).setMaxResults(fetchSize).getResultList();
        }
        else {
            return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public WSMessagesIn getWSMessagesIn(Integer wsId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<WSMessagesIn> criteria = builder.createQuery(WSMessagesIn.class);
        Root<WSMessagesIn> root = criteria.from(WSMessagesIn.class);

        Predicate whereClause = builder.equal(root.get("id"), wsId);

        criteria.where(whereClause);
        
        List<WSMessagesIn> wsList = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        
        if (wsList.size() == 1) {
            return wsList.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveWSMessagesOut(wsMessagesOut wsMessagesOut) throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(wsMessagesOut);
    }

    /**
     * this method get a list of outbound web messages restricted by Date range
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<wsMessagesOut> getWSMessagesOutList(Date fromDate, Date toDate,Integer fetchSize) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<wsMessagesOut> criteria = builder.createQuery(wsMessagesOut.class);
        Root<wsMessagesOut> root = criteria.from(wsMessagesOut.class);
        
        Predicate fromDateSearch = null;
        Predicate toDateSearch = null;
        Predicate whereClause = null;
        
        if (!"".equals(fromDate)) {
            fromDateSearch = builder.greaterThanOrEqualTo(root.get("dateCreated"), fromDate);
        }
        
        if (!"".equals(toDate)) {
            toDateSearch = builder.lessThan(root.get("dateCreated"), toDate);
        }
        
        if(fromDateSearch != null && toDateSearch != null) {
            whereClause = builder.and(fromDateSearch,toDateSearch);
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch != null && toDateSearch == null) {
            whereClause = fromDateSearch;
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        else if(fromDateSearch == null && toDateSearch != null) {
            whereClause = toDateSearch;
            criteria.orderBy(builder.desc(root.get("dateCreated"))).where(whereClause);
        }
        
        if (fetchSize > 0) {
            return sessionFactory.getCurrentSession().createQuery(criteria).setMaxResults(fetchSize).getResultList();
        }
        else {
            return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public wsMessagesOut getWSMessagesOut(Integer wsId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<wsMessagesOut> criteria = builder.createQuery(wsMessagesOut.class);
        Root<wsMessagesOut> root = criteria.from(wsMessagesOut.class);

        Predicate whereClause = builder.equal(root.get("id"), wsId);

        criteria.where(whereClause);
        
        List<wsMessagesOut> wsList = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        
        if (wsList.size() == 1) {
            return wsList.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<wsMessagesOut> getWSMessagesOutByBatchId(Integer batchId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<wsMessagesOut> criteria = builder.createQuery(wsMessagesOut.class);
        Root<wsMessagesOut> root = criteria.from(wsMessagesOut.class);

        Predicate whereClause = builder.equal(root.get("batchDownloadId"), batchId);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<WSMessagesIn> getWSMessagesInByBatchId(Integer batchId) throws Exception {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<WSMessagesIn> criteria = builder.createQuery(WSMessagesIn.class);
        Root<WSMessagesIn> root = criteria.from(WSMessagesIn.class);

        Predicate whereClause = builder.equal(root.get("batchUploadId"), batchId);

        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveWSMessagesIn(WSMessagesIn wsIn) throws Exception {
        sessionFactory.getCurrentSession().saveOrUpdate(wsIn);
    }
}