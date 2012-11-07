CREATE TABLE log_external_web_resource
(
  id bigserial NOT NULL,
  pid character varying(255),
  http_request text NOT NULL,
  request_timestamp timestamp without time zone NOT NULL,
  http_response text,
  response_timestamp timestamp without time zone,
  CONSTRAINT log_external_web_resource_pkey PRIMARY KEY (id )
);

CREATE TABLE log_webservice
(
  id bigserial NOT NULL,
  function_name character varying(255),
  requestor_ip character varying(255),
  http_request text NOT NULL,
  request_timestamp timestamp without time zone,
  http_response text,
  response_timestamp timestamp without time zone,
  CONSTRAINT log_webservice_pkey PRIMARY KEY (id )
);

