package com.kdrysun.smstotelegram.domain;

import lombok.Getter;

@Getter
public enum PaymentType {
    KBCARD(1, "국민카드"),
    SHINHAN(2, "신한카드"),
    CASH(3, "현금"),
    NONE(4, "없음"),
    LOTTE(5, "롯데카드");

    PaymentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
