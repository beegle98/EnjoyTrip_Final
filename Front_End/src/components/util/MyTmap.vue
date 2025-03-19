<template>
  <div id="map" ref="mapDiv"></div>
</template>

<script setup>
import { ref, onMounted, watch, toRefs } from 'vue'

const props = defineProps({
  selectedPlaces: {
    type: Array,
    default: () => [],
  },
})

const { selectedPlaces } = toRefs(props)

const routePlaces = ref([]) // 외부에서 받은 경로 데이터
const mapDiv = ref(null)
const map = ref(null)
const markers = ref([])
const polylines = ref([])
const TMAP_API_KEY = import.meta.env.VITE_TMAP_API_KEY

const dayColors = [
  '#FF0000',
  '#00FF00',
  '#0000FF',
  '#FFFF00',
  '#FFA500',
  '#800080',
  '#008080',
  '#FFC0CB',
  '#A52A2A',
  '#808000',
]

function createMarkerSVG(dayNumber, dayColor) {
  return `
    <svg width="24" height="35" viewBox="0 0 24 35" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M12 0C6.5 0 0 4 0 12C0 20 12 35 12 35C12 35 24 20 24 12C24 4 17.5 0 12 0Z" fill="${dayColor}"/>
      <text x="12" y="20" font-size="12" font-weight="bold" text-anchor="middle" fill="white">
        ${dayNumber}
      </text>
    </svg>
  `
}

const loadTmapScript = () => {
  return new Promise((resolve, reject) => {
    if (window.Tmapv2) {
      resolve()
      return
    }

    const script = document.createElement('script')
    script.src = `https://apis.openapi.sk.com/tmap/jsv2?version=1&appKey=${TMAP_API_KEY}`
    script.async = true

    script.onload = () => {
      if (window.Tmapv2) {
        resolve()
      } else {
        reject(new Error('Tmap API 로드 실패'))
      }
    }

    script.onerror = () => reject(new Error('Tmap API 로드 실패'))

    document.head.appendChild(script)
  })
}

const initTmap = () => {
  if (!window.Tmapv2) return

  map.value = new window.Tmapv2.Map(mapDiv.value, {
    center: new window.Tmapv2.LatLng(37.5665, 126.978),
    width: '100%',
    height: '100%',
    zoom: 15,
  })
}

const updateMarkersAndRoutes = (places) => {
  if (!map.value) return

  // 기존 마커 및 폴리라인 삭제
  markers.value.forEach((marker) => {
    marker.setMap(null)
  })
  markers.value = []

  polylines.value.forEach((polyline) => {
    polyline.setMap(null)
  })
  polylines.value = []

  const bounds = new window.Tmapv2.LatLngBounds() // 지도 경계를 계산할 객체

  // 일차별 경로 및 마커 추가
  if (Array.isArray(places[0]?.places)) {
    places.forEach((day, dayIndex) => {
      const dayColor = dayColors[dayIndex % dayColors.length]
      const positions = []

      day.places.forEach((place, placeIndex) => {
        const newPosition = new window.Tmapv2.LatLng(place.latitude, place.longitude)
        positions.push(newPosition)

        const iconSVG = createMarkerSVG(placeIndex + 1, dayColor)
        const marker = new window.Tmapv2.Marker({
          position: newPosition,
          map: map.value,
          iconHTML: iconSVG,
          iconSize: new window.Tmapv2.Size(24, 35),
          label: place.title,
        })
        markers.value.push(infoWindow(place, newPosition, marker))

        bounds.extend(newPosition) // 경계에 추가
      })

      // 경로 라인 추가
      if (positions.length > 1) {
        const polyline = new window.Tmapv2.Polyline({
          path: positions,
          strokeColor: dayColor,
          strokeWeight: 6,
          map: map.value,
        })
        polylines.value.push(polyline)

        // 모든 경로 좌표를 경계에 추가
        positions.forEach((pos) => bounds.extend(pos))
      }
    })
  } else {
    // 일차원 배열 - 기본 마커 표시
    places.forEach((place) => {
      const newPosition = new window.Tmapv2.LatLng(place.latitude, place.longitude)
      const marker = new window.Tmapv2.Marker({
        position: newPosition,
        map: map.value,
        label: place.title,
      })

      markers.value.push(infoWindow(place, newPosition, marker))

      bounds.extend(newPosition) // 경계에 추가
    })
  }

  // 모든 마커와 경로가 보이도록 지도 중심과 줌을 설정
  if (!bounds.isEmpty()) {
    map.value.fitBounds(bounds, { bottom: 50, left: 50, top: 50, right: 50 }) // 경계에 맞게 설정
  } else if (places.length > 0) {
    // 경계가 없을 때 마지막 장소로 맵 중심 설정
    const lastPlace = Array.isArray(places[0]?.places)
      ? places[places.length - 1].places.slice(-1)[0]
      : places[places.length - 1]
    const lastPosition = new window.Tmapv2.LatLng(lastPlace.latitude, lastPlace.longitude)
    map.value.setCenter(lastPosition)
  }
}

const infoWindow = (place, newPosition, marker) => {
  new window.Tmapv2.InfoWindow({
    position: newPosition,
    content: `
        <div style="padding: 10px; border-radius: 8px; background-color: white; box-shadow: 0px 2px 5px rgba(0,0,0,0.2);">
          <strong>${place.title}</strong>
          <br>
          <span>${place.address}</span>
        </div>
      `,
    border: '1px solid #ddd',
    type: 2, // 마우스 오버로 활성화되는 정보창
    map: null, // 초기에는 지도에 추가하지 않음
  })

  // 마커 이벤트 추가
  marker.addListener('mouseover', () => {
    infoWindow.setMap(map.value) // 지도에 정보창 표시
  })

  marker.addListener('mouseout', () => {
    infoWindow.setMap(null) // 지도에서 정보창 제거
  })
  return marker
}

// 마커 생성
const createMarker = (position, index, label) => {
  const markerSVG = `
    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="40" viewBox="0 0 24 34">
      <path d="M12 0C6 0 0 5 0 12C0 19 12 34 12 34C12 34 24 19 24 12C24 5 18 0 12 0Z" fill="#007bff"/>
      <text x="12" y="20" text-anchor="middle" font-size="14" fill="white" font-family="Arial" font-weight="bold">${
        index + 1
      }</text>
    </svg>`
  console.log('label: ' + label)
  return new window.Tmapv2.Marker({
    position,
    map: map.value,
    iconHTML: markerSVG,
    iconSize: new window.Tmapv2.Size(30, 40),
    label: label, // 마��에 라�� 표시하지 않음
  })
}

const clearMap = () => {
  markers.value.forEach((marker) => marker.setMap(null)) // 기존 마커 제거
  markers.value = []
  polylines.value.forEach((polyline) => polyline.setMap(null)) // 기존 경로 제거
  polylines.value = []
}

const adjustMapToBounds = (bounds) => {
  if (map.value) {
    map.value.fitBounds(bounds, { bottom: 50, left: 50, top: 50, right: 50 })
  }
}

// 경로 및 마커 그리기
const drawRoutes = async () => {
  clearMap() // 기존 마커와 경로 초기화

  if (routePlaces.value.length === 0) {
    // 여행지가 없는 경우 초기화 후 종료
    return
  }
  const bounds = new window.Tmapv2.LatLngBounds() // 지도 영역 계산용 객체

  if (routePlaces.value.length === 1) {
    // 여행지가 1개인 경우 마커만 추가
    const singleMarker = createMarker(
      new window.Tmapv2.LatLng(routePlaces.value[0].latitude, routePlaces.value[0].longitude),
      0,
      routePlaces.value[0].title
    )
    markers.value.push(singleMarker)
    map.value.setCenter(singleMarker.getPosition()) // 지도 중심을 해당 마커로 이동
    return
  }
  console.log('routplaces.value', routePlaces.value)
  // 여행지가 2개 이상인 경우 경로와 마커 그리기
  for (let i = 0; i < routePlaces.value.length - 1; i++) {
    const start = routePlaces.value[i]
    const end = routePlaces.value[i + 1]

    // 각 여행지의 마커 추가
    const startMarker = createMarker(
      new window.Tmapv2.LatLng(start.latitude, start.longitude),
      i,
      start.title
    )
    markers.value.push(startMarker)
    bounds.extend(startMarker.getPosition()) // 경계 확장

    // 마지막 장소의 마커 추가
    if (i === routePlaces.value.length - 2) {
      const endMarker = createMarker(
        new window.Tmapv2.LatLng(end.latitude, end.longitude),
        i + 1,
        end.title
      )
      markers.value.push(endMarker)
      bounds.extend(endMarker.getPosition()) // 경계 확장
    }

    // 경로 그리기
    try {
      const url = `https://apis.openapi.sk.com/tmap/routes?version=1&format=json&callback=result&appKey=${TMAP_API_KEY}`
      const data = {
        startX: start.longitude,
        startY: start.latitude,
        endX: end.longitude,
        endY: end.latitude,
        reqCoordType: 'WGS84GEO',
        resCoordType: 'WGS84GEO',
        searchOption: '0',
      }

      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      })
      const result = await response.json()
      console.log('result: ', result)
      if (result.features) {
        const path = result.features
          .filter((feature) => feature.geometry.type === 'LineString')
          .map((feature) =>
            feature.geometry.coordinates.map(
              (coord) => new window.Tmapv2.LatLng(coord[1], coord[0])
            )
          )
          .flat()
        const polyline = new window.Tmapv2.Polyline({
          path,
          strokeColor: '#FF0000',
          strokeWeight: 4,
          map: map.value,
        })

        polylines.value.push(polyline)

        path.forEach((point) => bounds.extend(point)) // 경계 확장
      }
    } catch (error) {
      console.error('경로 그리기 오류:', error)
    }
  }

  adjustMapToBounds(bounds) // 계산된 경계로 지도 설정
}

const drawPublicRoutes = async () => {
  clearMap() // 기존 마커 및 경로 초기화
  console.log('routplaces........', routePlaces)
  if (routePlaces.value.length === 0) {
    // 여행지가 없는 경우 초기화 후 종료
    return
  }

  const bounds = new window.Tmapv2.LatLngBounds() // 지도 영역 계산용 객체

  if (routePlaces.value.length === 1) {
    // 여행지가 1개인 경우 마커만 추가
    const singleMarker = createMarker(
      new window.Tmapv2.LatLng(routePlaces.value[0].latitude, routePlaces.value[0].longitude),
      0,
      routePlaces.value[0].title
    )
    markers.value.push(singleMarker)
    map.value.setCenter(singleMarker.getPosition()) // 해당 위치로 지도 이동
    return
  }

  for (let i = 0; i < routePlaces.value.length - 1; i++) {
    const start = routePlaces.value[i]
    const end = routePlaces.value[i + 1]

    const startMarker = createMarker(
      new window.Tmapv2.LatLng(start.latitude, start.longitude),
      i,
      start.title
    )
    markers.value.push(startMarker)
    bounds.extend(startMarker.getPosition()) // 경계 확장

    // 마지막 장소의 마커 추가
    if (i === routePlaces.value.length - 2) {
      const endMarker = createMarker(
        new window.Tmapv2.LatLng(end.latitude, end.longitude),
        i + 1,
        end.title
      )
      markers.value.push(endMarker)
      bounds.extend(endMarker.getPosition()) // 경계 확장
    }

    try {
      const url = `https://apis.openapi.sk.com/transit/routes?appKey=${TMAP_API_KEY}`
      const data = {
        startX: start.longitude,
        startY: start.latitude,
        endX: end.longitude,
        endY: end.latitude,
        format: 'json',
        count: 1, // 최적 경로 하나만 반환
      }

      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      const result = await response.json()
      console.log('result: ', result)
      if (
        !result.metaData ||
        !result.metaData.plan ||
        !result.metaData.plan.itineraries ||
        result.metaData.plan.itineraries.length === 0
      ) {
        throw new Error('No itinerary data found in response')
      }

      const itinerary = result.metaData.plan.itineraries[0]

      itinerary.legs.forEach((leg) => {
        let positions = []

        // WALK일 때 처리
        if (leg.mode === 'WALK') {
          positions = (leg.steps || []).flatMap((step) =>
            (step.linestring || '').split(' ').map((coord) => {
              const [lon, lat] = coord.split(',')
              return new window.Tmapv2.LatLng(parseFloat(lat), parseFloat(lon))
            })
          )
        }
        // BUS나 SUBWAY일 때 처리
        else if (leg.mode === 'BUS' || leg.mode === 'SUBWAY') {
          if (leg.passShape?.linestring) {
            positions = leg.passShape.linestring.split(' ').map((coord) => {
              const [lon, lat] = coord.split(',')
              return new window.Tmapv2.LatLng(parseFloat(lat), parseFloat(lon))
            })
          }
        }

        // 경로를 Polyline으로 그리기
        if (positions.length > 0) {
          const strokeColor =
            leg.mode === 'WALK'
              ? '#00FF00' // 걷기 - 초록색
              : leg.mode === 'BUS'
              ? '#FF0000' // 버스 - 빨간색
              : leg.mode === 'SUBWAY'
              ? '#0000FF' // 지하철 - 파란색
              : '#000000' // 기본 - 검정색

          const polyline = new window.Tmapv2.Polyline({
            path: positions,
            strokeColor: strokeColor,
            strokeWeight: 4,
            map: map.value,
          })

          polylines.value.push(polyline)

          // 지도 경계 확장
          positions.forEach((point) => bounds.extend(point))
        }
      })

      adjustMapToBounds(bounds) // 계산된 경계로 지도 설정
    } catch (error) {
      console.error('대중교통 경로 그리기 오류:', error)
    }
  }
}

const setRoutePlaces = (places, transportationType) => {
  routePlaces.value = places
  console.log('setRoutePlaces selectedPlaces:', routePlaces.value)
  if (transportationType === 'car') {
    drawRoutes() // 승용차 경로 그리기
  } else if (transportationType === 'public') {
    drawPublicRoutes() // 대중교통 경로 그리기
  }
}
onMounted(async () => {
  try {
    await loadTmapScript()
    initTmap()
    if (selectedPlaces.value.length > 0) {
      updateMarkersAndRoutes(selectedPlaces.value)
    }
  } catch (error) {
    console.error('지도 초기화 오류:', error)
  }
})

watch(
  selectedPlaces,
  (newPlaces) => {
    updateMarkersAndRoutes(newPlaces)
  },
  { deep: true }
)

defineExpose({
  setRoutePlaces,
})
</script>

<style scoped>
#map {
  border: 1px solid #ddd;
}
</style>
