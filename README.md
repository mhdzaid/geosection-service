## GeoSection Service
* ### How To Run
  * Move to the project directory `geosection-service`
    * `cd geosection-service`
  * Run the project
    * `mvn spring-boot:run`
* ### How To Test
  * Move to the project directory `geosection-service`
    * `cd geosection-service`
  * Run the tests
    * `mvn test`
* ### Project Description
  * This project is a Spring Boot application that provides a REST API for the GeoSection Service.
  * The REST API is documented using Swagger and can be accessed at `http://localhost:8080/swagger-ui/index.html#/`
  * The project is implemented with very Basic Authentication using Spring Security. The credentials to be sent with each request are:
    * Username: `user`
    * Password: `password`
  ### Section Endpoints
  * Implemented using a simple in-memory h2 database for the sake of simplicity.
  * Relationships between section and geological classes is `@ManyToMany`.
  * The name of section is considered unique.
  * The endpoints are:
    * `GET /sections` - Returns all sections.
    * `GET /sections/{id}` - Returns a section by id.
    * `POST /sections` - Creates a new section along with geological classes.
  ```
    {
         "name": "Section 2",
         "geologicalClasses": 
         [
            { "name": "Geo Class 21", "code": "GC11" },
            { "name": "Geo Class 22", "code": "GC22" }
         ]
    }
  ```
    * `PUT /sections/{id}` - Updates a section as well as geological classes by id.
    ```
    {
        "id": 3,
        "name": "Section 11",
        "geologicalClasses": 
        [
            {
                "id": 3,
                "name": "Geo Class 21",
                "code": "GC11"
            },
            {
                "id": 4,
                "name": "Geo Class 22",
                "code": "GC22"
            },
            {
                "id": 5,
                "name": "Geo Class 51",
                "code": "GC51"
            }
        ]
   }
  ```
    * `DELETE /sections/{id}` - Deletes a section by id but retains the geological classes that were created.
  ### Geological Class Endpoints
  * Also implemented CRUD restpoint for geological classes in-case we want to modify or create data without the need to use section.
  * The endpoints are:
    * `GET /geological-classes` - Returns all geological classes.
    * `GET /geological-classes/{id}` - Returns a geological class by id.
    * `POST /geological-classes` - Creates a new geological class.
    * `PUT /geological-classes/{id}` - Updates a geological class by id.
    * `DELETE /geological-classes/{id}` - Deletes a geological class by id.