package com.example.demo.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.redis.core.StringRedisTemplate;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {
    // String 데이터 타입을 다루기 위한 스프링 제공 템플릿
    private final StringRedisTemplate redisTemplate;

    /**
     * redis의 INCR 연산은 원자적임,
     * 여러 스레드가 동시에 요청해도 Redis는 하나씩 순차처리하기에 중복 숫자가 나타나지 X
     */

    public Long increment(Long couponId)
    {
        return redisTemplate
                .opsForValue()
                .increment("coupon_count:"+couponId);
    }
}
