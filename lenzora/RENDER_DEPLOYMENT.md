# Deploy Lenzora on Render

The Render service uses the Dockerfile in this repository and receives its listening port from Render's `PORT` environment variable. It does not expose your laptop or its local MySQL server.

## Database

Before deploying, provide a MySQL database reachable from Render. `localhost` and private LAN addresses such as `10.x.x.x` are not reachable from Render. If your current data is in a local MySQL instance, create a hosted MySQL database and migrate the schema and account data first. If you use the existing shared MySQL database, confirm its network access rules allow Render and use its externally reachable connection details.

The blueprint sets `DDL_AUTO=none` to avoid changing a shared production schema. Ensure the database already has the application's current schema, including `license_number` and `profile_completed`, before deployment.

## Deploy

1. Push this repository to a GitHub repository connected to Render.
2. In Render, create a new **Blueprint** from that repository and review `render.yaml`.
3. When prompted, enter `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` as secret environment values. `DB_URL` must be a MySQL JDBC URL reachable from Render; do not use `localhost`.
4. Deploy and wait for the `/login` health check to pass. Render will provide a public HTTPS URL for members.
5. Use an existing `ADMIN` account in the configured database to review registrations. The development admin seeder is disabled in the `production` profile.

Do not put database passwords, Google OAuth secrets, or mail credentials in `render.yaml` or source control. Add optional OAuth and mail settings as Render environment variables if those features are needed.
