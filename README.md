# simple-spring-cicd

Minimal Spring Boot app that is ready for CI/CD with GitHub Actions.

## Requirements
- Java 17
- Maven 3.6+
- Docker (optional)

## Build & run locally

Run tests:

```bash
mvn -B clean test
```

Build package:

```bash
mvn -B -DskipTests=false package
```

Run locally with Maven:

```bash
mvn spring-boot:run
```

Or run the produced jar:

```bash
java -jar target/simple-spring-cicd-0.0.1-SNAPSHOT.jar
```

## Docker

Build image:

```bash
docker build -t simple-spring-cicd:latest .
```

Run container:

```bash
docker run -p 8080:8080 simple-spring-cicd:latest
```

## CI (GitHub Actions)

A workflow is included at `.github/workflows/ci.yml` which:
- checks out the code
- sets up JDK 17
- caches Maven repository
- runs `mvn verify`
- uploads the generated jar as an artifact
- optionally builds and pushes a Docker image if registry secrets are configured

To enable Docker publish, set the following secrets in the repository settings:
- `DOCKER_REGISTRY` (e.g. `ghcr.io/<owner>` or `docker.io/<user>`)
- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`

You can trigger the workflow by pushing to `main` or opening a pull request. Manual runs are possible via `workflow_dispatch`.

