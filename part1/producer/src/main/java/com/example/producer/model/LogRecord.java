package com.example.producer.model;

public record LogRecord(
        String userId,
        Action action, // ("CLICK", "PURCHASE", "LOGIN", "VIEW")
        String pageUrl,
        String ipAddress,
        long timeStamp
) {
}

/**
 * Class vs Record
 * Class :
 *  - 데이터 + 상태를 변경하는 로직 중심
 *  - setter 를 통해 값 변경 가능 (가변)
 *  - getter, equals, hashCode 직접 구현 필요
 *  - 비교적 무거움?
 *
 * Record :
 *  - 순수하게 데이터 그 자체를 전달하는 목적
 *  - 모든 필드가 final, 생성 후 변경 불가
 *  - 선언 한 줄로 위 모든 메서드가 자동 생성
 *  - 최적화되어 있어 데이터 전달용으로 가볍고 빠름
 *
 *  그럼 카프카에서 Record를 쓰는 이유
 *  - 카프카 메시지는 한 번 발송되면 중간에 값이 바뀌면 안됨
 *      -> 모든 필드가 final 로 되어 있어서 생성 후 바뀌 지않음
 *         즉, 데이터 신뢰성이 높고, 코드가 짧아 가독성이 좋음
 */