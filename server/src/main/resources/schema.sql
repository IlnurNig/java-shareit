DROP TABLE IF EXISTS users, items, bookings, comments, requests;


CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(255)                            NOT NULL,
    email   VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);


CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(1000),
    created      TIMESTAMP                               NOT NULL,
    requester_id BIGINT                                  NOT NULL,

    CONSTRAINT pk_request PRIMARY KEY (request_id),
    CONSTRAINT fk_requester_id FOREIGN KEY (requester_id) REFERENCES users (user_id)
);


CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    available   BOOLEAN,
    request_id  BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_items_to_request FOREIGN KEY (request_id) REFERENCES requests (request_id)
);


CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP                               NOT NULL,
    end_time   TIMESTAMP                               NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    status     VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items (item_id),
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users (user_id)
);


CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text       VARCHAR(500)                            NOT NULL,
    item_id    BIGINT                                  NOT NULL,
    author_id  BIGINT                                  NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items (item_id),
    CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users (user_id)
);