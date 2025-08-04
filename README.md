# Movie Recommendation Service

This is a Spring Boot application that provides a simple movie recommendation service based on user ratings. Users can like films, manage friendships, and receive personalized top-5 movie recommendations.
## Key Features:
- CRUD operations for users and films
- Social features: friend lists and mutual friends
- Rating system using "likes"
- Dynamic top film recommendations

## Technologies Used:
- Spring Boot (Web, Validation, Testing)
- JDBC with H2 an in-memory database
- Lombok for reducing boilerplate
- Logbook for HTTP request/response logging
- Comprehensive testing with `@JdbcTest` and an isolated test database