# Gambling App

A full-stack blackjack game with real authentication, a virtual coin economy, and a trash-talking AI dealer.
Bet coins, play against the house, and (if you're an admin) manage every player from a dedicated panel.

## What is this project?

A complete web application where players can:

- **Play blackjack** against an AI dealer with animated card reveals.
- **Bet virtual coins** — every new account starts with a 1000-coin signup bonus. No real money is involved.
- **Earn coins** by working in the "Labor" tab.
- **Get mocked by the dealer** — a separate microservice generates trash talk based on how each round ends.
- **Manage players** (admins only) — search, paginate, and grant coins to any user.

The app uses **real authentication**: users log in through Keycloak (OIDC), the frontend attaches JWTs to API calls, and the backend validates every request and enforces role-based access control.

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular 21 (standalone components, Angular Signals, RxJS), TypeScript |
| Backend | Java 25, Spring Boot 4.1, Spring Security (OAuth2 resource server), Spring Data JPA, Flyway, Lombok |
| AI Dealer | Python 3.14, FastAPI, Uvicorn |
| Authentication | Keycloak 26 (OpenID Connect, JWT) |
| Database | PostgreSQL 16 |
| Containerization | Podman Compose, multi-stage Dockerfiles, Nginx |

## Architecture

Five services run together and share the host network, so everything talks over `localhost`:

- **Frontend** (port 4200) — the built Angular SPA, served by Nginx. It redirects to
  Keycloak for login and attaches the returned JWT to every API call.
- **Keycloak** (port 8443) — handles login and issues the JWTs. The realm (roles, users,
  clients) is imported from `keycloak-config/realm-export.json` on first start.
- **Backend** (port 8080) — a stateless JWT resource server (Spring Boot) that contains
  all game logic and the coin economy.
- **AI Dealer** (port 8000) — a Python microservice the backend calls for the dealer's
  post-round comments.
- **PostgreSQL** (port 5432) — shared by the backend and Keycloak. The backend schema is
  managed entirely by Flyway migrations (PostgreSQL-specific: JSONB, custom enums).

## Prerequisites

- Linux with **Podman** (`podman compose`) or **Docker** (`docker compose`) — the stack
  uses host networking, so it runs on Linux
- Free ports: `4200`, `8080`, `8443`, `8000`, `5432`

## Quick Start

```bash
podman compose up -d --build
# or, with Docker:
docker compose up -d --build
```

When it's done, open **http://localhost:4200** and log in with one of the test accounts below.

## Test Accounts

Two users are seeded into Keycloak on every fresh start, with different permission levels:

| Username | Password | Role | What they can do |
|---|---|---|---|
| `admin` | `admin` | Administrator | Everything a player can, plus the **Admin Panel** at `/admin` (search players, view balances, grant coins) |
| `user` | `user` | Player | Play blackjack, bet coins, earn coins in the Labor tab |

Self-registration is also enabled, so anyone can create their own account from the login page.

## How the App Works

- **Blackjack** — place a bet, then Hit or Stand. Dealer plays by house rules (draws to 17).
  A win pays 2x the bet, a natural blackjack pays 2.5x, and a push refunds it.
- **AI Dealer** — after every finished round the dealer comments on the result
  (e.g. "Thanks for another donation to the casino."). It was originally planned to be
  backed by a real language model, but slow response times and incoherent answers made
  it impractical for a snappy game loop, so it now serves from a curated static list of responses.
- **Labor** — a clicker tab: 20 clicks = 20 coins.
- **Economy** — every coin movement is recorded as a transaction
  (wagers, payouts, refunds, signup bonus, labor wages, admin grants).
- **Admin Panel** — guarded on both sides: the Angular route guard checks the JWT's
  realm roles, and the backend re-checks with `@PreAuthorize("hasRole('ADMIN')")`.
