
-- User
INSERT INTO user (username, token, status, password, creation_date, birth_date, language, wins, losses, winlossratio, avatar_url)
VALUES
    ('user1', 'token1', 1, '111', '2024-01-01', '2000-01-01', 'en', 20, 13, 1.335, '/images/avatar/villager.png'),
    ('user2', 'token2', 1, '222', '2024-01-02', '2000-01-02', 'zh', 40, 60, 0.748, '/images/avatar/wolf.png'),
    ('user3', 'token3', 1, '333', '2024-01-03', '2000-01-03', 'de', 36, 27, 0.571, '/images/avatar/demon.png'),
    ('user4', 'token4', 1, '444', '2024-01-04', '2000-01-04', 'ja', 11, 1, 1.263, '/images/avatar/frightened_villager.png'),
    ('user5', 'token5', 1, '555', '2024-01-05', '2000-01-05', 'fr', 22, 22, 0.846, '/images/avatar/good_wolf.png'),
    ('user6', 'token6', 1, '111', '2024-01-06', '2000-01-06', 'es', 38, 12, 2.051, '/images/avatar/hunter.png'),
    ('user7', 'token7', 1, '111', '2024-01-07', '2000-01-07', 'hi', 7, 2, 1.521, '/images/avatar/knight.png'),
    ('user8', 'token8', 1, '111', '2024-01-08', '2000-01-08', 'ar', 62, 22, 2.158, '/images/avatar/magician.png'),
    ('user9', 'token9', 1, '111', '2024-01-09', '2000-01-09', 'pt', 108, 2, 2.986, '/images/avatar/nobody.png'),
    ('user10', 'token10', 1, '111', '2024-01-10', '2000-01-10', 'bn', 24, 5, 2.037, '/images/avatar/ogre.png'),
    ('user11', 'token11', 1, '111', '2024-01-11', '2000-01-11', 'ru', 89, 43, 2.103, '/images/avatar/suspicious_dude.png');
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
