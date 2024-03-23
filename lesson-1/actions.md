Запуск контейнера

```shell
cd kafka
docker compose up -d
```

Получить список топиков

```shell
docker exec -ti kafka-learning-kafka /usr/bin/kafka-topics --list --bootstrap-server localhost:9091
```

Отправить сообщение

```shell
docker exec -ti kafka-learning-kafka /usr/bin/kafka-console-producer --topic topic-1 --bootstrap-server localhost:9091
```

Каждая строка - одно сообщение. Прервать - Ctrl+Z

Получить сообщения

```shell
docker exec -ti kafka-learning-kafka /usr/bin/kafka-console-consumer --from-beginning --topic topic-1 --bootstrap-server localhost:9091 
```

Получить сообщения как consumer1

```shell
docker exec -ti kafka-learning-kafka /usr/bin/kafka-console-consumer --group consumer1 --topic topic-1 --bootstrap-server localhost:9091 
```

Отправить сообщение c ключом через двоеточие (key:value)

```shell
docker exec -ti kafka-learning-kafka /usr/bin/kafka-console-producer --topic topic-1 --property "parse.key=true" --property "key.separator=:" --bootstrap-server localhost:9091
```