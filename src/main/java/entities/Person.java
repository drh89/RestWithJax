package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Person")
@NamedQueries({
    @NamedQuery(name = "Person.deleteAllRows", query = "DELETE FROM Person"),
    @NamedQuery(name = "Person.deletePersonById", query = "DELETE FROM Person p WHERE p.id = :id"),
    @NamedQuery(name = "Person.getById", query = "SELECT p FROM Person p WHERE p.id = :id"),
    @NamedQuery(name = "Person.getAll", query = "SELECT p FROM Person p")
})
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "phone")
    private String phone;
    @Temporal(TemporalType.DATE)
    @Column(name = "created")
    private Date created;
    @Temporal(TemporalType.DATE)
    @Column(name = "lastEdited")
    private Date lastEdited;

    public Person() {
    }

    public Person(String firstName, String lastName, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.created = new Date();
        this.lastEdited = new Date();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date lastEdited) {
        this.lastEdited = lastEdited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
