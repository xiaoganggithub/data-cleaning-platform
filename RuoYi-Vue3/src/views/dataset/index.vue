<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">数据集总数</div>
            <div class="stat-value">{{ statistics.total || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">清洗中</div>
            <div class="stat-value">{{ statistics.cleaning || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">待审核</div>
            <div class="stat-value">{{ statistics.approved || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">已发布</div>
            <div class="stat-value">{{ statistics.published || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleCreate">新建数据集</el-button>
      <el-button @click="fetchDatasets">刷新</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="datasets" v-loading="loading" stripe>
      <el-table-column prop="datasetCode" label="数据集编码" width="120" />
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalImageCount" label="总图片数" width="100" align="center" />
      <el-table-column prop="cleanedImageCount" label="已清洗数" width="100" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" @click="handleInitialize(row)" v-if="row.status === 0">
            初始化
          </el-button>
          <el-button link type="primary" @click="handleStartCleaning(row)" v-if="row.status === 1">
            开始清洗
          </el-button>
          <el-button link type="success" @click="handleApprove(row)" v-if="row.status === 2">
            审核通过
          </el-button>
          <el-button link type="warning" @click="handleArchive(row)" v-if="row.status === 4">
            归档
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建数据集对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建数据集" width="500px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入数据集名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" rows="3" placeholder="请输入数据集描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useDatasetStore } from '@/store/modules/dataset'

const datasetStore = useDatasetStore()

const datasets = ref([])
const loading = ref(false)
const statistics = ref({})
const createDialogVisible = ref(false)
const createFormRef = ref(null)

const createForm = ref({
  name: '',
  description: ''
})

const createRules = {
  name: [{ required: true, message: '请输入数据集名称', trigger: 'blur' }]
}

const statusMap = {
  0: { label: '已创建', type: 'info' },
  1: { label: '待清洗', type: 'warning' },
  2: { label: '清洗中', type: 'primary' },
  3: { label: '已审核', type: 'success' },
  4: { label: '已发布', type: 'success' },
  5: { label: '已归档', type: 'info' },
  6: { label: '已删除', type: 'danger' }
}

const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

const getStatusLabel = (status) => {
  return statusMap[status]?.label || '未知'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const fetchDatasets = async () => {
  loading.value = true
  try {
    datasets.value = await datasetStore.fetchDatasets()
    await fetchStatistics()
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  statistics.value = await datasetStore.fetchStatistics()
}

const handleCreate = () => {
  createForm.value = { name: '', description: '' }
  createDialogVisible.value = true
}

const submitCreate = async () => {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      await datasetStore.createDataset(createForm.value)
      ElMessage.success('创建成功')
      createDialogVisible.value = false
      fetchDatasets()
    }
  })
}

const handleView = (row) => {
  // TODO: 跳转到详情页
  console.log('View dataset:', row.datasetId)
}

const handleInitialize = async (row) => {
  await datasetStore.initialize(row.datasetId)
  ElMessage.success('初始化成功')
  fetchDatasets()
}

const handleStartCleaning = async (row) => {
  await datasetStore.startCleaning(row.datasetId)
  ElMessage.success('开始清洗')
  fetchDatasets()
}

const handleApprove = async (row) => {
  await datasetStore.approve(row.datasetId)
  ElMessage.success('审核通过')
  fetchDatasets()
}

const handleArchive = async (row) => {
  await datasetStore.archive(row.datasetId)
  ElMessage.success('归档成功')
  fetchDatasets()
}

onMounted(() => {
  fetchDatasets()
})
</script>

<style scoped>
.statistics-cards {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.toolbar {
  margin-bottom: 16px;
}
</style>
