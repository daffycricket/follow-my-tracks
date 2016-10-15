package org.nla.followmytracks.core.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nla.followmytracks.BuildConfig;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class UtilsTest {

    @Test
    public void buildRandomName() throws Exception {
        final String name = Utils.buildRandomName();

        boolean nameStartsWithACorrectRoot = false;
        for (String root : Utils.POTENTIAL_NAMES) {
            if (name.startsWith(root)) {
                nameStartsWithACorrectRoot = true;
                break;
            }
        }

        assertThat(nameStartsWithACorrectRoot).isTrue();
    }

    @Test
    public void GetLogTag_WhenObjectIsNull() {
        String logTag = Utils.getLogTag(null);
        assertThat(logTag).isEqualTo(Utils.LOGTAG);
    }

    @Test
    public void GetLogTag_WhenObjectIsNotNull() {
        StringBuilder stringBuilder = new StringBuilder();
        String logTag = Utils.getLogTag(stringBuilder);
        assertThat(logTag).isEqualTo(Utils.LOGTAG + " " + stringBuilder.getClass().getSimpleName());
    }
}