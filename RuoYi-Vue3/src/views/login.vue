<template>
  <div class="login-container">
    <div class="login-particles">
      <div v-for="n in 8" :key="n" class="particle"></div>
    </div>
    <div class="login-glow"></div>

    <div class="login-card" :class="{ 'is-loading': loading }">
      <div class="login-header">
        <div class="login-logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <p class="login-subtitle">瑶池智浣</p>
      </div>

      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form" size="large" @submit.prevent="handleLogin">
        <div class="form-item-wrapper">
          <label class="form-label-top">账号</label>
          <el-form-item prop="username" class="form-item">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
              clearable
            >
              <template #prefix>
                <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                  <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <div class="form-item-wrapper">
          <label class="form-label-top">密码</label>
          <el-form-item prop="password" class="form-item">
            <el-input
              v-model="loginForm.password"
              :type="showPassword ? 'text' : 'password'"
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter="handleLogin"
              clearable
            >
              <template #prefix>
                <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                  <path d="M7 11V7C7 4.23858 9.23858 2 12 2C14.7614 2 17 4.23858 17 7V11" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </template>
              <template #suffix>
                <button type="button" class="password-toggle" @click="showPassword = !showPassword">
                  <svg v-if="!showPassword" viewBox="0 0 24 24" fill="none">
                    <path d="M1 12C1 12 5 4 12 4C19 4 23 12 23 12C23 12 19 20 12 20C5 20 1 12 1 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none">
                    <path d="M17.94 17.94C16.2306 19.243 14.1491 19.9649 12 20C5 20 1 12 1 12C2.24389 9.68192 3.96914 7.65663 6.06 6.06M9.9 4.24C10.5883 4.0789 11.2931 3.99836 12 4C19 4 23 12 23 12C22.393 13.1356 21.6691 14.2048 20.84 15.19M14.12 14.12C13.8454 14.4148 13.5141 14.6512 13.1462 14.8151C12.7782 14.9791 12.3809 15.0673 11.9781 15.0744C11.5753 15.0815 11.1752 15.0074 10.8016 14.8565C10.4281 14.7056 10.0887 14.4809 9.80385 14.1961C9.51897 13.9112 9.29439 13.5718 9.14347 13.1982C8.99254 12.8246 8.91846 12.4246 8.92556 12.0217C8.93266 11.6189 9.02084 11.2217 9.18483 10.8537C9.34882 10.4858 9.58525 10.1544 9.88 9.88" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="3" y1="3" x2="21" y2="21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <div class="form-item-wrapper captcha-wrapper" v-if="captchaEnabled">
          <label class="form-label-top">验证码</label>
          <div class="captcha-row">
            <el-form-item prop="code" class="form-item captcha-item">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="请输入验证码"
                @keyup.enter="handleLogin"
                clearable
              >
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2"/>
                    <path d="M14.5 9.5L9.5 14.5M9.5 9.5L14.5 14.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </template>
              </el-input>
            </el-form-item>
            <div class="captcha-image" @click="getCode">
              <img :src="codeUrl" alt="验证码" v-if="codeUrl" />
              <div class="captcha-loading" v-else>
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <div class="form-options">
          <el-checkbox v-model="loginForm.rememberMe" @change="handleRememberMe">
            <span class="remember-text">记住密码</span>
          </el-checkbox>
          <router-link v-if="register" class="register-link" :to="'/register'">
            立即注册
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </router-link>
        </div>

        <el-button
          type="primary"
          native-type="submit"
          class="login-button"
          :class="{ 'is-loading': loading }"
          :loading="loading"
          size="large"
        >
          <span class="button-text" v-if="!loading">
            <span>登 录</span>
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
          <span class="button-loading" v-else>
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
          </span>
        </el-button>
      </el-form>
    </div>

    <div class="login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
}

.login-particles {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;

  .particle {
    position: absolute;
    width: 4px;
    height: 4px;
    background: rgba(255, 255, 255, 0.4);
    border-radius: 50%;
    animation: float 15s infinite ease-in-out;
    will-change: transform, opacity;

    &:nth-child(1) { left: 10%; animation-delay: 0s; }
    &:nth-child(2) { left: 20%; animation-delay: 1s; }
    &:nth-child(3) { left: 35%; animation-delay: 2s; }
    &:nth-child(4) { left: 50%; animation-delay: 0.5s; }
    &:nth-child(5) { left: 65%; animation-delay: 1.5s; }
    &:nth-child(6) { left: 80%; animation-delay: 2.5s; }
    &:nth-child(7) { left: 90%; animation-delay: 3s; }
    &:nth-child(8) { left: 5%; animation-delay: 3.5s; }
  }
}

@keyframes float {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
    transform: translateY(90vh) scale(1);
  }
  90% {
    opacity: 1;
    transform: translateY(10vh) scale(1);
  }
  100% {
    transform: translateY(0vh) scale(0);
    opacity: 0;
  }
}

.login-glow {
  position: absolute;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.15) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse-glow 8s ease-in-out infinite;
  pointer-events: none;
  will-change: transform, opacity;
}

@keyframes pulse-glow {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.8; }
}

.login-card {
  position: relative;
  width: 420px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow:
    0 25px 50px -12px rgba(0, 0, 0, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  animation: card-enter 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
  transform: translateY(30px) scale(0.95);
  will-change: transform, opacity;

  &.is-loading {
    pointer-events: none;
  }
}

@keyframes card-enter {
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
  animation: fade-in-up 0.5s ease-out 0.2s forwards;
  opacity: 0;
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-logo {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px -5px rgba(6, 182, 212, 0.4);
  animation: logo-enter 0.6s cubic-bezier(0.16, 1, 0.3, 1) 0.3s both;

  svg {
    width: 36px;
    height: 36px;
    color: white;
  }
}

@keyframes logo-enter {
  from {
    opacity: 0;
    transform: scale(0.5) rotate(-10deg);
  }
  to {
    opacity: 1;
    transform: scale(1) rotate(0);
  }
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.login-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
  letter-spacing: 4px;
  text-transform: uppercase;
}

.login-form {
  animation: fade-in-up 0.5s ease-out 0.4s forwards;
  opacity: 0;
}

.form-item-wrapper {
  margin-bottom: 24px;
}

.form-label-top {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 8px;
  font-weight: 500;
  letter-spacing: 1px;
  transition: color 0.2s ease;
}

.form-item {
  margin: 0;

  :deep(.el-form-item__error) {
    font-size: 12px;
    padding-top: 4px;
  }
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  padding: 0 16px !important;
  height: 50px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important;
  will-change: border-color, background-color, box-shadow !important;

  &:hover {
    border-color: rgba(255, 255, 255, 0.2) !important;
    background: rgba(255, 255, 255, 0.08) !important;
  }

  &.is-focus {
    background: rgba(255, 255, 255, 0.1) !important;
    border-color: #06b6d4 !important;
    box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.15) !important;
  }

  input {
    color: #ffffff !important;
    font-size: 15px;
    height: 50px;
    line-height: 50px;
  }

  .el-input__prefix {
    color: rgba(255, 255, 255, 0.4);
  }

  .el-input__prefix-inner {
    pointer-events: none;
  }
}

.input-icon {
  width: 18px;
  height: 18px;
}

.password-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  color: rgba(255, 255, 255, 0.4);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;

  &:hover {
    color: #06b6d4;
    background: rgba(6, 182, 212, 0.1);
  }

  svg {
    width: 18px;
    height: 18px;
  }
}

.captcha-wrapper {
  .form-label-top {
    display: block;
  }

  .captcha-row {
    display: flex;
    gap: 12px;
  }

  .captcha-item {
    flex: 1;
  }
}

.captcha-image {
  height: 50px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 120px;
  will-change: border-color, transform !important;

  &:hover {
    border-color: #06b6d4;
    background: rgba(255, 255, 255, 0.08);
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(6, 182, 212, 0.2);
  }

  &:active {
    transform: translateY(0);
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.captcha-loading {
  display: flex;
  gap: 4px;
  padding: 0 8px;

  span {
    width: 6px;
    height: 6px;
    background: #06b6d4;
    border-radius: 50%;
    animation: loading-bounce 1.4s infinite ease-in-out both;

    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
  }
}

@keyframes loading-bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  animation: fade-in-up 0.5s ease-out 0.5s forwards;
  opacity: 0;

  :deep(.el-checkbox__label) {
    color: rgba(255, 255, 255, 0.6);
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: #06b6d4;
    border-color: #06b6d4;
  }

  :deep(.el-checkbox__inner) {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(255, 255, 255, 0.2);
  }
}

.remember-text {
  font-size: 14px;
}

.register-link {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #06b6d4;
  text-decoration: none;
  transition: all 0.2s ease;

  svg {
    width: 16px;
    height: 16px;
    transition: transform 0.2s ease;
  }

  &:hover {
    color: #22d3ee;

    svg {
      transform: translateX(4px);
    }
  }
}

.login-button {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%) !important;
  border: none !important;
  box-shadow: 0 4px 20px rgba(6, 182, 212, 0.35);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  overflow: hidden;
  position: relative;
  will-change: transform, box-shadow !important;
  animation: fade-in-up 0.5s ease-out 0.6s forwards;
  opacity: 0;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #22d3ee 0%, #06b6d4 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &:hover::before {
    opacity: 1;
  }

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 30px rgba(6, 182, 212, 0.5);
  }

  &:active {
    transform: translateY(-1px);
  }

  &.is-loading {
    pointer-events: none;
  }

  .button-text {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    position: relative;
    z-index: 1;

    svg {
      width: 18px;
      height: 18px;
      transition: transform 0.2s ease;
    }
  }

  &:hover .button-text svg {
    transform: translateX(4px);
  }

  .button-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    position: relative;
    z-index: 1;
  }

  .loading-dot {
    width: 8px;
    height: 8px;
    background: white;
    border-radius: 50%;
    animation: button-loading 1.4s infinite ease-in-out both;

    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
    &:nth-child(3) { animation-delay: 0s; }
  }
}

@keyframes button-loading {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.login-footer {
  position: absolute;
  bottom: 12%;
  left: 0;
  right: 0;
  width: max-content;
  max-width: 90%;
  margin: 0 auto;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 1.5px;
  animation: fade-in-up 0.5s ease-out 0.8s forwards;
  opacity: 0;
  text-align: center;
  z-index: 10;
}

:deep(.el-form-item__error) {
  color: #f87171;
}
</style>

<script setup>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from "@/utils/jsencrypt"
import useUserStore from '@/store/modules/user'
import defaultSettings from '@/settings'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
  code: "",
  uuid: ""
})

const showPassword = ref(false)

const loginRules = {
  username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
  password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
  code: [{ required: true, trigger: "blur", message: "请输入验证码" }]
}

const codeUrl = ref("")
const loading = ref(false)
const captchaEnabled = ref(true)
const register = ref(false)
const redirect = ref(undefined)

watch(route, (newRoute) => {
  redirect.value = newRoute.query && newRoute.query.redirect
}, { immediate: true })

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (valid) {
      loading.value = true
      if (loginForm.value.rememberMe) {
        Cookies.set("username", loginForm.value.username, { expires: 30 })
        Cookies.set("password", encrypt(loginForm.value.password), { expires: 30 })
        Cookies.set("rememberMe", loginForm.value.rememberMe, { expires: 30 })
      } else {
        Cookies.remove("username")
        Cookies.remove("password")
        Cookies.remove("rememberMe")
      }
      userStore.login(loginForm.value).then(() => {
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
          if (cur !== "redirect") {
            acc[cur] = query[cur]
          }
          return acc
        }, {})
        router.push({ path: redirect.value || "/", query: otherQueryParams })
      }).catch(() => {
        loading.value = false
        if (captchaEnabled.value) {
          getCode()
        }
      })
    }
  })
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img
      loginForm.value.uuid = res.uuid
    }
  })
}

function handleRememberMe(val) {
  loginForm.value.rememberMe = val
}

function getCookie() {
  const username = Cookies.get("username")
  const password = Cookies.get("password")
  const rememberMe = Cookies.get("rememberMe")
  if (username) {
    loginForm.value.username = username
  }
  if (password) {
    loginForm.value.password = decrypt(password)
  }
  if (rememberMe) {
    loginForm.value.rememberMe = true
  }
}

onMounted(() => {
  getCode()
  getCookie()
})
</script>
