# If minikube is not running apply
# ./minikibe-start.sh

# make sure host name exists in /etc/hosts
#echo "$(minikube ip) example.mockserver.com" | sudo tee -a /etc/hosts
kubectl create namespace mockserver
kubectl config set-context --current --namespace=mockserver
# Clean old object just in case
kubectl delete deploy mockserver
kubectl delete ing mock-server-chunk-config-test
kubectl delete svc mockserver

sleep 10
#Deploy
kubectl apply -f k8s/mockserver.yaml
kubectl apply -f k8s/mockserver-ingress.yaml

#sleep 5
#MOCK_SERVER_POD=$(kubectl get pod -n mockserver -l app=mockserver -o jsonpath="{.items[0].metadata.name}")

for i in {1..10}
do
  sleep 5
  MOCK_SERVER_POD=$(kubectl get pod -n mockserver -l app=mockserver -o jsonpath="{.items[0].metadata.name}")
  echo $MOCK_SERVER_POD "Is mock server Running"
  kubectl get pod $MOCK_SERVER_POD | grep "0/1" || true
  [ `kubectl get pod $MOCK_SERVER_POD -n mockserver| grep "1/1" | wc -l` -eq 1 ] && break
  sleep 5
done
echo $MOCK_SERVER_POD "Is Running"

curl -X PUT "http://example.mockserver.com/expectation" -H "accept: */*" -H "Content-Type: application/json" -d '{"httpRequest":{"path":"/delay/1"},"httpResponse":{"body":"Delayed 1 second","delay":{"timeUnit":"SECONDS","value":1}}}'
curl -v "http://example.mockserver.com/delay/1"


curl -X PUT "http://example.mockserver.com/expectation" -H "accept: */*" -H "Content-Type: application/json" -d '[{"httpRequest":{"path":"/api/1/space/.*","method":"POST"},"httpResponse":{"delay":{"timeUnit":"SECONDS","value":1},"statusCode":200,"reasonPhrase":"OK"}}]'
curl -d '{"key1":"value1", "key2":"value2"}' -H "Content-Type: application/json" -X POST http://example.mockserver.com/api/1/space/

## tcpdumps in mockserver pod
kubectl exec $MOCK_SERVER_POD -- tcpdump -w tcpdump-ingress-chunk.pcap &

docker pull oscneira/java_client:5.0.0
docker run -it --rm=true oscneira/java_client:5.0.0

#Copy tcpdumps to local
kubectl cp $MOCK_SERVER_POD:tcpdump-ingress-chunk.pcap ~/tcpdump-ingress-chunk.pcap

# Options to get ingress output
# 1) Inspect files with wireshark
# 2) Set ingress in debug mode and check the headers

# clean all
#kubectl delete deploy mockserver
#kubectl delete ing mock-server-chunk-config-test
#kubectl delete svc mockserver