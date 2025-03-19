<template>
  <div class="profile-section text-center">
    <img
      :src="user.profileImageUrl"
      alt="프로필 이미지"
      class="rounded-circle mb-3"
      width="100"
      height="100"
    />
    <h3 class="mb-2">{{user.username}}</h3>
    <button class="btn btn-outline-primary btn-sm">
      <i class="fas fa-user-edit me-2"></i>프로필 수정
    </button>
  </div>
</template>

<script setup>

import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();

const user = authStore.user.body.user;
console.log(user);

</script>

<style scoped>
.profile-section img {
  border: 2px solid #007bff;
}
</style>
