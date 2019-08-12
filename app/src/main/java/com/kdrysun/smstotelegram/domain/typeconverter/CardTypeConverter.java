package com.kdrysun.smstotelegram.domain.typeconverter;

import androidx.room.TypeConverter;
import com.kdrysun.smstotelegram.domain.PaymentType;

public class CardTypeConverter {

    @TypeConverter
    public int fromCardType(PaymentType type) {
        return type.getId();
    }

    @TypeConverter
    public PaymentType toCardType(int id) {
        for (PaymentType type : PaymentType.values())
            if (id == type.getId())
                return type;

        return PaymentType.NONE;
    }
}
