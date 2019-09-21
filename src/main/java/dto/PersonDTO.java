/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dennis
 */
public class PersonDTO {
    
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private ArrayList<PersonDTO> all;
    
    public PersonDTO(List<Person> persons){
        all = new ArrayList();
        persons.forEach((person) -> {
            all.add(new PersonDTO(person));
        });
    }
    
    public PersonDTO(Person p){
        this.id = p.getId();
        this.firstName = p.getFirstName();
        this.lastName = p.getLastName();
        this.phone = p.getPhone();
        
    }
    public PersonDTO(String firstName, String lastName, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
    public PersonDTO(){
        
    }

    public ArrayList<PersonDTO> getAll() {
        return all;
    }

    public void setAll(ArrayList<PersonDTO> all) {
        this.all = all;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    
    
}
