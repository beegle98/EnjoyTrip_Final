<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { detailNotice, deleteNotice } from "@/api/notice";
import { useDateFormat } from '@vueuse/core'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import Quill from 'quill'


import { useAuthStore } from "@/stores/auth";
const authStore = useAuthStore();
const userInfo = authStore.user.body.user;

const Delta = Quill.import('delta')

const editorOption = {
  modules: {
    toolbar: false // 툴바 비활성화
  },
  readOnly: true // 읽기 전용 설정
}

const formatDate = (dateString) => {
  if (!dateString) return '';
  return useDateFormat(dateString, 'YYYY-MM-DD HH:mm').value;
};

const route = useRoute();
const router = useRouter();

// const noticeno = ref(route.params.noticeno);
const { noticeno } = route.params;

const notice = ref({});

onMounted(() => {
  getNotice();
});

const getNotice = () => {
  // const { noticeno } = route.params;
  console.log(noticeno + "번글 얻으러 가자!!!");
  detailNotice(
    noticeno,
      ({ data }) => {

        // JSON 문자열을 Delta 객체로 변환
        notice.value = data;
        notice.value.content = new Delta(data.content)  // Delta 객체 생성
        
        console.log('Detail Delta content:', notice.value.content);
      },
      (error) => {
        console.log(error);
      }
  );
};

function moveList() {
  router.push({ name: "notice-list" });
}

function moveModify() {
  router.push({ name: "notice-modify", params: { noticeno } });
}

function onDeleteNotice() {
  // const { noticeno } = route.params;
  console.log(noticeno + "번글 삭제하러 가자!!!");
  deleteNotice(
    noticeno,
    (response) => {
      if (response.status == 200) moveList();
    },
    (error) => {
      console.log(error);
    }
  );
}
</script>

<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-lg-10">
        <h2 class="my-3 py-3 shadow-sm bg-light text-center">
          공지사항
        </h2>
      </div>
      <div class="col-lg-10 text-start">
        <div class="row my-2">
          <h2 class="text-secondary">{{ notice.subject }}</h2>
        </div>
        <div class="row">
          <div class="col-md-8">
            <div class="clearfix align-content-center">
              <p>
                <span class="fw-bold">{{ notice.username }}</span> <br />
                <span class="text-secondary fw-light">
                  {{ formatDate(notice.registerTime) }} 조회 : {{ notice.hit }}
                </span>
              </p>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">내용</label>
            <QuillEditor
              :content="notice.content"
              :options="editorOption"
              :readOnly="true"
              theme="snow"
            />
          </div>
          <div class="divider mt-3 mb-3"></div>
          <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-outline-primary mb-3" @click="moveList">
              글목록
            </button>
            <button 
              v-if="userInfo.roleType === 'ADMIN'"
              type="button" 
              class="btn btn-outline-success mb-3 ms-1" 
              @click="moveModify"
            >
              글수정
            </button>
            <button 
              v-if="userInfo.roleType === 'ADMIN'"
              type="button" 
              class="btn btn-outline-danger mb-3 ms-1" 
              @click="onDeleteNotice"
            >
              글삭제
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
