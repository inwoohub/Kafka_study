package com.example.producer.service;


import com.example.producer.model.Action;
import com.example.producer.model.LogRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Slf4j - Lombok 이 제공하는 어노테이션임
 * 이 어노테이션을 붙임으로써 logging.info 가 사용 가능
 * 그렇다면 System.out.println vs logging.info ??
 * <p>
 * System.out.println :
 * - 무조건 표준 출력 (콘솔) 에만 찍힘, 나중에 파일로 저장이 불가능함, 필터링도 불가능함,
 * - 그리고 성능이 안 좋음 많이 무거움 그래서 실무 절대 사용x
 * <p>
 * log.info :
 * - 콘솔에 찍힘, 그리고 파일로 저장이 가능함
 * - 로깅 레벨에 따라서 로깅 컨트롤이 가능
 * ex) TRACE, DEBUG, INFO, WARN, ERROR, FATAL
 * <p>
 * 어노테이션을 붙임으로써 아래에 코드가 백그라운드에서 자동으로 생성되어 동작함
 * private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TrafficService.class)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficService {

    // 스프링 카프카(Spring for Apache Kafka) 환경에서 프로듀서(Producer)가 카프카 토픽으로 데이터를 쉽게 전송할 수 있도록 지원하는 핵심 추상화 클래스
    // 내부적으로 Kafka Producer를 래핑(Wrapping)하여 thread-safe한 방식으로 메시지 전송, 비동기 콜백 처리 등 복잡한 과정을 단순화
    private final KafkaTemplate<String, String> kafkaTemplate;

    // ObjectMapper 는 Jackson 라이브러리이며, Java객체 -> JSON 변환, JSON -> Java객체로 역직렬화 시켜주는 매퍼
    private final ObjectMapper objectMapper;

    // 그럴사한 가짜 데이터 생성
    private final Faker faker = new Faker();

    @Scheduled(fixedRate = 1000) // ms 단위로 1초마다 실행하는 스케줄러 어노테이션
    public void sendLog() {

        try {
            // enum 타입으로 만든 Action 랜덤으로 하나 뽑기
            Action randomAction = faker.options().option(Action.class);

            // 1. 가짜 데이터 생성
            LogRecord record = new LogRecord(
                    faker.name().username(),
                    randomAction,
                    faker.internet().url(),
                    faker.internet().ipV4Address(),
                    System.currentTimeMillis()
            );

            // 2. record -> JSON 문자열 변환 (직렬화)
            String message = objectMapper.writeValueAsString(record); // 직렬화 실패시 JsonProcessingException 에러 발생함.

            // 3. message를 kafka 로 전송
            kafkaTemplate.send("user-logs", message); // Topic : 데이터가 저장되는 카테고리를 user-logs 로 지정함
            // Topic 을 또 여러개로 나누면 Partition 이 됨 -> Partition 개수가 나중에는 성능으로 직결타한다함.

            // 4. @Slf4j 으로 로그 남기기
            log.info("메시지 전송 성공 {}", message);
            /**
             * 여기서 자바 로깅에서 유명한 주제 !
             * log.info("메시지 전송 성공 "+message) vs log.info("메시지 전송 성공 {}",message)
             * 결론 부터는 {} 를 사용하는게 성능상 유리하다고함. (조건적으로)
             * 왜? "" + message 는 StringBuilder 로 메시지를 합치고 나서 info() 로 전달하는 과정
             * "{}",message 는 문자열을 나중에 만듦 즉, 패턴 "{}"과 message 객체를 그대로 전달 (합치지 않음)
             * 로깅 프레임워크가 "로그 레벨 체크" → INFO 안 찍으면 즉시 return
             * 찍어야 할 때만 그 시점에 {} 자리에 message.toString() 끼워넣음
             *
             * 결론 :
             *  로그를 둘 다 출력해야하는 경우에는 둘다 toString() 을 거치기 때문에 성능상 차이가 업승ㅁ
             *  다만, log.debug 같이 디버그가 꺼져있는 경우라면, toString() 자체를 호출하지 않기 떄문에 DEBUG 로그 가 많을 수록 "{}",message 조합이 훨씬 유리함.
             */
        } catch (JsonProcessingException e) {
            log.error("메시지 변환 에러!", e);
        }
    }

}
