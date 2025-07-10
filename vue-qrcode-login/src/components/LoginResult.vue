<template>
  <el-result icon="el-icon-circle-check" title="登录成功">
    <template #subTitle>
      <el-avatar :src="user.avatar" size="large" style="margin-right: 10px" />
      <span>欢迎回来，{{ user.username }}（{{ user.email }}）</span>
    </template>
    <el-button type="primary" @click="$emit('logout')" style="margin-top: 16px;">退出登录</el-button>
  </el-result>
</template>

<script setup>
const props = defineProps({
  user: Object
})
</script>
