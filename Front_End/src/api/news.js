import { localAxios } from '@/util/http-commons'

const local = localAxios()

function listNews(success, fail) {
  local.get(`/api/news/today`).then(success).catch(fail)
}

function listSearchAttraction(param, success, fail) {
  local.get(`/plan/search/attraction/list`, { params:param }).then(success).catch(fail)
}

function listMyAttraction(success, fail) {
  local.get(`/plan/my/attraction/list`).then(success).catch(fail)
}

function changeLike(param, success, fail) {
  local.post(`/plan/${param}/like`, param).then(success).catch(fail)
}
export { listNews,listSearchAttraction,changeLike,listMyAttraction }
