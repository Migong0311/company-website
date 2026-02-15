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
const showAdminMenu = ref(false)

function handleScroll() {
    isScrolled.value = window.scrollY > 10
}

function toggleMobileMenu() {
    isMobileMenuOpen.value = !isMobileMenuOpen.value
}

function closeMobileMenu() {
    isMobileMenuOpen.value = false
}

function toggleAdminMenu() {
    showAdminMenu.value = !showAdminMenu.value
}

function closeAdminMenu(e) {
    if (!e.target.closest('.admin-menu-wrap')) {
        showAdminMenu.value = false
    }
}

/* 로그인 모달 — 엔터키 지원 + 실패 시 모달 유지 */
async function openLoginModal() {
    closeMobileMenu()
    const { value: result } = await Swal.fire({
        title: '관리자 로그인',
        html:
            '<input id="swal-username" class="swal2-input" placeholder="아이디" autocomplete="username">' +
            '<input id="swal-password" class="swal2-input" type="password" placeholder="비밀번호" autocomplete="current-password">',
        focusConfirm: false,
        showCancelButton: true,
        confirmButtonText: '로그인',
        cancelButtonText: '취소',
        confirmButtonColor: '#1a3a5c',
        didOpen: () => {
            const pwInput = document.getElementById('swal-password')
            const unInput = document.getElementById('swal-username')
            const handler = (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault()
                    Swal.clickConfirm()
                }
            }
            pwInput.addEventListener('keydown', handler)
            unInput.addEventListener('keydown', handler)
        },
        preConfirm: async () => {
            const username = document.getElementById('swal-username').value
            const password = document.getElementById('swal-password').value
            if (!username || !password) {
                Swal.showValidationMessage('아이디와 비밀번호를 입력해주세요.')
                return false
            }
            try {
                await authStore.login(username, password)
                return true
            } catch {
                Swal.showValidationMessage('아이디 또는 비밀번호가 올바르지 않습니다.')
                return false
            }
        }
    })

    if (result) {
        Swal.fire({ icon: 'success', title: '로그인 성공', text: `${adminName.value}님 환영합니다.`, timer: 1500, showConfirmButton: false })
    }
}

async function handleLogout() {
    closeMobileMenu()
    showAdminMenu.value = false
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

/* 비밀번호 변경 모달 */
async function openPasswordModal() {
    showAdminMenu.value = false
    const { value: result } = await Swal.fire({
        title: '비밀번호 변경',
        html:
            '<input id="swal-cur-pw" class="swal2-input" type="password" placeholder="현재 비밀번호">' +
            '<input id="swal-new-pw" class="swal2-input" type="password" placeholder="새 비밀번호">' +
            '<input id="swal-new-pw2" class="swal2-input" type="password" placeholder="새 비밀번호 확인">',
        focusConfirm: false,
        showCancelButton: true,
        confirmButtonText: '변경',
        cancelButtonText: '취소',
        confirmButtonColor: '#1a3a5c',
        didOpen: () => {
            document.getElementById('swal-new-pw2').addEventListener('keydown', (e) => {
                if (e.key === 'Enter') { e.preventDefault(); Swal.clickConfirm() }
            })
        },
        preConfirm: async () => {
            const cur = document.getElementById('swal-cur-pw').value
            const np = document.getElementById('swal-new-pw').value
            const np2 = document.getElementById('swal-new-pw2').value
            if (!cur || !np || !np2) {
                Swal.showValidationMessage('모든 항목을 입력해주세요.')
                return false
            }
            if (np !== np2) {
                Swal.showValidationMessage('새 비밀번호가 일치하지 않습니다.')
                return false
            }
            if (np.length < 4) {
                Swal.showValidationMessage('비밀번호는 4자 이상이어야 합니다.')
                return false
            }
            try {
                await authStore.changePassword(cur, np)
                return true
            } catch {
                Swal.showValidationMessage('현재 비밀번호가 일치하지 않습니다.')
                return false
            }
        }
    })

    if (result) {
        Swal.fire({ icon: 'success', title: '비밀번호 변경 완료', timer: 1500, showConfirmButton: false })
    }
}

/* 관리자 계정 관리 모달 */
async function openAdminManageModal() {
    showAdminMenu.value = false

    let admins = []
    try {
        admins = await authStore.getAdminList()
    } catch {
        Swal.fire({ icon: 'error', title: '오류', text: '관리자 목록을 불러올 수 없습니다.' })
        return
    }

    const listHtml = admins.map(a =>
        `<div style="display:flex;align-items:center;justify-content:space-between;padding:8px 4px;border-bottom:1px solid #eee;">
            <span><b>${a.name}</b> <small style="color:#888">(${a.username})</small></span>
            <button class="admin-del-btn" data-id="${a.id}" style="background:#e74c3c;color:#fff;border:none;border-radius:4px;padding:4px 10px;cursor:pointer;font-size:12px;">삭제</button>
        </div>`
    ).join('')

    const { value: action } = await Swal.fire({
        title: '관리자 계정 관리',
        html:
            `<div style="max-height:200px;overflow-y:auto;margin-bottom:12px;text-align:left;">${listHtml || '<p style="color:#888;text-align:center;">관리자 없음</p>'}</div>
             <hr style="margin:12px 0;">
             <p style="font-weight:600;margin-bottom:8px;text-align:left;">새 관리자 추가</p>
             <input id="swal-admin-name" class="swal2-input" placeholder="이름" style="margin-top:0;">
             <input id="swal-admin-user" class="swal2-input" placeholder="아이디">
             <input id="swal-admin-pw" class="swal2-input" type="password" placeholder="비밀번호">`,
        showCancelButton: true,
        confirmButtonText: '추가',
        cancelButtonText: '닫기',
        confirmButtonColor: '#1a3a5c',
        didOpen: () => {
            // 삭제 버튼 핸들러
            document.querySelectorAll('.admin-del-btn').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    const id = e.target.dataset.id
                    const confirm = await Swal.fire({
                        icon: 'warning',
                        title: '삭제 확인',
                        text: '이 관리자 계정을 삭제하시겠습니까?',
                        showCancelButton: true,
                        confirmButtonText: '삭제',
                        cancelButtonText: '취소',
                        confirmButtonColor: '#e74c3c'
                    })
                    if (confirm.isConfirmed) {
                        try {
                            await authStore.deleteAdmin(id)
                            Swal.fire({ icon: 'success', title: '삭제 완료', timer: 1200, showConfirmButton: false })
                            // 모달 다시 열기
                            setTimeout(() => openAdminManageModal(), 1300)
                        } catch {
                            Swal.fire({ icon: 'error', title: '삭제 실패', text: '관리자 삭제에 실패했습니다.' })
                        }
                    }
                })
            })
            // 엔터키
            document.getElementById('swal-admin-pw').addEventListener('keydown', (e) => {
                if (e.key === 'Enter') { e.preventDefault(); Swal.clickConfirm() }
            })
        },
        preConfirm: async () => {
            const name = document.getElementById('swal-admin-name').value
            const username = document.getElementById('swal-admin-user').value
            const password = document.getElementById('swal-admin-pw').value
            if (!name || !username || !password) {
                Swal.showValidationMessage('모든 항목을 입력해주세요.')
                return false
            }
            if (password.length < 4) {
                Swal.showValidationMessage('비밀번호는 4자 이상이어야 합니다.')
                return false
            }
            try {
                await authStore.registerAdmin(username, password, name)
                return true
            } catch (err) {
                Swal.showValidationMessage(err.response?.data?.message || '관리자 추가에 실패했습니다.')
                return false
            }
        }
    })

    if (action) {
        await Swal.fire({ icon: 'success', title: '관리자 추가 완료', timer: 1200, showConfirmButton: false })
        setTimeout(() => openAdminManageModal(), 1300)
    }
}

onMounted(() => {
    window.addEventListener('scroll', handleScroll)
    document.addEventListener('click', closeAdminMenu)
    authStore.checkLogin()
})

onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll)
    document.removeEventListener('click', closeAdminMenu)
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
                    <button v-if="!isLoggedIn" class="auth-btn-mobile" @click="openLoginModal">
                        <i class="fas fa-lock"></i> 관리자 로그인
                    </button>
                    <div v-else class="auth-info-mobile">
                        <span class="admin-label"><i class="fas fa-user-shield"></i> {{ adminName }}</span>
                        <button class="auth-btn-mobile" @click="openPasswordModal">
                            <i class="fas fa-key"></i> 비밀번호 변경
                        </button>
                        <button class="auth-btn-mobile" @click="openAdminManageModal">
                            <i class="fas fa-users-cog"></i> 계정 관리
                        </button>
                        <button class="auth-btn-mobile logout" @click="handleLogout">
                            <i class="fas fa-sign-out-alt"></i> 로그아웃
                        </button>
                    </div>
                </div>
            </nav>
            <!-- 데스크탑 인증 버튼 -->
            <div class="header-auth">
                <button v-if="!isLoggedIn" class="auth-btn" @click="openLoginModal">
                    <i class="fas fa-lock"></i> 관리자
                </button>
                <div v-else class="auth-info admin-menu-wrap">
                    <button class="auth-btn admin-toggle" @click.stop="toggleAdminMenu">
                        <i class="fas fa-user-shield"></i> {{ adminName }} <i class="fas fa-caret-down"></i>
                    </button>
                    <div v-show="showAdminMenu" class="admin-dropdown">
                        <button @click="openPasswordModal"><i class="fas fa-key"></i> 비밀번호 변경</button>
                        <button @click="openAdminManageModal"><i class="fas fa-users-cog"></i> 계정 관리</button>
                        <hr>
                        <button class="danger" @click="handleLogout"><i class="fas fa-sign-out-alt"></i> 로그아웃</button>
                    </div>
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

.auth-btn.admin-toggle {
    color: var(--primary);
    border-color: var(--primary);
    font-weight: 500;
}

.auth-btn.admin-toggle:hover {
    background: var(--primary);
    color: var(--white);
}

.auth-info {
    position: relative;
}

.admin-label {
    font-size: 0.8rem;
    color: var(--primary);
    font-weight: 500;
}

.admin-label i {
    margin-right: 4px;
}

/* 관리자 드롭다운 메뉴 */
.admin-dropdown {
    position: absolute;
    top: calc(100% + 8px);
    right: 0;
    min-width: 180px;
    background: var(--white);
    border-radius: var(--radius-sm);
    box-shadow: var(--shadow-md);
    padding: 6px 0;
    z-index: 1001;
}

.admin-dropdown button {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
    padding: 10px 16px;
    background: none;
    border: none;
    font-size: 0.85rem;
    color: var(--text-body);
    cursor: pointer;
    transition: var(--transition);
    text-align: left;
}

.admin-dropdown button:hover {
    background: var(--bg-light);
    color: var(--primary);
}

.admin-dropdown button.danger {
    color: #e74c3c;
}

.admin-dropdown button.danger:hover {
    background: #fef2f2;
}

.admin-dropdown hr {
    border: none;
    border-top: 1px solid var(--border);
    margin: 4px 0;
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
        margin-bottom: 6px;
    }

    .auth-btn-mobile.logout {
        background: transparent;
        color: #e74c3c;
        border: 1px solid #e74c3c;
        margin-top: 4px;
    }

    .auth-info-mobile {
        text-align: center;
    }

    .auth-info-mobile .admin-label {
        display: block;
        margin-bottom: 10px;
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
