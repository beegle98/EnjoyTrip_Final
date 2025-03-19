// stores/auth.js
import { defineStore } from 'pinia';
import { ref, watch, computed } from 'vue';
import router from '@/router';
import { localAxios } from "@/util/http-commons";
import axios from "axios";

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(null);
  const user = ref(null);
  const isAuthenticated = ref(false);
  const local = localAxios();

  watch(user, (newUser) => {
    isAuthenticated.value = !!newUser;
  }, { immediate: true });

  const initializeStore = () => {
    const storedAuth = sessionStorage.getItem('auth');
    if (storedAuth) {
      const authData = JSON.parse(storedAuth);
      user.value = authData.user;
      setAuthHeader(authData.accessToken);
      console.log('Stored username:', user.value);
      console.log('isAuthenticated:', isAuthenticated.value);
    } else {
      console.log('No auth data found in sessionStorage.');
    }
  };

  const login = async (userId, password) => {
    try {
      const response = await axios.post(
        'http://localhost:8080/tournest/api/v1/auth/login', 
        {
           id: userId,
           password: password
        },
        {
          headers: {
            'Content-Type': 'application/json'
          },
          withCredentials: true  // CORS 요청에 필요
        }
      );
      const token = response.data.body.token;
      console.log("login Token: ", token);
      setAuthHeader(token);
      fetchUser();

      return true;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      //유저 정보 초기화
      user.value = null;

      // 세션 스토리지에서 인증 정보 제거
      sessionStorage.removeItem('auth');

      // axios 헤더에서 인증 정보 제거
      clearAuthHeader();

      // 로그인 페이지로 리다이렉트
      router.push('/');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  //refreshToken 확인 후 accessToken 갱신
  const refreshToken = async () => {
    try {
      const response = await axios.get('http://localhost:8080/tournest/api/v1/auth/refresh', 
        {
          headers: { 
            Authorization: `Bearer ${accessToken.value}` 
          },
          withCredentials: true
        }
      );
      const newToken = response.data.body.token;
      console.log('Token refreshed:', newToken);
      setAuthHeader(newToken);

      // 세션 스토리지 업데이트
      const authData = JSON.parse(sessionStorage.getItem('auth') || '{}');
      authData.accessToken = newToken;
      sessionStorage.setItem('auth', JSON.stringify(authData));

      return newToken;
    } catch (error) {
      console.error('Token refresh failed:', error);
      logout();
      throw error;
    }
  };

  const fetchUser = async () => {
    if (!accessToken.value) {
      console.error('No access token available');
      return;
    }

    try {
      const response = await axios.get('http://localhost:8080/tournest/api/v1/users', {
        headers: { Authorization: `Bearer ${accessToken.value}` }
      });
      user.value = response.data;
      isAuthenticated.value = true;
      console.log("isAuthenticated: ", isAuthenticated.value);
      // 세션 스토리지 업데이트
      const authData = JSON.parse(sessionStorage.getItem('auth') || '{}');
      authData.user = response.data;
      sessionStorage.setItem('auth', JSON.stringify(authData));

      console.log('User fetched:', user.value);
    } catch (error) {
      console.error('Error fetching user:', error);
      if (error.response && error.response.status === 401) {
        // 토큰이 만료되었을 경우 리프레시 시도
        try {
          await refreshToken();
          await fetchUser(); // 리프레시 후 다시 사용자 정보 요청
        } catch (refreshError) {
          logout(); // 리프레시 실패 시 로그아웃
        }
      }
    }
  };

  const setAuthHeader = (token) => {
    accessToken.value = token;
    console.log('access token: ', accessToken);
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    console.log("setAuthHeader: ", axios.defaults.headers.common['Authorization']);
  };

  const clearAuthHeader = () => {
    accessToken.value = null;
    console.log('access token cleared');
    delete axios.defaults.headers.common['Authorization'];
  };

  return {
    initializeStore,
    accessToken,
    user,
    isAuthenticated,
    setAuthHeader,
    clearAuthHeader,
    login,
    logout,
    refreshToken,
    fetchUser,
  };
}, {
  persist: {
    key: 'auth',
    storage: sessionStorage,
    paths: ['user', 'accessToken']
  }
});
