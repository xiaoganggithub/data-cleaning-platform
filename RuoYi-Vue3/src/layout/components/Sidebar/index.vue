<template>
  <div :class="{ 'has-logo': showLogo }" class="sidebar-container">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper" :thumb-gsap="false">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="'transparent'"
        :text-color="variables.sidebarText"
        :unique-opened="true"
        :active-text-color="variables.sidebarActiveText"
        :collapse-transition="false"
        mode="vertical"
        :class="['sidebar-menu', sideTheme]"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/assets/styles/variables.module.scss'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const route = useRoute()
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()

const sidebarRouters = computed(() => permissionStore.sidebarRouters)
const showLogo = computed(() => settingsStore.sidebarLogo)
const sideTheme = computed(() => settingsStore.sideTheme)
const theme = computed(() => settingsStore.theme)
const isCollapse = computed(() => !appStore.sidebar.opened)

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: linear-gradient(180deg, #0f0c29 0%, #1a1a2e 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 4px 0 30px rgba(0, 0, 0, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    right: 0;
    width: 1px;
    height: 100%;
    background: linear-gradient(180deg,
      transparent 0%,
      rgba(6, 182, 212, 0.3) 50%,
      transparent 100%
    );
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &:hover::before {
    opacity: 1;
  }
}

.scrollbar-wrapper {
  flex: 1;
  overflow-x: hidden !important;
  overflow-y: auto !important;

  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 3px;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }
}

.sidebar-menu {
  flex: 1;
  border: none;
  padding: 8px;

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    position: relative;
    height: 48px;
    line-height: 48px;
    margin: 4px 0;
    padding-left: 16px !important;
    padding-right: 16px !important;
    border-radius: 12px;
    color: rgba(255, 255, 255, 0.6);
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 0;
      background: linear-gradient(180deg, #06b6d4, #22d3ee);
      border-radius: 0 2px 2px 0;
      opacity: 0;
      transition: all 0.2s ease;
    }

    .menu-icon {
      margin-right: 12px;
      font-size: 18px;
      opacity: 0.7;
      transition: all 0.2s ease;
    }

    .sub-menu-icon {
      font-size: 14px;
      transition: transform 0.3s ease;
    }

    &:hover {
      color: #ffffff;
      background: rgba(255, 255, 255, 0.06) !important;

      &::before {
        opacity: 0.5;
        height: 20px;
      }

      .menu-icon {
        opacity: 1;
        transform: scale(1.1);
      }
    }

    &.is-active {
      color: #22d3ee !important;
      background: rgba(6, 182, 212, 0.12) !important;
      font-weight: 600;

      &::before {
        opacity: 1;
        height: 24px;
      }

      .menu-icon {
        opacity: 1;
        color: #22d3ee;
      }

      &::after {
        content: '';
        position: absolute;
        right: 8px;
        top: 50%;
        transform: translateY(-50%);
        width: 6px;
        height: 6px;
        background: #06b6d4;
        border-radius: 50%;
        box-shadow: 0 0 8px rgba(6, 182, 212, 0.6);
      }
    }
  }

  :deep(.el-sub-menu) {
    .el-sub-menu__title {
      .sub-menu-icon {
        transform: rotate(0deg);
        transition: transform 0.3s ease;
      }
    }

    &.is-active > .el-sub-menu__title {
      color: #22d3ee !important;
    }
  }

  :deep(.el-menu--inline) {
    background: transparent !important;
    border: none !important;
    padding-left: 16px;

    .el-menu-item {
      padding-left: 32px !important;
      height: 42px;
      line-height: 42px;
      margin: 2px 0;
      font-size: 13px;
      background: transparent !important;

      &::before {
        display: none;
      }

      &.is-active {
        background: rgba(6, 182, 212, 0.08) !important;
        color: #22d3ee !important;

        &::after {
          display: none;
        }
      }

      &:hover {
        background: rgba(255, 255, 255, 0.06) !important;
      }
    }
  }
}

:deep(.el-scrollbar__bar) {
  opacity: 0.3;

  &:hover {
    opacity: 0.5;
  }
}
</style>
