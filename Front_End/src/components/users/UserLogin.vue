<!-- LoginView.vue -->
<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useMenuStore } from "@/stores/menu";
const {VITE_TOURNEST_API_URL } = import.meta.env;
const router = useRouter();
const authStore = useAuthStore();
const menuStore = useMenuStore();
// 폼 데이터
const username = ref('');
const password = ref('');
const loginError = ref('');
const isLoading = ref(false);

// 일반 로그인
const handleLogin = async (e) => {
  e.preventDefault();

  if (!username.value || !password.value) {
    loginError.value = '아이디와 비밀번호를 모두 입력해주세요';
    return;
  }

  try {
    isLoading.value = true;
    await authStore.login(username.value, password.value);
    router.push('/');
  } catch (error) {
    loginError.value = '아이디 또는 비밀번호가 올바르지 않습니다';
    console.error('Login error:', error);
  } finally {
    isLoading.value = false;
  }
};

// OAuth 로그인
// url은 맞게 수정해줘야 됨
const kakaoLogin = () => {
  window.location.href = `${VITE_TOURNEST_API_URL}/oauth2/authorization/kakao?redirect_uri=http://localhost:3000/oauth/redirect`;
};

const naverLogin = () => {
  window.location.href = `${VITE_TOURNEST_API_URL}/oauth2/authorization/naver?redirect_uri=http://localhost:3000/oauth/redirect`;
};

const googleLogin = () => {
  window.location.href = `${VITE_TOURNEST_API_URL}/oauth2/authorization/google?redirect_uri=http://localhost:3000/oauth/redirect`;
};
</script>

<template>
  <div class="login">
    <form class="login-form" @submit="handleLogin">
      <!-- ID Field -->
      <div class="mb-3">
        <label for="username" class="form-label custom-label">아이디</label>
        <input
          type="text"
          id="username"
          v-model="username"
          class="form-control"
          placeholder="아이디 입력"
          :class="{ 'is-invalid': loginError }"
          :disabled="isLoading"
        />
      </div>

      <!-- Password Field -->
      <div class="mb-3">
        <label for="password" class="form-label custom-label">비밀번호</label>
        <input
          type="password"
          id="password"
          v-model="password"
          class="form-control"
          placeholder="비밀번호 입력"
          :class="{ 'is-invalid': loginError }"
          :disabled="isLoading"
        />
        <!-- Error Message -->
        <div class="error-message" v-if="loginError">
          {{ loginError }}
        </div>
      </div>

      <!-- Login Button -->
      <button
        type="submit"
        class="btn custom-btn w-100"
        :disabled="isLoading"
      >
        <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
        {{ isLoading ? '로그인 중...' : '로그인' }}
      </button>

      <!-- Additional Links -->
      <!-- <div class="d-flex justify-content-center mt-4 gap-4">
        <a href="/user/find-id" class="text-decoration-none">아이디 찾기</a>
        <a href="/user/find-password" class="text-decoration-none">비밀번호 변경</a>
      </div> -->
      <p class="text-center mt-3">
          아직 계정이 없으신가요?
          <a href="/user/join" class="text-decoration-none">회원가입</a>
        </p>
      <!-- SNS Login Options -->
      <hr />
      <p class="text-center">SNS 간편 로그인</p>
      <div class="d-flex justify-content-around">
        <img
          src="@/assets/OAuth/kakao-icon.png"
          alt="Kakao Login"
          width="40px"
          @click="kakaoLogin"
          class="oauth-icon"
          :class="{ 'disabled': isLoading }"
        />
        <img
          src="@/assets/OAuth/naver-icon.png"
          alt="Naver Login"
          width="40px"
          @click="naverLogin"
          class="oauth-icon"
          :class="{ 'disabled': isLoading }"
        />
        <img
          src="@/assets/OAuth/google-icon.png"
          alt="Google Login"
          width="40px"
          @click="googleLogin"
          class="oauth-icon"
          :class="{ 'disabled': isLoading }"
        />
      </div>
    </form>
  </div>
</template>

<style scoped>
.login-form {
  width: 50%;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.custom-label {
  text-align: left;
  margin-bottom: 10px;
  font-weight: bold;
  color: #444;
  width: 100%;
}

.error-message {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.oauth-icon {
  cursor: pointer;
  transition: transform 0.2s;
}

.oauth-icon:hover:not(.disabled) {
  transform: scale(1.1);
}

.oauth-icon.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.is-invalid {
  border-color: #dc3545;
}

.is-invalid:focus {
  border-color: #dc3545;
  box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
}
/* 커스텀 버튼 */
.custom-btn {
  background-color: #98DFE3;
  border-color: #98DFE3;
  color: white;
  box-shadow: 0px 0px 0px 0px rgba(0, 0, 0, 0.5);

}
.custom-btn:hover {
  background-color: #7fcfd3; /* 호버 시 약간 어두운 색상 */
  border-color: #7fcfd3;
}

.custom-btn:focus {
  background-color: #7fcfd3;
  border-color: #7fcfd3;
  box-shadow: 0 0 0 0.25rem rgba(152, 223, 227, 0.25);
}

.custom-btn:disabled {
  background-color: #98DFE3;
  border-color: #98DFE3;
  opacity: 0.65;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .login-form {
    width: 90%;
  }
}
</style>
