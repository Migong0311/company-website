<script setup>
import gov1 from '@/assets/images/gov1.png'
import gov2 from '@/assets/images/gov2.png'
import gov3 from '@/assets/images/gov3.png'

const partners = [
    { image: gov1, alt: '안전보건공단', url: 'https://www.kosha.or.kr' },
    { image: gov2, alt: '고용노동부', url: 'https://www.moel.go.kr' },
    { image: gov3, alt: 'K-MAS', url: 'https://www.k-mas.or.kr' },
]
</script>

<template>
    <section class="partners-section">
        <div class="section-header">
            <p class="section-subtitle">Partners</p>
            <h2 class="section-title">유관기관</h2>
            <div class="section-bar"></div>
        </div>
        <div class="partners-grid">
            <a
                v-for="(partner, index) in partners"
                :key="index"
                :href="partner.url"
                target="_blank"
                rel="noopener"
                class="partner-logo"
                v-scroll-animate
            >
                <img :src="partner.image" :alt="partner.alt">
            </a>
        </div>
    </section>
</template>

<style scoped>
.partners-section {
    padding: 60px 30px;
    max-width: var(--max-width);
    margin: 0 auto;
}

.partners-grid {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 60px;
    flex-wrap: wrap;
}

.partner-logo {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 16px 30px;
    border-radius: var(--radius-sm);
    border: 1px solid var(--border);
    background: var(--white);
    transition: var(--transition);
}

.partner-logo:hover {
    box-shadow: var(--shadow-sm);
    transform: translateY(-2px);
    border-color: var(--primary-light);
}

.partner-logo img {
    height: 50px;
    width: auto;
    filter: grayscale(30%);
    transition: var(--transition);
}

.partner-logo:hover img {
    filter: grayscale(0%);
}

@media (max-width: 768px) {
    .partners-grid {
        gap: 20px;
    }

    .partner-logo {
        padding: 12px 20px;
    }

    .partner-logo img {
        height: 36px;
    }
}
</style>
