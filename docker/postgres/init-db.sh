#!/bin/bash
# Se ejecuta SOLO la primera vez que se crea el volumen.
# Crea un usuario de aplicación (sin superusuario) y una BD de su propiedad.
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres <<-EOSQL
    CREATE ROLE ${DB_USER} LOGIN PASSWORD '${DB_PASSWORD}'
        NOSUPERUSER NOCREATEDB NOCREATEROLE;
    CREATE DATABASE ${DB_NAME} OWNER ${DB_USER} ENCODING 'UTF8';
    REVOKE ALL ON DATABASE ${DB_NAME} FROM PUBLIC;
EOSQL