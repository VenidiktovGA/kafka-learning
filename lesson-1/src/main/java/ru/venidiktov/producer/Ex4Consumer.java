package ru.venidiktov.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import ru.venidiktov.utils.Util;

import java.time.Duration;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Consumer в момент запуска запрашивает топики из которых ему читать и его группу
 */
@Slf4j
public class Ex4Consumer {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.print("Enter topics names separated by commas: ");
        String topics = scanner.nextLine();
        System.out.print("Enter group-id: ");
        String groupId = scanner.nextLine();
        log.warn("Subscribe to topic {} with group-id {}", topics, groupId);

        /**
         * Важно использовать try() с ресурсами, или закрывать Consumer'а так как при падении
         * приложения или Consumer'а kafka не сразу об этом узнает, kafka будет ждать некоторое время и
         * опрашивать Consumer'а на то что жив он или нет
         * После того как kafka поймет что Consumer умер kafka начнет перебалансировку и отдаст партицию мертвого
         * Consumer'а живому и он их прочитает!!! На практике перебалансировка при смерти одного из консумеров класса
         * Ex4Consumer произошла через 30 секунд!!!
         */
        try (var consumer = new KafkaConsumer<String, String>(Util.createConsumerConfig(m ->
                m.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)))) {

            consumer.subscribe(Arrays.stream(topics.split(",")).map(String::trim).toList());

            while (true) {
                /**
                 * Метод poll() блокирующий работает как long poll, то есть при вызове метода poll()
                 * процесс идет в kafka и смотрит есть ли там готовый batch для получений, если он есть то
                 * batch отправляется в Consumer если готового batch'а нет то мы ждем указанное количество времени
                 * переданное в качестве аргумента в метод poll(Duration.ofSeconds(10)) - ждем 10 секунд
                 */
                var result = consumer.poll(Duration.ofSeconds(10));

                log.info("Read {}", result.count());

                for (var record : result) {
                    log.warn("Message from {}. Partition {} key:{} -> value:{}", record.topic(), record.partition(),
                            record.key(), record.value());
                }
            }
        }
    }
}
