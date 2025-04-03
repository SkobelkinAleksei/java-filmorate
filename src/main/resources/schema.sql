CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30),
    email VARCHAR(100) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    birthday DATE NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS friend_status (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    status_name VARCHAR(20) NOT NULL CHECK(status_name IN ('WAITING_FOR_ANSWER',
                                                           'FRIEND',
                                                           'FRIEND_REQUEST'))
);

CREATE TABLE IF NOT EXISTS user_status (
    user_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    PRIMARY KEY(user_id, status_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (status_id) REFERENCES friend_status (id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    PRIMARY KEY(user_id, friend_id),
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS movie_genre (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    genre_name VARCHAR(30) NOT NULL CHECK(genre_name IN ('COMEDY',
                                                         'DRAMA',
                                                         'CARTOON',
                                                         'THRILLER',
                                                         'DOCUMENTARY',
                                                         'ACTION'))
);

CREATE TABLE IF NOT EXISTS movie_rating (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    rating VARCHAR(10) NOT NULL CHECK(rating IN ('G',
                                                 'PG',
                                                 'PG13',
                                                 'R',
                                                 'NC17'))
);

CREATE TABLE IF NOT EXISTS movies (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL UNIQUE,
    description VARCHAR(200),
    releaseDate DATE NOT NULL,
    duration INT NOT NULL,
    genre_id BIGINT NOT NULL,
    rating_id BIGINT NOT NULL,
    FOREIGN KEY (genre_id) REFERENCES movie_genre (id),
    FOREIGN KEY (rating_id) REFERENCES movie_rating (id)
);

CREATE TABLE IF NOT EXISTS user_likes (
    user_id BIGINT,
    movie_id BIGINT,
    liked_at TIMESTAMP,
    PRIMARY KEY(user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (movie_id) REFERENCES movies (id)
);