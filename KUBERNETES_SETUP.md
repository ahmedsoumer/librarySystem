# Kubernetes Deployment Guide for Library Management System

This guide provides comprehensive instructions for deploying the Library Management System to Kubernetes.

## Prerequisites

- Kubernetes cluster (v1.20+)
- kubectl configured to access your cluster
- Docker registry access (for custom images)
- NGINX Ingress Controller (optional, for external access)

## Quick Start

### 1. Build and Push Docker Image

```bash
# Build the application image
cd librarySystem_Backend
docker build -t library-management-system:latest .

# Tag for your registry (replace with your registry URL)
docker tag library-management-system:latest your-registry/library-management-system:latest
docker push your-registry/library-management-system:latest
```

Or use the provided script:
```bash
chmod +x build-and-push.sh
./build-and-push.sh your-registry.com latest
```

### 2. Deploy to Kubernetes

```bash
# Deploy all resources using Kustomize
kubectl apply -k k8s/

# Check deployment status
kubectl get pods -n library-system
kubectl get services -n library-system
```

### 3. Access the Application

**Internal Access (within cluster):**
- API: `http://library-app-service.library-system.svc.cluster.local:8081`
- Swagger UI: `http://library-app-service.library-system.svc.cluster.local:8081/swagger-ui.html`

**External Access (with Ingress):**
- Add to `/etc/hosts`: `<ingress-ip> library-api.local`
- API: `http://library-api.local/api/library`
- Swagger UI: `http://library-api.local/swagger-ui.html`

## Kubernetes Resources

### Core Components

- **Namespace**: `library-system` - Isolated environment
- **ConfigMap**: Application configuration (non-sensitive)
- **Secret**: Database credentials (base64 encoded)
- **PVC**: Persistent storage for PostgreSQL data

### Database Layer

- **PostgreSQL Deployment**: Single replica with persistent storage
- **PostgreSQL Service**: ClusterIP service for internal access
- **Health Checks**: Liveness and readiness probes

### Application Layer

- **App Deployment**: 2 replicas with rolling updates
- **App Service**: ClusterIP service for internal access
- **Init Container**: Waits for PostgreSQL availability
- **Health Checks**: Spring Boot Actuator endpoints

### Networking & Scaling

- **Ingress**: External access with path-based routing
- **HPA**: Auto-scaling based on CPU/memory usage (2-10 replicas)

## Configuration

### Environment Variables

**ConfigMap** (`library-app-config`):
- `SPRING_PROFILES_ACTIVE`: Application profile
- `SERVER_PORT`: Application port
- `DB_HOST`, `DB_PORT`, `DB_NAME`: Database connection

**Secret** (`library-app-secrets`):
- `DB_USERNAME`: Database username (base64)
- `DB_PASSWORD`: Database password (base64)

### Resource Limits

**PostgreSQL**:
- Requests: 256Mi memory, 250m CPU
- Limits: 512Mi memory, 500m CPU

**Application**:
- Requests: 512Mi memory, 250m CPU
- Limits: 1Gi memory, 500m CPU

## Deployment Commands

### Initial Deployment

```bash
# Create namespace and deploy all resources
kubectl apply -k k8s/

# Wait for pods to be ready
kubectl wait --for=condition=ready pod -l app=postgres -n library-system --timeout=300s
kubectl wait --for=condition=ready pod -l app=library-app -n library-system --timeout=300s
```

### Updates

```bash
# Update application image
kubectl set image deployment/library-app-deployment library-app=your-registry/library-management-system:v2.0.0 -n library-system

# Rolling restart
kubectl rollout restart deployment/library-app-deployment -n library-system

# Check rollout status
kubectl rollout status deployment/library-app-deployment -n library-system
```

### Scaling

```bash
# Manual scaling
kubectl scale deployment library-app-deployment --replicas=5 -n library-system

# Auto-scaling (HPA already configured)
kubectl get hpa -n library-system
```

## Monitoring & Troubleshooting

### Check Pod Status

```bash
# View all pods
kubectl get pods -n library-system

# Describe pod for details
kubectl describe pod <pod-name> -n library-system

# View logs
kubectl logs <pod-name> -n library-system -f
```

### Database Access

```bash
# Port forward to PostgreSQL
kubectl port-forward service/postgres-service 5432:5432 -n library-system

# Connect using psql
psql -h localhost -U postgres -d library_db
```

### Application Access

```bash
# Port forward to application
kubectl port-forward service/library-app-service 8081:8081 -n library-system

# Access API
curl http://localhost:8081/api/library/books
```

### Health Checks

```bash
# Check application health
kubectl port-forward service/library-app-service 8081:8081 -n library-system
curl http://localhost:8081/actuator/health
```

## Security Considerations

### Production Deployment

1. **Update Secrets**: Change default database credentials
```bash
# Create new secret with custom credentials
kubectl create secret generic library-app-secrets \
  --from-literal=DB_USERNAME=your-username \
  --from-literal=DB_PASSWORD=your-secure-password \
  --from-literal=POSTGRES_USER=your-username \
  --from-literal=POSTGRES_PASSWORD=your-secure-password \
  -n library-system
```

2. **Network Policies**: Restrict pod-to-pod communication
3. **RBAC**: Configure service accounts with minimal permissions
4. **TLS**: Enable HTTPS in Ingress configuration
5. **Image Security**: Use specific image tags, not `latest`

### Backup Strategy

```bash
# PostgreSQL backup
kubectl exec -it <postgres-pod> -n library-system -- pg_dump -U postgres library_db > backup.sql

# Restore
kubectl exec -i <postgres-pod> -n library-system -- psql -U postgres library_db < backup.sql
```

## Testing the Deployment

### API Endpoints Test

```bash
# Set up port forwarding
kubectl port-forward service/library-app-service 8081:8081 -n library-system

# Test endpoints
curl -X POST http://localhost:8081/api/library/borrowers \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'

curl -X POST http://localhost:8081/api/library/books \
  -H "Content-Type: application/json" \
  -d '{"isbn": "978-0134685991", "title": "Effective Java", "author": "Joshua Bloch"}'

curl http://localhost:8081/api/library/books
```

### Load Testing

```bash
# Install hey (HTTP load testing tool)
# Test with concurrent requests
hey -n 1000 -c 10 http://localhost:8081/api/library/books
```

## Cleanup

```bash
# Remove all resources
kubectl delete -k k8s/

# Or delete namespace (removes everything)
kubectl delete namespace library-system
```

## Troubleshooting Common Issues

### Pod Stuck in Pending
- Check PVC availability: `kubectl get pvc -n library-system`
- Check node resources: `kubectl describe nodes`

### Application Won't Start
- Check init container logs: `kubectl logs <pod> -c wait-for-postgres -n library-system`
- Verify database connectivity: `kubectl exec -it <app-pod> -n library-system -- nc -zv postgres-service 5432`

### Database Connection Issues
- Verify secret values: `kubectl get secret library-app-secrets -o yaml -n library-system`
- Check PostgreSQL logs: `kubectl logs <postgres-pod> -n library-system`

### Ingress Not Working
- Verify Ingress Controller: `kubectl get pods -n ingress-nginx`
- Check Ingress status: `kubectl describe ingress library-app-ingress -n library-system`
