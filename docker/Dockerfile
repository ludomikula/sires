FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

COPY src /build/src
COPY pom.xml /build/pom.xml
WORKDIR /build

RUN mvn clean package

## Build final image

FROM eclipse-temurin:21-alpine

RUN addgroup -S -g 1000 spring && adduser -S -u 1000 spring -G spring \
  && mkdir -p /sires/data/templates \
  && chown -R spring:spring /sires

RUN apk add --no-cache bash

COPY --chown=spring:spring --from=builder /build/target/*.jar /sires/app.jar
COPY --chmod=0750 --chown=spring:spring docker/docker-entry.sh docker-entry.sh

EXPOSE 8080

USER spring:spring
ENTRYPOINT ["sh", "docker-entry.sh"]

