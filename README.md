<<<<<<< HEAD
# SmartNotes Manager

A modern, AI-powered notes and task management application built with Spring Boot 3, WebFlux, MongoDB, and OpenAI integration.

## Features

- **User Authentication**: Secure OAuth2 login with Google
- **Note Management**: Create, read, update, and delete notes
- **Task Management**: Create, read, update, and delete tasks
- **AI-Powered Features**:
    - Generate summaries for notes using OpenAI
    - Generate task suggestions from note content using OpenAI
- **RESTful API**: Fully documented with Swagger/OpenAPI
- **Reactive & Scalable**: Built with Spring WebFlux for non-blocking I/O
- **Containerized**: Easy deployment with Docker and Docker Compose

## Prerequisites

- Java 21 or later
- Maven 3.6.3 or later
- Docker and Docker Compose (for containerized deployment)
- MongoDB (or use the provided Docker Compose setup)
- Google OAuth2 credentials (for authentication)
- OpenAI API key (for AI features)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/smart-notes-manager.git
cd smart-notes-manager
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# OpenAI
OPENAI_API_KEY=your-openai-api-key

# JWT Secret (generate a secure random string)
JWT_SECRET=$(openssl rand -base64 32)
```

### 3. Run with Docker Compose (Recommended)

The easiest way to run the application is using Docker Compose:

```bash
docker-compose up --build
```

This will start:
- The SmartNotes Manager application on port 8080
- MongoDB on port 27017
- Mongo Express (web-based MongoDB admin) on port 8081

### 4. Run Locally (Development)

If you prefer to run the application locally:

1. Make sure you have MongoDB running locally
2. Build and run the application:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Documentation

Once the application is running, you can access the following:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`

## Available Endpoints

### Authentication

- `GET /api/auth/me` - Get current user info
- `GET /api/auth/token` - Get JWT token for the current session

### Notes

- `GET /api/notes` - Get all notes
- `GET /api/notes/{id}` - Get a specific note
- `POST /api/notes` - Create a new note
- `PUT /api/notes/{id}` - Update a note
- `DELETE /api/notes/{id}` - Delete a note
- `POST /api/notes/{id}/archive` - Archive/unarchive a note
- `POST /api/notes/{id}/summarize` - Generate a summary for a note

### Tasks

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get a specific task
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task
- `PATCH /api/tasks/{id}/status` - Update task status
- `POST /api/tasks/generate-from-note/{noteId}` - Generate tasks from a note

## Configuration

You can configure the application by setting the following environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/smartnotes` |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID` | Google OAuth2 client ID | - |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | - |
| `APP_OPENAI_API_KEY` | OpenAI API key | - |
| `APP_JWT_SECRET` | JWT signing key | Randomly generated |

## Development

### Build

```bash
./mvnw clean package
```

### Run Tests

```bash
./mvnw test
```

### Code Formatting

```bash
./mvnw spotless:apply
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Project Reactor](https://projectreactor.io/)
- [MongoDB](https://www.mongodb.com/)
- [OpenAI](https://openai.com/)
- [Docker](https://www.docker.com/)
=======
# SmartNotes Manager

A modern, AI-powered notes and task management application built with Spring Boot 3, WebFlux, MongoDB, and OpenAI integration.

## Features

- **User Authentication**: Secure OAuth2 login with Google
- **Note Management**: Create, read, update, and delete notes
- **Task Management**: Create, read, update, and delete tasks
- **AI-Powered Features**:
    - Generate summaries for notes using OpenAI
    - Generate task suggestions from note content using OpenAI
- **RESTful API**: Fully documented with Swagger/OpenAPI
- **Reactive & Scalable**: Built with Spring WebFlux for non-blocking I/O
- **Containerized**: Easy deployment with Docker and Docker Compose

## Prerequisites

- Java 21 or later
- Maven 3.6.3 or later
- Docker and Docker Compose (for containerized deployment)
- MongoDB (or use the provided Docker Compose setup)
- Google OAuth2 credentials (for authentication)
- OpenAI API key (for AI features)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/smart-notes-manager.git
cd smart-notes-manager
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# OpenAI
OPENAI_API_KEY=your-openai-api-key

# JWT Secret (generate a secure random string)
JWT_SECRET=$(openssl rand -base64 32)
```

### 3. Run with Docker Compose (Recommended)

The easiest way to run the application is using Docker Compose:

```bash
docker-compose up --build
```

This will start:
- The SmartNotes Manager application on port 8080
- MongoDB on port 27017
- Mongo Express (web-based MongoDB admin) on port 8081

### 4. Run Locally (Development)

If you prefer to run the application locally:

1. Make sure you have MongoDB running locally
2. Build and run the application:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Documentation

Once the application is running, you can access the following:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`

## Available Endpoints

### Authentication

- `GET /api/auth/me` - Get current user info
- `GET /api/auth/token` - Get JWT token for the current session

### Notes

- `GET /api/notes` - Get all notes
- `GET /api/notes/{id}` - Get a specific note
- `POST /api/notes` - Create a new note
- `PUT /api/notes/{id}` - Update a note
- `DELETE /api/notes/{id}` - Delete a note
- `POST /api/notes/{id}/archive` - Archive/unarchive a note
- `POST /api/notes/{id}/summarize` - Generate a summary for a note

### Tasks

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get a specific task
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task
- `PATCH /api/tasks/{id}/status` - Update task status
- `POST /api/tasks/generate-from-note/{noteId}` - Generate tasks from a note

## Configuration

You can configure the application by setting the following environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/smartnotes` |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID` | Google OAuth2 client ID | - |
| `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | - |
| `APP_OPENAI_API_KEY` | OpenAI API key | - |
| `APP_JWT_SECRET` | JWT signing key | Randomly generated |

## Development

### Build

```bash
./mvnw clean package
```

### Run Tests

```bash
./mvnw test
```

### Code Formatting

```bash
./mvnw spotless:apply
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Project Reactor](https://projectreactor.io/)
- [MongoDB](https://www.mongodb.com/)
- [OpenAI](https://openai.com/)
- [Docker](https://www.docker.com/)
>>>>>>> e5519aa4d153a23a8c06402362310514a9833054
