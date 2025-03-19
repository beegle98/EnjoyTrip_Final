<template>
  <div class="select-destination container-fluid">
    <div class="row h-100">
      <!-- 왼쪽 패널 -->
      <div class="col-md-3 bg-light p-3 h-100 d-flex flex-column">
        <h2>{{ plan.title }}</h2>
        <p class="lead">{{ tripDuration }} {{ locationName }} 여행</p>
        <div class="d-flex gap-3 mb-4">
          <button class="btn btn-outline-primary" @click="goToSelectDestination">
            여행지 선택
          </button>
          <button class="btn btn-outline-secondary" @click="goToManagePlan">여행지 관리</button>
        </div>
        <h3 class="mb-3">여행지 검색</h3>

        <!-- 검색어 입력 -->
        <div class="mb-3">
          <label for="searchKeyword">검색어</label>
          <div class="d-flex gap-2">
            <input
              type="text"
              id="searchKeyword"
              v-model="searchKeyword"
              class="form-control"
              placeholder="검색어를 입력하세요"
            />
            <button class="btn btn-primary" style="width: 70px" @click="searchAttractions">
              검색
            </button>
          </div>
        </div>

        <!-- 카테고리 필터 -->
        <label for="searchAttraction">카테고리</label>
        <div class="filter-section" id="searchAttraction">
          <select v-model="selectedContentType" @change="searchAttractions" class="form-select">
            <option value="">전체</option>
            <option v-for="type in contentTypes" :key="type.id" :value="type.id">
              {{ type.name }}
            </option>
          </select>
        </div>

        <!-- 검색 결과 목록 -->
        <div
          class="destination-list overflow-scroll d-flex flex-grow-1 flex-column no-scrollbar"
          @scroll="onScroll"
          ref="listContainer"
        >
          <div
            v-for="(place, index) in filteredPlaces"
            :key="index"
            class="place-item border rounded p-2 mb-2 d-flex align-items-center"
            @click="togglePlaceSelection(place)"
          >
            <img :src="place.image" alt="여행지 이미지" class="place-image me-2" />
            <div class="flex-grow-1">
              <h5 class="mb-1">{{ place.title }}</h5>
              <p class="mb-0 text-muted">{{ place.address }}</p>
              <div class="d-flex align-items-center mt-2">
                <span class="text-danger me-2">
                  <i class="fas fa-heart"></i> {{ place.likes }}
                </span>
              </div>
            </div>
            <input
              type="checkbox"
              :checked="isSelected(place)"
              @change="togglePlaceSelection(place)"
              @click.stop
            />
          </div>
        </div>
      </div>

      <!-- 오른쪽 패널 -->
      <div class="col-md-9">
        <MyTmap :selected-places="selectedPlacesArr" />
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MyTmap from '@/components/util/MyTmap.vue'
import { listAttraction } from '@/api/plan'
import { onBeforeRouteLeave } from 'vue-router'

const router = useRouter()
const contentTypes = ref([
  { id: 12, name: '관광' },
  { id: 39, name: '식당' },
  { id: 14, name: '문화' },
  { id: 15, name: '공연' },
])

const selectedContentType = ref('')
const searchKeyword = ref('')
const filteredPlaces = ref([])
const plan = ref(JSON.parse(sessionStorage.getItem('planData')) || {})
const selectedPlacesArr = ref(JSON.parse(sessionStorage.getItem('selectedPlaces')) || [])
const planDays = ref(JSON.parse(sessionStorage.getItem('planDays')) || [])
const tripDuration = computed(() => {
  const startDate = new Date(plan.value.startDate)
  const endDate = new Date(plan.value.endDate)
  const days = (endDate - startDate) / (1000 * 60 * 60 * 24) + 1
  const nights = days - 1
  return `${nights}박 ${days}일`
})

// 페이지네이션 상태
const page = ref(0)
const size = ref(10) // 한 번에 불러올 데이터 수
const isLoading = ref(false)
const hasMore = ref(true) // 추가 데이터 여부

const fetchAttractions = (append = false) => {
  if (isLoading.value || !hasMore.value) return

  const param = {
    article: searchKeyword.value,
    areaCode: plan.value.location || 0,
    contentType: selectedContentType.value || 0,
    page: page.value,
    size: size.value,
  }
  console.log(searchKeyword.value)
  isLoading.value = true

  listAttraction(
    param,
    (response) => {
      const data = response.data.content
      console.log(data)
      if (data.length < size.value) {
        hasMore.value = false // 더 이상 가져올 데이터가 없음
      }

      const updatedData = data.map((place) => {
        const isAlreadySelected = selectedPlacesArr.value.some((p) => p.id === place.id)
        if (isAlreadySelected) {
          place.selected = true
        }
        return place
      })

      if (append) {
        filteredPlaces.value = [...filteredPlaces.value, ...data]
      } else {
        filteredPlaces.value = updatedData
      }
      isLoading.value = false
      page.value += 1
    },
    (error) => {
      console.error('데이터 로드 오류:', error)
      isLoading.value = false
    }
  )
}

const searchAttractions = () => {
  // 새로운 검색 시 초기화
  page.value = 0
  hasMore.value = true
  fetchAttractions(false)
}

const locations = ref([
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
])

const locationName = computed(() => {
  const location = locations.value.find((loc) => loc.id === plan.value.location)
  return location ? location.name : '알 수 없음' // 해당하는 name이 없으면 '알 수 없음' 출력
})

const togglePlaceSelection = (place) => {
  const index = selectedPlacesArr.value.findIndex((p) => p.no === place.no)
  if (index === -1) {
    selectedPlacesArr.value.push(place)

    // Check if the place exists in planDays
    const isInPlanDays = planDays.value.some((day) => day.places.some((p) => p.no === place.no))

    if (!isInPlanDays) {
      // Add to the first day's places if not in planDays
      if (planDays.value.length > 0) {
        planDays.value[0].places.push(place)
      } else {
        // Create first day if planDays is empty
        planDays.value.push({ places: [place] })
      }
    }
  } else {
    selectedPlacesArr.value.splice(index, 1)
  }

  // Save updated data to sessionStorage
  sessionStorage.setItem('selectedPlaces', JSON.stringify(selectedPlacesArr.value))
}

const isSelected = (place) => {
  return selectedPlacesArr.value.some((p) => p.no === place.no)
}

// 무한 스크롤 이벤트
const onScroll = () => {
  const container = document.querySelector('.destination-list')
  if (container.scrollTop + container.clientHeight >= container.scrollHeight - 10) {
    fetchAttractions(true) // 추가 데이터 로드
  }
}

onMounted(() => {
  fetchAttractions(false) // 처음 로드 시 전체 데이터를 가져옴
})

const goToManagePlan = () => {
  sessionStorage.setItem('selectedPlaces', JSON.stringify(selectedPlacesArr.value)) // 선택된 여행지 정보를 sessionStorage에 저장
  router.push({ name: 'manage-plan' }) // 여행지 관리 페이지로 이동
}

onBeforeRouteLeave((to, from, next) => {
  // selectdestination 또는 manageplan에서만 sessionStorage 유지
  if (
    to.name !== 'select-destination' &&
    to.name !== 'manage-plan' &&
    to.name !== 'travel-plan-details'
  ) {
    console.log('select..beforeRouterLeave')
    sessionStorage.removeItem('planData')
    sessionStorage.removeItem('selectedPlaces')
    sessionStorage.removeItem('planDays')
  }
  next()
})
</script>

<style scoped>
.select-destination {
  width: 100%;
  margin: 0 auto;
  height: calc(100vh - 100px);
  overflow-y: hidden;
}
.row {
  height: 100%;
}

.place-item:hover {
  background-color: #f0f0f0;
}
html,
body {
  height: 100%;
  margin: 0;
}
.filter-section {
  margin-bottom: 1rem;
}

.form-select {
  width: 100%;
  max-width: 300px;
}
.place-item {
  cursor: pointer;
  display: flex;
  transition: background-color 0.3s;
}

.place-image {
  width: 80px;
  height: 80px;
  object-fit: cover; /* 이미지 비율 유지 */
  border-radius: 8px;
}
.text-danger {
  font-size: 0.9rem;
  font-weight: bold;
}

.fa-heart {
  font-size: 1rem;
}
.no-scrollbar::-webkit-scrollbar {
  display: none;
}

.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
