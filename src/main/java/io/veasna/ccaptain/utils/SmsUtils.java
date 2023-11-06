package io.veasna.ccaptain.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 6/11/23 15:08
 */
public class SmsUtils {
    public static final String FROM_NUMBER = "+16419436230";
    public static final String SID_KEY = "AC25f4def3de06128ec66d34d168b94a89";
    public static final String TOKEN_KEY = "09281f2a9ab98a3f2d7c5b14b5cf4f5c";

    public static void sendSMS(String to, String messageBody){
        Twilio.init(SID_KEY,TOKEN_KEY);
        Message message = creator(new PhoneNumber("" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }

}
