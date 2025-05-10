# 내일의 그대에게

![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-green) ![MySQL](https://img.shields.io/badge/MySQL-8.0-orange) ![AWS S3](https://img.shields.io/badge/AWS%20S3-SDK_v2-yellow)

---

## 📖 목차

1. 서비스 소개
2. 주요 기능

   * 💬 추억 회상 AI 챗봇
   * 📸 추억 포토부스
3. 기술 스택
4. API 사용 예시
5. 데이터베이스 스키마
6. 향후 개선 사항

---

## 📝 서비스 소개

**내일의 그대에게**는 사랑하는 이를 기억하고 추모하기 위해 만들어진 맞춤형 장례 플랫폼입니다.

* **기록 기능**: 사진, 음악, 책, 영화 등의 추억을 업로드하고 공유할 수 있습니다.
* **댓글 기능**: 사진마다 댓글을 남기며 함께 추억을 나눌 수 있습니다.
* **추억 회상 AI 챗봇**: 사용자가 남긴 영상과 텍스트를 학습하여, 고인의 말투로 대화할 수 있는 챗봇을 제공합니다.

---

## 🚀 주요 기능

### 💬 추억 회상 AI 챗봇

* **엔드포인트**: `POST /chat/{userId}`
* **절차**:

  1. 사용자가 질문 전송
  2. 이전 대화 히스토리 및 업로드된 영상·텍스트 기반 프롬프트 생성
  3. OpenAI GPT 모델 호출 (예: `gpt-4-turbo`)
  4. 응답을 `ChatbotHistory` 테이블에 누적 저장

### 📸 추억 포토부스

* **엔드포인트**: `POST /showing`
* **절차**:

  1. 사진 및 메타데이터(`title`, `category`, `content`, `date`) 수신
  2. 임시 파일 생성 및 AWS S3 업로드
  3. 업로드된 이미지의 퍼블릭 URL 생성
  4. `PhotoBooth` 엔티티에 URL 및 메타데이터 저장
  5. 사진별 댓글 기능 지원

### 🎵 취향 저장소

* **엔드포인트**: `POST /playlist`
* **절차**:
  1. 클라이언트로부터 플레이리스트 정보(`title`, `description`, `representative`) 수신
  2. `representative`가 true인 경우, 기존 대표 플레이리스트를 찾아 false로 갱신
  3. 새로운 `Playlist` 엔티티 생성 및 DB에 저장
  4. 대표 여부는 항상 1개만 유지되도록 보장
  5. 해당 플레이리스트에 노래(`Song`) 추가 가능

---

## 🧰 기술 스택

| 분야    | 기술                                 |
|-------|------------------------------------|
| 백엔드   | Spring Boot, AWS EC2/S3/RDS, MySQL |
| 프론트엔드 | React JS                           |
