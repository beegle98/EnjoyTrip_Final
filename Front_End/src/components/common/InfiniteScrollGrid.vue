<template>
    <div class="infinite-grid" ref="scrollContainer">
      <slot name="items" :items="items"></slot>
      <div v-if="loading" class="loading">Loading...</div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, onUnmounted } from 'vue';
  
  const props = defineProps({
    fetchItems: {
      type: Function,
      required: true
    },
    pageSize: {
      type: Number,
      default: 6
    }
  });
  
  const emit = defineEmits(['update:items']);
  
  const items = ref([]);
  const currentPage = ref(1);
  const loading = ref(false);
  const hasMore = ref(true);
  
  const handleScroll = () => {
    const scrollHeight = document.documentElement.scrollHeight;
    const scrollTop = window.scrollY;
    const clientHeight = document.documentElement.clientHeight;
  
    if (scrollHeight - scrollTop <= clientHeight + 100) {
      if (!loading.value && hasMore.value) {
        currentPage.value++;
        loadMore();
      }
    }
  };
  
  const loadMore = async () => {
    if (loading.value || !hasMore.value) return;
    loading.value = true;
  
    try {
      const newItems = await props.fetchItems(currentPage.value, props.pageSize);
      
      if (!newItems || newItems.length === 0) {
        hasMore.value = false;
      } else {
        items.value = currentPage.value === 1 
          ? newItems 
          : [...items.value, ...newItems];
        emit('update:items', items.value);
      }
    } catch (error) {
      console.error('Failed to fetch items:', error);
      hasMore.value = false;
    } finally {
      loading.value = false;
    }
  };
  
  const reset = () => {
    items.value = [];
    currentPage.value = 1;
    hasMore.value = true;
    loadMore();
  };
  
  defineExpose({ reset });
  
  onMounted(() => {
    loadMore();
    window.addEventListener('scroll', handleScroll);
  });
  
  onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
  });
  </script>
  
  <style scoped>
  .infinite-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .loading {
    grid-column: 1 / -1;
    text-align: center;
    padding: 20px;
  }
  </style>