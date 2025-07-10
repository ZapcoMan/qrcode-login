<template>
  <el-container style="height:100vh;">
    <el-header><h2>移动端扫码模拟</h2></el-header>
    <el-main>
      <el-steps :active="step - 1" style="margin-bottom:20px">
        <el-step title="扫码输入"></el-step>
        <el-step title="选择用户"></el-step>
        <el-step title="确认登录"></el-step>
        <el-step title="完成"></el-step>
      </el-steps>

      <div v-if="step === 1">
        <el-form @submit.prevent="onScan">
          <el-form-item label="二维码 ID">
            <el-input v-model="qrcodeIdInput" placeholder="输入二维码ID"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onScan">确认扫码</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="step === 2">
        <div>ID: {{ currentCode }}</div>
        <UserSelect :users="users" @select="onSelectUser" />
        <el-button type="warning" @click="onCancelScan" style="margin-top:10px;">取消</el-button>
      </div>

      <div v-if="step === 3">
        <el-card>
          <el-avatar :src="currentUser.avatar" size="large" />
          <div>{{ currentUser.username }} ({{ currentUser.email }})</div>
          <el-button type="danger" @click="onCancelConfirm" style="margin-right:10px;">取消</el-button>
          <el-button type="primary" @click="onConfirmLogin">确认登录</el-button>
        </el-card>
      </div>

      <div v-if="step === 4">
        <el-result icon="el-icon-check" title="登录成功">
          <el-button type="primary" @click="onReset">返回</el-button>
        </el-result>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { scanQRCode, confirmLogin, cancelScan, getUsers } from '@/api/qrcode'
import UserSelect from '@/components/UserSelect.vue'

const step = ref(1)
const qrcodeIdInput = ref('')
const currentCode = ref('')
const users = ref([])
const currentUser = reactive({ userId: '', username: '', avatar: '', email: '' })

async function onScan() {
  if (!qrcodeIdInput.value) return
  await scanQRCode(qrcodeIdInput.value)
  currentCode.value = qrcodeIdInput.value
  step.value = 2
}

function onSelectUser(userId) {
  const u = users.value.find(u => u.userId === userId)
  Object.assign(currentUser, u)
  step.value = 3
}

async function onConfirmLogin() {
  await confirmLogin(currentCode.value, currentUser.userId)
  step.value = 4
}

async function onCancelScan() {
  await cancelScan(currentCode.value)
  step.value = 1
  resetData()
}

function onCancelConfirm() {
  step.value = 2
}

function onReset() {
  resetData()
  step.value = 1
}

function resetData() {
  qrcodeIdInput.value = ''
  currentCode.value = ''
  Object.assign(currentUser, { userId: '', username: '', avatar: '', email: '' })
}

onMounted(async () => {
  const res = await getUsers()
  users.value = Object.entries(res.data).map(([userId, u]) => ({ userId, ...u }))
})
</script>
