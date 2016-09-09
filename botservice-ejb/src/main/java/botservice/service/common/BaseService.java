package botservice.service.common;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Базовый класс для обслуживания запросов
 */

public class BaseService {

    @Inject
    private EntityManager em;

    public <T> T mergeEntity(T entity){
        return em.merge(entity);
    }

    public <T> void removeEntity(T entity){
        entity = mergeEntity(entity); // Праттачим сущность, заодно отработает оптимистическая блокировка
        em.remove(entity);
    }

    public <T> List<T> getEntityList(Class<T> entityClass){
        CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(entityClass);
        criteria.select(criteria.from(entityClass));
        return em.createQuery(criteria).getResultList();
    }

    public <T> T getEntityByCriteria(Class<T> entityClass, BaseParam ... params){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(entityClass);
        Root<T> entityRoot = criteriaQuery.from(entityClass);
        criteriaQuery.select(entityRoot);
        for (BaseParam baseParam: params)
            criteriaQuery.where(cb.equal(entityRoot.get(baseParam.getAttribute().getName()), baseParam.getValue()));
        return em.createQuery(criteriaQuery).getSingleResult();
    }

}
