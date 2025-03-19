import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

import App from "./App.vue";
import router from "./router";

// Font Awesome 설정
import { library } from "@fortawesome/fontawesome-svg-core";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { far } from "@fortawesome/free-regular-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import '@fortawesome/fontawesome-free/css/all.css';

// Bootstrap 관련 import
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";
import BootstrapVue3 from 'bootstrap-vue-3';
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css';

// Font Awesome 라이브러리 추가
library.add(fas, far);

const app = createApp(App);
const pinia = createPinia();

pinia.use(piniaPluginPersistedstate);

// 컴포넌트 및 플러그인 등록
app.component('font-awesome-icon', FontAwesomeIcon);
app.use(BootstrapVue3);
app.use(pinia);
app.use(router);

app.mount("#app");