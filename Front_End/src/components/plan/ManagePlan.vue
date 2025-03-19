<template>
  <div class="manage-plan container-fluid">
    <!-- 왼쪽 패널과 오른쪽 지도 패널 나누기 -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div class="row h-100">
      <!-- 왼쪽 패널: 일차별 일정 관리 -->
      <div class="col-md-2 bg-light p-3 overflow-scroll h-100">
        <h2>{{ plan.title }}</h2>
        <p class="lead">{{ tripDuration }} {{ locationName }} 여행</p>

        <!-- 여행지 선택 및 관리 버튼 -->
        <div class="d-flex gap-3 mb-4">
          <button class="btn btn-outline-primary" @click="goToSelectDestination">
            여행지 선택
          </button>
          <button class="btn btn-outline-secondary" @click="goToManagePlan">여행지 관리</button>
          <button class="btn btn-outline-success" @click="fetchRecommendedRoutes">
            여행경로 추천받기
          </button>
        </div>
        <h3 class="mb-3">일정 관리</h3>
        <div
          v-for="(day, index) in planDays"
          :key="index"
          class="day-box mb-4 p-3 border rounded"
          @dragover.prevent
          @drop="() => drop(index, planDays[index].places.length)"
        >
          <h4>{{ index + 1 }}일차</h4>
          <div
            v-for="(place, placeIndex) in day.places"
            :key="placeIndex"
            class="place-item border p-2 mb-2"
            draggable="true"
            @dragstart="dragStart(index, placeIndex)"
            @dragover.prevent
            @drop="() => drop(index, placeIndex)"
          >
            {{ place.title }}
          </div>
          <p v-if="day.places && day.places.length === 0">아직 선택된 여행지가 없습니다.</p>
        </div>
        <button
          @click="savePlan"
          class="btn btn-primary mt-4"
          data-bs-toggle="modal"
          data-bs-target="#transportModal"
        >
          경로보기
        </button>
      </div>

      <!-- 오른쪽 패널: 지도 -->
      <div class="col-md-10">
        <MyTmap :selected-places="planDays" />
      </div>
    </div>

    <!-- 부트스트랩 모달 -->
    <div
      class="modal fade"
      id="transportModal"
      tabindex="-1"
      aria-labelledby="transportModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-centered" id="transportModal">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="transportModalLabel">이동수단 선택</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div class="modal-body">
            <button
              @click="selectTransport('car')"
              class="btn btn-outline-primary w-100 mb-3"
              data-bs-dismiss="modal"
            >
              승용차
            </button>
            <button
              @click="selectTransport('public')"
              class="btn btn-outline-secondary w-100"
              data-bs-dismiss="modal"
            >
              대중교통
            </button>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MyTmap from '@/components/util/MyTmap.vue'
import { onBeforeRouteLeave } from 'vue-router'
const router = useRouter()

const planDays = ref([])
const draggedItem = ref(null)
const selectedPlacesForMap = ref([])
const selectedPlacesArr = ref(JSON.parse(sessionStorage.getItem('selectedPlaces')) || [])
const showTransportModal = ref(false)
const plan = ref(JSON.parse(sessionStorage.getItem('planData')) || {})
const selectedTransport = ref(null)
const loading = ref(false) // 로딩 상태 추가

const tripDuration = computed(() => {
  const startDate = new Date(plan.value.startDate)
  const endDate = new Date(plan.value.endDate)
  const days = (endDate - startDate) / (1000 * 60 * 60 * 24) + 1
  const nights = days - 1
  return `${nights}박 ${days}일`
})

const fetchRecommendedRoutes = async () => {
  try {
    // OpenAI API 호출을 위한 설정값
    loading.value = true // 로딩 시작
    const apiKey = import.meta.env.VITE_GPT_API_KEY
    const endpoint = 'https://api.openai.com/v1/chat/completions'
    const system = '너는 여행 계획을 효율적으로 짜주는 여행 플래너야.'
    // 요청 데이터 구성
    const prompt = `
      너는 여행지를 보고 효율적으로 여행할 수 있게끔 여행지별 위치를 바탕으로 최단 경로로 생각해서 일자별로 여행지 데이터를 나눠줄꺼야.
      아래 json데이터는 여행지 정보가 들어있는 배열 객체야.
      너가 나한테 답을 해줄때는 ${
        tripDuration.value
      }에 맞게끔 일자는 1일은 0 index부터 매핑되게 해주고,
      배열은 index가 0부터 1일차로 생각해서 객체를 답해줘. 현재는 모든 여행지가 index가 0에 있거나 모든 index에 있을텐데 여행 기간과 여행지의 위치와 교통 편리를 고려해서 최고의 여행 계획을 추천해줘.
      다른 설명 필요없이 아래 json데이터 형태와 똑같이 json 객체만 답으로해줘. 다른 text로 된 말은 필요없어.
      ${JSON.stringify(planDays.value)}
    `
    // const prompt = `
    //   너는 여행지를 보고 효율적으로 여행할 수 있게끔 거리별, 위치별로 생각해서 일자별로 여행지 데이터를 나눠줄꺼야.
    //   아래 json데이터는 여행지 정보가 들어있는 배열 객체야.${
    //     tripDuration.value
    //   } 기간에 맞게 아래 여행지 데이터를 위치와 교통 편리를 고려해서 json객체의 순서를 바꿔줘. 설명 필요없어 json객체만 주면돼. 제일 중요한거는 json객체만 줘.
    //         ${JSON.stringify(planDays.value)}
    // `

    const requestData = {
      model: 'gpt-4', // 사용할 모델
      messages: [
        {
          role: 'system',
          content: system,
        },
        {
          role: 'user',
          content: prompt,
        },
      ],
      temperature: 0.7, // 생성 다양성 조절
    }

    const response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${apiKey}`, // OpenAI API 키
      },
      body: JSON.stringify(requestData),
    })
    console.log(response)
    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`추천 경로 요청 실패: ${errorText}`)
    }

    const data = await response.json()

    const resultText = data.choices[0]?.message?.content
    console.log('GPT resultText:', resultText)
    // GPT 응답 데이터를 JSON으로 파싱하여 planDays 업데이트
    try {
      planDays.value = JSON.parse(resultText)
    } catch (parseError) {
      console.error('JSON 파싱 중 오류:', parseError)
    }
  } catch (error) {
    console.error('추천 경로 가져오기 중 오류:', error)
  } finally {
    loading.value = false // 로딩 종료
  }
}

const calculateTripDays = () => {
  const startDate = new Date(plan.value.startDate)
  const endDate = new Date(plan.value.endDate)
  return (endDate - startDate) / (1000 * 60 * 60 * 24) + 1
}

const updateSelectedPlacesForMap = () => {
  selectedPlacesForMap.value = planDays.value.flatMap((day) => day.places)
}

const savePlan = () => {
  showTransportModal.value = true
}

const selectTransport = (mode) => {
  sessionStorage.setItem('planDays', JSON.stringify(planDays.value))
  router.push({ name: 'travel-plan-details', query: { selectedTransport: mode } })
}

const goToSelectDestination = () => {
  sessionStorage.setItem('selectedPlaces', JSON.stringify(selectedPlacesArr.value))
  sessionStorage.setItem('planDays', JSON.stringify(planDays.value))
  router.push({ name: 'select-destination' })
}

const goToManagePlan = () => {
  // 현재 페이지 유지
}

const dragStart = (dayIndex, placeIndex) => {
  draggedItem.value = { dayIndex, placeIndex }
}

const drop = (targetDayIndex, targetPlaceIndex = null) => {
  if (draggedItem.value) {
    const { dayIndex: sourceDayIndex, placeIndex: sourcePlaceIndex } = draggedItem.value

    // 드래그한 아이템을 source에서 제거
    const [movedPlace] = planDays.value[sourceDayIndex].places.splice(sourcePlaceIndex, 1)

    // 같은 일자 내에서 순서 변경
    if (sourceDayIndex === targetDayIndex) {
      const insertIndex =
        targetPlaceIndex !== null ? targetPlaceIndex : planDays.value[targetDayIndex].places.length
      planDays.value[targetDayIndex].places.splice(insertIndex, 0, movedPlace)
    } else {
      // 다른 일자로 이동 시, 특정 위치에 삽입
      const insertIndex =
        targetPlaceIndex !== null ? targetPlaceIndex : planDays.value[targetDayIndex].places.length
      planDays.value[targetDayIndex].places.splice(insertIndex, 0, movedPlace)
    }

    // 드래그 상태 초기화
    draggedItem.value = null
    updateSelectedPlacesForMap()
  }
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
onMounted(() => {
  const storedPlanDays = sessionStorage.getItem('planDays')
  console.log('storedPlanDays:', storedPlanDays)
  console.log('selectedPlacesArr:', selectedPlacesArr.value)
  if (storedPlanDays) {
    planDays.value = JSON.parse(storedPlanDays)

    selectedPlacesArr.value.forEach((place) => {
      const isPlaceExist = planDays.value.some((day) =>
        day.places.some((storedPlace) => storedPlace.no === place.no)
      )
      if (!isPlaceExist && planDays.value[0]) {
        planDays.value[0].places.push(place)
      }
    })

    planDays.value.forEach((day) => {
      day.places = day.places.filter((place) =>
        selectedPlacesArr.value.some((selectedPlace) => selectedPlace.no === place.no)
      )
    })
  } else {
    const tripDays = calculateTripDays()
    planDays.value = Array.from({ length: tripDays }, () => ({ places: [] }))
    if (selectedPlacesArr.value.length > 0 && planDays.value[0]) {
      planDays.value[0].places = [...selectedPlacesArr.value]
    }
  }
  updateSelectedPlacesForMap()
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
.manage-plan {
  width: 100%; /* 가로를 부모 요소의 너비로 설정 */
  margin: 0 auto; /* 중앙 정렬 유지 (필요 시 적용) */
  height: calc(100vh - 100px);
  overflow-y: hidden;
}
.day-box {
  min-height: 150px;
  border: 2px dashed #ddd;
}
.place-item {
  cursor: pointer;
  background-color: #f9f9f9;
}
.place-item:hover {
  background-color: #e9e9e9;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 3000; /* 부트스트랩 모달보다 위 */
}

html,
body {
  height: 100%; /* 기본적으로 html과 body의 높이를 100%로 설정 */
  margin: 0; /* 기본 여백 제거 */
}
</style>
