# If minikube is not running apply
# ./minikibe-start.sh

# make sure host name exists in /etc/hosts
#echo "$(minikube ip) example.mockserver.com" | sudo tee -a /etc/hosts
kubectl create namespace mockserver
kubectl config set-context --current --namespace=mockserver


for i in {1..10}
do
  sleep 5
  MOCK_SERVER_POD=$(kubectl get pod -n mockserver -l app=mockserver -o jsonpath="{.items[0].metadata.name}")
  echo $MOCK_SERVER_POD "Is mock server Running"
  kubectl get pod $MOCK_SERVER_POD | grep "0/1" || true
  [ `kubectl get pod $MOCK_SERVER_POD -n mockserver| grep "1/1" | wc -l` -eq 1 ] && break
  sleep 5
done

sleep 5
echo $MOCK_SERVER_POD "Is Running"

## tcpdumps in mockserver pod
kubectl exec $MOCK_SERVER_POD -- tcpdump -w tcpdump-ingress-chunk-no-clean.pcap &

docker pull oscneira/java_client:5.0.0

for i in 1 2 3 4 5
do
#   sleep 2
   docker run -it --rm=true oscneira/java_client:5.0.0
done

#Copy tcpdumps to local
kubectl cp $MOCK_SERVER_POD:tcpdump-ingress-chunk-no-clean.pcap ~/tcpdump-ingress-chunk-no-clean.pcap

# Options to get ingress output
# 1) Inspect files with wireshark
# 2) Set ingress in debug mode and check the headers
