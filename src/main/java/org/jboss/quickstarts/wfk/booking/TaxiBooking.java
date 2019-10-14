package org.jboss.quickstarts.wfk.booking;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.taxi.Taxi;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Date;

/**
 * <p>This is a the Domain object. The Booking class represents how booking resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a bookings are retrieved from the database (with @NamedQueries), and acceptable values
 * for Booking fields (with @NotNull, @Pattern etc...)</p>
 *
 * @author Constance He
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = TaxiBooking.FIND_ALL, query = "SELECT b FROM TaxiBooking b ORDER BY b.id ASC"),
        @NamedQuery(name = TaxiBooking.FIND_BY_CUSTOMER, query = "SELECT b FROM TaxiBooking b WHERE b.customer.id = :customerId"),
        @NamedQuery(name = TaxiBooking.FIND_BY_TAXI, query = "SELECT b FROM TaxiBooking b WHERE b.taxi.id = :taxiId"),
        @NamedQuery(name = TaxiBooking.FIND_BY_DATE_AND_TAXI, query = "SELECT b FROM TaxiBooking b WHERE b.taxi.id = :taxiId and b.date = :date")

})
@XmlRootElement
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = { "date", "taxiId" }))
public class TaxiBooking implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomer";
    public static final String FIND_BY_TAXI = "Booking.findByTaxi";
    public static final String FIND_BY_DATE_AND_TAXI = "Booking.findByDateAndTaxi";
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "customerId")
    private Customer customer;
    
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "taxiId")
    private Taxi taxi;
    
    @NotNull
    @Future(message = "Booking day must be be in the future.")
    @Column(name = "date")
    @Temporal(TemporalType.DATE)  
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
  
    public Customer getCustomer() {
    	return customer;
    }
  
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Taxi getTaxi() {
    	return taxi;
    }
    
    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }
    
    public Long getCustomerId() {
        return customer.getId();
    }
    
    public void setCustomerId(Long id) {
        this.customer.setId(id);
    }

    public Long getTaxiId() {
        return taxi.getId();
    }
    
    public void setTaxiId(Long id) {
        this.taxi.setId(id);
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
}
