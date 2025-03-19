<template>
  <div class="travel-plan-details">
    <!-- 지도 및 여행 경로 정보 -->
    <div class="map-container">
      <div class="header">
        <div class="day-buttons">
          <button
            v-for="(day, index) in planDays"
            :key="index"
            @click="
              () => {
                selectDay(index)
                drawRoutes(index)
              }
            "
            :class="{ active: selectedDayIndex === index }"
          >
            {{ index + 1 }}일차
          </button>
        </div>
      </div>
      <MyTmap ref="myTmapRef" />
      <div class="transport-toggle">
        <button
          :class="{ active: selectedTransport === 'public' }"
          @click="setTransportMode('public')"
        >
          대중교통
        </button>
        <button :class="{ active: selectedTransport === 'car' }" @click="setTransportMode('car')">
          승용차
        </button>
      </div>

      <div class="button-panel">
        <template v-if="isOwner">
          <button class="btn btn-secondary" @click="goToManagePlan">편집</button>
          <button class="btn btn-primary" @click="savePlan">저장</button>
        </template>
        <template v-else>
          <button class="btn btn-secondary" @click="goBack">돌아가기</button>
        </template>
      </div>
      <div class="day-info-panel">
        <div v-if="selectedDayIndex !== null" class="day-info">
          <h4>{{ selectedDayIndex + 1 }}일차 이동 경로</h4>
          <div
            v-for="(placeInfo, idx) in dayRoutes[selectedDayIndex]?.routes"
            :key="idx"
            class="route-info"
          >
            <div class="place">
              <h5>{{ placeInfo.from }}</h5>
              <p>{{ placeInfo.description }}</p>
            </div>
            <div class="travel-info" v-if="placeInfo.to">
              <p>
                {{ placeInfo.from }} ➔ {{ placeInfo.to }}<br />
                거리: {{ placeInfo.distance }}<br />
                시간: {{ placeInfo.duration }}<br />
                <span v-if="transportationType === 'car'">
                  택시 요금: {{ placeInfo.fare }}<br />
                </span>
                <span v-else-if="transportationType === 'public'">
                  총 요금: {{ placeInfo.fare }}<br />
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MyTmap from '@/components/util/MyTmap.vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { listAttractionSave } from '@/api/plan'
import { onBeforeRouteLeave } from 'vue-router'
import { computed } from 'vue'
const authStore = useAuthStore()

const user = authStore.user.body.user

// Props
const props = defineProps({
  selectedTransport: {
    type: String,
    default: 'car',
  },
})

// State
const planDays = ref([])
const plan = ref({})
const selectedDayIndex = ref(null)
const selectedPlacesForMap = ref([])
const dayRoutes = ref([])
// const transportationType = ref(props.selectedTransport)

// State
const selectedTransport = ref(props.selectedTransport)
const transportationType = ref(selectedTransport.value)

// Methods
const setTransportMode = (mode) => {
  selectedTransport.value = mode
  transportationType.value = mode
  // Re-draw routes based on the new transportation type
  if (selectedDayIndex.value !== null) {
    drawRoutes(selectedDayIndex.value)
    calculateRoutes
  }
}

// Router
const router = useRouter()

// Refs
const myTmapRef = ref(null)
const isOwner = computed(() => {
  return (
    user.userSeq === plan.value.user_seq ||
    plan.value.user_seq === null ||
    plan.value.user_seq === undefined
  )
})

// 돌아가기 버튼 동작
const goBack = () => {
  router.back() // 이전 화면으로 이동
}

// Functions
const fetchSessionStorageData = () => {
  const planData = sessionStorage.getItem('planData')
  const daysData = sessionStorage.getItem('planDays')

  plan.value = planData ? JSON.parse(planData) : {}
  planDays.value = daysData ? JSON.parse(daysData) : []
}

const drawRoutes = (dayIndex) => {
  const places = planDays.value[dayIndex]?.places || []
  if (places.length > 0) {
    const routePlaces = places.map((place) => ({
      latitude: place.latitude,
      longitude: place.longitude,
      title: place.title,
    }))
    myTmapRef.value?.setRoutePlaces(routePlaces, transportationType.value)
  }
}

const selectDay = (dayIndex) => {
  selectedDayIndex.value = dayIndex
  selectedPlacesForMap.value = planDays.value[dayIndex]?.places || []
}

const calculateRoutes = async () => {
  console.log('Calculating route')
  dayRoutes.value = []
  for (let i = 0; i < planDays.value.length; i++) {
    const places = planDays.value[i].places
    let routes = []
    if (!places || places.length === 0) {
      dayRoutes.value.push({ dayIndex: i, routes })
      continue
    }
    if (places.length === 1) {
      routes.push({
        from: places[0].title,
        description: places[0].description || '',
        distance: 'N/A',
        duration: 'N/A',
        fare: 'N/A',
      })
      dayRoutes.value.push({ dayIndex: i, routes })
      continue
    }
    for (let j = 0; j < places.length - 1; j++) {
      const from = places[j]
      const to = places[j + 1]
      let response = undefined
      if (transportationType.value == 'public') {
        response = await getPublicRouteInfo(from, to)
      } else {
        response = await getRouteInfo(from, to)
      }

      routes.push({
        from: from.title,
        to: to.title,
        description: from.description || '',
        distance: response.distance,
        duration: response.duration,
        fare: response.fare,
      })
    }
    routes.push({
      from: places[places.length - 1].title,
      description: places[places.length - 1].description || '',
    })
    dayRoutes.value.push({ dayIndex: i, routes })
  }
}

const getRouteInfo = async (from, to) => {
  try {
    const appKey = import.meta.env.VITE_TMAP_API_KEY
    const url = `https://apis.openapi.sk.com/tmap/routes?version=1&format=json&appKey=${appKey}`
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        startX: from.longitude,
        startY: from.latitude,
        endX: to.longitude,
        endY: to.latitude,
        reqCoordType: 'WGS84GEO',
        resCoordType: 'EPSG3857',
        searchOption: 0,
      }),
    })
    const result = await response.json()
    const properties = result.features[0]?.properties || {}
    return {
      distance: `${(properties.totalDistance / 1000).toFixed(1)} km`,
      duration: `${(properties.totalTime / 60).toFixed(0)} 분`,
      fare: properties.taxiFare ? `${properties.taxiFare} 원` : '0 원',
    }
  } catch (error) {
    console.error('Error fetching route info:', error)
    return {
      distance: '알 수 없음',
      duration: '알 수 없음',
      fare: '알 수 없음',
    }
  }
}

const getPublicRouteInfo = async (from, to) => {
  try {
    const appKey = import.meta.env.VITE_TMAP_API_KEY
    const url = `https://apis.openapi.sk.com/transit/routes?appKey=${appKey}`
    const data = {
      startX: from.longitude,
      startY: from.latitude,
      endX: to.longitude,
      endY: to.latitude,
      format: 'json',
      count: 1, // 최적 경로 하나만 반환
    }

    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const result = await response.json()

    if (
      !result.metaData ||
      !result.metaData.plan ||
      !result.metaData.plan.itineraries ||
      result.metaData.plan.itineraries.length === 0
    ) {
      throw new Error('No itinerary data found in response')
    }

    const itinerary = result?.metaData?.plan?.itineraries?.[0]
    const totalFare = itinerary?.fare?.regular?.totalFare // 총 요금
    const totalTime = itinerary?.totalTime // 총 시간
    const totalDistance = itinerary?.totalDistance // 총 거리
    return {
      distance: `${(totalDistance / 1000).toFixed(1)} km`,
      duration: `${(totalTime / 60).toFixed(0)} 분`,
      fare: totalFare ? `${totalFare} 원` : '0 원',
    }
  } catch (error) {
    console.error('Error fetching route info:', error)
    return {
      distance: '알 수 없음',
      duration: '알 수 없음',
      fare: '알 수 없음',
    }
  }
}

const goToManagePlan = () => {
  router.push({ name: 'manage-plan' })
}

const savePlan = () => {
  listAttractionSave()

  const plan = JSON.parse(sessionStorage.getItem('planData'))
  const dayData = JSON.parse(sessionStorage.getItem('planDays'))

  const planDetails = {
    plan: {
      id: plan.id,
      title: plan.title,
      startDate: plan.startDate,
      endDate: plan.endDate,
      isPublic: plan.isPublic,
      location: plan.location,
    },
    daysData: dayData,
  }
  console.log('planDetails:', planDetails)
  listAttractionSave(
    planDetails,
    (response) => {
      console.log('데이터 저장 성공:', response)
      router.push({ name: 'user-mypage' })
    },
    (error) => {
      console.error('데이터 로드 오류:', error)
    }
  )
}

onMounted(() => {
  fetchSessionStorageData()
  calculateRoutes()
})

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
.travel-plan-details {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  position: absolute; /* 맵 위에 겹쳐지도록 설정 */
  top: 10px; /* 화면 위에서 10px 떨어지도록 설정 */
  left: 50%; /* 화면의 가로 가운데 정렬 */
  transform: translateX(-50%); /* 가운데 정렬 보정 */
  z-index: 10; /* 버튼이 지도 위에 보이도록 z-index 설정 */
  display: flex;
  gap: 10px;
  border-radius: 10px;
  padding: 10px;
}

.day-buttons {
  display: flex;
  gap: 10px;
}

.day-buttons button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
  font-size: 14px;
}

.day-buttons button.active {
  background-color: #007bff;
  color: #fff;
  border-color: #007bff;
}

.map-container {
  position: relative;
  flex-grow: 1;
  height: 100%; /* 부모 요소의 크기를 꽉 채움 */
}

.day-info-panel {
  position: absolute;
  top: 100px; /* 버튼 패널과 겹치지 않도록 위치 조정 */
  right: 90px;
  width: 300px;
  max-height: calc(100% - 100px);
  background: #fff;
  overflow-y: auto;
  border-left: 1px solid #ddd;
}

.route-info {
  margin-bottom: 20px;
}

.place h5 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.travel-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #555;
}
.button-panel {
  position: absolute;
  top: 60px;
  right: 90px;
  display: flex;
  gap: 5px; /* 버튼 간의 간격을 좁게 설정 */
  width: auto; /* 패널 너비를 버튼 크기에 맞게 자동 조정 */
  justify-content: flex-end; /* 버튼이 오른쪽 정렬되도록 설정 */
}
.transport-toggle {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  display: flex;
  gap: 10px;
}

.transport-toggle button {
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f9f9f9;
  cursor: pointer;
}

.transport-toggle button.active {
  background-color: #007bff;
  color: #fff;
  border-color: #007bff;
}
</style>
