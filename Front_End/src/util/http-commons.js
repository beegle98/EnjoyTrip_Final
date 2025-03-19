import axios from "axios";
import { useAuthStore } from '@/stores/auth';

const { VITE_TOURNEST_API_URL } = import.meta.env;


function localAxios() {
  const instance = axios.create({
    baseURL: VITE_TOURNEST_API_URL,
    headers: {
      "Content-Type": "application/json;charset=utf-8",
    },
  });

  // 요청 인터셉터
  instance.interceptors.request.use((config) => {
    const storedAuth = sessionStorage.getItem('auth');
    if (storedAuth) {
      const authData = JSON.parse(storedAuth);
      config.headers['Authorization'] = `Bearer ${authData.accessToken}`;
    }
    return config;
  }, (error) => {
    return Promise.reject(error);
  });

  // 응답 인터셉터
  instance.interceptors.response.use(
    response => response,
    async error => {
      const originalRequest = error.config;
  
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        
        try {
          const authStore = useAuthStore();
          const newToken = await authStore.refreshToken();
          
          // 새 토큰으로 원래 요청 재시도
          originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
          return instance(originalRequest);
          
        } catch (refreshError) {
          console.error('Token refresh failed:', refreshError);
          return Promise.reject(refreshError);
        }
      }
      return Promise.reject(error);
    }
  );

  return instance;
}

export {localAxios};