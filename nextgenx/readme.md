# Overview
This is the monolith behind the panorama website

# How to run it in docker

This will get you started

    export STACK=<your salary id>
    export ENVIRONMENT=jarvis
    export SID=dynamic
    export ABS_BUILD=<something specific | LATEST>
    export DOCKER_HOST=tcp://jarvis.cloud.btfin-dev.com:5000
    export COMPOSE_PROJECT_NAME=<salary id>
    export COMPOSE_HTTP_TIMEOUT=3600
    docker-compose up
    
You can then look at what is running with ```docker-compose ps``` or ```docker-compose logs -f```

Another way to optimise is to save those settings in the file `.env`, here's mine

    $ cat .env

    ENVIRONMENT=jarvis
    ABS_BUILD=img6080.tgz
    DOCKER_HOST=tcp://jarvis.cloud.btfin-dev.com:5000
    STACK=m034259
    SID=dynamic
    COMPOSE_PROJECT_NAME=m034259
    COMPOSE_HTTP_TIMEOUT=3600

    $


# To shut it all down run
`docker-compose down -v` the `-v` is important as it clears up the disk usage

# How to build docker base

    mvn dependency:copy-dependencies 
    docker build --tag docker.cloud.btfin-dev.com/panorama/tomcat-base -f Dockerfile_base .
