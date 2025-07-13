

== Building the docker image

```
docker build -t ludomikula/sires:latest -f docker/Dockerfile .
```

== Running the image

```
docker run -d -p 8888:8080 -v ./examples:/sires/data docker.io/ludomikula/sires:latest
```

