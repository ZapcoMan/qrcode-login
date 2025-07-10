<template>
  <el-container
      style="height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px;"
  >
    <el-header style="text-align: center; margin-bottom: 30px;">
      <h2 style="font-weight: 600; font-size: 28px; color: #303133;">网页端扫码登录</h2>
    </el-header>

    <el-main
        style="width: 360px; max-width: 90vw; display: flex; flex-direction: column; align-items: center; justify-content: center;"
    >
      <div v-if="!loggedIn" style="width: 100%;">
        <QrcodeCard
            :imgUrl="qrcodeUrl"
            :tip="tip"
            :expired="expired"
            @refresh="refreshQRCode"
            style="width: 100%;"
        />
      </div>

      <div v-else style="width: 100%;">
        <LoginResult
            :user="userInfo"
            @logout="handleLogout"
            style="width: 100%;"
        />
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import {ref, reactive, onMounted, watch} from 'vue'
import QrcodeCard from '@/components/QrcodeCard.vue'
import LoginResult from '@/components/LoginResult.vue'
import {generateQRCode} from '@/api/qrcode'
import {baseURL} from '@/utils/request.js'

let ws

const qrcodeId = ref('')
const qrcodeUrl = ref('')
const tip = ref('请使用手机扫描二维码登录')
const expired = ref(false)
const userInfo = reactive({username: '', email: '', avatar: ''})
const loggedIn = ref(false)
let refreshTimer = null

function connectWebsocket() {
  if (ws) ws.close()
  const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
  ws = new WebSocket(`${protocol}//${location.host}/ws/qrcode`)
  ws.onopen = () => ws.send(JSON.stringify({qrcodeId: qrcodeId.value}))
  ws.onmessage = ({data}) => {
    const msg = JSON.parse(data)
    if (msg.type === 'STATUS_CHANGE') handleStatus(msg)
  }
}

function handleStatus(msg) {
  switch (msg.status) {
    case 'SCANNED':
      tip.value = '已扫描，请在手机端确认'
      break
    case 'CONFIRMED':
      Object.assign(userInfo, msg.userInfo)
      loggedIn.value = true
      clearTimeout(refreshTimer)
      break
    case 'EXPIRED':
    case 'CANCELLED':
      tip.value = '二维码已失效，请刷新'
      expired.value = true
      break
  }
}

function refreshQRCode() {
  startGenerate()
}

async function startGenerate() {
  clearTimeout(refreshTimer)
  loggedIn.value = false
  tip.value = '请使用手机扫描二维码登录'
  expired.value = false

  try {
    const res = await generateQRCode()
    const qrCodeId = res.data.qrCodeId
    qrcodeId.value = qrCodeId
    qrcodeUrl.value = baseURL + `/api/qrcode/image/${qrCodeId}`
    console.log('当前二维码地址为:', qrcodeUrl.value)
    connectWebsocket()
    refreshTimer = setTimeout(refreshQRCode, 120000)
  } catch (err) {
    console.error('二维码生成失败：', err)
    tip.value = '二维码生成失败，请稍后重试'
  }
}

function handleLogout() {
  loggedIn.value = false
  startGenerate()
}

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    Object.assign(userInfo, JSON.parse(stored))
    loggedIn.value = true
  } else {
    console.log('[onMounted] 未登录，准备生成二维码')
    startGenerate()
  }
})

watch(loggedIn, val => {
  if (val) localStorage.setItem('userInfo', JSON.stringify(userInfo))
})
</script>
