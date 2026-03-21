<template>
  <div class="app-container">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleCreateRoot">新建根标签</el-button>
      <el-button type="primary" @click="handleCreateChild" :disabled="!selectedTag">
        新建子标签
      </el-button>
      <el-select v-model="filterType" placeholder="按类型筛选" clearable style="margin-left: 10px;" @change="handleFilter">
        <el-option label="质量类" :value="0" />
        <el-option label="清晰度类" :value="1" />
        <el-option label="模糊类" :value="2" />
        <el-option label="遮挡类" :value="3" />
        <el-option label="背景类" :value="4" />
        <el-option label="其他" :value="9" />
      </el-select>
      <el-button @click="fetchTags">刷新</el-button>
    </div>

    <!-- 标签表格 -->
    <el-table
      :data="tags"
      v-loading="loading"
      stripe
      default-expand-all
      row-key="tagId"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @row-click="handleRowClick"
      :highlight-current-row="true"
    >
      <el-table-column prop="name" label="标签名称" min-width="150" />
      <el-table-column prop="tagCode" label="标签编码" width="120" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeTagType(row.type)">
            {{ getTypeLabel(row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="usageCount" label="使用次数" width="100" align="center" />
      <el-table-column prop="imageCount" label="图像数" width="100" align="center" />
      <el-table-column prop="priority" label="优先级" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'info'">
            {{ row.status === 0 ? '正常' : '已归档' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click.stop="handleEdit(row)">编辑</el-button>
          <el-button link type="warning" @click.stop="handleArchive(row)" v-if="row.status === 0">
            归档
          </el-button>
          <el-button link type="danger" @click.stop="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑标签对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="父标签" v-if="form.parentId">
          <el-input v-model="parentTagName" disabled />
        </el-form-item>
        <el-form-item label="标签类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择标签类型">
            <el-option label="质量类" :value="0" />
            <el-option label="清晰度类" :value="1" />
            <el-option label="模糊类" :value="2" />
            <el-option label="遮挡类" :value="3" />
            <el-option label="背景类" :value="4" />
            <el-option label="其他" :value="9" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="form.priority" :min="0" :max="99" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTagStore } from '@/store/modules/tag'

const tagStore = useTagStore()

const tags = ref([])
const loading = ref(false)
const selectedTag = ref(null)
const filterType = ref(null)
const dialogVisible = ref(false)
const dialogTitle = ref('新建标签')
const formRef = ref(null)
const isEdit = ref(false)

const form = reactive({
  tagId: null,
  parentId: null,
  type: 0,
  name: '',
  description: '',
  priority: 0
})

const parentTagName = ref('')

const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择标签类型', trigger: 'change' }]
}

const typeMap = {
  0: { label: '质量类', tagType: '' },
  1: { label: '清晰度类', tagType: 'success' },
  2: { label: '模糊类', tagType: 'warning' },
  3: { label: '遮挡类', tagType: 'danger' },
  4: { label: '背景类', tagType: 'info' },
  9: { label: '其他', tagType: '' }
}

const getTypeLabel = (type) => typeMap[type]?.label || '未知'
const getTypeTagType = (type) => typeMap[type]?.tagType || ''

const fetchTags = async () => {
  loading.value = true
  try {
    tags.value = await tagStore.fetchAll()
  } finally {
    loading.value = false
  }
}

const handleFilter = async () => {
  loading.value = true
  try {
    if (filterType.value !== null) {
      tags.value = await tagStore.fetchByType(filterType.value)
    } else {
      tags.value = await tagStore.fetchAll()
    }
  } finally {
    loading.value = false
  }
}

const handleRowClick = (row) => {
  selectedTag.value = row
}

const handleCreateRoot = () => {
  isEdit.value = false
  dialogTitle.value = '新建根标签'
  form.tagId = null
  form.parentId = null
  form.type = 0
  form.name = ''
  form.description = ''
  form.priority = 0
  parentTagName.value = ''
  dialogVisible.value = true
}

const handleCreateChild = () => {
  if (!selectedTag.value) {
    ElMessage.warning('请先选择一个标签')
    return
  }
  isEdit.value = false
  dialogTitle.value = '新建子标签'
  form.tagId = null
  form.parentId = selectedTag.value.tagId
  form.type = selectedTag.value.type
  form.name = ''
  form.description = ''
  form.priority = 0
  parentTagName.value = selectedTag.value.name
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑标签'
  form.tagId = row.tagId
  form.parentId = row.parentId
  form.type = row.type
  form.name = row.name
  form.description = row.description || ''
  form.priority = row.priority || 0
  parentTagName.value = row.parentId ? row.parentName : ''
  dialogVisible.value = true
}

const handleArchive = async (row) => {
  try {
    await ElMessageBox.confirm('确定要归档该标签吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await tagStore.archive(row.tagId)
    ElMessage.success('归档成功')
    fetchTags()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('归档失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该标签吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await tagStore.remove(row.tagId)
    ElMessage.success('删除成功')
    fetchTags()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) {
        await tagStore.updateTag(form.tagId, {
          name: form.name,
          description: form.description
        })
        ElMessage.success('更新成功')
      } else {
        if (form.parentId) {
          await tagStore.createChildTag({
            parentId: form.parentId,
            name: form.name,
            type: form.type,
            description: form.description
          })
        } else {
          await tagStore.createRootTag({
            name: form.name,
            type: form.type,
            description: form.description
          })
        }
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchTags()
    }
  })
}

onMounted(() => {
  fetchTags()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
</style>
