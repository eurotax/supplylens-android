# SupplyLens Backend (FastAPI + PostgreSQL)

Professional backend API for SupplyLens Android application with JWT authentication, PostgreSQL database, and full CRUD operations.

## 🚀 Features (Sprint 2)

### ✅ Authentication & Security
- **JWT Authentication** (Access + Refresh tokens)
- **Bcrypt password hashing** (12 rounds)
- **Password validation** (10+ chars, 3/4 character classes)
- **Rate limiting** (5 auth attempts / 15 min)
- **CORS protection**

### ✅ Database
- **PostgreSQL** with async SQLAlchemy 2.0
- **Alembic migrations** (auto-generate + rollback support)
- **UUID primary keys**
- **Automatic timestamps** (created_at, updated_at)
- **Unique constraints** (prevent duplicates)

### ✅ API Endpoints

#### Authentication (`/api/v1/auth`)
- `POST /register` - Register new user
- `POST /login` - Login and get JWT tokens
- `GET /me` - Get current user info

#### Alerts (`/api/v1/alerts`)
- `GET /` - List alerts (paginated)
- `POST /` - Create alert
- `GET /{id}` - Get specific alert
- `PUT /{id}` - Update alert
- `DELETE /{id}` - Delete alert
- `PATCH /{id}/toggle` - Toggle alert active status

#### Watchlist (`/api/v1/watchlist`)
- `GET /` - List watchlist (paginated)
- `POST /` - Add to watchlist
- `GET /{id}` - Get specific item
- `PUT /{id}` - Update notes
- `DELETE /{id}` - Remove from watchlist

#### System
- `GET /healthz` - Health check
- `GET /api/v1/version` - API version info

## 📋 Tech Stack

```
FastAPI 0.114.0          # Web framework
Uvicorn 0.30.0           # ASGI server
SQLAlchemy 2.0.23        # ORM (async)
Asyncpg 0.29.0           # PostgreSQL driver
Alembic 1.13.0           # Migrations
PyJWT 2.8.0              # JWT tokens
Passlib 1.7.4            # Password hashing
Pydantic 2.5.0           # Validation
SlowAPI 0.1.9            # Rate limiting
```

## 🏃 Local Development

### Prerequisites
- Python 3.11+
- PostgreSQL 14+

### Setup

```bash
cd backend

# Create virtual environment
python -m venv venv

# Activate
# Windows:
venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Install dev dependencies (optional)
pip install -r requirements-dev.txt

# Copy .env.example to .env
copy .env.example .env
# Edit .env with your DATABASE_URL and JWT_SECRET_KEY

# Run migrations
alembic upgrade head

# Start development server
uvicorn app:app --reload --host 0.0.0.0 --port 8000
```

API will be available at: `http://localhost:8000`

Interactive docs: `http://localhost:8000/docs`

### Generate JWT Secret

```bash
python -c "import secrets; print(secrets.token_urlsafe(32))"
```

### Run Tests

```bash
# Run all tests
pytest

# Run with coverage
pytest --cov=app --cov-report=html

# Run specific test file
pytest tests/test_auth.py -v
```

## 🗄️ Database Schema

### Users
```sql
id            UUID PRIMARY KEY
email         VARCHAR(255) UNIQUE NOT NULL
password_hash VARCHAR(255) NOT NULL
created_at    TIMESTAMP DEFAULT NOW()
updated_at    TIMESTAMP DEFAULT NOW()
```

### Alerts
```sql
id              UUID PRIMARY KEY
user_id         UUID FOREIGN KEY → users.id
token_symbol    VARCHAR(50) NOT NULL
token_address   VARCHAR(255)
alert_type      ENUM(price, volume, holder, liquidity)
condition       ENUM(above, below, equals)
threshold_value NUMERIC(20,8) NOT NULL
is_active       BOOLEAN DEFAULT TRUE
triggered_at    TIMESTAMP
created_at      TIMESTAMP DEFAULT NOW()
updated_at      TIMESTAMP DEFAULT NOW()
```

### Watchlist
```sql
id            UUID PRIMARY KEY
user_id       UUID FOREIGN KEY → users.id
token_symbol  VARCHAR(50) NOT NULL
token_address VARCHAR(255)
notes         TEXT
created_at    TIMESTAMP DEFAULT NOW()

UNIQUE (user_id, token_symbol, COALESCE(token_address, ''))
```

## 🐳 Docker

### Build Image

```bash
docker build -t supplylens-backend .
```

### Run Container

```bash
docker run -p 8000:8000 \
  -e DATABASE_URL="postgresql+asyncpg://..." \
  -e JWT_SECRET_KEY="your-secret" \
  supplylens-backend
```

## 🌍 Deployment on Render.com

### Step 1: Create PostgreSQL Database

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **New +** → **PostgreSQL**
3. Configure:
   - Name: `supplylens-db`
   - Region: `Frankfurt (EU Central)`
   - Plan: `Free` (1GB, 90 days)
4. Click **Create Database**
5. Copy **Internal Database URL**

### Step 2: Update Web Service

1. Go to your `app-backend` service
2. Click **Environment**
3. Add variables:
   ```
   DATABASE_URL=<Internal Database URL from Step 1>
   JWT_SECRET_KEY=<generate with secrets.token_urlsafe(32)>
   JWT_ALGORITHM=HS256
   JWT_ACCESS_TOKEN_EXPIRE_MINUTES=15
   JWT_REFRESH_TOKEN_EXPIRE_DAYS=7
   BCRYPT_ROUNDS=12
   ALLOWED_ORIGINS=["*"]
   ```
4. Click **Settings** → **Build & Deploy**
5. Set **Pre-Deploy Command**: `alembic upgrade head`
6. Click **Save Changes**

### Step 3: Deploy

```bash
git add backend/
git commit -m "Add PostgreSQL + Auth + CRUD endpoints"
git push origin main
```

Render will:
1. Build Docker image
2. Run `alembic upgrade head` (create tables)
3. Start uvicorn
4. Run health checks
5. Go live 🎉

### Step 4: Test Production

```bash
# Health check
curl https://app-backend-9fkr.onrender.com/healthz

# Register
curl -X POST https://app-backend-9fkr.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"SecurePass123!"}'

# Login
curl -X POST https://app-backend-9fkr.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"SecurePass123!"}'

# API Docs
# Open: https://app-backend-9fkr.onrender.com/docs
```

## 🔐 Security Notes

### Environment Variables
- ✅ All secrets in Render Environment Variables (encrypted at rest)
- ✅ JWT keys rotatable without code changes
- ✅ `.env` in `.gitignore` (never commit secrets)

### Authentication
- ✅ Bcrypt with 12 rounds (adaptive cost)
- ✅ JWT with 15-minute access tokens
- ✅ Refresh tokens for 7 days
- ✅ Password validation (10+ chars, complexity)

### API Protection
- ✅ Rate limiting (auth: 5/15min, general: 60/min)
- ✅ CORS configured per environment
- ✅ SQL injection prevention (ORM + parameterized queries)
- ✅ Input validation (Pydantic schemas)

### Production Checklist
- [ ] Change `ALLOWED_ORIGINS` to specific domains
- [ ] Enable HTTPS (Render auto-configures)
- [ ] Monitor logs (Render Dashboard)
- [ ] Set up alerts (Render notifications)
- [ ] Database backups (Render auto-backup)

## 🧪 Testing

```bash
# Run all tests
pytest

# Run with verbose output
pytest -v

# Run specific test
pytest tests/test_auth.py::test_register_success

# Coverage report
pytest --cov=app --cov-report=html
open htmlcov/index.html
```

## 🔄 Database Migrations

```bash
# Create new migration (auto-generate)
alembic revision --autogenerate -m "Add new table"

# Review migration file in alembic/versions/

# Apply migrations
alembic upgrade head

# Rollback one migration
alembic downgrade -1

# Show current version
alembic current

# Show migration history
alembic history
```

## 📊 API Response Format

### Success Response
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful"
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "AUTH_INVALID_CREDENTIALS",
    "message": "Invalid email or password"
  }
}
```

### Paginated Response
```json
{
  "success": true,
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "per_page": 20,
    "total": 150,
    "pages": 8
  }
}
```

## 🛣️ Roadmap

### Sprint 3 (Next)
- [ ] Redis for rate limiting (distributed)
- [ ] Refresh token rotation
- [ ] Email verification
- [ ] Password reset
- [ ] WebSocket for real-time alerts
- [ ] Admin panel
- [ ] Logging (structured JSON)
- [ ] Monitoring (Sentry/Rollbar)

### Sprint 4
- [ ] Custom domains (api-dev/stage/prod.supplylens.io)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Load testing
- [ ] Performance optimization
- [ ] API versioning
- [ ] GraphQL API (optional)

## 📝 License

Copyright © 2025 Eurotax

## 🆘 Troubleshooting

**"alembic: command not found"**
```bash
# Ensure virtual environment is activated
pip install alembic
```

**"FATAL: database does not exist"**
```bash
# Check DATABASE_URL in .env
# Ensure PostgreSQL is running
# Create database: createdb supplylens
```

**"Could not validate credentials" (401)**
```bash
# Check JWT_SECRET_KEY matches
# Ensure token hasn't expired
# Verify Authorization header format: "Bearer <token>"
```

**Migration conflicts**
```bash
# Reset database (WARNING: deletes all data)
alembic downgrade base
alembic upgrade head
```

## 📞 Support

- GitHub Issues: https://github.com/eurotax/supplylens-android/issues
- Render Docs: https://render.com/docs
- FastAPI Docs: https://fastapi.tiangolo.com
