CREATE TABLE endpoint_hit (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app         VARCHAR(50) NOT NULL,
    uri         VARCHAR(2000) NOT NULL,
    ip          VARCHAR(50) NOT NULL,
    dates       TIMESTAMP WITHOUT TIME ZONE NOT NULL
);