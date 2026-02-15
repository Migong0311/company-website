<script setup>
import LocationBox from './LocationBox.vue'
import NewsCarousel from './NewsCarousel.vue'
</script>

<template>
    <section class="info-section">
        <div class="info-grid">
            <LocationBox />
            <NewsCarousel />
        </div>
    </section>
</template>

<style scoped>
.info-section {
    background: var(--bg-section);
    padding: 80px 30px;
}

.info-grid {
    max-width: var(--max-width);
    margin: 0 auto;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 50px;
}

@media (max-width: 1024px) {
    .info-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 768px) {
    .info-section {
        padding: 50px 20px;
    }
}
</style>
