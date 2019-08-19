package com.kdrysun.smstotelegram.domain.typeconverter;

import androidx.room.TypeConverter;
import com.kdrysun.smstotelegram.domain.PaymentType;

public class PaymentTypeConverter {

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

    @TypeConverter
    public PaymentType toCardType(String name) {
        for (PaymentType type : PaymentType.values())
            if (type.getName().equals(name))
                return type;

        return PaymentType.NONE;
    }
}
