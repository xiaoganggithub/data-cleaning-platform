# Code Review Issues

## Status: Ongoing - Critical Issues Fixed

## Backend Issues Fixed

### 1. MyBatis-Plus Version Incompatibility
- **File**: `ruoyi-system/pom.xml`
- **Issue**: `mybatis-plus-boot-starter` version 3.5.5 was incompatible with Spring Boot 3.5.x
- **Fix**: Upgraded to `mybatis-plus-spring-boot3-starter` version 3.5.7
- **Status**: FIXED ✓

### 2. Duplicate DomainException Class
- **Files**:
  - `ruoyi-system/src/main/java/com/ruoyi/system/domain/entity/DomainException.java` (KEPT)
  - `ruoyi-system/src/main/java/com/ruoyi/system/domain/model/DomainException.java` (REMOVED)
- **Issue**: Two identical DomainException classes caused MyBatis alias conflict
- **Fix**: Removed `domain/model/DomainException.java`
- **Status**: FIXED ✓

### 3. XML Syntax Error in SysMenuMapper.xml
- **File**: `ruoyi-system/src/main/resources/mapper/system/SysMenuMapper.xml` line 15
- **Issue**: `property=query` without quotes
- **Fix**: Changed to `property="query" column="query"`
- **Status**: FIXED ✓

### 4. Dataset.publish() Called Wrong Method
- **File**: `DatasetApplicationService.java` line 104
- **Issue**: `publish()` called `dataset.approve()` instead of `dataset.publish()`
- **Fix**: Added `publish()` method to Dataset entity, updated application service to call it
- **Status**: FIXED ✓

## Frontend Issues Fixed

### 1. Missing `ref` Import
- **File**: `RuoYi-Vue3/src/views/index.vue` line 120
- **Issue**: `ref({...})` used but `ref` not imported from 'vue'
- **Fix**: Added `import { ref } from 'vue'`
- **Status**: FIXED ✓

### 2. Login Page Copyright Not Centered
- **File**: `RuoYi-Vue3/src/views/login.vue` line 612-624
- **Issue**: `left: 35%` hardcoded instead of centering
- **Fix**: Changed to `left: 50%; transform: translateX(-50%);`
- **Status**: FIXED ✓

### 3. Duplicate Style Block in login.vue
- **File**: `RuoYi-Vue3/src/views/login.vue`
- **Issue**: Two identical `<style lang="scss" scoped>` blocks (lines 143-629 and 738-1211)
- **Fix**: Removed duplicate style block
- **Status**: FIXED ✓

### 4. TagsView Creating 3-Layer Layout
- **File**: `RuoYi-Vue3/src/settings.js`
- **Issue**: `tagsView: true` created separate bar between navbar and content
- **Fix**: Set `tagsView: false` to show single toolbar
- **Status**: FIXED ✓

### 5. Image Tab Icon Missing
- **File**: `RuoYi-Vue3/src/assets/icons/svg/image.svg`
- **Issue**: No icon available for image tab
- **Fix**: Created image.svg icon
- **Status**: FIXED ✓

## Remaining Issues (Lower Priority)

### Backend
1. Domain Layer imports persistence PO - DDD violation (structural)
2. countByStatus() loads entire table into memory - needs GROUP BY SQL
3. @Data on entities - mutable equals/hashCode
4. Domain events never dispatched - dead feature
5. Exception type inconsistency (DomainException vs IllegalArgumentException)
6. Sidebar first-level menu expansion - Database menu config issue (parent menus need `alwaysShow: true`)

### Frontend
1. Password stored in cookies - security risk
2. Missing error handling in store mutations
3. Global mutable state in request.js
4. Inconsistent form handling patterns

## Verification Status

- Backend: Running on http://localhost:8080 ✓
- Frontend: Running on http://localhost:3001 ✓
- Maven Build: SUCCESS ✓
- Login page copyright: FIXED ✓
- TagsView (3-layer layout): FIXED (disabled via settings.js) ✓
- Image tab icon: CREATED image.svg ✓
- User dropdown: Already implemented in Navbar.vue ✓
- Duplicate login.vue styles: FIXED ✓
- Missing tag store module: FIXED (created tag.js) ✓
