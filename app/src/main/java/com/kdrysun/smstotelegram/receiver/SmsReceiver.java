package com.kdrysun.smstotelegram.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.parser.SmsParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

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

            String phoneNumber = "";
            String msg = "";
            List<Sms> smsList = new ArrayList<>();

            for (SmsMessage message : messagesFromIntent) {
                if (StringUtils.isNotBlank(phoneNumber) &&
                        !StringUtils.equals(phoneNumber, message.getOriginatingAddress())) {

                    smsList.add(new Sms(phoneNumber, msg,
                            DateFormatUtils.format(new Date(), "yyyyMMddhhmmss")));
                    msg = "";
                }

                phoneNumber = message.getOriginatingAddress();
                msg += message.getMessageBody();
            }

            smsList.add(new Sms(phoneNumber, msg,
                    DateFormatUtils.format(new Date(), "yyyyMMddhhmmss")));

            new Thread(() -> new SmsParser().parse(context, smsList)).start();
        }
    }
}