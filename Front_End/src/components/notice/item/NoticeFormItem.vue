<template>
  <div class="container mt-5">
    <form @submit.prevent="onSubmit" class="bg-light p-4 rounded">
      <div class="mb-3">
        <label for="username" class="form-label">작성자</label>
        <input type="text" class="form-control" id="username" v-model="userInfo.username" disabled>
      </div>
      <div class="mb-3">
        <label for="subject" class="form-label">제목</label>
        <input type="text" class="form-control" id="subject" v-model="notice.subject" placeholder="제목을 입력하세요" :class="{ 'is-invalid': subjectErrMsg }">
        <div class="invalid-feedback">{{ subjectErrMsg }}</div>
      </div>
      <div class="mb-3">
        <label for="content" class="form-label">내용</label>
        <QuillEditor
          v-model:content="notice.content"
          :options="editorOption"
          :readOnly="false"
          theme="snow"
          @update:content="onEditorChange"
          @blur="onEditorBlur"
          @focus="onEditorFocus"
          @ready="onEditorReady"
        />
        <div class="invalid-feedback">{{ contentErrMsg }}</div>
      </div>
      <div class="d-flex justify-content-between mt-4">
        <button type="button" class="btn btn-secondary" @click="moveList">목록으로</button>
        <button type="submit" class="btn btn-primary">{{ props.type === 'regist' ? '등록' : '수정' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { registNotice, getModifyNotice, modifyNotice } from "@/api/notice";
import { useAuthStore } from "@/stores/auth";
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import Quill from 'quill'
import ImageResize from 'quill-image-resize-vue'
const Delta = Quill.import('delta')

Quill.register('modules/imageResize', ImageResize)

const router = useRouter();
const route = useRoute();
const props = defineProps({ type: String });
const authStore = useAuthStore();
const userInfo = authStore.user.body.user;

const notice = ref({
  noticeNo: 0, 
  subject: "",
  content: "",
  userId: userInfo.userId,
  userName: userInfo.username,
  hit: 0,
  registerTime: "",
  modifiedTime: ""
});

const subjectErrMsg = ref("");
const contentErrMsg = ref("");

const editorOption = {
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote', 'code-block'],
      [{ header: 1 }, { header: 2 }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      [{ script: 'sub' }, { script: 'super' }],
      [{ indent: '-1' }, { indent: '+1' }],
      [{ direction: 'rtl' }],
      [{ size: ['small', false, 'large', 'huge'] }],
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      [{ font: [] }],
      [{ color: [] }, { background: [] }],
      [{ align: [] }],
      ['link', 'image', 'video']
    ],
    imageResize: {
      displaySize: true,
      modules: ['Resize', 'DisplaySize', 'Toolbar'],

    }
  },
};

onMounted(() => {
  if (props.type === "modify") {
    let { noticeno } = route.params;
    getModifyNotice(
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
  }
});

watch(() => notice.value.subject, (value) => {
  subjectErrMsg.value = value.length === 0 || value.length > 100 ? "제목은 1-100자 사이여야 합니다." : "";
});

watch(() => notice.value.content, (value) => {
  contentErrMsg.value = value.length === 0 ? "내용을 입력해주세요." : "";
});

function onSubmit() {
  if (subjectErrMsg.value || contentErrMsg.value) {
    alert("입력 내용을 확인해주세요.");
    return;
  }
  
  props.type === "regist" ? writeNotice() : updateNotice();
}

function writeNotice() {
  console.log("writeNotice: ", notice.value.content);
  registNotice(
    notice.value,
    (response) => {
      alert(response.status === 201 ? "공지사항이 등록되었습니다." : "등록 중 문제가 발생했습니다.");
      moveList();
    },
    (error) => console.log(error)
  );
}

function updateNotice() {
  console.log("updateNotice: ", notice.value.content);
  modifyNotice(
    notice.value,
    (response) => {
      alert(response.status === 200 ? "공지사항이 수정되었습니다." : "수정 중 문제가 발생했습니다.");
      moveList();
    },
    (error) => console.log(error)
  );
}

function moveList() {
  router.push({ name: "notice-list" });
}
const onEditorChange = (content) => {
  if (content && content.ops) {
    notice.value.content = content;
  }
  console.log('editor content changed:', content);
}
const onEditorBlur = (quill) => {
  console.log('editor blur!', quill)
}

const onEditorFocus = (quill) => {
  console.log('editor focus!', quill)
}

const onEditorReady = (quill) => {
  console.log('editor ready!', quill)
}
</script>

<style>
.ql-editor {
  min-height: 400px;
}
.ql-container .ql-editor img {
  cursor: pointer;
}
</style>
