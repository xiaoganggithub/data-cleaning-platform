<template>
  <div :class="classObj" class="app-wrapper" :style="{ '--current-color': theme }">
    <div class="app-background">
      <div class="bg-gradient"></div>
      <div class="bg-particles">
        <div v-for="n in 8" :key="n" class="bg-particle"></div>
      </div>
      <div class="bg-glow"></div>
    </div>
    <div v-if="device === 'mobile' && sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <sidebar v-if="!sidebar.hide" class="sidebar-container" />
    <div :class="{ hasTagsView: needTagsView, sidebarHide: sidebar.hide }" class="main-container">
      <div :class="{ 'fixed-header': fixedHeader }">
        <navbar @setLayout="setLayout" />
        <tags-view v-if="needTagsView" />
      </div>
      <app-main />
      <settings ref="settingRef" />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, watchEffect } from 'vue'
import { useWindowSize } from '@vueuse/core'
import Sidebar from './components/Sidebar/index.vue'
import { AppMain, Navbar, Settings, TagsView } from './components'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'

const settingsStore = useSettingsStore()
const theme = computed(() => settingsStore.theme)
const sidebar = computed(() => useAppStore().sidebar)
const device = computed(() => useAppStore().device)
const needTagsView = computed(() => settingsStore.tagsView)
const fixedHeader = computed(() => settingsStore.fixedHeader)

const classObj = computed(() => ({
  hideSidebar: !sidebar.value.opened,
  openSidebar: sidebar.value.opened,
  withoutAnimation: sidebar.value.withoutAnimation,
  mobile: device.value === 'mobile'
}))

const { width, height } = useWindowSize()
const WIDTH = 992

watch(() => device.value, () => {
  if (device.value === 'mobile' && sidebar.value.opened) {
    useAppStore().closeSideBar({ withoutAnimation: false })
  }
})

watchEffect(() => {
  if (width.value - 1 < WIDTH) {
    useAppStore().toggleDevice('mobile')
    useAppStore().closeSideBar({ withoutAnimation: true })
  } else {
    useAppStore().toggleDevice('desktop')
  }
})

function handleClickOutside() {
  useAppStore().closeSideBar({ withoutAnimation: false })
}

const settingRef = ref(null)
function setLayout() {
  settingRef.value.openSetting()
}
</script>

<style lang="scss" scoped>
@use "@/assets/styles/mixin.scss" as mix;
@use "@/assets/styles/variables.module.scss" as vars;

.app-wrapper {
  @include mix.clearfix;
  position: relative;
  height: 100%;
  width: 100%;
}

.app-background {
  position: fixed;
  inset: 0;
  z-index: -1;
  overflow: hidden;
  pointer-events: none;

  .bg-gradient {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  }

  .bg-particles {
    position: absolute;
    inset: 0;

    .bg-particle {
      position: absolute;
      width: 4px;
      height: 4px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      animation: float-particle 25s infinite ease-in-out;

      &:nth-child(1) { left: 5%; animation-delay: 0s; animation-duration: 30s; }
      &:nth-child(2) { left: 15%; animation-delay: 2s; animation-duration: 28s; }
      &:nth-child(3) { left: 25%; animation-delay: 4s; animation-duration: 32s; }
      &:nth-child(4) { left: 40%; animation-delay: 1s; animation-duration: 26s; }
      &:nth-child(5) { left: 55%; animation-delay: 3s; animation-duration: 24s; }
      &:nth-child(6) { left: 70%; animation-delay: 5s; animation-duration: 30s; }
      &:nth-child(7) { left: 80%; animation-delay: 2.5s; animation-duration: 28s; }
      &:nth-child(8) { left: 90%; animation-delay: 4.5s; animation-duration: 26s; }
    }
  }

  .bg-glow {
    position: absolute;
    width: 800px;
    height: 800px;
    background: radial-gradient(circle, rgba(6, 182, 212, 0.08) 0%, transparent 70%);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    animation: pulse-glow 10s ease-in-out infinite;
  }
}

@keyframes float-particle {
  0%, 100% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
    transform: translateY(90vh) scale(1);
  }
  90% {
    opacity: 0.8;
    transform: translateY(10vh) scale(0.8);
  }
  100% {
    transform: translateY(0vh) scale(0);
    opacity: 0;
  }
}

@keyframes pulse-glow {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.3); opacity: 0.8; }
}

.main-container:has(.fixed-header) {
  height: 100vh;
  overflow: hidden;
}

.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 9;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.hideSidebar {
  .main-container {
    transition: padding-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

.openSidebar {
  .main-container {
    transition: padding-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

.sidebarHide {
  .main-container {
    transition: padding-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

.withoutAnimation {
  .main-container,
  .sidebar-container {
    transition: none;
  }
}

.mobile.openSidebar {
  position: fixed;
  top: 0;
}
</style>
