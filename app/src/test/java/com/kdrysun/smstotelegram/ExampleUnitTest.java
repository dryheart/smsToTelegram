package com.kdrysun.smstotelegram;

import com.kdrysun.smstotelegram.parser.TextDto;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void regExpTest() {

        String[] kbcardPattern = {
                ".*(승인|취소)[\\s\\r\\n]*\\S+[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n(]+(\\S[^\\s\\r\\n)]+).[^\\d]*(\\d+.*:\\d+)[\\s\\r\\n]*(.+)[\\s\\r\\n]+누적([0-9,]+)원",
                ".*(승인|취소)[\\s\\r\\n]*\\S+[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n(]+(\\S[^\\s\\r\\n)]*)[\\s\\r\\n]*(.+)",
                ".[^\\d]*([\\d/]+)[\\s\\r\\n]*결제금액[\\s\\r\\n]+([\\d,]+)원.*"

        };

        String[] shinhanPattern = {
                ".*(승인|취소)[\\s\\r\\n]*\\S+[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n(]+(\\S[^\\s\\r\\n)]+).[^\\d]*(\\d+.*:\\d+)[\\s\\r\\n]*(.+)[\\s\\r\\n]+누적([0-9,]+)원",
                ".*(승인|취소)[\\s\\r\\n]*\\S+[\\s\\r\\n]*([\\S\\s]+)[\\s\\r\\n]([0-9,]+)원.*",
                ".[^\\d]*([\\d/]+)[\\s\\r\\n]*결제금액.*[\\s\\r\\n]+([\\d,]+)원.*"
        };

        String defaultPattern = "\\[Web발신\\][\\s\\r\\n]*(.*)";

        String[] cashPattern = {
                ".*KDB[\\S]*[\\s\\r\\n]*(.[^\\r\\n]*)[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n]*(.[^\\r\\n]+)[\\s\\r\\n]*잔액([0-9,]+)원[\\s\\r\\n]*(.*)",
                ".*전북[\\S]*[\\s\\r\\n]*(.[^\\r\\n\\d,]*)([0-9,]+)원.*잔액([0-9,]+)원[\\s\\r\\n]*([\\d\\s/:]+)[\\s\\r\\n](.*)"
        };

        String[] kbCardMsg = {
                "[Web발신]\nKB국민카드4*4*승인\n김*선\n5,000원 일시불\n08/13 10:03\n(주)티몬\n누적1,823,148원",
                "[Web발신]\nKB국민카드4*4*승인\n김*선\n20,020원 08/12\nSK텔레콤-자동납부",
                "[Web발신]\n[KB국민카드]김두선님 08/14 결제금액 1,510,490원. 잔여포인트리28,906(08/01기준)"
        };

        String[] cashMsg = {
                "[Web발신]\n(KDB)020****3636709\n출금\n100,000원\n윤혜령\n잔액30,075,963원 07:32:29",
                "[Web발신]\n[전북]####946\n출금25,800원\n잔액1,115,316원\n12/24 13:39\n모> 신한신헌섭",
                "[Web발신]\n[전북]####946\n입금200,000원\n잔액1,147,346원\n01/02 19:37\n산업김두선"
        };


        String[] shinhanMsg = {
                "[Web발신]\n신한카드(141) 승인 김*선 4,300원(일시불)08/10 20:52 GS25 강서안  누적805,315원",
                "[Web발신]\n신한카드(141) 취소 김*선 120,000원(일시불)08/07 20:01 주식회사명주 누적801,015원",
                "[Web발신]\n신한카드(141) 승인 김*선님 건강보험(지역)  191,308원 정상승인 되었습니다.",
                "[Web발신]\n신한카드(141) 승인 김*선님 국민연금(지역)  136,694원 정상승인 되었습니다.",
                "[Web발신] \n신한카드(141) 승인 김*선님 주택임대료  194,500원 정상승인 되었습니다.",
                "[Web발신] \n신한카드(141) 승인 김*선님 아파트 관리비  120,940원 정상승인 되었습니다.",
                "[Web발신]\n[신한카드]        김*선님 08/15결제금액(08/16기준)    723,065원(결제:국민은행)"
        };



        MSG: for (String test : kbCardMsg) {
            System.out.println(test);
            System.out.println("----------------------");

            PT: for (String pt : kbcardPattern) {
                Pattern p = Pattern.compile(pt, Pattern.DOTALL);
                Matcher matcher = p.matcher(test);

                TextDto dto = new TextDto();
                if (matcher.matches()) {

                    for (int i = 1; i <= matcher.groupCount(); i++)
                        System.out.println(i + ":" + matcher.group(i));
                    break PT;
                }
            }
            System.out.println("=========================");
        }
    }




}