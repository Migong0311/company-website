<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import slide1 from '@/assets/images/slideshow_1.png'
import slide2 from '@/assets/images/slideshow_2.jpg'
import slide3 from '@/assets/images/slideshow_3.jpg'
import slide4 from '@/assets/images/slideshow_4.jpeg'

const slides = [
    { image: slide1, alt: '슬라이드1' },
    { image: slide2, alt: '슬라이드2' },
    { image: slide3, alt: '슬라이드3' },
    { image: slide4, alt: '슬라이드4' },
]

const slideTexts = [
    '산업보건 Total Service 제공이 가능한 기업',
    '기업 맞춤형 안전보건 컨설팅 서비스',
    '현장 밀착형 작업환경개선 솔루션 제공',
    '질식·재해예방 One-Call 서비스 운영 중',
]

const currentSlide = ref(0)
const progressBarRef = ref(null)
const slideDuration = 5000
let slideTimer = null

function goToSlide(index) {
    currentSlide.value = index
    resetProgress()
}

function nextSlide() {
    goToSlide((currentSlide.value + 1) % slides.length)
}

function resetProgress() {
    const bar = progressBarRef.value
    if (!bar) return
    bar.style.transition = 'none'
    bar.style.width = '0%'
    void bar.offsetWidth
    bar.style.transition = `width ${slideDuration}ms linear`
    bar.style.width = '100%'

    clearTimeout(slideTimer)
    slideTimer = setTimeout(nextSlide, slideDuration)
}

onMounted(() => {
    resetProgress()
})

onUnmounted(() => {
    clearTimeout(slideTimer)
})
</script>

<template>
    <section class="hero-slider">
        <div class="hero-overlay"></div>
        <div class="hero-slides">
            <div
                v-for="(slide, index) in slides"
                :key="index"
                class="hero-slide"
                :class="{ active: index === currentSlide }"
            >
                <img :src="slide.image" :alt="slide.alt">
            </div>
        </div>
        <div class="hero-content">
            <p class="hero-subtitle">SM Industrial Safety</p>
            <h1 class="hero-title">{{ slideTexts[currentSlide] }}</h1>
            <div class="hero-bar"></div>
        </div>
        <div class="hero-indicators">
            <span
                v-for="(slide, index) in slides"
                :key="index"
                class="hero-dot"
                :class="{ active: index === currentSlide }"
                @click="goToSlide(index)"
            ></span>
        </div>
        <div class="hero-progress">
            <div ref="progressBarRef" class="hero-progress-bar"></div>
        </div>
    </section>
</template>

<style scoped>
.hero-slider {
    position: relative;
    width: 100%;
    height: 520px;
    overflow: hidden;
    margin-top: 72px;
}

.hero-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(
        135deg,
        rgba(15, 36, 64, 0.75) 0%,
        rgba(26, 58, 92, 0.5) 50%,
        rgba(42, 90, 140, 0.3) 100%
    );
    z-index: 2;
}

.hero-slides {
    position: absolute;
    inset: 0;
}

.hero-slide {
    position: absolute;
    inset: 0;
    opacity: 0;
    transition: opacity 1s ease-in-out;
}

.hero-slide.active {
    opacity: 1;
}

.hero-slide img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.hero-content {
    position: absolute;
    z-index: 3;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    width: 90%;
    max-width: 800px;
}

.hero-subtitle {
    font-size: 14px;
    font-weight: 500;
    color: var(--accent-light);
    letter-spacing: 4px;
    text-transform: uppercase;
    margin-bottom: 16px;
}

.hero-title {
    font-size: 36px;
    font-weight: 900;
    color: var(--white);
    line-height: 1.4;
    text-shadow: 0 2px 20px rgba(0,0,0,0.3);
    transition: opacity 0.5s ease;
}

.hero-bar {
    width: 60px;
    height: 3px;
    background: var(--accent);
    margin: 24px auto 0;
    border-radius: 2px;
}

.hero-indicators {
    position: absolute;
    bottom: 40px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 12px;
    z-index: 5;
}

.hero-dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.4);
    cursor: pointer;
    transition: var(--transition);
    border: 2px solid transparent;
}

.hero-dot.active {
    background: var(--accent);
    border-color: var(--white);
    transform: scale(1.2);
}

.hero-dot:hover {
    background: rgba(255, 255, 255, 0.7);
}

.hero-progress {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background: rgba(255, 255, 255, 0.15);
    z-index: 5;
}

.hero-progress-bar {
    height: 100%;
    background: var(--accent);
    width: 0%;
    transition: width 0.1s linear;
}

@media (max-width: 1024px) {
    .hero-slider {
        height: 420px;
    }

    .hero-title {
        font-size: 28px;
    }
}

@media (max-width: 768px) {
    .hero-slider {
        height: 360px;
        margin-top: 60px;
    }

    .hero-title {
        font-size: 22px;
    }

    .hero-subtitle {
        font-size: 12px;
    }
}
</style>
