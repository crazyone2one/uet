package cn.master.uet;

import cn.master.uet.commom.TestLinkApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UetApplicationTests {

    @Autowired
    private TestLinkApiConfig apiConfig;

    @Test
    void tc01() {
        System.out.println(apiConfig.api().ping());
    }

}
