# Easyabac
[![pipeline status](https://git.custis.ru/dr/easyabac/easyabac/badges/develop/pipeline.svg)](https://git.custis.ru/dr/easyabac/easyabac/commits/develop)
Easyabac Framework: для авторизации на основе атрибутов объектов в приложениях на Java. Реализация подхода Attribute 
Based Access Control (ABAC). Более подробно про ABAC можно почитать
в [нашей статье на Хабре](https://habr.com/company/custis/blog/248649/). Framework построен на основе стандарта XACML, краткий
обзор стандарта можно прочесть в [еще одной нашей статье на Хабре](https://habr.com/company/custis/blog/258861/).
Про сам framework был сделан [доклад на Joker 2018](https://jokerconf.com/2018/talks/x4puhgszkukugmwk4c06g/). 

# Как подключить framework
Пример подключения и использования EasyABAC можно увидеть в `example-project`.

# Запуск benchmark-ов
Benchmark-и подготовлены на основе [JMH framework](http://openjdk.java.net/projects/code-tools/jmh/). Чтобы запустить их:
1. Соберите проект `easyabac-benchmark` командой `mvn clean install`
1. Запустите `java -jar easyabac-benchmark/target/easyabac-benchmark.jar` чтобы прогнать тесты в режиме throughput
1. Запустите `java -jar easyabac-benchmark/target/easyabac-benchmark.jar -h` для показа всех параметров и варианта запуска 