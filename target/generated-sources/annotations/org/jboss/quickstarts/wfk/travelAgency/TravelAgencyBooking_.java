package org.jboss.quickstarts.wfk.travelAgency;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.customer.Customer;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TravelAgencyBooking.class)
public abstract class TravelAgencyBooking_ {

	public static volatile SingularAttribute<TravelAgencyBooking, Long> flightBookingId;
	public static volatile SingularAttribute<TravelAgencyBooking, Long> taxiId;
	public static volatile SingularAttribute<TravelAgencyBooking, Long> taxiBookingId;
	public static volatile SingularAttribute<TravelAgencyBooking, Long> flightId;
	public static volatile SingularAttribute<TravelAgencyBooking, Long> id;
	public static volatile SingularAttribute<TravelAgencyBooking, Long> hotelId;
	public static volatile SingularAttribute<TravelAgencyBooking, Long> hotelBookingId;
	public static volatile SingularAttribute<TravelAgencyBooking, Date> time;
	public static volatile SingularAttribute<TravelAgencyBooking, Customer> customer;

}

