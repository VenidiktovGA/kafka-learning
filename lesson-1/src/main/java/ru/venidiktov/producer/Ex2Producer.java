package ru.venidiktov.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import ru.venidiktov.utils.KafkaProperties;
import ru.venidiktov.utils.Util;

@Slf4j
public class Ex2Producer {
    private static final String topic = KafkaProperties.topic();

    public static void main(String[] args) {
        /**
         * При сериализации ключей не в Stirng а во все что Numeric kafka с данными сериализаторами плохо,
         * работает и в программах администрирования kafka эти ключи показываются не числами!
         * Столкнулся с такой проблемой и в продакшене но пока не копал!
         */
        try (var producer = new KafkaProducer<Integer, String>(Util.createProducerConfig(m ->
                m.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class)
        ))) {
            /**
             * В данном случае в консоли мы сначало увидим лог Send 0 до Send 199, это момент вызова метода send() но не фактической отправки
             * после вызова метода send() и перед фактической отправкой java закрывает producer забирает у него ресурсы
             * а только потом произойдет фактическая отправка, и будет напечатаны логи из callBack из метода send()
             *
             * !!! Объем batch'а или времени достаточного для отправки batch'а определяется параметрами конфигурации,
             * которые можно задать !!!
             */
            for (int i = 0; i < 200; i++) {
                log.info("Send {}", i);
                String value = "|%s| payload = %s".formatted(Ex2Producer.class.getName(), i);

                var record = new ProducerRecord<>(topic, i, value);
                /**
                 * Тут мы используем метод send в который передаем callBack который вызовется в момент отправки,
                 * из metadata можно достать номер партиции, топик, timestamp
                 */
                producer.send(record,
                        (metadata, error) -> { // callBck отработает когда произойдет отправка или exception
                            log.info("Sending record {}", record);
                            log.info("Complete {}", record.key());
                            log.info("Metadata: offset = {}, timestamp = {}, partition = {}",
                                    metadata.offset(), metadata.timestamp(), metadata.partition());
                        });
            }
        }
    }
}
