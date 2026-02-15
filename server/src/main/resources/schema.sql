CREATE TABLE IF NOT EXISTS users (
	id int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
	"name" varchar NOT NULL,
	email varchar NOT NULL,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS requests (
	id int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
	description varchar(255) NOT NULL,
	requester_id int8 NOT NULL,
	created_date timestamp NOT NULL,
	CONSTRAINT requests_pk PRIMARY KEY (id),
	CONSTRAINT requests_users_fk FOREIGN KEY (requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
	id int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
	"name" varchar NOT NULL,
	description varchar(255) NOT NULL,
	is_available bool NOT NULL,
	owner_id int8 NULL,
	request_id int8 NULL,
	CONSTRAINT items_pk PRIMARY KEY (id),
	CONSTRAINT items_requests_fk
	    FOREIGN KEY (request_id)
	    REFERENCES requests(id)
	    ON DELETE CASCADE,
	CONSTRAINT items_users_fk
	    FOREIGN KEY (owner_id)
	    REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_items_owner ON items(owner_id);

CREATE TABLE IF NOT EXISTS bookings (
	id int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
	booker_id int8 NOT NULL,
	item_id int8 NOT NULL,
	status varchar(8) NOT NULL,
	CONSTRAINT bookings_check
	    CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')),
	CONSTRAINT bookings_pk PRIMARY KEY (id),
	CONSTRAINT bookings_items_fk FOREIGN KEY (item_id) REFERENCES items(id),
	CONSTRAINT bookings_users_fk FOREIGN KEY (booker_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_bookings_item_status_dates ON bookings(item_id, status, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_bookings_booker_status ON bookings(booker_id, status);
CREATE INDEX IF NOT EXISTS idx_bookings_item_id ON bookings(item_id);

CREATE TABLE IF NOT EXISTS "comments" (
	id int8 GENERATED ALWAYS AS IDENTITY NOT NULL,
	"text" varchar(500) NOT NULL,
	author_id int8 NOT NULL,
	item_id int8 NOT NULL,
	created_date timestamp NOT NULL,
	CONSTRAINT comments_pk PRIMARY KEY (id),
	CONSTRAINT comments_items_fk FOREIGN KEY (item_id) REFERENCES items(id),
	CONSTRAINT comments_users_fk FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_comments_author ON "comments"(author_id);
CREATE INDEX IF NOT EXISTS idx_comments_item ON "comments"(item_id);