package org.jboss.quickstarts.wfk.taxi;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * <p>This is a the Domain object. The Taxi class represents how contact resources are represented in the application
 * database.</p>
 *
 * <p>The class also specifies how a contacts are retrieved from the database (with @NamedQueries), and acceptable values
 * for Taxi fields (with @NotNull, @Pattern etc...)</p>
 *
 * @author Constance He
 */
/*
 * The @NamedQueries included here are for searching against the table that reflects this object.  This is the most efficient
 * form of query in JPA though is it more error prone due to the syntax being in a String.  This makes it harder to debug.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Taxi.FIND_ALL, query = "SELECT t FROM Taxi t ORDER BY t.registration ASC"),
        @NamedQuery(name = Taxi.FIND_BY_REGISTRATION, query = "SELECT t FROM Taxi t WHERE t.registration = :registration")
})
@XmlRootElement
@Table(name = "taxi", uniqueConstraints = @UniqueConstraint(columnNames = "registration"))
public class Taxi implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Taxi.findAll";
    public static final String FIND_BY_REGISTRATION = "Taxi.findByRegistration";
    public static final String FIND_BY_SEATS = "Taxi.findBySeats";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9]{7}$", message = "Please note String length is 7.")
    @Column(name = "registration")
    private String registration;

    @NotNull
    @Column(name = "seats")
    @Min(value = 2)
    @Max(value = 20)
    private int seats;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }
    
    public void setRegistration(String registration) {
        this.registration = registration;
    }
    
    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}

