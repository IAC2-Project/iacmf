package org.iac2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.main.lazy-initialization=true",
        classes = {IACMFApplicationTest.class})
class IACMFApplicationTest {

    @Test
    void contextLoads() {
    }
}
