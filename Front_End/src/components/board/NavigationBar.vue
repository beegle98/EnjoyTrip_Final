<template>
    <nav class="navigation">
      <router-link to="/" class="nav-logo">TOURNEST</router-link>
      <div class="nav-items">
        <router-link to="/" :class="{ active: currentPath === '/' }">전체</router-link>
        <router-link to="/travel-review" :class="{ active: currentPath === '/travel-review' }">
          여행 리뷰
        </router-link>
        <router-link to="/schedule-review" :class="{ active: currentPath === '/schedule-review' }">
          일정 리뷰
        </router-link>
      </div>
    </nav>
  </template>
  
  <script setup>
  import { computed } from 'vue'
  import { useRoute } from 'vue-router'
  
  const route = useRoute()
  const currentPath = computed(() => route.path)
  </script>