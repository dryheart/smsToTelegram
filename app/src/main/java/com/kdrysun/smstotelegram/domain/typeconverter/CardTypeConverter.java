package com.kdrysun.smstotelegram.domain.typeconverter;

import androidx.room.TypeConverter;
import com.kdrysun.smstotelegram.domain.CardType;

public class CardTypeConverter {

    @TypeConverter
    public String fromCardType(CardType type) {
        return type.name();
    }

    @TypeConverter
    public CardType toCardType(String type) {
        return CardType.valueOf(type);
    }
}
