package org.jboss.quickstarts.wfk.booking;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.taxi.Taxi;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TaxiBooking.class)
public abstract class TaxiBooking_ {

	public static volatile SingularAttribute<TaxiBooking, Date> date;
	public static volatile SingularAttribute<TaxiBooking, Taxi> taxi;
	public static volatile SingularAttribute<TaxiBooking, Long> id;
	public static volatile SingularAttribute<TaxiBooking, Customer> customer;

}

