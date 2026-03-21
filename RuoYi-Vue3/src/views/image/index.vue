<template>
  <div class="app-container">
    <!-- 筛选工具栏 -->
    <div class="filter-toolbar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="数据集">
          <el-select v-model="filterForm.datasetId" placeholder="请选择数据集" clearable>
            <el-option
              v-for="ds in datasets"
              :key="ds.datasetId"
              :label="ds.name"
              :value="ds.datasetId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="待清洗" :value="0" />
            <el-option label="已清洗" :value="1" />
            <el-option label="待审核" :value="2" />
            <el-option label="已通过" :value="3" />
            <el-option label="已拒绝" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">待清洗</div>
            <div class="stat-value pending">{{ statistics.pending || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">已清洗</div>
            <div class="stat-value cleaned">{{ statistics.cleaned || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">待审核</div>
            <div class="stat-value review">{{ statistics.review || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">已通过</div>
            <div class="stat-value approved">{{ statistics.approved || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table :data="images" v-loading="loading" stripe @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="imageMd5" label="图片MD5" width="150" show-overflow-tooltip />
      <el-table-column prop="shopcode" label="店铺编码" width="120" />
      <el-table-column prop="vendorcode" label="供应商编码" width="120" />
      <el-table-column prop="imageUrl" label="图片URL" min-width="200" show-overflow-tooltip />
      <el-table-column prop="imageStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.imageStatus)">
            {{ getStatusLabel(row.imageStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="qualityScore" label="质量评分" width="100" align="center">
        <template #default="{ row }">
          {{ row.qualityScore ? row.qualityScore.toFixed(1) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleView(row)">查看</el-button>
          <el-button link type="primary" @click="handleClean(row)" v-if="row.imageStatus === 0">
            清洗
          </el-button>
          <el-button link type="success" @click="handleApprove(row)" v-if="row.imageStatus === 2">
            通过
          </el-button>
          <el-button link type="danger" @click="handleReject(row)" v-if="row.imageStatus === 2">
            拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      class="pagination"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />

    <!-- 清洗对话框 -->
    <el-dialog v-model="cleanDialogVisible" title="清洗图片" width="500px">
      <el-form :model="cleanForm" ref="cleanFormRef" label-width="100px">
        <el-form-item label="问题类型">
          <el-checkbox-group v-model="cleanForm.problemTypes">
            <el-checkbox label="模糊">模糊</el-checkbox>
            <el-checkbox label="遮挡">遮挡</el-checkbox>
            <el-checkbox label="背景杂乱">背景杂乱</el-checkbox>
            <el-checkbox label="色彩失真">色彩失真</el-checkbox>
            <el-checkbox label="其他">其他</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="清洗备注">
          <el-input v-model="cleanForm.cleanRemark" type="textarea" rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cleanDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitClean">确定</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝图片" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因">
          <el-input v-model="rejectForm.reason" type="textarea" rows="3" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useImageStore } from '@/store/modules/image'
import { useDatasetStore } from '@/store/modules/dataset'

const imageStore = useImageStore()
const datasetStore = useDatasetStore()

const images = ref([])
const datasets = ref([])
const loading = ref(false)
const statistics = ref({})
const selectedImages = ref([])

const filterForm = reactive({
  datasetId: null,
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const cleanDialogVisible = ref(false)
const cleanFormRef = ref(null)
const cleanForm = reactive({
  imageId: null,
  problemTypes: [],
  cleanRemark: ''
})

const rejectDialogVisible = ref(false)
const rejectForm = reactive({
  imageId: null,
  reason: ''
})

const statusMap = {
  0: { label: '待清洗', type: 'warning' },
  1: { label: '已清洗', type: 'primary' },
  2: { label: '待审核', type: 'info' },
  3: { label: '已通过', type: 'success' },
  4: { label: '已拒绝', type: 'danger' }
}

const getStatusType = (status) => statusMap[status]?.type || 'info'
const getStatusLabel = (status) => statusMap[status]?.label || '未知'

const fetchDatasets = async () => {
  datasets.value = await datasetStore.fetchDatasets()
}

const fetchImages = async () => {
  loading.value = true
  try {
    const params = {
      ...filterForm,
      page: pagination.page - 1,
      size: pagination.size
    }
    images.value = await imageStore.fetchFilteredImages(params)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.page = 1
  fetchImages()
}

const handleReset = () => {
  filterForm.datasetId = null
  filterForm.status = null
  pagination.page = 1
  fetchImages()
}

const handleSizeChange = (size) => {
  pagination.size = size
  fetchImages()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchImages()
}

const handleSelectionChange = (selection) => {
  selectedImages.value = selection
}

const handleView = (row) => {
  // TODO: 跳转到图片详情
  console.log('View image:', row.imageId)
}

const handleClean = (row) => {
  cleanForm.imageId = row.imageId
  cleanForm.problemTypes = []
  cleanForm.cleanRemark = ''
  cleanDialogVisible.value = true
}

const submitClean = async () => {
  const data = {
    problemTypes: cleanForm.problemTypes.join(','),
    cleanRemark: cleanForm.cleanRemark
  }
  await imageStore.clean(cleanForm.imageId, data)
  ElMessage.success('清洗成功')
  cleanDialogVisible.value = false
  fetchImages()
}

const handleApprove = async (row) => {
  await imageStore.approve(row.imageId)
  ElMessage.success('审核通过')
  fetchImages()
}

const handleReject = (row) => {
  rejectForm.imageId = row.imageId
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

const submitReject = async () => {
  await imageStore.reject(rejectForm.imageId, { rejectReason: rejectForm.reason })
  ElMessage.success('已拒绝')
  rejectDialogVisible.value = false
  fetchImages()
}

onMounted(() => {
  fetchDatasets()
  fetchImages()
})
</script>

<style scoped>
.filter-toolbar {
  margin-bottom: 16px;
}

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
}

.stat-value.pending { color: #e6a23c; }
.stat-value.cleaned { color: #409eff; }
.stat-value.review { color: #909399; }
.stat-value.approved { color: #67c23a; }

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
