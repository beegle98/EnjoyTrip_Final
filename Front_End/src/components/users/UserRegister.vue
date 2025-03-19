<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';

// 입력 필드 상태
const userId = ref('');
const username = ref('');
const email = ref('');
const password = ref('');
const passwordCheck = ref('');
const verificationCode = ref('');

// 유효성 상태
const isIdAvailable = ref(false);
const isEmailAvailable = ref(false);
const isEmailVerified = ref(false);
const showVerificationInput = ref(false);

// 에러/성공 메시지
const userIdError = ref('');
const emailError = ref('');
const verificationError = ref('');
const passwordError = ref('');
const passwordCheckError = ref('');

// 아이디 중복 체크
const checkDuplicateId = async () => {
  if (!userId.value) {
    userIdError.value = '아이디를 입력해주세요';
    return;
  }
  isIdAvailable.value = true;
  userIdError.value = '사용 가능한 아이디입니다';
  try {
    const response = await axios.post('http://localhost:8080/tournest/api/v1/users/check-userId', {
      userId: userId.value
    });
    
    if (response.data.available) {
      isIdAvailable.value = true;
      userIdError.value = '사용 가능한 아이디입니다';
    } else {
      isIdAvailable.value = false;
      userIdError.value = '이미 사용 중인 아이디입니다';
    }
  } catch (error) {
    isIdAvailable.value = false;
    userIdError.value = '아이디 중복 체크에 실패했습니다';
  }
};

const checkDuplicateEmail = async () => {
  if (!userId.value) {
    emailError.value = '이메일을 입력해주세요';
    return;
  }
  isEmailAvailable.value = true;
  emailError.value = '사용 가능한 이메일입니다';
  try {
    const response = await axios.post('http://localhost:8080/tournest/api/v1/users/check-email', {
        email: email.value
    });
    
    if (response.data.available) {
        isEmailAvailable.value = true;
        emailError.value = '사용 가능한 이메일입니다';
    } else {
        isEmailAvailable.value = false;
        emailError.value = '이미 사용 중인 이메일입니다';
    }
} catch (error) {
    isEmailAvailable.value = false;
    emailError.value = '이메일 중복 체크에 실패했습니다';
}
}

// 이메일 인증 코드 발송
const sendVerificationEmail = async () => {
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!email.value) {
    emailError.value = '이메일을 입력해주세요';
    return;
  }
  if (!emailPattern.test(email.value)) {
    emailError.value = '이메일 형식으로 입력해주세요';
    return;
  }
  showVerificationInput.value = true;
  emailError.value = '인증 코드가 이메일로 발송되었습니다';
  // try {
  //   await axios.post('/api/send-verification', {
  //     email: email.value
  //   });
    
  //   showVerificationInput.value = true;
  //   emailError.value = '인증 코드가 이메일로 발송되었습니다';
  // } catch (error) {
  //   emailError.value = '인증 코드 발송에 실패했습니다';
  // }
};

// 이메일 인증 코드 확인
const verifyEmail = async (email) => {
  
  if (!verificationCode.value) {
    verificationError.value = '인증 코드를 입력해주세요';
    return;
  }
  
  try {
    // const response = await axios.post('/api/verify-email', {
    //   email: email.value,
    //   code: verificationCode.value
    // });
    
    //if (response.data.verified) {
    const tempResponse = true;
    if(tempResponse) {
      isEmailVerified.value = true;
      verificationError.value = '이메일 인증이 완료되었습니다';
    } else {
      isEmailVerified.value = false;
      verificationError.value = '잘못된 인증 코드입니다';
    }
  } catch (error) {
    verificationError.value = '이메일 인증에 실패했습니다';
  }
};

// 비밀번호 유효성 검사
const validatePassword = () => {
  if (password.value.length < 8) {
    passwordError.value = '비밀번호는 8자 이상이어야 합니다';
    return false;
  }
  
  const hasLetter = /[a-zA-Z]/.test(password.value);
  const hasNumber = /[0-9]/.test(password.value);
  const hasSpecial = /[!@#$%^&*]/.test(password.value);
  
  if (!hasLetter || !hasNumber || !hasSpecial) {
    passwordError.value = '비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다';
    return false;
  }
  
  passwordError.value = '사용 가능한 비밀번호입니다';
  return true;
};

// 비밀번호 확인 검사
const validatePasswordCheck = () => {
  if (!passwordCheck.value) {
    passwordCheckError.value = '비밀번호 확인을 입력해주세요';
    return false;
  }
  
  if (password.value !== passwordCheck.value) {
    passwordCheckError.value = '비밀번호가 일치하지 않습니다';
    return false;
  }
  
  passwordCheckError.value = '비밀번호가 일치합니다';
  return true;
};

// 비밀번호 실시간 검사
watch(password, () => {
  validatePassword();
  if (passwordCheck.value) {
    validatePasswordCheck();
  }
});

// 비밀번호 확인 실시간 검사
watch(passwordCheck, () => {
  validatePasswordCheck();
});

// 회원가입 제출
const handleSubmit = async (e) => {
  e.preventDefault();
  
  if (!isIdAvailable.value) {
    userIdError.value = '아이디 중복 확인이 필요합니다';
    return;
  }

    
  if (!isEmailAvailable.value) {
    emailError.value = '이메일 중복 확인이 필요합니다';
    return;
  }
  
  // if (!isEmailVerified.value) {
  //   emailError.value = '이메일 인증이 필요합니다';
  //   return;
  // }
  
  if (!validatePassword() || !validatePasswordCheck()) {
    return;
  }
  
  // 회원가입 API 호출 로직 구현
  try {
    const formData = new FormData();
    formData.append('userId', userId.value);
    formData.append('username', username.value);
    formData.append('password', password.value);
    formData.append('email', email.value);
    
    if (profileImage.value) {
      formData.append('profileImage', profileImage.value);
    }
    console.log('formData:', formData.get('userId'));
    const response = await axios.post(
      'http://localhost:8080/tournest/api/v1/users/signup', 
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'  // 대소문자 주의
        },
        withCredentials: true  // CORS 관련 쿠키 전송을 위해 필요
      }
    );

    if (response.status === 200) {
      // 회원가입 성공 시 로그인 페이지로 이동
      window.location.href = '/user/login';
    }
  } catch (error) {
    console.error('회원가입 실패:', error);
    if (error.response) {
      // 서버에서 에러 메시지를 보내는 경우
      alert(error.response.data.message || '회원가입에 실패했습니다.');
    } else {
      alert('회원가입 처리 중 오류가 발생했습니다.');
    }
  }
};

// 프로필 이미지 관련 상태
const profileImage = ref(null);
const imagePreview = ref('');
const imageError = ref('');

// 이미지 업로드 처리
const handleImageUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;
  
  // 파일 크기 제한 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    imageError.value = '이미지 크기는 5MB 이하여야 합니다';
    return;
  }
  
  // 이미지 파일 형식 검사
  if (!file.type.match('image.*')) {
    imageError.value = '이미지 파일만 업로드 가능합니다';
    return;
  }
  console.log('file:', file);
  profileImage.value = file;
  console.log(' profileImage:', profileImage.value);
  imagePreview.value = URL.createObjectURL(file);
  console.log('imagePreview:', imagePreview.value);
  imageError.value = '';
};

// 이미지 제거
const removeImage = () => {
  profileImage.value = null;
  imagePreview.value = '';
  imageError.value = '';
};
</script>

<template>
  <div class="join">
    <form class="join-form" @submit="handleSubmit">
      <!-- 프로필 이미지 업로드 영역 추가 -->
      <div class="mb-3">
        <label class="form-label custom-label">프로필 이미지</label>
        <div class="profile-upload">
          <div class="image-preview" v-if="imagePreview">
            <img :src="imagePreview" alt="프로필 미리보기" />
            <button type="button" class="remove-image" @click="removeImage">
              ✕
            </button>
          </div>
          <div class="upload-area" v-else>
            <input
              type="file"
              id="profile-image"
              accept="image/*"
              @change="handleImageUpload"
              class="file-input"
            />
            <label for="profile-image" class="upload-label">
              <i class="fas fa-upload"></i>
              이미지 업로드
            </label>
          </div>
        </div>
        <div class="error-message">{{ imageError }}</div>
      </div>
      <!-- ID Field -->
      <div class="mb-3">
        <label for="userId" class="form-label custom-label">아이디</label>
        <div class="input-group">
          <input 
            type="text" 
            id="userId" 
            v-model="userId"
            class="form-control" 
            placeholder="아이디 입력"
            :class="{ 'is-valid': isIdAvailable }"
          />
          <button 
            type="button" 
            class="btn btn-outline-primary" 
            @click="checkDuplicateId"
          >
            중복확인
          </button>
        </div>
        <div class="error-message" :class="{ 'success-message': isIdAvailable }">
          {{ userIdError }}
        </div>
      </div>
      <!-- Username Field (새로 추가) -->
      <div class="mb-3">
        <label for="username" class="form-label custom-label">닉네임</label>
        <input 
          type="text" 
          id="username" 
          v-model="username"
          class="form-control" 
          placeholder="닉네임 입력"
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
          placeholder="비밀번호 입력 (영문, 숫자, 특수문자 포함 8자 이상)"
          :class="{ 'is-valid': passwordError === '사용 가능한 비밀번호입니다' }"
        />
        <div class="error-message" 
             :class="{ 'success-message': passwordError === '사용 가능한 비밀번호입니다' }">
          {{ passwordError }}
        </div>
      </div>

      <!-- Password Check Field -->
      <div class="mb-3">
        <label for="password-check" class="form-label custom-label">비밀번호 확인</label>
        <input 
          type="password" 
          id="password-check" 
          v-model="passwordCheck"
          class="form-control" 
          placeholder="비밀번호 확인"
          :class="{ 'is-valid': passwordCheckError === '비밀번호가 일치합니다' }"
        />
        <div class="error-message" 
             :class="{ 'success-message': passwordCheckError === '비밀번호가 일치합니다' }">
          {{ passwordCheckError }}
        </div>
      </div>
      <!-- Email Field -->
      <div class="mb-3">
        <label for="email" class="form-label custom-label">이메일</label>
        <div class="input-group">
          <input 
            type="email" 
            id="email" 
            v-model="email"
            class="form-control" 
            placeholder="이메일 입력"
            :class="{ 'is-valid': isEmailVerified }"
          />
          <button 
            type="button" 
            class="btn btn-outline-primary" 
            @click="checkDuplicateEmail"
          >
            중복확인
          </button>
          <!-- <button 
            type="button" 
            class="btn btn-outline-primary" 
            @click="sendVerificationEmail"
          >
            인증하기
          </button> -->
        </div>
        <div class="error-message" :class="{ 'success-message': isEmailAvailable }">
          {{ emailError }}
        </div>
      </div>

      <!-- Email Verification Code Field -->
      <div class="mb-3" v-if="showVerificationInput">
        <div class="input-group">
          <input 
            type="text" 
            v-model="verificationCode"
            class="form-control" 
            placeholder="인증 코드 입력"
          />
          <button 
            type="button" 
            class="btn btn-outline-success" 
            @click="verifyEmail"
          >
            확인
          </button>
        </div>
        <div class="error-message" :class="{ 'success-message': isEmailVerified }">
          {{ verificationError }}
        </div>
      </div>

      <!-- Submit Button -->
      <button type="submit" class="btn custom-btn w-100">회원가입</button>

      <!-- Login Link -->
      <p class="text-center mt-3">
        계정이 이미 있으신가요?
        <a href="/user/login">로그인 하러가기</a>
      </p>

    </form>
  </div>
</template>

<style scoped>
.join-form {
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

.success-message {
  color: #198754;
}

.is-valid {
  border-color: #198754;
}

.input-group {
  margin-bottom: 0.25rem;
}

.form-control:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.form-control.is-valid:focus {
  border-color: #198754;
  box-shadow: 0 0 0 0.25rem rgba(25, 135, 84, 0.25);
}
/* 커스텀 버튼 */
.custom-btn {
  background-color: #98DFE3;
  border-color: #98DFE3;
  color: white;
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
.profile-upload {
  width: 150px;
  height: 150px;
  border: 2px dashed #ccc;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  position: relative;
}

.image-preview {
  width: 100%;
  height: 100%;
  position: relative;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.remove-image {
  position: absolute;
  top: -10px;
  right: -10px;
  background: #504b4b;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  cursor: pointer;
}

.upload-area {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.file-input {
  display: none;
}

.upload-label {
  cursor: pointer;
  text-align: center;
  color: #666;
}
/* 반응형 디자인 */
@media (max-width: 768px) {
  .join-form {
    width: 90%;
  }
}
</style>