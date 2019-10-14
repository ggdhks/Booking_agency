package org.jboss.quickstarts.wfk.taxi;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Taxi objects against arbitrary requirements.</p>
 *
 * @author Constance He 
 * @see Taxi
 * @see TaxiRepository
 * @see javax.validation.Validator
 */
public class TaxiValidator {
    @Inject
    private Validator validator;

    @Inject
    private TaxiRepository crud;

    /**
     * <p>Validates the given Taxi object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing taxi with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param taxi The Taxi object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If taxi with the same email already exists
     */
    void validateTaxi(Taxi taxi) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Taxi>> violations = validator.validate(taxi);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
        
        // Check the uniqueness of the registration
        if (registrationAlreadyExists(taxi.getRegistration(), taxi.getId())) {
            throw new ValidationException("Unique registration Violation");
        }
    }
    
    /**
     * <p>Checks if a taxi with the same registration is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "registration")" constraint from the Taxi class.</p>
     *
     * <p>Since Update will being using a registration that is already in the database we need to make sure that it is the registration
     * from the record being updated.</p>
     *
     * @param registration The registration to check is unique
     * @param id The taxi id to check the registration against if it was found
     * @return boolean which represents whether the registration was found, and if so if it belongs to the taxi with id
     */
	boolean registrationAlreadyExists(String registration, Long id) {
		Taxi taxi = null;
		Taxi taxiWithID = null;
	    try {
	    	taxi = crud.findByRegistration(registration);
	    } catch (NoResultException e) {
	    	// ignore
	    }
	    
	    if (taxi != null && id != null) {
	    	try {
	    		taxiWithID = crud.findById(id);
	    		if (taxiWithID != null && taxiWithID.getRegistration().equals(registration)) {
	                taxi = null;
	            }
	        } catch (NoResultException e) {
	         // ignore
	        }
	    }
	    return taxi != null;
	 }
}


