# SAP BTP Weather Microservice POC

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat&logo=springboot&logoColor=white)
![AWS](https://img.shields.io/badge/AWS_Elastic_Beanstalk-FF9900?style=flat&logo=amazonaws&logoColor=white)
![SAP BTP](https://img.shields.io/badge/SAP_BTP-0070F2?style=flat&logo=sap&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)

A Spring Boot 3 REST API deployed on **AWS Elastic Beanstalk** and registered as an HTTP Destination in **SAP BTP Connectivity Service** — demonstrating a production-ready hybrid cloud integration pattern.

---

## Architecture

```
SAP BTP Cockpit
  └── Connectivity Service
        └── Destination: WeatherService (HTTP, NoAuthentication)
              └──► AWS Elastic Beanstalk (eu-central-1, Frankfurt)
                      └── Spring Boot 3 App (Java 17, port 5000)
                              └──► OpenWeatherMap API
```

---

## What This Demonstrates

| Concept | Implementation |
|---|---|
| **Hybrid AWS / SAP BTP integration** | App runs on AWS, registered as HTTP Destination in BTP Cockpit — connection verified via "Check Connection" |
| **SAP BTP Destination Service** | `WeatherService` destination, NoAuthentication, reusable across BTP services |
| **Spring Boot 3 REST API** | Clean layered architecture: Controller → Service → Model |
| **External API consumption** | OpenWeatherMap API, API key injected via environment variable |
| **AWS Elastic Beanstalk** | Java 17 (Corretto), Amazon Linux 2023, nginx reverse proxy |
| **Security best practice** | Zero secrets in code — `WEATHER_API_KEY` passed as env var only |

---

## Live Demo

```
GET http://btp-weather-poc-env.eba-cprtkiuw.eu-central-1.elasticbeanstalk.com/api/weather/public
GET http://btp-weather-poc-env.eba-cprtkiuw.eu-central-1.elasticbeanstalk.com/api/weather?city=Frankfurt
```

**Example response:**
```json
{
  "city": "Frankfurt am Main",
  "country": "DE",
  "condition": "Clouds",
  "description": "overcast clouds",
  "temperatureCelsius": 15.72,
  "feelsLikeCelsius": 15.36,
  "humidityPercent": 77,
  "windSpeedMs": 1.28,
  "source": "OpenWeatherMap API via AWS Elastic Beanstalk",
  "fetchedBy": "api-user"
}
```

---

## Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/weather/public` | None | App info — public health check |
| `GET` | `/api/weather?city={city}` | None | Live weather for any city |
| `GET` | `/actuator/health` | None | Spring Boot actuator health |

---

## Tech Stack

- **Java 17** · **Spring Boot 3.2.5** · **Maven**
- **AWS Elastic Beanstalk** (Corretto 17, Amazon Linux 2023, eu-central-1)
- **SAP BTP Connectivity Service** — Destination configuration
- **OpenWeatherMap API**

---

## Run Locally

```bash
# 1. Clone
git clone https://github.com/Marwan11500/btp-weather-poc.git
cd btp-weather-poc

# 2. Build
mvn clean package -DskipTests

# 3. Run (supply your own API key)
WEATHER_API_KEY=your_key java -jar target/btp-weather-poc-1.0.0.jar
```

Open: `http://localhost:5000/api/weather?city=Berlin`

---

## Deploy to AWS Elastic Beanstalk

1. Build the JAR: `mvn clean package -DskipTests`
2. Go to AWS Console → Elastic Beanstalk → Create Application
3. Platform: **Java / Corretto 17**
4. Upload `target/btp-weather-poc-1.0.0.jar`
5. Add environment variable: `WEATHER_API_KEY=<your key>`
6. Create environment — app starts on port 5000 (nginx proxy default)

---

## SAP BTP Integration

1. Open SAP BTP Cockpit → your subaccount
2. **Connectivity → Destinations → New Destination**
3. Configure:
   - Name: `WeatherService`
   - Type: `HTTP`
   - URL: `http://<your-beanstalk-domain>`
   - Authentication: `NoAuthentication`
4. Click **Check Connection** — should return `200 OK`

---

## Author

**Marwan Soltan**  
SAP BTP Administrator (C_ADBTP) · AWS Solutions Architect Associate (SAA-C03) · Spring Boot Developer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=flat&logo=linkedin&logoColor=white)](https://linkedin.com/in/marwan-soltan)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/Marwan11500)
