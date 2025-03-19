<template>
    <div class="post-card" @click="$emit('click')">
      <img :src="post.imageUrl" :alt="post.title" />
      <div class="post-info">
        <h3>{{ post.title }}</h3>
        <div class="post-metadata">
          <span>❤️ {{ post.likes }}</span>
          <span>👁️ {{ post.views }}</span>
          <span>💬 {{ post.comments }}</span>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  defineProps({
    post: {
      type: Object,
      required: true
    }
  })
  </script>