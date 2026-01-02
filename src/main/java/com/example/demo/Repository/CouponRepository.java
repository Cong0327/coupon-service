package com.example.demo.Repository;

import com.example.demo.Entity.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    //락을 걸고 데이터 조회
    // PESSIMISTIC_WRITE는 다른 트랜잭션이 읽기/쓰기를 모두 못하게 막습니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :id")
    Optional<Coupon> findByIdWithLock(Long id);

}
