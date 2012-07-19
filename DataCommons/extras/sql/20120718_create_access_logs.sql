/**
 * 20120718_create_access_logs.sql
 * 
 * Australian National University Data Commons
 * 
 * Creates table 'access_logs' which stores records of CRUD operations on resources.
 */

CREATE TABLE access_logs
(
  id bigserial NOT NULL,
  uri character varying(255) NOT NULL,
  user_fk bigint NOT NULL,
  operation integer NOT NULL,
  "timestamp" timestamp without time zone NOT NULL,
  ip_address character varying(255) NOT NULL,
  CONSTRAINT access_logs_pkey PRIMARY KEY (id ),
  CONSTRAINT fkc27387aaec35b08 FOREIGN KEY (user_fk)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)