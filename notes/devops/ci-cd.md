# CI/CD — Continuous Integration and Continuous Deployment

> CI/CD is the practice of automatically building, testing, and deploying code every time a change is pushed. It removes manual steps and catches bugs early — before they reach production.

---

## The Core Idea

**Without CI/CD:**
```
Developer writes code → manually tests locally → manually deploys
                          ↓ mistakes happen, takes days
```

**With CI/CD:**
```
Developer pushes code → automated pipeline runs
                           ↓
                      Build → Test → Deploy
                           ↓
                   Fast feedback in minutes
```

---

## Two Concepts

| Term | Meaning |
|------|---------|
| **CI (Continuous Integration)** | Automatically build and test on every commit. Catch integration problems early. |
| **CD (Continuous Delivery)** | Automatically prepare a release-ready artifact. Deploy to staging automatically. Manual approval for production. |
| **CD (Continuous Deployment)** | Fully automated deployment all the way to production, no manual step. |

---

## Typical CI/CD Pipeline

```
 Code Push
    │
    ▼
 ┌─────────────────────────────────────────────────────┐
 │                  CI Pipeline                         │
 │                                                     │
 │  1. Checkout code                                   │
 │  2. Install dependencies                            │
 │  3. Run linter / static analysis                    │
 │  4. Build (compile / create image)                  │
 │  5. Run unit tests                                  │
 │  6. Run integration tests                           │
 │  7. Build Docker image                              │
 │  8. Push image to registry                          │
 └──────────────────────────┬──────────────────────────┘
                            │ (on main branch)
                            ▼
 ┌─────────────────────────────────────────────────────┐
 │                  CD Pipeline                         │
 │                                                     │
 │  9. Deploy to staging                               │
 │  10. Run smoke tests                                │
 │  11. [Manual approval for production]               │
 │  12. Deploy to production                           │
 │  13. Monitor / rollback if needed                   │
 └─────────────────────────────────────────────────────┘
```

---

## GitHub Actions Example

GitHub Actions is one of the most popular CI/CD tools. Workflows are defined in `.github/workflows/`.

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up Go
      uses: actions/setup-go@v5
      with:
        go-version: '1.22'

    - name: Install dependencies
      run: go mod download

    - name: Run linter
      run: go vet ./...

    - name: Run tests
      run: go test ./... -v

  build-and-push:
    runs-on: ubuntu-latest
    needs: test  # only run if tests pass
    if: github.ref == 'refs/heads/main'

    steps:
    - uses: actions/checkout@v4

    - name: Log in to Docker Hub
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}

    - name: Build and push Docker image
      uses: docker/build-push-action@v5
      with:
        push: true
        tags: myorg/myapp:${{ github.sha }}
```

---

## Deployment to Kubernetes (CD Step)

```yaml
  deploy:
    runs-on: ubuntu-latest
    needs: build-and-push
    environment: production  # requires manual approval

    steps:
    - uses: actions/checkout@v4

    - name: Set up kubectl
      uses: azure/setup-kubectl@v3

    - name: Configure Kubernetes credentials
      run: echo "${{ secrets.KUBECONFIG }}" | base64 -d > kubeconfig.yaml

    - name: Deploy to cluster
      run: |
        kubectl --kubeconfig=kubeconfig.yaml \
          set image deployment/my-app \
          my-app=myorg/myapp:${{ github.sha }}
        kubectl --kubeconfig=kubeconfig.yaml \
          rollout status deployment/my-app
```

---

## Popular CI/CD Tools

| Tool | Description |
|------|-------------|
| **GitHub Actions** | Built into GitHub, YAML-based, free for public repos |
| **GitLab CI/CD** | Built into GitLab, very powerful |
| **Jenkins** | Self-hosted, highly configurable, older |
| **CircleCI** | Cloud-based, fast, good Docker support |
| **ArgoCD** | GitOps for Kubernetes — syncs cluster state from Git |

---

## Key Best Practices

1. **Run tests on every PR** — never merge untested code.
2. **Keep pipelines fast** — under 10 minutes is the target. Use caching.
3. **Use secrets management** — never hardcode credentials; use CI secrets or Vault.
4. **Deploy to staging first** — catch environment-specific issues before production.
5. **Use rollback strategies** — keep previous version ready; canary or blue/green deployments.
6. **Monitor after deploy** — alert on error rate or latency spikes right after a release.

---

## Blue/Green Deployment

```
Current traffic:
  Users → Load Balancer → [Blue: v1.0] (100%)
                          [Green: v1.1] (0%)

After deploy and verification:
  Users → Load Balancer → [Blue: v1.0] (0%)   ← keep for rollback
                          [Green: v1.1] (100%)

Rollback: flip back to Blue instantly
```

---

## Real-World Use Case

- **Every PR triggers CI** — lint, test, security scan. Catches bugs before merge.
- **Merge to main triggers CD** — app is built, containerized, and deployed to staging automatically.
- **Manual approval gate** — a team lead approves the production deploy.
- **Rollback** — if error rate spikes, trigger the previous deployment in one click.
