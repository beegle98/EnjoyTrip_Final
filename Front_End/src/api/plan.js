import { localAxios } from "@/util/http-commons";

const local = localAxios();

function listAttraction(param, success, fail) {
    local.get(`/plan/attraction/list`,{params : param}).then(success).catch(fail);
}

function listAttractionSave(param, success, fail) {
  local.post(`/plan/attraction/save`,param).then(success).catch(fail);
}

function listplan(userId, success, fail) {
  local.get(`/plan/list/${userId}`).then(success).catch(fail);
}

function planInfo(planId, success, fail) {
  local.get(`/plan/attraction/list/${planId}`).then(success).catch(fail);
}

function planInfoByRegion(param, success, fail) {
  local.get(`/plan/list/region?regionId=${param.region}&page=${param.page}&size=${param.size}`).then(success).catch(fail);
}

function deletePlan(planId, success, fail) {
  local.delete(`/plan/${planId}`).then(success).catch(fail);
}

export { listAttraction,listAttractionSave, listplan, planInfo,planInfoByRegion,deletePlan};

