package org.jboss.quickstarts.wfk.taxi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.taxi.Taxi;

import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link TaxiService} with the
 * Domain/Entity Object (see {@link Taxi}).</p>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.</p>
 *
 * @author Constance He
 * @see Taxi
 * @see javax.persistence.EntityManager
 */
public class TaxiRepository {

    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private EntityManager em;
    
    // @Inject
    // private BookingService service;

    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sorted alphabetically by registration.</p>
     *
     * @return List of Taxi objects
     */
    List<Taxi> findAllOrderedByRegistration() {
        TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_ALL, Taxi.class);
        return query.getResultList();
    }
    
    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sorted alphabetically by seats.</p>
     *
     * @return List of Taxi objects
     
    List<Taxi> findAllOrderedBySeats() {
        TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_BY_SEATS, Taxi.class);
        return query.getResultList();
    }
    */
    
    /**
     * <p>Returns a single Taxi object, specified by a Long id.<p/>
     *
     * @param id The id field of the Taxi to be returned
     * @return The Taxi with the specified id
     */
    public Taxi findById(Long id) {
        return em.find(Taxi.class, id);
    }

    /**
     * <p>Returns a single Taxi object, specified by a String registration.<p/>
     *
     * @param registration The registration field of the Taxi to be returned
     * @return The Taxi with the specified registration
     */
    Taxi findByRegistration(String registration) {
    	TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_BY_REGISTRATION, Taxi.class).setParameter("registration", registration);
        return query.getSingleResult();
    }
    
    /**
     * <p>Persists the provided Taxi object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param taxi The Taxi object to be persisted
     * @return The Taxi object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Taxi create(Taxi taxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("TaxiRepository.create() - Creating " + taxi.getRegistration());

        // Write the taxi to the database.
        em.persist(taxi);

        return taxi;
    }

    /**
     * <p>Updates an existing Taxi object in the application database with the provided Taxi object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param taxi The Taxi object to be merged with an existing Taxi
     * @return The Taxi that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Taxi update(Taxi taxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("TaxiRepository.update() - Updating " + taxi.getRegistration());

        // Either update the taxi or add it if it can't be found.
        em.merge(taxi);

        return taxi;
    }
    
    /**
     * <p>Deletes the provided Taxi object from the application database if found there</p>
     *
     * @param taxi The Taxi object to be removed from the application database
     * @return The Taxi object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Taxi delete(Taxi taxi) throws Exception {
        log.info("TaxiRepository.delete() - Deleting " + taxi.getRegistration());
        
        // List<Booking> bookings = service.findAllByTaxiId(taxi.getId());
        
        
        if (taxi.getId() != null) {
            /*
             * The Hibernate session (aka EntityManager's persistent context) is closed and invalidated after the commit(), 
             * because it is bound to a transaction. The object goes into a detached status. If you open a new persistent 
             * context, the object isn't known as in a persistent state in this new context, so you have to merge it. 
             * 
             * Merge sees that the object has a primary key (id), so it knows it is not new and must hit the database 
             * to reattach it. 
             * 
             * Note, there is NO remove method which would just take a primary key (id) and a entity class as argument. 
             * You first need an object in a persistent state to be able to delete it.
             * 
             * Therefore we merge first and then we can remove it.
             */
            em.remove(em.merge(taxi));
           /* for(int i=0; i<bookings.size(); ++i) {
           
            	service.delete(bookings.get(i));
            	
            }*/
            
        } else {
            log.info("TaxiRepository.delete() - No ID was found so can't Delete.");
        }
        
        return taxi;
    }
}
