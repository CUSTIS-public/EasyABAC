# EasyABAC
[![pipeline status](https://git.custis.ru/dr/easyabac/easyabac/badges/develop/pipeline.svg)](https://git.custis.ru/dr/easyabac/easyabac/commits/develop)
Framework для авторизации на основе атрибутов (Attribute Based Access Control).

# Как подключить framework

# Запуск benchmark-ов
Benchmark-и подготовлены на основе [JMH framework](http://openjdk.java.net/projects/code-tools/jmh/). Чтобы запустить их:
1. Соберите проект `easyabac-benchmark` командой `mvn clean install`
1. Запустите `java -jar easyabac-benchmark/target/easyabac-benchmark.jar` чтобы прогнать тесты в режиме throughput
1. Запустите `java -jar easyabac-benchmark/target/easyabac-benchmark.jar -h` для показа всех параметров и варианта запуска