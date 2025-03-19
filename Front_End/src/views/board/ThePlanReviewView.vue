<template>
  <div class="container py-4">
    <!-- 지역 선택 -->
    <div class="mb-4">
      <label for="regionSelect" class="form-label">지역 검색</label>
      <select
        id="regionSelect"
        class="form-select"
        v-model="selectedRegion"
        @change="fetchReviews(false, true)"
      >
        <option value="0">전체</option>
        <option v-for="region in regions" :key="region.id" :value="region.id">
          {{ region.name }}
        </option>
      </select>
    </div>

    <!-- 카드 리스트 -->
    <CardList
      :cards="reviews"
      title="지역 여행 리뷰"
      @cardClick="handleCardClick"
      @deleteClick="handleDelete"
    />

    <!-- 로딩 표시 -->
    <div v-if="loading" class="text-center my-3">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import CardList from '@/components/users/CardList.vue'
import { planInfoByRegion, planInfo, deletePlan } from '@/api/plan'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
// 지역 목록 및 리뷰 데이터
const regions = ref([
  { id: 1, name: '서울' },
  { id: 2, name: '인천' },
  { id: 3, name: '대전' },
  { id: 4, name: '대구' },
  { id: 5, name: '광주' },
  { id: 6, name: '부산' },
  { id: 7, name: '울산' },
  { id: 8, name: '세종특별자치시' },
  { id: 31, name: '경기도' },
  { id: 32, name: '강원도' },
  { id: 33, name: '충청북도' },
  { id: 34, name: '충청남도' },
  { id: 35, name: '경상북도' },
  { id: 36, name: '경상남도' },
  { id: 37, name: '전라북도' },
  { id: 38, name: '전라남도' },
  { id: 39, name: '제주도' },
]) // 예시 데이터
const selectedRegion = ref('0')
const reviews = ref([])
const loading = ref(false)
const page = ref(0)
const size = ref(6)
const hasMore = ref(true)

const router = useRouter()
const authStore = useAuthStore()

const user = authStore.user.body.user

const throttle = (func, delay) => {
  let lastCall = 0
  return (...args) => {
    const now = new Date().getTime()
    if (now - lastCall < delay) return
    lastCall = now
    func(...args)
  }
}

// 무한 스크롤 이벤트
const handleScroll = throttle(() => {
  const scrollTop = window.scrollY // 현재 스크롤 위치
  const windowHeight = window.innerHeight // 화면 높이
  const documentHeight = document.body.offsetHeight // 문서 전체 높이

  // 스크롤이 거의 바닥에 도달했을 때 데이터 가져오기
  if (scrollTop + windowHeight >= documentHeight - 50 && !loading.value && hasMore.value) {
    console.log('스크롤이 바닥에 닿음, 데이터 가져오기 실행')
    fetchReviews(true) // 추가 데이터 로드
  }
}, 200) // 200ms마다 호출 제한

// 지역 선택에 따른 리뷰 가져오기
const fetchReviews = async (append = false, force = false) => {
  if (!force && (!hasMore.value || loading.value)) return // force가 true면 조건 무시

  loading.value = true
  try {
    const params = {
      region: selectedRegion.value,
      page: append ? page.value : 0, // append가 false면 첫 페이지로 초기화
      size: size.value,
    }
    console.log('fetchReviews params:', params)

    planInfoByRegion(
      params,
      (response) => {
        const data = response.data.content
        console.log('API 응답 데이터:', data)

        if (data.length < size.value)
          hasMore.value = false // 데이터가 부족하면 더 이상 요청하지 않음
        else hasMore.value = true // 강제로 호출 시 데이터가 있다면 hasMore를 true로 초기화

        if (append) {
          reviews.value = [...reviews.value, ...data]
        } else {
          reviews.value = data
          page.value = 1 // 첫 페이지로 초기화
        }
      },
      (error) => {
        console.error('데이터를 가져오는 중 오류 발생:', error)
      }
    )
  } catch (error) {
    console.error('fetchRegionPlans 실행 중 오류 발생:', error)
  } finally {
    loading.value = false
  }
}

const handleDelete = (card) => {
  deletePlan(
    card.id,
    (response) => {
      console.log('삭제 성공:', response.data)
      // UI에서 삭제된 항목 제거 처리
      fetchReviews
    },
    (error) => {
      console.error('삭제 실패:', error.response?.data || error.message)
    }
  )
}

// 카드 클릭 이벤트
const handleCardClick = (card) => {
  planInfo(
    card.id, // 선택한 Plan의 ID
    (response) => {
      console.log('Plan 정보:', response.data)
      const planDays = response.data.daysData // PlanDay 데이터
      console.log('PlanDay 데이터:', planDays)
      const plan = response.data.plan // Plan 데이터
      const selectedArr = planDays.flatMap((planDay) => planDay.places)
      sessionStorage.setItem('selectedPlaces', JSON.stringify(selectedArr))
      sessionStorage.setItem('planData', JSON.stringify(plan)) // 세션에 Plan 데이터 저장
      sessionStorage.setItem('planDays', JSON.stringify(planDays))
      router.push({ name: 'travel-plan-details' }) // 여행지 관리 페이지로 이동
    },
    (error) => {
      console.error('Plan 정보 로드 오류:', error)
    }
  )
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  fetchReviews() // 화면 로드 시 모든 지역 데이터 가져오기
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.container {
  max-width: 960px;
  margin: 0 auto;
}
</style>
