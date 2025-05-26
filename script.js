document.addEventListener("DOMContentLoaded", () => {
    // ✅ 팝업 동작
    const popup = document.getElementById('popup');
    const closeBtn = document.querySelector('.close');
    popup.style.display = "flex";
    closeBtn.addEventListener('click', () => popup.style.display = "none");
    window.addEventListener('click', (e) => {
        if (e.target === popup) popup.style.display = "none";
    });

    // ✅ 상단 메인 배너 슬라이드쇼 텍스트 + 이미지 (페이드 방식)
    const slideTexts = [
        "산업보건 Total Service 제공이 가능한 기업",
        "기업 맞춤형 안전보건 컨설팅 서비스",
        "현장 밀착형 작업환경개선 솔루션 제공",
        "질식·재해예방 One-Call 서비스 운영 중"
    ];
    let slideIndex = 0;
    const slides = document.getElementsByClassName("slide");
    const title = document.getElementById("slide-title");
    const dots = document.getElementsByClassName("dot");

    function showSlides() {
        for (let i = 0; i < slides.length; i++) {
            slides[i].style.display = "none";
            dots[i].classList.remove("active");
        }
        slideIndex++;
        if (slideIndex > slides.length) slideIndex = 1;
        slides[slideIndex - 1].style.display = "block";
        title.textContent = slideTexts[slideIndex - 1];
        dots[slideIndex - 1].classList.add("active");
        setTimeout(showSlides, 4000);
    }
    showSlides();

    // ✅ 포스터 슬라이드 (가로 이동 캐러셀)
    const posterCarousel = document.getElementById("poster-carousel");
    const posterCount = posterCarousel.children.length;
    let posterIndex = 0;

    setInterval(() => {
        posterIndex = (posterIndex + 1) % posterCount;
        const offset = posterIndex * (300 + 20); // 이미지 너비 + margin
        posterCarousel.style.transform = `translateX(-${offset}px)`;
    }, 3000);

    // ✅ 기관 로고 슬라이드 (가로 이동 캐러셀)
    const orgCarousel = document.getElementById("org-carousel");
    const orgCount = orgCarousel.children.length;
    let orgIndex = 0;

    setInterval(() => {
        orgIndex = (orgIndex + 1) % orgCount;
        const offset = orgIndex * (300 + 20); // 로고 간 거리
        orgCarousel.style.transform = `translateX(-${offset}px)`;
    }, 4000);
});
