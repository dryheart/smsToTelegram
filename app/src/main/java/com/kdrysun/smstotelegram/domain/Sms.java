package com.kdrysun.smstotelegram.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

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

    @SneakyThrows
    @Override
    public String toString() {
        return "[" + DateFormatUtils.format(DateUtils.parseDate(date, "yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss") + "] " +
                seq + " " + number + "\n" +
                message;
    }
}
