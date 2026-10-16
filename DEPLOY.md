# BTP Weather POC — Deployment Guide

**Stack:** Spring Boot 3.2 · SAP BTP Cloud Foundry · XSUAA · Destination Service · OpenWeatherMap

---

## Prerequisites

- SAP BTP Trial account (free): https://hanatrial.ondemand.com
- CF CLI: `brew install cloudfoundry/tap/cf-cli@8` (or download from https://github.com/cloudfoundry/cli)
- Java 17 + Maven
- Free OpenWeatherMap API key: https://openweathermap.org/api (sign up → free tier)

---

## Step 1 — Build the JAR

```bash
cd btp-weather-poc
mvn clean package -DskipTests
# → target/btp-weather-poc-1.0.0.jar
```

---

## Step 2 — Login to BTP Cloud Foundry

```bash
cf login -a https://api.cf.eu10.hana.ondemand.com
# Enter your BTP trial email + password
# Select org: <your-trial-org>
# Select space: dev
```

---

## Step 3 — Create XSUAA Service Instance

```bash
cf create-service xsuaa application btp-weather-xsuaa -c xs-security.json
# Wait ~30 seconds for provisioning
cf service btp-weather-xsuaa  # check status = "create succeeded"
```

This creates the OAuth2 authorization server with the scopes defined in xs-security.json.

---

## Step 4 — Create Destination Service Instance

```bash
cf create-service destination lite btp-weather-destination
cf service btp-weather-destination  # check status = "create succeeded"
```

---

## Step 5 — Configure the Destination in BTP Cockpit

1. Open BTP Cockpit → your subaccount → Connectivity → Destinations
2. Click **New Destination**
3. Fill in:
   - **Name:** `OpenWeatherMap`
   - **Type:** HTTP
   - **URL:** `https://api.openweathermap.org`
   - **Proxy Type:** Internet
   - **Authentication:** NoAuthentication
4. Under **Additional Properties**, add:
   - Key: `URL.queries.appid`   Value: `<YOUR_OPENWEATHERMAP_API_KEY>`
   - Key: `URL.queries.units`   Value: `metric`
5. Click **Save** and then **Check Connection** (should return 200)

The API key lives in BTP Destination — never in the code or environment variables. ✓

---

## Step 6 — Deploy to Cloud Foundry

```bash
cf push
# Reads manifest.yml automatically
# CF downloads java_buildpack, uploads JAR, binds services
# Takes ~2 minutes on first push
```

Check logs:
```bash
cf logs btp-weather-poc --recent
```

---

## Step 7 — Get the App URL

```bash
cf app btp-weather-poc
# Look for: routes: btp-weather-poc-<random>.cfapps.eu10.hana.ondemand.com
```

---

## Step 8 — Test the Public Endpoint (no token needed)

```bash
curl https://btp-weather-poc-<random>.cfapps.eu10.hana.ondemand.com/api/weather/public
```

Expected response:
```json
{
  "app": "BTP Weather POC",
  "author": "Marwan Soltan",
  "stack": "Spring Boot 3 · SAP BTP Cloud Foundry · XSUAA · Destination Service",
  "status": "running"
}
```

---

## Step 9 — Test the Secured Endpoint (requires XSUAA token)

Create a service key to get a token for testing:
```bash
cf create-service-key btp-weather-xsuaa test-key
cf service-key btp-weather-xsuaa test-key
# Note: clientid, clientsecret, url
```

Get a token:
```bash
curl -X POST "<url>/oauth/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&client_id=<clientid>&client_secret=<clientsecret>"
# Copy the access_token value
```

Call the secured endpoint:
```bash
curl -H "Authorization: Bearer <access_token>" \
  "https://btp-weather-poc-<random>.cfapps.eu10.hana.ondemand.com/api/weather?city=Frankfurt"
```

Expected response:
```json
{
  "city": "Frankfurt",
  "country": "DE",
  "condition": "Clouds",
  "description": "overcast clouds",
  "temperatureCelsius": 14.2,
  "feelsLikeCelsius": 12.8,
  "humidityPercent": 78,
  "windSpeedMs": 3.1,
  "source": "SAP BTP Destination Service → OpenWeatherMap API",
  "fetchedBy": "sb-btp-weather-poc!t12345"
}
```

---

## Architecture

```
Client
  │
  │  Bearer Token (XSUAA JWT)
  ▼
Spring Boot App (CF)
  │  SecurityConfig validates token via XSUAA JWKS endpoint
  │
  │  TokenAuthenticationConverter extracts scopes → Spring Security roles
  │
  ▼
WeatherService
  │  DestinationAccessor.getDestination("OpenWeatherMap")
  │  → calls BTP Destination Service (bound via VCAP_SERVICES)
  │  → Destination Service resolves URL + injects API key
  │
  ▼
OpenWeatherMap API
```

---

## CV Entry (copy-paste ready)

**BTP Weather Dashboard** | Spring Boot · SAP BTP Cloud Foundry · XSUAA · Destination Service
- Deployed Spring Boot 3 microservice to SAP BTP Cloud Foundry
- Secured REST API with XSUAA OAuth2 (JWT validation, role collections, scope-based RBAC)
- External API connectivity via BTP Destination Service (API key management, zero secrets in code)
- GitHub: https://github.com/marwan-soltan/btp-weather-poc
