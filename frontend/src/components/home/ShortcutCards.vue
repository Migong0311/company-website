<script setup>
const shortcuts = [
    { icon: 'fas fa-building', title: '회사소개', subtitle: 'Company Introduction', href: '#about' },
    { icon: 'fas fa-comments', title: 'Q&A 게시판', subtitle: 'Question & Answer', href: '#qna' },
    { icon: 'fas fa-folder-open', title: '자료실', subtitle: 'Reference Room', href: '#reference' },
]
</script>

<template>
    <section class="shortcut-section">
        <div class="shortcut-cards">
            <a
                v-for="(item, index) in shortcuts"
                :key="index"
                :href="item.href"
                class="shortcut-card"
                v-scroll-animate
            >
                <div class="shortcut-icon">
                    <i :class="item.icon"></i>
                </div>
                <div class="shortcut-text">
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.subtitle }}</span>
                </div>
                <i class="fas fa-arrow-right shortcut-arrow"></i>
            </a>
        </div>
    </section>
</template>

<style scoped>
.shortcut-section {
    position: relative;
    z-index: 10;
    margin-top: -50px;
    padding: 0 20px;
}

.shortcut-cards {
    max-width: 900px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
}

.shortcut-card {
    display: flex;
    align-items: center;
    gap: 16px;
    background: var(--white);
    padding: 24px 28px;
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-md);
    transition: var(--transition);
    cursor: pointer;
}

.shortcut-card:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
}

.shortcut-card:hover .shortcut-icon {
    background: var(--primary);
}

.shortcut-card:hover .shortcut-icon i {
    color: var(--white);
}

.shortcut-card:hover .shortcut-arrow {
    transform: translateX(4px);
    color: var(--primary);
}

.shortcut-icon {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    background: var(--bg-light);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: var(--transition);
}

.shortcut-icon i {
    font-size: 20px;
    color: var(--primary);
    transition: var(--transition);
}

.shortcut-text {
    flex: 1;
}

.shortcut-text strong {
    display: block;
    font-size: 16px;
    color: var(--text-dark);
    margin-bottom: 2px;
}

.shortcut-text span {
    font-size: 12px;
    color: var(--text-muted);
}

.shortcut-arrow {
    color: var(--text-muted);
    font-size: 14px;
    transition: var(--transition);
}

@media (max-width: 768px) {
    .shortcut-cards {
        grid-template-columns: 1fr;
        max-width: 400px;
    }

    .shortcut-section {
        margin-top: -30px;
    }
}
</style>
