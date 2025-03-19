<template>
  <div class="d-flex justify-content-center gap-3">
    <button
      v-for="(value, key) in stats"
      :key="key"
      class="btn btn-outline-dark px-4"
      @click="$emit('tabChange', key)"
    >
      <div class="fw-bold">{{ key }}</div>
      <div class="display-6 text-primary">{{ value }}</div>
    </button>
  </div>
</template>

<script setup>
defineProps({
  stats: {
    type: Object,
    required: true,
  },
})
</script>
