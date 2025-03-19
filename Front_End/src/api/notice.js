import { localAxios } from "@/util/http-commons";

const local = localAxios();

function listNotice(param, success, fail) {
  const validParam = {
    pgno: param.pgno || 1,
    spp: param.spp || 10,
    key: param.key || '',
    word: param.word || ''
  };

  local.get(`/notice`, { 
    params: validParam 
  })
  .then((response) => {
    console.log("API Response:", response); // 응답 확인용
    success(response);
  })
  .catch(fail);
}

function detailNotice(noticeNo, success, fail) {
  local.get(`/notice/${noticeNo}`).then(success).catch(fail);
}

function registNotice(notice, success, fail) {
  console.log("regist notice", notice);
  local.post(`/notice`, JSON.stringify(notice)).then(success).catch(fail);
}

function getModifyNotice(noticeNo, success, fail) {
  local.get(`/notice/modify/${noticeNo}`).then(success).catch(fail);
}

function modifyNotice(notice, success, fail) {
  local.put(`/notice`, JSON.stringify(notice)).then(success).catch(fail);
}

function deleteNotice(noticeNo, success, fail) {
  local.delete(`/notice/${noticeNo}`).then(success).catch(fail);
}

export {
  listNotice,
  detailNotice,
  registNotice,
  getModifyNotice,
  modifyNotice,
  deleteNotice,
};
