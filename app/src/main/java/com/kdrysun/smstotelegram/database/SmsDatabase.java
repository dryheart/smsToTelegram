package com.kdrysun.smstotelegram.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.kdrysun.smstotelegram.dao.CardDao;
import com.kdrysun.smstotelegram.dao.SettlementDao;
import com.kdrysun.smstotelegram.domain.Card;
import com.kdrysun.smstotelegram.domain.PaymentType;
import com.kdrysun.smstotelegram.domain.Settlement;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.dao.SmsDao;
import com.kdrysun.smstotelegram.domain.typeconverter.CardTypeConverter;

@Database(entities = {Sms.class, Settlement.class, Card.class}, version = 1)
@TypeConverters(CardTypeConverter.class)
public abstract class SmsDatabase extends RoomDatabase {
    public abstract SmsDao smsDao();

    public abstract SettlementDao settlementDao();

    public abstract CardDao cardDao();

    private static SmsDatabase database;

    public static SmsDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, SmsDatabase.class, "sms.db").addCallback(new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    new Thread(() -> {
                        getInstance(context).cardDao().insertAll(
                                new Card("15881688", PaymentType.KBCARD),
                                new Card("18990800", PaymentType.KBCARD),
                                new Card("15447200", PaymentType.SHINHAN),
                                new Card("15447000", PaymentType.SHINHAN)
                        );

                        getInstance(context).settlementDao().insertAll(
                                new Settlement("201907", 1510490L, PaymentType.KBCARD),
                                new Settlement("201907", 723065L, PaymentType.SHINHAN)
                        );
                    }).start();
                }
            }).build();
        }

        return database;
    }
}
