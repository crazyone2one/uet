package cn.master.uet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author jingll
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class UetApplication {

    public static void main(String[] args) {
        SpringApplication.run(UetApplication.class, args);
    }

}
