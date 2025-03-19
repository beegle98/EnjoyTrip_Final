<template>
  <div id="map" style="width: 100%; height: 400px"></div>
</template>

<script>
export default {
  props: ['selectedPlace'],
  watch: {
    selectedPlace(newPlace) {
      if (newPlace) {
        this.updateMap(newPlace.latitude, newPlace.longitude)
      }
    },
  },
  mounted() {
    this.initMap()
  },
  methods: {
    initMap() {
      const script = document.createElement('script')
      script.src =
        `//dapi.kakao.com/v2/maps/sdk.js?appkey=${import.meta.env.VITE_KAKAO_MAP_SERVICE_KEY}&autoload=false`
      script.onload = () => {
        window.kakao.maps.load(() => {
          this.map = new window.kakao.maps.Map(document.getElementById('map'), {
            center: new window.kakao.maps.LatLng(37.5665, 126.978), // 기본 중심좌표
            level: 7,
          })
        })
      }
      document.head.appendChild(script)
    },
    updateMap(latitude, longitude) {
      if (this.map) {
        const newPosition = new window.kakao.maps.LatLng(latitude, longitude)
        this.map.setCenter(newPosition)
        const marker = new window.kakao.maps.Marker({ position: newPosition })
        marker.setMap(this.map)
      }
    },
  },
}
</script>

<style scoped>
#map {
  border: 1px solid #ddd;
}
</style>
