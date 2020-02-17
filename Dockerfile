## docker run -it --rm=true oscneira/java_client:5.0.0
#FROM adoptopenjdk:latest
##Requires gradle `installDist` task to be run before building the image
#COPY ./build/install/sample.java-client /opt/java-client
#COPY  ./build/resources/main /
#CMD [ "/opt/java-client/bin/sample.java-client"]

## docker run -it --rm=true oscneira/java_client:6.0.0
#FROM openjdk:11
##Requires gradle `installDist` task to be run before building the image
#COPY ./build/install/sample.java-client /opt/java-client
#COPY  ./build/resources/main /
#CMD [ "/opt/java-client/bin/sample.java-client"]


## docker run -it --rm=true oscneira/java_client:7.0.0
#FROM adoptopenjdk/openjdk8:ubi
#FROM openjdk:8
##Requires gradle `installDist` task to be run before building the image
#COPY ./build/install/sample.java-client /opt/java-client
#COPY  ./build/resources/main /
#CMD [ "/opt/java-client/bin/sample.java-client"]


## docker run -it --rm=true oscneira/java_client:8.0.0
#FROM adoptopenjdk:11-jre-hotspot
FROM adoptopenjdk:11-jre-openj9
#Requires gradle `installDist` task to be run before building the image
COPY ./build/install/sample.java-client /opt/java-client
COPY  ./build/resources/main /
CMD [ "/opt/java-client/bin/sample.java-client"]