<template>
  <el-scrollbar
    ref="scrollContainer"
    :vertical="false"
    class="scroll-container"
    @wheel.prevent="handleScroll"
  >
    <slot />
  </el-scrollbar>
</template>

<script setup>
import { ref, computed, getCurrentInstance, onMounted, onBeforeUnmount } from 'vue'
import useTagsViewStore from '@/store/modules/tagsView'

const tagAndTagSpacing = ref(4)
const { proxy } = getCurrentInstance()

const scrollWrapper = computed(() => proxy.$refs.scrollContainer.$refs.wrapRef)

onMounted(() => {
  if (scrollWrapper.value) {
    scrollWrapper.value.addEventListener('scroll', emitScroll, true)
  }
})

onBeforeUnmount(() => {
  if (scrollWrapper.value) {
    scrollWrapper.value.removeEventListener('scroll', emitScroll)
  }
})

function handleScroll(e) {
  const eventDelta = e.wheelDelta || -e.deltaY * 40
  const $scrollWrapper = scrollWrapper.value
  if ($scrollWrapper) {
    $scrollWrapper.scrollLeft = $scrollWrapper.scrollLeft + eventDelta / 4
  }
}

const emit = defineEmits(['scroll'])
const emitScroll = () => {
  emit('scroll')
}

const tagsViewStore = useTagsViewStore()
const visitedViews = computed(() => tagsViewStore.visitedViews)

function moveToTarget(currentTag) {
  if (!proxy.$refs.scrollContainer) return
  const $container = proxy.$refs.scrollContainer.$el
  if (!$container) return
  const $containerWidth = $container.offsetWidth
  const $scrollWrapper = scrollWrapper.value
  if (!$scrollWrapper) return

  let firstTag = null
  let lastTag = null

  // find first tag and last tag
  if (visitedViews.value.length > 0) {
    firstTag = visitedViews.value[0]
    lastTag = visitedViews.value[visitedViews.value.length - 1]
  }

  if (firstTag === currentTag) {
    $scrollWrapper.scrollLeft = 0
  } else if (lastTag === currentTag) {
    $scrollWrapper.scrollLeft = $scrollWrapper.scrollWidth - $containerWidth
  } else {
    const tagListDom = document.getElementsByClassName('tags-view-item')
    const currentIndex = visitedViews.value.findIndex(item => item === currentTag)
    let prevTag = null
    let nextTag = null
    
    // 查找当前标签、前一个标签和后一个标签
    let currentTagDom = null
    for (const k in tagListDom) {
      if (k !== 'length' && Object.hasOwnProperty.call(tagListDom, k)) {
        const tagDom = tagListDom[k]
        if (tagDom.dataset.path === currentTag.path) {
          currentTagDom = tagDom
        }
        if (currentIndex > 0 && tagDom.dataset.path === visitedViews.value[currentIndex - 1].path) {
          prevTag = tagDom
        }
        if (currentIndex < visitedViews.value.length - 1 && tagDom.dataset.path === visitedViews.value[currentIndex + 1].path) {
          nextTag = tagDom
        }
      }
    }

    // 确保找到当前标签DOM元素
    if (currentTagDom) {
      // 计算当前标签的位置
      const currentTagOffsetLeft = currentTagDom.offsetLeft
      const currentTagWidth = currentTagDom.offsetWidth
      
      // 计算当前标签的中心位置
      const currentTagCenter = currentTagOffsetLeft + currentTagWidth / 2
      // 计算容器的中心位置
      const containerCenter = $scrollWrapper.scrollLeft + $containerWidth / 2
      
      // 如果当前标签的中心不在容器的中心范围内，调整滚动位置
      if (currentTagCenter < containerCenter - 50 || currentTagCenter > containerCenter + 50) {
        // 计算目标滚动位置，使当前标签居中
        let targetScrollLeft = currentTagOffsetLeft - ($containerWidth / 2 - currentTagWidth / 2)
        // 确保滚动位置不会小于0
        targetScrollLeft = Math.max(0, targetScrollLeft)
        // 确保滚动位置不会超过最大滚动范围
        targetScrollLeft = Math.min(targetScrollLeft, $scrollWrapper.scrollWidth - $containerWidth)
        $scrollWrapper.scrollLeft = targetScrollLeft
      }
    }
  }
}

defineExpose({
  moveToTarget,
})
</script>

<style lang='scss' scoped>
.scroll-container {
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  width: 100%;
  :deep(.el-scrollbar__bar) {
    bottom: 0px;
  }
  :deep(.el-scrollbar__wrap) {
    height: 39px;
  }
}
</style>