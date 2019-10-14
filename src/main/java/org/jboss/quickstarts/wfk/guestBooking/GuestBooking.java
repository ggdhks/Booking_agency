package org.jboss.quickstarts.wfk.guestBooking;


import org.jboss.quickstarts.wfk.booking.TaxiBooking;
import org.jboss.quickstarts.wfk.customer.Customer;

import java.io.Serializable;


/**
 * <p>This is a the Domain object. The GuestBooking class represents how booking resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a bookings are retrieved from the database (with @NamedQueries), and acceptable values
 * for GuestBooking fields (with @NotNull, @Pattern etc...)</p>
 *
 * @author Constance He
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */

public class GuestBooking implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Default value included to remove warning. Remove or modify at will. **/
   
    private Customer customer;

    private TaxiBooking booking;
    
    public Customer getCustomer() {
    	return customer;
    }
  
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public TaxiBooking getBooking() {
    	return booking;
    }
    
    public void setBooking(TaxiBooking booking) {
        this.booking = booking;
    }
   
}
