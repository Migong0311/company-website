# 🛡️ 에스엠 산업안전 웹 서비스 (SM Industrial Safety Website)

![Vue.js](https://img.shields.io/badge/vuejs-%2335495e.svg?style=for-the-badge&logo=vuedotjs&logoColor=%234FC08D)
![Spring Boot](https://img.shields.io/badge/spring_boot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![Oracle Cloud](https://img.shields.io/badge/Oracle_Cloud-%23F80000.svg?style=for-the-badge&logo=oracle&logoColor=white)

> **산업안전 전문기업 SM산업안전의 공식 웹 플랫폼 프로젝트입니다.** 

> 고객에게 신뢰를 주는 기업 소개, 소통을 위한 Q&A, 자료 공유를 위한 자료실 기능을 제공하며, Oracle Cloud 기반의 안정적인 인프라 위에서 운영됩니다.

## 🔴 [Domain](http://168.107.32.191/)


## 🏗️ System Architecture

Oracle Cloud Infrastructure(OCI)를 활용하여 고가용성 및 확장성을 고려한 인프라를 구축하였습니다. GitHub Actions를 통한 CI/CD 파이프라인과 Docker Compose 기반의 컨테이너 오케스트레이션을 적용했습니다.

![](/image/image.png)

---

## ✨ Key Features

### 🏠 메인 페이지 (Main Page)

* **Hero Banner**: 시각적 임팩트를 주는 이미지 슬라이드쇼
* **Service Info**: 안전관리, 컨설팅, 교육 등 핵심 가치 전달
* **Quick Links**: 자료실, Q&A 등 주요 기능 바로가기 카드 UI
* **Partners**: 신뢰도를 높이는 협력사 목록 섹션

### 💬 Q&A 게시판 (Community)

* **계층형 구조**: 질문에 대한 답변과 대댓글(Depth) 지원
* **보안/인증**: 게시글 및 댓글 작성/수정/삭제 시 비밀번호 검증 로직
* **공지사항**: 관리자 전용 공지 (Highlight 처리)
* **편의성**: 제목/내용 검색 및 페이징(15건) 처리

### 📂 자료실 (Reference)

* **직관적 UI**: 썸네일이 포함된 포스터형 4열 그리드 레이아웃
* **파일 관리**: 다중 파일 업로드(최대 5개) 및 개별 다운로드 지원
* **카테고리**: 자료 유형별 필터링 및 검색 기능

### 🛡️ 관리자 시스템 (Admin)

* **대시보드 권한**: 세션 기반의 관리자 인증 시스템
* **콘텐츠 제어**: 비밀번호 없이 모든 게시글/댓글 관리(삭제/수정) 가능
* **관리자 관리**: 슈퍼 관리자에 의한 관리자 계정 생성 및 삭제
* **리소스 관리**: 자료실 업로드 및 카테고리 편집 권한

---

## 🛠️ Tech Stack

| Category | Technology |
| --- | --- |
| **Frontend** | Vue 3, Vite, Pinia, Vue Router, Axios, SweetAlert2 |
| **Backend** | Spring Boot 3, Java 17, JPA/Hibernate, Gradle |
| **Database** | MySQL 8.0 |
| **DevOps** | Docker, Docker Compose, Nginx, GitHub Actions, Oracle Cloud (OCI) |

---

## 📂 Project Structure

```bash
sm-osh-company-website/
├── 📂 frontend/                  # Vue 3 Frontend Application
│   └── src/
│       ├── 📂 views/             # Page Components
│       │   ├── HomeView.vue      # Main Landing Page
│       │   ├── QnaListView.vue   # Q&A Board List
│       │   ├── QnaDetailView.vue # Q&A Detail & Comments
│       │   ├── QnaWriteView.vue  # Q&A Create/Edit Form
│       │   └── ReferenceListView.vue # Reference Gallery
│       ├── 📂 components/        # Reusable UI Components
│       ├── 📂 stores/            # Pinia State Management
│       └── 📂 router/            # Route Definitions
├── 📂 backend/                   # Spring Boot Backend Application
│   └── src/main/java/com/smcompany/backend/
│       ├── 📂 controller/        # REST API Controllers
│       ├── 📂 service/           # Business Logic
│       ├── 📂 entity/            # JPA Entities
│       ├── 📂 repository/        # Data Access Layer
│       └── 📂 dto/               # Data Transfer Objects
├── 🐳 docker-compose.yml         # Container Orchestration Config
└── ⚙️ nginx.conf                 # Nginx Reverse Proxy Config

```

---

## 🚀 Getting Started

### 1. Production Mode (Docker)

Oracle Cloud 또는 로컬 Docker 환경에서 전체 서비스를 실행합니다.

```bash
# 최신 이미지 Pull (GitHub Container Registry 연동 시)
docker-compose pull

# 컨테이너 실행 (Background Mode)
docker-compose up -d

```

* **Frontend Access**: `http://localhost` (or Server IP)
* **Backend API**: `http://localhost/api`

### 2. Development Mode

**Backend (Spring Boot)**

```bash
cd backend
./gradlew bootRun

```

**Frontend (Vue 3)**

```bash
cd frontend
npm install
npm run dev

```

---

## 🔌 API Endpoints

### 📝 Q&A Board

| Method | URI | Description |
| --- | --- | --- |
| `GET` | `/api/qna` | 게시글 목록 조회 (Paging) |
| `GET` | `/api/qna/search` | 게시글 검색 |
| `GET` | `/api/qna/{id}` | 게시글 상세 조회 |
| `POST` | `/api/qna` | 게시글 작성 |
| `PUT` | `/api/qna/{id}` | 게시글 수정 (비밀번호 검증) |
| `DELETE` | `/api/qna/{id}` | 게시글 삭제 (비밀번호 검증) |

### 💬 Comments

| Method | URI | Description |
| --- | --- | --- |
| `GET` | `/api/qna/{postId}/comments` | 댓글 목록 조회 |
| `POST` | `/api/qna/{postId}/comments` | 댓글 작성 |

### 💾 Reference (자료실)

| Method | URI | Description |
| --- | --- | --- |
| `GET` | `/api/references` | 자료 목록 조회 |
| `GET` | `/api/references/search` | 자료 검색 |
| `POST` | `/api/references` | 자료 업로드 (Multipart, Admin Only) |
| `GET` | `/api/references/{id}/download` | 전체 파일 다운로드 |
| `GET` | `/api/references/files/{fileId}/download` | 개별 첨부파일 다운로드 |

### 🔐 Admin

| Method | URI | Description |
| --- | --- | --- |
| `POST` | `/api/admin/login` | 관리자 로그인 (Session) |

---

© 2026 **SM Industrial Safety**. All Rights Reserved.

