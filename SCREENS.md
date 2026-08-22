# SCREENS.md

This document describes every screen in the AI-Cane Android app for a design agent.
Apply the design system defined in `DESIGN.md` to all screens.

---

## Service Context

AI-Cane is a smart cane companion app for visually impaired users.
The app is operated primarily by the user (visually impaired) or their guardian to:
- Register an account and pair a smart cane device (Orange Pi)
- Register destinations with OCR target text for the cane to identify
- Start walking navigation — the app calculates direction actions and sends them to the cane, which gives vibration feedback

**Design priority**: High contrast, large touch targets, minimal clutter.
The app is used while walking outdoors.

---

## Screen List

### 1. Splash / Onboarding Entry

**Purpose**: App entry point. Route to Login if token exists, else to Signup.

**Layout**:
- Full-screen black band (`hero-band-dark`)
- Center: App name "AI-Cane" in `display-xl` white
- Below: Tagline in `body-lg` white, muted

**No interactive elements.** Auto-navigates after 1.5s.

---

### 2. Signup Screen

**Purpose**: Create a new account with email and password.

**Layout** (scrollable, white background):
- Top: Back arrow icon (`icon-button-circular`)
- Headline: "회원가입" in `display-md`
- Form card (`ex-auth-form-card`):
  - Email input (`text-input`) — label: "이메일"
  - Password input (`text-input`) — label: "비밀번호"
  - Password confirm input (`text-input`) — label: "비밀번호 확인"
  - Name input (`text-input`) — label: "이름"
- Primary CTA: "다음" (`button-primary`, full-width pill)
- Divider line with "또는" text in `body-sm` muted
- Google login button (`button-secondary`, full-width pill, Google logo icon left)
- Bottom link: "이미 계정이 있으신가요? 로그인" (`link-blue`)

**States**:
- Input error: red border + `caption` error message below field
- Loading: CTA button disabled, spinner inside pill

---

### 3. Email Verification Screen

**Purpose**: Enter the 6-digit code sent to the registered email.

**Layout** (white background):
- Top: Back arrow icon
- Headline: "이메일 인증" in `display-md`
- Sub-text: "이메일로 전송된 6자리 코드를 입력해주세요." in `body-md` body color
- Large 6-box OTP input row (each box: `text-input` style, square, `display-sm` center-aligned)
- Primary CTA: "인증하기" (`button-primary`, full-width pill)
- Below CTA: "코드 재전송" (`link-blue`, centered)

**States**:
- Wrong code: All boxes red border + error toast (`ex-toast`)
- Verified: Brief success state → auto-navigate to Login

---

### 4. Login Screen

**Purpose**: Sign in with email/password or Google.

**Layout** (white background):
- Center logo: "AI-Cane" in `display-lg`
- Form card (`ex-auth-form-card`):
  - Email input
  - Password input
- Primary CTA: "로그인" (`button-primary`, full-width pill)
- Divider "또는"
- Google login (`button-secondary`, full-width pill)
- Bottom link: "계정이 없으신가요? 회원가입" (`link-blue`)

---

### 5. Device Registration Screen

**Purpose**: Pair the AI-Cane Orange Pi device by entering its device ID.

**Layout** (white background):
- Progress indicator: Step 1 of 2 (two dots, active dot black)
- Headline: "디바이스 등록" in `display-md`
- Sub-text: "AI-Cane 지팡이의 Device ID를 입력해주세요." in `body-md`
- Device ID input (`text-input`, full-width, placeholder: "예: aicane-0001")
- Primary CTA: "등록하기" (`button-primary`, full-width pill)
- If devices already registered: show list as `card-soft-tinted` cards with device name + "선택" pill button per row

---

### 6. Guardian Registration Screen

**Purpose**: Register an emergency contact (guardian).

**Layout** (white background):
- Progress indicator: Step 2 of 2
- Headline: "보호자 등록" in `display-md`
- Sub-text: "낙상 감지 시 SOS를 전송할 보호자 정보를 입력해주세요." in `body-md`
- Form card (`ex-auth-form-card`):
  - Name input (`text-input`) — label: "보호자 이름"
  - Phone input (`text-input`, type phone) — label: "전화번호"
- Primary CTA: "등록하기" (`button-primary`, full-width pill)
- Secondary: "나중에 등록하기" (`button-subtle`, full-width pill)

---

### 7. Destination List Screen

**Purpose**: Main home screen. View, add, and select destinations to navigate.

**Layout** (white background):
- Top bar: "목적지" in `display-sm` + right-side "+" icon button (`icon-button-circular`)
- Empty state (`ex-empty-state-card`, centered):
  - Icon (location pin)
  - "등록된 목적지가 없습니다" in `body-md`
  - "목적지 추가하기" pill (`button-primary`)
- List state: each destination as `card-elevated`:
  - Title in `body-md-strong`
  - Sub-text: targetText in `body-sm` body color
  - Right: "길안내 시작" (`category-button` pill)
  - Swipe-left: Delete action (black band, white trash icon)

---

### 8. Destination Search Screen

**Purpose**: Search for a destination by name and select from results. Entry point for destination registration.

**Layout** (white background):
- Top bar: Back arrow (48dp circle) + "목적지 검색" in `display-sm`
- Search input row (`rounded.pill`, 64dp height, `canvas-soft` background):
  - Left: circle search icon (18dp, gray border)
  - Input: `body-lg`, placeholder "장소 이름을 입력하세요"
  - Right (when text entered): ✕ clear button (48dp circle, white)
- Results list (below search, appears as user types):
  - Each result: `card-elevated` row with place name + address sub-text
  - Tap a result → navigate to Destination Registration Screen

**States**:
- Empty query: hint text "자주 찾는 목적지를 검색해보세요" in `body-md` muted
- No results: `ex-empty-state-card` with "검색 결과가 없습니다"

---

### 9. Destination Registration Screen

**Purpose**: Confirm selected place and add OCR target text + radius before saving.

**Layout** (scrollable, white background):
- Top: Back arrow + selected place name in `display-sm`
- Selected place card (`card-elevated`):
  - Place name in `body-md-strong`
  - Address in `body-sm` muted
- Form:
  - OCR Target input (`text-input`, 64dp) — label: "인식할 텍스트" (예: 103동)
  - Sub-caption: "지팡이 카메라가 이 글자를 보면 도착으로 판단합니다"
  - Radius input (`text-input`, numeric) — label: "도착 인식 반경 (미터)", default: 30
- Primary CTA: "저장하기" (`button-primary`, full-width pill)

---

### 10. Mypage Screen

**Purpose**: View profile, manage registered devices and guardians.

**Layout**:
- **Top band** — black (`hero-band-dark`), fixed height:
  - Back arrow (48dp circle, `black-elevated` #282828 background, white icon)
  - "마이페이지" label in `body-sm` muted white
  - Avatar circle (60dp, white border 3px) showing name initial + name + email
  - Device chip + Guardian chip (`category-button` style, `black-elevated` background)
- **Scrollable body** — white:
  - Section "등록된 지팡이": list rows with connection dot (black=connected, gray=disconnected) + device ID + "해제" button (red text)
  - Empty state row: "등록된 지팡이가 없습니다" + "등록" black pill button
  - "추가" button (white pill with black border) in section header
  - Section "보호자": same row pattern with guardian name + phone + "삭제" button
  - Logout button at bottom: `button-subtle`, full-width pill, red text

---

### 11. Navigation Screen (Active Guidance)

**Purpose**: Real-time navigation screen shown while walking guidance is active.

**Layout** (full-screen, split into two bands):

**Top band** — black (`hero-band-dark`), ~40% height:
- Current action displayed in `display-xl` white, center-aligned
  - e.g., "직진", "좌회전 준비", "좌회전", "도착"
- Below: remaining distance in `body-lg` muted white
  - e.g., "약 120m"

**Bottom band** — white (`hero-band-light`), ~60% height:
- Destination name in `display-sm` ink
- Sub-text: "길안내 중" in `body-sm` body color
- Device status row (`card-soft-tinted`, compact):
  - Battery icon + percentage
  - Connection status dot (black = connected, gray = disconnected)
- "길안내 종료" button (`button-secondary`, full-width pill, red text override for danger action)

**Action → visual mapping**:

| action | Top band text | Top band background |
|--------|--------------|-------------------|
| straight | 직진 | `colors.ink` (#000) |
| prepare_left | 좌회전 준비 | `colors.ink` |
| left | 좌회전 | `colors.ink` |
| prepare_right | 우회전 준비 | `colors.ink` |
| right | 우회전 | `colors.ink` |
| arrived | 도착 | `colors.ink` |
| reroute | 경로 재탐색 중 | `colors.black-elevated` (#282828) |

**No map shown.** The screen is intentionally minimal — the cane handles all physical feedback.

---

### 12. Navigation End / Arrival Screen

**Purpose**: Confirm session end after arrived or canceled.

**Layout** (white background, centered):
- Large checkmark icon (black circle, `rounded.full`)
- Headline: "도착했습니다!" or "길안내를 종료했습니다" in `display-md`
- Destination name in `body-lg` body color
- Primary CTA: "홈으로" (`button-primary`, full-width pill)

---

## Navigation Flow

```
Splash
  ├─ (token valid) → Destination List
  └─ (no token)
        ├─ Signup → Email Verification → Login
        └─ Login (direct)
              └─ (first login) → Device Registration → Guardian Registration
                    └─ Destination List
                          ├─ [아바타] → Mypage → Destination List
                          ├─ [+] → Search → Destination Registration → Destination List
                          └─ [길안내 시작] → Navigation Screen
                                └─ [종료 / 도착] → Navigation End → Destination List
```

---

## Design Constraints

- All primary CTAs: full-width `button-primary` pill (`rounded.pill`, black background)
- All secondary actions: `button-secondary` or `button-subtle` pill
- Touch targets: minimum 48dp height on all interactive elements
- Font substitute for Android: **Pretendard** (700 bold / 500 medium / 400 regular)
- No accent colors beyond black/white/gray palette
- Navigation Screen top band must remain high-contrast black at all times
- Error states: red (`#E53E3E`) border on inputs + `caption` text below. No red fills.
