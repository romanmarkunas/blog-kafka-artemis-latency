# blog-fibers-intro

docker rm $(docker ps -a -q)
docker run -d -p 2181:2181 --name zookeeper blacktop/kafka zookeeper-server-start.sh config/zookeeper.properties
docker run -d -e KAFKA_ADVERTISED_HOST_NAME=192.168.99.100 --link zookeeper -p 9092:9092 --name kafka blacktop/kafka

docker run -d --net=host --name zookeeper blacktop/kafka zookeeper-server-start.sh config/zookeeper.properties
docker run -d -e KAFKA_ADVERTISED_HOST_NAME=192.168.99.100 --net=host --name kafka blacktop/kafka
