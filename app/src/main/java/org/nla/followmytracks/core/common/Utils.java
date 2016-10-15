package org.nla.followmytracks.core.common;

import android.support.annotation.VisibleForTesting;

import java.util.Random;

public class Utils {

    public static final String LOGTAG = "Follow-My-Track";

    @VisibleForTesting
    static final String[] POTENTIAL_NAMES = new String[]{
            "Mizuno",
            "Brooks",
            "Nike",
            "Adadas",
            "Asics",
            "Diadora",
            "Saucony"
    };

    private Utils() {
    }

    public static String buildRandomName() {

        Random random = new Random();
        return POTENTIAL_NAMES[random.nextInt(POTENTIAL_NAMES.length)] + " " + random.nextInt(500);
    }

    public static String getLogTag(Object o) {
        return LOGTAG + (o == null ? "" : " " + o.getClass().getSimpleName());
    }
}
