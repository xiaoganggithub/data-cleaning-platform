# Analysis Findings: 瑶池智浣数据清洗平台

## Document Analysis
**File**: `/Users/zg/Downloads/data-cleaning-platform/数据清洗平台后端技术方案.md`
**Lines**: 4077
**Last Updated**: 2026-03-21

---

## Executive Summary

A comprehensive data cleaning platform with DDD (Domain-Driven Design) 4-layer architecture, featuring:

- **10 Core Data Models** spanning dataset, categories, images, tags, AI scoring, quality assessment
- **30+ Features** covering data cleaning, quality management, AI analysis, export, etc.
- **Complete Tech Stack**: Vue 3 + Spring Boot 3 + PostgreSQL + Redis + MinIO
- **Deployment**: Docker-based with Docker Compose orchestration

---

## Architecture Design

### DDD 4-Layer Structure

```
Interface Layer (接口层)
  - Controller: REST API endpoints
  - DTO: Data transfer objects (requests)
  - VO: View objects (responses)
  - Parameter validation

Application Layer (应用层)
  - Service: Application services
  - Assembler: Entity <-> VO/DTO conversion
  - Transaction orchestration
  - Business process control

Domain Layer (领域层)
  - Entity: Domain entities (充血模型)
  - Value Object: Value objects
  - Domain Service: Domain services
  - Repository Interface: Repository interfaces
  - Domain Event: Domain events
  - Specification: Specification pattern

Infrastructure Layer (基础设施层)
  - Repository Impl: Repository implementations
  - PO: Persistent objects
  - Mapper: MyBatis-Plus mappers
  - External service calls
  - Message queue, cache, storage
```

### Module Structure

```
ruoyi-system/
├── domain/           # Domain entities
│   ├── entity/
│   ├── valueobject/
│   ├── service/
│   ├── repository/
│   └── event/
├── application/      # Application services
│   ├── service/
│   └── assembler/
├── persistence/      # Infrastructure layer
│   ├── po/
│   ├── mapper/
│   └── repository/
└── interfaces/       # Interface layer
    ├── controller/
    ├── dto/
    └── vo/
```

---

## Data Models (10 Total)

### 1. Dataset (数据集)
**Purpose**: Core data container with lifecycle management

**Key Fields**:
- dataset_id (PK), dataset_code (unique)
- name, description
- status (0:初始化, 1:待清洗, 2:清洗中, 3:已审核, 4:已发布, 5:已归档, 6:已删除)
- total_image_count, cleaned_image_count
- create_time, update_time

**Relationships**:
- 1:N → ProductCategory (categories)
- 1:N → DataProcessTrace (processing history)

**Lifecycle**:
```
创建 → 初始化 → 数据导入 → 数据清洗 → 数据审核 → 数据发布 → 数据归档 → 数据删除
```

**Constraints**:
- Status transitions must follow flow
- Audit required before publish
- Archive after 365 days
- Delete after 30 days

---

### 2. ProductCategory (商品图片分类)
**Purpose**: Categorize images within a dataset

**Key Fields**:
- category_id (PK)
- dataset_id (FK)
- dataset_code (unique)
- plu_code, plu_name
- total_image_count, cleaned_image_count
- status (0:待清洗, 1:清洗中, 2:已审核)
- locked (boolean), sample_score

**Relationships**:
- N:M → Tag (through category_tag_rel)
- 1:N → DataShard (shards)
- 1:N → ProductImage (images)

**Key Features**:
- PLU merge, split, copy, delete operations
- Import from MinIO
- Import from other datasets
- Random sampling and scoring
- Tag management integration

**Constraints**:
- Similarity threshold 0.8 for merge
- Max 100,000 images per category after merge
- Locked categories cannot be modified

---

### 3. DataShard (数据分片)
**Purpose**: Organize images within a category

**Key Fields**:
- shard_id (PK)
- dataset_id, dataset_code
- category_id, plu_code, plu_name
- status (0:待清洗, 1:清洗中, 2:已审核)
- total_image_count, cleaned_image_count

**Relationships**:
- 1:N → ProductImage (images)

**Constraints**:
- Unique constraint: dataset_code + plu_code
- Shard created per category, can create multiple or use default

---

### 4. ProductImage (商品图片)
**Purpose**: Individual image with cleaning status

**Key Fields**:
- image_id (PK)
- dataset_id, dataset_code
- category_id, plu_code, plu_name
- shard_id
- shopcode, vendorcode, sn
- image_md5 (unique)
- image_url
- image_status (0:待清洗, 1:清洗中, 2:已审核)
- locked, status (0:废弃, 1:采纳)

**Relationships**:
- N:M → Tag (through image_tag_rel)
- 1:N → AiCleanScore (AI scores)
- 1:1 → DataProcessTrace (processing trace)

**Special Feature**:
- Partition table by create_time
- Supports batch operations
- Keyboard shortcuts for quick adoption/discard
- Cross-dataset copy/delete in MinIO

**Constraints**:
- UNIQUE(dataset_code, image_md5)
- Status based on image_status, not general status

---

### 5. Tag (标签)
**Purpose**: Multi-level tagging system

**Key Fields**:
- tag_id (PK)
- tag_name, tag_color
- tag_type (0:系统标签, 1:用户标签)
- tag_description
- parent_tag_id (supports 3-level hierarchy)
- sort_order, create_user_id
- create_time, update_time

**Relationships**:
- N:M → ProductCategory (through category_tag_rel)
- N:M → ProductImage (through image_tag_rel)

**Hierarchy**:
- Max 3 levels deep
- Display full tag path (e.g., "质量/清晰度/模糊")
- System tags (0): cannot be deleted
- User tags (1): editable, can convert to system tags

**Features**:
- Multi-level tree structure
- Parent-child relationships
- Tag type management
- User-based permission control

---

### 6. CategoryTagRel (分类标签关联)
**Purpose**: Many-to-many relationship between categories and tags

**Key Fields**:
- rel_id (PK)
- category_id, tag_id
- create_user_id, create_time

**Constraints**:
- UNIQUE(category_id, tag_id)
- CASCADE delete when category or tag deleted

---

### 7. ImageTagRel (图片标签关联)
**Purpose**: Many-to-many relationship between images and tags

**Key Fields**:
- rel_id (PK)
- image_id, tag_id
- create_user_id, create_time

**Constraints**:
- UNIQUE(image_id, tag_id)
- CASCADE delete when image or tag deleted

---

### 8. AiCleanScore (AI清洗评分)
**Purpose**: AI-powered image quality scoring

**Key Fields**:
- score_id (PK)
- image_id (unique)
- dataset_code
- clarity_score (0-100)
- completeness_score (0-100)
- quality_score (0-100)
- problem_tags (JSONB array)
- cleaning_suggestion (text)
- model_version
- score_time

**Scoring Rules**:
- quality_score = clarity_score × 0.4 + completeness_score × 0.6
- Grading: A(90-100), B(70-89), C(50-69), D(30-49), E(0-29)

**Decision Rules**:
- ≥80: Auto-adopt
- <50: Auto-discard
- 50-79: Manual review

**Problem Tags**:
- 模糊, 遮挡, 过曝, 欠曝, 角度不正, 背景杂乱, 主体不明确, 色彩失真, 尺寸异常, 格式错误

---

### 9. DataQualityAssessment (数据质量评估)
**Purpose**: 5-dimension quality assessment

**Key Fields**:
- assessment_id (PK)
- dataset_id
- dataset_code
- completeness_score (0-100)
- accuracy_score (0-100)
- consistency_score (0-100)
- timeliness_score (0-100)
- uniqueness_score (0-100)
- overall_score (0-100)
- quality_report (JSONB)
- assessment_time (unique per dataset)

**5 Dimensions**:
1. **Completeness**: (non-empty / total_fields) × 100
2. **Accuracy**: (correct / total) × 100
3. **Consistency**: (consistent / total) × 100
4. **Timeliness**: (timely / total) × 100
5. **Uniqueness**: (unique / total) × 100

**Overall Score**: Weighted average of 5 dimensions
**Grade**: A(90-100), B(80-89), C(70-79), D(60-69), E(0-59)

---

### 10. DataProcessTrace (数据处理跟踪)
**Purpose**: Responsibility chain processing trace

**Key Fields**:
- trace_id (PK)
- record_id (unique)
- dataset_code
- original_data (JSONB snapshot)
- processed_data (JSONB)
- process_nodes (JSONB array)
- total_duration_ms
- overall_status (0:失败, 1:成功)
- error_message
- create_time, update_time

**Process Nodes**:
1. Data Extraction (数据提取)
   - Batch read from materialized view
   - Validate format and completeness

2. Data Conversion (数据转换)
   - Map fields to DDD model
   - Standardization

3. Data Validation (数据验证)
   - Format validation
   - Business rule validation
   - Uniqueness check

4. Data Storage (数据入库)
   - Batch insert to temp table
   - Update statistics
   - Transaction control

**Tracking Format**:
```json
{
  "record_id": "unique_id",
  "dataset_code": "DS000001",
  "original_data": {...},
  "processed_data": {...},
  "chain_nodes": [
    {
      "node_name": "数据提取节点",
      "start_time": "2026-03-21 10:00:00",
      "end_time": "2026-03-21 10:00:01",
      "status": "成功",
      "duration_ms": 1000,
      "error_message": null,
      "metadata": {"batch_id": "batch_001", "record_count": 1000}
    },
    // ... 4 nodes total
  ],
  "total_duration_ms": 8000,
  "overall_status": "成功"
}
```

---

## Core Features (30+)

### 1. Dataset Management
- CRUD operations
- List with search (by code, name, plu)
- Status management
- Permission control (view only)

### 2. Category Management
- CRUD operations
- PLU merge, split, copy, delete
- Import from MinIO
- Import from other datasets
- Random sampling and scoring
- Tag binding
- Create new dataset from selected categories

### 3. Tag Management
- Multi-level tag hierarchy
- System vs user tags
- Tag CRUD
- Tag binding to categories/images
- Batch tag operations
- Tag statistics

### 4. Image Management
- List all images in shard (no pagination)
- Adopt/discard operations (keyboard shortcuts)
- Batch operations
- Cross-dataset operations in MinIO

### 5. AI Cleaning & Scoring
- AI quality analysis
- Problem detection
- Cleaning suggestions
- Auto-decision (≥80 adopt, <50 discard)
- Manual review queue (50-79)
- Quality sampling (10% random)

### 6. Data Quality Assessment
- 5-dimension assessment
- Quality report generation
- Quality trend analysis
- Grade calculation
- Alert on quality degradation

### 7. Data Export
- Async export
- Cleaned data only
- Image URLs only
- Compressed package
- Format: CSV, JSON, Excel, Parquet

### 8. Responsibility Chain Processing
- Extract → Convert → Validate → Store
- Complete trace recording
- Real-time progress monitoring
- Task observation system

### 9. Locking Mechanism
- Distributed locks (Redis)
- Category locking for large operations
- Lock timeout and auto-release
- Lock monitoring

### 10. Temporary Table Management
- Auto-create on dataset import
- Naming: dataset_temp_{dataset_code}
- Lifecycle management
- Manual cleanup
- Expiry checking (7 days)

### 11. Data Preview & EDA
- Dataset overview
- Sample preview (100 records)
- Field statistics
- Visualization analysis
- Interactive exploration

### 12. Task Center
- Real-time progress display
- Task status tracking
- Estimated completion time
- Historical query

### 13. Data Comparison
- Dataset comparison
- Version comparison
- Field value comparison
- Visual comparison (charts)

### 14. Workflow Orchestration
- Visual designer
- Node types: import, clean, validate, export, condition, loop, parallel, human-review
- Flow control: sequential, conditional, loop, parallel
- Trigger: manual, scheduled, event-based, API

### 15. Monitoring & Alerts
- Performance monitoring (CPU, memory, disk, network)
- Business monitoring (processing speed, task success rate, response time)
- Data monitoring (data volume, quality metrics)
- Alert rules: threshold, trend, event, composite
- Notifications: email, SMS, DingTalk/WeChat, in-app

### 16. Data Lineage
- Source tracking
- Transform tracking
- Data flow visualization
- Impact analysis
- Source tracing
- Problem localization

### 17. Data Security
- Data masking
- Access audit
- Permission control (dataset, field, record, operation level)

### 18. Data Augmentation
- Geometric transforms: rotate, scale, flip, crop
- Color transforms: brightness, contrast, saturation, hue
- Noise: Gaussian, salt-pepper, blur, sharpen
- Hybrid augmentation

### 19. Intelligent Recommendations
- Cleaning rule recommendations
- Exception handling suggestions
- Similar dataset recommendations
- Hot dataset recommendations

### 20. Notifications
- Multi-channel notifications
- Task completion, error alerts, review notifications, system announcements
- Subscription management

---

## Technical Stack

### Frontend
- Framework: Vue 3 + TypeScript + Vite
- UI Library: Element Plus
- State Management: Pinia
- Routing: Vue Router 4
- HTTP Client: Axios
- Charts: ECharts 5.x
- Reference: https://gitcode.com/yangzongzhuan/RuoYi-Vue3

### Backend
- Core Framework: Spring Boot 3.x
- Microservices: Spring Cloud Alibaba
- Persistence: MyBatis-Plus 3.5.x
- Connection Pool: Druid 1.2.x
- Database: PostgreSQL 14.x
- Cache: Redis 7.x
- Message Queue: RocketMQ 5.x
- Object Storage: MinIO
- Distributed Transaction: Seata
- Service Discovery: Nacos
- Flow Control: Sentinel
- Tracing: SkyWalking
- Monitoring: Prometheus + Grafana
- API Docs: Swagger 3.x + Apifox sync

### Development Tools
- Version Control: Git
- Build Tool: Maven 3.9.x
- API Documentation: Swagger 3.x

### Deployment
- Containerization: Docker + Docker Compose
- Log Collection: ELK Stack (Elasticsearch + Logstash + Kibana)

---

## Deployment Architecture

### Test Environment (Recommended Starting Point)
- **Architecture**: Single monolithic application with folder-based DDD modules
- **Infra Services**: 1 instance each (PostgreSQL, Redis, MinIO, Nacos)
- **Business Service**: 1 instance, single monolithic application
- **Deployment**: Docker Compose

**Directory Structure**:
```
deploy/
├── .env
├── infrastructure/  # Docker Compose for infra
│   ├── docker-compose.yml
│   ├── postgres/init.sql
│   ├── minio/policies/
│   └── config/
├── app/             # Application deployment
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── config/
└── scripts/         # Deployment scripts
```

**Domain Module Structure** (inside monolith):
```
ruoyi-system/
├── domain/              # Domain entities
├── application/         # Application services
├── persistence/         # Infrastructure layer
└── interfaces/          # Interface layer

ruoyi-web/               # Controllers
ruoyi-common/            # Common services (lock, export, etc.)
```

### Production Environment
- Microservices architecture
- Multiple services (dataset, category, image, clean, export, tag, quality, auth, ai)
- Multiple instances per service
- PostgreSQL master-slave cluster
- Redis cluster
- MinIO distributed cluster

---

## Deployment Config

### Docker Compose (Test Environment)
```yaml
# Infrastructure (infrastructure/docker-compose.yml)
services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: data_cleaning
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./postgres/init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:7
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  minio:
    image: minio/minio
    environment:
      MINIO_ROOT_USER: admin
      MINIO_ROOT_PASSWORD: admin123
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
    command: server /data --console-address ":9001"

  nacos:
    image: nacos/nacos-server:v2.2.0
    environment:
      MODE: standalone
    ports:
      - "8848:8848"

volumes:
  postgres_data:
  redis_data:
  minio_data:
```

---

## Key Algorithms & Patterns

### 1. Snowflake ID Generation
Distributed ID generator supporting multiple strategies (snowflake, Redis auto-increment, database sequence)

### 2. Responsibility Chain Pattern
Sequential processing: Extract → Convert → Validate → Store

### 3. Distributed Lock
Redis-based with Lua script for atomicity

### 4. Cache Strategies
- Multi-level caching (L1: Caffeine, L2: Redis)
- Cache penetration protection (Bloom filter)
- Cache breakdown protection (distributed lock)
- Cache avalanche protection (random expiration)

### 5. Async Processing
- Spring @Async for async export
- RocketMQ for message queue
- Batch processing with Spring Batch

### 6. Partition Table Management
- Dynamic partition creation
- Automatic yearly partitions
- Partition expiration handling

---

## Performance Considerations

### Database
- Indexes on all foreign keys
- Partition tables for product_image
- Materialized views for data warehouse
- Connection pool: Druid with monitoring

### Application
- Streaming queries to avoid memory overflow
- Batch operations (1000 records at a time)
- Async processing for long-running tasks
- Cache: L1 (Caffeine) + L2 (Redis)

### Frontend
- Lazy loading
- Code splitting
- Image lazy loading
- Virtual scrolling for large lists

---

## Security Considerations

### Data
- SQL injection prevention (parameterized queries)
- XSS protection
- CSRF protection enabled
- Data masking for sensitive fields

### Access
- Authentication: Spring Security + JWT
- Authorization: Role-based + permission-based
- Field-level permissions
- Record-level permissions

### Compliance
- Audit logging for all data access
- Access control at dataset, field, record, operation levels
- Data masking and encryption for sensitive data

---

## Testing Strategy

### Unit Tests (Target: 80% coverage)
- Domain entities (JUnit 5 + MockK/ Mockito)
- Application services
- Utility classes
- Validation logic

### Integration Tests
- Repository layer
- Service layer
- API endpoints
- External service integration (AI, MinIO, Redis)

### E2E Tests
- Critical user flows (login, import, clean, export)
- Playwright for frontend E2E

### Test Tools
- Backend: JUnit 5, Mockito, Testcontainers, JaCoCo
- Frontend: Vitest, Playwright

---

## Estimated Effort

**Phases**:
- Phase 1: Analysis (completed)
- Phase 2: Database Schema: 2-3 days
- Phase 3: Domain Layer: 3-4 days
- Phase 4: Infrastructure Layer: 2-3 days
- Phase 5: Application Layer: 2-3 days
- Phase 6: Interface Layer: 2-3 days
- Phase 7: Core Services: 2-3 days
- Phase 8: Frontend: 5-7 days
- Phase 9: AI Integration: 2-3 days
- Phase 10: Quality Assessment: 1-2 days
- Phase 11: Testing: 3-4 days
- Phase 12: Documentation: 1-2 days

**Total Estimated**: 26-38 days (manual development)

**Automated Execution**: Same phases, automated via scripts

---

## Success Criteria

1. All 10 data models implemented with correct relationships
2. All 30+ features functional
3. Frontend and backend integrated
4. API endpoints documented
5. Test coverage >80%
6. Docker deployment working
7. Documentation complete
8. Performance benchmarks met
9. Security requirements satisfied
10. No critical bugs

---

## Dependencies & Constraints

### Required Services
- PostgreSQL 14+
- Redis 7+
- MinIO
- JDK 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.9+
- Git

### External APIs
- AI model API (for scoring)
- Email service (for notifications)
- SMS service (optional)
- DingTalk/WeChat (optional)

---

## Next Steps

**Immediate Actions**:
1. Execute PHASE 2: Database Schema Implementation
2. Generate SQL migration scripts
3. Set up partition tables
4. Create temporary table management functions

**Critical Path**:
Database → Domain Layer → Infrastructure → Application → Interface → Frontend

**Risk Areas**:
- Large data volume handling (10M+ records)
- AI API integration reliability
- Distributed lock consistency
- Async processing coordination

---

## Notes

- Technical specification provides comprehensive coverage
- Follow DDD principles strictly
- Prioritize core features (phases 2-10)
- Frontend can be developed in parallel with backend
- Testing integrated throughout, not just at the end
- Documentation kept current throughout development

---

**Analysis Completed**: 2026-03-21
**Analyzer**: Claude Code (superpowers:planning-with-files)
**Total Analysis Time**: Automated from spec reading
