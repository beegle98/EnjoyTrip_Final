<script setup>
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { onMounted} from "vue";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
console.log('OauthRedirect Component mounted');
onMounted(() => {
  const token = route.query.token
  console.log('Checking access token:', token);
  if (token) {
    authStore.setAuthHeader(token);
    authStore.fetchUser()
  }else{
    console.error('No token found');
  }
  router.replace('/')
})

</script>
<template>
    <div>
        
    </div>
</template>

<style scoped>

</style>