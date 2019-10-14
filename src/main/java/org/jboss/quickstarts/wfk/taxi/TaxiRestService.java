/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.taxi;

import io.swagger.annotations.*;

import org.jboss.quickstarts.wfk.booking.TaxiBooking;
import org.jboss.quickstarts.wfk.booking.BookingService;
import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>This class produces a RESTful service exposing the functionality of {@link TaxiService}.</p>
 *
 * <p>The Path annotation defines this as a REST Web Service using JAX-RS.</p>
 *
 * <p>By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they
 * can be overriden by adding the Consumes or Produces annotations to the individual methods.</p>
 *
 * <p>It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow
 * transaction demarcation when accessing the database." - Antonio Goncalves</p>
 *
 * <p>The full path for accessing endpoints defined herein is: api/taxis/*</p>
 * 
 * @author Constance He
 * @see TaxiService
 * @see javax.ws.rs.core.Response
 */
@Path("/taxis")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/taxis", description = "Operations about taxis")
@Stateless
public class TaxiRestService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private TaxiService service;

    @Inject
    private BookingService service2;
    
    /**
     * <p>Return all the Taxis.  They are sorted alphabetically by name.</p>
     *
     * <p>Examples: <pre>GET "api/taxis" -H "accept: application/json"</pre>
     * </p>
     *
     * @return A Response containing a list of Taxis
     */
    @GET
    @ApiOperation(value = "Fetch all Taxis", notes = "Returns a JSON array of all stored Taxi objects.")
    public Response retrieveAllTaxis() {
        //Create an empty collection to contain the intersection of Taxis to be returned
        List<Taxi> taxis = service.findAllOrderedByRegistration();

        return Response.ok(taxis).build();
    }

    
    /**
     * <p>Search for and return a Taxi identified by id.</p>
     * 
     * <p>Example: <pre>GET "api/taxis/10003" -H "accept: application/json"</pre> </p>
     * 
     * @param id The long parameter value provided as a Taxi's id
     * @return A Response containing a single Taxi
     */
    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Taxi by id",
            notes = "Returns a JSON representation of the Taxi object with the provided id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Taxi found"),
            @ApiResponse(code = 404, message = "Taxi with id not found")
    })
    public Response retrieveTaxiById(
            @ApiParam(value = "Id of Taxi to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Taxi taxi = service.findById(id);
        if (taxi == null) {
            // Verify that the taxi exists. Return 404, if not present.
            throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Taxi = " + taxi.toString());

        return Response.ok(taxi).build();
    }

    /**
     * <p>Search for and return a Taxi identified by registration address.</p>
     *
     * <p>Path annotation includes very simple regex to differentiate between registration addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate registration addresses.</p>
     *
     * <p>Example: <pre>GET "api/taxis/QWE1234" -H "accept: application/json"</p></pre>
     *
     * @param registration The string parameter value provided as a Taxi's registration
     * @return A Response containing a single Taxi
     */
    @GET
    @Cache
   // @Path("/{registration:^[A-Za-z0-9]{7}$}")
    @Path("/{registration:[A-Za-z0-9]+}")
    @ApiOperation(
            value = "Fetch a Taxi by Registration",
            notes = "Returns a JSON representation of the Taxi object with the provided registration."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Taxi found"),
            @ApiResponse(code = 404, message = "Taxi with registration not found")
    })
    public Response retrieveTaxiByRegistration(
            @ApiParam(value = "Registration of Taxi to be fetched", required = true)
            @PathParam("registration")
            String registration) {

        Taxi taxi;
        try {
        	taxi = service.findByRegistration(registration);
        } catch (NoResultException e) {
            // Verify that the Taxi exists. Return 404, if not present.
            throw new RestServiceException("No Taxi with the registration " + registration + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findByRegistration " + registration + ": found Taxi = " + taxi.toString());
        return Response.ok(taxi).build();
    }
    
    
    /**
     * <p>Creates a new taxi from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * <p>Example: <pre>POST "api/taxis" -H "accept: application/json" -H "content-type: application/json" -d "{ \"id\": 10004, \"registration\": \"QWE5678\", \"seats\": 10}"</pre></p>
     * @param taxi The Taxi object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link TaxiService#create(Taxi)}
     * @return A Response indicating the outcome of the create operation
     */

    @POST
    @ApiOperation(value = "Add a new Taxi to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Taxi created successfully."),
            @ApiResponse(code = 400, message = "Invalid Taxi supplied in request body"),
            @ApiResponse(code = 409, message = "Taxi supplied in request body conflicts with an existing Taxi"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createTaxi(
            @ApiParam(value = "JSON representation of Taxi object to be added to the database", required = true)
            Taxi taxi) {


        if (taxi == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Go add the new Taxi.
            service.create(taxi);

            // Create a "Resource Created" 201 Response and pass the taxi back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(taxi);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createTaxi completed. Taxi = " + taxi.toString());
        return builder.build();
    }

    /**
     * <p>Updates the taxi with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * <p>Example: <pre>PUT "api/taxis/10003" -H "accept: application/json" -H "content-type: application/json" -d "{ \"id\": 10003, \"registration\": \"QWE8970\", \"seats\": 10}"</pre></p>
     * 
     * @param taxi The Taxi object, constructed automatically from JSON input, to be <i>updated</i> via
     * {@link TaxiService#update(Taxi)}
     * @param id The long parameter value provided as the id of the Taxi to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Update a Taxi in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Taxi updated successfully"),
            @ApiResponse(code = 400, message = "Invalid Taxi supplied in request body"),
            @ApiResponse(code = 404, message = "Taxi with id not found"),
            @ApiResponse(code = 409, message = "Taxi details supplied in request body conflict with another existing Taxi"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response updateTaxi(
            @ApiParam(value = "Id of Taxi to be updated", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id,
            @ApiParam(value = "JSON representation of Taxi object to be updated in the database", required = true)
            Taxi taxi) {

        if (taxi == null || taxi.getId() == null) {
            throw new RestServiceException("Invalid Taxi supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (taxi.getId() != null && taxi.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The Taxi ID in the request body must match that of the Taxi being updated");
            throw new RestServiceException("Taxi details supplied in request body conflict with another Taxi",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findById(taxi.getId()) == null) {
            // Verify that the taxi exists. Return 404, if not present.
            throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            // Apply the changes the Taxi.
            service.update(taxi);

            // Create an OK Response and pass the taxi back in case it is needed.
            builder = Response.ok(taxi);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("updateTaxi completed. Taxi = " + taxi.toString());
        return builder.build();
    }

    /**
     * <p>Deletes a taxi using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Taxi to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @ApiOperation(value = "Delete a Taxi from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The taxi has been successfully deleted"),
            @ApiResponse(code = 400, message = "Invalid Taxi id supplied"),
            @ApiResponse(code = 404, message = "Taxi with id not found"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response deleteTaxi(
            @ApiParam(value = "Id of Taxi to be deleted", allowableValues = "range[0, infinity]", required = true)
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Taxi taxi = service.findById(id);
        if (taxi == null) {
            // Verify that the taxi exists. Return 404, if not present.
            throw new RestServiceException("No Taxi with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        List<TaxiBooking> bookings = service2.findAllByTaxiId(taxi.getId());
        
        try {
            for(int i=0; i<bookings.size(); ++i) {
            	service2.delete(bookings.get(i));
            }
            
            service.delete(taxi);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteTaxi completed. Taxi = " + taxi.toString());
        return builder.build();
    }

}
