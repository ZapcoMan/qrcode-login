<template>
  <el-card class="qrcode-card" shadow="hover">
    <el-image :src="imgUrl" fit="contain" style="width: 200px; height: 200px" />
    <div class="tip">
      <span :class="statusClass">{{ tip }}</span>
      <el-button
          v-if="expired"
          @click="$emit('refresh')"
          type="primary"
          size="small"
          style="margin-top: 8px;"
      >
        刷新
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import {computed, onMounted} from 'vue'
import baseURL from "@/utils/request.js";
const props = defineProps({
  imgUrl: String,
  tip: String,
  expired: Boolean,
})

// ✅ 修复错误：从 props 中解构 expired
const statusClass = computed(() => props.expired ? 'expired-tip' : '')
</script>

<style scoped>
.qrcode-card {
  text-align: center;
}
.tip {
  margin-top: 12px;
}
.expired-tip {
  color: red;
}
</style>
