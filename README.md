# Simple test Java Client to Test Ingress controller
Simple java client test request to a mock sever to test ingress controller

## Prerequisites
### Minikube
To start minikube run ./k8s/minikube-start.sh
- Start minikube with version 1.13.6
- Enable Ingress controller

## Run the full example
Run ./k8s/start.sh

The scripts is executed in this order:
- Deploy a specific mock server
- Waits for running state
- Start the tcpdump process inside the mockserver

The script run this project in docker
- Locally pull and start this Java client
- Creates expectations for the mock server already deployed 
- Creates a Transfer-encoding chunk requests to a POST endpoint in the mock server.
- The dump file inside the pod is copied to a local folder.
 
### Run only this project 
./gradlew build installDist
docker build -t oscneira/java_client:2.0.0 .
docker run -it --rm=true oscneira/java_client:2.0.0

## Push public repo
docker push oscneira/java_client:2.0.0

## Mock server version used in this test
https://github.com/OscarNeira/mockserver/tree/add/tcpdumps
- Added tcpdump package

### Run Mock server locally
 docker build -t oscneira/mockserver:5.9.0 .
 docker push oscneira/mockserver:5.9.0

## Test Mock server is running with the delay expectation
curl -v -k GET "http://example.mockserver.com/delay/1" 