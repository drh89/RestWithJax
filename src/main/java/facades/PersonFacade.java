package facades;

import dto.PersonDTO;
import entities.Person;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements PersonFacadeInterface {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Person addPerson(String fName, String lName, String phone) {
        EntityManager em = getEntityManager();
        Person p = new Person(fName, lName, phone);
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        em.refresh(p);
        em.close();

        return p;
    }

    @Override
    public Person deletePerson(int id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person p = em.find(Person.class, id);
        if(p == null){
            throw new PersonNotFoundException(String.format("Person with id: (%d) not found", id));
        }

        try {
            em.getTransaction().begin();
            em.remove(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return p;
    }

    @Override
    public Person getPerson(int id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        try {
            Person p = em.find(Person.class, id);
            if (p == null) {
                throw new PersonNotFoundException(String.format("Person with id: (%d) not found", id));
            }
            return p;
        } finally {
            em.close();
        }

    }

    @Override
    public List<Person> getAllPersons() {
        EntityManager em = getEntityManager();
        TypedQuery tq = em.createNamedQuery("Person.getAll", Person.class);
        List<Person> persons = tq.getResultList();
        return persons;
    }

    @Override
    public Person editPerson(Person p) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        
        Person person = em.find(Person.class, p.getId());
        if(person == null){
            throw new PersonNotFoundException(String.format("Person with id: (%d) not found", p.getId()));
        }
        
        person.setFirstName(p.getFirstName());
        person.setLastName(p.getLastName());
        person.setPhone(p.getPhone());
        try{
        em.getTransaction().begin();

        person.setLastEdited(new Date());
        em.merge(person);
        em.getTransaction().commit();
        }finally{
        em.close();
        }
        return person;

    }

}
