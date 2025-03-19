<script setup>
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'
import TheHeadingNavbar from '@/components/layout/TheHeadingNavbar.vue'
import Footer from '@/components/layout/Footer.vue'
import { useAuthStore } from '@/stores/auth' // auth 스토어 import
import Alert from '@/components/common/Alert.vue'

// auth 스토어 인스턴스 생성
const authStore = useAuthStore()

// 컴포넌트가 마운트된 후 초기화 수행
onMounted(() => {
  authStore.initializeStore()
})
</script>

<template>
  <div id="wrapper">
    <TheHeadingNavbar></TheHeadingNavbar>
    <router-view></router-view>
    <!-- Alert 컴포넌트 추가 -->
    <Alert />
  </div>
  <!--<Footer></Footer>-->
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');
#wrapper {
  margin: 0 auto;
  height: 100vh;
  min-height: 100%;
  padding-bottom: 100;
  /* overflow-y: hidden; */
  font-family: 'Noto Sans KR', sans-serif;
  font-optical-sizing: auto;
  font-weight: 400;
  font-style: normal;
}
</style>
