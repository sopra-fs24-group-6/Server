/*
 This is the data that will be initially inserted into the database.
 */

-- User
INSERT INTO user (username, token, status, password, creation_date, birth_date, language, wins, losses, winlossratio)
VALUES
    ('user1', 'token1', 1, '111', '2024-01-01', '2000-01-01', 'en', 20, 13, 1.335),
    ('user2', 'token2', 1, '222', '2024-01-02', '2000-01-02', 'zh', 40, 60, 0.748),
    ('user3', 'token3', 1, '333', '2024-01-03', '2000-01-03', 'de', 36, 27, 0.571),
    ('user4', 'token4', 1, '444', '2024-01-04', '2000-01-04', 'ja', 11, 1, 1.263),
    ('user5', 'token5', 1, '555', '2024-01-05', '2000-01-05', 'fr', 22, 22, 0.846);

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
