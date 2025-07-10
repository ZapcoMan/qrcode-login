import {createRouter, createWebHistory} from 'vue-router'
import WebLogin from '@/views/WebLogin.vue'
import MobileLogin from '@/views/MobileLogin.vue'
const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {path: '/', redirect: '/login'},
        {path: '/login', component: WebLogin},
        {path: '/mobile', component: MobileLogin}
    ],
})

export default router
