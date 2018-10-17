# easyabac

# Run benchmarks
Benchmarks are prepared with [JMH framework](http://openjdk.java.net/projects/code-tools/jmh/). To run benchmark:
1. Build `easyabac-benchmark` project with `mvn clean install`
1. Run `java -jar easyabac-benchmark/target/easyabac-benchmark.jar` to run benchmark in throughput mode
1. Run `java -jar easyabac-benchmark/target/easyabac-benchmark.jar -h` to get help on available options