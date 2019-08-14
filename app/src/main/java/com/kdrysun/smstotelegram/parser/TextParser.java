package com.kdrysun.smstotelegram.parser;

import android.icu.text.NumberFormat;
import com.kdrysun.smstotelegram.domain.Settlement;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextParser {

    String defaultPattern = "\\[Web발신\\][\\s\\r\\n]*(.*)";

    public TextDto dto;

    /**
     * 각 사용처별로 parsing 처리해서 dto 를 구성
     * @param originalMessage sms 원본 메세지
     * @return parsing 된 내용 dto
     */
    public TextDto parse(String originalMessage) {

        if (dto == null)
            throw new RuntimeException("직접 호출 금지");

        Pattern p = Pattern.compile(defaultPattern, Pattern.DOTALL);
        Matcher matcher = p.matcher(originalMessage);
        if (matcher.matches())
            dto.setPlace(matcher.group(1));
        else
            dto.setPlace(originalMessage);

        return dto;
    }

    /**
     * 카드금액 아래내용처럼 포맷팅
     *
     * [국민카드] 일시불 승인 5000원
     * 08/09 12:10 완이네 작은
     *
     * @param dto 문자정보
     * @return 포맷팅한 문자열
     */
    public String format(TextDto dto) {
        if (dto.isParsed()) {
            return new StringBuilder().append("[" + dto.getPaymentType().getName() + "]").
                    append(" ").append(StringUtils.defaultString(dto.getMethod())).
                    append(" ").append(StringUtils.defaultString(dto.getType())).
                    append(" ").append(NumberFormat.getInstance().format(dto.getPrice()) + "원\n").
                    append(StringUtils.defaultString(dto.getTime())).
                    append(" ").append(StringUtils.defaultString(dto.getPlace())).toString();
        } else {
            return dto.getPlace();
        }
    }

    /**
     * 사용금액 하단에 누적금액 노출
     *
     * 2019.08월 누적 금액
     * 국민카드: 80000 원
     * 신한카드: 00000 원
     * 현금: 00000 원
     *
     * @param currentMonth 현재월
     * @param settlements 현재월 누적금액
     * @return 포맷팅된 문자
     */
    public String formatAccum(String currentMonth, List<Settlement> settlements) {
        StringBuilder str = new StringBuilder();

        if (settlements == null || settlements.size() == 0)
            return "";

        str.append("\n\n").
                append(StringUtils.left(currentMonth, 4)).append(".").
                append(StringUtils.right(currentMonth, 2)).append("월").
                append(" 누적금액\n");

        long total = 0;
        for (Settlement stl : settlements) {
            str.append(stl.getType().getName()).append(": ").append(NumberFormat.getInstance().format(stl.getPrice())).append("원\n");
            total += stl.getPrice();
        }
        str.append("\n총합: ").append(NumberFormat.getInstance().format(total)).append("원");

        return str.toString();
    }
}
