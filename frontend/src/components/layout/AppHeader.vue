<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import logoImg from '@/assets/images/logo_2.png'

const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)

function handleScroll() {
    isScrolled.value = window.scrollY > 10
}

function toggleMobileMenu() {
    isMobileMenuOpen.value = !isMobileMenuOpen.value
}

function closeMobileMenu() {
    isMobileMenuOpen.value = false
}

onMounted(() => {
    window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
    <header class="main-header" :class="{ scrolled: isScrolled }">
        <div class="header-inner">
            <router-link to="/" class="logo-link">
                <img :src="logoImg" alt="에스엠 산업안전 로고" class="logo">
            </router-link>
            <nav class="main-nav" :class="{ 'mobile-open': isMobileMenuOpen }">
                <ul class="nav-list">
                    <li class="nav-item">
                        <a href="#about" class="nav-link" @click="closeMobileMenu">회사소개</a>
                        <ul class="dropdown">
                            <li><a href="#about" @click="closeMobileMenu">회사 개요</a></li>
                            <li><a href="#ceo" @click="closeMobileMenu">CEO 인사말</a></li>
                        </ul>
                    </li>
                    <li class="nav-item">
                        <a href="#reference" class="nav-link" @click="closeMobileMenu">자료실</a>
                    </li>
                    <li class="nav-item">
                        <a href="#qna" class="nav-link" @click="closeMobileMenu">Q&amp;A</a>
                    </li>
                </ul>
            </nav>
            <button class="mobile-menu-btn" aria-label="메뉴 열기" @click="toggleMobileMenu">
                <i class="fas" :class="isMobileMenuOpen ? 'fa-times' : 'fa-bars'"></i>
            </button>
        </div>
    </header>
</template>

<style scoped>
.main-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
    background: rgba(255, 255, 255, 0.97);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid var(--border);
    transition: var(--transition);
}

.main-header.scrolled {
    box-shadow: var(--shadow-sm);
}

.header-inner {
    max-width: var(--max-width);
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 30px;
    height: 72px;
}

.logo-link {
    display: flex;
    align-items: center;
}

.logo {
    height: 44px;
    transition: var(--transition);
}

.logo:hover {
    opacity: 0.8;
}

.nav-list {
    display: flex;
    gap: 8px;
}

.nav-item {
    position: relative;
}

.nav-link {
    display: block;
    padding: 10px 22px;
    font-size: 15px;
    font-weight: 500;
    color: var(--text-dark);
    border-radius: var(--radius-sm);
    transition: var(--transition);
    position: relative;
}

.nav-link::after {
    content: '';
    position: absolute;
    bottom: 4px;
    left: 50%;
    width: 0;
    height: 2px;
    background: var(--primary);
    transition: var(--transition);
    transform: translateX(-50%);
}

.nav-link:hover::after {
    width: 60%;
}

.nav-link:hover {
    color: var(--primary);
}

.dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    min-width: 180px;
    background: var(--white);
    border-radius: var(--radius-sm);
    box-shadow: var(--shadow-md);
    padding: 8px 0;
    opacity: 0;
    visibility: hidden;
    transform: translateY(8px);
    transition: var(--transition);
}

.nav-item:hover .dropdown {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
}

.dropdown li a {
    display: block;
    padding: 10px 20px;
    font-size: 14px;
    color: var(--text-body);
    transition: var(--transition);
}

.dropdown li a:hover {
    background: var(--bg-light);
    color: var(--primary);
}

.mobile-menu-btn {
    display: none;
    background: none;
    border: none;
    font-size: 24px;
    color: var(--text-dark);
    cursor: pointer;
}

@media (max-width: 768px) {
    .main-nav {
        display: none;
    }

    .main-nav.mobile-open {
        display: block;
        position: absolute;
        top: 60px;
        left: 0;
        right: 0;
        background: var(--white);
        box-shadow: var(--shadow-md);
        padding: 16px 0;
    }

    .main-nav.mobile-open .nav-list {
        flex-direction: column;
        gap: 0;
    }

    .main-nav.mobile-open .nav-link {
        padding: 12px 24px;
    }

    .main-nav.mobile-open .dropdown {
        position: static;
        opacity: 1;
        visibility: visible;
        transform: none;
        box-shadow: none;
        padding-left: 20px;
    }

    .mobile-menu-btn {
        display: block;
    }

    .header-inner {
        padding: 0 16px;
        height: 60px;
    }

    .logo {
        height: 36px;
    }
}
</style>
