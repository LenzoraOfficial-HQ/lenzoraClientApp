# lenzoraClientApp

> The Maven project lives in the `lenzora/` subfolder. **All build/run commands must be run from `lenzora/`.**

---

## 🧰 Tech Stack

| Layer      | Technology                                        |
|------------|---------------------------------------------------|
| Language   | Java 21                                           |
| Framework  | Spring Boot 4.1.0                                 |
| Web        | Spring Web MVC                                    |
| Templates  | Thymeleaf (+ Thymeleaf Spring Security extras)    |
| Security   | Spring Security                                   |
| Persistence| Spring Data JPA (MySQL driver included)           |
| Tooling    | Lombok, Maven (via included wrapper `mvnw`)       |
| Dev        | Spring Boot DevTools (auto-restart + live reload) |

---

## ✅ Prerequisites

| Requirement | Version        | Notes                                             |
|-------------|----------------|---------------------------------------------------|
| JDK         | **21**         | Required. Older/newer JDKs will fail the build.   |
| Maven       | *not required* | The Maven Wrapper (`mvnw`) downloads Maven for you.|
| MySQL       | 8.x (optional) | Needed for the login and order screens.     |
| IDE         | any            | VS Code, IntelliJ IDEA, or Eclipse/STS all work.  |

Check your Java version:

```bash
java -version
```

It should report `openjdk version "21..."`. If not, install **Temurin 21 (Adoptium)** or your preferred JDK 21 and point `JAVA_HOME` at it.

---

## 🚀 Getting Started (init & run)

### 1. Clone the repository

```bash
git clone <repo-url>
cd lenzoraClientApp
```

### 2. Open the project

Open the `lenzoraClientApp` folder in your IDE. The Maven project is at `lenzora/`.

### 3. Run the application

From the `lenzora/` directory:

**Windows (PowerShell / CMD):**
```powershell
cd lenzora
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**
```bash
cd lenzora
./mvnw spring-boot:run
```

> On the **first run** the wrapper downloads Maven plus all dependencies, so it may take a few minutes. Subsequent runs are fast.

### 4. Verify it's running

Open your browser and go to:

```
http://localhost:8081
```

You should see the **Lenzora** welcome page. (Note: the app runs on port **8081**, not the default 8080.)

### 5. Stop the app

Press `Ctrl + C` in the terminal running the app.

---

## 📦 Common Commands

Run these from inside `lenzora/`.

| Task                          | Command                                   |
|-------------------------------|-------------------------------------------|
| Run the app (dev mode)        | `.\mvnw.cmd spring-boot:run` (Win) / `./mvnw spring-boot:run` (Mac/Linux) |
| Compile + run tests           | `.\mvnw.cmd test`                         |
| Build a runnable JAR          | `.\mvnw.cmd package`                      |
| Clean previous build output   | `.\mvnw.cmd clean`                        |
| Run the packaged JAR          | `java -jar target/lenzora-0.0.1-SNAPSHOT.jar` |

---

## ⚙️ Configuration

Main config file: `lenzora/src/main/resources/application.properties`

| Property                            | Value  | Purpose                                        |
|-------------------------------------|--------|------------------------------------------------|
| `spring.application.name`           | lenzora| App name used by logging/actuator.             |
| `server.port`                       | 8081   | HTTP port the app listens on.                  |
| `spring.thymeleaf.cache=false`      | —      | Disables template caching (dev convenience).   |
| `spring.devtools.restart.enabled`   | true   | Auto-restart on code changes.                  |
| `spring.devtools.livereload.enabled`| true   | Live-reload browser pages during development.  |

---

## 🗄️ Database (MySQL)

The connection settings come from `lenzora/.env` (copy `lenzora/.env.example` to `lenzora/.env`,
which is never committed). If there is no `.env`, the app falls back to a local MySQL server.

### Option A — your own local MySQL (no `.env` needed)

1. Start a MySQL server on `localhost:3306`.
2. Run the app from the `lenzora/` folder (`cd lenzora; .\mvnw.cmd spring-boot:run`).

The default URL is `jdbc:mysql://localhost:3306/lenzora?createDatabaseIfNotExist=true…`, so the
`lenzora` database is created for you and Hibernate builds the tables it needs (`app_user`,
`app_order`, `order_item`, `prescription`, `frame`, `lens_option`, `supplier`, …) on the first start.

Set `ADMIN_EMAIL` and `ADMIN_PASSWORD` in `lenzora/.env` before the first start. The configured
administrator is seeded only while `app_user` is empty. These credentials are not updated for an
administrator that already exists in the database.

### Option B — the shared team database (Aiven)

Put the shared connection details in `lenzora/.env`:

```properties
DB_URL=jdbc:mysql://<host>:<port>/defaultdb?ssl-mode=REQUIRED&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=<user>
DB_PASSWORD=<password>
DDL_AUTO=none
```

> **`DDL_AUTO` decides who owns the schema.** `none` means "the database already has the tables —
> never change them", which is required for the shared database. `update` lets Hibernate create
> missing tables, which is what a private local database needs — it is the default when no `.env`
> is present.

### "Table 'lenzora.app_user' doesn't exist"

The app reached a database with no tables. Two causes:

* `DB_URL` in `lenzora/.env` points at an empty database while `DDL_AUTO=none` is in effect — remove
  the `DDL_AUTO` line (or set it to `update`) so Hibernate can create the tables, or
* the DMS schema script was run separately: it loads its tables into `defaultdb`, so `DB_URL` must
  name that same database.

---

## 🧱 Project Structure

```
lenzoraClientApp/
├── README.md
├── .gitignore
└── lenzora/                     ← Maven / Spring Boot project
    ├── pom.xml                  ← Dependencies & build config
    ├── mvnw / mvnw.cmd          ← Maven Wrapper (no global Maven needed)
    ├── .mvn/wrapper/            ← Wrapper config
    ├── HELP.md                  ← Spring Initializr helper (ignored)
    └── src/
        ├── main/
        │   ├── java/com/lenzora/official/system/lenzora/
        │   │   ├── LenzoraApplication.java   ← App entry point
        │   │   └── Home.java                 ← "/" controller → index template
        │   └── resources/
        │       ├── application.properties    ← App configuration
        │       └── templates/index.html      ← Home page (Thymeleaf)
        └── test/java/...                     ← Tests
```

---

## 🛠️ IDE Setup (recommended)

### VS Code
- Install the **Extension Pack for Java** and the **Spring Boot Extension Pack**.
- Optional: **Lombok Annotations Support** extension.
- Run the app with the **Spring Boot Dashboard** or the `mvnw` commands above.

### IntelliJ IDEA
- Open `lenzora/pom.xml` as a project (Maven imports automatically).
- Enable **annotation processing**: `Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable`.
- The **Lombok plugin** is bundled; keep it enabled.

### Eclipse / Spring Tool Suite
- Run `java -jar lombok.jar` to install the Lombok agent into your Eclipse installation.
- Import `lenzora/pom.xml` via `File → Import → Existing Maven Projects`.

---

## 🔍 Troubleshooting

| Problem | Solution |
|---------|----------|
| `java: invalid source release: 21` | Your JDK isn't 21. Install JDK 21 and set `JAVA_HOME`. |
| Port 8081 already in use | Change `server.port` in `application.properties`, or stop the process using the port. |
| First build very slow | Normal — Maven wrapper downloads the toolchain + dependencies once. |
| Changes not showing in browser | DevTools live-reload needs the page to be re-opened; make sure you started with `spring-boot:run` (not the packaged JAR). |
| Lombok methods not generated in IDE | Enable annotation processing (see IDE setup above). |

---

## 🤝 Contributing

- Branch from `main` using a descriptive name, e.g. `feature/login`, `fix/home-page`.
- Keep changes focused and run `.\mvnw.cmd test` before opening a PR.
- Follow the existing package structure under `com.lenzora.official.system.lenzora`.
- Update this README if you change build steps, ports, or required tooling.

---
