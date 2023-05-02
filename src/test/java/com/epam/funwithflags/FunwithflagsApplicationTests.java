package com.epam.funwithflags;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootTest(properties = {
        "data.cache.enabled=false"
})
class FunwithflagsApplicationTests {

    @Test
    void contextLoads() {
    }

}
