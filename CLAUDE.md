# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

瑶池智浣 platform based on RuoYi-Vue3 + Spring Boot 3. This is a enterprise-level data management system that handles dataset creation, image cleaning, categorization, and quality assessment workflows.

## Architecture

### Tech Stack

**Frontend (Vue 3):**
- Framework: Vue 3.5.26 + Vite 6.4.1
- UI Library: Element Plus 2.13.1
- State Management: Pinia 3.0.4
- Router: Vue Router 4.6.4
- HTTP Client: Axios 1.13.2
- Additional: ECharts 5.6.0 for data visualization

**Backend (Spring Boot 3):**
- Framework: Spring Boot 3.5.11
- ORM: MyBatis-Plus 3.0.5
- Database Connection: Druid 1.2.28
- Security: Spring Security + JWT
- API Documentation: SpringDoc 2.8.16
- JDK: 17+

**Infrastructure:**
- Database: PostgreSQL 12+ or MySQL 8+
- Cache: Redis
- Object Storage: MinIO
- Build Tool: Maven 3.9+

### Project Structure

```
data-cleaning-platform/
├── RuoYi-Vue3/              # Vue 3 frontend (primary)
│   ├── src/
│   │   ├── api/            # API requests
│   │   ├── components/     # Reusable components
│   │   ├── layout/         # Layout components
│   │   ├── views/          # Page components
│   │   ├── router/         # Vue Router config
│   │   ├── store/          # Pinia stores
│   │   └── utils/          # Utilities
│   ├── .env.development    # Dev environment config
│   ├── package.json
│   └── vite.config.js
│
├── RuoYi-Vue-springboot3/   # Spring Boot 3 backend
│   ├── ruoyi-admin/        # Main module (entry point)
│   ├── ruoyi-common/       # Common utilities
│   ├── ruoyi-framework/    # Framework config
│   ├── ruoyi-generator/    # Code generator
│   ├── ruoyi-quartz/       # Scheduled tasks
│   ├── ruoyi-system/       # System management
│   ├── ruoyi-ui/           # Vue 2 admin UI (legacy)
│   └── sql/                # Database scripts
│
└── 数据清洗平台后端技术方案.md  # Technical specification
```

### Backend Module Structure

**ruoyi-admin/**: Application entry point, contains main Spring Boot class and controller layer
**ruoyi-common/**: Shared utilities, exceptions, constants, and cross-cutting concerns
**ruoyi-framework/**: Security, configuration, cache, and infrastructure
**ruoyi-generator/**: Code generation module
**ruoyi-quartz/**: Scheduled task management
**ruoyi-system/**: System management (users, roles, permissions, menus, etc.)

## Development Workflow

### Backend Development

**Environment Setup:**
```bash
cd RuoYi-Vue-springboot3

# Build project
mvn clean package -DskipTests

# Run development server
mvn spring-boot:run

# Run tests
mvn test

# Compile only
mvn compile
```

**Configuration:**
- Database config: `ruoyi-admin/src/main/resources/application-druid.yml`
- Application config: `ruoyi-admin/src/main/resources/application.yml`

### Frontend Development

**Environment Setup:**
```bash
cd RuoYi-Vue3

# Install dependencies
npm install
# or
yarn install

# Development server
npm run dev
# or
yarn dev

# Build for production
npm run build:prod
# or
yarn build:prod
```

**Configuration:**
- API base URL: `RuoYi-Vue3/.env.development`
- Build config: `RuoYi-Vue3/vite.config.js`

## Key Business Concepts

### Data Lifecycle

1. **Dataset Creation**: Auto-generated dataset codes (prefix + 6-digit sequence via Redis)
2. **Data Import**: Spring Batch import from data warehouse materialized views to temporary tables
3. **Cleaning Process**: AI-assisted scoring (0-100), automatic adopt (>80) or discard (<50), manual review (50-79)
4. **Categorization**: Hierarchical structure - Dataset → Category → Shard → Image
5. **Quality Assessment**: 5 dimensions (completeness, accuracy, consistency, timeliness, uniqueness)

### Core Entities

- **Dataset**: Container for images with status (initialized→cleaning→reviewed→published)
- **ProductCategory**: Product classification within a dataset
- **DataShard**: Logical partition within a category
- **ProductImage**: Individual images with cleaning status
- **Tag**: Multi-level tag system for categorization
- **AiCleanScore**: AI quality scores (clarity, completeness, overall)

### Data Architecture

**4-Layer DDD Architecture:**
- Interface Layer: Controllers, DTOs, VOs
- Application Layer: Services, command/query objects
- Domain Layer: Entities (rich domain model), value objects, domain services
- Infrastructure Layer: Repository implementations, POs, mappers

**Table Design:**
- Partitioned `product_image` table by create_time
- Temporary tables: `dataset_temp_{dataset_code}`
- Audit trails via `data_process_trace` table

## Important Implementation Notes

### Locking Mechanism

All category operations require locking due to large data volumes:
```java
// Use distributed lock service
distributedLockService.lock(categoryId);
try {
    // Perform operations
} finally {
    distributedLockService.unlock(categoryId);
}
```

### Temporary Table Management

- Created before import: `dataset_temp_{dataset_code}`
- Schema: shopcode, vendorcode, sn, image_md5, image_url, image_time
- Cleaned after dataset review passes
- Manual cleanup via UI button for expired tables (>7 days)

### Parallel Processing

- Use reactive programming (Spring WebFlux) for IO operations
- ForkJoinPool for multi-threaded processing
- Async operations with `@Async` annotation

### Data Consistency

- Dataset statistics must recalculate on any data adjustment
- Cascade updates across hierarchy (dataset → category → shard)
- Transaction management with `@Transactional`

## Testing

```bash
# Backend tests
cd RuoYi-Vue-springboot3
mvn test

# Frontend unit tests (if configured)
cd RuoYi-Vue3
npm run test
```

## Deployment

**Test Environment (Docker):**
- Single instance backend with folder-based DDD modules
- Infrastructure services: PostgreSQL, Redis, MinIO, Nacos (single instances)

**Production (Planned):**
- Microservices architecture
- High availability configurations
- Load balancing and service discovery

## API Documentation

- Swagger UI: http://localhost:8080/swagger-ui.html
- ReDoc: http://localhost:8080/swagger-ui.html

## Common Commands Summary

**Backend:**
```bash
cd RuoYi-Vue-springboot3
mvn clean package -DskipTests        # Build
mvn spring-boot:run                 # Run
mvn test                            # Tests
```

**Frontend:**
```bash
cd RuoYi-Vue3
npm install                          # Install deps
npm run dev                         # Dev server
npm run build:prod                  # Build production
```

## Data Models

See `数据清洗平台后端技术方案.md` for detailed entity relationships, database schemas, and domain models.

Key tables: `dataset`, `product_category`, `data_shard`, `product_image`, `tag`, `category_tag_rel`, `image_tag_rel`, `ai_clean_score`, `data_quality_assessment`, `data_process_trace`.
