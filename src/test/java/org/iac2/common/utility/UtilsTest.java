package org.iac2.common.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilsTest {
    @Test
    void testExtractIp() {
        String url = "http://1.2.3.4:1234/gggg?afdas=1&abc=false";
        Assertions.assertEquals("1.2.3.4", Utils.extractHost(url));
        Assertions.assertEquals("1.2.3.4", Utils.extractHost("1.2.3.4"));
        Assertions.assertEquals("localhost", Utils.extractHost("localhost"));
        Assertions.assertNull(Utils.extractHost("hello kitty!"));
    }
}