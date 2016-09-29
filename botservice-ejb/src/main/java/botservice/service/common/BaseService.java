package botservice.service.common;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для обслуживания запросов
 */

public class BaseService {

    @Inject
    protected EntityManager em;

    public <T> T mergeEntity(T entity){
        return em.merge(entity);
    }

    public <T> void removeEntity(T entity){
        entity = mergeEntity(entity);
        em.remove(entity);
    }

    public <T> void removeAllEntities(Class<T> entityClass){
        List<T> entityList = getEntityList(entityClass);
        for (T entity: entityList)
            removeEntity(entity);
    }

    public <T> List<T> getEntityList(Class<T> entityClass){
        return getEntityList(entityClass, 0);
    }

    public <T> List<T> getEntityList(Class<T> entityClass, int maxResult){
        return getEntityList(entityClass, maxResult, null, null);
    }

    public <T> List<T> getEntityList(Class<T> entityClass, int maxResult,
                                     SingularAttribute[] orderByArrAsc, SingularAttribute[] orderByArrDesc){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(entityClass);
        Root<T> root = criteria.from(entityClass);
        criteria.select(root);
        if (orderByArrAsc != null){
            for (SingularAttribute attr: orderByArrAsc)
                criteria.orderBy(cb.asc(root.get(attr.getName())));
        }
        if (orderByArrDesc != null){
            for (SingularAttribute attr: orderByArrDesc)
                criteria.orderBy(cb.desc(root.get(attr.getName())));
        }
        TypedQuery<T> typedQuery = em.createQuery(criteria);
        if (maxResult > 0)
            typedQuery.setMaxResults(maxResult);
        return typedQuery.getResultList();
    }

    private <T> TypedQuery<T> getQueryByCriteria(Class<T> entityClass, BaseParam ... params){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(entityClass);
        Root<T> entityRoot = criteriaQuery.from(entityClass);
        criteriaQuery.select(entityRoot);
        List<Predicate> predicateList = new ArrayList<>();
        for (BaseParam baseParam: params)
          predicateList.add(cb.equal(entityRoot.get(baseParam.getAttribute().getName()), baseParam.getValue()));
        criteriaQuery.where(cb.and(predicateList.toArray(new Predicate[predicateList.size()])));
        return em.createQuery(criteriaQuery);
    }

    public <T> List<T> getEntityListByCriteria(Class<T> entityClass, BaseParam ... params){
        return getQueryByCriteria(entityClass, params).getResultList();
    }

    public <T> T getEntityByCriteria(Class<T> entityClass, BaseParam ... params){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(entityClass);
        Root<T> entityRoot = criteriaQuery.from(entityClass);
        criteriaQuery.select(entityRoot);
        for (BaseParam baseParam: params)
            criteriaQuery.where(cb.equal(entityRoot.get(baseParam.getAttribute().getName()), baseParam.getValue()));
        return getQueryByCriteria(entityClass, params).getSingleResult();
    }

}
