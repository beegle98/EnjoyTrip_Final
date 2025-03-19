// utils/axios.js
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

axios.interceptors.response.use(
  response => response,
  async error => {
    if (error.response.status === 401) {
      try {
        // Attempt to refresh the token here using an endpoint like /api/auth/refresh
        const response = await axios.post('/api/auth/refresh');
        authStore.setAccessToken(response.data.accessToken);
        
        // Retry original request with new token
        error.config.headers['Authorization'] = `Bearer ${response.data.accessToken}`;
        return axios(error.config);
      } catch (refreshError) {
        authStore.clearAuth();
        console.error('Refresh token failed:', refreshError);
        // Optionally redirect to login page
      }
    }
    return Promise.reject(error);
  }
);