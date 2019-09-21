package rest;

import dto.PersonDTO;
import entities.Person;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import static io.restassured.parsing.Parser.JSON;
import java.net.URI;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import static org.glassfish.grizzly.http.util.Header.ContentType;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    //Read this line from a settings-file  since used several places
    private static final String TEST_DB = "jdbc:mysql://localhost:3307/startcode_test";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    private static Person p1,p2;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);

        //NOT Required if you use the version of EMF_Creator.createEntityManagerFactory used above        
        //System.setProperty("IS_TEST", TEST_DB);
        //We are using the database on the virtual Vagrant image, so username password are the same for all dev-databases
        
        httpServer = startServer();
        
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
   
        RestAssured.defaultParser = Parser.JSON;
    }
    
    @AfterAll
    public static void closeTestServer(){
        //System.in.read();
         httpServer.shutdownNow();
    }
    
    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Person("Jan", "Hansen", "22233311");
        p2 = new Person("Adam", "Jensen", "22444411");       
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
           
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/persons").then().statusCode(200);
    }
   
    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
        .contentType("application/json")
        .get("/persons/").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("msg", equalTo("Hello World"));   
    }
    
    @Test
    public void testGetAllPersons()  {
        given()
                .contentType("application/json")
                .get("/persons/all").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size",equalTo(2));
//        List<PersonDTO> personDTOs;
//        personDTOs= given()    
//        .contentType("application/json")
//        .when()
//        .get("/persons/all")
//        .then()
//        .extract().body().jsonPath().getList("all",PersonDTO.class);
//        PersonDTO p1DTO = new PersonDTO(p1);
//        PersonDTO p2DTO = new PersonDTO(p2);
//        assertThat(personDTOs,containsInAnyOrder(p1DTO,p2DTO));            
    }
    
    @Test
    public void findPerson()  {
         given()    
        .contentType("application/json")
        .when()
        .get("/persons/find/{id}",p1.getId())
        .then()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("id", equalTo(p1.getId()));           
    }
    
     @Test
    public void FindNonExistingPerson() {
         given()    
        .contentType("application/json")
//        .accept(ContentType.ANY)
        .when()
        .get("/persons/find/{id}",p1.getId()+10)
        .then()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code",equalTo(404))
        .body("$",hasKey("message")); //Don't bother to check for the actual value  
    }
    
    @Test
    public void addPerson() {
        given()    
        .contentType("application/json")
        .body(new PersonDTO("Ib","Ibsen","123"))
        .when()
        .post("/persons")
        .then()
        .body("firstName",equalTo("Ib"))
        .body("lastName",equalTo("Ibsen"));
    }
    
    @Test
    public void editPerson() {
        //change Jan's phone and last name
        p1.setPhone("12345");
        p1.setLastName("Andersen");
        given()    
        .contentType("application/json")
        .body(new PersonDTO(p1.getFirstName(),p1.getLastName(),p1.getPhone()))
        .when()
        .post("/persons/edit/{id}",p1.getId())
        .then()
        //.log().body()
        .body("lastName",equalTo("Andersen"))
        .body("phone",equalTo("12345"));
    }
    @Test
    public void deletePerson() {
        System.out.println("------> "+p1.getId());
         given()    
        .contentType("application/json")
//        .accept(ContentType.)        
        .when()
        .post("/persons/delete/id/{id}",p1.getId())
        .then()
        .body("status",equalTo("deleted"));
         
         //Finally check that item was removed from DB
         EntityManager em = emf.createEntityManager();
         try{
             assertThat(em.find(Person.class, p1.getId()), is(nullValue()));
         }finally{
             em.close();
         } 
    }
    @Test
    public void deleteNonExistingPerson() {
         given()    
        .contentType("application/json")
//        .accept(ContentType.JSON)        
        .when()
        .delete("/persons/{id}",p1.getId()+10)
        .then()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code",equalTo(404))
        .body("$",hasKey("message")); //Don't bother to check for the actual value  
    }

}
