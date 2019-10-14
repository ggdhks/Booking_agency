package org.jboss.quickstarts.wfk.contact;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.booking.BookingRestService;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRestService;

import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxi.TaxiRestService;
import org.jboss.quickstarts.wfk.travelAgency.TravelAgencyRestService;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TravelAgencyRegistration {
	@Deployment
    public static Archive<?> createTestArchive() {
        // This is currently not well tested. If you run into issues, comment line 67 (the contents of 'resolve') and
        // uncomment 65. This will build our war with all dependencies instead.
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
//                .importRuntimeAndTestDependencies()
                .resolve(
                        "io.swagger:swagger-jaxrs:1.5.15"
        ).withTransitivity().asFile();

        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.quickstarts.wfk")
                .addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Inject
    TaxiRestService taxiRestService;
	
	@Inject
	BookingRestService bookingRestService;
	
	@Inject
	CustomerRestService customerRestService;
	
	@Inject
	TravelAgencyRestService TravelAgencyBookingRestService;
	
	@Inject EntityManager em;

    @Inject
    @Named("logger") Logger log;


    private Date date = new Date(1484179200000L);
    
	Customer user = createUser("keshan", "kk@ncl.com", "01111111111");
	Taxi taxi = createTaxi("wefewgy", 5);
    
	/*
	@Test
	@InSequence(1)
	public void validCreationTest(){
		taxiRestService.createTaxi(taxi);
		customerRestService.createCustomer(user);
		TravelAgencyBooking travelAgencyBooking = new TravelAgencyBooking();
		user.setId(new Long(2));
		travelAgencyBooking.setCustomer(user);
		taxi.setId(new Long(1));
		travelAgencyBooking.settaxiId(taxi.getId());
		travelAgencyBooking.setHotelId(new Long(10001));
		travelAgencyBooking.setTaxiId(new Long(20001));
		travelAgencyBooking.setTime(date);
		assertEquals(201, TravelAgencyBookingRestService.createTravelAgencyBooking(TravelAgencyBooking).getStatus());
	}
	
	
	@Test
	@InSequence(2)
	public void testInvalidTaxi(){
		TravelAgencyBooking TravelAgencyBooking = new TravelAgencyBooking();
		TravelAgencyBooking.setCustomer(user);
		TravelAgencyBooking.settaxiId(taxi.getId());
		TravelAgencyBooking.setHotelId(new Long(10001));
		TravelAgencyBooking.setTaxiId(new Long(1));
		TravelAgencyBooking.setTime(date);
		assertEquals(400, TravelAgencyBookingRestService.createTravelAgencyBooking(TravelAgencyBooking).getStatus());
	}
	
	@Test
	@InSequence(3)
	public void testInvalidHotel(){
		TravelAgencyBooking TravelAgencyBooking = new TravelAgencyBooking();
		TravelAgencyBooking.setCustomer(user);
		TravelAgencyBooking.settaxiId(taxi.getId());
		TravelAgencyBooking.setHotelId(new Long(11));
		TravelAgencyBooking.setTaxiId(new Long(20001));
		TravelAgencyBooking.setTime(date);
		assertEquals(400, TravelAgencyBookingRestService.createTravelAgencyBooking(TravelAgencyBooking).getStatus());
	}
	
	@Test
	@InSequence(4)
	public void testInvalidtaxi(){
		TravelAgencyBooking TravelAgencyBooking = new TravelAgencyBooking();
		TravelAgencyBooking.setCustomer(user);
		TravelAgencyBooking.settaxiId(new Long(13213));
		TravelAgencyBooking.setHotelId(new Long(10001));
		TravelAgencyBooking.setTaxiId(new Long(20001));
		TravelAgencyBooking.setTime(date);
		assertEquals(400, TravelAgencyBookingRestService.createTravelAgencyBooking(TravelAgencyBooking).getStatus());
	}
	*/
    public Taxi createTaxi(String registration, int seats){
    	Taxi taxi = new Taxi();
    	taxi.setRegistration(registration);
    	taxi.setSeats(seats);
    	return taxi;
    }
    
    public Customer createUser(String name, String email, String phoneNumber){
    	Customer user = new Customer();
    	user.setName(name);
    	user.setEmail(email);
    	user.setPhoneNumber(phoneNumber);
    	return user;
    }
}
