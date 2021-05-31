package cn.master.uet.commom;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author by 11's papa on 2021年05月28日
 * @version 1.0.0
 */
@Configuration
public class TestLinkApiConfig {

    private final TestLinkProperties properties;

    @Autowired
    public TestLinkApiConfig(TestLinkProperties testLinkProperties) {
        this.properties = testLinkProperties;
    }

    @Bean
    public TestLinkAPI api() {
        TestLinkAPI api = null;
        URL url = null;
        try {
            url = new URL(properties.getUrl());
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
        try {
            api = new TestLinkAPI(url, properties.getKey());
        } catch (TestLinkAPIException e) {
            e.printStackTrace();
        }
        return api;
    }
}
