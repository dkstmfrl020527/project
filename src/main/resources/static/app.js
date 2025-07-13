let map;
let markers = [];

// 카카오맵 초기화
function initMap() {
    const container = document.getElementById('map');
    const options = {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 3
    };

    map = new kakao.maps.Map(container, options);

    // 지도 클릭 이벤트
    kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
        const latlng = mouseEvent.latLng;
        const lat = latlng.getLat();
        const lon = latlng.getLng();

        getRecommendations(lat, lon);
    });

    // 현재 위치로 시작
    getCurrentLocation();
}

// 현재 위치 가져오기
function getCurrentLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            const moveLatLon = new kakao.maps.LatLng(lat, lon);
            map.setCenter(moveLatLon);

            getRecommendations(lat, lon);
        });
    }
}

// 추천 식당 가져오기
async function getRecommendations(lat, lon) {
    try {
        const response = await fetch(`/api/recommend?lat=${lat}&lon=${lon}&radius=1000`);
        const restaurants = await response.json();

        displayRestaurants(restaurants);
        displayMarkers(restaurants);

    } catch (error) {
        console.error('Error:', error);
    }
}

// 식당 목록 표시
function displayRestaurants(restaurants) {
    const container = document.getElementById('restaurantList');
    container.innerHTML = '';

    restaurants.forEach((restaurant, index) => {
        const item = document.createElement('div');
        item.className = 'restaurant-item';
        item.innerHTML = `
            <h3>${restaurant.name}</h3>
            <p>${restaurant.category}</p>
            <p>${restaurant.address}</p>
            <p>📞 ${restaurant.phone || 'N/A'}</p>
            <p>📍 ${restaurant.distance}m</p>
        `;

        item.onclick = () => {
            const moveLatLon = new kakao.maps.LatLng(restaurant.lat, restaurant.lon);
            map.setCenter(moveLatLon);
        };

        container.appendChild(item);
    });
}

// 지도에 마커 표시
function displayMarkers(restaurants) {
    // 기존 마커 제거
    markers.forEach(marker => marker.setMap(null));
    markers = [];

    restaurants.forEach(restaurant => {
        const position = new kakao.maps.LatLng(restaurant.lat, restaurant.lon);
        const marker = new kakao.maps.Marker({ position, map });

        const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:10px;">${restaurant.name}</div>`
        });

        kakao.maps.event.addListener(marker, 'click', () => {
            infowindow.open(map, marker);
        });

        markers.push(marker);
    });
}

// 페이지 로드 시 실행
window.onload = () => {
    kakao.maps.load(initMap);
};