# Kafka-Powered Real-time Traffic Monitoring System
> **대규모 트래픽 처리를 위한 Kafka 기반 이벤트 기반 아키텍처(EDA) 학습 및 대시보드 구현**

## Project Goals
- **대규모 트래픽 시뮬레이션**: 초당 수천 건의 로그 생성 및 처리 경험.
- **Java 21 Virtual Threads**: 가상 스레드를 활용한 Producer/Consumer 성능 최적화.
- **실시간 데이터 파이프라인**: Kafka와 WebSocket을 연동한 실시간 대시보드 구축.
- **MSA 구조 이해**: 분산 커밋 로그 플랫폼으로서의 Kafka 핵심 개념 내재화.

---

## System Architecture
1. **Traffic Generator (Producer)**: Spring Boot 3.4 + Java 21 기반의 가상 로그 생성기.
2. **Message Broker (Kafka)**: 분산 메시징 시스템 (Docker 기반 환경).
3. **Dashboard Server (Consumer)**: 실시간 로그 소비 및 WebSocket을 통한 클라이언트 전송.
4. **Client (Web)**: 실시간 로그 시각화 대시보드.

---

## 핵심 개념 정리 (What I Learned)

### 1. Kafka: 단순 메시지 큐 그 이상
- 카프카는 단순한 전달 도구가 아닌 **'분산 커밋 로그'** 플랫폼입니다.
- MSA 환경에서 서비스 간 결합도를 낮추는 **'혈관'**이자 데이터를 안전하게 보관하는 **'심장'** 역할을 합니다.

### 2. Producer & Consumer
- **Producer**: 가상의 트래픽 로그를 생성하여 Kafka Topic으로 발행하는 주체입니다.
- **Consumer**: Topic에 쌓인 데이터를 실시간으로 읽어와 비즈니스 로직(저장, 전파)을 처리합니다.

### 3. Partition & Parallelism
- **Partition**: 토픽을 나누는 단위로, **병렬 처리의 핵심**입니다. 
- 파티션 개수를 조절함으로써 컨슈머의 처리량을 결정하며, 대규모 트래픽 상황에서 성능의 핵심 열쇠가 됩니다. (파티션 수 = 최대 병렬 처리 가능 컨슈머 수)

### 4. Consumer Group
- 여러 컨슈머가 하나의 그룹으로 묶여 토픽의 데이터를 나눠 처리합니다.
- 특정 컨슈머가 장애가 나더라도 그룹 내 다른 컨슈머가 작업을 이어받아 가용성을 보장합니다.

---

## Tech Stack
- **Language**: Java 21 (Virtual Threads 사용!)
- **Framework**: Spring Boot 3.4.x
- **Message Broker**: Apache Kafka
- **Infrastructure**: Docker, Docker Compose
- **Real-time**: Spring WebSocket (STOMP)
- **Libraries**: Java Faker (Data Gen), Lombok, Spring Data JPA

---

## Roadmap
- 프로젝트 구조 설계 및 초기 세팅 (Producer, Consumer)
- Docker-compose를 이용한 Kafka 환경 구축
- Java 21 Virtual Threads 기반 Traffic Generator 구현
- Kafka-WebSocket 연동 및 실시간 대시보드 구현
- ELK 스택(Logstash, Elasticsearch, Kibana)으로 확장하여 로그 분석 환경 구축
