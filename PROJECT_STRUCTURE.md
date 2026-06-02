# FocusSound 프로젝트 구조 설명

## 프로젝트 한줄 설명

Android Kotlin 오디오 생성 앱입니다. 사용자의 문장 프롬프트를 규칙 기반으로 해석해 집중용 소리, 노이즈, 악기 샘플, 음악 패턴을 합성하고 재생합니다.

## 기본 작동 흐름

- MainActivity와 화면 계층이 프롬프트, 재생 상태, 설정값을 받습니다.
- ai 패키지가 자연어 프롬프트를 SoundIntent로 바꾸고, audio 패키지가 그 의도를 실제 오디오 신호로 합성합니다.
- assets의 악기 샘플과 Room schema가 재생 재료 및 앱 데이터 구조를 보조합니다.

## 문서 기준

- 아래 목록은 `git ls-files`로 확인되는 Git 추적 파일을 기준으로 작성했습니다.
- `.git`, `node_modules`, `build`, `.gradle`, 임시 업로드/출력물처럼 Git이 관리하지 않는 폴더는 제외했습니다.
- 폴더 표는 코드와 자산이 어떤 책임으로 나뉘는지, 파일 표는 각 파일이 실제로 무엇을 담당하는지 설명합니다.

## 폴더별 설명 (62개)

| 폴더 | 설명 |
| --- | --- |
| `.` | 프로젝트 루트입니다. 실행/빌드 설정, README, 전체 구조 문서, 최상위 진입 파일이 모여 있습니다. |
| `app` | Android 앱 모듈입니다. 앱 전용 빌드 설정, 소스 코드, 리소스, ProGuard 설정이 이 아래에 있습니다. |
| `app/schemas` | Room 데이터베이스 스키마 JSON을 버전별로 저장합니다. 마이그레이션 검증과 DB 구조 추적에 사용됩니다. |
| `app/schemas/com.focussound.database.FocusSoundDatabase` | Room 데이터베이스 스키마 JSON을 버전별로 저장합니다. 마이그레이션 검증과 DB 구조 추적에 사용됩니다. |
| `app/src` | Android 소스 세트가 들어 있는 상위 폴더입니다. main, test 같은 빌드 대상별 파일을 구분합니다. |
| `app/src/main` | 실제 앱에 포함되는 AndroidManifest, Kotlin/Java 소스, 리소스, 에셋을 담는 기본 소스 세트입니다. |
| `app/src/main/assets` | APK 안에 원본 그대로 포함되는 파일 자산 폴더입니다. 모델, 샘플 오디오, 라이선스, 웹 브리지 파일 등이 이곳에 들어갑니다. |
| `app/src/main/assets/instruments` | FocusSound에서 샘플러가 읽는 악기 음원과 관련 라이선스를 보관하는 폴더입니다. |
| `app/src/main/assets/instruments/uiowa` | FocusSound에서 샘플러가 읽는 악기 음원과 관련 라이선스를 보관하는 폴더입니다. |
| `app/src/main/assets/instruments/vsco2ce` | FocusSound에서 샘플러가 읽는 악기 음원과 관련 라이선스를 보관하는 폴더입니다. |
| `app/src/main/java` | 앱의 Kotlin/Java 패키지 루트입니다. 패키지명에 맞춰 실제 클래스 파일이 하위 폴더에 배치됩니다. |
| `app/src/main/java/com` | Kotlin 패키지 네임스페이스의 `com` 단계입니다. 실제 앱 패키지는 이 아래 `findmine`, `focussound`, `ownlifeos` 같은 이름으로 이어집니다. |
| `app/src/main/java/com/focussound` | FocusSound 앱의 최상위 Kotlin 패키지입니다. 화면 진입점과 주요 기능 패키지가 이 아래에서 갈라집니다. |
| `app/src/main/java/com/focussound/ai` | 프롬프트 해석, 키워드 사전, 의도 모델처럼 AI/규칙 기반 해석 로직을 담습니다. |
| `app/src/main/java/com/focussound/audio` | 오디오 합성, 필터, 노이즈, 샘플 재생, 스테레오 처리 등 실제 소리 생성 로직을 담습니다. |
| `app/src/main/java/com/focussound/audio/instrument` | 악기 샘플 라이브러리, 프리셋, 다운로드/관리 로직을 담습니다. |
| `app/src/main/java/com/focussound/audio/playback` | 음표 스케줄링, 재생 시계, 라이브 재생 엔진처럼 시간 기반 재생 흐름을 담당합니다. |
| `app/src/main/java/com/focussound/audio/sampler` | 오디오 샘플을 디코딩하고 캐싱해 재생 엔진에 공급하는 코드가 들어 있습니다. |
| `app/src/main/java/com/focussound/audio/soundfont` | 사운드폰트나 악기 음색 데이터를 재생 엔진에서 쓸 수 있게 다루는 코드를 담습니다. |
| `app/src/main/java/com/focussound/audio/synth` | 파형 합성기, 발진기, 합성 음색 생성 로직을 담습니다. |
| `app/src/main/java/com/focussound/audio/texture` | 비, 패드, 질감음처럼 배경 사운드 텍스처를 만드는 오디오 로직을 담습니다. |
| `app/src/main/java/com/focussound/audio/tone` | 음색, 톤, 주파수 성향을 만드는 오디오 처리 로직을 담습니다. |
| `app/src/main/java/com/focussound/composition` | 음악 조합, 섹션 구성, 생성된 소리를 곡 형태로 묶는 로직을 담습니다. |
| `app/src/main/java/com/focussound/condition` | 집중 상황, 사용자 조건, 추천 조건을 모델링하거나 판정하는 코드를 담습니다. |
| `app/src/main/java/com/focussound/data` | 앱 데이터 계층입니다. 로컬 DB, DAO, Entity, Repository처럼 저장소와 데이터 변환 코드를 담당합니다. |
| `app/src/main/java/com/focussound/database` | 앱 데이터베이스 정의와 DB 접근 보조 코드를 담는 폴더입니다. |
| `app/src/main/java/com/focussound/instrument` | 악기 샘플 라이브러리, 프리셋, 다운로드/관리 로직을 담습니다. |
| `app/src/main/java/com/focussound/music` | 멜로디, 화성, 편곡, 실시간 생성처럼 음악 생성 전반의 도메인 로직을 담습니다. |
| `app/src/main/java/com/focussound/music/arrangement` | 악기 배치, 파트 구성, 편곡 흐름을 계산하는 코드를 담습니다. |
| `app/src/main/java/com/focussound/music/director` | 음악 생성의 전체 방향과 장면 전환을 조율하는 디렉터 로직을 담습니다. |
| `app/src/main/java/com/focussound/music/form` | 인트로, 반복, 전개 같은 음악 형식과 섹션 구조를 다룹니다. |
| `app/src/main/java/com/focussound/music/generation` | 실제 멜로디/리듬/패턴을 생성하는 알고리즘을 담습니다. |
| `app/src/main/java/com/focussound/music/harmony` | 코드 진행, 조성, 화성 규칙을 계산하는 로직을 담습니다. |
| `app/src/main/java/com/focussound/music/knowledge` | 음악 생성에 필요한 규칙, 사전, 기준 데이터를 코드로 정리합니다. |
| `app/src/main/java/com/focussound/music/learning` | 사용자 피드백이나 사용 기록을 학습/개인화에 반영하는 로직을 담습니다. |
| `app/src/main/java/com/focussound/music/melody` | 멜로디 라인 생성과 음정 선택 로직을 담습니다. |
| `app/src/main/java/com/focussound/music/model` | 앱 내부에서 주고받는 도메인 모델과 값 객체를 정의하는 폴더입니다. |
| `app/src/main/java/com/focussound/music/realtime` | 재생 중 실시간으로 음악 상태를 갱신하거나 반응시키는 로직을 담습니다. |
| `app/src/main/java/com/focussound/personalization` | 사용자 취향, 사용 기록, 선호도 기반 개인화 로직을 담습니다. |
| `app/src/main/java/com/focussound/playback` | 음표 스케줄링, 재생 시계, 라이브 재생 엔진처럼 시간 기반 재생 흐름을 담당합니다. |
| `app/src/main/java/com/focussound/recommendation` | 상황과 취향에 맞는 소리/음악 추천 결과를 계산하는 코드를 담습니다. |
| `app/src/main/java/com/focussound/repository` | Repository 계층입니다. DAO와 도메인/화면 사이를 연결하고 데이터 접근 규칙을 한 곳에 모읍니다. |
| `app/src/main/java/com/focussound/service` | 백그라운드 실행, Android 서비스, 앱 외부 연동 흐름을 담당하는 코드를 담습니다. |
| `app/src/main/java/com/focussound/sounddesign` | 소리의 질감, 공간감, 악기 조합 같은 사운드 디자인 규칙을 담습니다. |
| `app/src/main/java/com/focussound/timer` | 집중 타이머, 세션 시간, 남은 시간 계산과 관련된 코드를 담습니다. |
| `app/src/main/java/com/focussound/ui` | 화면, ViewModel, UI 상태처럼 사용자 인터페이스와 직접 연결되는 Kotlin 파일을 담습니다. |
| `app/src/main/java/com/focussound/ui/ai` | 프롬프트 해석, 키워드 사전, 의도 모델처럼 AI/규칙 기반 해석 로직을 담습니다. |
| `app/src/main/java/com/focussound/ui/composer` | 사용자가 만들 소리/음악 조건을 입력하고 편집하는 작성 화면을 담당합니다. |
| `app/src/main/java/com/focussound/ui/condition` | 집중 조건, 상황 조건, 입력 조건을 선택하거나 표시하는 UI 코드를 담습니다. |
| `app/src/main/java/com/focussound/ui/home` | 홈 화면 구성과 홈 화면 상태 표시 컴포넌트를 담습니다. |
| `app/src/main/java/com/focussound/ui/instrument` | 악기 샘플 라이브러리, 프리셋, 다운로드/관리 로직을 담습니다. |
| `app/src/main/java/com/focussound/ui/player` | 재생 화면과 재생 상태 표시 UI를 담당합니다. |
| `app/src/main/java/com/focussound/ui/profile` | 사용자 프로필, 취향, 개인 설정 화면을 담당합니다. |
| `app/src/main/java/com/focussound/ui/report` | 분석 결과와 사용 리포트를 보여주는 화면 코드를 담습니다. |
| `app/src/main/java/com/focussound/ui/setup` | 초기 설정, 온보딩, 앱 사용 준비 화면을 담당합니다. |
| `app/src/main/java/com/focussound/ui/sounddesign` | 사운드 디자인 파라미터를 표시하고 조정하는 UI 코드를 담습니다. |
| `app/src/main/java/com/focussound/ui/theme` | Compose 화면에서 공통으로 쓰는 색상, 타이포그래피, 테마 설정을 담습니다. |
| `app/src/main/res` | Android XML 리소스 루트입니다. 문자열, 색상, 스타일, 아이콘, XML 설정처럼 코드가 참조하는 리소스를 보관합니다. |
| `app/src/main/res/drawable` | Android 벡터/드로어블 이미지 리소스 폴더입니다. 아이콘이나 그래픽 XML을 보관합니다. |
| `app/src/main/res/values` | 문자열, 색상, 테마, 스타일 등 앱 전역 XML 값을 정의하는 리소스 폴더입니다. |
| `gradle` | Gradle Wrapper와 데몬 설정처럼 Android/Kotlin 빌드 도구가 사용하는 파일을 보관합니다. |
| `gradle/wrapper` | 개발 PC에 Gradle이 없어도 동일한 버전으로 빌드할 수 있게 하는 Wrapper 실행 파일과 속성 파일을 보관합니다. |

## 파일별 설명 (283개)

| 파일 | 설명 |
| --- | --- |
| `.gitignore` | Git에 올리지 않을 빌드 산출물, 캐시, 개인 환경 파일을 지정하는 설정 파일입니다. 저장소에는 필요한 소스/자산만 남기도록 도와줍니다. |
| `app/build.gradle.kts` | Android 앱 모듈의 Gradle 빌드 설정입니다. SDK 버전, 의존성, Kotlin/Compose/Room 같은 모듈별 빌드 옵션을 지정합니다. |
| `app/schemas/com.focussound.database.FocusSoundDatabase/1.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.focussound.database.FocusSoundDatabase/2.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.focussound.database.FocusSoundDatabase/3.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.focussound.database.FocusSoundDatabase/4.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.focussound.database.FocusSoundDatabase/5.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/schemas/com.focussound.database.FocusSoundDatabase/6.json` | Room 데이터베이스 스키마 JSON입니다. 해당 버전의 테이블/컬럼 구조를 기록해 마이그레이션 검증에 사용합니다. |
| `app/src/main/AndroidManifest.xml` | Android 앱의 패키지 구성, Activity/Service, 권한, 파일 provider 같은 시스템 등록 정보를 선언합니다. |
| `app/src/main/assets/instruments/uiowa/LICENSE_UIOWA_MIS.txt` | 앱에 포함된 악기 샘플 라이브러리의 라이선스 고지 파일입니다. |
| `app/src/main/assets/instruments/uiowa/soft_flute_b3.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/uiowa/soft_piano_c3.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/uiowa/soft_piano_c4.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/uiowa/soft_piano_c5.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/uiowa/sub_bass_e1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/uiowa/warm_cello_d3.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/uiowa/warm_violin_g3.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/BKCtbss_SusNV_A1_v1_rr1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/BKCtbss_SusNV_C1_v1_rr1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/BKCtbss_SusNV_E1_v1_rr1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/LDFlute_susNV_A4_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/LDFlute_susNV_C4_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/LDFlute_susNV_C5_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/LDFlute_susNV_E4_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/LICENSE_VSCO2_CE.txt` | 앱에 포함된 악기 샘플 라이브러리의 라이선스 고지 파일입니다. |
| `app/src/main/assets/instruments/vsco2ce/susvib_A2_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/susvib_C3_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/susvib_E3_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/susvib_G3_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C2_mf_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C2_pp_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C3_mf_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C3_pp_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C4_mf_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C4_pp_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C5_mf_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_C5_pp_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_G2_mf_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_G2_pp_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_G3_mf_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_G3_pp_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_G4_mf_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/UR1_G4_pp_RR1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/ViolaEns_susvib_A3_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/ViolaEns_susvib_C4_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/ViolaEns_susvib_E4_v1_1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/VlnEns_susVib_C4_v1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/VlnEns_susVib_D5_v1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/VlnEns_susVib_E4_v1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/assets/instruments/vsco2ce/VlnEns_susVib_G4_v1.wav` | FocusSound 샘플러가 재생할 악기 오디오 샘플입니다. 파일명에는 악기, 음 높이, 세기 정보가 들어 있어 프리셋 매핑에 사용됩니다. |
| `app/src/main/java/com/focussound/ai/LocalCompositionPromptParser.kt` | FocusSound의 프롬프트 해석 계층에서 문장을 소리 생성 의도로 바꾸는 데 사용됩니다. LocalCompositionPromptParser Kotlin 소스입니다. 주 역할은 입력 텍스트나 프롬프트를 앱 내부 모델로 해석 입니다. |
| `app/src/main/java/com/focussound/ai/PromptKeywordDictionary.kt` | FocusSound의 프롬프트 해석 계층에서 문장을 소리 생성 의도로 바꾸는 데 사용됩니다. PromptKeywordDictionary Kotlin 소스입니다. 주 역할은 사용자 프롬프트의 단어를 소리 속성, 감정, 악기, 분위기 키워드로 매핑하는 사전 제공 입니다. |
| `app/src/main/java/com/focussound/ai/PromptParserFallback.kt` | FocusSound의 프롬프트 해석 계층에서 문장을 소리 생성 의도로 바꾸는 데 사용됩니다. PromptParserFallback Kotlin 소스입니다. 주 역할은 프롬프트 해석이 애매할 때 기본값과 안전한 대체 SoundIntent를 만드는 보정 처리 입니다. |
| `app/src/main/java/com/focussound/ai/RuleBasedSoundPromptParser.kt` | FocusSound의 프롬프트 해석 계층에서 문장을 소리 생성 의도로 바꾸는 데 사용됩니다. RuleBasedSoundPromptParser Kotlin 소스입니다. 주 역할은 입력 텍스트나 프롬프트를 앱 내부 모델로 해석 입니다. |
| `app/src/main/java/com/focussound/ai/SoundIntent.kt` | FocusSound의 프롬프트 해석 계층에서 문장을 소리 생성 의도로 바꾸는 데 사용됩니다. SoundIntent Kotlin 소스입니다. 주 역할은 프롬프트 해석 결과인 소리 종류, 강도, 분위기, 재생 조건을 담는 의도 모델 정의 입니다. |
| `app/src/main/java/com/focussound/ai/SoundPromptParser.kt` | FocusSound의 프롬프트 해석 계층에서 문장을 소리 생성 의도로 바꾸는 데 사용됩니다. SoundPromptParser Kotlin 소스입니다. 주 역할은 입력 텍스트나 프롬프트를 앱 내부 모델로 해석 입니다. |
| `app/src/main/java/com/focussound/audio/AmbientPadGenerator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. AmbientPadGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/audio/AudioEnvelope.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. AudioEnvelope Kotlin 소스입니다. 주 역할은 소리의 시작, 유지, 감소, 종료 볼륨 곡선을 계산해 자연스러운 음량 변화를 만드는 처리 입니다. |
| `app/src/main/java/com/focussound/audio/AudioFilter.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. AudioFilter Kotlin 소스입니다. 주 역할은 오디오 신호를 주파수/톤 관점에서 보정 입니다. |
| `app/src/main/java/com/focussound/audio/AudioModulator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. AudioModulator Kotlin 소스입니다. 주 역할은 시간에 따라 볼륨, 필터, 피치 같은 오디오 파라미터를 흔들어 움직임을 만드는 변조 처리 입니다. |
| `app/src/main/java/com/focussound/audio/AudioSampleBus.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. AudioSampleBus Kotlin 소스입니다. 주 역할은 여러 샘플 재생 신호를 한 버스로 모아 믹싱 엔진에 전달하는 오디오 라우팅 처리 입니다. |
| `app/src/main/java/com/focussound/audio/BrownNoiseGenerator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. BrownNoiseGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/audio/FocusSoundEngine.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. FocusSoundEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/audio/instrument/InstrumentDownloadManager.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. InstrumentDownloadManager Kotlin 소스입니다. 주 역할은 악기 팩이나 외부 샘플 리소스를 내려받고 로컬 상태를 관리 입니다. |
| `app/src/main/java/com/focussound/audio/instrument/InstrumentLibrary.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. InstrumentLibrary Kotlin 소스입니다. 주 역할은 악기/코드/프리셋처럼 재사용 가능한 항목 목록과 조회 규칙을 제공 입니다. |
| `app/src/main/java/com/focussound/audio/instrument/InstrumentPreset.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. InstrumentPreset Kotlin 소스입니다. 주 역할은 사용자가 저장하거나 앱이 추천하는 사운드/악기 설정 묶음을 표현 입니다. |
| `app/src/main/java/com/focussound/audio/NoiseGenerator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. NoiseGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/audio/PinkNoiseGenerator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. PinkNoiseGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/audio/playback/LiveMusicPlaybackEngine.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. LiveMusicPlaybackEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/audio/playback/NoteScheduler.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. NoteScheduler Kotlin 소스입니다. 주 역할은 재생 타이밍과 이벤트 순서를 관리 입니다. |
| `app/src/main/java/com/focussound/audio/playback/PlaybackClock.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. PlaybackClock Kotlin 소스입니다. 주 역할은 재생 타이밍과 이벤트 순서를 관리 입니다. |
| `app/src/main/java/com/focussound/audio/RainTextureGenerator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. RainTextureGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/SampleCache.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SampleCache Kotlin 소스입니다. 주 역할은 반복 로딩 비용을 줄이기 위한 메모리/파일 캐시 관리 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/SampleData.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SampleData Kotlin 소스입니다. 주 역할은 디코딩된 샘플 PCM 데이터와 샘플레이트 같은 재생 메타데이터를 담는 모델 정의 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/SampleDecoder.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SampleDecoder Kotlin 소스입니다. 주 역할은 오디오 샘플이나 외부 데이터를 앱에서 사용할 형태로 디코딩 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/SampleInstrumentEngine.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SampleInstrumentEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/SampleMixer.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SampleMixer Kotlin 소스입니다. 주 역할은 여러 오디오 신호나 파트를 볼륨 균형에 맞게 합쳐 최종 출력 신호를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/SampleResampler.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SampleResampler Kotlin 소스입니다. 주 역할은 샘플레이트나 피치를 바꿔 악기 샘플을 원하는 음높이로 재생할 수 있게 변환 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/SampleVoice.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SampleVoice Kotlin 소스입니다. 주 역할은 개별 음표 또는 샘플 재생 단위를 관리해 시작/종료/볼륨 변화를 처리 입니다. |
| `app/src/main/java/com/focussound/audio/sampler/StudioReverb.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. StudioReverb Kotlin 소스입니다. 주 역할은 잔향을 더해 공간감과 거리감을 만드는 오디오 이펙트 처리 입니다. |
| `app/src/main/java/com/focussound/audio/SoftLimiter.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SoftLimiter Kotlin 소스입니다. 주 역할은 오디오 출력 피크를 제한해 클리핑을 방지 입니다. |
| `app/src/main/java/com/focussound/audio/soundfont/FluidSynthBridge.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. FluidSynthBridge Kotlin 소스입니다. 주 역할은 외부 라이브러리나 네이티브 엔진과 Kotlin 코드 사이의 호출 연결부 제공 입니다. |
| `app/src/main/java/com/focussound/audio/soundfont/SoundFontEngine.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SoundFontEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/audio/SpectralTiltFilter.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SpectralTiltFilter Kotlin 소스입니다. 주 역할은 오디오 신호를 주파수/톤 관점에서 보정 입니다. |
| `app/src/main/java/com/focussound/audio/StereoProcessor.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. StereoProcessor Kotlin 소스입니다. 주 역할은 오디오 신호를 후처리해 스테레오, 톤, 출력 안정성을 조정 입니다. |
| `app/src/main/java/com/focussound/audio/synth/ADSREnvelope.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. ADSREnvelope Kotlin 소스입니다. 주 역할은 소리의 시작, 유지, 감소, 종료 볼륨 곡선을 계산해 자연스러운 음량 변화를 만드는 처리 입니다. |
| `app/src/main/java/com/focussound/audio/synth/BassSynth.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. BassSynth Kotlin 소스입니다. 주 역할은 발진기와 합성 규칙으로 악기음, 베이스, 노이즈 텍스처 같은 합성 사운드를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/synth/InternalSynthRenderer.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. InternalSynthRenderer Kotlin 소스입니다. 주 역할은 발진기와 합성 규칙으로 악기음, 베이스, 노이즈 텍스처 같은 합성 사운드를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/synth/Mixer.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. Mixer Kotlin 소스입니다. 주 역할은 여러 오디오 신호나 파트를 볼륨 균형에 맞게 합쳐 최종 출력 신호를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/synth/NoiseTextureSynth.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. NoiseTextureSynth Kotlin 소스입니다. 주 역할은 발진기와 합성 규칙으로 악기음, 베이스, 노이즈 텍스처 같은 합성 사운드를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/synth/OfflineWavRenderer.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. OfflineWavRenderer Kotlin 소스입니다. 주 역할은 생성된 음악/사운드를 실시간 재생 없이 WAV 파일 데이터로 렌더링하는 오프라인 출력 처리 입니다. |
| `app/src/main/java/com/focussound/audio/synth/Oscillator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. Oscillator Kotlin 소스입니다. 주 역할은 사인파, 톱니파, 사각파 같은 기본 파형을 생성해 합성기의 원천 신호를 만드는 처리 입니다. |
| `app/src/main/java/com/focussound/audio/synth/PolySynth.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. PolySynth Kotlin 소스입니다. 주 역할은 발진기와 합성 규칙으로 악기음, 베이스, 노이즈 텍스처 같은 합성 사운드를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/synth/SimpleFilter.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SimpleFilter Kotlin 소스입니다. 주 역할은 오디오 신호를 주파수/톤 관점에서 보정 입니다. |
| `app/src/main/java/com/focussound/audio/synth/SoftLimiter.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SoftLimiter Kotlin 소스입니다. 주 역할은 오디오 출력 피크를 제한해 클리핑을 방지 입니다. |
| `app/src/main/java/com/focussound/audio/synth/SoftPianoSynth.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. SoftPianoSynth Kotlin 소스입니다. 주 역할은 발진기와 합성 규칙으로 악기음, 베이스, 노이즈 텍스처 같은 합성 사운드를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/synth/Voice.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. Voice Kotlin 소스입니다. 주 역할은 개별 음표 또는 샘플 재생 단위를 관리해 시작/종료/볼륨 변화를 처리 입니다. |
| `app/src/main/java/com/focussound/audio/synth/WarmPadSynth.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. WarmPadSynth Kotlin 소스입니다. 주 역할은 발진기와 합성 규칙으로 악기음, 베이스, 노이즈 텍스처 같은 합성 사운드를 생성 입니다. |
| `app/src/main/java/com/focussound/audio/texture/TextureEngine.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. TextureEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/audio/tone/OnePoleLowPass.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. OnePoleLowPass Kotlin 소스입니다. 주 역할은 한 개의 pole을 쓰는 저역 통과 필터로 고주파를 줄이고 부드러운 톤을 만드는 처리 입니다. |
| `app/src/main/java/com/focussound/audio/tone/RealtimeToneProcessor.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. RealtimeToneProcessor Kotlin 소스입니다. 주 역할은 오디오 신호를 후처리해 스테레오, 톤, 출력 안정성을 조정 입니다. |
| `app/src/main/java/com/focussound/audio/tone/ToneControlState.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. ToneControlState Kotlin 소스입니다. 주 역할은 밝기, 따뜻함, 필터 강도 같은 톤 조절 파라미터의 현재 상태를 담는 모델 정의 입니다. |
| `app/src/main/java/com/focussound/audio/WhiteNoiseGenerator.kt` | FocusSound의 오디오 엔진 계층에서 실제 신호 생성, 필터링, 샘플 재생을 담당합니다. WhiteNoiseGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/composition/BassLineGenerator.kt` | BassLineGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/composition/ChordProgressionGenerator.kt` | ChordProgressionGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionExporter.kt` | CompositionExporter Kotlin 소스입니다. 주 역할은 생성된 작곡 결과를 파일이나 외부에서 사용할 수 있는 데이터 형식으로 내보내는 처리 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionIntent.kt` | CompositionIntent Kotlin 소스입니다. 주 역할은 사용자가 원하는 분위기, 목적, 악기, 길이 같은 작곡 요청 의도를 담는 모델 정의 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionParserFallback.kt` | CompositionParserFallback Kotlin 소스입니다. 주 역할은 작곡 프롬프트 해석이 부족할 때 기본 작곡 의도와 안전한 설정을 채우는 대체 처리 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionPatch.kt` | CompositionPatch Kotlin 소스입니다. 주 역할은 작곡 결과에 적용할 악기, 톤, 패턴, 섹션 조정값을 묶어 표현하는 패치 모델 정의 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionPatchGenerator.kt` | CompositionPatchGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionPromptParser.kt` | CompositionPromptParser Kotlin 소스입니다. 주 역할은 입력 텍스트나 프롬프트를 앱 내부 모델로 해석 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionRecommender.kt` | CompositionRecommender Kotlin 소스입니다. 주 역할은 사용자 상태에 맞는 추천 결과 생성 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionRequest.kt` | CompositionRequest Kotlin 소스입니다. 주 역할은 로컬 작곡 엔진에 전달할 프롬프트, 조건, 길이, 스타일 요청값을 담는 입력 모델 정의 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionSetup.kt` | CompositionSetup Kotlin 소스입니다. 주 역할은 작곡 시작 전에 선택된 악기, 템포, 스타일, 조건 설정을 구성하는 초기 설정 모델 정의 입니다. |
| `app/src/main/java/com/focussound/composition/CompositionVariation.kt` | CompositionVariation Kotlin 소스입니다. 주 역할은 같은 작곡 요청에서 생성할 수 있는 변주 옵션과 차이점을 표현하는 모델 정의 입니다. |
| `app/src/main/java/com/focussound/composition/FatigueAwareArranger.kt` | FatigueAwareArranger Kotlin 소스입니다. 주 역할은 사용자의 피로도나 집중 상태에 따라 리듬/밀도/악기 배치를 조절하는 편곡 처리 입니다. |
| `app/src/main/java/com/focussound/composition/LocalComposerEngine.kt` | LocalComposerEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/composition/MelodyGenerator.kt` | MelodyGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/composition/MidiExporter.kt` | MidiExporter Kotlin 소스입니다. 주 역할은 생성된 음표와 코드 이벤트를 MIDI 파일 또는 MIDI 호환 데이터로 변환해 내보내는 처리 입니다. |
| `app/src/main/java/com/focussound/composition/MusicalTypes.kt` | MusicalTypes Kotlin 소스입니다. 주 역할은 악기, 사운드, 상태처럼 코드 전반에서 공유하는 타입 정의 입니다. |
| `app/src/main/java/com/focussound/composition/PadArranger.kt` | PadArranger Kotlin 소스입니다. 주 역할은 배경 패드 파트의 코드, 지속음, 밀도를 배치해 작곡 결과의 공간감을 만드는 편곡 처리 입니다. |
| `app/src/main/java/com/focussound/composition/RhythmGenerator.kt` | RhythmGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/composition/RuleBasedCompositionPromptParser.kt` | RuleBasedCompositionPromptParser Kotlin 소스입니다. 주 역할은 입력 텍스트나 프롬프트를 앱 내부 모델로 해석 입니다. |
| `app/src/main/java/com/focussound/condition/ConditionRepository.kt` | ConditionRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/condition/HealthConnectConditionSource.kt` | HealthConnectConditionSource Kotlin 소스입니다. 주 역할은 Android Health Connect에서 수면, 활동, 컨디션 데이터를 읽어 집중 조건으로 변환하는 데이터 소스 처리 입니다. |
| `app/src/main/java/com/focussound/condition/ManualConditionSource.kt` | ManualConditionSource Kotlin 소스입니다. 주 역할은 사용자가 직접 입력한 기분, 피로, 집중 조건을 앱 내부 조건 모델로 제공하는 데이터 소스 처리 입니다. |
| `app/src/main/java/com/focussound/condition/UserCondition.kt` | UserCondition Kotlin 소스입니다. 주 역할은 사용자의 피로도, 기분, 시간대, 작업 상태 같은 집중 조건 값을 담는 모델 정의 입니다. |
| `app/src/main/java/com/focussound/data/FocusSession.kt` | FocusSession Kotlin 소스입니다. 주 역할은 집중 세션의 시작/종료, 사용한 사운드, 지속 시간, 결과 상태를 저장하는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/data/FocusSoundRepository.kt` | FocusSoundRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/data/SoundProfile.kt` | SoundProfile Kotlin 소스입니다. 주 역할은 사운드 종류, 톤, 볼륨, 추천 정보처럼 재생 프로필을 구성하는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/data/UserPreference.kt` | UserPreference Kotlin 소스입니다. 주 역할은 사용자의 선호 사운드, 볼륨, 스타일, 개인 설정 값을 저장하는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/data/UserSoundPreset.kt` | UserSoundPreset Kotlin 소스입니다. 주 역할은 사용자가 저장하거나 앱이 추천하는 사운드/악기 설정 묶음을 표현 입니다. |
| `app/src/main/java/com/focussound/database/CompositionDao.kt` | CompositionDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/focussound/database/CompositionNoteEntity.kt` | CompositionNoteEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/CompositionPatchEntity.kt` | CompositionPatchEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/ConditionSnapshotDao.kt` | ConditionSnapshotDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/focussound/database/ConditionSnapshotEntity.kt` | ConditionSnapshotEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/FocusSessionDao.kt` | FocusSessionDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/focussound/database/FocusSessionEntity.kt` | FocusSessionEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/FocusSoundDatabase.kt` | FocusSoundDatabase Kotlin 소스입니다. 주 역할은 Room 데이터베이스 정의와 Entity/DAO 연결 입니다. |
| `app/src/main/java/com/focussound/database/InstrumentDao.kt` | InstrumentDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/focussound/database/InstrumentPresetEntity.kt` | InstrumentPresetEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/PresetDao.kt` | PresetDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/focussound/database/PresetEntity.kt` | PresetEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/SampleZoneEntity.kt` | SampleZoneEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/SoundPatchDao.kt` | SoundPatchDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/focussound/database/SoundPatchEntity.kt` | SoundPatchEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/database/UserTasteVectorDao.kt` | UserTasteVectorDao Kotlin 소스입니다. 주 역할은 데이터베이스 접근 쿼리 정의 입니다. |
| `app/src/main/java/com/focussound/database/UserTasteVectorEntity.kt` | UserTasteVectorEntity Kotlin 소스입니다. 주 역할은 저장 데이터 구조 정의 입니다. |
| `app/src/main/java/com/focussound/instrument/AutoInstrumentSelector.kt` | AutoInstrumentSelector Kotlin 소스입니다. 주 역할은 사용자 조건이나 프롬프트에 맞는 옵션, 레벨, 악기, 모드를 선택 입니다. |
| `app/src/main/java/com/focussound/instrument/InstrumentImporter.kt` | InstrumentImporter Kotlin 소스입니다. 주 역할은 외부 악기/샘플 파일을 앱 내부 형식으로 가져와 사용할 수 있게 등록 입니다. |
| `app/src/main/java/com/focussound/instrument/InstrumentRepository.kt` | InstrumentRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/instrument/InstrumentTypes.kt` | InstrumentTypes Kotlin 소스입니다. 주 역할은 악기, 사운드, 상태처럼 코드 전반에서 공유하는 타입 정의 입니다. |
| `app/src/main/java/com/focussound/instrument/SoundFontInstrumentProvider.kt` | SoundFontInstrumentProvider Kotlin 소스입니다. 주 역할은 앱의 다른 계층이 사용할 데이터, 악기, 위젯, 상태 객체를 공급 입니다. |
| `app/src/main/java/com/focussound/MainActivity.kt` | MainActivity Kotlin 소스입니다. 주 역할은 Android 화면 진입점과 UI 초기화 입니다. |
| `app/src/main/java/com/focussound/music/arrangement/ArrangementEngine.kt` | ArrangementEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/music/arrangement/BassLineGenerator.kt` | BassLineGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/arrangement/PadArranger.kt` | PadArranger Kotlin 소스입니다. 주 역할은 생성된 음악의 악기 파트, 리듬, 패드, 스트링 배치를 편곡 규칙에 맞게 구성 입니다. |
| `app/src/main/java/com/focussound/music/arrangement/PianoPatternGenerator.kt` | PianoPatternGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/arrangement/RhythmArranger.kt` | RhythmArranger Kotlin 소스입니다. 주 역할은 생성된 음악의 악기 파트, 리듬, 패드, 스트링 배치를 편곡 규칙에 맞게 구성 입니다. |
| `app/src/main/java/com/focussound/music/arrangement/StringsArranger.kt` | StringsArranger Kotlin 소스입니다. 주 역할은 생성된 음악의 악기 파트, 리듬, 패드, 스트링 배치를 편곡 규칙에 맞게 구성 입니다. |
| `app/src/main/java/com/focussound/music/director/MusicEnergyPlanner.kt` | MusicEnergyPlanner Kotlin 소스입니다. 주 역할은 회복/일정/처리 계획 계산 입니다. |
| `app/src/main/java/com/focussound/music/director/TaskMusicDirector.kt` | TaskMusicDirector Kotlin 소스입니다. 주 역할은 작업 목적에 맞는 음악 프로필과 전체 생성 방향을 결정 입니다. |
| `app/src/main/java/com/focussound/music/director/TaskMusicProfile.kt` | TaskMusicProfile Kotlin 소스입니다. 주 역할은 작업 목적에 맞는 음악 프로필과 전체 생성 방향을 결정 입니다. |
| `app/src/main/java/com/focussound/music/form/FormGenerator.kt` | FormGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/form/MusicSection.kt` | MusicSection Kotlin 소스입니다. 주 역할은 음악의 섹션, 반복, 전개 구조를 표현하고 생성 흐름에 제공 입니다. |
| `app/src/main/java/com/focussound/music/form/SectionTransitionPlanner.kt` | SectionTransitionPlanner Kotlin 소스입니다. 주 역할은 회복/일정/처리 계획 계산 입니다. |
| `app/src/main/java/com/focussound/music/generation/ArrangementSampler.kt` | ArrangementSampler Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/FormSampler.kt` | FormSampler Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/GenerationTemperature.kt` | GenerationTemperature Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/HarmonySampler.kt` | HarmonySampler Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/MotifSampler.kt` | MotifSampler Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/MusicFingerprint.kt` | MusicFingerprint Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/NoveltyGuard.kt` | NoveltyGuard Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/SectionTransitionSampler.kt` | SectionTransitionSampler Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/TaskAwareComposer.kt` | TaskAwareComposer Kotlin 소스입니다. 주 역할은 화성, 모티프, 섹션 전환, 새로움 제어 등 실제 음악 생성 샘플링 규칙을 수행 입니다. |
| `app/src/main/java/com/focussound/music/generation/VariationEngine.kt` | VariationEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/music/harmony/ChordProgressionLibrary.kt` | ChordProgressionLibrary Kotlin 소스입니다. 주 역할은 악기/코드/프리셋처럼 재사용 가능한 항목 목록과 조회 규칙을 제공 입니다. |
| `app/src/main/java/com/focussound/music/harmony/HarmonyEngine.kt` | HarmonyEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/music/harmony/ModulationPlanner.kt` | ModulationPlanner Kotlin 소스입니다. 주 역할은 회복/일정/처리 계획 계산 입니다. |
| `app/src/main/java/com/focussound/music/harmony/VoicingGenerator.kt` | VoicingGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/knowledge/MusicAvoidRule.kt` | MusicAvoidRule Kotlin 소스입니다. 주 역할은 작업 의미, 회피 규칙, 음악 프로필 같은 생성 지식 베이스를 정의 입니다. |
| `app/src/main/java/com/focussound/music/knowledge/TaskMeaningKnowledgeBase.kt` | TaskMeaningKnowledgeBase Kotlin 소스입니다. 주 역할은 작업 의미, 회피 규칙, 음악 프로필 같은 생성 지식 베이스를 정의 입니다. |
| `app/src/main/java/com/focussound/music/knowledge/TaskMusicProfile.kt` | TaskMusicProfile Kotlin 소스입니다. 주 역할은 작업 의미, 회피 규칙, 음악 프로필 같은 생성 지식 베이스를 정의 입니다. |
| `app/src/main/java/com/focussound/music/knowledge/TaskMusicProfileRepository.kt` | TaskMusicProfileRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/music/learning/ArrangementStatisticsExtractor.kt` | ArrangementStatisticsExtractor Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/learning/FormStatisticsExtractor.kt` | FormStatisticsExtractor Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/learning/HarmonyStatisticsExtractor.kt` | HarmonyStatisticsExtractor Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/learning/LearnedTaskStyleModel.kt` | LearnedTaskStyleModel Kotlin 소스입니다. 주 역할은 도메인 또는 UI에서 쓰는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/music/learning/MidiCorpusAnalyzer.kt` | MidiCorpusAnalyzer Kotlin 소스입니다. 주 역할은 입력 데이터 분석과 점수/상태 계산 입니다. |
| `app/src/main/java/com/focussound/music/learning/ModelSerializer.kt` | ModelSerializer Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/learning/MotifStatisticsExtractor.kt` | MotifStatisticsExtractor Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/learning/RhythmStatisticsExtractor.kt` | RhythmStatisticsExtractor Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/learning/SectionTransitionStatisticsExtractor.kt` | SectionTransitionStatisticsExtractor Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/learning/TaskMusicGrammarLearner.kt` | TaskMusicGrammarLearner Kotlin 소스입니다. 주 역할은 생성 결과와 사용자 반응에서 통계를 추출하고 개인화 모델을 갱신/직렬화 입니다. |
| `app/src/main/java/com/focussound/music/melody/CounterMelodyGenerator.kt` | CounterMelodyGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/melody/MelodyGenerator.kt` | MelodyGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/melody/MotifEngine.kt` | MotifEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/music/melody/PhraseVariationEngine.kt` | PhraseVariationEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/music/model/ChordEvent.kt` | ChordEvent Kotlin 소스입니다. 주 역할은 음표, 코드, 섹션, 생성 결과처럼 음악 생성 결과를 주고받는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/music/model/GeneratedPiece.kt` | GeneratedPiece Kotlin 소스입니다. 주 역할은 음표, 코드, 섹션, 생성 결과처럼 음악 생성 결과를 주고받는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/music/model/GeneratedSection.kt` | GeneratedSection Kotlin 소스입니다. 주 역할은 음표, 코드, 섹션, 생성 결과처럼 음악 생성 결과를 주고받는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/music/model/LiveCompositionRequest.kt` | LiveCompositionRequest Kotlin 소스입니다. 주 역할은 음표, 코드, 섹션, 생성 결과처럼 음악 생성 결과를 주고받는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/music/model/NoteEvent.kt` | NoteEvent Kotlin 소스입니다. 주 역할은 음표, 코드, 섹션, 생성 결과처럼 음악 생성 결과를 주고받는 데이터 모델 정의 입니다. |
| `app/src/main/java/com/focussound/music/realtime/LiveVariationController.kt` | LiveVariationController Kotlin 소스입니다. 주 역할은 기능 실행 흐름을 조율하고 상태 전환을 관리 입니다. |
| `app/src/main/java/com/focussound/music/realtime/MusicMemory.kt` | MusicMemory Kotlin 소스입니다. 주 역할은 재생 중 이어지는 음악 생성을 위해 큐, 메모리, 롤링 작곡 상태를 관리 입니다. |
| `app/src/main/java/com/focussound/music/realtime/NextBarGenerator.kt` | NextBarGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/realtime/NextSectionGenerator.kt` | NextSectionGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/music/realtime/RealTimeGenerationQueue.kt` | RealTimeGenerationQueue Kotlin 소스입니다. 주 역할은 재생 중 이어지는 음악 생성을 위해 큐, 메모리, 롤링 작곡 상태를 관리 입니다. |
| `app/src/main/java/com/focussound/music/realtime/RollingComposer.kt` | RollingComposer Kotlin 소스입니다. 주 역할은 재생 중 이어지는 음악 생성을 위해 큐, 메모리, 롤링 작곡 상태를 관리 입니다. |
| `app/src/main/java/com/focussound/personalization/AdaptiveSoundRecommender.kt` | AdaptiveSoundRecommender Kotlin 소스입니다. 주 역할은 사용자 상태에 맞는 추천 결과 생성 입니다. |
| `app/src/main/java/com/focussound/personalization/LocalCompositionRecommender.kt` | LocalCompositionRecommender Kotlin 소스입니다. 주 역할은 사용자 상태에 맞는 추천 결과 생성 입니다. |
| `app/src/main/java/com/focussound/personalization/TasteVectorRepository.kt` | TasteVectorRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/personalization/TasteVectorUpdater.kt` | TasteVectorUpdater Kotlin 소스입니다. 주 역할은 사용자의 선호 벡터와 취향 변화를 갱신해 추천과 생성에 반영 입니다. |
| `app/src/main/java/com/focussound/personalization/UserSoundTasteVector.kt` | UserSoundTasteVector Kotlin 소스입니다. 주 역할은 사용자의 선호 벡터와 취향 변화를 갱신해 추천과 생성에 반영 입니다. |
| `app/src/main/java/com/focussound/playback/CompositionPlaybackEngine.kt` | CompositionPlaybackEngine Kotlin 소스입니다. 주 역할은 핵심 처리 엔진과 실행 흐름 제어 입니다. |
| `app/src/main/java/com/focussound/playback/NoteScheduler.kt` | NoteScheduler Kotlin 소스입니다. 주 역할은 재생 타이밍과 이벤트 순서를 관리 입니다. |
| `app/src/main/java/com/focussound/playback/PlaybackMode.kt` | PlaybackMode Kotlin 소스입니다. 주 역할은 재생 모드, 재생 상태, 현재 세션의 오디오 출력 상태를 표현 입니다. |
| `app/src/main/java/com/focussound/playback/PlaybackState.kt` | PlaybackState Kotlin 소스입니다. 주 역할은 재생 모드, 재생 상태, 현재 세션의 오디오 출력 상태를 표현 입니다. |
| `app/src/main/java/com/focussound/recommendation/FatigueEstimator.kt` | FatigueEstimator Kotlin 소스입니다. 주 역할은 입력 부족 상황에서 필요한 값을 추정 입니다. |
| `app/src/main/java/com/focussound/recommendation/SoundRecommender.kt` | SoundRecommender Kotlin 소스입니다. 주 역할은 사용자 상태에 맞는 추천 결과 생성 입니다. |
| `app/src/main/java/com/focussound/repository/CompositionPatchRepository.kt` | CompositionPatchRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/repository/PresetRepository.kt` | PresetRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/repository/SessionRepository.kt` | SessionRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/repository/UserPreferenceRepository.kt` | UserPreferenceRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/service/FocusSoundController.kt` | FocusSoundController Kotlin 소스입니다. 주 역할은 기능 실행 흐름을 조율하고 상태 전환을 관리 입니다. |
| `app/src/main/java/com/focussound/service/FocusSoundService.kt` | FocusSoundService Kotlin 소스입니다. 주 역할은 백그라운드에서 계속 동작하는 Android 서비스 로직 입니다. |
| `app/src/main/java/com/focussound/service/PlaybackNotificationManager.kt` | PlaybackNotificationManager Kotlin 소스입니다. 주 역할은 백그라운드 재생 알림과 Android 서비스 연동을 관리 입니다. |
| `app/src/main/java/com/focussound/sounddesign/SoundPatch.kt` | SoundPatch Kotlin 소스입니다. 주 역할은 사운드 패치와 음색 설계 값을 정의해 생성 엔진과 UI가 공유 입니다. |
| `app/src/main/java/com/focussound/sounddesign/SoundPatchGenerator.kt` | SoundPatchGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/sounddesign/SoundPatchNameGenerator.kt` | SoundPatchNameGenerator Kotlin 소스입니다. 주 역할은 분석 결과, 리포트, 신호, 초안 같은 출력 생성 입니다. |
| `app/src/main/java/com/focussound/sounddesign/SoundPatchRepository.kt` | SoundPatchRepository Kotlin 소스입니다. 주 역할은 데이터 계층과 화면/도메인 계층 연결 입니다. |
| `app/src/main/java/com/focussound/timer/FocusTimer.kt` | FocusTimer Kotlin 소스입니다. 주 역할은 집중 세션의 시작, 종료, 남은 시간, 진행 상태를 계산 입니다. |
| `app/src/main/java/com/focussound/ui/ai/AiSoundDesignerScreen.kt` | AiSoundDesignerScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/ai/GeneratedPatchCard.kt` | GeneratedPatchCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/ai/PromptSuggestionChip.kt` | PromptSuggestionChip Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/composer/ChordProgressionView.kt` | ChordProgressionView Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/composer/ComposerAdjustmentChips.kt` | ComposerAdjustmentChips Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/composer/CompositionResultCard.kt` | CompositionResultCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/composer/ExportCompositionDialog.kt` | ExportCompositionDialog Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/composer/LocalComposerScreen.kt` | LocalComposerScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/condition/ConditionCheckScreen.kt` | ConditionCheckScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/FocusSoundApp.kt` | FocusSoundApp Kotlin 소스입니다. 주 역할은 Compose 앱 루트와 화면 내비게이션 구성 입니다. |
| `app/src/main/java/com/focussound/ui/FocusSoundViewModel.kt` | FocusSoundViewModel Kotlin 소스입니다. 주 역할은 화면 상태, 이벤트 처리, 비동기 작업 관리 입니다. |
| `app/src/main/java/com/focussound/ui/home/AiCompositionOnlyCard.kt` | AiCompositionOnlyCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/home/HomeScreen.kt` | HomeScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/home/InstrumentPackCard.kt` | InstrumentPackCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/home/PromptCompositionCard.kt` | PromptCompositionCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/home/RecentPresetCard.kt` | RecentPresetCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/home/RecommendedSoundCard.kt` | RecommendedSoundCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/home/SoundDesignCard.kt` | SoundDesignCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/instrument/InstrumentImportScreen.kt` | InstrumentImportScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/instrument/InstrumentPackScreen.kt` | InstrumentPackScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/instrument/InstrumentPresetCard.kt` | InstrumentPresetCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/instrument/SampleMappingScreen.kt` | SampleMappingScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/player/FatigueScoreCard.kt` | FatigueScoreCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/player/PlayerScreen.kt` | PlayerScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/player/SoundAnalysisCard.kt` | SoundAnalysisCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/player/ToneControlPanel.kt` | ToneControlPanel Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/player/WaveformView.kt` | WaveformView Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/profile/SoundTasteProfileScreen.kt` | SoundTasteProfileScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/report/ReportCard.kt` | ReportCard Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/report/WeeklyReportScreen.kt` | WeeklyReportScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/setup/CompositionResultScreen.kt` | CompositionResultScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/setup/InstrumentSelectionScreen.kt` | InstrumentSelectionScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/setup/MoodStyleSelectionScreen.kt` | MoodStyleSelectionScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/setup/SoundTypeSelectionScreen.kt` | SoundTypeSelectionScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/sounddesign/PresetSaveDialog.kt` | PresetSaveDialog Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/sounddesign/SoundDesignScreen.kt` | SoundDesignScreen Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/sounddesign/SoundSlider.kt` | SoundSlider Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/java/com/focussound/ui/theme/Theme.kt` | Theme Kotlin 소스입니다. 주 역할은 Compose 앱의 색상, 글꼴, 테마 값을 정의해 UI 스타일을 통일 입니다. |
| `app/src/main/java/com/focussound/ui/UiFormat.kt` | UiFormat Kotlin 소스입니다. 주 역할은 사용자에게 보이는 Compose 화면 또는 UI 보조 컴포넌트 구성 입니다. |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | Android 적응형 런처 아이콘의 전경 그래픽을 정의합니다. |
| `app/src/main/res/values/strings.xml` | 앱에서 표시하는 문자열 리소스를 한 곳에 모아 다국어 처리와 재사용을 쉽게 합니다. |
| `app/src/main/res/values/themes.xml` | Android 앱의 Material/Compose 테마, 색상 연결, 공통 스타일을 정의하는 values 리소스 XML입니다. |
| `build.gradle.kts` | 루트 Gradle 빌드 설정입니다. Android/Kotlin 플러그인과 전체 프로젝트 빌드 구성을 정의합니다. |
| `gradle.properties` | Gradle 빌드 성능, AndroidX 사용 여부, Kotlin/빌드 옵션 같은 공통 속성을 지정합니다. |
| `gradle/wrapper/gradle-wrapper.jar` | Gradle Wrapper가 지정된 Gradle 버전을 내려받고 실행하는 데 사용하는 바이너리 파일입니다. |
| `gradle/wrapper/gradle-wrapper.properties` | Gradle Wrapper가 사용할 Gradle 배포판 버전과 다운로드 URL을 지정합니다. |
| `gradlew` | Unix/macOS/Linux에서 Gradle Wrapper를 실행하는 스크립트입니다. |
| `gradlew.bat` | Windows에서 Gradle Wrapper를 실행하는 배치 스크립트입니다. |
| `PROJECT_STRUCTURE.md` | 프로젝트의 모든 주요 폴더와 Git 추적 파일을 한글로 설명하는 구조 문서입니다. 처음 보는 사람이 경로별 역할을 빠르게 파악하기 위해 추가했습니다. |
| `settings.gradle.kts` | Gradle이 인식할 프로젝트 이름과 포함할 모듈을 지정하는 설정 파일입니다. |

## 읽는 방법

- 먼저 폴더별 설명에서 큰 기능 묶음을 확인한 다음, 파일별 설명에서 실제 구현 파일을 찾으면 됩니다.
- Android 프로젝트는 `app/src/main/java` 아래 Kotlin 파일이 핵심 코드이고, `app/src/main/res`와 `app/src/main/assets`는 화면/모델/오디오 자산입니다.
- 웹 프로젝트는 `index.html`, `styles.css`, `script.js` 또는 `app.js`가 화면 구조, 스타일, 동작을 나눠 담당합니다.
- Python 프로젝트는 루트의 실행 스크립트와 `src`, `backend`, `scripts`, `tests` 폴더를 함께 보면 처리 흐름을 이해할 수 있습니다.
