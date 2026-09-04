-- This container is shared by user-service and listing-service only, for
-- local dev (see docker-compose.yaml) - every other service has its own
-- dedicated local Postgres container. Production uses Neon instead, with
-- its own databases created directly there, not through this script.
CREATE DATABASE userdb;
CREATE DATABASE listingdb;