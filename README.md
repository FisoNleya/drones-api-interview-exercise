### Drones Api README  ( Fiso  www.linkedin.com/in/fiso-nleya )

This README serves as a guide to set up, build, run, and test the Drones project locally or in a containerized environment.

#### Prerequisites
Before getting started, ensure you have the following installed on your system:
- JDK Java 17
- Maven (for building the project)
- Docker (if running the project within a container)

#### Setup Instructions
1. Clone the repository to your local machine:

    ```
    git clone [repository_url]
    ```

2. Navigate to the project directory:

    ```
    cd [project_directory]
    ```

#### Building the Project
To build the project, execute the following command:

```
mvn clean install
```

#### Running the Project Locally
To run the project locally, follow these steps:

1. Ensure Maven has built the project successfully.
2. Run the following command:

    ```
    mvn spring-boot:run
    ```

3. Once the application has started, you can access the Swagger documentation at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) to explore the API endpoints and interact with them.

#### Running the Project in a Container
If you prefer to run the project within a containerized environment, follow these steps:

1. Ensure Docker is installed on your system.
2. Build the Docker image using the provided Dockerfile:

    ```
    docker build -t [image_name] .
    ```

3. Once the image is built successfully, you can run the container:

    ```
    docker run -p 8080:8080 [image_name]
    ```

4. Access the Swagger documentation at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) to interact with the API endpoints.

#### Testing
Unit tests and integration tests are included in the project to ensure its functionality. To run the tests, execute the following command:

```
mvn test
```

#### Additional Information
- This project utilizes the Spring Boot framework version 3.2.3.
- The H2 in-memory database is used for data storage.
- Default data examples are preloaded and documented in the Swagger documentation.

### Troubleshooting
If you encounter any issues during setup, building, running, or testing the project, please  reach out on www.linkedin.com/in/fiso-nleya.
