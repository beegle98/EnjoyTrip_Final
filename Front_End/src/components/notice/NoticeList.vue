travelreview<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { listNotice } from "@/api/notice.js";

import VSelect from "@/components/common/VSelect.vue";
import NoticeListItem from "@/components/notice/item/NoticeListItem.vue";
import VPageNavigation from "@/components/common/VPageNavigation.vue";


import { useAuthStore } from "@/stores/auth";
const authStore = useAuthStore();
const userInfo = authStore.user.body.user;


const router = useRouter();

const selectOption = ref([
  { text: "검색조건", value: "" },
  { text: "글번호", value: "notice_no" },
  { text: "제목", value: "subject" },
]);


const notices = ref([]);
const currentPage = ref(1);
const totalPage = ref(0);
const { VITE_ARTICLE_LIST_SIZE } = import.meta.env;
const param = ref({
  pgno: currentPage.value,
  spp: VITE_ARTICLE_LIST_SIZE,
  key: "",
  word: "",
});

onMounted(() => {
  getNoticeList();
});

const changeKey = (val) => {
  console.log("BoarList에서 선택한 조건 : " + val);
  param.value.key = val;
};

const getNoticeList = () => {
  console.log("서버에서 글목록 얻어오자!!!", param.value);
  const searchParam = {
    pgno: param.value.pgno,
    spp: param.value.spp,
    key: param.value.key || '',
    word: param.value.word || ''
  };

  listNotice(
    searchParam,
    (response) => {
      const data = response.data;
      console.log("Received data:", data); // 데이터 확인용
      
      if (data && Array.isArray(data.body.notices)) {
        notices.value = data.body.notices;
        currentPage.value = data.body.currentPage;
        totalPage.value = data.body.totalPageCount;
      } else {
        console.error('Invalid data format:', data);
        notices.value = [];
      }
    },
    (error) => {
      console.error('API Error:', error);
      notices.value = [];
    }
  );
};

const onPageChange = (val) => {
  console.log(val + "번 페이지로 이동 준비 끝!!!");
  currentPage.value = val;
  param.value.pgno = val;
  getNoticeList();
};

const moveWrite = () => {
  router.push({ name: "notice-write" });
};
</script>

<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-lg-10">
        <h2 class="my-3 py-3 shadow-sm bg-light text-center">
          공지사항
        </h2>
      </div>
      <div class="col-lg-10">
        <div class="row align-self-center mb-2">
          <div class="col-md-2 text-start">
            <button 
              v-if="userInfo.roleType === 'ADMIN'" 
              type="button" 
              class="btn btn-outline-primary btn-sm" 
              @click="moveWrite"
            >
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
                <button class="btn btn-dark" type="button" @click="getNoticeList">검색</button>
              </div>
            </form>
          </div>
        </div>
        <table class="table table-hover">
          <thead>
            <tr class="text-center">
              <th scope="col">글번호</th>
              <th scope="col">제목</th>
              <th scope="col">작성자</th>
              <th scope="col">조회수</th>
              <th scope="col">작성일</th>
            </tr>
          </thead>
          <tbody>
            <NoticeListItem
              v-for="notice in notices"
              :key="notice.noticeNo"
              :notice="notice"
            ></NoticeListItem>
          </tbody>
        </table>
      </div>
      <VPageNavigation
        :current-page="currentPage"
        :total-page="totalPage"
        @pageChange="onPageChange"
      ></VPageNavigation>
    </div>
  </div>
</template>

<style scoped></style>
