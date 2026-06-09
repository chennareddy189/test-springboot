Kubernetes deployment for SimpleJavaProject

This folder contains example Kubernetes manifests and instructions to deploy the Spring Boot app built in this repository.

Files
- `deployment.yaml` — Deployment (2 replicas) with liveness/readiness probes and resource limits.
- `service.yaml` — ClusterIP Service that exposes the app on port 80 (targets container port 8080).

Quick start (local cluster like minikube / kind / microk8s)
1. Build and push your image to a registry (Docker Hub, GHCR, or private registry). Example using Docker Hub:

   docker build -t youruser/simple-spring-cicd:latest .
   docker push youruser/simple-spring-cicd:latest

2. Edit `k8s/deployment.yaml` and replace the `image:` value with the pushed image (e.g. `youruser/simple-spring-cicd:latest` or `ghcr.io/OWNER/simple-spring-cicd:tag`).

3. If your image is in a private registry (including GHCR if your repo is private), create an image pull secret:

   kubectl create secret docker-registry regcred \
     --docker-server=<REGISTRY_URL> \
     --docker-username=<USERNAME> \
     --docker-password=<PASSWORD> \
     --docker-email=<EMAIL>

   Note: `regcred` is the name used in `deployment.yaml` under `imagePullSecrets`. If you use a different name, update the manifest.

4. Apply the manifests:

   kubectl apply -f k8s/deployment.yaml
   kubectl apply -f k8s/service.yaml

5. Verify pods and service:

   kubectl get pods -l app=simple-spring
   kubectl get svc simple-spring

6. Access the app (cluster-specific):
   - minikube: `minikube service simple-spring` or `minikube tunnel` then use the assigned IP
   - kind: use `kubectl port-forward svc/simple-spring 8080:80` and open http://localhost:8080

GitHub Actions: automatic deploy job (example)
If you want to have GitHub Actions apply these manifests after building and pushing the image, add a deploy job that:
- has access to a kubeconfig (store it as a repository secret, base64-encoded)
- decodes kubeconfig and sets the KUBECONFIG environment variable

Example snippet to add as a job in `.github/workflows/ci.yml` after your image is pushed (replace placeholders):

  deploy:
    needs: docker
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup kubectl
        uses: azure/setup-kubectl@v3
        with:
          version: 'latest'

      - name: Configure kubeconfig
        env:
          KUBE_CONFIG_DATA: ${{ secrets.KUBE_CONFIG_DATA }} # base64-encoded kubeconfig
        run: |
          echo "$KUBE_CONFIG_DATA" | base64 --decode > kubeconfig
          mkdir -p $HOME/.kube
          mv kubeconfig $HOME/.kube/config
          chmod 600 $HOME/.kube/config

      - name: Update deployment image
        run: |
          # use the same image tag your CI pushed (example uses latest)
          kubectl set image deployment/simple-spring simple-spring=ghcr.io/OWNER/simple-spring-cicd:latest --record || true

      - name: Apply manifests
        run: |
          kubectl apply -f k8s/deployment.yaml
          kubectl apply -f k8s/service.yaml

Secrets you need in GitHub
- `KUBE_CONFIG_DATA` — base64 of your kubeconfig file so the Action can authenticate to the cluster.
  Create with: `cat ~/.kube/config | base64 | pbcopy` and paste into the repository secret.
- If pulling image from a private registry from the cluster, create `regcred` on the cluster (see steps above) — this is not a GitHub secret unless you plan to create the secret via Actions.

Troubleshooting tips
- If pods fail with ImagePullBackOff, ensure the image name and tag are correct and that the cluster can access the registry (imagePullSecret correctly created).
- If probes fail, check the application logs: `kubectl logs <pod>` and `kubectl describe pod <pod>`.
- If you use GHCR and the image is private, you can either make the image public or create a Docker registry secret in the cluster using a personal access token as the password.

If you'd like, I can:
- Add a GitHub Actions job to your `ci.yml` that performs the deploy using a `KUBE_CONFIG_DATA` secret.
- Create an `ingress.yaml` example for exposing the application externally (requires an Ingress controller).
- Help you set up pushing to GHCR or Docker Hub and fix the CI issues you saw earlier (artifact not found, missing JAR, docker login failures). 

Tell me what you'd like next (add deploy job, ingress, or fix CI image build/push problems).
