package com.example.demo;

import com.example.demo.Entity.Coupon;
import com.example.demo.Repository.CouponRepository;
import com.example.demo.Service.CouponIssueService;
import com.example.demo.Service.CouponIssueService2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest

 class CouponServiceTest {
//    @Autowired
//    private CouponIssueService couponIssueService;
    @Autowired
    private CouponIssueService2 couponIssueService;
    @Autowired
    private CouponRepository couponRepository;


    @Test
    void multiple_coupon_test() throws InterruptedException{
        // 1. 테스트 기초 데이터 준비
        Coupon coupon = couponRepository.save(new Coupon("선착순 이벤트", 100L));
        Long couponId = coupon.getId();

        // 2. 동시성 테스트를 위한 설정
        int threadCount = 1000; // 동시에 요청을 보낼 총 가상 사용자 수

        /**
         * [ExecutorService]
         * 왜 사용했나: Java에서 스레드를 직접 생성(new Thread())하면 비용이 많이 듭니다.
         * 역할: 미리 스레드들을 만들어두고 관리하는 '스레드 풀'입니다.
         * 여기서는 32개의 스레드를 미리 만들어놓고 1000개의 작업을 돌아가며 처리하게 합니다.
         */
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        /**
         * [CountDownLatch]
         * 왜 사용했나: 여러 스레드가 동시에 작업을 시작하거나, 모든 작업이 끝날 때까지 메인 스레드를 기다리게 할 때 사용합니다.
         * 역할: 일종의 '결승선'입니다. 숫자를 1000으로 설정하고, 작업이 끝날 때마다 숫자를 하나씩 줄여서(countDown) 0이 되면 대기(await)를 해제합니다.
         */
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 3. 비동기 작업 시작
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 실제 쿠폰 발급 로직 호출
//                    couponIssueService.issue(couponId);
                    couponIssueService.issue(couponId);
                } catch (Exception e) {
                    // 에러가 나더라도 latch는 줄여야 하므로 catch문을 두거나 로그를 남깁니다.
                } finally {
                    /**
                     * .countDown(): 래치(Latch) 숫자를 1 감소시킵니다.
                     * 이 호출이 1000번 일어나야 아래의 latch.await()이 풀립니다.
                     */
                    latch.countDown();
                }
            });
        }

        /**
         * .await(): 모든 스레드 작업이 끝날 때까지 현재(메인) 스레드를 일시 정지시킵니다.
         * 이게 없으면 쿠폰 발급이 다 끝나기도 전에 아래의 println이 먼저 실행되어버립니다.
         */
        latch.await();

        // 4. 결과 검증
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        System.out.println("발급된 쿠폰 수: " + updatedCoupon.getIssuedQuantity());
    }
}
