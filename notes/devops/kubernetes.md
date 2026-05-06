# Kubernetes

> Kubernetes (K8s) is a system for running and managing containers at scale. If Docker is like a shipping container, Kubernetes is the port that organizes, moves, and tracks thousands of containers efficiently.

---

## Why Kubernetes?

Docker is great for running one container. But in production you might need:
- 50 instances of your app (high traffic)
- Auto-restart if a container crashes
- Rolling updates without downtime
- Traffic load balanced across instances
- Secrets and config managed separately from code

Kubernetes handles all of this.

---

## Key Concepts

```
Cluster
  └── Node (a machine — physical or VM)
       └── Pod (one or more containers)
            └── Container (your app)
```

| Object | Description |
|--------|-------------|
| **Cluster** | The whole K8s system (control plane + worker nodes) |
| **Node** | A machine (VM or physical) running your workloads |
| **Pod** | The smallest deployable unit; wraps one (or more) containers |
| **Deployment** | Manages a set of identical Pods (handles scaling and updates) |
| **Service** | Exposes Pods to network traffic; provides a stable DNS name |
| **ConfigMap** | Stores non-sensitive config (env vars, config files) |
| **Secret** | Stores sensitive data (passwords, API keys) |
| **Namespace** | Logical isolation of resources within a cluster |
| **Ingress** | Routes external HTTP/S traffic to Services |

---

## Diagram: Kubernetes Architecture

```
                    ┌─────────────────────────────────┐
                    │         Control Plane            │
                    │  ┌──────────┐  ┌─────────────┐  │
                    │  │  API     │  │  Scheduler  │  │
                    │  │  Server  │  └─────────────┘  │
                    │  └──────────┘  ┌─────────────┐  │
                    │                │  etcd (DB)  │  │
                    │                └─────────────┘  │
                    └──────────────┬──────────────────┘
                                   │
              ┌────────────────────┴────────────────────┐
              │                                         │
    ┌─────────▼──────────┐               ┌─────────────▼──────┐
    │      Node 1         │               │       Node 2        │
    │  ┌────┐  ┌────┐     │               │  ┌────┐  ┌────┐    │
    │  │Pod │  │Pod │     │               │  │Pod │  │Pod │    │
    │  └────┘  └────┘     │               │  └────┘  └────┘    │
    └─────────────────────┘               └────────────────────┘
```

---

## Core YAML Resources

### Deployment

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 3               # run 3 identical pods
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app
        image: myapp:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: DB_HOST
          value: "postgres-service"
        resources:
          requests:
            memory: "64Mi"
            cpu: "250m"
          limits:
            memory: "128Mi"
            cpu: "500m"
```

### Service

```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  selector:
    app: my-app           # routes to pods with this label
  ports:
  - port: 80              # service port (external)
    targetPort: 8080      # container port (internal)
  type: ClusterIP         # only accessible within cluster
  # type: LoadBalancer    # creates a cloud load balancer (external)
  # type: NodePort        # exposes on each node's IP + port
```

---

## Essential kubectl Commands

```bash
# Cluster info
kubectl cluster-info
kubectl get nodes

# Apply a YAML file (create or update)
kubectl apply -f deployment.yaml
kubectl apply -f .  # apply all YAML files in current directory

# List resources
kubectl get pods
kubectl get deployments
kubectl get services
kubectl get all  # show everything

# Inspect a resource
kubectl describe pod my-app-abc123
kubectl describe deployment my-app

# View logs
kubectl logs my-app-abc123
kubectl logs -f my-app-abc123         # follow
kubectl logs my-app-abc123 -c my-app  # specific container

# Execute inside a pod
kubectl exec -it my-app-abc123 -- sh

# Scale a deployment
kubectl scale deployment my-app --replicas=5

# Rolling update (update image)
kubectl set image deployment/my-app my-app=myapp:1.1.0

# Rollback
kubectl rollout undo deployment/my-app

# Check rollout status
kubectl rollout status deployment/my-app

# Delete resources
kubectl delete pod my-app-abc123
kubectl delete -f deployment.yaml
```

---

## ConfigMap and Secret

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  APP_ENV: "production"
  LOG_LEVEL: "info"
```

```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  DB_PASSWORD: cGFzc3dvcmQ=  # base64 encoded value
```

```yaml
# Reference in a Deployment:
envFrom:
- configMapRef:
    name: app-config
- secretRef:
    name: app-secrets
```

---

## Real-World Use Case

- **Zero-downtime deployments:** Rolling updates replace old pods one by one.
- **Auto-healing:** If a pod crashes, K8s restarts it automatically.
- **Horizontal scaling:** Add more pods during high traffic, remove during low traffic.
- **Multi-environment:** Use namespaces to isolate dev, staging, and production on the same cluster.
