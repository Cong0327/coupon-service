🚀 선착순 쿠폰 발급 시스템 (High-Concurrency Coupon System)
대규모 트래픽이 발생하는 선착순 이벤트 상황에서 **데이터 정합성(Data Integrity)**을 유지하고, 시스템 성능을 최적화하는 과정을 학습하고 증명하기 위한 프로젝트입니다.

🎯 프로젝트 목적
동시성 제어: 1,000명 이상의 동시 접속자가 수량이 제한된 쿠폰을 요청할 때 생기는 Race Condition 해결

성능 최적화: DB 부하를 줄이기 위한 효율적인 아키텍처 설계 (RDB vs Redis)

단계적 문제 해결: 단순 구현에서 시작하여 기술적 한계를 마주하고, 이를 해결해 나가는 과정 기록

🛠 Tech Stack
Language: Java 17

Framework: Spring Boot 3.x

Database: MySQL / H2 (Test)

ORM: Spring Data JPA

Concurrency Control: Redis / Redisson (도입 예정)

Test Tool: JUnit5, Jmeter (부하 테스트용)

🚨 핵심 도전 과제: 동시성 이슈 재현 및 해결
1. 문제 상황 (Problem)
현상: 100개 한정 쿠폰에 대해 1,000개의 동시 요청을 보냈을 때, DB에 100개 이상의 발급 내역이 저장되는 현상 발생.

원인: 여러 스레드가 동시에 issued_quantity를 조회(Read)하고 업데이트(Write)하는 과정에서 데이터 정합성이 깨지는 Race Condition 발생.

2. 해결 로직 로드맵 (Roadmap)
이 프로젝트는 아래의 단계로 고도화를 진행합니다.

[x] Step 1. 기본 로직 구현: JPA를 활용한 기본 발급 로직 (동시성 이슈 확인)

[ ] Step 2. DB Lock 적용: Pessimistic Lock(비관적 락)을 통한 데이터 정합성 보장

[ ] Step 3. Redis 기반 분산 시스템: Redis의 원자적(Atomic) 연산을 이용한 성능 최적화

[ ] Step 4. 비동기 처리: Kafka 또는 RabbitMQ를 활용한 Write 부하 분산

🧪 테스트 결과 (Step 1 기준)
테스트 환경: 32개 스레드 풀, 1,000건 동시 요청

기대 결과: 발급 수량 100개

실제 결과: 발급 수량 142개 (예시 숫자) -> 데이터 정합성 실패 확인


### Step 2. DB Pessimistic Lock 적용 결과
- **해결 방안**: JPA의 `@Lock(LockModeType.PESSIMISTIC_WRITE)`를 적용하여 데이터 수정 시 로우 레벨 락 점유.
- **장점**: 데이터 정합성을 확실하게 보장함.
- **단점**: 
    - 모든 요청이 순차적으로 대기해야 하므로 **응답 시간이 대폭 증가**함 (성능 저하).
    - 트래픽이 몰릴 경우 DB 커넥션 풀이 마를 수 있는 위험 존재.
 
### Step 3. Redis 기반 원자적 연산 적용 (최종 해결)
- **해결 방안**: Redis의 싱글 스레드 기반 `INCR` 명령어를 사용하여 애플리케이션 레벨에서 순번 선점.
- **성과**: 
    - **데이터 정합성**: 1,000건의 동시 요청에도 정확히 100건 발급 성공.
    - **성능 개선**: DB 비관적 락 대비 응답 속도 약 N배 향상 (테스트 기준).
    - **부하 분산**: 초과된 요청(900건)은 DB에 접근하지 못하도록 Redis 레이어에서 차단하여 DB 리소스 보호.
