<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { listNews } from '@/api/news'
const router = useRouter()

const createplan = () => {
  router.push({ name: 'plan' })
}
const newsCards = ref([])

// "오늘의 뉴스" 데이터 가져오기
const fetchTodayNews = async () => {
  listNews(
    (response) => {
      console.log('성공:', response.data)
      newsCards.value = response.data
    },
    (error) => {
      console.error('실패:', error.response?.data || error.message)
    }
  )
}

// 컴포넌트 마운트 시 뉴스 데이터 로드
onMounted(() => {
  fetchTodayNews()
})
</script>

<template>
  <div>
    <main class="d-flex align-items-center justify-content-between my-auto vh-100">
      <!-- Left Section: Text and Button -->
      <div class="col-md-6 pe-md-4">
        <h2 class="display-5 fw-bold">기존에 경험하지 못한 새로운 여행 플래너</h2>
        <p>고민만 하던 여행 계획을 <strong>TOURNEST</strong>를 통해 몇 분 만에 스케줄링 해보세요.</p>
        <button class="btn btn-lg mt-2 custom-btn" @click="createplan"><span>TourNest 시작하기</span></button>
      </div>

      <!-- Right Section: Map Image -->
      <div class="col-md-6 text-center">
        <img src="@/assets/map.png" alt="여행지 지도" class="img-fluid rounded shadow map-image" />
      </div>
    </main>
  </div>
  <section class="news-section py-5">
    <h3 class="mb-4 text-center">오늘의 여행 소식</h3>
    <div class="container">
      <div class="row g-3">
        <!-- 뉴스 카드 반복 출력 -->
        <div class="col-6 col-md-3" v-for="(news, index) in newsCards" :key="index">
          <div class="card shadow-sm">
            <img
              :src="news.imageUrl || 'https://via.placeholder.com/300x200'"
              class="card-img-top"
              alt="뉴스 이미지"
            />
            <div class="card-body">
              <h5 class="card-title">{{ news.title }}</h5>
              <p class="card-text">{{ news.summary }}</p>
              <a :href="news.link" target="_blank" class="btn btn-primary btn-sm"> 자세히 보기 </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
main {
  height: calc(100vh - 100px); /* Adjust height based on header/footer size */
  padding: 60px;

}

h2 {
  color: #333;
  font-size: 4rem;
}

button {
  background-color: #000;
  color: #fff;
}
/* Update style for the map image */
.map-image {
  width: 140%;       /* Make width responsive */
  height: auto;      /* Maintain aspect ratio */
  max-width: 800px;  /* Set a max width for large screens */
}

.custom-btn {
  border: none;
  display: block;
  text-align: center;
  cursor: pointer;
  text-transform: uppercase;
  outline: none;
  overflow: hidden;
  position: relative;
  color: #fff;
  font-weight: 700;
  font-size: 15px;
  background-color: #222;
  padding: 17px 60px;
  margin: 0 auto;
  box-shadow: 0 5px 15px rgba(0,0,0,0.20);
}

.custom-btn span {
  position: relative; 
  z-index: 1;
}

.custom-btn:after {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  height: 490%;
  width: 140%;
  background: #98DFE3;
  -webkit-transition: all .5s ease-in-out;
  transition: all .5s ease-in-out;
  -webkit-transform: translateX(-98%) translateY(-25%) rotate(45deg);
  transform: translateX(-98%) translateY(-25%) rotate(45deg);
}

.custom-btn:hover:after {
  -webkit-transform: translateX(-9%) translateY(-25%) rotate(45deg);
  transform: translateX(-9%) translateY(-25%) rotate(45deg);
}
.news-section {
  background-color: #f9f9f9;
}

.news-section h3 {
  font-size: 1.8rem;
  color: #333;
}

.card {
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.card:hover {
  transform: scale(1.03);
}

.card-img-top {
  height: 150px;
  object-fit: cover;
}

.card-title {
  font-size: 1rem;
  font-weight: bold;
  color: #212529;
}

.card-text {
  font-size: 0.9rem;
  color: #495057;
  margin-bottom: 10px;
}

.btn-primary {
  font-size: 0.8rem;
}
</style>
