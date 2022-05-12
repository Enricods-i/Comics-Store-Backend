CREATE TABLE personal_data (
	id BIGSERIAL PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
	email VARCHAR(90) NOT NULL UNIQUE,
	phone_number VARCHAR(20),
	address VARCHAR(50),
	city VARCHAR(30),
	created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE collection (
    name VARCHAR(50) PRIMARY KEY,
    image VARCHAR(100),
    description VARCHAR(1000)
);

CREATE TABLE cart_data (
    user BIGINT PRIMARY KEY REFERENCES personal_data (id),
    total DECIMAL(7,2) NOT NULL,
    size INT,
    modified_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE comic (
    id BIGSERIAL PRIMARY KEY,
    collection BIGINT REFERENCES collection (name),
	number INT,
	price DECIMAL(5,2) NOT NULL,
	quantity INT NOT NULL,
	image VARCHAR(100),
	writers VARCHAR(50),
	catoonists VARCHAR(50),
    format_and_binding 
	pages INT,
    format_binding VARCHAR(50)
	isbn VARCHAR(13) NOT NULL UNIQUE,
	description VARCHAR(200),
	created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cart_content(
    cart BIGINT REFERENCES cart_data (user),
    comic BIGINT REFERENCES comic (id),
    quantity INT
    PRIMARY KEY (cart,comic)
);

CREATE TABLE wish_list(
    id BIGSERIAL PRIMARY KEY,
    user BIGINT REFERENCES personal_data (id),
    name VARCHAR(70) NOT NULL,
    notifications BOOLEAN DEFAULT FALSE,
	created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE list_content(
    list BIGINT REFERENCES wish_list (id),
    comic BIGINT REFERENCES comic (id),
    PRIMARY KEY (list,comic)
);

CREATE TABLE category(
    name VARCHAR(50) PRIMARY KEY,
    description VARCHAR(200)
);

CREATE TABLE classification(
    category BIGINT REFERENCES category (name),
    collection BIGINT REFERENCES collection (name),
    PRIMARY KEY (category,collection)
);

CREATE TABLE discount(
    id BIGSERIAL PRIMARY KEY,
    percentage
    expiration_date
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);