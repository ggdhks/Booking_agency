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
import org.jboss.quickstarts.wfk.booking.TaxiBooking;
import org.jboss.quickstarts.wfk.booking.BookingRestService;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxi.TaxiRestService;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRestService;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BookingRegistrationTest {
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
	
	@Inject EntityManager em;

    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

	Customer user = createUser("keshan", "kk@ncl.com", "01111111111");
	Taxi taxi = createTaxi("wefewgy", 5);
    
    @Test
    @InSequence(1)
    public void testSuccesfulBookingCreation(){
    	customerRestService.createCustomer(user);
    	taxiRestService.createTaxi(taxi);
    	TaxiBooking booking = new TaxiBooking();
    	Customer customer = new Customer();
    	customer.setId(new Long(1));
    	Taxi taxi = new Taxi();
    	taxi.setId(new Long(2));
    	booking.setCustomer(customer);
    	booking.setTaxi(taxi);
    	booking.setDate(date);
    	assertEquals(201, bookingRestService.createBooking(booking).getStatus());
    }
    
    @Test
    @InSequence(2)
    public void testInvalidTaxiBookingCreation(){
    	TaxiBooking booking = new TaxiBooking();
    	Customer customer = new Customer();
    	customer.setId(new Long(1));
    	Taxi taxi = new Taxi();
    	taxi.setId(new Long(3));
    	booking.setCustomer(customer);
    	booking.setTaxi(taxi);
      	booking.setDate(date);
    	try{
    		bookingRestService.createBooking(booking);
    		fail("should return 400");
    	}catch(RestServiceException e){
    		assertEquals(400, e.getStatus().getStatusCode());
    	}
    }
    
    @Test
    @InSequence(3)
    public void testInvalidUserBookingCreation(){
    	TaxiBooking booking = new TaxiBooking();
    	Customer customer = new Customer();
    	customer.setId(new Long(2));
    	Taxi taxi = new Taxi();
    	taxi.setId(new Long(2));
    	booking.setCustomer(customer);
    	booking.setTaxi(taxi);
    	booking.setDate(date);
    	try{
    		bookingRestService.createBooking(booking);
    		fail("should return 400");
    	}catch(RestServiceException e){
    		assertEquals(400, e.getStatus().getStatusCode());
    	}
    }
    

    
    @Test
    @InSequence(8)
    public void testGetAllBookings(){
    	bookingRestService.retrieveAllBookings().getStatus();
    }
    
    @Test
    @InSequence(9)
    public void testUniqueConstraint(){
    	customerRestService.createCustomer(user);
    	taxiRestService.createTaxi(taxi);
    	TaxiBooking booking = new TaxiBooking();
    	Customer customer = new Customer();
    	customer.setId(new Long(1));
    	Taxi taxi = new Taxi();
    	taxi.setId(new Long(2));
    	booking.setCustomer(customer);
    	booking.setTaxi(taxi);
    	booking.setDate(date);
    	try{
    		bookingRestService.createBooking(booking).getStatus();
    	}catch(RestServiceException e){
    		assertEquals(409, e.getStatus().getStatusCode());
    	}
    }
    
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
