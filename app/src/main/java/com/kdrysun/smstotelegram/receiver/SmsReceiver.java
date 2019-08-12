package com.kdrysun.smstotelegram.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import androidx.room.Room;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.domain.PaymentType;
import com.kdrysun.smstotelegram.domain.Settlement;
import com.kdrysun.smstotelegram.domain.Sms;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
                        DateFormatUtils.format(new Date(), "yyyyMMddhhmmss")));

            new Thread(() -> {
                SmsDatabase db = Room.databaseBuilder(context, SmsDatabase.class, "sms.db").build();

                Date date = new Date();
                String currentMonth = DateFormatUtils.format(date, "yyyyMM");
                String prevMonth = DateFormatUtils.format(DateUtils.addMonths(date, -1), "yyyyMM");

                Log.d("@@@@@@@@@@@@@@@@@@@@ current Month", currentMonth);
                Log.d("@@@@@@@@@@@@@@@@@@@@ prev Month", prevMonth);

                try {

                    for (Sms sms : smsList) {
                        PaymentType paymentType = db.cardDao().findCardType(sms.getNumber());
                        if (paymentType != null) {

                            TextDto dto = TextParse.parse(paymentType, sms.getMessage());
                            String telegramMsg = TextParse.format(dto);
                            if (dto.isParsed() && dto.getAccum() > 0) {
                                // 누적금액 계산
                                Settlement prevSettle = db.settlementDao().findSettlementByDateAndCardType(paymentType, prevMonth);
                                long prevPrice = prevSettle != null ? prevSettle.getPrice() : 0;

                                Settlement settle = new Settlement(currentMonth,
                                        dto.getAccum() - prevPrice,
                                        paymentType);

                                Settlement currentSettle = db.settlementDao().findSettlementByDateAndCardType(paymentType, currentMonth);

                                if (currentSettle == null)
                                    db.settlementDao().insertAll(settle);
                                else
                                    db.settlementDao().updateByDateAndCardType(settle.getPrice(), settle.getType(), settle.getDate());
                            }

                            List<Settlement> settlements = db.settlementDao().findSettlementByDate(currentMonth);
                            telegramMsg = TextParse.formatAccum(telegramMsg, settlements);

                            // 선언된 번호만 입력
                            sms.setMessage(telegramMsg);
                            db.smsDao().insertAll(smsList.stream().toArray(Sms[]::new));

                            TelegramBot bot = new TelegramBot("917964506:AAFmTrHNH6AX7ViqxI0zWFRERSCnjY1-tWI");
                            bot.execute(new SendMessage(-1001333464175L, telegramMsg));
                        }
                    }
                } catch (Exception e) {
                    Sms sms = new Sms("0000", e.getMessage(), DateFormatUtils.format(new Date(), "yyyyMMddhhmmss"));
                    db.smsDao().insertAll(sms);
                }
            }).start();
        }
    }
}