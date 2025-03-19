<template>
  <div class="container review-container">
    <div class="review-form">
      <div class="form-header">
        <div class="author-line">
          <b-avatar size="2rem" :src="travelReview.profileImageUrl" class="author-avatar"></b-avatar>
          <span class="author-name">{{ travelReview.username }}</span>
          <span class="text-secondary fw-light">
            {{ formatDate(travelReview.registerTime) }} 조회: {{ travelReview.hit }}
          </span>
        </div>
        
        <div class="title-section">
          <div class="label-wrapper">
            <span class="label-title">제목</span>
            <span class="label-text">{{ travelReview.subject }}</span>
          </div>
        </div>
        <div class="location-section">
          <div class="label-wrapper">
            <span class="label-title">여행지</span>
            <span class="label-text">{{ travelReview.location }}</span>
          </div>
        </div>

        <div class="thumbnail-section" v-if="travelReview.thumbnailUrl">
          <div class="thumbnail-container">
            <img :src="`http://localhost:8080/tournest${travelReview.thumbnailUrl}`" 
                 alt="썸네일 이미지" 
                 class="preview-image">
          </div>
        </div>
      </div>

      <div class="content-section">
        <div class="editor-wrapper">
          <QuillEditor
            :content="travelReview.content"
            :options="editorOption"
            :readOnly="true"
            theme="snow"
          />
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn-back" @click="moveList">
          <i class="fas fa-arrow-left"></i> 목록으로
        </button>
        <div
          v-if="userInfo && userInfo.userId === travelReview.userId"
          class="action-buttons">
          <button type="button" class="btn btn-submit" @click="moveModify">
            수정하기 <i class="fas fa-edit"></i>
          </button>
          <button type="button" class="btn btn-danger" @click="onDeleteTravelReview">
            삭제하기 <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { detailTravelReview, deleteTravelReview } from "@/api/travelreview";
import { useDateFormat } from '@vueuse/core'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import Quill from 'quill'
const Delta = Quill.import('delta')

import { useAuthStore } from "@/stores/auth";
const authStore = useAuthStore();
const userInfo = authStore.user.body.user;

const editorOption = {
  modules: {
    toolbar: false
  },
  readOnly: true
}

const formatDate = (dateString) => {
  if (!dateString) return '';
  return useDateFormat(dateString, 'YYYY-MM-DD HH:mm').value;
};

const route = useRoute();
const router = useRouter();
const { travelReviewNo } = route.params;
const travelReview = ref({});

onMounted(() => {
  getTravelReview();
});

const getTravelReview = () => {
  detailTravelReview(
    travelReviewNo,
    ({ data }) => {
      travelReview.value = data;
      travelReview.value.content = new Delta(data.content);
    },
    (error) => {
      console.log(error);
    }
  );
};

function moveList() {
  router.push({ name: "travelReview-list" });
}

function moveModify() {
  router.push({ name: "travelReview-modify", params: { travelReviewNo } });
}

function onDeleteTravelReview() {
  deleteTravelReview(
    travelReviewNo,
    (response) => {
      if (response.status == 200) moveList();
    },
    (error) => {
      console.log(error);
    }
  );
}
</script>

<style scoped>
.label-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 1rem;
}

.label-title {
  min-width: 100px;
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
}

.label-text {
  min-width: 200px;
  font-size: 1rem;
  color: #495057;
}

.review-form {
  background: white;
  border-radius: 15px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
  padding: 2.5rem;
}

.form-header {
  margin-bottom: 2rem;
}

.author-line {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 1.5rem;
  padding: 12px 0;
  border-bottom: 1px solid #e9ecef;
}

.author-avatar {
  background-color: #588ef3;
}

.author-name {
  color: #2c3e50;
  font-weight: 500;
  padding-left: 8px;
}

.title-section {
  margin-bottom: 1.5rem;
}

.location-section {
  margin-bottom: 1.5rem;
}

.location-text {
  color: #495057;
  font-size: 1rem;
  padding: 0.5rem 0;
}

.thumbnail-section {
  margin-bottom: 2rem;
}

.thumbnail-container {
  width: 400px;
  height: 300px;
  margin: 0 auto;
  border-radius: 12px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.content-section {
  margin-bottom: 2rem;
}

.editor-wrapper {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.ql-toolbar) {
  border: 2px solid #e9ecef !important;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  background-color: #f8f9fa;
}

:deep(.ql-container) {
  border: 2px solid #e9ecef !important;
  border-top: none !important;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
  min-height: 400px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e9ecef;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

.btn {
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-back {
  background-color: #f8f9fa;
  color: #495057;
  border: 2px solid #e9ecef;
}

.btn-back:hover {
  background-color: #e9ecef;
}

.btn-submit {
  background-color: #588ef3;
  color: white;
  border: none;
}

.btn-submit:hover {
  background-color: #4070d0;
  transform: translateY(-1px);
}

.btn-danger {
  background-color: #dc3545;
  color: white;
  border: none;
}

.btn-danger:hover {
  background-color: #bb2d3b;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .review-form {
    padding: 1.5rem;
  }
  
  .btn {
    padding: 0.6rem 1rem;
  }
}
</style>