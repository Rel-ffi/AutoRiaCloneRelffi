FROM openjdk:17-alpine
MAINTAINER Relffi
RUN apk add bash
RUN mkdir /app
WORKDIR /app