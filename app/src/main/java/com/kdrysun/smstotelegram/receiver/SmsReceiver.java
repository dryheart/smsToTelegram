package com.kdrysun.smstotelegram.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.format.DateUtils;
import android.util.Log;
import androidx.room.Room;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.database.SmsDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private final String TAG = "SMS_Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Log.d(TAG, "onReceiver() 호출");

            SmsMessage[] messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent);

            List<Sms> smsList = new ArrayList<>();
            for (SmsMessage message : messagesFromIntent)
                smsList.add(new Sms(message.getOriginatingAddress(),
                        message.getMessageBody(),
                        new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())));

            new Thread(() -> {
                SmsDatabase smsDatabase = Room.databaseBuilder(context, SmsDatabase.class, "sms.db").build();
                smsDatabase.smsDao().insertAll(smsList.stream().toArray(Sms[]::new));
            }).start();
        }
    }
}