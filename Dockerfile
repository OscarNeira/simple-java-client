FROM adoptopenjdk:latest
#Requires gradle `installDist` task to be run before building the image
COPY ./build/install/sample.java-client /opt/java-client
COPY  ./build/resources/main /
CMD [ "/opt/java-client/bin/sample.java-client"]