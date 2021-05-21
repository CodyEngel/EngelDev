FROM gradle:jdk12 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew installDist

FROM openjdk:12-jdk

EXPOSE 8080:8080
COPY --from=builder /home/gradle/src/build/install/api/ /app/
WORKDIR /app

CMD ["bin/api"]