<script setup>
import logoImg from '@/assets/images/logo.png'
</script>

<template>
    <footer class="main-footer">
        <div class="footer-inner">
            <div class="footer-brand">
                <img :src="logoImg" alt="에스엠 산업안전 로고">
                <p class="footer-company-name">에스엠 산업안전</p>
            </div>
            <div class="footer-info">
                <div class="footer-col">
                    <h4>연락처</h4>
                    <p><i class="fas fa-envelope"></i> yst0803@hanmail.net</p>
                    <p><i class="fas fa-phone"></i> 0507-0523-1234</p>
                </div>
                <div class="footer-col">
                    <h4>주소</h4>
                    <p><i class="fas fa-map-marker-alt"></i> 경상남도 양산시 소주로 182</p>
                    <p>(서창대동아파트, 상가동 205호)</p>
                </div>
                <div class="footer-col">
                    <h4>운영시간</h4>
                    <p><i class="fas fa-clock"></i> 평일 08:00 ~ 17:00</p>
                    <p>주말 및 공휴일 휴무</p>
                </div>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2026 에스엠 산업안전. All Rights Reserved.</p>
        </div>
    </footer>
</template>

<style scoped>
.main-footer {
    background: var(--primary-dark);
    color: rgba(255, 255, 255, 0.7);
}

.footer-inner {
    max-width: var(--max-width);
    margin: 0 auto;
    padding: 50px 30px;
    display: flex;
    align-items: flex-start;
    gap: 60px;
}

.footer-brand img {
    height: 40px;
    margin-bottom: 12px;
    filter: brightness(0) invert(1);
    opacity: 0.9;
}

.footer-company-name {
    font-size: 16px;
    font-weight: 700;
    color: var(--white);
}

.footer-info {
    display: flex;
    gap: 60px;
    flex: 1;
}

.footer-col h4 {
    font-size: 14px;
    font-weight: 600;
    color: var(--white);
    margin-bottom: 14px;
}

.footer-col p {
    font-size: 13px;
    line-height: 1.8;
    display: flex;
    align-items: center;
    gap: 8px;
}

.footer-col p i {
    font-size: 12px;
    color: var(--accent);
}

.footer-bottom {
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    text-align: center;
    padding: 18px 30px;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
}

@media (max-width: 1024px) {
    .footer-inner {
        flex-direction: column;
        gap: 30px;
    }

    .footer-info {
        flex-wrap: wrap;
        gap: 30px;
    }
}

@media (max-width: 768px) {
    .footer-info {
        flex-direction: column;
        gap: 24px;
    }
}
</style>
