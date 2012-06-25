CREATE TABLE published (
	fedora_id				bigint		NOT NULL
	, location_id			bigint		NOT NULL
	, PRIMARY KEY (fedora_id, location_id)
	, CONSTRAINT fk_published_fedora foreign key (fedora_id) references fedora_object(id)
	, CONSTRAINT fk_published_location foreign key (location_id) references publish_location(id)
)