package org.jboss.quickstarts.wfk.booking;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB(Entity-Control-Boundary) pattern.</p>
 * 
 * <p>http://www.cs.sjsu.edu/~pearce/modules/patterns/enterprise/ecb/ecb.htm</p>
 * 
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 *
 * @author Constance He
 * @see BookingValidator
 * @see BookingRepository
 */
//The @Dependent is the default scope is listed here so that you know what scope is being used.
@Dependent
public class BookingService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private BookingValidator validator;

    @Inject
    private BookingRepository crud;

    /**
     * <p>Create a new client which will be used for our outgoing REST client communication</p>
     */
    public BookingService() {
        new ResteasyClientBuilder().build();
    }

    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Booking objects
     */
    List<TaxiBooking> findAllOrderedById() {
        return crud.findAllOrderedById();
    }

    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    TaxiBooking findById(Long id) {
        return crud.findById(id);
    }


    /**
     * <p>Returns a single Booking object, specified by a Long customerId.</p>
     *
     * <p>If there is more than one Booking with the specified customerId, only the first encountered will be returned.<p/>
     * 
     * @param customerId The customerId field of the Booking to be returned
     * @return The first Booking with the specified customerId
     */
    public List<TaxiBooking> findAllByCustomerId(Long customerId) {
        return crud.findAllByCustomerId(customerId);
    }
        
    /**
     * <p>Returns a single Booking object, specified by a Long taxiId.</p>
     *
     * <p>If there is more than one Booking with the specified taxiId, only the first encountered will be returned.<p/>
     * 
     * @param taxiId The taxiId field of the Booking to be returned
     * @return The first Booking with the specified taxiId
     */  
    public List<TaxiBooking> findAllByTaxiId(Long taxiId) {
        return crud.findAllByTaxiId(taxiId);
    }
    

    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     * <p>Validates the data in the provided Booking object using a {@link BookingValidator} object.<p/>
     *
     * @param booking The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public TaxiBooking create(TaxiBooking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingService.create() - Creating " + booking.getId());
        
        booking.setCustomer(crud.findCustomerById(booking.getCustomerId()));
        booking.setTaxi(crud.findTaxiById(booking.getTaxiId()));

        
        // Check to make sure the data fits with the parameters in the Booking model and passes validation.
        validator.validateBooking(booking);

        // Write the booking to the database.
        return crud.create(booking);
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there.<p/>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public TaxiBooking delete(TaxiBooking booking) throws Exception {
        log.info("delete() - Deleting " + booking.toString());

        TaxiBooking deletedBooking = null;

        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("delete() - No Id was found so can't Delete.");
        }

        return deletedBooking;
    }
}
