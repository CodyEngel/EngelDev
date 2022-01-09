FROM gradle:jdk17 as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew installDist

FROM openjdk:17

EXPOSE 8080:8080
COPY --from=builder /home/gradle/src/build/install/api/ /app/
WORKDIR /app

CMD ["bin/api"]