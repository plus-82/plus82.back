# Plus82 Backend Development Guidelines

## Build & Command Reference
- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- Test all: `./gradlew test`
- Test single class: `./gradlew test --tests "com.etplus.SomeTest"`
- Test single method: `./gradlew test --tests "com.etplus.SomeTest.specificTestMethod"`
- Clean build: `./gradlew clean build`

## Code Style & Organization
- **Naming**:
  - Classes: PascalCase with descriptive suffixes (Entity, Service, Repository, etc.)
  - Methods: camelCase with verb prefixes (get, create, update, delete)
  - Variables: camelCase with clear meaning
  - Constants: SNAKE_CASE_CAPS
- **Package Structure**:
  - Controllers in controller package
  - Services in service package
  - Repository interfaces in repository package
  - Domain entities in repository.domain package
  - DTOs in controller.dto package

## Error Handling
- Use custom exceptions with specific types
- All exceptions should extend from appropriate base exceptions
- Handle exceptions at the controller level via CustomExceptionHandler
- Return consistent error responses using CommonResponse format

## Patterns & Conventions
- Use constructor injection for dependencies
- Follow immutability principles where appropriate
- Document public APIs with clear method descriptions
- Write tests for all non-trivial functionality
- Validate inputs at controller level using @Valid