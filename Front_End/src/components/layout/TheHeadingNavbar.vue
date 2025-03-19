<script setup>
import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
import { useMenuStore } from '@/stores/menu'
import { storeToRefs } from 'pinia'
import { useAuthStore } from "@/stores/auth";
import { jwtDecode } from "jwt-decode"; // JWT
import { useAlertStore } from '@/stores/alert'

// Alert 스토어 초기화
const alertStore = useAlertStore()

const menuStore = useMenuStore()

// 반응형을 유지하면서 스토어에서 속성을 추출하려면, storeToRefs()를 사용
// https://pinia.vuejs.kr/core-concepts/
const { menuList } = storeToRefs(menuStore)

const AuthStore = useAuthStore();
const visibleMenus = computed(() => menuList.value.filter(menu => menu.show));

const remainingTime = ref('')
let timer = null

const updateAccessToken = async () => {
  try {
    await AuthStore.refreshToken();
    alertStore.showAlert({
      message: '로그인 갱신에 성공했습니다',
      type: 'success'
    })
    initTimer() // 타이머 재설정
  } catch (error) {
    alertStore.showAlert({
      message: '로그인 갱신에 실패했습니다.',
      type: 'error'
    })
    console.error('토큰 갱신 에러:', error)
  }
}

const calculateRemainingTime = () => {
  if (!AuthStore.accessToken) return

  try {
    const token = AuthStore.accessToken
    //console.log("access token: " + token)
    const decodedToken = jwtDecode(token)
    //console.log("decoded token:", JSON.stringify(decodedToken));
    const expirationTime = decodedToken.exp * 1000 // JWT의 exp는 초 단위
    const currentTime = Date.now()
    const timeLeft = Math.max(0, Math.floor((expirationTime - currentTime) / 1000))

    if (timeLeft <= 0) {
      clearInterval(timer)
      alertStore.showAlert({
        message: '로그인이 만료되었습니다. 다시 로그인해주세요.',
        type: 'warning'
      })
      AuthStore.logout()
      return
    }

    const minutes = Math.floor(timeLeft / 60)
    const seconds = timeLeft % 60
    remainingTime.value = `${minutes}:${seconds.toString().padStart(2, '0')}`

  } catch (error) {
    console.error('토큰 파싱 에러:', error)
    remainingTime.value = ''
  }
}
// 타이머 초기화 함수
const initTimer = () => {
  if (timer) clearInterval(timer)
  if (AuthStore.isAuthenticated && AuthStore.accessToken) {
    calculateRemainingTime()
    timer = setInterval(calculateRemainingTime, 1000)
  }
}

// AuthStore의 isAuthenticated와 accessToken 변화 감지
watch(
  () => [AuthStore.isAuthenticated, AuthStore.accessToken],
  ([newIsAuthenticated, newAccessToken]) => {
    if (newIsAuthenticated && newAccessToken) {
      initTimer()
    } else {
      if (timer) clearInterval(timer)
      remainingTime.value = ''
    }
  },
  { immediate: true }
)

onMounted(() => {
  initTimer()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const logout = () => {
  AuthStore.logout();
  console.log('로그아웃!!!!')
}
</script>

<template>
  <nav class="navbar navbar-expand-lg sticky-top">
    <div class="container-fluid">
      <router-link :to="{ name: 'home' }" class="navbar-brand">
        <img src="@/assets/logo.png" class="rounded mx-auto d-block logo-image" alt="..." />
      </router-link>
      <button
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarScroll"
        aria-controls="navbarScroll"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarScroll">
        <ul
          class="navbar-nav ms-auto my-2 my-lg-0 navbar-nav-scroll"
          style="--bs-scroll-height: 100px"
        >
          <li class="nav-item">
            <router-link :to="{ name: 'travelReview' }" class="nav-link">여행지 리뷰</router-link>
          </li>
          <li class="nav-item">
            <router-link :to="{ name: 'PlanReview' }" class="nav-link">일정 공유</router-link>
          </li>

          <li class="nav-item">
            <router-link :to="{ name: 'attractionSearch' }" class="nav-link">여행지 정보</router-link>
          </li>
          
          <li class="nav-item">
            <router-link :to="{ name: 'notice' }" class="nav-link">공지사항</router-link>
          </li>

        </ul>

        <ul
          class="navbar-nav ms-auto my-2 my-lg-0 navbar-nav-scroll"
          style="--bs-scroll-height: 100px"
        >
          <!-- 토큰 만료 시간 표시 및 갱신 버튼 -->
          <li class="nav-item" v-if="AuthStore.isAuthenticated && remainingTime">
            <span class="token d-flex align-items-center">
              <small class="text-muted me-2">로그인 만료: {{ remainingTime }}</small>
              <button 
                @click="updateAccessToken" 
                class="btn btn-outline-primary btn-sm refresh-btn"
                title="토큰 갱신">
                <i class="fas fa-sync-alt"></i>
              </button>
            </span>
          </li>
          <template v-for="menu in visibleMenus" :key="menu.routeName">
              <template v-if="menu.routeName === 'user-logout'">
                <li class="nav-item">
                  <router-link to="/" @click.prevent="logout" class="nav-link">{{
                    menu.name
                  }}</router-link>
                </li>
              </template>
              <template v-else>
                <li class="nav-item">
                  <router-link :to="{ name: menu.routeName }" class="nav-link">{{
                    menu.name
                  }}</router-link>
                </li>
              </template>
          </template>
        </ul>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.text-muted {
  font-size: 0.8rem;
}
.nav-link span {
  color: #666;
  font-size: 0.9rem;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100px;
  padding: 60px;
  font-weight: 800;
  font-size: large;
  font-style: normal;
  border: 1px solid #e9ecef;
  background-color:white;
}

.logo-image {
  width: 150px;
  height: 150px;
  object-fit: contain;
}

.nav-link {
  padding: 0.25rem 0.5rem;
}
.navbar-nav .nav-link {
  transition: color 0.2s ease, text-decoration 0.2s ease;
}

.navbar-nav .nav-link:hover {
  color: #588ef3; /* 원하는 색상으로 변경 */
  text-decoration: underline;
}
.refresh-btn {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  line-height: 1;
}

.refresh-btn:hover {
  background-color: #588ef3;
  color: white;
}

.token {
  padding: 0.25rem 0.5rem;
  margin-right: 5px;
  position: relative;
  top: 2px;
}
</style>
