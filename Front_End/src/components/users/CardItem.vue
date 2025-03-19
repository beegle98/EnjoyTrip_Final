<template>
  <div class="card-item col-12 col-md-4" @mouseenter="hovered = true" @mouseleave="hovered = false">
    <!-- 이미지 및 정보 -->
    <div class="card-image">
      <img :src="getLocationImage(card.location)" alt="Card Image" />
    </div>
    <div class="card-info">
      <h2>{{ getLocationName(card.location) }}</h2>
      <!-- 지역 이름 표시 -->
      <h5>{{ card.title }}</h5>
      <p>D-여행 중 | {{ card.startDate }} ~ {{ card.endDate }}</p>
    </div>

    <!-- 버튼 영역 -->
    <div class="card-buttons" v-if="hovered">
      <button class="btn btn-primary" @click="$emit('cardClick', card)">자세히 보기</button>
      <button
        class="btn btn-danger"
        v-if="card.user_seq === user.userSeq"
        @click="$emit('deleteClick', card)"
      >
        삭제
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const user = authStore.user.body.user // 로그인한 사용자 정보
const hovered = ref(false) // 마우스 호버 상태

defineProps({
  card: {
    type: Object,
    required: true,
  },
})

// 이미지 경로 계산
const getLocationImage = (locationId) => {
  if (locationId > 0) {
    return `/src/assets/localPicture/${locationId}.jpg` // 문자열로 경로 생성
  }
  return '/src/assets/localPicture/default.jpg' // 기본 이미지 경로
}

// 지역 목록
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

// 지역 이름 계산 함수
const getLocationName = (locationId) => {
  const location = locations.value.find((loc) => loc.id === locationId)
  return location ? location.name : '알 수 없음'
}
</script>

<style scoped>
.card-item {
  position: relative;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s, background-color 0.2s;
}

.card-item:hover {
  background-color: #f0f8ff;
  transform: scale(1.03);
}

.card-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.card-info {
  padding: 16px;
  text-align: center;
}

.card-info h5 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.card-info p {
  margin: 8px 0 0;
  font-size: 14px;
  color: #555;
}

.card-buttons {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  gap: 8px;
  z-index: 10;
}

.btn {
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary {
  background-color: #007bff;
  color: #fff;
  border: none;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-danger {
  background-color: #dc3545;
  color: #fff;
  border: none;
}

.btn-danger:hover {
  background-color: #a71d2a;
}
</style>
