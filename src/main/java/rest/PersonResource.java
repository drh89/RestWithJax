package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import entities.Person;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//Todo Remove or change relevant parts before ACTUAL use
@Path("persons")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/RestWithJax",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE =  PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/edit/{id}")
    public Response editPerson(@PathParam("id") int id, String person) throws PersonNotFoundException{
        PersonDTO p = GSON.fromJson(person, PersonDTO.class);
        Person pToEdit = new Person(p.getFirstName(),p.getLastName(),p.getPhone());
        pToEdit.setId(id);
        Person editedPerson = FACADE.editPerson(pToEdit);
        return Response.ok().entity(GSON.toJson(new PersonDTO(editedPerson))).build();
    }
            
            
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addPerson(String person){
        PersonDTO dto = GSON.fromJson(person, PersonDTO.class);
        Person p = FACADE.addPerson(dto.getFirstName(), dto.getLastName(), dto.getPhone());
        return Response.ok().entity(GSON.toJson(new PersonDTO(p))).build();
    }
    @Path("/find/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPerson(@PathParam("id") int id) throws PersonNotFoundException{
        PersonDTO p = new PersonDTO(FACADE.getPerson(id));
        return Response.ok().entity(GSON.toJson(p)).build();
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/delete/id/{id}")
    public String delete(@PathParam("id") int id) throws PersonNotFoundException{
        FACADE.deletePerson(id);
        
        return "{\"status\":\"deleted\"}";
    }
    
    
    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllPersons(){
        List<PersonDTO> persons = new PersonDTO(FACADE.getAllPersons()).getAll();
        return Response.ok().entity(GSON.toJson(persons)).build();
    }
    
    
    public static void main(String[] args) {
        System.out.println(FACADE.addPerson("Bent", "Fabricius", "66677788"));
    }
    
 
}
