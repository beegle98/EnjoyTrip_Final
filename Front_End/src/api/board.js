import { localAxios } from "@/util/http-commons";

const local = localAxios();
function listNotice(param, success, fail) {
    local.get(`/board`, { params: param }).then(success).catch(fail);
  }
  
  function detailNotice(noticeno, success, fail) {
    local.get(`/board/${noticeno}`).then(success).catch(fail);
  }
  
  function registNotice(notice, success, fail) {
    console.log("boardjs notice", notice);
    local.post(`/board`, JSON.stringify(notice)).then(success).catch(fail);
  }
  
  function getModifyNotice(noticeno, success, fail) {
    local.get(`/board/modify/${noticeno}`).then(success).catch(fail);
  }
  
  function modifyNotice(notice, success, fail) {
    local.put(`/board`, JSON.stringify(notice)).then(success).catch(fail);
  }
  
  function deleteNotice(noticeno, success, fail) {
    local.delete(`/board/${noticeno}`).then(success).catch(fail);
  }
  
  export {
    listNotice,
    detailNotice,
    registNotice,
    getModifyNotice,
    modifyNotice,
    deleteNotice,
  };