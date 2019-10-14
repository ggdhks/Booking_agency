package org.jboss.quickstarts.wfk.taxi;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Taxi.class)
public abstract class Taxi_ {

	public static volatile SingularAttribute<Taxi, String> registration;
	public static volatile SingularAttribute<Taxi, Long> id;
	public static volatile SingularAttribute<Taxi, Integer> seats;

}

