# AfincoApp - Guia de Setup e Configuração

## Sumário
1. [Pré-requisitos](#pre-requisitos)
2. [Setup Inicial](#initial-setup)
3. [Configuração de Banco de Dados](#configuracao-banco-de-dados)
4. [Configuração de Armazenamento no Supabase](#configuracao-armazenamento-supabase)
5. [Processo de Construção](#processo-construcao)
6. [Running the Application](#running-the-application)
7. [Troubleshooting](#troubleshooting)
8. [Development Setup](#development-setup)

---

## Prerequisites

### System Requirements

| Requirement | Minimum Version | Recommended |
|-------------|-----------------|-------------|
| Java | 17 | 17 LTS or 21 LTS |
| Maven | 3.6+ | 3.9+ |
| PostgreSQL | 12+ | 14+ (opcional em caso de utilização do Supabase) |
| Git | 2.0+ | Latest |
| RAM | 2GB | 4GB+ |
| Disk Space | 500MB | 1GB+ |

### Required Software

#### 1. Install Java 17

**Windows**:
```bash
# Using Chocolatey
choco install openjdk17

# Or download from: https://adoptium.net/
```

**Linux (Ubuntu/Debian)**:
```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk
```

**macOS**:
```bash
# Using Homebrew
brew install openjdk@17

# Add to PATH if needed
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

**Verify Installation**:
```bash
java -version
# Output: openjdk version "17.x.x" ...
```

#### 2. Install Maven

**Windows**:
```bash
# Using Chocolatey
choco install maven

# Or download from: https://maven.apache.org/download.cgi
```

**Linux**:
```bash
sudo apt-get install maven
```

**macOS**:
```bash
brew install maven
```

**Verify Installation**:
```bash
mvn -version
# Output: Apache Maven 3.x.x ...
```

#### 3. Git (Optional but Recommended)

**Windows**:
```bash
choco install git
```

**Linux**:
```bash
sudo apt-get install git
```

**macOS**:
```bash
brew install git
```

---

## Initial Setup

### Step 1: Obtain the Project

**Option A: Clone from Repository**
```bash
git clone <repository-url> AfincoApp
cd AfincoApp
```

**Option B: Extract from Archive**
```bash
# Extract the .zip file
unzip AfincoApp.zip
cd AfincoApp
```

### Step 2: Verify Project Structure

Ensure the project has this structure:
```
AfincoApp/
├── pom.xml                    # Maven configuration
├── mvnw / mvnw.cmd           # Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── AfincoTeam/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
└── target/                    # Will be created during build
```

### Step 3: Create Local Configuration

Copy the example configuration:
```bash
# Create a local configuration file (optional)
cp src/main/resources/application.properties src/main/resources/application-local.properties
```

---

## Database Configuration

### Option 1: Using Supabase (Recommended for Development)

#### Create Supabase Account

1. Go to [https://supabase.com](https://supabase.com)
2. Sign up for a free account
3. Create a new project
4. Choose region (preferably closest to you)
5. Wait for database to be ready

#### Get Connection Credentials

1. Go to project settings → Database
2. Under "Connection string", select URI
3. Copy the full connection string
4. Extract credentials:
   - **Host**: Extract from connection string
   - **Port**: Usually 5432
   - **Database**: postgres
   - **Username**: postgres
   - **Password**: Your chosen password

#### Update application.properties

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/postgres?sslmode=require
spring.datasource.username=postgres.YOUR_PROJECT_ID
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.auto-commit=true

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.jdbc.fetch_size=50

# Session Configuration
server.servlet.session.timeout=28800s
```

### Option 2: Local PostgreSQL Database

#### Install PostgreSQL

**Windows**:
```bash
choco install postgresql
```

**Linux (Ubuntu)**:
```bash
sudo apt-get install postgresql postgresql-contrib
```

**macOS**:
```bash
brew install postgresql
```

#### Create Database and User

```bash
# Connect to PostgreSQL
psql -U postgres

# In psql prompt, run:
CREATE DATABASE afincoapp;
CREATE USER afincouser WITH PASSWORD 'your_password_here';
ALTER ROLE afincouser SET client_encoding TO 'utf8';
ALTER ROLE afincouser SET default_transaction_isolation TO 'read committed';
ALTER ROLE afincouser SET default_transaction_deferrable TO on;
ALTER ROLE afincouser SET default_transaction_read_committed TO on;
GRANT ALL PRIVILEGES ON DATABASE afincoapp TO afincouser;
\q
```

#### Update application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/afincoapp
spring.datasource.username=afincouser
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Test Database Connection

```bash
# Using psql
psql -h localhost -U afincouser -d afincoapp

# Or with connection string
psql "postgresql://afincouser:password@localhost:5432/afincoapp"
```

---

## Supabase Storage Configuration

### Create Supabase Storage Bucket

1. Go to Supabase dashboard
2. Navigate to Storage
3. Create new bucket named `AfincoStorage`
4. Set bucket to public
5. Allow file uploads

### Get API Credentials

1. Go to Settings → API
2. Copy the following:
   - **API URL**: Your project URL
   - **Anon Key**: Public API key
   - **Service Role Key**: Private API key (for backend)
   - **Project ID**: From project info

### Update application.properties

```properties
# Supabase Storage Configuration
supabase.storage.url=https://YOUR_PROJECT_ID.supabase.co
supabase.storage.api-key=YOUR_ANON_KEY
supabase.storage.bucket-name=AfincoStorage
supabase.storage.project-id=YOUR_PROJECT_ID
```

### Example Complete application.properties

```properties
# ============ Database Configuration ============
spring.datasource.url=jdbc:postgresql://aws-1-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require
spring.datasource.username=postgres.YOUR_PROJECT_ID
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# ============ Connection Pooling ============
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=60000
spring.datasource.hikari.auto-commit=true

# ============ JPA/Hibernate ============
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.jdbc.fetch_size=50

# ============ Session Configuration ============
server.servlet.session.timeout=28800s

# ============ Supabase Storage ============
supabase.storage.url=https://YOUR_PROJECT_ID.supabase.co
supabase.storage.api-key=YOUR_ANON_KEY
supabase.storage.bucket-name=AfincoStorage
supabase.storage.project-id=YOUR_PROJECT_ID

# ============ Application Server ============
server.port=8080
spring.application.name=AfincoApp

# ============ Logging ============
logging.level.root=INFO
logging.level.AfincoTeam=DEBUG
```

---

## Build Process

### Option 1: Using Maven Wrapper (Recommended)

**Windows**:
```bash
# Navigate to project directory
cd AfincoApp

# Clean and build
mvnw.cmd clean install

# Or just compile
mvnw.cmd clean compile
```

**Linux/macOS**:
```bash
cd AfincoApp
./mvnw clean install
```

### Option 2: Using System Maven

```bash
mvn clean install
```

### Build Commands Reference

| Command | Purpose |
|---------|---------|
| `mvn clean` | Remove target directory |
| `mvn compile` | Compile source code |
| `mvn test` | Run unit tests |
| `mvn package` | Create JAR file |
| `mvn clean install` | Full build (recommended) |
| `mvn install -DskipTests` | Build without tests |

### Build Output

Successful build output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXs
[INFO] Finished at: YYYY-MM-DDTHH:MM:SS+HH:MM
[INFO] Final Memory: XXMb/XXXMb
```

### Verify Build

```bash
# Check if target/AfincoApp-0.0.1-SNAPSHOT.jar was created
ls target/AfincoApp-0.0.1-SNAPSHOT.jar

# Or on Windows
dir target\AfincoApp-0.0.1-SNAPSHOT.jar
```

---

## Running the Application

### Option 1: Using Maven

**Development Mode** (with automatic reload):
```bash
mvn spring-boot:run
```

**Development Mode with Debugging**:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

### Option 2: Using JAR File

**Build and Run**:
```bash
mvn clean install
java -jar target/AfincoApp-0.0.1-SNAPSHOT.jar
```

**Run with Custom Configuration**:
```bash
java -jar target/AfincoApp-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://host:5432/db \
  --spring.datasource.username=user \
  --spring.datasource.password=pass
```

### Option 3: Using IDE (IntelliJ or Eclipse)

**IntelliJ IDEA**:
1. Open project
2. Right-click on `AfincoAppApplication.java`
3. Select "Run 'AfincoAppApplication.main()'"

**Eclipse**:
1. Right-click on project
2. Run As → Spring Boot App

### Verify Application is Running

```bash
# Check if server is running on port 8080
curl http://localhost:8080/login

# On Windows (PowerShell)
Invoke-WebRequest http://localhost:8080/login

# Check port in use
# Windows
netstat -ano | findstr :8080

# Linux/macOS
lsof -i :8080
```

### Application Startup Output

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v4.0.5)

2026-04-22 10:30:45.123  INFO 12345 --- [main] AfincoTeam.AfincoAppApplication: Starting AfincoAppApplication
...
2026-04-22 10:30:50.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer: Tomcat started on port(s): 8080 (http) with context path ''
2026-04-22 10:30:50.789  INFO 12345 --- [main] AfincoTeam.AfincoAppApplication: Started AfincoAppApplication in X.XXXs (JVM running for X.XXXs)
```

### Access the Application

Open your browser and navigate to:
```
http://localhost:8080/login
```

You should see the login page.

---

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: Port 8080 Already in Use

**Error Message**:
```
Failed to bind to port 8080
```

**Solution**:
```bash
# Option 1: Change port in application.properties
server.port=8081

# Option 2: Kill the process using the port
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -i :8080
kill -9 <PID>
```

#### Issue 2: Database Connection Failed

**Error Message**:
```
Connection refused
HikariPool cannot be started - connection time out
```

**Solution**:
```bash
# 1. Verify database is running
# For PostgreSQL
psql -U postgres

# 2. Check credentials in application.properties
# 3. Test connection manually
psql -h <host> -U <username> -d <database>

# 4. Ensure PostgreSQL is accepting connections
# Check pg_hba.conf for connection rules
```

#### Issue 3: Maven Build Fails

**Error Message**:
```
[ERROR] COMPILATION ERROR
[ERROR] Failed to execute goal org.apache.maven.plugins
```

**Solution**:
```bash
# 1. Clean Maven cache
mvn clean

# 2. Clear local repository
rm -rf ~/.m2/repository/
# or Windows: rm -r %USERPROFILE%\.m2\repository\

# 3. Rebuild
mvn clean install -DskipTests

# 4. Check Java version
java -version

# 5. Check Maven version
mvn -version
```

#### Issue 4: Thymeleaf Template Not Found

**Error Message**:
```
Could not resolve template
Error resolving template "login.html"
```

**Solution**:
```bash
# 1. Verify template files exist
# Windows: dir src\main\resources\templates\
# Linux: ls src/main/resources/templates/

# 2. Check application.properties
# Ensure no custom template path is set

# 3. Rebuild application
mvn clean install

# 4. Restart application
```

#### Issue 5: Supabase Connection Error

**Error Message**:
```
Failed to connect to Supabase
SSL connection error
```

**Solution**:
```properties
# 1. Verify URL and credentials in application.properties
# 2. Ensure ?sslmode=require is in connection string
# 3. Check firewall/network access to Supabase

# 4. If still failing, try without SSL (development only):
spring.datasource.url=jdbc:postgresql://host:5432/db?sslmode=disable
```

#### Issue 6: Out of Memory Error

**Error Message**:
```
Exception in thread "main" java.lang.OutOfMemoryError
```

**Solution**:
```bash
# Increase heap size
java -Xmx1024m -jar target/AfincoApp-0.0.1-SNAPSHOT.jar

# Or set environment variable
# Windows
set JAVA_OPTS=-Xmx1024m
mvnw.cmd spring-boot:run

# Linux/macOS
export JAVA_OPTS=-Xmx1024m
./mvnw spring-boot:run
```

### Logging and Debugging

#### Enable Debug Logging

**Option 1: In application.properties**
```properties
logging.level.root=DEBUG
logging.level.AfincoTeam=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**Option 2: Via command line**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

#### View Application Logs

```bash
# View logs during execution
# Logs appear in console

# Save logs to file
java -jar target/AfincoApp-0.0.1-SNAPSHOT.jar > app.log 2>&1

# Tail logs in real-time
tail -f app.log
```

---

## Development Setup

### IDE Configuration

#### IntelliJ IDEA

1. **Import Project**:
   - File → Open → Select AfincoApp directory
   - Choose "Maven" as build tool

2. **Configure JDK**:
   - File → Project Structure → Project
   - SDK: Select Java 17
   - Language Level: 17

3. **Configure Run Configuration**:
   - Run → Edit Configurations
   - Click "+" and select "Spring Boot"
   - Main class: `AfincoTeam.AfincoAppApplication`
   - Environment variables: Set any needed

#### Eclipse/STS

1. **Import Project**:
   - File → Import → Maven → Existing Maven Projects
   - Select AfincoApp directory

2. **Configure JDK**:
   - Project → Properties
   - Java Compiler: Set to Java 17
   - JRE: Set to Java 17

#### VS Code

1. **Install Extensions**:
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack

2. **Open Project**:
   - File → Open Folder → AfincoApp
   - Wait for Maven dependencies to load

3. **Create Launch Configuration**:
   - Debug → Add Configuration
   - Select "Java" and "Spring Boot"

### Maven Profiles (Optional)

Create profiles for different environments in `pom.xml`:

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

Use profiles:
```bash
mvn spring-boot:run -Pdev
```

### Environment-Specific Configurations

Create separate property files:

```
src/main/resources/
├── application.properties        # Default
├── application-dev.properties    # Development
├── application-prod.properties   # Production
└── application-test.properties   # Testing
```

Activate profiles via environment variable:
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

---

## Próximos Passos

1. **Reveja a Documentação**:
   - Leia [README.md](./README.md) for overview
   - Cheque [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) for endpoints
   - Estude [ARCHITECTURE.md](./ARCHITECTURE.md) para 

2. **Crie um Usuário para Testes**:
   - Navigate to registration page
   - Create a test account
   - Test login functionality

3. **Explore a Aplicação**:
   - Access dashboard
   - Browse content
   - Try questionnaires

4. **Reveja o Código**:
   - Examine controller classes
   - Study service implementations
   - Review model entities

---

## Support

Em caso de problemas, dúvidas, ou perguntas:
1. Verifique a sessão de troubleshooting acima
2. Reveja a documentação do Spring Boot: https://spring.io/projects/spring-boot
3. Veja a documentação do Supabase: https://supabase.com/docs
4. Contate o time de desenvolvimento AfincoTeam

---

**Última atualização**: 23 de Abril, 2026
**Versão**: 1.0
**Aplicação**: AfincoApp v0.0.1-SNAPSHOT