package com.kdrysun.smstotelegram.domain;

import android.icu.text.NumberFormat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Entity
@Data
public class Settlement {

    @PrimaryKey(autoGenerate = true)
    private int seq;

    @ColumnInfo
    private String date;

    @ColumnInfo
    private Long price;

    @ColumnInfo
    private CardType type;

    @Override
    public String toString() {
        return "결제월:" + date +
                ", [카드사:" + type + "]" +
                ", 금액: " + NumberFormat.getInstance().format(price) + "원";
    }
}
