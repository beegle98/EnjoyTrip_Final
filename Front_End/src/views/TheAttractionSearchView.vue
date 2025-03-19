<template>
  <div class="travel-search-container">
    <!-- 검색 영역 -->
    <div class="search-bar">
      <select v-model="selectedRegion" class="region-select">
        <option value="0">전체</option>
        <option v-for="region in regions" :key="region.id" :value="region.id">
          {{ region.name }}
        </option>
      </select>
      <input
        v-model="searchKeyword"
        type="text"
        class="search-input"
        placeholder="검색어를 입력하세요"
      />
      <button @click="resetAndFetchTravelData" class="search-button">검색</button>
    </div>

    <!-- 카드 리스트 -->
    <div class="card-list">
      <div
        v-for="(card, index) in travelCards"
        :key="index"
        class="travel-card"
        @mouseover="hoveredCardIndex = index"
        @mouseleave="hoveredCardIndex = null"
      >
        <!-- 여행지 이미지 -->
        <img :src="card.image || placeholderImage" class="card-image" />

        <div class="card-info">
          <div class="card-name">{{ card.name }}</div>
          <div class="card-title">{{ card.title }}</div>
        </div>

        <!-- 좋아요 버튼 -->
        <button class="like-button" :class="{ liked: card.liked }" @click="toggleLike(card.id)">
          <font-awesome-icon :icon="[card.liked ? 'fas' : 'far', 'heart']" />
        </button>

        <!-- 자세히 보기 버튼 -->
        <button
          v-if="hoveredCardIndex === index"
          class="details-button"
          @click="goToNews(card.link)"
        >
          자세히 보기
        </button>
      </div>
    </div>

    <!-- 스크롤 감지용 엘리먼트 -->
    <div ref="scrollObserver" class="scroll-observer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { listSearchAttraction, changeLike } from '@/api/news'
import defaultImage from '@/assets/logo_default.png'
// 여행지 데이터
const travelCards = ref([])
const currentPage = ref(0)
const isFetching = ref(false)

// 검색 상태
const searchKeyword = ref('')
const selectedRegion = ref('0')

// 마우스 오버 상태
const hoveredCardIndex = ref(null)

// 지역 데이터
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
])

// 뉴스 페이지 열기
const goToNews = (link) => {
  window.open(link, '_blank')
}
// 기본 이미지 경로
const placeholderImage = defaultImage

// 좋아요 토글 함수 수정
const toggleLike = (cardId) => {
  changeLike(
    cardId,
    (response) => {
      const liked = response.data.liked
      console.log('liked', liked)

      // travelCards 상태 업데이트
      travelCards.value = travelCards.value.map((card) =>
        card.id === cardId ? { ...card, liked } : card
      )
    },
    (error) => {
      console.error('Error changing like status:', error)
    }
  )
}

// 서버에서 데이터 가져오기
const fetchTravelData = async () => {
  if (isFetching.value) return
  isFetching.value = true

  try {
    const params = {
      article: searchKeyword.value, // 검색 키워드
      areaCode: selectedRegion.value, // 선택된 지역 코드
      contentType: 0, // 선택된 여행지 유형 (전체: 0)
      page: currentPage.value, // 현재 페이지
      size: 30, // 한 페이지의 항목 수
    }
    listSearchAttraction(
      params,
      (response) => {
        console.log('Fetched attractions:', response)
        const newCards = response.data.map((item) => ({
          id: item.id,
          name: item.name,
          title: item.title,
          image: item.image || placeholderImage,
          liked: item.liked,
          link: item.link,
        }))
        travelCards.value = [...travelCards.value, ...newCards]
        currentPage.value++
      },
      (error) => {
        console.error('Error fetching attractions:', error)
      }
    )

    console.log('Fetching page:', currentPage.value)
    // Replace this with actual API call
  } catch (error) {
    console.error('Error fetching data:', error)
  } finally {
    isFetching.value = false
  }
}

// 검색 시 초기화 및 데이터 로드
const resetAndFetchTravelData = () => {
  currentPage.value = 0
  travelCards.value = []
  fetchTravelData()
}

// IntersectionObserver로 무한 스크롤 감지
const scrollObserver = ref(null)
const observer = new IntersectionObserver(
  (entries) => {
    if (entries[0].isIntersecting && !isFetching.value) {
      fetchTravelData()
    }
  },
  { rootMargin: '100px' }
)

// Observer 초기화 및 해제
onMounted(() => {
  fetchTravelData()
  if (scrollObserver.value) observer.observe(scrollObserver.value)
})
onBeforeUnmount(() => {
  if (scrollObserver.value) observer.unobserve(scrollObserver.value)
})
</script>

<style scoped>
/* 컨테이너 */
.travel-search-container {
  width: 100%;
  padding: 20px;
  overflow-y: auto; /* 스크롤 가능 */
  -ms-overflow-style: none; /* IE, Edge 스크롤바 숨김 */
  scrollbar-width: none; /* Firefox 스크롤바 숨김 */
}

.travel-search-container::-webkit-scrollbar {
  display: none; /* 크롬, 사파리 스크롤바 숨김 */
}

/* 검색바 */
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.region-select,
.search-input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 카드 리스트 */
.card-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

/* 카드 */
.travel-card {
  position: relative;
  width: 300px;
  border: 1px solid #ccc;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  padding-bottom: 40px; /* 버튼들을 위한 공간 확보 */
}

.travel-card:hover {
  transform: scale(1.05);
}

/* 버튼 영역 */
.like-button {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: gray;
  z-index: 1;
}

.details-button {
  position: absolute;
  bottom: 10px;
  left: 10px;
  padding: 5px 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  z-index: 1;
}

.scroll-observer {
  height: 1px;
}

/* 이미지 크기 조정 */
.card-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

/* 카드 영역 */

.card-info {
  padding: 10px;
  margin-bottom: 30px; /* 버튼과의 간격 */
}

.card-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.card-title {
  font-size: 0.9em;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}
.liked {
  color: red;
}
</style>
