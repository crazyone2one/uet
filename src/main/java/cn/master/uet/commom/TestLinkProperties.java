package cn.master.uet.commom;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author by 11's papa on 2021年05月28日
 * @version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "testlink")
public class TestLinkProperties {
    private String url;
    private String key;
}
