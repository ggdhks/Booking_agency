package org.jboss.quickstarts.wfk.flight;

import java.io.Serializable;
import java.util.Date;

import org.jboss.quickstarts.wfk.travelAgency.Booking;

public class FlightBooking implements Serializable, Booking{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long customerId;
	
	private Long flightId;
	
	private Date date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getFightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
    public String toString() {
        return "flightBooking{" +
                "id=" + id +
                ", customerID='" + customerId + '\'' +
                ", flightID='" + flightId + '\'' +
                ", date='" + date.toString() + '\'' +
                '}';
    }
}
