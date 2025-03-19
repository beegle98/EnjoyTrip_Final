import { localAxios } from "@/util/http-commons";

const local = localAxios();

function listTravelReview(param, success, fail) {
    const validParam = {
        pgno: param.pgno || 1,
        spp: param.spp || 10,
        key: param.key || '',
        word: param.word || ''
    };

    local.get(`/travelReview`, {
        params: validParam
    })
        .then((response) => {
            console.log("API Response:", response); // 응답 확인용
            success(response);
        })
        .catch(fail);
}
function listUserTravelReview(param, success, fail) {
    local.get(`/travelReview/user/${param}`, {
    }).then((response) => {
        console.log("API Response:", response);
        success(response);
    }).catch(fail);

}

function detailTravelReview(travelReviewNo, success, fail) {
    local.get(`/travelReview/${travelReviewNo}`).then(success).catch(fail);
}

function registTravelReview(formData, success, fail) {
    return local.post(`/travelReview`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
    }).then(success).catch(fail);
}

function getModifyTravelReview(travelReviewNo, success, fail) {
    local.get(`/travelReview/modify/${travelReviewNo}`).then(success).catch(fail);
}

function modifyTravelReview(formData, success, fail) {
    return local.put(`/travelReview`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
    }).then(success).catch(fail);
}

function deleteTravelReview(travelReviewNo, success, fail) {
    local.delete(`/travelReview/${travelReviewNo}`).then(success).catch(fail);
}

export {
    listTravelReview,
    detailTravelReview,
    registTravelReview,
    getModifyTravelReview,
    modifyTravelReview,
    deleteTravelReview,
    listUserTravelReview,
};
