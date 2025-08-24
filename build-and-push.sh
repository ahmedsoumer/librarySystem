#!/bin/bash

# Build and push Docker image for Kubernetes deployment
# Usage: ./build-and-push.sh [registry-url] [tag]

REGISTRY_URL=${1:-"localhost:5000"}
TAG=${2:-"latest"}
IMAGE_NAME="library-management-system"
FULL_IMAGE_NAME="${REGISTRY_URL}/${IMAGE_NAME}:${TAG}"

echo "Building Docker image..."
cd librarySystem_Backend
docker build -t ${IMAGE_NAME}:${TAG} .

if [ $? -eq 0 ]; then
    echo "✅ Docker image built successfully: ${IMAGE_NAME}:${TAG}"
    
    # Tag for registry
    docker tag ${IMAGE_NAME}:${TAG} ${FULL_IMAGE_NAME}
    
    echo "Pushing to registry: ${FULL_IMAGE_NAME}"
    docker push ${FULL_IMAGE_NAME}
    
    if [ $? -eq 0 ]; then
        echo "✅ Image pushed successfully to ${FULL_IMAGE_NAME}"
        echo ""
        echo "To deploy to Kubernetes:"
        echo "1. Update k8s/app-deployment.yaml image field to: ${FULL_IMAGE_NAME}"
        echo "2. Run: kubectl apply -k k8s/"
    else
        echo "❌ Failed to push image to registry"
        exit 1
    fi
else
    echo "❌ Failed to build Docker image"
    exit 1
fi
