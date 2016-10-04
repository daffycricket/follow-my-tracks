package org.nla.followmytracks.core.common;

import android.location.Address;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    public static final String LOGTAG = "Follow-My-Track";

    private Utils() {
    }

    public static String buildRandomName() {
        final String[] POTENTIAL_NAMES = new String[] { "Mizuno", "Brooks", "Nike", "Adadas", "Asics", "Diadora", "Saucony" };

        Random random = new Random();
        return POTENTIAL_NAMES[random.nextInt(POTENTIAL_NAMES.length)] + " " + random.nextInt(500);
    }

    public static String getLogTag(Object o) {
        return LOGTAG + (o == null ? null : o.getClass().getSimpleName());
    }

    public static String transformAddressToSingleLine(Address address) {
        List<String> addressFragments = new ArrayList<>();
        for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        return TextUtils.join(System.getProperty("line.separator"), addressFragments);
    }
}
