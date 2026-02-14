<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import poster1 from '@/assets/images/poster1.jpg'
import poster2 from '@/assets/images/poster2.jpg'
import poster3 from '@/assets/images/poster3.jpg'

const posters = [
    { image: poster1, alt: '안전 포스터 1' },
    { image: poster2, alt: '안전 포스터 2' },
    { image: poster3, alt: '안전 포스터 3' },
]

const currentIndex = ref(0)
let autoSlideInterval = null

const displayIndex = computed(() => currentIndex.value + 1)
const carouselTransform = computed(() => `translateX(-${currentIndex.value * 100}%)`)

function prev() {
    currentIndex.value = currentIndex.value <= 0 ? posters.length - 1 : currentIndex.value - 1
}

function next() {
    currentIndex.value = currentIndex.value >= posters.length - 1 ? 0 : currentIndex.value + 1
}

onMounted(() => {
    autoSlideInterval = setInterval(next, 4000)
})

onUnmounted(() => {
    clearInterval(autoSlideInterval)
})
</script>

<template>
    <div class="news-box" v-scroll-animate>
        <div class="section-header left-align">
            <p class="section-subtitle">News Zone</p>
            <h2 class="section-title">안전 소식존</h2>
            <div class="section-bar"></div>
        </div>
        <div class="poster-carousel-wrapper">
            <div class="poster-carousel" :style="{ transform: carouselTransform }">
                <div
                    v-for="(poster, index) in posters"
                    :key="index"
                    class="poster-slide"
                >
                    <img :src="poster.image" :alt="poster.alt">
                </div>
            </div>
        </div>
        <div class="poster-nav">
            <button class="poster-prev" aria-label="이전" @click="prev">
                <i class="fas fa-chevron-left"></i>
            </button>
            <span class="poster-counter">{{ displayIndex }} / {{ posters.length }}</span>
            <button class="poster-next" aria-label="다음" @click="next">
                <i class="fas fa-chevron-right"></i>
            </button>
        </div>
    </div>
</template>

<style scoped>
.poster-carousel-wrapper {
    border-radius: var(--radius-md);
    overflow: hidden;
    margin-bottom: 16px;
    box-shadow: var(--shadow-sm);
}

.poster-carousel {
    display: flex;
    transition: transform 0.5s ease-in-out;
}

.poster-slide {
    min-width: 100%;
}

.poster-slide img {
    width: 100%;
    height: 340px;
    object-fit: cover;
}

.poster-nav {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
}

.poster-prev,
.poster-next {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    border: 1px solid var(--border);
    background: var(--white);
    color: var(--text-body);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: var(--transition);
}

.poster-prev:hover,
.poster-next:hover {
    background: var(--primary);
    color: var(--white);
    border-color: var(--primary);
}

.poster-counter {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-body);
}
</style>
