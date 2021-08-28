# FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-debianslim-slim
FROM adoptopenjdk/openjdk11:latest

RUN apt-get update && \
    apt-get install -y sqlite3

COPY . /anteus
WORKDIR /anteus

EXPOSE 7000
# When the container starts: build, test and run the app.
CMD ./gradlew build && ./gradlew test && ./gradlew run
