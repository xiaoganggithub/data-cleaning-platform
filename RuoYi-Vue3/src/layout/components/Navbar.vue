<template>
  <div class="navbar" :class="'nav' + settingsStore.navType">
    <hamburger id="hamburger-container" :is-active="appStore.sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <breadcrumb v-if="settingsStore.navType == 1" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="settingsStore.navType == 2" id="topmenu-container" class="topmenu-container" />
    <template v-if="settingsStore.navType == 3">
      <logo v-show="settingsStore.sidebarLogo" :collapse="false"></logo>
      <top-bar id="topbar-container" class="topbar-container" />
    </template>

    <div class="right-menu">
      <template v-if="appStore.device !== 'mobile'">
        <header-search id="header-search" class="right-menu-item" />
        <screenfull id="screenfull" class="right-menu-item hover-effect" />
        <el-tooltip content="主题模式" effect="dark" placement="bottom">
          <div class="right-menu-item hover-effect theme-switch-wrapper" @click="toggleTheme">
            <svg-icon v-if="settingsStore.isDark" icon-class="sunny" />
            <svg-icon v-if="!settingsStore.isDark" icon-class="moon" />
          </div>
        </el-tooltip>
        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>

      <el-dropdown trigger="click" @command="handleCommand" placement="bottom-end">
        <div class="avatar-container">
          <div class="avatar-wrapper">
            <img :src="userStore.avatar" class="user-avatar" />
            <span class="user-nickname">{{ userStore.nickName }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </div>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>
              <router-link to="/user/profile" class="dropdown-link">
                <i class="el-icon-user"></i>
                <span>个人中心</span>
              </router-link>
            </el-dropdown-item>
            <el-dropdown-item command="setLayout" v-if="settingsStore.showSettings">
              <i class="el-icon-setting"></i>
              <span>布局设置</span>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <i class="el-icon-switch-button"></i>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import TopBar from './TopBar'
import Logo from './Sidebar/Logo'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import HeaderSearch from '@/components/HeaderSearch'
import useAppStore from '@/store/modules/app'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'

const appStore = useAppStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()

function toggleSideBar() {
  appStore.toggleSideBar()
}

function handleCommand(command) {
  switch (command) {
    case "setLayout":
      setLayout()
      break
    case "logout":
      logout()
      break
    default:
      break
  }
}

function setLayout() {
  useSettingsStore().updateSetting({ key: 'showSettings', value: true })
}

function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logOut().then(() => {
      location.href = '/index'
    })
  }).catch(() => { })
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 16px;
  background: rgba(15, 12, 41, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg,
      transparent 0%,
      rgba(6, 182, 212, 0.3) 50%,
      transparent 100%
    );
  }
}

.hamburger-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.06);
  }

  &:active {
    transform: scale(0.95);
  }
}

.breadcrumb-container,
.topmenu-container {
  flex: 1;
  margin-left: 12px;
}

.topbar-container {
  flex: 1;
}

.right-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.right-menu-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: rgba(255, 255, 255, 0.6);

  &:hover {
    background: rgba(255, 255, 255, 0.08);
    color: #ffffff;
  }

  &:active {
    transform: scale(0.95);
  }
}

.theme-switch-wrapper {
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #06b6d4 0%, #a78bfa 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
    border-radius: inherit;
  }

  &:hover::before {
    opacity: 0.15;
  }

  svg {
    position: relative;
    z-index: 1;
    font-size: 18px;
  }
}

.avatar-container {
  height: 40px;
  display: flex;
  align-items: center;
  cursor: pointer;

  &:hover {
    .avatar-wrapper {
      background: rgba(255, 255, 255, 0.08);
    }
  }
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  object-fit: cover;
}

.user-nickname {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-weight: 500;
}

.dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;

  &:hover {
    color: #22d3ee;
  }
}
</style>

<style lang="scss">
.el-dropdown-menu {
  background: rgba(15, 12, 41, 0.98) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
  padding: 8px !important;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5) !important;

  .el-dropdown-menu__item {
    padding: 10px 14px !important;
    border-radius: 8px !important;
    color: rgba(255, 255, 255, 0.7) !important;
    font-size: 14px !important;
    transition: all 0.2s ease !important;
    display: flex !important;
    align-items: center !important;
    gap: 8px !important;

    i {
      font-size: 16px;
      opacity: 0.7;
    }

    &:hover {
      background: rgba(6, 182, 212, 0.12) !important;
      color: #22d3ee !important;

      i {
        opacity: 1;
      }
    }

    .dropdown-link {
      color: inherit;

      &:hover {
        color: inherit;
      }
    }
  }

  .el-dropdown-menu__item--divided {
    border-top: 1px solid rgba(255, 255, 255, 0.06) !important;
    margin-top: 8px !important;
    padding-top: 14px !important;
  }
}
</style>
