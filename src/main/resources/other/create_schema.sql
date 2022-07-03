CREATE TABLE cart_data (
		id BIGSERIAL PRIMARY KEY,
    	size SMALLINT NOT NULL,
    	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE personal_data (
		id BIGSERIAL PRIMARY KEY,
		first_name VARCHAR(50) NOT NULL,
		last_name VARCHAR(50) NOT NULL,
		birth_date DATE NOT NULL,
		cart_id BIGINT REFERENCES cart_data(id),
		email VARCHAR(90) NOT NULL UNIQUE,
		phone_number VARCHAR(20),
		city VARCHAR(30),
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE collection (
		name VARCHAR(50) PRIMARY KEY,
		price FLOAT NOT NULL,
		image VARCHAR(60),
    	format_and_binding VARCHAR(30),
		color BOOLEAN,
		description VARCHAR(1000),
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comic (
    	id BIGSERIAL PRIMARY KEY,
    	collection_id VARCHAR(50) NOT NULL REFERENCES collection (name) ON UPDATE CASCADE,
		number SMALLINT NOT NULL,
		quantity SMALLINT NOT NULL,
		image VARCHAR(60),
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
    	PRIMARY KEY (cart,comic)
);

CREATE TABLE author (
		id BIGSERIAL PRIMARY KEY,
		name VARCHAR(30) NOT NULL
);

CREATE TABLE authors (
		author_id BIGINT REFERENCES author(id),
		comic_id BIGINT REFERENCES comic(id),
		PRIMARY KEY (author_id, comic_id)
);

CREATE TABLE wish_list(
    	id BIGSERIAL PRIMARY KEY,
    	user_id BIGINT NOT NULL REFERENCES personal_data (id),
    	name VARCHAR(70) NOT NULL,
    	notifications BOOLEAN DEFAULT FALSE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE list_content(
    	wish_list_id BIGINT REFERENCES wish_list (id) ON DELETE CASCADE,
    	comic_id BIGINT REFERENCES comic (id),
    	PRIMARY KEY (wish_list_id, comic_id)
);

CREATE TABLE category(
    	name VARCHAR(50) PRIMARY KEY,
    	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classification(
    	category_id VARCHAR(50) REFERENCES category (name) ON UPDATE CASCADE,
    	collection_id VARCHAR(50) REFERENCES collection (name) ON UPDATE CASCADE,
    	PRIMARY KEY (category_id, collection_id)
);

CREATE TABLE discount(
    	id BIGSERIAL PRIMARY KEY,
    	percentage SMALLINT NOT NULL,
    	expiration_date DATE NOT NULL,
    	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE promotion(
    	comic_id BIGINT REFERENCES comic (id),
    	discount_id BIGINT REFERENCES discount (id),
    	PRIMARY KEY (comic_id, discount_id)
);

CREATE TABLE purchase(
    	id BIGSERIAL PRIMARY KEY,
    	user_id BIGINT NOT NULL REFERENCES personal_data (id),
    	total FLOAT NOT NULL,
    	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comic_in_purchase(
    	id BIGSERIAL PRIMARY KEY,
    	purchase_id BIGINT NOT NULL REFERENCES purchase (id),
    	comic_id BIGINT NOT NULL REFERENCES comic (id),
    	price FLOAT NOT NULL,
    	quantity SMALLINT NOT NULL
);

CREATE TABLE discount_application(
    	comic_in_purchase_id BIGINT REFERENCES comic_in_purchase (id),
    	discount_id BIGINT REFERENCES discount (id),
    	PRIMARY KEY (comic_in_purchase_id, discount_id)
);
