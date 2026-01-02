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
//        Coupon coupon = couponRepository.findById(couponId)
//                .orElseThrow( ()-> new IllegalArgumentException("쿠폰 존재하지 않음"));

        //일반 findById 대신 락이 걸린 조회 메서드 사용
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow( ()-> new IllegalArgumentException("쿠폰 존재하지 않음"));

        //트랜잭션이 끝나기 까지 다른 스레드는 이 로직에 접근 불가
        if(coupon.getIssuedQuantity() < coupon.getTotalQuantity())
        {
            // 강제 지연을 그대로 두어도 락 때문에 숫자가 꼬이지 않습니다.
            try{
                Thread.sleep(10);
            }catch(InterruptedException e){}
        }
        coupon.issue();     //현재 쿠폰수량 증가
        couponRepository.saveAndFlush(coupon); // 변경 사항 즉시 반영

    }

}
