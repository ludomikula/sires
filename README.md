# Sires
Simple reporting server.  
Generates reports based on Jasper Reports compiled templates (.jasper files).

This is currently a proof of concept and can change without warning.

## Building the docker image

```
docker build -t ludomikula/sires:latest -f docker/Dockerfile .
```

## Running the image

```
docker run -d -p 8888:8080 -v ./examples:/sires/data docker.io/ludomikula/sires:latest
```

## Creating templates
Easiest way to create the templates is using Jaspersoft Studio.  
Server uses jasper report libraries version 7.0.3, so I recommend using Jaspersoft Studio version 7.x.  

Template can access data from request by using `$P{JSON_INPUT_DATA}` property, which is a string containing the data JSON.  
It is also automatically set as `JsonDataSource` in the main report. For subreports or tables be sure to create a new DataSource
by using DataSource expression:

```
new net.sf.jasperreports.json.data.JsonDataSource( new ByteArrayInputStream($P{JSON_INPUT_DATA}.getBytes("UTF8")))
```

You can also specify a selector to use only a subtree, check the `examples/templates/example.jrxml` line 96, where it uses `departments` as a selector.


## Running using docker-compose

- Create a `docker-compose.yaml` file with contents:

```
services:

  ##
  ## Sires - Simple Reporting Server
  ##
  sires:
    image: docker.io/ludomikula/sires:latest
    container_name: sires
    environment:
      # Where to look for jasper report templates
      SIRES_TEMPLATES_FOLDER: "/sires/data/templates"
      # Where to cache generated reports
      SIRES_REPORTS_CACHE_FOLDER: "/sires/data"
      # Cache generated reports for given amount of seconds
      SIRES_REPORTS_CACHE_TIMEOUT: 300
    ports:
      - "8888:8080"
    volumes:
      - ./:/sires/data
    restart: unless-stopped
```

Next to the `docker-compose.yaml` file, create two folders: `templates` and `reports`.  
Copy your jasper report compiled reports into the created `templates` folder and start the server with:

```docker compose up -d```
