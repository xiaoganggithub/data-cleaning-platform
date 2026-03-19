<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPaneRef" class="tags-view-wrapper" @scroll="handleScroll">
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :data-path="tag.path"
        :class="{ 'active': isActive(tag), 'has-icon': tagsIcon }"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        class="tags-view-item"
        :style="activeStyle(tag)"
        @click.middle="!isAffix(tag) ? closeSelectedTag(tag) : ''"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        <svg-icon v-if="tagsIcon && tag.meta && tag.meta.icon && tag.meta.icon !== '#'" :icon-class="tag.meta.icon" />
        {{ tag.title }}
        <span v-if="!isAffix(tag)" @click.prevent.stop="closeSelectedTag(tag)" class="tag-close">
          <close class="el-icon-close" />
        </span>
      </router-link>
    </scroll-pane>
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">
        <refresh-right /> 刷新页面
      </li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">
        <close /> 关闭当前
      </li>
      <li @click="closeOthersTags">
        <circle-close /> 关闭其他
      </li>
      <li v-if="!isFirstView()" @click="closeLeftTags">
        <back /> 关闭左侧
      </li>
      <li v-if="!isLastView()" @click="closeRightTags">
        <right /> 关闭右侧
      </li>
      <li @click="closeAllTags(selectedTag)">
        <circle-close /> 全部关闭
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ScrollPane from './ScrollPane'
import { getNormalPath } from '@/utils/ruoyi'
import useTagsViewStore from '@/store/modules/tagsView'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const visible = ref(false)
const top = ref(0)
const left = ref(0)
const selectedTag = ref({})
const affixTags = ref([])
const scrollPaneRef = ref(null)

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()

const visitedViews = computed(() => useTagsViewStore().visitedViews)
const routes = computed(() => usePermissionStore().routes)
const theme = computed(() => useSettingsStore().theme)
const tagsIcon = computed(() => useSettingsStore().tagsIcon)

watch(route, () => {
  addTags()
  moveToCurrentTag()
})

watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})

onMounted(() => {
  initTags()
  addTags()
})

function isActive(r) {
  return r.path === route.path
}

function activeStyle(tag) {
  if (!isActive(tag)) return {}
  return {
    "background-color": "rgba(6, 182, 212, 0.15)",
    "border-color": "rgba(6, 182, 212, 0.3)",
    "color": "#22d3ee"
  }
}

function isAffix(tag) {
  return tag.meta && tag.meta.affix
}

function filterAffixTags(routes) {
  let tags = []
  routes.forEach(route => {
    if (route.meta && route.meta.affix) {
      const tagPath = getNormalPath(route.fullPath)
      tags.push({
        fullPath: tagPath,
        path: tagPath,
        name: route.name,
        meta: { ...route.meta }
      })
    }
    if (route.children) {
      const childTags = filterAffixTags(route.children)
      if (childTags.length > 0) {
        tags = [...tags, ...childTags]
      }
    }
  })
  return tags
}

function initTags() {
  const affixTagsRes = filterAffixTags(routes.value)
  affixTags.value = affixTagsRes
  for (const tag of affixTagsRes) {
    if (tag.name) {
      useTagsViewStore().addVisitedView(tag)
    }
  }
}

function addTags() {
  const { name } = route
  if (name) {
    useTagsViewStore().addView(route)
  }
  return false
}

function moveToCurrentTag() {
  nextTick(() => {
    const tags = document.querySelectorAll('.tags-view-item')
    if (tags.length === 0) return

    for (const tag of tags) {
      const href = tag.getAttribute('href')
      const hash = window.location.hash
      if (href === hash) {
        const scrollPane = scrollPaneRef.value
        if (scrollPane) {
          scrollPane.moveToTarget(tag)
        }
        if (tag.getAttribute('data-path') === route.path) {
          tag.querySelector('.tag-close')?.focus()
        }
      }
    }
  })
}

function handleScroll() {
  closeMenu()
}

function openMenu(tag, event) {
  const menuMinWidth = 180
  const offsetLeft = event.currentTarget.getBoundingClientRect().left
  const offsetWidth = event.currentTarget.offsetWidth
  const maxLeft = offsetWidth - menuMinWidth
  const leftTemp = offsetLeft + offsetWidth - menuMinWidth

  visible.value = true
  selectedTag.value = tag
  if (leftTemp > 0) {
    left.value = offsetLeft
  } else {
    left.value = leftTemp
  }
  top.value = event.currentTarget.offsetTop + 20
}

function closeMenu() {
  visible.value = false
}

function isFirstView() {
  try {
    return selectedTag.value.path === visitedViews.value[1].path || selectedTag.value.path === '/index'
  } catch (err) {
    return false
  }
}

function isLastView() {
  try {
    return selectedTag.value.path === visitedViews.value[visitedViews.value.length - 1].path
  } catch (err) {
    return false
  }
}

function closeSelectedTag(view) {
  useTagsViewStore().delView(view).then(({ visitedViews: views }) => {
    if (isActive(view)) {
      toLastView(views)
    }
  })
}

function closeLeftTags() {
  useTagsViewStore().delLeftViews(selectedTag.value).then(({ visitedViews: views }) => {
    if (!views.some(v => v.path === route.path)) {
      toLastView(views)
    }
  })
}

function closeRightTags() {
  useTagsViewStore().delRightViews(selectedTag.value).then(({ visitedViews: views }) => {
    if (!views.some(v => v.path === route.path)) {
      toLastView(views)
    }
  })
}

function closeOthersTags() {
  useTagsViewStore().delOtherViews(selectedTag.value).then(({ visitedViews: views }) => {
    moveToCurrentTag()
  })
}

function closeAllTags(view) {
  useTagsViewStore().delAllViews().then(({ visitedViews: views }) => {
    if (views.some(v => v.path === route.path)) {
      return
    }
    toLastView(views)
  })
}

function toLastView(visitedViews) {
  const lastView = visitedViews.slice(-1)[0]
  if (lastView) {
    router.push(lastView.fullPath)
  } else {
    if (route.name === 'Dashboard') {
      router.replace({ path: '/redirect' + route.fullPath })
    } else {
      router.push('/')
    }
  }
}

function refreshSelectedTag(view) {
  useTagsViewStore().delView(view)
  const { fullPath } = view
  nextTick(() => {
    router.replace({
      path: '/redirect' + fullPath
    })
  })
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  position: fixed;
  top: 60px;
  right: 0;
  left: 0;
  z-index: 99;
  height: 40px;
  background: rgba(15, 12, 41, 0.6);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);

  .tags-view-wrapper {
    .tags-view-item {
      position: relative;
      display: inline-flex;
      align-items: center;
      height: 32px;
      margin: 4px 6px;
      padding: 0 12px;
      font-size: 13px;
      border: 1px solid transparent;
      border-radius: 8px;
      color: rgba(255, 255, 255, 0.6);
      background: rgba(255, 255, 255, 0.03);
      cursor: pointer;
      transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

      .tag-icon {
        margin-right: 6px;
        font-size: 14px;
      }

      .tag-close {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-left: 6px;
        width: 16px;
        height: 16px;
        border-radius: 50%;
        opacity: 0.5;
        transition: all 0.15s ease;

        &:hover {
          opacity: 1;
          background: rgba(239, 68, 68, 0.2);
          color: #ef4444;
        }
      }

      &.has-icon {
        padding-left: 8px;
      }

      &:hover {
        background: rgba(255, 255, 255, 0.08);
        color: rgba(255, 255, 255, 0.9);
      }

      &.active {
        background: rgba(6, 182, 212, 0.15);
        border-color: rgba(6, 182, 212, 0.3);
        color: #22d3ee;

        .tag-close {
          opacity: 0.7;

          &:hover {
            opacity: 1;
            background: rgba(239, 68, 68, 0.2);
            color: #ef4444;
          }
        }
      }
    }
  }

  .contextmenu {
    position: absolute;
    z-index: 200;
    margin: 0;
    padding: 6px;
    font-size: 13px;
    font-weight: 500;
    color: rgba(255, 255, 255, 0.8);
    background: rgba(15, 12, 41, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    box-shadow: 0 16px 32px rgba(0, 0, 0, 0.4);
    list-style: none;

    li {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 3px 0;
      padding: 8px 14px;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.15s ease;

      &:hover {
        background: rgba(6, 182, 212, 0.12);
        color: #22d3ee;
      }

      svg {
        width: 14px;
        height: 14px;
      }
    }
  }
}
</style>
