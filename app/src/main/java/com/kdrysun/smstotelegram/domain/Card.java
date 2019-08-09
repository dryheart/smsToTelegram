package com.kdrysun.smstotelegram.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

@Entity
@Data
public class Card {
    public Card(String phoneNumber, CardType type) {
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    @PrimaryKey
    @NonNull
    private String phoneNumber;

    @ColumnInfo
    private CardType type;
}
