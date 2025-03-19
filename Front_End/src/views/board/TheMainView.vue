<template>
    <div class="main-view">
      <section class="travel-review-section">
        <div class="section-header">
          <h2>여행지 리뷰</h2>
          <router-link to="/travel-review" class="view-all">전체보기</router-link>
        </div>
        <div class="post-grid">
          <PostCard
            v-for="post in travelReviews.slice(0, 4)"
            :key="post.id"
            :post="post"
            @click="navigateToDetail('travel', post.id)"
          />
        </div>
      </section>
  
      <section class="schedule-review-section">
        <div class="section-header">
          <h2>여행 일정</h2>
          <router-link to="/schedule-review" class="view-all">전체보기</router-link>
        </div>
        <div class="post-grid">
          <PostCard
            v-for="post in planReviews.slice(0, 4)"
            :key="post.id"
            :post="post"
            @click="navigateToDetail('schedule', post.id)"
          />
        </div>
      </section>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import PostCard from '@/components/board/PostCard.vue'
  
  const router = useRouter()
  const travelReviews = ref([])
  const planReviews = ref([])
  
  onMounted(async () => {
    // API 호출하여 데이터 가져오기
  })
  
  const navigateToDetail = (type, postId) => {
    router.push(`/${type}-review/${postId}`)
  }
  </script>