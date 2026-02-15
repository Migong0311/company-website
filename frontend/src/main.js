import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import '@/assets/styles/global.css'
import { vScrollAnimate } from '@/directives/scrollAnimate'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.directive('scroll-animate', vScrollAnimate)

app.mount('#app')
