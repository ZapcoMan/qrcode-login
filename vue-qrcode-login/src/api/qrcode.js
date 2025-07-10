import request from '@/utils/request.js'
export function generateQRCode() {
    return request.get('/api/qrcode/generate')
}

export function scanQRCode(qrCodeId) {
    return request.post('/api/qrcode/scan', { qrCodeId })
}

export function confirmLogin(qrCodeId, userId) {
    return request.post('/api/qrcode/confirm', { qrCodeId, userId })
}

export function cancelScan(qrCodeId) {
    return request.post('/api/qrcode/cancel', { qrCodeId })
}

export function getUsers() {
    return request.get('/api/auth/users')
}
