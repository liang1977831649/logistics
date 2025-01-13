import '@/assets/global.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from "element-plus"
import 'element-plus/dist/index.css'
import locale from "element-plus/dist/locale/zh-cn.js"


import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-persistedstate-plugin'

const pinia=createPinia();
const piniaPersist=createPersistedState();


const app=createApp(App)


app.use(pinia);
pinia.use(piniaPersist);
app.use(router)
app.use(ElementPlus,{locale})
app.mount('#app')
