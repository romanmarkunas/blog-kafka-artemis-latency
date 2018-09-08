# Apache Kafka latency benchmark

Most developers I talk to about Kafka agree on a catchphrase "Kafka is designed 
for throughput". That's fair and you can find plenty of benchmarks that show
700k/s throughput for single producer without replication. But does that mean
we should discard Kafka when talking about low latency messaging? 

After quick Google'ing I found [this outdated SO question](https://stackoverflow.com/questions/20520492/how-to-minimize-the-latency-involved-in-kafka-messaging-framework).
That question is very old and includes very old version of Kafka (0.7.2 whereas
current version is 2.0.0) and also [this article](TODO link) 
states that very decent latencies (2-3ms 99 percentile) are achievable. Having 
such controversial info is not enough to make final decision so I decided to 
create a little benchmark myself, to finally conclude whether Kafka is good for 
low-latency applications.


## What is measured

Latency test intent is to test following scenarios:
1. light throughput of 200 messages/second + non-durable broker
1. light throughput of 200 messages/second + fault-tolerant broker
1. moderate throughput of 1000 messages/second + non-durable broker
1. moderate throughput of 1000 messages/second + fault tolerant broker

This test do not measure latency drops due to cluster node failovers as 
these scenarios are very different for Artemis and Kafka. Hopefully node failures
are not part of your normal day-to-day operations ;)

As usual, all code can be found [here](TODO link), 
if you want to play around and see how your setup compares. All scenarios are 
located in test directory under benchmark/LatencyBenchmark.

For exact broker/client configuration see code above. Also I'll put a little 
explanation why these settings were used.


## Results

commit at read (synchronous commit increase 1 record read 75 percentile
from 4 ms to 7 ms when timeout is 1 ms) and replication at write impact
8 rides + 1.5

at certain range smaller poll timeout with Kafka increases percentile, but
decreases maximum latencies.

max 1300 latency on default setting? that's usually 1 outlier.

compare how kafka and artemis are easy to configure and total feel, e.g.
kafka feels like is controlled from settings and code must embbrace it

timeout on consumer is only if no records are available!! what is fetch interval then?

describe how each optimization option helps

.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
.\bin\windows\kafka-server-start.bat .\config\server.properties
.\bin\windows\kafka-consumer-groups.bat --describe --bootstrap-server localhost:9092 --group latency-test-group

Either will do for most cases, just don't make the zoo. My opinion:
a man who knows how to use a knife with a dull knife is better than a 
man who has no clue with 5 sharp knifes. Latter will probably just cut 
himself


## Conclusion



## Overview of settings and their impact on latency
