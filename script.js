document.addEventListener("DOMContentLoaded", () => {
    // 팝업 동작
    const popup = document.getElementById('popup');
    const closeBtn = document.querySelector('.close');
    popup.style.display = "flex";
    closeBtn.addEventListener('click', () => popup.style.display = "none");
    window.addEventListener('click', (e) => {
        if (e.target === popup) popup.style.display = "none";
    });

    // 슬라이드쇼
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

    // 포스터 슬라이드
    const posterCarousel = document.getElementById('poster-carousel');
    let posterOffset = 0;
    const posterSlides = document.querySelectorAll('.poster-slide');
    setInterval(() => {
        posterOffset = (posterOffset + 1) % posterSlides.length;
        posterCarousel.style.transform = `translateX(-${posterOffset * 320}px)`;
    }, 3000);

    // 기관 로고 슬라이드
    const orgCarousel = document.getElementById('org-carousel');
    let orgOffset = 0;
    const orgSlides = document.querySelectorAll('.org-slide');
    setInterval(() => {
        orgOffset = (orgOffset + 1) % orgSlides.length;
        orgCarousel.style.transform = `translateX(-${orgOffset * 320}px)`;
    }, 4000);
});
