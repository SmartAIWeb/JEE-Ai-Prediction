# JEE Mini Project

Kidney disease prediction app, Tomcat + MariaDB + Flask ML API.

## Prerequisites

```bash
sudo dnf install java-21-openjdk maven docker docker-compose-plugin
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
```

## Run

Build the Java app first:
```bash
cd web-app && mvn package && cd ..
```

Then:
```bash
docker compose up --build
```

| Service   | URL                   |
|-----------|-----------------------|
| Web app   | http://localhost:8080 |
| Flask API | http://localhost:5000 |

## Flask API
Your work is in `ml-api/api/app.py`. 
Load models from `./models/`, they are mounted there at runtime.

After changing `requirements.txt`:
```bash
docker compose build --no-cache ml-api
```
