<template>
  <el-card>
    <div class="user-list">
      <el-row :gutter="20">
        <el-col v-for="user in users" :key="user.userId" :span="8">
          <el-card class="user-item" @click="select(user.userId)">
            <el-avatar :src="user.avatar" size="medium" />
            <div>{{ user.username }}</div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>

<script setup>
const props = defineProps({
  users: Array
})
const emit = defineEmits(['select'])
function select(userId) {
  emit('select', userId)
}
</script>

<style scoped>
.user-item {
  text-align: center;
  cursor: pointer;
}
.user-item:hover {
  background: #f5f5f5;
}
</style>
