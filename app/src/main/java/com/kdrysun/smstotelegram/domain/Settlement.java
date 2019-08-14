package com.kdrysun.smstotelegram.domain;

import android.icu.text.NumberFormat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Entity
@Data
public class Settlement {

    public Settlement(String date, long price, PaymentType type) {
        this.date = date;
        this.price = price;
        this.type = type;
    }

    @PrimaryKey(autoGenerate = true)
    private int seq;

    @ColumnInfo
    private String date;

    @ColumnInfo
    private long price;

    @ColumnInfo
    private PaymentType type;

    @ColumnInfo
    private boolean isPaid = false;

    @Override
    public String toString() {
        return "결제월:" + date +
                ", [카드사:" + type + "]" +
                ", 금액: " + NumberFormat.getInstance().format(price) + "원 " + (isPaid ? "결제완료" : "미결제");
    }
}
