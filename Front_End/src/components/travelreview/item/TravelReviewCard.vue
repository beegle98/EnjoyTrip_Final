<template>
    <div class="schedule-card" @click="handleClick">
      <div class="schedule-header">
        <img :src="review.profileImageUrl" class="user-avatar" />
        <span class="username">{{ review.username }}</span>
      </div>
      <img :src="`http://localhost:8080/tournest${review.thumbnailUrl}`" class="schedule-thumbnail" />
      <div class="schedule-info">
        <h3>제목: {{ review.subject }}</h3>
        <p>여행지: {{ review.location }}</p>
        <div class="schedule-stats">
          <div>{{review.hit}} views</div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  const props = defineProps({
    review: {
      type: Object,
      required: true
    }
  });
  
  const emit = defineEmits(['click']);
  
  const handleClick = () => {
    emit('click', props.review.travelReviewNo);
  };
  </script>
  
  <style scoped>
   .schedule-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
  }
  
  .schedule-card:hover {
  transform: translateY(-5px);
  }
  
  .schedule-thumbnail {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 10px;
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
  
  .schedule-info {
  padding: 10px 0;
  text-align: left;
  }
  
  .schedule-stats {
  display: flex;
  gap: 15px;
  margin-top: 10px;
  color: #666;
  }
  </style>