# Docker

> Docker lets you package your application and all its dependencies into a single unit called a **container**. The container runs the same way on your laptop, your colleague's machine, and in production. "It works on my machine" becomes "it works on every machine."

---

## The Core Problem Docker Solves

Without Docker:
```
Developer A:  Python 3.9, library X v1.2
Developer B:  Python 3.11, library X v2.0
Production:   Python 3.8, library X v1.0
                  ↓
           Bugs, version mismatches, pain
```

With Docker:
```
Everyone uses the same container image → same environment, always
```

---

## Key Concepts

| Concept | Description |
|---------|-------------|
| **Image** | A read-only blueprint for a container (like a class in OOP) |
| **Container** | A running instance of an image (like an object) |
| **Dockerfile** | A recipe that describes how to build an image |
| **Docker Hub** | A public registry to store and share images |
| **Volume** | Persistent storage that survives container restarts |
| **Network** | How containers communicate with each other |

---

## Dockerfile Basics

A `Dockerfile` is a text file with instructions to build an image.

```dockerfile
# 1. Start from a base image
FROM golang:1.22-alpine

# 2. Set working directory inside the container
WORKDIR /app

# 3. Copy dependency files first (for layer caching)
COPY go.mod go.sum ./
RUN go mod download

# 4. Copy source code
COPY . .

# 5. Build the binary
RUN go build -o server .

# 6. Declare the port the app listens on (documentation only)
EXPOSE 8080

# 7. Default command to run when container starts
CMD ["./server"]
```

---

## Diagram: Docker Build Process

```
Dockerfile
  │
  ▼
docker build → Image (layers)
                  │
                  ▼
              docker run → Container (running process)
```

---

## Essential Docker Commands

```bash
# Build an image from Dockerfile in current directory
docker build -t myapp:latest .

# List images
docker images

# Run a container (detached, port mapping)
docker run -d -p 8080:8080 --name myapp myapp:latest
#              ↑host:container

# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# View logs
docker logs myapp
docker logs -f myapp  # follow (like tail -f)

# Execute a command inside a running container
docker exec -it myapp sh

# Stop a container
docker stop myapp

# Remove a container
docker rm myapp

# Remove an image
docker rmi myapp:latest

# Pull an image from Docker Hub
docker pull postgres:16
```

---

## Docker Compose

Docker Compose lets you define and run multi-container applications with a single file.

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=postgres://user:pass@db:5432/mydb
    depends_on:
      - db
    volumes:
      - .:/app  # mount local directory for development

  db:
    image: postgres:16
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: pass
      POSTGRES_DB: mydb
    volumes:
      - pg_data:/var/lib/postgresql/data  # persistent storage

volumes:
  pg_data:
```

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs for all services
docker-compose logs -f

# Rebuild and restart
docker-compose up -d --build
```

---

## Volumes (Persistent Storage)

Containers are ephemeral — data written inside is lost when the container is removed. Volumes persist data.

```bash
# Named volume
docker run -v pg_data:/var/lib/postgresql/data postgres:16

# Bind mount (local path → container path)
docker run -v /home/user/data:/data myapp

# Inspect volumes
docker volume ls
docker volume inspect pg_data
```

---

## Best Practices

1. **Use specific image tags** (`golang:1.22-alpine`, not `golang:latest`) for reproducibility.
2. **Order Dockerfile layers** from least to most frequently changing to maximize cache hits.
3. **Use `.dockerignore`** to exclude `node_modules`, `.git`, build artifacts.
4. **Keep images small** — use `alpine` base images; multi-stage builds for compiled languages.
5. **Don't run as root** — add a non-root user in the Dockerfile.

### Multi-Stage Build Example (Small Final Image)

```dockerfile
# Stage 1: build
FROM golang:1.22-alpine AS builder
WORKDIR /app
COPY . .
RUN go build -o server .

# Stage 2: minimal runtime image
FROM alpine:3.19
COPY --from=builder /app/server /server
CMD ["/server"]
# Final image is ~10MB instead of ~600MB
```

---

## Real-World Use Case

- **Local development:** Spin up databases, message queues, and services with `docker-compose up`.
- **CI/CD:** Build and test in containers to ensure consistency with production.
- **Deployment:** Ship your app as a container image to any cloud (AWS ECS, GKE, Azure ACI).
- **Microservices:** Each service runs in its own container, independently deployable.
