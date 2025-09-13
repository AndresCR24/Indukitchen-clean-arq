README.md

Bienvenido 👋 — al clonar este repositorio debes crear dos archivos en la raíz del proyecto (y opcionalmente un .env):

docker-compose.yaml — contiene la configuración de la base de datos (Postgres).

src/main/resources/application.properties — configuración de Spring Boot para conectar con la base de datos.

Opcional pero recomendado: .env (y agregar .env a .gitignore) para no commitear credenciales.

A continuación tienes todo listo para copiar y pegar en tu README.md.

1. docker-compose.yaml (ejemplo listo para usar)

   Crea en la raíz del repo un archivo llamado docker-compose.yaml y pega esto. Si prefieres, hay dos variantes abajo: con valores literales y con variables que leen .env.

   Opción — valores literales (lista para copiar)

CREACION DEL docker-compose.yaml:

services:

postgres:

image: 'postgres:latest'

environment:

- 'POSTGRES_DB='

- 'POSTGRES_PASSWORD='

- 'POSTGRES_USER='

ports:

- '5432:5432'

    volumes:

      - pgdata:/data/postgres

volumes:

pgdata: