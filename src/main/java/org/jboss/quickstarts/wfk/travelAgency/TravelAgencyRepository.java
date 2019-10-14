package org.jboss.quickstarts.wfk.travelAgency;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


public class TravelAgencyRepository {

	@Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    TravelAgencyBooking findById(Long id){
    	TypedQuery<TravelAgencyBooking> query = em.createNamedQuery(TravelAgencyBooking.FIND_BY_NUMBER, TravelAgencyBooking.class).setParameter("number", id);
    	return query.getSingleResult();
    }
    
    List<TravelAgencyBooking> findAllBookings(){
    	TypedQuery<TravelAgencyBooking> query = em.createNamedQuery(TravelAgencyBooking.FIND_ALL, TravelAgencyBooking.class);
    	return query.getResultList();
    }
    
    TravelAgencyBooking createBooking(TravelAgencyBooking tab){
    	em.persist(tab);
    	return tab;
    }
    
    TravelAgencyBooking deleteBooking(TravelAgencyBooking tab){
    	TravelAgencyBooking b = em.merge(tab);
    	em.remove(b);
    	return tab;
    }
	
}
