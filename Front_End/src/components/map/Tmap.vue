<template>
    <div class="tmap-container">
      <div class="search-box">
        <input
          v-model="searchKeyword"
          @keyup.enter="searchPOI"
          placeholder="검색할 장소를 입력하세요"
          type="text"
        />
        <button @click="searchPOI">검색</button>
      </div>
      <div id="map_div" ref="mapDiv"></div>
    </div>
  </template>

  <script setup>
  import { ref, onMounted } from 'vue'

  const mapDiv = ref(null)
  const map = ref(null)
  const searchKeyword = ref('')

  // 지도 초기화
  const initTmap = () => {
    map.value = new Tmapv2.Map(mapDiv.value, {
      center: new Tmapv2.LatLng(37.56520450, 126.98702028),
      width: "100%",
      height: "400px",
      zoom: 10
    })
  }

  // 마커 추가
  const addMarker = (lonlatoption) => {
    const marker = new Tmapv2.Marker({
      position: new Tmapv2.LatLng(
        lonlatoption.lonlat.lat(),
        lonlatoption.lonlat.lng()
      ),
      map: map.value,
      title: lonlatoption.title
    })
  }

  // 검색 완료 콜백
  const onComplete = function() {
    console.log(this._responseData)

    if (this._responseData.searchPoiInfo.pois.poi !== '') {
      jQuery(this._responseData.searchPoiInfo.pois.poi).each(function() {
        const name = this.name
        const lon = this.frontLon
        const lat = this.frontLat

        const lonlatoption = {
          title: name,
          lonlat: new Tmapv2.LatLng(lat, lon)
        }

        addMarker(lonlatoption)
      })
    } else {
      alert('검색결과가 없습니다.')
    }

    map.value.setCenter(new Tmapv2.LatLng(37.56520450, 126.98702028))
    map.value.setZoom(14)
  }

  // 진행중 콜백
  const onProgress = () => {
    console.log('검색 진행중...')
  }

  // 에러 콜백
  const onError = () => {
    alert('검색 중 오류가 발생했습니다.')
  }

  // POI 검색
  const searchPOI = () => {
    if (!searchKeyword.value) {
      alert('검색어를 입력해주세요')
      return
    }

    const center = map.value.getCenter()

    const optionObj = {
      reqCoordType: "WGS84GEO",
      resCoordType: "WGS84GEO",
      centerLon: center.lng(),
      centerLat: center.lat()
    }

    const params = {
      onComplete: onComplete,
      onProgress: onProgress,
      onError: onError
    }

    const tData = new Tmapv2.extension.TData()
    tData.getPOIDataFromSearchJson(
      encodeURIComponent(searchKeyword.value),
      optionObj,
      params
    )
  }

  onMounted(() => {
    initTmap()
  })
  </script>

  <style scoped>
  .tmap-container {
    position: relative;
    width: 100%;
    padding: 20px;
  }

  #map_div {
    width: 100%;
    height: 100%;
    margin-top: 10px;
    border-radius: 8px;
    overflow: hidden;
  }

  .search-box {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;
  }

  .search-box input {
    flex: 1;
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
  }

  .search-box button {
    padding: 8px 16px;
    background-color: #253C95;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
  }

  .search-box button:hover {
    background-color: #1a2a6d;
  }
  </style>
