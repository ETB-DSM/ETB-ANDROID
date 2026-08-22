# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Restrictions

- `.env` 파일은 절대 읽거나 수정하지 않는다. 환경변수 구조가 필요하면 `.env.example`을 참조한다.

## Project Reference Documents

현재 컨텍스트에 프로젝트 요구사항이나 기술 스택 정보가 없으면 아래 문서를 먼저 읽는다.

- `PRD.md` — 기능 요구사항, 사용자 흐름, API 목록
- `TRD.md` — 기술 스택, 아키텍처, DB 스키마, 인증 설계, WebSocket 설계

문서가 존재하지 않으면 사용자에게 요청한다.

---

## Work Flow

모든 작업은 아래 3단계를 순서대로 따른다.

### Step 1 — 설계

사용자 요구사항을 수신하면 곧바로 코드를 작성하지 않는다.

1. 요구사항을 분석해 작업 방식을 도출한다.
   - 어떤 파일을 생성·수정할지
   - 함수·메서드 시그니처
   - 데이터 흐름 및 레이어 간 책임 분리
   - API 요청/응답 포맷 변경이 있으면 명시
   - DB 스키마 변경이 있으면 명시
2. 도출한 내용을 `docs/YYYY.MM.DD_<description>.md` 에 작성한다.
   - 날짜는 작업 당일 기준
   - description은 영문 kebab-case (예: `2026.08.18_auth-signup-flow.md`)
3. 설계 문서를 사용자에게 공유하고 피드백을 기다린다.

### Step 2 — 피드백

사용자의 피드백을 반영해 설계 문서를 수정한다.  
사용자가 **"진행해줘"** 라고 하면 Step 3으로 넘어간다.

### Step 3 — 개발 및 검증

#### 브랜치 생성
`develop` 브랜치에서 새 브랜치를 만들어 작업한다.

```
feat/<description>    # 신규 기능
fix/<description>     # 버그 수정
chore/<description>   # 설정·문서·도구 변경
hotfix/<description>  # main에서 직접 분기하는 긴급 수정
```

`hotfix` 브랜치는 main의 프로덕션 버그를 즉시 수정해야 할 때 사용한다.  
`main`에서 분기하고, 수정 완료 후 `main`과 `develop` 양쪽에 merge한다.

```bash
git checkout main
git checkout -b hotfix/<description>
# 수정 후
git checkout main && git merge --no-ff hotfix/<description>
git checkout develop && git merge --no-ff hotfix/<description>
git branch -d hotfix/<description>
```

#### 커밋 메시지 컨벤션

```
<type>: <subject>
```

| type | 사용 시점 |
|------|----------|
| `feat` | 신규 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 동작 변경 없는 코드 구조 개선 |
| `chore` | 빌드 설정, 의존성, 도구, 문서 변경 |
| `test` | 테스트 추가·수정 |
| `style` | 포맷·공백 등 코드 의미에 영향 없는 변경 |

규칙:
- subject는 한국어 또는 영어 모두 허용, 명령형으로 작성 (예: `feat: 로그인 화면 추가`)
- 제목은 50자 이내
- 본문이 필요하면 제목과 한 줄 띄우고 작성

#### 개발
설계 문서(docs/)를 기준으로 코드를 작성한다.

#### 검증 (sub-agent)
코드 작성이 완료되면 **새 세션의 Claude Sonnet sub-agent**를 실행해 검증한다.

sub-agent에게 전달하는 내용:
- 해당 작업의 설계 문서 전문
- 작성된 코드 전문
- 기존 코드베이스 중 변경된 파일과 연관된 파일

sub-agent가 검증하는 항목:
- 구현이 설계 문서와 일치하는가
- 기존 코드베이스와 정합성이 맞는가 (레이어 의존 방향, 에러 포맷, 네이밍 컨벤션 등)

**통과 기준: 설계 문서 대비 지적 사항이 0개여야 한다.**  
지적 사항이 1개라도 있으면 실패로 간주한다.

검증 결과 불일치가 있으면:
1. Claude가 코드를 수정한다.
2. sub-agent를 다시 실행해 재검증한다.
3. 지적 사항 0개가 될 때까지 반복한다.

#### PR 생성
검증 통과 후 작업 브랜치를 `develop`으로 향하는 PR을 생성한다. merge는 사용자가 직접 한다.

```bash
gh pr create --base develop --head <브랜치명> --title "<type>: <설명>" --body "..."
```

PR body에는 다음을 포함한다:
- 작업 내용 요약
- 관련 설계 문서 경로 (docs/)
- 테스트 방법
