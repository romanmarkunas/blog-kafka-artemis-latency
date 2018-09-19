# Messaging system latencies, part 1: Apache Kafka

Most developers I talk to about Kafka agree on a catchphrase "Kafka is designed 
for throughput". That's fair and you can find plenty of benchmarks that show
700k/s throughput for single producer without replication. But does that mean
we should discard Kafka when talking about low latency messaging? 

After quick googling I found [this outdated SO question](https://stackoverflow.com/questions/20520492/how-to-minimize-the-latency-involved-in-kafka-messaging-framework).
That question is very old and includes very old version of Kafka (0.7.2 whereas
current version is 2.0.0) and also [this article](https://engineering.linkedin.com/kafka/benchmarking-apache-kafka-2-million-writes-second-three-cheap-machines) 
states that very decent latencies (2-3ms 99 percentile) are achievable. Having 
such controversial info is not enough to make final decision so I decided to 
create a little benchmark myself, to finally conclude whether Kafka is good for 
low-latency applications.


## What is measured

For all tests I run producer, consumer and broker on the same machine. For these 
tests I used my work laptop: i7-7820HQ, 16GB, Windows 10 (yes, I know...). This 
is why pre-allocation is on in broker settings and you will need to disable it
should you run provided code on Linux box. Also I didn't change default Kafka
startup scripts, which means brokers had 1GB heap maximum.   

Latency test intent is to test following scenarios:
1. light throughput of 200 messages/second + non-durable broker
1. light throughput of 200 messages/second + fault-tolerant broker
1. moderate throughput of 4000 messages/second + non-durable broker
1. moderate throughput of 4000 messages/second + fault tolerant broker

This test do not measure latency drops due to cluster node failovers as 
these scenarios are very different depending on your partitioning schema and 
replication factor. Hopefully node failures are not part of your normal 
day-to-day operations ;)

As usual, all code can be found [here](https://github.com/romanmarkunas/blog-kafka-artemis-latency), 
if you want to play around and see how your setup compares. All scenarios are 
located in test directory under benchmark/LatencyBenchmark.

For exact broker/client configuration see code above. Also I'll put a little 
explanation why these settings were used.


## Results

#### Lowest latencies possible

Measurements @ 200 messages/s:
Total sent     - 5000
Total received - 5000
Send rate      - 200.004
99 percentile  - 1.576891
75 percentile  - 1.180825
Min latency    - 0.593806
Max latency    - 8.921906
Avg latency    - 1.085105

Measurements @ 4000 messages/s:
Total sent     - 50000
Total received - 50000
Send rate      - 3837.725
99 percentile  - 1.327242
75 percentile  - 0.945013
Min latency    - 0.413972
Max latency    - 13.287300
Avg latency    - 0.802173

So these are lowest latencies possible on my machine. Configuration for these:
1. No batching on producer (consumer always fetches all available messages up 
to configured size)
1. No acks on producer
1. No commits after each message (but uses asynchronous background commits) on 
consumer
1. Single node cluster, no replication
1. Broker has delayed sync to disk
1. All internal topics are single partition

As you can see most messages make a roundtrip within 1-2 ms. There are always 
outliers at rare occasion, which are caused by different maintenance operations
and probably some interference from OS. For example, expired offset removal took
around 7 ms.

Also it's possible to decrease consumer poll timeout to get smaller max latency
at cost of higher 99 percentile. 

#### Non fault-tolerant setup with consumer commit after each read

Measurements @ 200 messages/s and synchronous commit:
Total sent     - 5000
Total received - 5000
Send rate      - 200.004
99 percentile  - 4.535680
75 percentile  - 1.927668
Min latency    - 0.904814
Max latency    - 9.848230
Avg latency    - 1.865007

I made these measurements as I was curious how synchronous commit affects 
latency, and it's roughly 30% overhead.

#### Fault-tolerant setup with synchronous consumer commit

3 broker nodes + commit after each read
Measurement of Kafka fault tolerant message 200 messages/s complete!

Total sent     - 5000
Total received - 5000
Send rate      - 199.989
99 percentile  - 5.137226
75 percentile  - 3.924183
Min latency    - 2.115347
Max latency    - 14.580346
Avg latency    - 3.613249
===============================================

Rate is not 4K with tese latencies obvs
Measurement of Kafka fault tolerant message 4000 messages/s complete!

Total sent     - 50000
Total received - 50000
Send rate      - 632.051
99 percentile  - 5.121739
75 percentile  - 3.679203
Min latency    - 1.930928
Max latency    - 26.264651
Avg latency    - 3.397053
===============================================

but if I allow batching this happens:
Measurement of Kafka fault tolerant message 4000 messages/s complete!

Total sent     - 50000
Total received - 50000
Send rate      - 3993.356
99 percentile  - 17.898772
75 percentile  - 11.823148
Min latency    - 5.039950
Max latency    - 35.926347
Avg latency    - 11.018564
===============================================

commit at read (synchronous commit increase 1 record read 75 percentile
from 4 ms to 7 ms when timeout is 1 ms) and replication at write impact
8 rides + 1.5


Either will do for most cases, just don't make the zoo. My opinion:
a man who knows how to use a knife with a dull knife is better than a 
man who has no clue with 5 sharp knifes. Latter will probably just cut 
himself


## Conclusion



## Overview of settings and their impact on latency