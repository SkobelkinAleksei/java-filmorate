INSERT INTO MOVIE_RATING (id, mpa)
VALUES (1, 'G');
INSERT INTO MOVIE_RATING (id, mpa)
VALUES (2, 'PG');
INSERT INTO MOVIE_RATING (id, mpa)
VALUES (3, 'PG-13');
INSERT INTO MOVIE_RATING (id, mpa)
VALUES (4, 'R');
INSERT INTO MOVIE_RATING (id, mpa)
VALUES (5, 'NC-17');

INSERT INTO genre (genre_id, genre_type) VALUES (1, 'Комедия');
INSERT INTO genre (genre_id, genre_type) VALUES (2, 'Драма');
INSERT INTO genre (genre_id, genre_type) VALUES (3, 'Мультфильм');
INSERT INTO genre (genre_id, genre_type) VALUES (4, 'Триллер');
INSERT INTO genre (genre_id, genre_type) VALUES (5, 'Документальный');
INSERT INTO genre (genre_id, genre_type) VALUES (6, 'Боевик');

INSERT INTO friend_status (status_name)
VALUES('WAITING_FOR_ANSWER'),
      ('FRIEND'),
      ('FRIEND_REQUEST');