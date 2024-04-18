/*
 This is the data that will be initially inserted into the database.
 */

-- User
INSERT INTO user (username, token, status, password, creation_date, birth_date, language)
VALUES
    ('user1', 'token1', 1, '111', '2024-01-01', '2000-01-01', 'en'),
    ('user2', 'token2', 1, '222', '2024-01-02', '2000-01-02', 'zh'),
    ('user3', 'token3', 1, '333', '2024-01-03', '2000-01-03', 'de'),
    ('user4', 'token4', 1, '444', '2024-01-04', '2000-01-04', 'ja'),
    ('user5', 'token5', 1, '555', '2024-01-05', '2000-01-05', 'fr');

-- Theme
INSERT INTO THEME (name)
VALUES
    ('Animal'),
    ('Food'),
    ('Sports');

-- Words
INSERT INTO WORD_PAIR (theme_id, first_word, second_word)
VALUES
    (1, 'Dog', 'Cat'),
    (1, 'Elephant', 'Giraffe'),
    (1, 'Crow', 'Swan'),
    (2, 'Apple', 'Orange'),
    (2, 'Coffee', 'Tea'),
    (2, 'Pasta', 'Pizza'),
    (3, 'Baseball', 'Basketball'),
    (3, 'Soccer', 'Rugby'),
    (3, 'Golf', 'Tennis');
