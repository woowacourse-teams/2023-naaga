INSERT INTO member(id, email, password, created_at)
VALUES (1, '111@woowa.com', '1111', now());
INSERT INTO member(id, email, password, created_at)
VALUES (2, '222@woowa.com', '2222', now());
INSERT INTO member(id, email, password, created_at)
VALUES (3, '333@woowa.com', '3333', now());

INSERT INTO player(id, member_id, total_score, nickname, created_at)
VALUES (1, 1, 1000, 'krrong', now());
INSERT INTO player(id, member_id, total_score, nickname, created_at)
VALUES (2, 2, 1100, 'bixx', now());
INSERT INTO player(id, member_id, total_score, nickname, created_at)
VALUES (3, 3, 11000, 'bboddo', now());

INSERT INTO place(id, name, description, latitude, longitude, image_url, created_at, player_id)
VALUES (1, '잠실역교보분고', '이곳은 잠실역교보문고', 37.514258, 127.100883, '잠실역교보문고', now(), 1);
INSERT INTO place(id, name, description, latitude, longitude, image_url, created_at, player_id)
VALUES (2, '역삼역', '이곳은 역삼역', 37.500845, 127.036953, '역삼역', now(), 1);
INSERT INTO place(id, name, description, latitude, longitude, image_url, created_at, player_id)
VALUES (3, '파리바게트 방이시장점', '이곳은 파리바게트 방이시장점', 37.511737, 127.114016, '파리바게트 방이시장점', now(), 1);
INSERT INTO place(id, name, description, latitude, longitude, image_url, created_at, player_id)
VALUES (4, '1km', '이곳은 1km', 37.512256, 127.112584, '얘가 1km 안에 안정적으로 있어야함', now(), 1);

INSERT INTO game(id, game_status, player_id, place_id, start_latitude, start_longitude, remaining_attempts, start_time, end_time, created_at)
VALUES (1, 'DONE', 1, 1, 37.500845, 127.036953, 2, now(), now(), now());

INSERT INTO game(id, game_status, player_id, place_id, start_latitude, start_longitude, remaining_attempts, start_time, end_time, created_at)
VALUES (2, 'DONE', 1, 2, 37.500845, 127.036953, 1, now(), now(), now());

INSERT INTO game(id, game_status, player_id, place_id, start_latitude, start_longitude, remaining_attempts, start_time, end_time, created_at)
VALUES (3, 'DONE', 1, 3, 37.500845, 127.036953, 0, now(), now(), now());

INSERT INTO game_result(id, result_type, score, game_id, created_at)
VALUES (1, 'SUCCESS', 100, 1, now());

INSERT INTO game_result(id, result_type, score, game_id, created_at)
VALUES (2, 'FAIL', 0, 2, now());

INSERT INTO game_result(id, result_type, score, game_id, created_at)
VALUES (3, 'SUCCESS', 1000, 3, now());