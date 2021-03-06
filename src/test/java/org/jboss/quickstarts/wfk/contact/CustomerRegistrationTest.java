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
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.quickstarts.wfk.customer.CustomerRestService;
import org.jboss.quickstarts.wfk.customer.CustomerService;
import org.jboss.quickstarts.wfk.customer.CustomerValidator;
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
 * Customer creation functionality
 * (see {@link CustomerRESTService#createCustomer(Customer) createCustomer(Customer)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see CustomerRESTService
 */
@RunWith(Arquillian.class)
public class CustomerRegistrationTest {

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
        //HttpComponents and org.JSON are required by CustomerService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Customer.class, 
                        CustomerRestService.class, 
                        CustomerRepository.class, 
                        CustomerValidator.class, 
                        CustomerService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    CustomerRestService customerRESTService;
    
    @Inject
    @Named("logger") Logger log;
    
    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Response response = customerRESTService.createCustomer(null);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New customer was persisted and returned status " + response.getStatus());
    }
    

/**
    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
        Customer customer = createCustomerInstance("Jack", "jack@mailinator.com", "06464646464");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New customer was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
        Customer customer = createCustomerInstance("", "", "");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 3,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicateEmail() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("Jane", "jane@mailinator.com", "06464646464");
        customerRESTService.createCustomer(customer);

        // Register a different user with the same email
        Customer anotherCustomer = createCustomerInstance("John", "jane@mailinator.com", "06464646464");
        Response response = customerRESTService.createCustomer(anotherCustomer);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate customer register attempt failed with return code " + response.getStatus());
    }

    @Test
    @InSequence(4)
    public void testDeleteCustomer() throws Exception {
        // Register an initial user
        Customer customer = createCustomerInstance("Jaa", "jaa@mailinator.com", "06464646464");
        customerRESTService.createCustomer(customer);

        Response response = customerRESTService.deleteCustomer(customer.getId());

        assertEquals("Unexpected response status", 400, response.getStatus());
        log.info("Delete customer register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(5)
    public void testRetrieveCustomer() throws Exception {
        Response response = customerRESTService.retrieveAllCustomers();

        assertEquals("Unexpected response status", 200, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        log.info("Retrieve customer register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(6)
    public void testNameValidation() throws Exception {
        Customer customer = createCustomerInstance("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", "hfghi@qq.com", "01111111111");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(7)
    public void testEmailValidation() throws Exception {
        Customer customer = createCustomerInstance("ffffff", "hfghi444qq.com", "01111111111");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testPhoneNumberValidation() throws Exception {
        Customer customer = createCustomerInstance("ffffff", "hfghigg@qq.com", "xxxxxx");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
/*    @SuppressWarnings("unchecked")
    @Test
    @InSequence(9)
    public void testChangeID() throws Exception {
        Customer customer = createCustomerInstance("ffffff", "hfghikk@qq.com", "09999999999");
        customerRESTService.createCustomer(customer);
        Customer customer2 = createCustomerInstance("fjjffff", "yyghi@qq.com", "07777777777");
        customerRESTService.createCustomer(customer2);

        Response response = customerRESTService.updateCustomer(4, customer2);
        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 2,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }*/
 /**   
    @SuppressWarnings("unchecked")
    @Test
    @InSequence(8)
    public void testIDValidation() throws Exception {
        Customer customer = createCustomerInstanceWithID((long)9,"ffffff", "hfghig33g@qq.com", "09888888888");
        Response response = customerRESTService.createCustomer(customer);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.customer.Customer Customer} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param name The name of the Customer being created
     * @param email     The email address of the Customer being created
     * @param phone     The phone number of the Customer being created
     * @return The Customer object create
     */
    /*
    private Customer createCustomerInstance(String name, String email, String phone) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phone);
        return customer;
    }
    */
}
