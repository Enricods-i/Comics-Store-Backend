CREATE TABLE personal_data (
		id BIGSERIAL PRIMARY KEY,
		first_name VARCHAR(50) NOT NULL,
		last_name VARCHAR(50) NOT NULL,
		birth_date DATE NOT NULL,
		email VARCHAR(90) NOT NULL UNIQUE,
		phone_number VARCHAR(20),
		address VARCHAR(50),
		city VARCHAR(30),
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE collection (
		name VARCHAR(50) PRIMARY KEY,
		image VARCHAR(60),
		description VARCHAR(1000)
);

CREATE TABLE cart_data (
    	user_id BIGINT PRIMARY KEY REFERENCES personal_data (id),
    	total FLOAT NOT NULL,
    	size SMALLINT NOT NULL,
    	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comic (
    	id BIGSERIAL PRIMARY KEY,
    	collection VARCHAR(50) NOT NULL REFERENCES collection (name) ON UPDATE CASCADE,
		number SMALLINT NOT NULL,
		price FLOAT NOT NULL,
		quantity SMALLINT NOT NULL,
		image VARCHAR(60),
		writers VARCHAR(50),
		catoonists VARCHAR(50),
    	format_and_binding VARCHAR(30),
		pages SMALLINT,
		isbn VARCHAR(13) UNIQUE,
		description VARCHAR(200),
		version LONG,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_content(
    	cart BIGINT REFERENCES cart_data (user_id),
   		comic BIGINT REFERENCES comic (id),
   		quantity SMALLINT NOT NULL,
    	PRIMARY KEY (cart,comic)
);

CREATE TABLE wish_list(
    	id BIGSERIAL PRIMARY KEY,
    	user_id BIGINT NOT NULL REFERENCES personal_data (id),
    	name VARCHAR(70) NOT NULL,
    	notifications BOOLEAN DEFAULT FALSE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE list_content(
    	list BIGINT REFERENCES wish_list (id) ON DELETE CASCADE,
    	comic BIGINT REFERENCES comic (id),
    	PRIMARY KEY (list,comic)
);

CREATE TABLE category(
    	name VARCHAR(50) PRIMARY KEY,
    	description VARCHAR(200)
);

CREATE TABLE classification(
    	category VARCHAR(50) REFERENCES category (name),
    	collection VARCHAR(50) REFERENCES collection (name),
    	PRIMARY KEY (category,collection)
);

CREATE TABLE discount(
    	id BIGSERIAL PRIMARY KEY,
    	percentage SMALLINT NOT NULL,
    	expiration_date DATE NOT NULL,
    	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE promotion(
    	comic BIGINT REFERENCES comic (id),
    	discount BIGINT REFERENCES discount (id),
    	PRIMARY KEY (comic,discount)
);

CREATE TABLE purchase(
    	id BIGSERIAL PRIMARY KEY,
    	user_id BIGINT NOT NULL REFERENCES personal_data (id),
    	total FLOAT NOT NULL,
    	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comic_in_purchase(
    	id BIGSERIAL PRIMARY KEY,
    	purchase BIGINT NOT NULL REFERENCES purchase (id),
    	comic BIGINT NOT NULL REFERENCES comic (id),
    	price FLOAT NOT NULL,
    	quantity SMALLINT NOT NULL
);

CREATE TABLE discount_application(
    	comic BIGINT REFERENCES comic (id),
    	discount BIGINT REFERENCES discount (id),
    	PRIMARY KEY (comic,discount)
);
