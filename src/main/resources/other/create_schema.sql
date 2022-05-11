CREATE TABLE customer (
	id BIGSERIAL NOT NULL PRIMARY KEY,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
    birth_date
    shopping_cart_id BIGINT,
	email VARCHAR(90) NOT NULL UNIQUE,
	phone_number VARCHAR(20) UNIQUE,
	address VARCHAR(50),
	city VARCHAR(30),
	created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP NOT NULL,
	admin BOOLEAN NOT NULL DEFAULT False,
    CONSTRAINT fk_customer_cart
    FOREIGN KEY (shopping_cart_id)
    REFERENCES shopping_cart(id)
);

CREATE TABLE collection (
    name VARCHAR(50) NOT NULL PRIMARY KEY,
    image VARCHAR(100)
);

CREATE TABLE comic (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    collection_id BIGINT,
	number INT,
	price DECIMAL(5,2) NOT NULL,
	quantity INT NOT NULL,
	image VARCHAR(100),
	writers VARCHAR(50),
	draftsmans VARCHAR(50),
	pages INT,
    format_binding VARCHAR(50)
	isbn VARCHAR(50) NOT NULL UNIQUE,
	description TEXT,
    CONSTRAINT fk_comic_collection
    FOREIGN KEY (collection_id)
    REFERENCES collection(id)
    ON DELETE NO ACTION
    ON UPDATE CASCADE
);

CREATE TABLE shopping_cart (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    total DECIMAL(7,2) NOT NULL,
    size INT,
    modified_at TIMESTAMP NOT NULL
);

CREATE TABLE cart_content (
    
);

CREATE TABLE cart_item (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    shopping_cart_id BIGINT NOT NULL,
    comic_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_cartitem_shoppingcart
    FOREIGN KEY (shopping_cart_id)
    REFERENCES shopping_cart(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT fk_cartitem_comic
    FOREIGN KEY (comic_id)
    REFERENCES comic(id)
    ON DELETE NO ACTION
    ON UPDATE CASCADE
);

CREATE TABLE order_details (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    total DECIMAL(7,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP,
    CONSTRAINT fk_orderdetails_customer
    FOREIGN KEY (customer_id)
    REFERENCES customer(id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

CREATE TABLE order_item (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    order_details_id BIGINT NOT NULL,
    comic_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_orderitem_orderdetails
    FOREIGN KEY (order_details_id)
    REFERENCES order_details(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CONSTRAINT fk_orderitem_comic
    FOREIGN KEY (comic_id)
    REFERENCES comic(id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);