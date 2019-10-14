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
package org.jboss.quickstarts.wfk.contact;

import static org.junit.Assert.assertEquals;


import java.io.File;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.contact.Contact;

import org.jboss.quickstarts.wfk.contact.ContactRestService;

import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.quickstarts.wfk.taxi.TaxiRepository;
import org.jboss.quickstarts.wfk.taxi.TaxiRestService;
import org.jboss.quickstarts.wfk.taxi.TaxiService;
import org.jboss.quickstarts.wfk.taxi.TaxiValidator;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Contact creation functionality
 * (see {@link ContactRestService#createContact(Contact) createContact(Contact)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see ContactRestService
 */
@RunWith(Arquillian.class)
public class TaxiRegistrationTest {

    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        //HttpComponents and org.JSON are required by ContactService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Taxi.class, 
                        TaxiRestService.class, 
                        TaxiRepository.class, 
                        TaxiValidator.class, 
                        TaxiService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    TaxiRestService taxiRestService;
    
    @Inject
    @Named("logger") Logger log;


    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
//        Taxi taxi = createTaxiInstance("FSAF000",5);
        Response response = taxiRestService.createTaxi(null);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New taxi was persisted and returned status " + response.getStatus());
    }

/*    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Taxi taxi = createTaxiInstance("",0);
        Response response = taxiRestService.createTaxi(taxi);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateRegistration() throws Exception {
        // Register an initial user
        Taxi taxi = createTaxiInstance("FSAO000",5);
        taxiRestService.createTaxi(taxi);

        // Register a different user with the same email
        Taxi anotherTaxi = createTaxiInstance("FSAO000", 6);
        Response response = taxiRestService.createTaxi(anotherTaxi);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate taxi register attempt failed with return code " + response.getStatus());
    }

    @Test
    @InSequence(4)
    public void testDeleteTaxi() throws Exception {
        // Register an initial user
        Taxi taxi = createTaxiInstance("FSAJ000",5);
        taxiRestService.createTaxi(taxi);

        Response response = taxiRestService.deleteTaxi(taxi.getId());

        assertEquals("Unexpected response status", 400, response.getStatus());
        log.info("Delete contact register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(5)
    public void testRetrieveContact() throws Exception {
        Response response = taxiRestService.retrieveAllTaxis();

        assertEquals("Unexpected response status", 200, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        log.info("Retrieve contact register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(6)
    public void testRegistrationValidation() throws Exception {
        Taxi taxi = createTaxiInstance("gggggggggggggggggg",7);
        Response response = taxiRestService.createTaxi(taxi);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(7)
    public void testSeatsValidation() throws Exception {
        Taxi taxi = createTaxiInstance("DHFU654",999);
        Response response = taxiRestService.createTaxi(taxi);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testIdValidation() throws Exception {
        Taxi taxi = createTaxiInstanceWithID((long)88,"ASDE666",9);
        Response response = taxiRestService.createTaxi(taxi);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid contact register attempt failed with return code " + response.getStatus());
    }
    
    *//**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.taxi.Taxi Taxi} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param registration The registration of the Taxi being created
     * @param seats  The seats number of the Taxi being created
     * @return The Taxi object create
     *//*
    private Taxi createTaxiInstance(String registration, int seats) {
        Taxi taxi = new Taxi();
        taxi.setRegistration(registration);;
        taxi.setSeats(seats);;
        return taxi;
    }
    
    *//**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.taxi.Taxi Taxi} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param id The id of the Taxi being created
     * @param registration The registration of the Taxi being created
     * @param seats  The seats number of the Taxi being created
     * @return The Taxi object create
     *//*
    private Taxi createTaxiInstanceWithID(Long id,String registration, int seats) {
        Taxi taxi = new Taxi();
        taxi.setId(id);
        taxi.setRegistration(registration);;
        taxi.setSeats(seats);;
        return taxi;
    }*/
}
