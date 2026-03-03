#!/bin/zsh

cd auth
mvn spring-boot:run -Dspring-boot.run.profiles=local &

cd ../gateway
mvn spring-boot:run -Dspring-boot.run.profiles=local &

cd ../redirector
mvn spring-boot:run -Dspring-boot.run.profiles=local &

cd ../shortener
mvn spring-boot:run -Dspring-boot.run.profiles=local &

wait

cd ..
