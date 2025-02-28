package com.hel.ut.dao.impl;

import com.hel.ut.dao.hispDAO;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.hel.ut.model.hisps;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import org.hibernate.query.SelectionQuery;

/**
 * The userDAOImpl class will implement the DAO access layer to handle updates for organization system users
 *
 *
 * @author chadmccue
 *
 */
@Repository
public class hispDAOImpl implements hispDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * The 'getAllActiveHisps' function will return all active hisps;
     *
     *
     * @return The function will return a list of hisp objects
     */
    @Override
    @Transactional(readOnly = true)
    public List<hisps> getAllActiveHisps() {
        
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<hisps> criteria = builder.createQuery(hisps.class);
        Root<hisps> root = criteria.from(hisps.class);
        
        Predicate whereClause = builder.equal(root.get("status"), true);
        criteria.where(whereClause);
        
        return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
    }

    /**
     * The 'getHispById' function will return a single hisp object based on the hispId passed in.
     *
     * @param	hispId	This will be used to find the specifc hisp
     *
     * @return	The function will return a hisp object
     * @throws java.lang.Exception
     */
    @Override
    @Transactional(readOnly = true)
    public hisps getHispById(Integer hispId) throws Exception {
        return (hisps) sessionFactory.getCurrentSession().get(hisps.class, hispId);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveHisp(hisps hispDetails) throws Exception {
        if (Objects.isNull(sessionFactory.getCurrentSession().find(hisps.class, hispDetails.getId()))) {
            sessionFactory.getCurrentSession().persist(hispDetails);
        } else {
            sessionFactory.getCurrentSession().merge(hispDetails);
        }
    }
}
