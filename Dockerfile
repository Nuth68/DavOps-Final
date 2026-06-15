FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

# Install base packages and adoptium repo for JDK 23
RUN apt-get update && \
    apt-get install -y wget apt-transport-https gnupg && \
    mkdir -p /usr/share/keyrings && \
    wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | \
      gpg --dearmor --output /usr/share/keyrings/adoptium.gpg && \
    echo 'deb [signed-by=/usr/share/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb noble main' \
      > /etc/apt/sources.list.d/adoptium.list && \
    apt-get update && \
    apt-get install -y temurin-23-jdk nginx openssh-server git curl php php-mysql php-curl php-gd php-xml php-mbstring && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Configure SSH
RUN mkdir -p /var/run/sshd && \
    echo 'PermitRootLogin yes' >> /etc/ssh/sshd_config && \
    echo 'root:root' | chpasswd

# Set up nginx to proxy to Spring Boot
COPY nginx/default.conf /etc/nginx/sites-available/default
RUN rm -f /etc/nginx/sites-enabled/default && \
    ln -s /etc/nginx/sites-available/default /etc/nginx/sites-enabled/default

WORKDIR /app

EXPOSE 80 22

CMD ["bash", "-c", "\
  service ssh start && \
  echo 'Waiting for MySQL to be ready (15s)...' && \
  sleep 15 && \
  echo 'Starting Spring Boot application...' && \
  java -jar /app/target/demo-0.0.1-SNAPSHOT.jar & \
  sleep 10 && \
  echo 'Starting NGINX...' && \
  exec /usr/sbin/nginx -g 'daemon off;' \
"]
