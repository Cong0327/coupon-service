package com.example.demo.Repository;

import com.example.demo.Entity.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
}
