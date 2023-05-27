package com.kdrysun.smstotelegram.parser;

import com.kdrysun.smstotelegram.domain.PaymentType;

public class TextParserBuilder {

    public static TextParser build(PaymentType paymentType) {

        switch (paymentType) {
            case KBCARD:
                return new KbCardParser();
            case SHINHAN:
                return new ShinhanCardParser();
            case CASH:
                return new CashParser();
            case LOTTE:
                return new LotteCardParser();
            case HYUNDAI:
                return new HyundaiCardParser();
        }

        return new TextParser() {
            @Override
            public TextDto parse(String originalMessage) {
                this.dto = new TextDto();
                return super.parse(originalMessage);
            }
        };
    }
}
