# SAP BTP Weather Microservice POC ☁️

A Spring Boot 3 microservice deployed on **AWS Elastic Beanstalk** and integrated with **SAP BTP Connectivity Service** — demonstrating a hybrid cloud integration pattern.

## What this demonstrates

| Concept | Implementation |
|---|---|
| **Hybrid AWS / SAP BTP integration** | App runs on AWS, registered as HTTP Destination in BTP Cockpit |
| **SAP BTP Destination Service** | `WeatherService` destination — NoAuthentication, connection verified |
| **Spring Boot 3 REST API** | Clean controller / service / model layers |
| **External API consumption** | OpenWeatherMap API, API key injected via environment variable |
| **AWS Elastic Beanstalk** | Java 17 (Corretto), eu-central-1 (Frankfurt), nginx reverse proxy |
| **Security best practice** | No secrets hardcoded — `WEATHER_API_KEY` as env var |

## Live Demo

```
GET http://btp-weather-poc-env.eba-cprtkiuw.eu-central-1.elasticbeanstalk.com/api/weather/public
GET http://btp-weather-poc-env.eba-cprtkiuw.eu-central-1.elasticbeanstalk.com/api/weather?city=Frankfurt
```

Example response:
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
  "source": "OpenWeatherMap API via SAP BTP Cloud Foundry",
  "fetchedBy": "api-user"
}
```

## Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/weather/public` | None | App info (public) |
| GET | `/api/weather?city={city}` | None | Current weather for any city |
| GET | `/actuator/health` | None | Health check |

## Architecture

```
SAP BTP Cockpit
  └── Connectivity Service
        └── Destination: WeatherService (HTTP, NoAuthentication)
              └──► AWS Elastic Beanstalk (eu-central-1)
                      └── Spring Boot 3 App (Java 17, port 5000)
                              └──► OpenWeatherMap API
```

## Tech Stack

- **Java 17** · **Spring Boot 3.2.5** · **Maven**
- **AWS Elastic Beanstalk** (Corretto 17, Amazon Linux 2023)
- **SAP BTP Connectivity Service** — Destination configuration
- **OpenWeatherMap API**

## Run Locally

```bash
# Clone and build
git clone https://github.com/Marwan11500/btp-weather-poc.git
cd btp-weather-poc
mvn clean package -DskipTests

# Run with your own API key
WEATHER_API_KEY=your_key java -jar target/btp-weather-poc-1.0.0.jar
```

Then open: `http://localhost:5000/api/weather?city=Berlin`

## Deploy to AWS Elastic Beanstalk

See [DEPLOY.md](DEPLOY.md) for the full step-by-step guide.

```bash
mvn clean package -DskipTests
# Upload target/btp-weather-poc-1.0.0.jar via AWS Console
# Set env var: WEATHER_API_KEY=<your key>
```

## Author

**Marwan Soltan** — SAP BTP Administrator (C_ADBTP) · AWS SAA-C03 · Spring Boot Developer  
[LinkedIn](https://linkedin.com/in/marwan-soltan) | [GitHub](https://github.com/Marwan11500)
