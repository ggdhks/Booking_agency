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

import org.jboss.quickstarts.wfk.taxi.Taxi;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB(Entity-Control-Boundary) pattern.</p>
 * 
 * <p>http://www.cs.sjsu.edu/~pearce/modules/patterns/enterprise/ecb/ecb.htm</p>
 * 
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 *
 * @author Constance He
 * @see TaxiValidator
 * @see TaxiRepository
 */
//The @Dependent is the default scope is listed here so that you know what scope is being used.
@Dependent
public class TaxiService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private TaxiValidator validator;

    @Inject
    private TaxiRepository crud;

    /**
     * <p>Create a new client which will be used for our outgoing REST client communication</p>
     */
    public TaxiService() {
        new ResteasyClientBuilder().build();
    }

    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sorted alphabetically by registration.<p/>
     *
     * @return List of Taxi objects
     */
    public List<Taxi> findAllOrderedByRegistration() {
        return crud.findAllOrderedByRegistration();
    }
    
    /**
     * <p>Returns a single Taxi object, specified by a Long id.<p/>
     *
     * @param id The id field of the Taxi to be returned
     * @return The Taxi with the specified id
     */
    public Taxi findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Taxi object, specified by a String registration.</p>
     *
     * <p>If there is more than one Taxi with the specified registration, only the first encountered will be returned.<p/>
     * 
     * @param registration The registration field of the Taxi to be returned
     * @return The first Taxi with the specified registration
     */
    Taxi findByRegistration(String registration) {
        return crud.findByRegistration(registration);
    }
    
    /**
     * <p>Writes the provided Taxi object to the application database.<p/>
     *
     * <p>Validates the data in the provided Taxi object using a {@link TaxiValidator} object.<p/>
     *
     * @param taxi The Taxi object to be written to the database using a {@link TaxiRepository} object
     * @return The Taxi object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Taxi create(Taxi taxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("TaxiService.create() - Creating " + taxi.getRegistration());
        
        // Check to make sure the data fits with the parameters in the Taxi model and passes validation.
        validator.validateTaxi(taxi);

        // Write the taxi to the database.
        return crud.create(taxi);
    }

    /**
     * <p>Updates an existing Taxi object in the application database with the provided Taxi object.<p/>
     *
     * <p>Validates the data in the provided Taxi object using a TaxiValidator object.<p/>
     *
     * @param taxi The Taxi object to be passed as an update to the application database
     * @return The Taxi object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Taxi update(Taxi taxi) throws ConstraintViolationException, ValidationException, Exception {
        log.info("TaxiService.update() - Updating " + taxi.getRegistration());
        
        // Check to make sure the data fits with the parameters in the Taxi model and passes validation.
        validator.validateTaxi(taxi);

        // Either update the taxi or add it if it can't be found.
        return crud.update(taxi);
    }
    
    /**
     * <p>Deletes the provided Taxi object from the application database if found there.<p/>
     *
     * @param taxi The Taxi object to be removed from the application database
     * @return The Taxi object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Taxi delete(Taxi taxi) throws Exception {
        log.info("delete() - Deleting " + taxi.toString());

        Taxi deletedTaxi = null;

        if (taxi.getId() != null) {
            deletedTaxi = crud.delete(taxi);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedTaxi;
    }
}
