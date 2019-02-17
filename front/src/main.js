import Vue from "vue";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import store from "./store";
import App from "./App.vue";
import router from "./router";
import "./assets/global.css";

Vue.use(ElementUI);

new Vue({
  store,
  el: "#app",
  router,
  render: h => h(App)
});
