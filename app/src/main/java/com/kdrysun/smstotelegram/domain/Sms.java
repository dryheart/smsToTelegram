package com.kdrysun.smstotelegram.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Entity
@Data
public class Sms {
    public Sms(String number, String message, String date) {
        this.number = number;
        this.message = message;
        this.date = date;
    }

    @PrimaryKey(autoGenerate = true)
    private int seq;

    @ColumnInfo
    private String number;

    @ColumnInfo
    private String message;

    @ColumnInfo
    private String date;
}
