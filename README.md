# easyabac
easyabac framework: for authorization based on object attributes in Java applications. Implement the Attribute Based Access Control (ABAC) approach. You can read more about ABAC on [Wikipedia](https://en.wikipedia.org/wiki/Attribute-based_access_control).
The framework is based on the XACML standard, a brief overview of the standard can be found in another of our article on [Wikipedia]https://en.wikipedia.org/wiki/XACML().

# How to connect framework
An example of connecting and using easyabac can be seen in `example-project`.

# Run benchmarks
Benchmark-and prepared on the basis of the [JMH framework](http://openjdk.java.net/projects/code-tools/jmh/). To run them:
    1.Build the easyabac-benchmark project with the mvn clean install command
    1.Run java -jar easyabac-benchmark / target / easyabac-benchmark.jar to run tests in throughput mode
    1.Run java -jar easyabac-benchmark / target / easyabac-benchmark.jar -h to display all parameters and launch options
   
