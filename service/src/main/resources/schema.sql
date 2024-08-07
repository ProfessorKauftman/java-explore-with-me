CREATE TABLE IF NOT EXISTS locations(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS compilations(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title VARCHAR(50) NOT NULL,
    pinned BOOLEAN NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit BIGINT NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(50) NOT NULL,
    title VARCHAR(120) NOT NULL,
    views BIGINT NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_events_to_categories FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_users FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_locations FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_requests(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT pk_event_request PRIMARY KEY (id),
    CONSTRAINT fk_requests_to_events FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events_compilations(
    event_id BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT pk_events_compilations PRIMARY KEY (event_id, compilation_id),
    CONSTRAINT fk_ec_to_events FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_ec_to_compilations FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    CONSTRAINT uq_events_in_compilation UNIQUE (event_id, compilation_id)
);

CREATE TABLE IF NOT EXISTS friends(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    friendship_state BOOLEAN NOT NULL,
    CONSTRAINT pk_friends PRIMARY KEY (id),
    CONSTRAINT fk_friendships_user_to_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_friendships_friend_to_user FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE
);