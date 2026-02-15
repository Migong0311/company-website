<script setup>
import mapImg from '@/assets/images/map1.png'
</script>

<template>
    <div class="location-box" v-scroll-animate>
        <div class="section-header left-align">
            <p class="section-subtitle">Location</p>
            <h2 class="section-title">오시는 길</h2>
            <div class="section-bar"></div>
        </div>
        <div class="map-container">
            <img :src="mapImg" alt="사무실 지도">
            <a
                href="https://map.naver.com/p/entry/place/15865310?c=16.62,0,0,0,dh"
                class="map-button"
                target="_blank"
                rel="noopener"
            >
                <i class="fas fa-map-marker-alt"></i> 네이버 지도
            </a>
        </div>
        <div class="office-details">
            <div class="detail-row">
                <i class="fas fa-map-pin"></i>
                <span>경상남도 양산시 소주로 182 (서창대동아파트, 상가동 205호)</span>
            </div>
            <div class="detail-row">
                <i class="fas fa-envelope"></i>
                <span>yst0803@hanmail.net</span>
            </div>
            <div class="detail-row">
                <i class="fas fa-clock"></i>
                <span>평일 08:00 ~ 17:00</span>
            </div>
        </div>
    </div>
</template>

<style scoped>
.map-container {
    position: relative;
    border-radius: var(--radius-md);
    overflow: hidden;
    margin-bottom: 24px;
    box-shadow: var(--shadow-sm);
}

.map-container img {
    width: 100%;
    height: 260px;
    object-fit: cover;
}

.map-button {
    position: absolute;
    top: 14px;
    right: 14px;
    background: var(--primary);
    color: var(--white);
    padding: 8px 18px;
    border-radius: 30px;
    font-size: 13px;
    font-weight: 500;
    transition: var(--transition);
    display: flex;
    align-items: center;
    gap: 6px;
}

.map-button:hover {
    background: var(--primary-dark);
    transform: translateY(-2px);
}

.office-details {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.detail-row {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    font-size: 14px;
    color: var(--text-body);
}

.detail-row i {
    color: var(--primary);
    margin-top: 4px;
    width: 16px;
    text-align: center;
    flex-shrink: 0;
}
</style>
