<template>
  <div class="schedule-review">
    <div class="search-controls">
      <div class="write-button">
        <button type="button" class="btn btn-outline-primary btn-sm" @click="moveWrite">
          글쓰기
        </button>
      </div>
      <div class="col-md-5 offset-5">
        <form class="d-flex">
          <VSelect :selectOption="selectOption" @onKeySelect="changeKey" />
          <div class="input-group input-group-sm ms-1">
            <input
              type="text"
              class="form-control"
              v-model="param.word"
              placeholder="검색어..."
            />
            <button class="btn btn-dark" type="button" @click="handleSearch">검색</button>
          </div>
        </form>
      </div>
    </div>    

    <InfiniteScrollGrid
      ref="gridRef"
      :fetch-items="fetchReviews"
      :page-size="6"
    >
      <template #items="{ items }">
        <TravelReviewCard
          v-for="review in items"
          :key="review.id"
          :review="review"
          @click="goToDetail"
        />
      </template>
    </InfiniteScrollGrid>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { listTravelReview } from "@/api/travelreview.js";
import VSelect from "@/components/common/VSelect.vue";
import TravelReviewCard from '@/components/travelreview/item/TravelReviewCard.vue';
import InfiniteScrollGrid from "@/components/common/InfiniteScrollGrid.vue";

const router = useRouter();
const gridRef = ref(null);

const param = ref({
  key: "",
  word: "",
});

const selectOption = ref([
  { text: "검색조건", value: "" },
  { text: "여행지", value: "location" },
  { text: "제목", value: "subject" },
]);

const fetchReviews = async (page, pageSize) => {
  const searchParam = {
    pgno: page,
    spp: pageSize,
    key: param.value.key || '',
    word: param.value.word || ''
  };

  try {
    const response = await new Promise((resolve, reject) => {
      listTravelReview(
        searchParam,
        (res) => resolve(res),
        (error) => reject(error)
      );
    });

    return response.data.body.travelReviews;
  } catch (error) {
    console.error('Error fetching reviews:', error);
    return [];
  }
};

const handleSearch = () => {
  gridRef.value?.reset();
};

const changeKey = (val) => {
  param.value.key = val;
};

const goToDetail = (travelReviewNo) => {
  console.log('이동할 리뷰 번호:', travelReviewNo); // 디버깅용
  router.push({ 
    name: "travelReview-view", 
    params: { travelReviewNo: travelReviewNo.toString() } 
  });
};

const moveWrite = () => {
  router.push({ name: "travelReview-write" });
};
</script>

<style scoped>
.schedule-review {
  width: 100%;
}

.search-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.write-button {
  margin-right: auto;
}
</style>