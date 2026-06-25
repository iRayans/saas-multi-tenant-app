# SaaS Multi-Tenant App

A learning project built to explore multi-tenancy concepts in a Spring Boot application. The goal was to understand and
implement two different multi-tenancy strategies from scratch.

## Architecture

The application is deployed on AWS using:

- **ECS Fargate** — runs the Spring Boot containers
- **Application Load Balancer** — distributes traffic across tasks
- **RDS PostgreSQL** — multi-tenant database (schema-per-tenant)
- **ElastiCache Redis** — caching layer for tenant schema lookups
- **ECR** — Docker image registry
- **VPC** with public/private subnets across 2 AZs
- **NAT Gateway** — allows private subnets to reach the internet (for ECR pulls)

## What I Learned

### Approach 1 — Shared Schema (Single Database)

All tenants share the same tables. Tenant isolation is achieved by:

- Passing `X-Tenant-Id` in every request header
- A `TenantFilter` that intercepts each request and extracts the tenant ID
- A Hibernate filter (`TenantHibernateFilter`) that automatically appends the tenant ID condition to every query via the
  session factory

### Approach 2 — Schema Per Tenant

Each tenant gets their own isolated PostgreSQL schema. When a new tenant is onboarded:

1. A new schema is created dynamically (`CREATE SCHEMA tenant_<company_code>`)
2. Flyway runs the DDL migration scripts against that schema, creating all the tables
3. A `MultiTenantConnectionProvider` switches the `search_path` to the correct tenant schema on every request
4. A `CurrentTenantIdentifierResolver` tells Hibernate which schema to use based on the current request context
5. The tenant schema is resolved from the JWT token and stored in a `ThreadLocal` via `TenantContext`
   The second approach is what the final implementation uses.

## Tech Stack

- **Java 21**
- **Spring Boot 3**
- **Spring Security** — JWT authentication with RSA key pairs
- **Spring Data JPA / Hibernate** — with multi-tenancy support
- **PostgreSQL** — schema-per-tenant isolation
- **Flyway** — automated schema migrations per tenant
- **HikariCP** — connection pooling
- **Lombok**

## Running the Project

### Prerequisites

- Java 21
- PostgreSQL
- Maven

### Setup

1. Clone the repository
2. Create a PostgreSQL database
3. Copy `.env.example` to `.env` and fill in your database credentials
4. Generate RSA key pair and place them under `certs/`
5. Run the application — Flyway will handle the public schema migrations automatically

```bash
./mvnw spring-boot:run
```

The app runs on `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Deploying to AWS

The full infrastructure is defined in Terraform under `terraform/`.

```bash
cd terraform
terraform init
terraform apply
```

After apply completes, push your Docker image:

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 
docker buildx build --platform linux/amd64 -t /saas-app:latest --push .
```

ECS will pick up the image automatically.

Get the ALB URL:

```bash
terraform output alb_dns_name
```

To tear everything down:

```bash
terraform destroy
```

## Project Status

This project is a learning portfolio piece built by following the "Alibou" SaaS multi-tenancy tutorial, then extending
it with full AWS deployment and Terraform automation.