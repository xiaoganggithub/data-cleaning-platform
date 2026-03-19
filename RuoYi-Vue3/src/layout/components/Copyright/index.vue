<template>
  <footer v-if="visible" class="copyright">
    <span>{{ content }}</span>
  </footer>
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'

const settingsStore = useSettingsStore()

const visible = computed(() => settingsStore.footerVisible)
const content = computed(() => settingsStore.footerContent)
</script>

<style scoped>
.copyright {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 36px;
  padding: 10px 20px;
  text-align: center;
  background: rgba(15, 12, 41, 0.6);
  backdrop-filter: blur(12px);
  color: rgba(255, 255, 255, 0.4);
  font-size: 13px;
  border-top: 1px solid rgba(255, 255, 255, 0.04);
  z-index: 999;
  letter-spacing: 0.5px;
}
</style>
