<template>
  <div class="dataset-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-grid"></div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="(stat, index) in statisticsCards" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
        <div class="stat-icon-wrap" :style="{ background: stat.gradient }">
          <svg viewBox="0 0 24 24" fill="none" v-html="stat.icon"></svg>
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
        <div class="stat-shine"></div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-panel">
      <div class="search-row">
        <div class="search-item">
          <label>数据集编码</label>
          <el-input
            v-model="queryParams.datasetCode"
            placeholder="请输入数据集编码"
            clearable
            @keyup.enter="handleQuery"
          >
            <template #prefix>
              <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </template>
          </el-input>
        </div>
        <div class="search-item">
          <label>数据集名称</label>
          <el-input
            v-model="queryParams.name"
            placeholder="请输入数据集名称"
            clearable
            @keyup.enter="handleQuery"
          >
            <template #prefix>
              <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </template>
          </el-input>
        </div>
        <div class="search-item search-item-date">
          <label>创建时间</label>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="handleQuery" class="btn-primary">
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
              <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12C21 16.9706 16.9706 21 12 21C9.69494 21 7.59227 20.1334 6 18.7083" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <path d="M3 4V10H10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            重置
          </el-button>
          <el-button type="success" @click="handleCreate">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            新建数据集
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        :data="datasets"
        v-loading="loading"
        :row-class-name="tableRowClassName"
        @row-click="handleRowClick"
      >
        <el-table-column prop="datasetCode" label="数据集编码" width="160" fixed>
          <template #default="{ row }">
            <span class="code-tag">{{ row.datasetCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="数据集名称" min-width="200">
          <template #default="{ row }">
            <div class="name-cell">
              <span class="name-text">{{ row.name || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="desc-text">{{ row.description || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="{ row }">
            <span class="status-badge" :class="`status-${row.status}`">
              <span class="status-dot"></span>
              {{ getStatusLabel(row.status) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="totalImageCount" label="总图片数" width="110" align="center">
          <template #default="{ row }">
            <span class="num-cell">{{ row.totalImageCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="cleanedImageCount" label="已清洗数" width="110" align="center">
          <template #default="{ row }">
            <span class="num-cell success">{{ row.cleanedImageCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">
            <span class="time-cell">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click.stop="handleView(row)" class="action-btn">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                </svg>
                查看
              </el-button>
              <el-button link type="warning" @click.stop="handleInitialize(row)" v-if="row.status === 0" class="action-btn">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                初始化
              </el-button>
              <el-button link type="danger" @click.stop="handleStartCleaning(row)" v-if="row.status === 1" class="action-btn">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M12 2V6M12 18V22M4.93 4.93L7.76 7.76M16.24 16.24L19.07 19.07M2 12H6M18 12H22M4.93 19.07L7.76 16.24M16.24 7.76L19.07 4.93" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                开始清洗
              </el-button>
              <el-button link type="success" @click.stop="handleApprove(row)" v-if="row.status === 2" class="action-btn">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M22 11.08V12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C14.165 2 16.1382 2.82108 17.73 4.14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <path d="M22 4L12 14.01L9 11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                审核通过
              </el-button>
              <el-button link type="info" @click.stop="handlePublish(row)" v-if="row.status === 3" class="action-btn">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M21 15V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <path d="M17 8L12 3L7 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M12 3V15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                发布
              </el-button>
              <el-button link type="warning" @click.stop="handleArchive(row)" v-if="row.status === 4" class="action-btn">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M21 8V21H3V3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M1 3H23V8H1V3Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M10 12H14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                归档
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="totalCount > 0">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </div>

    <!-- 创建数据集对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建数据集"
      width="500px"
      class="create-dialog"
      :close-on-click-modal="false"
    >
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="数据集名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入数据集名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" rows="3" placeholder="请输入数据集描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCreate" :loading="createLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useDatasetStore } from '@/store/modules/dataset'

const datasetStore = useDatasetStore()

const datasets = ref([])
const loading = ref(false)
const totalCount = ref(0)
const createDialogVisible = ref(false)
const createFormRef = ref(null)
const createLoading = ref(false)
const dateRange = ref([])

const createForm = ref({
  name: '',
  description: ''
})

const createRules = {
  name: [{ required: true, message: '请输入数据集名称', trigger: 'blur' }]
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  datasetCode: '',
  name: '',
  beginTime: '',
  endTime: ''
})

const statisticsCards = computed(() => [
  {
    label: '数据集总数',
    value: statistics.value.total || 0,
    gradient: 'linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)',
    icon: '<path d="M21 16V8a2 2 0 00-1-1.73l-7-4a2 2 0 00-2 0l-7 4A2 2 0 003 8v8a2 2 0 001 1.73l7 4a2 2 0 002 0l7-4A2 2 0 0021 16z" stroke="currentColor" stroke-width="2"/><polyline points="3.27,6.96 12,12.01 20.73,6.96" stroke="currentColor" stroke-width="2"/><line x1="12" y1="22.08" x2="12" y2="12" stroke="currentColor" stroke-width="2"/>'
  },
  {
    label: '清洗中',
    value: statistics.value.cleaning || 0,
    gradient: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
    icon: '<path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>'
  },
  {
    label: '待审核',
    value: statistics.value.approved || 0,
    gradient: 'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)',
    icon: '<circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M12 6V12L16 14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>'
  },
  {
    label: '已发布',
    value: statistics.value.published || 0,
    gradient: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
    icon: '<path d="M22 11.08V12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C14.165 2 16.1382 2.82108 17.73 4.14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M22 4L12 14.01L9 11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>'
  }
])

const statistics = ref({
  total: 0,
  cleaning: 0,
  approved: 0,
  published: 0
})

const statusMap = {
  0: { label: '已创建', class: 'created' },
  1: { label: '待清洗', class: 'pending' },
  2: { label: '清洗中', class: 'cleaning' },
  3: { label: '已审核', class: 'approved' },
  4: { label: '已发布', class: 'published' },
  5: { label: '已归档', class: 'archived' },
  6: { label: '已删除', class: 'deleted' }
}

const getStatusLabel = (status) => {
  return statusMap[status]?.label || '未知'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleDateChange = (val) => {
  if (val && val.length === 2) {
    queryParams.beginTime = val[0]
    queryParams.endTime = val[1]
  } else {
    queryParams.beginTime = ''
    queryParams.endTime = ''
  }
}

const fetchDatasets = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    }
    if (queryParams.datasetCode) params.datasetCode = queryParams.datasetCode
    if (queryParams.name) params.name = queryParams.name
    if (queryParams.beginTime) params.beginTime = queryParams.beginTime
    if (queryParams.endTime) params.endTime = queryParams.endTime

    const result = await datasetStore.fetchDatasets(params)
    datasets.value = result

    // 从响应头或响应中获取总数
    if (result.total || result.count) {
      totalCount.value = result.total || result.count
    }
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    const stats = await datasetStore.fetchStatistics()
    if (stats) {
      statistics.value = stats
    }
  } catch (e) {
    console.error('Failed to fetch statistics:', e)
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  fetchDatasets()
}

const handleReset = () => {
  queryParams.datasetCode = ''
  queryParams.name = ''
  queryParams.beginTime = ''
  queryParams.endTime = ''
  queryParams.pageNum = 1
  dateRange.value = []
  fetchDatasets()
}

const handleCreate = () => {
  createForm.value = { name: '', description: '' }
  createDialogVisible.value = true
}

const submitCreate = async () => {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      createLoading.value = true
      try {
        await datasetStore.createDataset(createForm.value)
        ElMessage.success('创建成功')
        createDialogVisible.value = false
        fetchDatasets()
        fetchStatistics()
      } catch (e) {
        ElMessage.error(e.message || '创建失败')
      } finally {
        createLoading.value = false
      }
    }
  })
}

const handleView = (row) => {
  ElMessage.info(`查看数据集: ${row.name || row.datasetCode}`)
}

const handleInitialize = async (row) => {
  try {
    await datasetStore.initialize(row.datasetId)
    ElMessage.success('初始化成功')
    fetchDatasets()
    fetchStatistics()
  } catch (e) {
    ElMessage.error(e.message || '初始化失败')
  }
}

const handleStartCleaning = async (row) => {
  try {
    await datasetStore.startCleaning(row.datasetId)
    ElMessage.success('开始清洗')
    fetchDatasets()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleApprove = async (row) => {
  try {
    await datasetStore.approve(row.datasetId)
    ElMessage.success('审核通过')
    fetchDatasets()
    fetchStatistics()
  } catch (e) {
    ElMessage.error(e.message || '审核失败')
  }
}

const handlePublish = async (row) => {
  try {
    await datasetStore.publish(row.datasetId)
    ElMessage.success('发布成功')
    fetchDatasets()
    fetchStatistics()
  } catch (e) {
    ElMessage.error(e.message || '发布失败')
  }
}

const handleArchive = async (row) => {
  try {
    await datasetStore.archive(row.datasetId)
    ElMessage.success('归档成功')
    fetchDatasets()
    fetchStatistics()
  } catch (e) {
    ElMessage.error(e.message || '归档失败')
  }
}

const handleRowClick = (row) => {
  // 可以添加行点击事件
}

const tableRowClassName = ({ row }) => {
  return ''
}

onMounted(() => {
  fetchDatasets()
  fetchStatistics()
})
</script>

<style lang="scss" scoped>
.dataset-container {
  padding: 24px;
  min-height: calc(100vh - 120px);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;

  .bg-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.15;

    &.bg-orb-1 {
      width: 400px;
      height: 400px;
      background: #06b6d4;
      top: -100px;
      right: -100px;
    }

    &.bg-orb-2 {
      width: 300px;
      height: 300px;
      background: #8b5cf6;
      bottom: -50px;
      left: -50px;
    }
  }

  .bg-grid {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px);
    background-size: 40px 40px;
  }
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.5s ease-out backwards;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    border-color: rgba(6, 182, 212, 0.3);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
  }

  .stat-icon-wrap {
    width: 56px;
    height: 56px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    svg {
      width: 28px;
      height: 28px;
      color: white;
    }
  }

  .stat-content {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .stat-value {
      font-size: 28px;
      font-weight: 700;
      color: #ffffff;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.5);
    }
  }

  .stat-shine {
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
    transition: left 0.5s ease;
  }

  &:hover .stat-shine {
    left: 100%;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 搜索栏 */
.search-panel {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  position: relative;
  z-index: 1;

  .search-row {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    align-items: flex-end;
  }

  .search-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
    min-width: 180px;

    &.search-item-date {
      min-width: 280px;
    }

    label {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.7);
      font-weight: 500;
    }

    :deep(.el-input__wrapper) {
      background: rgba(255, 255, 255, 0.05) !important;
      border: 1px solid rgba(255, 255, 255, 0.1) !important;
      border-radius: 10px !important;
      box-shadow: none !important;
      padding: 0 16px !important;
      height: 40px;
      transition: all 0.25s ease;

      &:hover {
        border-color: rgba(255, 255, 255, 0.2) !important;
        background: rgba(255, 255, 255, 0.08) !important;
      }

      &.is-focus {
        border-color: #06b6d4 !important;
        box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.15) !important;
      }

      input {
        color: #ffffff;
        font-size: 14px;
      }
    }

    :deep(.el-date-editor) {
      width: 100% !important;

      .el-range-input {
        color: #ffffff;
        font-size: 13px;
      }

      .el-range-separator {
        color: rgba(255, 255, 255, 0.4);
      }
    }
  }

  .search-actions {
    display: flex;
    gap: 12px;
    margin-left: auto;

    .btn-primary {
      background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%) !important;
      border: none !important;
      color: white;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 6px;

      svg {
        width: 16px;
        height: 16px;
      }

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(6, 182, 212, 0.35);
      }
    }

    .el-button {
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.1);
      color: rgba(255, 255, 255, 0.7);
      display: flex;
      align-items: center;
      gap: 6px;
      height: 40px;
      padding: 0 16px;

      svg {
        width: 16px;
        height: 16px;
      }

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        border-color: rgba(255, 255, 255, 0.2);
        color: #ffffff;
      }

      &[type="success"] {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        border: none;
        color: white;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(16, 185, 129, 0.35);
        }
      }
    }
  }
}

/* 数据表格 */
.table-container {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  padding: 20px;
  position: relative;
  z-index: 1;

  :deep(.el-table) {
    background: transparent;
    --el-table-bg-color: transparent;
    --el-table-tr-bg-color: transparent;
    --el-table-header-bg-color: rgba(255, 255, 255, 0.03);
    --el-table-row-hover-bg-color: rgba(6, 182, 212, 0.05);
    --el-table-border-color: rgba(255, 255, 255, 0.06);
    --el-table-text-color: rgba(255, 255, 255, 0.85);
    --el-table-header-text-color: rgba(255, 255, 255, 0.5);
    font-size: 13px;

    .el-table__header th {
      font-weight: 600;
      padding: 14px 0;
    }

    .el-table__row {
      cursor: pointer;
      transition: all 0.2s ease;

      td {
        padding: 14px 0;
      }
    }

    .el-table__empty-block {
      background: transparent;
    }
  }

  .code-tag {
    font-family: 'SF Mono', 'Fira Code', monospace;
    font-size: 12px;
    color: #06b6d4;
    background: rgba(6, 182, 212, 0.1);
    padding: 4px 8px;
    border-radius: 6px;
    letter-spacing: 0.5px;
  }

  .name-cell {
    .name-text {
      color: #ffffff;
      font-weight: 500;
    }
  }

  .desc-text {
    color: rgba(255, 255, 255, 0.5);
    font-size: 12px;
  }

  .status-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 5px 10px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;

    .status-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
    }

    &.status-0 {
      background: rgba(107, 114, 128, 0.2);
      color: #9ca3af;
      .status-dot { background: #9ca3af; }
    }
    &.status-1 {
      background: rgba(245, 158, 11, 0.2);
      color: #f59e0b;
      .status-dot { background: #f59e0b; }
    }
    &.status-2 {
      background: rgba(6, 182, 212, 0.2);
      color: #06b6d4;
      .status-dot { background: #06b6d4; }
    }
    &.status-3 {
      background: rgba(139, 92, 246, 0.2);
      color: #8b5cf6;
      .status-dot { background: #8b5cf6; }
    }
    &.status-4 {
      background: rgba(16, 185, 129, 0.2);
      color: #10b981;
      .status-dot { background: #10b981; }
    }
    &.status-5 {
      background: rgba(156, 163, 175, 0.2);
      color: #9ca3af;
      .status-dot { background: #9ca3af; }
    }
    &.status-6 {
      background: rgba(239, 68, 68, 0.2);
      color: #ef4444;
      .status-dot { background: #ef4444; }
    }
  }

  .num-cell {
    font-weight: 600;
    color: #ffffff;
    font-variant-numeric: tabular-nums;

    &.success {
      color: #10b981;
    }
  }

  .time-cell {
    color: rgba(255, 255, 255, 0.5);
    font-size: 12px;
    font-variant-numeric: tabular-nums;
  }

  .action-buttons {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    flex-wrap: wrap;
  }

  .action-btn {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 4px 8px;
    font-size: 12px;
    border-radius: 6px;
    transition: all 0.2s ease;

    svg {
      width: 14px;
      height: 14px;
    }

    &:hover {
      transform: translateY(-1px);
    }
  }
}

/* 分页 */
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);

  :deep(.el-pagination) {
    --el-pagination-bg-color: transparent;
    --el-pagination-text-color: rgba(255, 255, 255, 0.5);
    --el-pagination-button-disabled-bg-color: transparent;
    --el-pagination-hover-color: #06b6d4;

    .el-pager li {
      background: rgba(255, 255, 255, 0.05);
      border-radius: 8px;
      margin: 0 3px;
      min-width: 36px;
      height: 36px;
      line-height: 36px;

      &.is-active {
        background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
        color: white;
      }

      &:hover:not(.is-disabled) {
        background: rgba(6, 182, 212, 0.2);
      }
    }

    .btn-prev, .btn-next {
      background: rgba(255, 255, 255, 0.05);
      border-radius: 8px;
      min-width: 36px;
      height: 36px;
    }
  }
}

/* 创建对话框 */
:deep(.create-dialog) {
  .el-dialog {
    background: rgba(15, 12, 41, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 20px;

    .el-dialog__header {
      border-bottom: 1px solid rgba(255, 255, 255, 0.06);
      padding: 20px 24px;

      .el-dialog__title {
        color: #ffffff;
        font-weight: 600;
      }
    }

    .el-dialog__body {
      padding: 24px;
      color: rgba(255, 255, 255, 0.7);
    }

    .el-dialog__footer {
      border-top: 1px solid rgba(255, 255, 255, 0.06);
      padding: 16px 24px;
    }
  }

  .el-form-item__label {
    color: rgba(255, 255, 255, 0.7);
  }

  .el-input__wrapper {
    background: rgba(255, 255, 255, 0.05) !important;
    border: 1px solid rgba(255, 255, 255, 0.1) !important;
    border-radius: 10px !important;
    box-shadow: none !important;

    input {
      color: #ffffff;
    }
  }

  .el-textarea__inner {
    background: rgba(255, 255, 255, 0.05) !important;
    border: 1px solid rgba(255, 255, 255, 0.1) !important;
    border-radius: 10px !important;
    box-shadow: none !important;
    color: #ffffff;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    .el-button {
      border-radius: 10px;
      padding: 10px 20px;

      &[type="primary"] {
        background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
        border: none;
      }
    }
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .dataset-container {
    padding: 16px;
  }

  .search-panel {
    .search-row {
      flex-direction: column;

      .search-item {
        width: 100%;
      }

      .search-actions {
        width: 100%;
        margin-left: 0;
        flex-wrap: wrap;
      }
    }
  }
}
</style>
