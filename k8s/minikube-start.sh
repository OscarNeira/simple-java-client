### Mac users

minikube config set vm-driver hyperkit

minikube config set kubernetes-version v1.13.6  --vm-driver="hyperkit"

### Start version v1.13.6

minikube start --kubernetes-version v1.13.6 --vm-driver="hyperkit" --cpus 4 --memory 5120

minikube addons enable ingress

