package com.example.demo.Service;

import com.example.demo.Repository.CouponRedisRepository;
import com.example.demo.Repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueService2 {

    private final CouponRedisRepository couponRedisRepository;
    private final CouponRepository couponRepository;

    public void issue(Long couponId)
    {
        // Redis에서 수량을 1 증가시킴
        Long count = couponRedisRepository.increment(couponId);

        //100개 넘어가면 즉시 리턴
        if(count > 100)
        {
            return;
        }

        // 100등 안에든 요청만 실제 DB수량 변경
        // 이때 DB는 단ㄴ순 +1만 하면됨
        var coupon =couponRepository.findById(couponId).orElseThrow();
        coupon.issue();
        couponRepository.save(coupon);

    }
}
