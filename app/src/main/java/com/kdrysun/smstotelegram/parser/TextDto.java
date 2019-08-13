package com.kdrysun.smstotelegram.parser;

import com.kdrysun.smstotelegram.domain.PaymentType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

@Data
public class TextDto {

    /** 처리 여부 */
    private boolean isParsed;

    /** 카드 구분 */
    private PaymentType paymentType;

    /** 승인 구분 */
    private String type;

    /** 사용금액 */
    private long price;

    /** 결제 구분 */
    private String method;

    /** 시간 */
    private String time;

    /** 사용처 */
    private String place;

    /** 누적 */
    private long accum;

    /** 잔액 */
    private long balance;

    /** 카드사별 누적금액 노출 */
    private Map<PaymentType, Long> accumPrice;

    public void setPrice(String price) {
        this.price = NumberUtils.toLong(StringUtils.replace(price, ",", ""));
    }

    public void setAccum(String accum) {
        this.accum = NumberUtils.toLong(StringUtils.replace(accum, ",", ""));
    }

    public void setBalance(String balance) {
        this.balance = NumberUtils.toLong(StringUtils.replace(balance, ",", ""));
    }

    public boolean isAccum() {
        return !"입금".equals(type) ||
                !(PaymentType.CASH.equals(paymentType) && "카드금액".equals(place));
    }

    public boolean isAccumCalc() {
        return this.isParsed && this.isAccum();
    }
}
