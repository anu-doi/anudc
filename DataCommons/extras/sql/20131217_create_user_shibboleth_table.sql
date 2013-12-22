CREATE TABLE user_shibboleth (
	id				bigint	NOT NULL UNIQUE
	, display_name	text
	, email			text
	, institution	text
	, CONSTRAINT fk_user_shibboleth_1 FOREIGN KEY (id) REFERENCES users (id)
);

INSERT INTO users_type (type_name) VALUES ('Shibboleth User');