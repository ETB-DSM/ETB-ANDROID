# PRD.md

## 프로젝트 개요

AI-Cane은 시각장애인의 독립 보행을 지원하는 Orange Pi 5 Plus 기반 스마트 지팡이 시스템이다.
Android 앱은 인증, 디바이스·보호자·목적지 관리, 길안내 action 계산 및 서버 전송을 담당한다.

Orange Pi는 센서(LiDAR, GPS, 카메라, MPU6050)와 진동 모터를 직접 제어하며,
앱이 서버를 통해 전달하는 navigation action을 기반으로 방향 피드백을 제공한다.

## 시스템 내 앱의 역할

```
Android App → REST API Server ← Orange Pi 5 Plus
```

- 앱: 인증(JWT), 디바이스·보호자·목적지 관리, GPS 기반 경로 계산, action 서버 전송
- 서버: action 중계 및 상태 저장
- Orange Pi: 서버에서 action 조회 → 진동 피드백 실행

### GPS 역할 분리

| 주체 | GPS 사용 목적 |
|------|--------------|
| Android 앱 | FusedLocationProviderClient로 직접 읽어 navigation action 계산 |
| Orange Pi | 독립 GPS 모듈 — OCR 활성화 트리거(목적지 반경 진입 판단), SOS 위치 전송 |

앱과 Orange Pi는 GPS를 공유하지 않는다. 각자 독립적으로 읽는다.

## 사용자 흐름

```
앱 최초 실행
  └─ 회원가입 → 이메일 인증 → 로그인 (이메일 또는 구글)
        └─ 디바이스 등록 (deviceId 입력)
              └─ 보호자 등록 (이름, 전화번호)
                    └─ 목적지 화면
                          ├─ 목적지 등록 (이름, OCR targetText, 좌표, 반경)
                          └─ 목적지 선택
                                └─ 길안내 시작 → 세션 생성
                                      ├─ [반복] FusedLocation 수신 → 경로 계산 → action 계산 → 서버 전송
                                      ├─ [이탈] 경로 이탈 감지 → reroute action 전송 → 새 경로 계산
                                      └─ [종료] 도착 / 취소 / 오류 → 세션 종료
```

## 기능 요구사항

### APP-001 인증 및 초기 설정 (⭐⭐⭐ 필수)

회원가입, 이메일 인증, 로그인, 디바이스 등록, 보호자 등록을 순서대로 진행한다.

#### 회원가입 / 이메일 인증 / 로그인

- 입력: 이메일, 비밀번호, 이름 (회원가입) / 인증 코드 (이메일 인증) / 이메일+비밀번호 또는 구글 계정 (로그인)
- 출력: accessToken, refreshToken
- 구현 기준
  - 이메일: signup → verify-email → login 순서 진행
  - 구글: Google Sign-In SDK로 idToken 수신 후 login/google 호출
  - JWT는 EncryptedSharedPreferences에 저장
- 완료 기준: accessToken 저장 완료 후 디바이스 등록 화면으로 이동
- 의존: POST /api/v1/auth/signup, /verify-email, /login, /login/google

#### 디바이스 등록

- 입력: deviceId (Orange Pi 식별자)
- 출력: 등록된 Device
- 구현 기준: POST /api/v1/devices 호출, 기등록 디바이스가 있으면 목록 조회 후 선택 가능
- 완료 기준: deviceId가 서버에 저장되고 앱 내에 유지되어야 한다
- 의존: POST /api/v1/devices, GET /api/v1/devices

#### 보호자 등록

- 입력: 보호자 이름, 전화번호
- 출력: 등록된 Guardian
- 구현 기준: POST /api/v1/guardians 호출
- 완료 기준: 보호자 정보가 서버에 저장된 후 목적지 화면으로 이동
- 의존: POST /api/v1/guardians

### APP-002 목적지 등록 및 선택 (⭐⭐⭐ 필수)

사용자가 목적지 이름, OCR targetText, 좌표, 반경을 등록하고 길안내 시작 목적지를 선택한다.

- 입력: name, targetText, latitude, longitude, radius
- 출력: selected destinationId
- 구현 기준: 목적지 생성/목록 조회 API 사용
- 완료 기준: 등록한 목적지를 목록에서 선택 가능
- 의존: POST /api/v1/destinations, GET /api/v1/destinations

### APP-003 보행 경로 생성 (⭐⭐⭐ 필수)

선택한 목적지까지의 보행 경로를 T-Map 보행자 경로 API로 생성하고 turn-by-turn step 목록을 확보한다.

- 입력: 현재 위치(lat/lng), 목적지 위치(lat/lng)
- 출력: route steps (각 step에 heading, distance, turnType 포함)
- 구현 기준: T-Map `/tmap/routes/pedestrian` API 호출
- 완료 기준: 직진/좌회전/우회전 판단 가능한 step 데이터 확보
- 의존: APP-002

### APP-004 길안내 action 계산 (⭐⭐⭐ 필수)

FusedLocationProviderClient로 수신한 현재 위치와 route step을 비교해 navigation action을 계산한다.

- 입력: 현재 위치(FusedLocation), route steps, destination radius
- 출력: action(enum), distanceMeters
- action 종류: `straight` / `prepare_left` / `left` / `prepare_right` / `right` / `arrived` / `reroute`
- 계산 기준
  - 다음 회전 지점까지 20m 이내: `prepare_left` / `prepare_right`
  - 다음 회전 지점까지 5m 이내: `left` / `right`
  - destination radius 이내: `arrived`
- 완료 기준: 위치 변화에 따라 action이 갱신되어야 한다
- 의존: APP-003

### APP-005 길안내 action 서버 전송 (⭐⭐⭐ 필수)

앱에서 계산한 현재 action을 서버에 저장해 Orange Pi가 조회할 수 있게 한다.

- 입력: sessionId, action, distanceMeters, message
- 출력: instruction 저장 결과
- 구현 기준: action 변경 시 즉시, 변경 없으면 30초마다 전송
- 완료 기준: 서버 조회 시 최신 action이 반환되어야 한다
- 의존: APP-004, POST /api/v1/navigation/sessions/:sessionId/instructions

### APP-006 경로 이탈 및 재탐색 (⭐⭐ 중요)

사용자가 경로에서 벗어난 경우 reroute action을 서버에 전송하고 새 경로를 계산한다.

- 입력: 현재 위치, route geometry
- 출력: reroute action, new route steps
- 구현 기준: 경로 이탈 거리 기준(30m) 초과 시 reroute 처리
- 완료 기준: 경로 이탈 시 reroute action이 서버에 전송되어야 한다
- 의존: APP-003, APP-005

### APP-007 길안내 세션 종료 (⭐⭐ 중요)

목적지 도착, 사용자 취소, 오류 발생 시 navigation session 상태를 변경한다.

- 입력: sessionId, status, reason
- 출력: session status 변경 결과
- 전송 상태: `arrived` / `canceled` / `error`
- 완료 기준: 세션 상태가 서버에서 종료 상태로 바뀌어야 한다
- 의존: PATCH /api/v1/navigation/sessions/:sessionId/status

## API 목록

앱이 호출하는 서버 API 목록이다.

| 엔드포인트 | 기능 | 호출 시점 |
|-----------|------|----------|
| POST /api/v1/auth/signup | 회원가입 | APP-001 |
| POST /api/v1/auth/verify-email | 이메일 인증 | APP-001 |
| POST /api/v1/auth/login | 이메일 로그인 | APP-001 |
| POST /api/v1/auth/login/google | 구글 로그인 | APP-001 |
| POST /api/v1/auth/refresh | 토큰 갱신 | 자동 (401 수신 시) |
| POST /api/v1/auth/logout | 로그아웃 | 사용자 요청 시 |
| POST /api/v1/devices | 디바이스 등록 | APP-001 |
| GET  /api/v1/devices | 디바이스 목록 | APP-001 |
| POST /api/v1/guardians | 보호자 등록 | APP-001 |
| GET  /api/v1/guardians | 보호자 목록 | APP-001 |
| POST /api/v1/destinations | 목적지 등록 | APP-002 |
| GET  /api/v1/destinations | 목적지 목록 | APP-002 |
| POST /api/v1/navigation/sessions | 길안내 세션 생성 | APP-002 목적지 선택 후 |
| POST /api/v1/navigation/sessions/:sessionId/instructions | 길안내 명령 저장 | APP-005 |
| PATCH /api/v1/navigation/sessions/:sessionId/status | 세션 상태 변경 | APP-007 |

외부 API:

| API | 용도 |
|-----|------|
| T-Map `/tmap/routes/pedestrian` | 보행자 경로 생성 (APP-003) |
