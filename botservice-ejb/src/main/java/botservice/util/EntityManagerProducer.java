package botservice.util;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Entity Manager Producer
 */
@SuppressWarnings("unused")
public class EntityManagerProducer {

    @Produces
    @PersistenceContext
    @SuppressWarnings("unused")
    private EntityManager em;

}
