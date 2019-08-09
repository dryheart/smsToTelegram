package com.kdrysun.smstotelegram.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.kdrysun.smstotelegram.dao.CardDao;
import com.kdrysun.smstotelegram.dao.SettlementDao;
import com.kdrysun.smstotelegram.domain.Card;
import com.kdrysun.smstotelegram.domain.Settlement;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.dao.SmsDao;
import com.kdrysun.smstotelegram.domain.typeconverter.CardTypeConverter;

@Database(entities = {Sms.class, Settlement.class, Card.class}, version = 2)
@TypeConverters(CardTypeConverter.class)
public abstract class SmsDatabase extends RoomDatabase {
    public abstract SmsDao smsDao();

    public abstract SettlementDao settlementDao();

    public abstract CardDao cardDao();

    private static SmsDatabase database;

    public static SmsDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, SmsDatabase.class, "sms.db").build();
        }

        return database;
    }
}
