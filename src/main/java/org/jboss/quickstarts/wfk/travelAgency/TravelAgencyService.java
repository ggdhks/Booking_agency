package org.jboss.quickstarts.wfk.travelAgency;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;


import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.booking.InvalidCredentialsException;
import org.jboss.quickstarts.wfk.booking.TaxiBooking;
import org.jboss.quickstarts.wfk.customer.Customer;


import org.jboss.quickstarts.wfk.hotel.HotelBooking;
import org.jboss.quickstarts.wfk.hotel.HotelBookingService;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.flight.FlightBooking;
import org.jboss.quickstarts.wfk.flight.FlightBookingService;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

@Dependent
public class TravelAgencyService {
	@Inject
	private TravelAgencyRepository crud;
	
	private ResteasyClient client;
	
	private final Long agentIdTaxi = new Long("10001");
	private final Long agentIdHotel = new Long("10002");
	private final Long agentIdFlight = new Long("10003");
	
	@Inject
    private @Named("logger") Logger log;
	
	@Inject
	private BookingService taxiBookingService;
		
	public HotelBooking makeHotelBooking(TravelAgencyBooking booking) throws Exception{
		
		client = new ResteasyClientBuilder().build();
		
		Long hotelId = booking.getHotelId();
		
		HotelBooking hotelBooking = new HotelBooking();
		
		hotelBooking.setHotelId(hotelId);
		hotelBooking.setCustomerId(agentIdHotel);;
		hotelBooking.setDate(booking.getTime());
		log.info("booking: " + hotelBooking.toString());
		
		ResteasyWebTarget hotelTarget = client.target("http://api-deployment-csc8104-170152145.b9ad.pro-us-east-1.openshiftapps.com/");
		
		HotelBookingService service = hotelTarget.proxy(HotelBookingService.class);
		
		try{
			Response response = service.makeBooking(hotelBooking);
			
			log.info("code: " + response.getStatus());
			
			if(response.getStatus() == 400){
				throw new InvalidCredentialsException("Invalid input provided to the hotel booking");
			}
			
			if(response.getStatus() == 409){
				throw new InvalidCredentialsException("duplicate hotel booking provided");
			}
			
			if(response.getStatus() != 201){
				throw new Exception("Unkown response code: " + response.getStatus());
			}
			
			HotelBooking returnedBooking = response.readEntity(HotelBooking.class);
			
			client.close();
			
			return returnedBooking;
		}catch(ClientErrorException e){
			return null;
		}
	}
	
	public FlightBooking makeFlightBooking(TravelAgencyBooking booking) throws Exception{
		
		client = new ResteasyClientBuilder().build();
		
		Long flightId = booking.getFlightId();
		
		FlightBooking flightBooking = new FlightBooking();
		
		flightBooking.setFlightId(flightId);
		flightBooking.setCustomerId(agentIdFlight);
		flightBooking.setDate(booking.getTime());
		log.info("booking: " + flightBooking.toString());
		
		ResteasyWebTarget flightTarget = client.target("http://api-deployment-csc8104-140230305.b9ad.pro-us-east-1.openshiftapps.com/");
		
		FlightBookingService service = flightTarget.proxy(FlightBookingService.class);
		
		try{
			Response response = service.makeBooking(flightBooking);
			
			log.info("code: " + response.getStatus());
			
			if(response.getStatus() == 400){
				throw new InvalidCredentialsException("Invalid input provided to the flight booking");
			}
			
			if(response.getStatus() == 409){
				throw new InvalidCredentialsException("duplicate flight booking provided");
			}
			
			if(response.getStatus() != 201){
				throw new Exception("Unkown response code: " + response.getStatus());
			}
			
			FlightBooking returnedBooking = response.readEntity(FlightBooking.class);
			
			client.close();
			
			return returnedBooking;
		}catch(ClientErrorException e){
			return null;
		}
	}

	public TaxiBooking makeTaxiBooking(TravelAgencyBooking booking) throws ConstraintViolationException, ValidationException, Exception{
		TaxiBooking taxiBooking = new TaxiBooking();
		
		Customer customer = new Customer();
		customer.setId(agentIdFlight);
		
		Taxi taxi = new Taxi();
		taxi.setId(booking.getTaxiId());
		
		taxiBooking.setCustomerId(agentIdFlight);;
		taxiBooking.setTaxiId(agentIdTaxi);
		taxiBooking.setDate(booking.getTime());;
		
		taxiBookingService.create(taxiBooking);
		return taxiBooking;
	}
	
	public TravelAgencyBooking getFlightBooking(TravelAgencyBooking tab){
		return crud.findById(tab.getId());
	}
	
	public List<TravelAgencyBooking> getAllBookings(){
		return crud.findAllBookings();
	}
	
	public TravelAgencyBooking storeTABooking(TravelAgencyBooking booking){
		return crud.createBooking(booking);
	}
	
	public HotelBooking rollBackHotel(Long id) throws Exception, InvalidCredentialsException{
		client = new ResteasyClientBuilder().build();

		ResteasyWebTarget hotelTarget = client.target("http://api-deployment-csc8104-170152145.b9ad.pro-us-east-1.openshiftapps.com/");
		
		HotelBookingService service = hotelTarget.proxy(HotelBookingService.class);
		
		try{
			Response response = service.deleteBooking(id);
			
			log.info("code: " + response.getStatus());
			
			if(response.getStatus() == 400){
				throw new InvalidCredentialsException("Invalid booking id supplied");
			}
			
			if(response.getStatus() == 409){
				throw new InvalidCredentialsException("Booking with that id not supplied");
			}
			
			if(response.getStatus() != 204){
				throw new Exception("Unkown response code: " + response.getStatus());
			}
			
			HotelBooking returnedBooking = response.readEntity(HotelBooking.class);
			
			client.close();
			
			return returnedBooking;
		}catch(ClientErrorException e){
			return null;
		}
	}
	
	public FlightBooking rollBackFlight(Long id) throws Exception{
		client = new ResteasyClientBuilder().build();

		ResteasyWebTarget flightTarget = client.target("http://api-deployment-csc8104-130277853.7e14.starter-us-west-2.openshiftapps.com/api");
		
		FlightBookingService service = flightTarget.proxy(FlightBookingService.class);
		
		try{
			Response response = service.deleteBooking(id);
			
			log.info("code: " + response.getStatus());
			
			if(response.getStatus() == 400){
				throw new InvalidCredentialsException("Invalid booking id supplied");
			}
			
			if(response.getStatus() == 409){
				throw new InvalidCredentialsException("Booking with that id not supplied");
			}
			
			if(response.getStatus() != 204){
				throw new Exception("Unkown response code: " + response.getStatus());
			}
			
			FlightBooking returnedBooking = response.readEntity(FlightBooking.class);
			
			client.close();
			
			return returnedBooking;
		}catch(ClientErrorException e){
			return null;
		}
	}
	
	public TravelAgencyBooking getBookingById(long id){
		return crud.findById(id);
	}
	
	
	public void rollBackTaxi(Long id) throws Exception{
		TaxiBooking booking = new TaxiBooking();
		booking.setId(id);
		taxiBookingService.delete(booking);
	}

	public void deleteTABooking(TravelAgencyBooking booking){
		crud.deleteBooking(booking);
	}
}