package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Long totalQuantity;         //전체 발급 가능 수량
    private Long issuedQuantity;        //현재 발급 수량

    public Coupon(String title, Long totalQuantity)
    {
        this.title = title;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = 0L;
    }
    //현재발급수량 +1
    public void issue()
    {
        if (this.issuedQuantity >= this.totalQuantity) {
            throw new RuntimeException("잔여 수량이 없습니다.");
        }
        this.issuedQuantity++;
    }
}
