package com.kdrysun.smstotelegram.database;

import com.kdrysun.smstotelegram.domain.Settlement;
import com.kdrysun.smstotelegram.parser.TextDto;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class AccumulatePrice {


    public static void calculate(SmsDatabase db, TextDto dto) {

        Date date = new Date();
        String currentMonth = DateFormatUtils.format(date, "yyyyMM");
        String prevMonth = DateFormatUtils.format(DateUtils.addMonths(date, -1), "yyyyMM");

        /*
        1. 누적금액 있을경우 누적금액에서 이전달 결제 금액 차감
        2. 누적금액이 없을 경우 현재결제 금액에 사용금액 증감
        3. 월 결제완료되었을 경우 누적금액이 이번달 결제 금액
         */
        Settlement prevSettle = db.settlementDao().findSettlementByDateAndCardType(dto.getPaymentType(), prevMonth);
        Settlement currentSettle = db.settlementDao().findSettlementByDateAndCardType(dto.getPaymentType(), currentMonth);

        long accumPrice = 0;

        if (prevSettle == null) {
            // 새로운 월이 시작되어서 아직 결제금액이 작성되지 않을 상태일 경우
            accumPrice = currentSettle == null ? dto.getPrice() :
                    currentSettle.getPrice() + dto.getPrice();

        } else if (prevSettle.isPaid()) {
            // 이미 결제 완료된 경우
            accumPrice = dto.getAccum() > 0 ? dto.getAccum() :
                    currentSettle == null ? dto.getPrice() : currentSettle.getPrice() + dto.getPrice();

        } else {
            // 결제금액이 나온상태에서 아직 결제가 안된 상태
            if (dto.getAccum() > 0) {
                if (prevSettle.getPrice() > dto.getAccum()) {

                    // 이전달 결제완료 처리
                    db.settlementDao().updatePaidByDateAndCardType(prevSettle.getType(), prevSettle.getDate(), true);

                    accumPrice = dto.getAccum();
                } else {
                    accumPrice = dto.getAccum() - prevSettle.getPrice();
                }
            } else {
                accumPrice = currentSettle == null ? dto.getPrice() :
                        currentSettle.getPrice() + dto.getPrice();
            }
        }

        if (currentSettle == null)
            db.settlementDao().insertAll(new Settlement(currentMonth, accumPrice, dto.getPaymentType()));
        else
            db.settlementDao().updatePriceByDateAndCardType(accumPrice, currentSettle.getType(), currentSettle.getDate());
    }
}
