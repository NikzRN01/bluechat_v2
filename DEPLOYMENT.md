# BlueChat v2 - Deployment Guide

This guide covers multiple deployment options to get your BlueChat application online.

## 🚀 Quick Deployment Options

### Option 1: Heroku (Recommended for Beginners)

**Prerequisites:**
- Heroku account (free): https://heroku.com
- Heroku CLI installed: https://devcenter.heroku.com/articles/heroku-cli
- Git repository (already set up)

**Steps:**
1. **Install Heroku CLI** and login:
   ```bash
   heroku login
   ```

2. **Create Heroku app:**
   ```bash
   heroku create your-bluechat-app-name
   ```

3. **Deploy:**
   ```bash
   git push heroku main
   ```

4. **Open your app:**
   ```bash
   heroku open
   ```

**Cost:** Free tier available (sleeps after 30 min of inactivity)

---

### Option 2: Railway (Modern & Easy)

**Prerequisites:**
- Railway account: https://railway.app
- GitHub account

**Steps:**
1. Push your code to GitHub
2. Go to Railway.app and sign in with GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your repository
5. Railway auto-detects Spring Boot and deploys

**Cost:** $5/month after free tier

---

### Option 3: Render (Good Free Tier)

**Prerequisites:**
- Render account: https://render.com
- GitHub account

**Steps:**
1. Push code to GitHub
2. Create new "Web Service" on Render
3. Connect your GitHub repo
4. Use these settings:
   - **Environment:** `Java`
   - **Build Command:** `chmod +x ./mvnw && ./mvnw clean package -DskipTests`
   - **Start Command:** `java -Dserver.port=$PORT -jar target/bluechat-v2-0.0.1-SNAPSHOT.jar`
5. **Environment Variables (Optional):**
   - `JAVA_OPTS`: `-Xmx300m -Xms300m`

**Cost:** Free tier available (slower startup)

---

### Option 4: Google Cloud Run (Serverless)

**Prerequisites:**
- Google Cloud account
- Docker installed
- gcloud CLI

**Steps:**
1. **Create Dockerfile:**
   ```dockerfile
   FROM openjdk:17-jdk-slim
   COPY target/bluechat-v2-0.0.1-SNAPSHOT.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

2. **Build and deploy:**
   ```bash
   mvn clean package -DskipTests
   gcloud run deploy bluechat --source . --platform managed --region us-central1 --allow-unauthenticated
   ```

**Cost:** Pay per use (very cheap for low traffic)

---

## 🔧 Pre-Deployment Checklist

- ✅ Procfile created
- ✅ system.properties configured
- ✅ Dynamic port configuration added
- ✅ Dependencies are production-ready
- ✅ Git repository is clean and committed

## 🌐 Making Your App Accessible

After deployment, your app will be available at:
- **Heroku:** `https://your-app-name.herokuapp.com`
- **Railway:** `https://your-app-name.up.railway.app`
- **Render:** `https://your-app-name.onrender.com`
- **Cloud Run:** `https://bluechat-[hash]-uc.a.run.app`

## 📱 Mobile Access

Your deployed chat app will work on:
- Desktop browsers
- Mobile browsers (iOS Safari, Android Chrome)
- Can be added to home screen as PWA

## 🔒 Security Notes

For production deployment, consider:
- Change default security credentials
- Add HTTPS (most platforms provide this automatically)
- Configure CORS if needed
- Add rate limiting

## 🐛 Troubleshooting

**Common Issues:**
- **App sleeping:** Use a service like UptimeRobot to ping your app
- **Memory issues:** Increase dyno size on Heroku
- **Build failures:** Check Java version compatibility

## 📞 Support

If you encounter issues:
1. Check application logs on your deployment platform
2. Verify all configuration files are correct
3. Test locally first with `mvn spring-boot:run`

---

**Recommendation:** Start with **Railway** or **Render** for the best balance of ease and reliability.

