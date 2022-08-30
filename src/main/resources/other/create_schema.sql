CREATE TABLE cart_data (
	id BIGSERIAL PRIMARY KEY,
	size SMALLINT NOT NULL,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE personal_data (
	id BIGSERIAL PRIMARY KEY,
	first_name VARCHAR(20) NOT NULL,
	last_name VARCHAR(20) NOT NULL,
	birth_date DATE NOT NULL,
	cart_id BIGINT NOT NULL REFERENCES cart_data (id),
	email VARCHAR(50) NOT NULL UNIQUE,
	phone_number VARCHAR(20),
	city VARCHAR(20),
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE collection (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL UNIQUE,
	price FLOAT NOT NULL,
	year_of_release INT,
	format_and_binding VARCHAR(30),
	color BOOLEAN,
	description VARCHAR(1000),
	version BIGINT,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comic (
    id BIGSERIAL PRIMARY KEY,
    collection_id BIGINT REFERENCES collection (id),
	number SMALLINT NOT NULL,
	quantity SMALLINT NOT NULL,
	pages SMALLINT,
	isbn VARCHAR(13) UNIQUE NOT NULL,
	publication_date DATE,
	description VARCHAR(200),
	version BIGINT,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_content(
	cart_id BIGINT REFERENCES cart_data (id),
	comic_id BIGINT REFERENCES comic (id),
	quantity SMALLINT NOT NULL,
    PRIMARY KEY (cart_id, comic_id)
);

CREATE TABLE author (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(20) NOT NULL UNIQUE,
	biography VARCHAR(1000),
	version BIGINT,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE authors (
	author_id BIGINT REFERENCES author (id),
	comic_id BIGINT REFERENCES comic (id),
	PRIMARY KEY (author_id, comic_id)
);

CREATE TABLE wish_list (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES personal_data (id),
    name VARCHAR(30) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE list_content (
	wish_list_id BIGINT REFERENCES wish_list (id),
	comic_id BIGINT REFERENCES comic (id),
	PRIMARY KEY (wish_list_id, comic_id)
);

CREATE TABLE category (
	id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
	version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classification (
	category_id BIGINT REFERENCES category (id),
	collection_id BIGINT REFERENCES collection (id),
	PRIMARY KEY (category_id, collection_id)
);

CREATE TABLE discount (
	id BIGSERIAL PRIMARY KEY,
	name VARCHAR(30),
	percentage SMALLINT NOT NULL,
	expiration_date DATE NOT NULL,
	activation_date DATE NOT NULL,
	version BIGINT,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE promotion (
	comic_id BIGINT REFERENCES comic (id),
	discount_id BIGINT REFERENCES discount (id),
	PRIMARY KEY (comic_id, discount_id)
);

CREATE TABLE purchase (
	id BIGSERIAL PRIMARY KEY,
	user_id BIGINT NOT NULL REFERENCES personal_data (id),
	total FLOAT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comic_in_purchase (
	id BIGSERIAL PRIMARY KEY,
	purchase_id BIGINT NOT NULL REFERENCES purchase (id),
	comic_id BIGINT NOT NULL REFERENCES comic (id),
	comic_price FLOAT NOT NULL,
	comic_quantity SMALLINT NOT NULL
);

CREATE TABLE discount_application (
	comic_in_purchase_id BIGINT REFERENCES comic_in_purchase (id),
	discount_id BIGINT REFERENCES discount (id),
	PRIMARY KEY (comic_in_purchase_id, discount_id)
);
