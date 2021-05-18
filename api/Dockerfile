FROM openjdk:12-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/api/ /app/
WORKDIR /app/bin
CMD ["./api"]