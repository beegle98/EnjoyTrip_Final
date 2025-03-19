<template>
  <div>
    <h4 class="mb-3">{{ title }}</h4>
    <div class="row g-3">
      <CardItem
        v-for="(card, index) in cards"
        :key="index"
        :card="card"
        @cardClick="handleCardClick"
        @deleteClick="handleDeleteClick"
      />
    </div>
  </div>
</template>

<script setup>
import CardItem from '@/components/users/CardItem.vue'

defineProps({
  cards: {
    type: Array,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['cardClick', 'deleteClick'])

const handleCardClick = (card) => {
  emit('cardClick', card) // 클릭 이벤트를 부모로 전달
}

const handleDeleteClick = (card) => {
  emit('deleteClick', card) // 삭제 클릭 이벤트를 부모로 전달
}
</script>
