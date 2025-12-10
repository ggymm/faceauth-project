package com.faceauth;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Slf4j
@SpringBootApplication
public class App implements ApplicationListener<ApplicationStartedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @Override
    public void onApplicationEvent(final @NonNull ApplicationStartedEvent event) {
        try {
            final File file = new File("schema.sql");
            final String schema = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            // 执行数据库初始化
            for (String init : StringUtils.split(schema, ';')) {
                if (StringUtils.isNotBlank(init)) {
                    SqlRunner.db().update(init);
                }
            }
            log.info("Database structure initialization completed");
        } catch (Exception e) {
            log.error("Database structure initialization failed", e);
            System.exit(1);
        }
    }
}
