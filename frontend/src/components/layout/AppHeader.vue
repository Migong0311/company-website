<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'
import logoImg from '@/assets/images/logo_2.png'

const authStore = useAuthStore()
const { isLoggedIn, adminName } = storeToRefs(authStore)

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

async function openLoginModal() {
    const { value: formValues } = await Swal.fire({
        title: '관리자 로그인',
        html:
            '<input id="swal-username" class="swal2-input" placeholder="아이디">' +
            '<input id="swal-password" class="swal2-input" type="password" placeholder="비밀번호">',
        focusConfirm: false,
        showCancelButton: true,
        confirmButtonText: '로그인',
        cancelButtonText: '취소',
        confirmButtonColor: '#1a3a5c',
        preConfirm: () => {
            const username = document.getElementById('swal-username').value
            const password = document.getElementById('swal-password').value
            if (!username || !password) {
                Swal.showValidationMessage('아이디와 비밀번호를 입력해주세요.')
                return false
            }
            return { username, password }
        }
    })

    if (!formValues) return

    try {
        await authStore.login(formValues.username, formValues.password)
        Swal.fire({ icon: 'success', title: '로그인 성공', text: `${adminName.value}님 환영합니다.`, timer: 1500, showConfirmButton: false })
    } catch {
        Swal.fire({ icon: 'error', title: '로그인 실패', text: '아이디 또는 비밀번호가 올바르지 않습니다.' })
    }
}

async function handleLogout() {
    const result = await Swal.fire({
        icon: 'question',
        title: '로그아웃',
        text: '로그아웃 하시겠습니까?',
        showCancelButton: true,
        confirmButtonText: '로그아웃',
        cancelButtonText: '취소',
        confirmButtonColor: '#1a3a5c'
    })
    if (!result.isConfirmed) return

    await authStore.logout()
    Swal.fire({ icon: 'success', title: '로그아웃 완료', timer: 1500, showConfirmButton: false })
}

onMounted(() => {
    window.addEventListener('scroll', handleScroll)
    authStore.checkLogin()
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
                        <router-link to="/references" class="nav-link" @click="closeMobileMenu">자료실</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link to="/qna" class="nav-link" @click="closeMobileMenu">Q&amp;A</router-link>
                    </li>
                </ul>
                <!-- 모바일 로그인 버튼 -->
                <div class="mobile-auth">
                    <button v-if="!isLoggedIn" class="auth-btn-mobile" @click="openLoginModal(); closeMobileMenu()">
                        <i class="fas fa-lock"></i> 관리자 로그인
                    </button>
                    <div v-else class="auth-info-mobile">
                        <span class="admin-label"><i class="fas fa-user-shield"></i> {{ adminName }}</span>
                        <button class="auth-btn-mobile logout" @click="handleLogout(); closeMobileMenu()">
                            <i class="fas fa-sign-out-alt"></i> 로그아웃
                        </button>
                    </div>
                </div>
            </nav>
            <!-- 데스크탑 로그인 버튼 -->
            <div class="header-auth">
                <button v-if="!isLoggedIn" class="auth-btn" @click="openLoginModal">
                    <i class="fas fa-lock"></i> 관리자
                </button>
                <div v-else class="auth-info">
                    <span class="admin-label"><i class="fas fa-user-shield"></i> {{ adminName }}</span>
                    <button class="auth-btn logout" @click="handleLogout">
                        <i class="fas fa-sign-out-alt"></i>
                    </button>
                </div>
            </div>
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

/* 데스크탑 인증 버튼 */
.header-auth {
    display: flex;
    align-items: center;
    gap: 8px;
}

.auth-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 7px 16px;
    background: transparent;
    color: var(--text-muted);
    border: 1px solid var(--border);
    border-radius: 20px;
    font-size: 0.8rem;
    cursor: pointer;
    transition: var(--transition);
}

.auth-btn:hover {
    border-color: var(--primary);
    color: var(--primary);
}

.auth-btn.logout {
    width: 32px;
    height: 32px;
    padding: 0;
    justify-content: center;
    border-radius: 50%;
    color: var(--text-muted);
}

.auth-btn.logout:hover {
    background: #e74c3c;
    border-color: #e74c3c;
    color: var(--white);
}

.auth-info {
    display: flex;
    align-items: center;
    gap: 10px;
}

.admin-label {
    font-size: 0.8rem;
    color: var(--primary);
    font-weight: 500;
}

.admin-label i {
    margin-right: 4px;
}

/* 모바일 인증 */
.mobile-auth {
    display: none;
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
    .header-auth {
        display: none;
    }

    .mobile-auth {
        display: block;
        padding: 12px 24px;
        border-top: 1px solid var(--border);
        margin-top: 8px;
    }

    .auth-btn-mobile {
        display: block;
        width: 100%;
        padding: 10px;
        background: var(--primary);
        color: var(--white);
        border: none;
        border-radius: var(--radius-sm);
        font-size: 0.85rem;
        cursor: pointer;
        text-align: center;
    }

    .auth-btn-mobile.logout {
        background: transparent;
        color: #e74c3c;
        border: 1px solid #e74c3c;
        margin-top: 8px;
    }

    .auth-info-mobile {
        text-align: center;
    }

    .auth-info-mobile .admin-label {
        display: block;
        margin-bottom: 8px;
        font-size: 0.9rem;
    }

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
