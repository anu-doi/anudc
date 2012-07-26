create table review_ready (
	id	bigint	not null
	, date_submitted	timestamp	not null
	, PRIMARY KEY (id)
	, CONSTRAINT fk_review_fedora FOREIGN KEY (id) REFERENCES fedora_object(id)
);

create table review_reject (
	id					bigint		not null
	, date_submitted	timestamp	not null
	, reason			text		not null
	, PRIMARY KEY (id)
	, CONSTRAINT fk_review_fedora FOREIGN KEY (id) REFERENCES fedora_object(id)
);

create table publish_ready (
	id	bigint	not null
	, date_submitted	timestamp	not null
	, PRIMARY KEY (id)
	, CONSTRAINT fk_publish_fedora FOREIGN KEY (id) REFERENCES fedora_object(id)
);