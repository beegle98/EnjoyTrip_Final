<template>
  <div class="container review-container">
    <form @submit.prevent="onSubmit" class="review-form">
      <div class="form-header">
        <div class="author-line">
          <b-avatar size="2rem" :src="userInfo.profileImageUrl" class="author-avatar"></b-avatar>
          <span class="author-name">{{ userInfo.username }}</span>
        </div>
        
        <div class="title-section">
          <label class="form-label">제목</label>
          <div class="title-input">
            <input type="text" class="form-control" v-model="travelReview.subject" 
              placeholder="여행의 제목을 입력해주세요" :class="{ 'is-invalid': subjectErrMsg }">
            <div class="invalid-feedback">{{ subjectErrMsg }}</div>
          </div>
        </div>

        <div class="location-section">
          <label class="form-label">여행지</label>
          <div class="location-input">
            <input type="text" class="form-control" v-model="travelReview.location" 
              placeholder="방문하신 지역을 입력해주세요">
          </div>
        </div>

        <div class="thumbnail-section">
          <label class="form-label">대표 이미지</label>
          <div class="thumbnail-container">
            <div class="thumbnail-preview" v-if="thumbnailPreview">
              <img :src="thumbnailPreview" alt="썸네일 미리보기" class="preview-image">
              <button type="button" class="remove-thumbnail" @click="removeThumbnail">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div class="thumbnail-upload" v-else>
              <input type="file" @change="onThumbnailChange" accept="image/*" 
                class="thumbnail-input" ref="thumbnailInput">
              <div class="upload-placeholder">
                <i class="fas fa-cloud-upload-alt"></i>
                <span>썸네일 이미지를 선택해주세요</span>
                <small>권장 크기: 400x300px</small>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="content-section">
        <label class="form-label">내용</label>
        <div class="editor-wrapper">
          <QuillEditor
            v-model:content="travelReview.content"
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
      </div>
      <div class="form-actions">
        <button type="button" class="btn btn-back" @click="moveList">
          <i class="fas fa-arrow-left"></i> 목록으로
        </button>
        <button type="submit" class="btn btn-submit">
          {{ props.type === 'regist' ? '여행 리뷰 등록' : '리뷰 수정' }}
          <i class="fas fa-paper-plane"></i>
        </button>
      </div>
    </form>
  </div>
</template>
  
  <script setup>
  import { ref, watch, onMounted } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { registTravelReview, getModifyTravelReview, modifyTravelReview } from "@/api/travelreview";
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

  // 썸네일 관련 상태 추가
const thumbnailPreview = ref(null);
const thumbnailInput = ref(null);
 // travelReview ref에 새로운 필드 추가
const travelReview = ref({
  travelReviewNo: 0,
  subject: "",
  location: "", // 여행지 필드 추가
  content: "",
  userId: userInfo.userId,
  username: userInfo.username,
  thumbnailImage: null, // 썸네일 파일 필드 추가
  hit: 0,
  registerTime: "",
  modifiedTime: ""
});

// 썸네일 관련 메서드 추가
const onThumbnailChange = (event) => {

  const file = event.target.files[0];
  if (file) {
    travelReview.value.thumbnailImage = file;
    console.log('thumbnailImage:', travelReview.value);
    thumbnailPreview.value = URL.createObjectURL(file);
  }
};

const removeThumbnail = () => {
  travelReview.value.thumbnailImage = null;
  thumbnailPreview.value = null;
  if (thumbnailInput.value) {
    thumbnailInput.value.value = '';
  }
};
  
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
      let { travelReviewNo } = route.params;
      getModifyTravelReview(
        travelReviewNo,
        ({ data }) => {
  
          // JSON 문자열을 Delta 객체로 변환
          travelReview.value = data;
          travelReview.value.content = new Delta(data.content)  // Delta 객체 생성
          thumbnailPreview.value = `http://localhost:8080/tournest${travelReview.value.thumbnailUrl}`;
          
          console.log('Detail Delta content:', travelReview.value.content);
        },
        (error) => {
          console.log(error);
        }
      );
    }
  });
  
  watch(() => travelReview.value.subject, (value) => {
    subjectErrMsg.value = value.length === 0 || value.length > 100 ? "제목은 1-100자 사이여야 합니다." : "";
  });
  
  watch(() => travelReview.value.content, (value) => {
    contentErrMsg.value = value.length === 0 ? "내용을 입력해주세요." : "";
  });
  
  function onSubmit() {
    if (subjectErrMsg.value || contentErrMsg.value) {
      alert("입력 내용을 확인해주세요.");
      return;
    }
    
    props.type === "regist" ? writeTravelReview() : updateTravelReview();
  }
  
  function writeTravelReview() {
    console.log("writeTravelReview: ", travelReview.value);

    const formData = new FormData();
    
    
    formData.append('subject', travelReview.value.subject);
    formData.append('location', travelReview.value.location);
    formData.append('content', JSON.stringify(travelReview.value.content));
    formData.append('userId', travelReview.value.userId);
    formData.append('username', travelReview.value.username);
    
    if (travelReview.value.thumbnailImage) {
        formData.append('thumbnailImage', travelReview.value.thumbnailImage);
    }
    registTravelReview(
      formData, 
      (response) => {
        alert(response.status === 201 ? "여행지 리뷰가 등록되었습니다." : "등록 중 문제가 발생했습니다.");
        moveList();
      },
      (error) => {
            console.error("Error:", error);  // 에러 로깅 추가
      }
    );
  }
  
  function updateTravelReview() {
    console.log("updateTravelReview: ", travelReview.value.content);

    const formData = new FormData();
    
    formData.append('travelReviewNo', travelReview.value.travelReviewNo);
    formData.append('subject', travelReview.value.subject);
    formData.append('location', travelReview.value.location);
    formData.append('content', JSON.stringify(travelReview.value.content));
    formData.append('userId', travelReview.value.userId);
    formData.append('username', travelReview.value.username);
    
    if (travelReview.value.thumbnailImage) {
        formData.append('thumbnailImage', travelReview.value.thumbnailImage);
    }
    modifyTravelReview(
      formData,
      (response) => {
        alert(response.status === 200 ? "여행 리뷰가 수정되었습니다." : "수정 중 문제가 발생했습니다.");
        moveList();
      },
      (error) => {
            console.error("Error:", error);  // 에러 로깅 추가
      }
    );
  }
  
  function moveList() {
    router.push({ name: "travelReview-list" });
  }
  const onEditorChange = (content) => {
    if (content && content.ops) {
      travelReview.value.content = content;
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
  
<style scoped>
.form-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #666;
  margin-bottom: 0.5rem;
  display: block;
  text-align: left;
}

.location-section {
  margin-bottom: 1.5rem;
}

.thumbnail-section {
  margin-bottom: 2rem;
}

.thumbnail-container {
  border: 2px dashed #e9ecef;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  width: 400px;
  margin: 0 auto;
}

.thumbnail-container:hover {
  border-color: #588ef3;
}

.thumbnail-preview {
  position: relative;
  width: 400px;
  height: 300px;
  margin: 0 auto;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.remove-thumbnail {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.remove-thumbnail:hover {
  background: rgba(0, 0, 0, 0.7);
}

.thumbnail-upload {
  position: relative;
  width: 400px;
  height: 300px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.thumbnail-input {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #666;
}

.upload-placeholder i {
  font-size: 2rem;
  color: #588ef3;
}

.upload-placeholder small {
  color: #999;
  font-size: 0.8rem;
}


  .review-container {
    max-width: 1200px;
    margin: 2rem auto;
    padding: 0 20px;
  }
  
  .review-form {
    background: white;
    border-radius: 15px;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
    padding: 2.5rem;
  }
  
  .form-header {
    margin-bottom: 2rem;
  }
  
  .title-section {
    margin-bottom: 1.5rem;
  }
  
  .form-label {
    font-size: 0.9rem;
    font-weight: 600;
    color: #666;
    margin-bottom: 0.5rem;
    display: block;
  }
  
  .title-input {
    position: relative;
  }
  .author-line {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 1.5rem;
  padding: 12px 0;
  border-bottom: 1px solid #e9ecef;
}

.author-avatar {
  background-color: #588ef3;
}

.author-name {
  color: #2c3e50;
  font-weight: 500;
  padding-left: 8px;
}
  
  
  .form-control {
    height: 48px;
    border: 2px solid #e9ecef;
    border-radius: 8px;
    padding: 0.5rem 1rem;
    font-size: 1rem;
    transition: all 0.3s ease;
  }
  
  .form-control {
    padding-left: 2.5rem;
    background-color: #f8f9fa;
    color: #495057;
  }
  
  .form-control:focus {
    border-color: #588ef3;
    box-shadow: 0 0 0 0.2rem rgba(88, 142, 243, 0.1);
  }
  
  .content-section {
    margin-bottom: 2rem;
  }
  
  .editor-wrapper {
    border-radius: 8px;
    overflow: hidden;
  }
  
  :deep(.ql-toolbar) {
    border: 2px solid #e9ecef !important;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
    background-color: #f8f9fa;
  }
  
  :deep(.ql-container) {
    border: 2px solid #e9ecef !important;
    border-top: none !important;
    border-bottom-left-radius: 8px;
    border-bottom-right-radius: 8px;
    min-height: 400px;
  }
  
  .form-actions {
    display: flex;
    justify-content: space-between;
    margin-top: 2rem;
    padding-top: 1.5rem;
    border-top: 1px solid #e9ecef;
  }
  
  .btn {
    padding: 0.8rem 1.5rem;
    border-radius: 8px;
    font-weight: 600;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  
  .btn-back {
    background-color: #f8f9fa;
    color: #495057;
    border: 2px solid #e9ecef;
  }
  
  .btn-back:hover {
    background-color: #e9ecef;
  }
  
  .btn-submit {
    background-color: #588ef3;
    color: white;
    border: none;
  }
  
  .btn-submit:hover {
    background-color: #4070d0;
    transform: translateY(-1px);
  }
  
  .invalid-feedback {
    color: #dc3545;
    font-size: 0.875rem;
    margin-top: 0.25rem;
  }
  
  @media (max-width: 768px) {
    .review-form {
      padding: 1.5rem;
    }
  
    .btn {
      padding: 0.6rem 1rem;
    }
  }
  </style>