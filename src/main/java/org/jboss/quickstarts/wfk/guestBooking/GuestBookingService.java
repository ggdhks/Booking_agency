package org.jboss.quickstarts.wfk.guestBooking;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.booking.BookingValidator;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRestService;
import org.jboss.quickstarts.wfk.customer.CustomerService;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxi.TaxiService;

public class GuestBookingService {
	
	@Inject
	CustomerService userService;
	@Inject
	BookingService bookingService;
	@Inject
	TaxiService taxiService;
	
	@Inject
	BookingValidator bookingValidator;
	@Inject
	CustomerRestService userRest;
	
	void createBooking(GuestBooking booking) throws ConstraintViolationException, ValidationException, Exception{
		Customer user = userService.create(booking.getCustomer());
	
		Taxi taxi = taxiService.findById(booking.getBooking().getTaxi().getId());
		
		booking.getBooking().setCustomer(user);
		
		booking.getBooking().setTaxi(taxi);
		
		bookingService.create(booking.getBooking());
	}
}
