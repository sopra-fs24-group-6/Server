/*
 This is the data that will be initially inserted into the database.
 */

-- User
INSERT INTO user (username, token, status, password, creation_date, birth_date)
VALUES
    ('user1', 'token1', 1, 'password1', '2024-01-01', '2000-01-01'),
    ('user2', 'token2', 1, 'password2', '2024-01-02', '2000-01-02'),
    ('user3', 'token3', 1, 'password3', '2024-01-03', '2000-01-03');

-- Theme
INSERT INTO THEME (name)
VALUES
    ('Animal'),
    ('Food');

-- Words
INSERT INTO WORD_PAIR (theme_id, first_word, second_word)
VALUES
    (1, 'Dog', 'Cat'),
    (1, 'Crow', 'Swan'),
    (2, 'Apple', 'Orange'),
    (2, 'Pasta', 'Pizza');
