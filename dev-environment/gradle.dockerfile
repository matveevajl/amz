FROM gradle:6.8.3-jdk11

RUN mkdir /application
WORKDIR /application

RUN chown gradle:gradle -R /application

USER gradle

RUN gradle wrapper