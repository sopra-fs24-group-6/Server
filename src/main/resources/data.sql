/*
 This is the data that will be initially inserted into the database.
 */

-- User
INSERT INTO USER (id, name, username, token, status, password, creation_date, birthday)
VALUES
    (1, 'user1', 'user1', 'user1', 1, 'user1', '2024-01-01', '2000-01-01'),
    (2, 'user2', 'user2', 'user2', 1, 'user2', '2024-01-02', '2000-01-02'),
    (3, 'user3', 'user3', 'user3', 1, 'user3', '2024-01-03', '2000-01-03');

-- Theme
INSERT INTO THEME (id, name)
VALUES
    (1, 'Animal'),
    (2, 'Food');

-- Words
INSERT INTO WORD_PAIR (id, theme_id, first_word, second_word)
VALUES
    (1, 1, 'Dog', 'Cat'),
    (2, 1, 'Crow', 'Swan'),
    (3, 2, 'Apple', 'Orange'),
    (4, 2, 'Pasta', 'Pizza');
