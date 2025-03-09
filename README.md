# java-filmorate
Template repository for Filmorate project.
https://dbdiagram.io/d/67cd689f263d6cf9a0ba5837

## Добавить нового пользователя 
INSERT INTO users 
(
    name,
    email,
    login,
    birthday,
    created_at
)
VALUES 
(
    'Alex',
    'skobelkin@inbox.ru',
    'login',
    '2000-10-10',
    NOW()
);

## Получить всех пользователей 
SELECT * FROM users;

## Обновить данные пользователя
UPDATE users 
SET name = 'new Name'
WHERE email = 'skobelkin@inbox.ru';

## Удалить пользователя 
DELETE FROM users
WHERE email = 'skobelkin@inbox.ru';

## Добавить друга 
INSERT INTO friends(user_id, friend_id, created_at)
VALUES(1, 2, NOW());

## Получить всех друзей пользователя 
SELECT u.*
FROM friends AS f
JOIN users AS u ON u.id = f.friend_id
WHERE f.user_id = 10;

## Удалить друга
DELETE FROM friends
WHERE user_id = 1 
AND friend_id = 2;

## Добавить новый фильм 
INSERT INTO movies
(
    name,
    description,
    release_date,
    duration
)
VALUES
(
    'Film',
    'descr',
    '2000-10-10',
    90
);

## Удалить фильм 
DELETE FROM movies 
WHERE name = 'Film';

## Обновить данные фильма 
UPDATE movies
SET description = 'new descr'
WHERE name = 'Film';

## Получение фильмов после 2000-х гг.
SELECT *
FROM movies
WHERE release_date > 2000;

## Пользователь ставит лайк фильму
INSERT INTO user_likes(user_id, movie_id, liked_at)
VALUES (1, 2, NOW());

## Получить список фильмов, которые пользователь лайкнул
SELECT m.*
FROM user_likes AS ul 
JOIN movies AS m ON ul.movie_id = m.id
WHERE ul.user_id = 1;

## Удалить лайк 
DELETE FROM user_likes
WHERE user_id = 1
AND movie_id = 2; 