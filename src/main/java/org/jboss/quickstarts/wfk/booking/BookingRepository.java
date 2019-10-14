package org.jboss.quickstarts.wfk.booking;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.taxi.Taxi;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingService} with the
 * Domain/Entity Object (see {@link Booking}).</p>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.</p>
 *
 * @author Constance He
 * @see Booking
 * @see javax.persistence.EntityManager
 */
public class BookingRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Booking objects
     */
    List<TaxiBooking> findAllOrderedById() {
        TypedQuery<TaxiBooking> query = em.createNamedQuery(TaxiBooking.FIND_ALL, TaxiBooking.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a List of Booking objects, specified by a Long taxiId.<p/>
     *     
     * @param taxiId The taxiId field of the Booking to be returned
     * @return List of Booking objects
     */
    List<TaxiBooking> findAllByTaxiId(Long taxiId) {
   	 TypedQuery<TaxiBooking> query = em.createNamedQuery(TaxiBooking.FIND_BY_TAXI, TaxiBooking.class).setParameter("taxiId", taxiId); 
        return query.getResultList();
   }
    
    /**
     * <p>Returns a List of Booking objects, specified by a Long customerId.<p/>
     *
     * @param customerId The customerId field of the Booking to be returned
     * @return List of Booking objects
     */
    public List<TaxiBooking> findAllByCustomerId(Long customerId) {
    	 TypedQuery<TaxiBooking> query = em.createNamedQuery(TaxiBooking.FIND_BY_CUSTOMER, TaxiBooking.class).setParameter("customerId", customerId); 
         return query.getResultList();
    }
    
    Customer findCustomerById(Long customerId) {
        return em.find(Customer.class, customerId);
    }
    
    Taxi findTaxiById(Long taxiId) {
        return em.find(Taxi.class, taxiId);
    }
 
    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    TaxiBooking findById(Long id) {
        return em.find(TaxiBooking.class, id);
    }
    
    /**
     * <p>Returns a single Booking object, specified by a Date date and taxiId.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param date The date field of the Booking to be returned
     * @param taxiId The taxiId field of the Booking to be returned
     * @return The Booking with the specified date and taxiId
     */
    TaxiBooking findByDateAndTaxi(Date date, Long taxiId) {
        TypedQuery<TaxiBooking> query = em.createNamedQuery(TaxiBooking. FIND_BY_DATE_AND_TAXI, TaxiBooking.class).setParameter("date", date).setParameter("taxiId", taxiId);
        return query.getSingleResult();
    }
    
    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    TaxiBooking create(TaxiBooking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.create() - Creating " + booking.getTaxi() + "-" + booking.getDate());

        // Write the booking to the database.
        em.persist(booking);

        return booking;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    TaxiBooking delete(TaxiBooking booking) throws Exception {
        log.info("BookingRepository.delete() - Deleting " + booking.getId());

        if (booking.getId() != null) {
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
            em.remove(em.merge(booking));

        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }

        return booking;
    }

}
