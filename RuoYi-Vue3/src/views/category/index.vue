<template>
  <div class="app-container">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleCreateRoot">新建根分类</el-button>
      <el-button type="primary" @click="handleCreateChild" :disabled="!selectedCategory">
        新建子分类
      </el-button>
      <el-button @click="fetchCategories">刷新</el-button>
    </div>

    <!-- 分类表格 -->
    <el-table
      :data="categories"
      v-loading="loading"
      stripe
      default-expand-all
      row-key="categoryId"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @row-click="handleRowClick"
      :highlight-current-row="true"
    >
      <el-table-column prop="name" label="分类名称" min-width="150" />
      <el-table-column prop="categoryCode" label="分类编码" width="120" />
      <el-table-column prop="imageCount" label="图像数量" width="100" align="center" />
      <el-table-column prop="childCount" label="子分类数" width="100" align="center" />
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
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

    <!-- 创建/编辑分类对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="父分类" v-if="form.parentId">
          <el-input v-model="parentCategoryName" disabled />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCategoryStore } from '@/store/modules/category'

const categoryStore = useCategoryStore()

const categories = ref([])
const loading = ref(false)
const selectedCategory = ref(null)
const dialogVisible = ref(false)
const dialogTitle = ref('新建分类')
const formRef = ref(null)
const isEdit = ref(false)

const form = reactive({
  categoryId: null,
  parentId: null,
  name: '',
  description: '',
  sortOrder: 0
})

const parentCategoryName = ref('')

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const fetchCategories = async () => {
  loading.value = true
  try {
    categories.value = await categoryStore.fetchCategories()
  } finally {
    loading.value = false
  }
}

const handleRowClick = (row) => {
  selectedCategory.value = row
}

const handleCreateRoot = () => {
  isEdit.value = false
  dialogTitle.value = '新建根分类'
  form.categoryId = null
  form.parentId = null
  form.name = ''
  form.description = ''
  form.sortOrder = 0
  parentCategoryName.value = ''
  dialogVisible.value = true
}

const handleCreateChild = () => {
  if (!selectedCategory.value) {
    ElMessage.warning('请先选择一个分类')
    return
  }
  isEdit.value = false
  dialogTitle.value = '新建子分类'
  form.categoryId = null
  form.parentId = selectedCategory.value.categoryId
  form.name = ''
  form.description = ''
  form.sortOrder = 0
  parentCategoryName.value = selectedCategory.value.name
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  form.categoryId = row.categoryId
  form.parentId = row.parentId
  form.name = row.name
  form.description = row.description || ''
  form.sortOrder = row.sortOrder || 0
  parentCategoryName.value = row.parentId ? row.parentName : ''
  dialogVisible.value = true
}

const handleArchive = async (row) => {
  try {
    await ElMessageBox.confirm('确定要归档该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await categoryStore.archive(row.categoryId)
    ElMessage.success('归档成功')
    fetchCategories()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('归档失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await categoryStore.remove(row.categoryId)
    ElMessage.success('删除成功')
    fetchCategories()
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
        await categoryStore.updateCategory(form.categoryId, {
          name: form.name,
          description: form.description
        })
        ElMessage.success('更新成功')
      } else {
        if (form.parentId) {
          await categoryStore.createChildCategory({
            parentId: form.parentId,
            name: form.name,
            description: form.description
          })
        } else {
          await categoryStore.createRootCategory({
            name: form.name,
            description: form.description
          })
        }
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchCategories()
    }
  })
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}
</style>
