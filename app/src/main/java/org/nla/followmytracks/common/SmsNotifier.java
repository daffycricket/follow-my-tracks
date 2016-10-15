package org.nla.followmytracks.common;

import android.content.Context;
import android.telephony.SmsManager;

import org.nla.followmytracks.R;

import javax.inject.Singleton;

@Singleton
public class SmsNotifier {

    private final Context context;

    public SmsNotifier(final Context context) {
        this.context = context;
    }

    public void notify(
            String phoneNumber,
            boolean isStartSms,
            String distance,
            String remainingTime,
            String origin
    ) {
        String message = buildMessage(isStartSms, distance, remainingTime, origin);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private String buildMessage(
            boolean isStartSms,
            String distance,
            String remainingTime,
            String origin
    ) {
        String message;
        if (isStartSms) {
            message = buildStartMessage(distance, remainingTime, origin);
        } else {
            message = buildUpdateMessage(distance, remainingTime, origin);
        }

        return message;
    }

    private String buildStartMessage(String distance, String remainingTime, String origin) {
        return context.getString(R.string.sms_start_distance_interval,
                                 distance,
                                 remainingTime,
                                 origin);
    }

    private String buildUpdateMessage(String distance, String remainingTime, String origin) {
        return context.getString(R.string.sms_update_distance_interval,
                                 distance,
                                 remainingTime,
                                 origin);
    }
}