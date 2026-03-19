<template>
   <div>
      <i-frame v-model:src="url"></i-frame>
   </div>
</template>

<script setup>
import iFrame from '@/components/iFrame'

import { ref } from 'vue'

const url = ref(import.meta.env.VITE_APP_BASE_API + '/druid/login.html')
</script>

<style scoped>
div {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(6, 182, 212, 0.1);
}
</style>
