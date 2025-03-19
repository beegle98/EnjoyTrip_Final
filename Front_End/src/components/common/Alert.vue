<template>
  <Teleport to="body">
    <div v-if="alertStore.show" class="alert-overlay" @click="handleOverlayClick">
      <div class="alert" :class="`alert-${alertStore.type}`" @click.stop>
        <p>{{ alertStore.message }}</p>
        <div class="alert-buttons">
          <button v-if="alertStore.action" @click="handleAction" class="action-button">
            {{ alertStore.action.text }}
          </button>
          <button @click="alertStore.hideAlert" class="close-button">닫기</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { useAlertStore } from '@/stores/alert'

const alertStore = useAlertStore()

const handleAction = () => {
  if (alertStore.action && alertStore.action.callback) {
    alertStore.action.callback()
  }
  alertStore.hideAlert()
}

const handleOverlayClick = (event) => {
  if (event.target === event.currentTarget) {
    alertStore.hideAlert()
  }
}
</script>
<style scoped>
.alert-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.alert {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  max-width: 400px;
  width: 90%;
  margin: 0 auto;
  text-align: center; /* 추가: 텍스트를 중앙 정렬 */
}

.alert p {
  font-family: 'Arial', sans-serif;
  color: #333;
  margin-bottom: 15px;
  font-size: 16px;
  line-height: 1.5; /* 추가: 줄 간격 조정 */
}

.alert-buttons {
  display: flex;
  justify-content: center; /* 변경: 버튼을 중앙 정렬 */
  margin-top: 20px; /* 변경: 여백 증가 */
}

button {
  padding: 10px 20px; /* 변경: 버튼 패딩 증가 */
  margin: 0 5px; /* 변경: 좌우 마진 추가 */
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease; /* 추가: 부드러운 전환 효과 */
}

.action-button {
  background-color: #98DFE3;;
  color: white;
}

.action-button:hover {
  background-color: #7fcfd3;
  transform: translateY(-2px); /* 추가: 호버 시 약간 위로 이동 */
}

.close-button {
  background-color: #e0e0e0;
  color: #333;
}

.close-button:hover {
  background-color: #d5d5d5;
  transform: translateY(-2px); /* 추가: 호버 시 약간 위로 이동 */
}
</style>

