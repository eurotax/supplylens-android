# SupplyLens Backend (FastAPI)

Minimal FastAPI backend for SupplyLens Android application.

## 🚀 MVP Features

- **Health Check**: `GET /healthz` → `{"status": "ok"}`
- **Root Endpoint**: `GET /` → `{"ok": true}`

## 📋 Tech Stack

- **Framework**: FastAPI 0.114.0
- **Server**: Uvicorn 0.30.0
- **Python**: 3.11

## 🏃 Local Development

### Prerequisites
- Python 3.11+
- pip

### Setup

```bash
cd backend

# Create virtual environment
python -m venv venv

# Activate virtual environment
# Windows:
venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run development server
uvicorn app:app --reload --host 0.0.0.0 --port 8000
```

API will be available at: `http://localhost:8000`

Interactive docs: `http://localhost:8000/docs`

## 🐳 Docker

### Build Image

```bash
docker build -t supplylens-backend .
```

### Run Container

```bash
docker run -p 8000:8000 supplylens-backend
```

## 🌍 Deployment on Render.com

### Step 1: Create New Web Service

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **New +** → **Web Service**
3. Connect your GitHub repository: `eurotax/supplylens-android`

### Step 2: Configure Service

**Basic Settings:**
- **Name**: `supplylens-backend`
- **Region**: `Frankfurt (EU Central)`
- **Branch**: `main`
- **Root Directory**: `backend`
- **Runtime**: `Docker`

**Build & Deploy:**
- **Docker Command**: (leave empty - will use Dockerfile)
- **Plan**: Free tier for MVP

### Step 3: Environment Variables

*(None required for MVP - add later when implementing DB/Auth)*

Example for future:
```
DATABASE_URL=postgresql://...
JWT_SECRET=your-secret-key
LOG_LEVEL=INFO
```

### Step 4: Deploy

Click **Create Web Service** - Render will:
1. Clone the repo
2. Build Docker image from `backend/Dockerfile`
3. Deploy to Frankfurt
4. Assign URL: `https://supplylens-backend-xxxx.onrender.com`

### Step 5: Test Deployment

```bash
# Health check
curl https://your-app.onrender.com/healthz

# Root endpoint
curl https://your-app.onrender.com/
```

## 🔗 Custom Domains (Future)

To use custom domains (`api-dev.supplylens.io`, etc.):

1. Go to **Settings** → **Custom Domain**
2. Add your domain
3. Update DNS records (provided by Render)
4. Wait for SSL certificate provisioning

## 📊 Monitoring

Render provides built-in monitoring:
- **Logs**: Real-time container logs
- **Metrics**: CPU, Memory, Request counts
- **Events**: Deployment history

## 🔐 Security Notes

**MVP Configuration:**
- ✅ CORS: `allow_origins=["*"]` (temporary for testing)
- ⚠️ **TODO**: Restrict CORS to specific Android app domains
- ⚠️ **TODO**: Add rate limiting (SlowAPI)
- ⚠️ **TODO**: Add authentication (JWT)
- ⚠️ **TODO**: Add HTTPS enforcement

## 🛣️ Roadmap

**Next Sprint:**
- [ ] PostgreSQL database integration
- [ ] SQLAlchemy models
- [ ] JWT authentication
- [ ] Alerts CRUD endpoints (`/api/v1/alerts`)
- [ ] Watchlist CRUD endpoints (`/api/v1/watchlist`)
- [ ] User management endpoints
- [ ] Rate limiting
- [ ] CORS restriction
- [ ] Logging and monitoring
- [ ] Unit tests

## 📚 API Documentation

Once deployed, interactive API documentation is available at:
- Swagger UI: `https://your-app.onrender.com/docs`
- ReDoc: `https://your-app.onrender.com/redoc`

## 🐛 Troubleshooting

**Container fails to start:**
- Check Render logs for Python errors
- Verify `requirements.txt` versions are compatible
- Ensure `PORT` environment variable is used (Render auto-assigns it)

**502 Bad Gateway:**
- Check if app is listening on `0.0.0.0:$PORT` (not `127.0.0.1`)
- Verify Dockerfile CMD uses `${PORT:-8000}`

**CORS errors from Android app:**
- Currently `allow_origins=["*"]` should work
- If issues persist, explicitly add Android app domains

## 📝 License

Copyright © 2025 Eurotax
