document.addEventListener("DOMContentLoaded", () => {

    // ==========================================
    // 팝업 동작
    // ==========================================
    const popup = document.getElementById('popup');
    const closeBtn = document.querySelector('.popup-close');

    // 딜레이 후 팝업 표시
    setTimeout(() => {
        popup.classList.add('show');
    }, 500);

    function closePopup() {
        popup.classList.remove('show');
    }

    closeBtn.addEventListener('click', closePopup);
    popup.addEventListener('click', (e) => {
        if (e.target === popup) closePopup();
    });

    // ==========================================
    // 헤더 스크롤 효과
    // ==========================================
    const header = document.querySelector('.main-header');
    window.addEventListener('scroll', () => {
        header.classList.toggle('scrolled', window.scrollY > 10);
    });

    // ==========================================
    // 히어로 슬라이더
    // ==========================================
    const slideTexts = [
        "산업보건 Total Service 제공이 가능한 기업",
        "기업 맞춤형 안전보건 컨설팅 서비스",
        "현장 밀착형 작업환경개선 솔루션 제공",
        "질식·재해예방 One-Call 서비스 운영 중"
    ];

    const slides = document.querySelectorAll('.hero-slide');
    const dots = document.querySelectorAll('.hero-dot');
    const titleEl = document.getElementById('slide-title');
    const progressBar = document.querySelector('.hero-progress-bar');
    let currentSlide = 0;
    const slideDuration = 5000;
    let slideTimer = null;
    let progressTimer = null;

    function goToSlide(index) {
        slides[currentSlide].classList.remove('active');
        dots[currentSlide].classList.remove('active');

        currentSlide = index;

        slides[currentSlide].classList.add('active');
        dots[currentSlide].classList.add('active');
        titleEl.textContent = slideTexts[currentSlide];

        resetProgress();
    }

    function nextSlide() {
        goToSlide((currentSlide + 1) % slides.length);
    }

    function resetProgress() {
        progressBar.style.transition = 'none';
        progressBar.style.width = '0%';

        // Force reflow
        void progressBar.offsetWidth;

        progressBar.style.transition = `width ${slideDuration}ms linear`;
        progressBar.style.width = '100%';

        clearTimeout(slideTimer);
        slideTimer = setTimeout(nextSlide, slideDuration);
    }

    // 도트 클릭 이벤트
    dots.forEach((dot) => {
        dot.addEventListener('click', () => {
            const index = parseInt(dot.dataset.index);
            if (index !== currentSlide) {
                goToSlide(index);
            }
        });
    });

    // 초기 실행
    resetProgress();

    // ==========================================
    // 포스터 캐러셀
    // ==========================================
    const posterCarousel = document.getElementById('poster-carousel');
    const posterSlides = document.querySelectorAll('.poster-slide');
    const posterCurrent = document.getElementById('poster-current');
    const posterTotal = document.getElementById('poster-total');
    const prevBtn = document.querySelector('.poster-prev');
    const nextBtn = document.querySelector('.poster-next');
    let posterIndex = 0;

    posterTotal.textContent = posterSlides.length;

    function updatePoster(index) {
        posterIndex = index;
        posterCarousel.style.transform = `translateX(-${posterIndex * 100}%)`;
        posterCurrent.textContent = posterIndex + 1;
    }

    prevBtn.addEventListener('click', () => {
        const newIndex = posterIndex <= 0 ? posterSlides.length - 1 : posterIndex - 1;
        updatePoster(newIndex);
    });

    nextBtn.addEventListener('click', () => {
        const newIndex = posterIndex >= posterSlides.length - 1 ? 0 : posterIndex + 1;
        updatePoster(newIndex);
    });

    // 자동 슬라이드
    setInterval(() => {
        const newIndex = posterIndex >= posterSlides.length - 1 ? 0 : posterIndex + 1;
        updatePoster(newIndex);
    }, 4000);

    // ==========================================
    // 스크롤 애니메이션 (Intersection Observer)
    // ==========================================
    const animateElements = document.querySelectorAll(
        '.service-card, .shortcut-card, .location-box, .news-box, .partner-logo'
    );

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.15 });

    animateElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });
});
