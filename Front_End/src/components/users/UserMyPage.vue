<template>
  <div class="my-page container py-4">
    <!-- 프로필 섹션 -->
    <ProfileSection class="mb-4" />

    <!-- 통계 버튼 섹션 -->
    <StatButtons :stats="stats" @tabChange="changeTab" class="mb-4" />

    <!-- 카드 리스트 섹션 -->
    <div>
      <CardList
        v-if="activeTab === '나의 일정'"
        :cards="myPlans"
        title="나의 일정"
        @cardClick="handleCardClick"
        @deleteClick="handleDelete"
      />
      <div v-else-if="activeTab === '여행지 리뷰'">
        <div class="schedule-grid">
          <div
            v-for="post in myReviews"
            :key="post.travelReviewNo"
            class="schedule-card"
            @click="goToDetail(post.travelReviewNo)"
          >
            <div class="schedule-header">
              <img :src="post.profileImageUrl" class="user-avatar" />
              <span class="username">{{ post.username }}</span>
            </div>
            <img
              :src="`http://localhost:8080/tournest${post.thumbnailUrl}`"
              class="schedule-thumbnail"
            />
            <div class="schedule-info">
              <h3>제목: {{ post.subject }}</h3>
              <p>여행지: {{ post.location }}</p>
              <div class="schedule-stats">
                <div>{{ post.hit }} views</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="activeTab === '관심 여행지'">
        <div class="card-list">
          <div
            v-for="(card, index) in myLikes"
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import ProfileSection from '@/components/users/ProfileSection.vue'
import StatButtons from '@/components/users/StatButtons.vue'
import CardList from '@/components/users/CardList.vue'
import { listplan, planInfo, deletePlan } from '@/api/plan'
import { listUserTravelReview } from '@/api/travelreview'
import { useRouter } from 'vue-router'
import { listMyAttraction, changeLike } from '@/api/news'
import defaultImage from '@/assets/logo_default.png'
const router = useRouter()
const authStore = useAuthStore()
const placeholderImage = defaultImage
const hoveredCardIndex = ref(null)
const user = authStore.user.body.user
const stats = ref({
  '나의 일정': 0,
  '여행지 리뷰': 0,
  '관심 여행지': 0,
})
const goToDetail = (travelReviewNo) => {
  router.push({ name: 'travelReview-view', params: { travelReviewNo } })
}
const handleDelete = (card) => {
  deletePlan(
    card.id,
    (response) => {
      console.log('삭제 성공:', response.data)
      // UI에서 삭제된 항목 제거 처리
      userPlan()
    },
    (error) => {
      console.error('삭제 실패:', error.response?.data || error.message)
    }
  )
}

const goToNews = (link) => {
  if (link == null || link === '') {
    console.error('링크 없음')
    return
  } else {
    window.open(link, '_blank')
  }
}

const userPlan = () => {
  listplan(
    user.userSeq,
    (response) => {
      console.log('여행 계획 데이터:', response.data.length)
      myPlans.value = response.data
      stats.value['나의 일정'] = response.data.length
    },
    (error) => {
      console.error('여행 계획 데이터 로드 오류:', error)
    }
  )
}
const toggleLike = (cardId) => {
  changeLike(
    cardId,
    (response) => {
      const liked = response.data.liked
      console.log('liked', liked)

      // travelCards 상태 업데이트
      myLikes.value = myLikes.value.map((card) => (card.id === cardId ? { ...card, liked } : card))
      userLikes()
    },
    (error) => {
      console.error('Error changing like status:', error)
    }
  )
}
const userLikes = () => {
  listMyAttraction(
    (response) => {
      myLikes.value = response.data
      stats.value['관심 여행지'] = response.data.length
    },
    (error) => {
      console.error('좋아하는 여행지 데이터 로드 오류', error)
    }
  )
}

const userTravelReviews = () => {
  listUserTravelReview(
    user.userId,
    (response) => {
      console.log('유저 여행 리뷰 데이터:', response.data.length)
      myReviews.value = response.data
      stats.value['여행지 리뷰'] = response.data.length
    },
    (error) => {
      console.error('유저 여행 리뷰 데이터 로드 오류:', error)
    }
  )
}
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

const activeTab = ref('나의 일정')
onMounted(() => {
  userPlan()
  userTravelReviews()
  userLikes()
})
const myPlans = ref([])

const myReviews = ref([])

const myLikes = ref([])

const changeTab = (tabName) => {
  activeTab.value = tabName
}
</script>

<style scoped>
.schedule-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.schedule-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
  cursor: pointer;
}

.schedule-card:hover {
  transform: translateY(-5px);
}

.schedule-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.schedule-thumbnail {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 10px;
}

.schedule-info {
  padding: 10px 0;
}

.schedule-info h3 {
  margin: 0 0 8px 0;
  font-size: 1.2rem;
}

.schedule-info p {
  margin: 0;
  color: #666;
}

.schedule-stats {
  display: flex;
  gap: 15px;
  margin-top: 10px;
  color: #666;
  font-size: 0.9rem;
}

.username {
  font-weight: 500;
  color: #333;
}

.my-page {
  max-width: 1200px;
  margin: 0 auto;
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
