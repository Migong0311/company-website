<script setup>
const services = [
    { icon: 'fas fa-shield-alt', title: '안전보건 컨설팅', description: '기업 맞춤형 안전보건 관리 체계 수립 및 컨설팅 서비스를 제공합니다.' },
    { icon: 'fas fa-clipboard-check', title: '작업환경 측정', description: '현장 밀착형 작업환경 측정 및 개선 솔루션을 제공합니다.' },
    { icon: 'fas fa-phone-alt', title: '재해예방 One-Call', description: '질식·재해예방을 위한 24시간 One-Call 서비스를 운영합니다.' },
]
</script>

<template>
    <section class="services-section">
        <div class="section-header">
            <p class="section-subtitle">Our Services</p>
            <h2 class="section-title">주요 서비스 안내</h2>
            <div class="section-bar"></div>
        </div>
        <div class="services-grid">
            <div
                v-for="(service, index) in services"
                :key="index"
                class="service-card"
                v-scroll-animate
            >
                <div class="service-icon-wrap">
                    <i :class="service.icon"></i>
                </div>
                <h3>{{ service.title }}</h3>
                <p>{{ service.description }}</p>
            </div>
        </div>
    </section>
</template>

<style scoped>
.services-section {
    padding: 100px 30px 80px;
    max-width: var(--max-width);
    margin: 0 auto;
}

.services-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;
}

.service-card {
    text-align: center;
    padding: 48px 30px;
    border-radius: var(--radius-md);
    background: var(--white);
    border: 1px solid var(--border);
    transition: var(--transition);
}

.service-card:hover {
    transform: translateY(-6px);
    box-shadow: var(--shadow-md);
    border-color: transparent;
}

.service-icon-wrap {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--primary), var(--primary-light));
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 24px;
}

.service-icon-wrap i {
    font-size: 28px;
    color: var(--white);
}

.service-card h3 {
    font-size: 18px;
    font-weight: 700;
    color: var(--text-dark);
    margin-bottom: 12px;
}

.service-card p {
    font-size: 14px;
    color: var(--text-muted);
    line-height: 1.8;
}

@media (max-width: 1024px) {
    .services-grid {
        grid-template-columns: repeat(3, 1fr);
        gap: 20px;
    }
}

@media (max-width: 768px) {
    .services-section {
        padding: 70px 20px 50px;
    }

    .services-grid {
        grid-template-columns: 1fr;
        gap: 20px;
    }

    .service-card {
        padding: 32px 24px;
    }
}
</style>
