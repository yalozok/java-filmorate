MERGE INTO MPA (MPA_ID, NAME)
VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');


MERGE INTO GENRES (GENRE_ID, NAME)
    VALUES
        (1,'Комедия'),
        (2,'Драма'),
        (3,'Мультфильм'),
        (4,'Триллер'),
        (5,'Документальный'),
        (6,'Боевик');

insert into USERS(EMAIL, LOGIN, NAME, BIRTHDAY)
values ('sveta@email.ru', 'sveta-login', 'Света', '1990-01-01'),
       ('vera@email.ru', 'vera-login', 'Вера', '1991-01-01'),
       ('eva@email.ru', 'eva-login', 'Ева', '1992-01-01'),
       ( 'nina@email.ru', 'nina-login', 'Нина',  '1993-01-01' );

insert into FRIENDS(USER_ID, FRIEND_ID) VALUES ( 1,2 ),
                                            (1,3),
                                            (2, 3),
                                            (3,4);

insert into FILMS( NAME, DESCRIPTION, DURATION, MPA_ID, RELEASE_DATE )
values('film1', 'description1', 160, 1, '1980-01-01'),
      ('film2', 'description2', 170, 2, '1981-01-01'),
      ('film3', 'description3', 180, 3, '1982-01-01'),
      ('film4', 'description4', 190, 4, '1983-01-01'),
      ('film5', 'description5', 195, 5, '1984-01-01'),
      ('film6', 'description6', 200, 1, '1985-01-01');

insert into FILM_GENRE(FILM_ID, GENRE_ID) VALUES ( 1,1 ),
                                                 (1, 3),
                                                 (2, 4),
                                                 (2, 6),
                                                 (3,5),
                                                 (4, 3),
                                                 (5, 1),
                                                 (5, 2),
                                                 (6, 6);

insert into LIKES(FILM_ID, USER_ID) values ( 1, 1 ),
                                           (1, 2),
                                           (1, 3),
                                           (2, 4),
                                           (2, 1),
                                           (3, 2),
                                           (4, 1),
                                           (4, 2),
                                           (4, 3),
                                           (4, 4);

