<script setup>
import { ref, onMounted } from 'vue'

const isVisible = ref(false)

onMounted(() => {
    setTimeout(() => {
        isVisible.value = true
    }, 500)
})

function close() {
    isVisible.value = false
}
</script>

<template>
    <div class="popup-overlay" :class="{ show: isVisible }" @click.self="close">
        <div class="popup-content">
            <button class="popup-close" aria-label="닫기" @click="close">&times;</button>
            <div class="popup-body">
                <i class="fas fa-hard-hat popup-icon"></i>
                <h2>에스엠 산업안전에 오신 것을 환영합니다</h2>
                <p>산업보건 Total Service를 제공하는 신뢰받는 파트너</p>
                <div class="popup-divider"></div>
                <p class="popup-sub">안전하고 건강한 작업환경을 함께 만들어 갑니다.</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.popup-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
    backdrop-filter: blur(4px);
    opacity: 0;
    visibility: hidden;
    transition: var(--transition);
}

.popup-overlay.show {
    opacity: 1;
    visibility: visible;
}

.popup-content {
    background: var(--white);
    border-radius: var(--radius-lg);
    padding: 50px 40px;
    max-width: 480px;
    width: 90%;
    position: relative;
    text-align: center;
    box-shadow: var(--shadow-lg);
    transform: translateY(20px);
    transition: var(--transition);
}

.popup-overlay.show .popup-content {
    transform: translateY(0);
}

.popup-close {
    position: absolute;
    top: 16px;
    right: 20px;
    background: none;
    border: none;
    font-size: 28px;
    color: var(--text-muted);
    cursor: pointer;
    transition: var(--transition);
    line-height: 1;
}

.popup-close:hover {
    color: var(--text-dark);
}

.popup-icon {
    font-size: 48px;
    color: var(--accent);
    margin-bottom: 20px;
}

.popup-body h2 {
    font-size: 22px;
    font-weight: 700;
    color: var(--primary);
    margin-bottom: 10px;
}

.popup-body p {
    color: var(--text-body);
    font-size: 15px;
}

.popup-divider {
    width: 50px;
    height: 3px;
    background: var(--accent);
    margin: 20px auto;
    border-radius: 2px;
}

.popup-sub {
    color: var(--text-muted) !important;
    font-size: 14px !important;
}
</style>
