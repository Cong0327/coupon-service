package com.example.demo.Service;

import com.example.demo.Entity.Coupon;
import com.example.demo.Repository.CouponIssueRepository;
import com.example.demo.Repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(Long couponId)
    {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow( ()-> new IllegalArgumentException("쿠폰 존재하지 않음"));

        coupon.issue();     //현재 쿠폰수량 증가
        couponRepository.saveAndFlush(coupon); // 변경 사항 즉시 반영

    }

}
