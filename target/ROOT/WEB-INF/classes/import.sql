--
-- JBoss, Home of Professional Open Source
-- Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
-- contributors by the @authors tag. See the copyright.txt in the
-- distribution for a full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements
-- Since the database doesn't know to increase the Sequence to match what is manually loaded here it starts at 1 and tries
--  to enter a record with the same PK and create an error.  If we use a high we don't interfere with the sequencing (at least until later).
-- NOTE: this file should be removed for production systems. 
insert into Contact (id, first_name, last_name, email, phone_number, birth_date) values (10001, 'John', 'Smith', 'john.smith@mailinator.com', '(212) 555-1212', '1963-06-03')
insert into Contact (id, first_name, last_name, email, phone_number, birth_date) values (10002, 'Davey', 'Jones', 'davey.jones@locker.com', '(212) 555-3333', '1996-08-07')

insert into Customer (id, name, email, phoneNumber) values (10000, 'Keshan', 'K.H3@ncl.ac.uk', '07422123456')
insert into Customer (id, name, email, phoneNumber) values (10001, 'Constance', 'Constance.He@ncl.ac.uk', '07123456789')
insert into Customer (id, name, email, phoneNumber) values (10002, 'Cathy', 'Cathy.Lin@ncl.ac.uk', '07410000000')

insert into Taxi (id, registration, seats) values (10003, 'QWE1234', 4)
insert into Taxi (id, registration, seats) values (10004, 'RTY1234', 10)
insert into Taxi (id, registration, seats) values (10005, 'UIO1234', 6)

insert into Booking (id, customerId, taxiId, date) values (11110, 10002, 10003,'2019-11-11')
insert into Booking (id, customerId, taxiId, date) values (11111, 10002, 10004,'2019-11-12')
