CREATE TABLE IF NOT EXISTS USERS (
                                     USER_ID INT PRIMARY KEY AUTO_INCREMENT,
                                     EMAIL VARCHAR(255) NOT NULL UNIQUE,
                                     LOGIN VARCHAR(255) NOT NULL,
                                     NAME VARCHAR(255) NOT NULL,
                                     BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS MPA (
                                   MPA_ID INT PRIMARY KEY CHECK (MPA_ID BETWEEN 1 AND 5),
                                   NAME VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS FILMS (
                                     FILM_ID INT PRIMARY KEY AUTO_INCREMENT,
                                     NAME VARCHAR(255) NOT NULL,
                                     DESCRIPTION VARCHAR(255) NOT NULL,
                                     DURATION INT CHECK (DURATION > 0),
                                     MPA_ID INT,
                                     RELEASE_DATE DATE,
                                     CONSTRAINT FK_FILMS_MPA FOREIGN KEY (MPA_ID) REFERENCES MPA(MPA_ID)
);

CREATE TABLE IF NOT EXISTS GENRES (
                                      GENRE_ID INT PRIMARY KEY,
                                      NAME VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
                                          FILM_ID INT NOT NULL,
                                          GENRE_ID INT NOT NULL,
                                          PRIMARY KEY (FILM_ID, GENRE_ID),
                                          CONSTRAINT FK_FILM_GENRE_FILM FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID),
                                          CONSTRAINT FK_FILM_GENRE_GENRE FOREIGN KEY (GENRE_ID) REFERENCES GENRES(GENRE_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDS (
                                       USER_ID INT NOT NULL,
                                       FRIEND_ID INT NOT NULL,
                                       PRIMARY KEY (USER_ID, FRIEND_ID),
                                       CONSTRAINT FK_FRIENDS_USER FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
                                       CONSTRAINT FK_FRIENDS_FRIEND FOREIGN KEY (FRIEND_ID) REFERENCES USERS(USER_ID)
);

CREATE TABLE IF NOT EXISTS LIKES (
                                     FILM_ID INT NOT NULL,
                                     USER_ID INT NOT NULL,
                                     PRIMARY KEY (FILM_ID, USER_ID),
                                     CONSTRAINT FK_LIKES_FILM FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID),
                                     CONSTRAINT FK_LIKES_USER FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);
