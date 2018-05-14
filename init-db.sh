#!/bin/bash

echo ""
echo "*****"
echo "Creating Karm DB"
echo "*****"
echo ""

dropdb karm --username postgres --host localhost
#dropdb test --username postgres --host localhost

sudo -u postgres bash -c "psql -c \"DROP ROLE karm;\""
sudo -u postgres bash -c "psql -c \"CREATE ROLE karm LOGIN PASSWORD 'karm' SUPERUSER INHERIT CREATEDB CREATEROLE NOREPLICATION;\""

sudo -u postgres bash -c "psql -c \"CREATE DATABASE karm WITH OWNER = karm ENCODING = 'UTF8' TABLESPACE = pg_default LC_COLLATE = 'en_GB.UTF-8' LC_CTYPE = 'en_GB.UTF-8' CONNECTION LIMIT = -1 TEMPLATE template0;\""
sudo -u postgres bash -c "psql -c \"GRANT ALL ON DATABASE karm TO karm;\""
sudo -u postgres bash -c "psql -c \"GRANT ALL ON DATABASE karm TO fc;\""
sudo -u postgres bash -c "psql -c \"REVOKE ALL ON DATABASE karm FROM public;\""
