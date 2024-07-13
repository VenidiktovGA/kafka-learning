package ru.venidiktov.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.PathResource;
import org.springframework.util.ResourceUtils;

/**
 * Загружаем yml файл и из него достаем нужные свойства!
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KafkaProperties {
    private static final YamlPropertiesFactoryBean YAML_FACTORY = new YamlPropertiesFactoryBean();
    private static final String TOPIC = "kafka.topic";
    private static final String GROUP_ID = "kafka.group-id";
    private static Properties properties;

    static {
        loadKafkaProperties();
    }

    private static void loadKafkaProperties() {
        try {
            File file = ResourceUtils.getFile("classpath:application.yml");
            YAML_FACTORY.setResources(new PathResource(file.toPath()));
            YAML_FACTORY.afterPropertiesSet();
            properties = YAML_FACTORY.getObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Не найден файл application.yml");
        }
    }

    public static String topic() {
        return properties.getProperty(TOPIC);
    }

    public static String groupId() {
        return properties.getProperty(GROUP_ID);
    }
}
