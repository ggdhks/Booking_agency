package org.jboss.quickstarts.wfk.booking;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxi.TaxiRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 *
 * @author Constance He
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
public class BookingValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud1;
    
    @Inject
    private CustomerRepository crud2;
    
    @Inject
    private TaxiRepository crud3;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing booking with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If booking with the same email already exists
     */
    void validateBooking(TaxiBooking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<TaxiBooking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        
        // Check the uniqueness of the booking id ??? 
        if (bookingAlreadyExists(booking.getTaxiId(), booking.getDate(), booking.getId())) {
            throw new ValidationException("Booking Exists");
        }
        
        // Check whether the taxi is exist
        if (!taxiAlreadyExists(booking.getTaxiId())) {
            throw new ValidationException("TaxiNotExist");
        }
        
        // Check whether the customer is exist
        if (!customerAlreadyExists(booking.getCustomerId())) {
            throw new ValidationException("CustomerNotExist");
        }
    }

    /**
     * <p>Checks if a booking with the same combination of taxi and date is already registered.
     * 
     * <p>Since Update will being using an booking that is already in the database we need to make sure that it is the combination of taxi and date
     * from the record being updated.</p>
     * 
     * @param taxiId
     * @param date
     * @param id
     * @return
     *
     */
	private boolean bookingAlreadyExists(Long taxiId, Date date, Long id) {
		List<TaxiBooking> booking = null;
		TaxiBooking bookingWithId = null;
	        try {
	        	booking = crud1.findAllByTaxiId(taxiId);
	        } catch (NoResultException e) {
	            // ignore    
	        }

	        try {
	        	for(TaxiBooking b: booking){
	        		if (b.getDate().equals(date) && b.getId()!=id) {
	        			bookingWithId = b;
	                }
	            }
	        } catch (NoResultException e) {
	        // ignore
	        }
	    return bookingWithId != null;
	}
	
	/**
	 * <p>Check whether the customer exists with customerId</p>
	 * 
	 * @param customerId the id of the customer
	 * @return boolean whether the customer exists
	 */
	private boolean customerAlreadyExists(Long customerId) {
		Customer customer = null;
    	try {
    		customer = crud2.findById(customerId);
    	}catch (NoResultException e) {
            // ignore
        }
    	return customer != null;
	}

	/**
	 * <p>Check whether the taxi exists with taxiId</p>
	 * 
	 * @param taxiId the id of the taxi
	 * @return boolean whether the taxi exists
	 */
	private boolean taxiAlreadyExists(Long taxiId) {
		Taxi taxi = null;
    	try {
    		taxi = crud3.findById(taxiId);
    	}catch (NoResultException e) {
            // ignore
        }
    	return taxi != null;
	}
}
