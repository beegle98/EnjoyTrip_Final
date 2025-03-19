<script setup>
import TravelReviewFormItem from "./item/TravelReviewFormItem.vue";
</script>

<template>
  <div class="container review-container">
    <div class="row justify-content-center">
      <div class="col-lg-10">
        <h2 class="page-title">
          나만의 특별한 여행 이야기
          <small class="subtitle">소중한 여행 경험을 공유해주세요</small>
        </h2>
      </div>
      <div class="col-lg-10">
        <div class="review-form-wrapper">
          <TravelReviewFormItem type="modify" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.review-container {
  padding: 40px 20px;
  background-color: #fafafa;
  min-height: 100vh;
}

.page-title {
  position: relative;
  text-align: center;
  color: #2c3e50;
  font-weight: 700;
  margin-bottom: 2.5rem;
  padding: 1.5rem 0;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background-color: #588ef3;
}

.subtitle {
  display: block;
  font-size: 1rem;
  color: #666;
  margin-top: 0.5rem;
  font-weight: normal;
}

.review-form-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.05);
  padding: 2rem;
  margin-bottom: 3rem;
  transition: transform 0.2s ease;
}

.review-form-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

@media (max-width: 768px) {
  .review-container {
    padding: 20px 10px;
  }
  
  .review-form-wrapper {
    padding: 1.5rem;
  }
  
  .page-title {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
  }
}
</style>