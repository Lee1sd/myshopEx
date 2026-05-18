package com.example.myShop.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ItemDto {

    private String itemName;        // 상품 이름
    private String itemDetail;     // 상품 설명
    private int price;             // 가격
    private LocalDateTime regTime; // 등록 시간
}
