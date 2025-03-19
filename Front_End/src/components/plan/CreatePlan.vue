<template>
  <div
    class="create-plan py-5 d-flex flex-column gap-5 h-75 align-items-center justify-content-center"
  >
    <div>
      <h1 class="text-center mb-4">여행 일정 만들기</h1>
    </div>
    <form @submit.prevent="validateAndProceed" class="bg-light p-4 rounded shadow w-100">
      <div class="mb-3">
        <label for="tripTitle" class="form-label">여행 제목</label>
        <input
          type="text"
          id="tripTitle"
          v-model="plan.title"
          class="form-control"
          placeholder="여행 제목을 입력하세요"
          required
        />
      </div>
      <div class="mb-3">
        <label class="form-label">여행 날짜</label>
        <div class="d-flex gap-3">
          <input type="date" v-model="plan.startDate" class="form-control" required />
          <input type="date" v-model="plan.endDate" class="form-control" required />
        </div>
      </div>
      <div class="mb-3">
        <div class="form-group">
          <label for="tripLocation" class="form-label">지역 설정</label>
          <select id="tripLocation" v-model="plan.location" class="form-select" required>
            <option value="" disabled>지역을 선택하세요</option>
            <option v-for="location in locations" :key="location.id" :value="location.id">
              {{ location.name }}
            </option>
          </select>
        </div>
      </div>
      <div class="mb-3 form-check form-switch">
        <label for="publicMode" class="form-check-label">공개 모드</label>
        <input type="checkbox" id="publicMode" v-model="plan.isPublic" class="form-check-input" />
      </div>
      <div class="d-flex justify-content-between">
        <button type="button" class="btn btn-secondary" @click="goBack">돌아가기</button>
        <!-- 여행지 선택 버튼 -->
        <button type="button" class="btn btn-primary" @click="validateAndProceed">
          여행지 선택
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const plan = ref({
  no: undefined,
  title: '',
  startDate: '',
  endDate: '',
  location: '',
  isPublic: false,
})

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

const validateAndProceed = () => {
  const missingFields = []

  if (!plan.value.title.trim()) {
    missingFields.push('여행 제목')
  }
  if (!plan.value.startDate.trim()) {
    missingFields.push('여행 시작 날짜')
  }
  if (!plan.value.endDate.trim()) {
    missingFields.push('여행 종료 날짜')
  }
  if (!plan.value.location) {
    missingFields.push('지역')
  }

  if (missingFields.length > 0) {
    alert(`다음 항목을 입력해주세요:\n- ${missingFields.join('\n- ')}`)
  } else {
    // 입력이 모두 완료된 경우 다음 단계로 진행
    sessionStorage.setItem('planData', JSON.stringify(plan.value))
    router.push({ name: 'select-destination' })
  }
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.create-plan {
  max-width: 800px;
  margin: 0 auto;
}

form {
  background-color: #f9f9f9;
}

button {
  min-width: 120px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px; /* 레이블과 셀렉트 박스 사이의 간격 */
  margin-bottom: 16px;
}
</style>
