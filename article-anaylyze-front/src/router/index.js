import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            redirect: '/search'
        },
        {
            path: '/search',
            component: resolve => require(['../components/Search.vue'], resolve)
        }
    ]
})