<script setup>
defineProps({ notice: Object });
import { useDateFormat } from '@vueuse/core'

const formatDate = (dateString) => {
  if (!dateString) return '';
  return useDateFormat(dateString, 'YYYY-MM-DD HH:mm').value;
};
</script>

<template>
  <tr class="text-center">
    <th scope="row">{{ notice.noticeNo }}</th>
    <td class="text-start">
      <router-link
        :to="{ name: 'notice-view', params: { noticeno: notice.noticeNo } }"
        class="notice-title link-dark"
      >
        {{ notice.subject }}
      </router-link>
    </td>
    <td>{{ notice.username }}</td>
    <td>{{ notice.hit }}</td>
    <td>{{ formatDate(notice.registerTime) }}</td>
  </tr>
</template>

<style scoped>
a {
  text-decoration: none;
}
.text-start {
  gap: 3;
}
</style>
