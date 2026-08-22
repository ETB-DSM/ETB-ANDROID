# TRD.md

## 기술 스택

| 분류 | 선택 |
|------|------|
| 언어 | Kotlin |
| 최소 SDK | API 26 (Android 8.0) |
| 빌드 | Gradle Kotlin DSL |
| UI | Jetpack Compose |
| 아키텍처 패턴 | MVVM + Clean Architecture |
| DI | Hilt |
| 비동기 | Coroutines + Flow |
| 네트워크 | Retrofit + OkHttp |
| 직렬화 | kotlinx.serialization |
| 위치 | FusedLocationProviderClient |
| 외부 지도 API | T-Map Mobility API |

## 아키텍처

### 레이어 구조

```
Presentation (Compose + ViewModel)
      ↓
Domain (UseCase + Repository Interface)
      ↓
Data (Repository Impl + Remote DataSource)
```

의존 방향은 항상 바깥 → 안쪽.
Domain은 Android 의존성 없는 순수 Kotlin.

### 패키지 구조

```
app/
  ├── presentation/
  │   ├── auth/          # 회원가입, 이메일 인증, 로그인
  │   ├── device/        # 디바이스 등록·목록
  │   ├── guardian/      # 보호자 등록·목록
  │   ├── destination/   # 목적지 등록·목록·선택
  │   └── navigation/    # 길안내 세션, action 계산·전송
  ├── domain/
  │   ├── model/
  │   ├── repository/
  │   └── usecase/
  └── data/
      ├── remote/
      │   ├── api/
      │   └── dto/
      └── repository/
```

### 로컬 저장소

DB 없음. EncryptedSharedPreferences만 사용.

| 저장 항목 | 용도 |
|-----------|------|
| accessToken | API 요청 Authorization 헤더 |
| refreshToken | 토큰 갱신 |
| userId | 서버 발급 UUID, 이후 요청에 재사용 |
| deviceId | 등록된 디바이스 ID |

## 인증 설계

### 인증 흐름

```
이메일:
  회원가입 (POST /api/v1/auth/signup)
    → 이메일 인증 (POST /api/v1/auth/verify-email)
    → 로그인 (POST /api/v1/auth/login)
    → accessToken + refreshToken 수신 → EncryptedSharedPreferences 저장

구글 OAuth:
  Google Sign-In SDK → idToken 수신
    → POST /api/v1/auth/login/google
    → accessToken + refreshToken 수신 → EncryptedSharedPreferences 저장
```

### 토큰 관리

- accessToken, refreshToken은 EncryptedSharedPreferences에 저장
- accessToken 만료(401) 시 자동으로 POST /api/v1/auth/refresh 호출 후 재시도
- refreshToken 만료 시 토큰 삭제 후 로그인 화면으로 이동

### OkHttp 구성

```
AuthInterceptor
  모든 App API 요청에 Authorization: Bearer {accessToken} 헤더 추가

TokenAuthenticator (OkHttp Authenticator)
  401 응답 수신 시 refreshToken으로 갱신 시도
  갱신 실패 시 로그인 화면으로 이동
```

## 길안내 엔진 설계

길안내 중 GPS 수신 → action 계산 → 서버 전송 루프는
Foreground Service 위에서 실행한다.

```
NavigationService (Foreground Service)
  ├── LocationCollector   — FusedLocationProviderClient
  ├── ActionCalculator    — 현재 위치 + route steps → action
  └── ActionUploader      — action 변경 시 즉시 / 30초 keep-alive 전송
```

### LocationCollector

- Priority: `PRIORITY_HIGH_ACCURACY`
- Interval: 2000ms / MinUpdateInterval: 1000ms
- 화면 꺼짐 상태에서도 Foreground Service로 수신 유지

### ActionCalculator

1. 현재 위치 → 각 step waypoint까지 Haversine 거리 계산
2. 가장 가까운 step을 현재 step으로 설정
3. 다음 회전 step까지의 거리로 action 결정

| 조건 | action |
|------|--------|
| 다음 회전까지 > 20m | `straight` |
| 다음 회전까지 ≤ 20m | `prepare_left` / `prepare_right` |
| 다음 회전까지 ≤ 5m | `left` / `right` |
| destination radius 이내 | `arrived` |
| 경로 이탈 거리 초과 (기준: 30m) | `reroute` |

경로 이탈 판단: 현재 위치에서 route geometry 전체 선분 중
가장 가까운 점까지의 거리가 30m 초과 시 이탈로 간주

### ActionUploader

- action이 이전과 다를 때 즉시 전송
- action이 같더라도 30초마다 전송 (keep-alive)
- 전송 실패 시 로그 기록 후 다음 주기 재시도 (세션 유지)

## 데이터 모델

### Domain Model

```kotlin
data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)

data class Device(
    val deviceId: String,
    val name: String
)

data class Guardian(
    val guardianId: String,
    val name: String,
    val phone: String
)

data class Destination(
    val destinationId: String,
    val name: String,
    val targetText: String,   // Orange Pi OCR 비교 기준 문자열
    val latitude: Double,
    val longitude: Double,
    val radius: Double        // 단위: 미터
)

data class RouteStep(
    val heading: Double,         // 진행 방위각 (0~360)
    val distanceMeters: Double,
    val turnType: TurnType,      // STRAIGHT / LEFT / RIGHT
    val description: String
)

enum class NavigationAction {
    STRAIGHT, PREPARE_LEFT, LEFT,
    PREPARE_RIGHT, RIGHT, ARRIVED, REROUTE
}

data class NavigationInstruction(
    val sessionId: String,
    val action: NavigationAction,
    val distanceMeters: Double,
    val message: String
)
```

## 네트워크 설계

### 공통 설정

- BaseURL: `BuildConfig.BASE_URL`
- Timeout: connect 10s / read 15s / write 15s
- App API 공통 헤더: `Authorization: Bearer {accessToken}`

### 에러 처리

| 상황 | 처리 |
|------|------|
| 401 | TokenAuthenticator가 토큰 갱신 후 재시도 |
| 갱신 실패 | 토큰 삭제 후 로그인 화면으로 이동 |
| 네트워크 오류 / 5xx | 1회 retry 후 실패, UI 에러 상태 전달 |
| 길안내 중 전송 실패 | 로그 기록 후 다음 주기 재시도 (세션 유지) |

### App API 명세 (JWT 필수, /api/v1/...)

#### 인증
```
POST /api/v1/auth/signup
POST /api/v1/auth/verify-email
POST /api/v1/auth/login
POST /api/v1/auth/login/google    # Google Sign-In SDK idToken 전달
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
```

#### 디바이스
```
POST   /api/v1/devices
GET    /api/v1/devices
DELETE /api/v1/devices/:deviceId
```

#### 보호자
```
POST   /api/v1/guardians
GET    /api/v1/guardians
DELETE /api/v1/guardians/:guardianId
```

#### 목적지
```
POST   /api/v1/destinations
GET    /api/v1/destinations
DELETE /api/v1/destinations/:destinationId
```

#### 길안내
```
POST  /api/v1/navigation/sessions
POST  /api/v1/navigation/sessions/:sessionId/instructions
PATCH /api/v1/navigation/sessions/:sessionId/status
```

### 외부 API

#### T-Map 보행자 경로
```
POST https://apis.openapi.sk.com/tmap/routes/pedestrian
Header: appKey: {TMAP_API_KEY}
Body: {
  startX, startY, endX, endY,
  reqCoordType: "WGS84GEO",
  resCoordType: "WGS84GEO",
  startName, endName
}
Response: GeoJSON FeatureCollection
          (각 Feature에 turnType, distance, description 포함)
```
