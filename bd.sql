-- DROP SCHEMA cinema;

CREATE SCHEMA cinema AUTHORIZATION postgres;

-- DROP TYPE cinema.discount_type;

CREATE TYPE cinema.discount_type AS ENUM (
	'NO_DISCOUNT',
	'CHILD_DISCOUNT',
	'STUDENT_DISCOUNT',
	'SENIOR_DISCOUNT');

-- DROP SEQUENCE cinema.actors_seq;

CREATE SEQUENCE cinema.actors_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- Permissions

ALTER SEQUENCE cinema.actors_seq OWNER TO postgres;
GRANT ALL ON SEQUENCE cinema.actors_seq TO postgres;

-- DROP SEQUENCE cinema.directors_seq;

CREATE SEQUENCE cinema.directors_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- Permissions

ALTER SEQUENCE cinema.directors_seq OWNER TO postgres;
GRANT ALL ON SEQUENCE cinema.directors_seq TO postgres;

-- DROP SEQUENCE cinema.films_film_id_seq;

CREATE SEQUENCE cinema.films_film_id_seq
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 2147483647
	START 1
	CACHE 1
	NO CYCLE;

-- Permissions

ALTER SEQUENCE cinema.films_film_id_seq OWNER TO postgres;
GRANT ALL ON SEQUENCE cinema.films_film_id_seq TO postgres;

-- DROP SEQUENCE cinema.genres_seq;

CREATE SEQUENCE cinema.genres_seq
	INCREMENT BY 50
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- Permissions

ALTER SEQUENCE cinema.genres_seq OWNER TO postgres;
GRANT ALL ON SEQUENCE cinema.genres_seq TO postgres;
-- cinema.actors определение

-- Drop table

-- DROP TABLE cinema.actors;

CREATE TABLE cinema.actors ( actor_id int8 NOT NULL, "name" varchar(255) NOT NULL, CONSTRAINT actors_name_check CHECK (((name)::text <> ''::text)), CONSTRAINT actors_pkey PRIMARY KEY (actor_id));

-- Permissions

ALTER TABLE cinema.actors OWNER TO postgres;
GRANT ALL ON TABLE cinema.actors TO postgres;


-- cinema.directors определение

-- Drop table

-- DROP TABLE cinema.directors;

CREATE TABLE cinema.directors ( director_id int8 NOT NULL, "name" varchar(255) NOT NULL, CONSTRAINT directors_name_check CHECK (((name)::text <> ''::text)), CONSTRAINT directors_pkey PRIMARY KEY (director_id));

-- Permissions

ALTER TABLE cinema.directors OWNER TO postgres;
GRANT ALL ON TABLE cinema.directors TO postgres;


-- cinema.films определение

-- Drop table

-- DROP TABLE cinema.films;

CREATE TABLE cinema.films ( film_id bigserial NOT NULL, "name" varchar(255) NOT NULL, description text NOT NULL, release_date date NOT NULL, duration int4 NOT NULL, CONSTRAINT films_description_check CHECK (((length(description) >= 10) AND (length(description) <= 5000))), CONSTRAINT films_duration_check CHECK (((duration >= 1) AND (duration <= 300))), CONSTRAINT films_name_check CHECK (((name)::text <> ''::text)), CONSTRAINT films_pkey PRIMARY KEY (film_id));

-- Permissions

ALTER TABLE cinema.films OWNER TO postgres;
GRANT ALL ON TABLE cinema.films TO postgres;


-- cinema.genres определение

-- Drop table

-- DROP TABLE cinema.genres;

CREATE TABLE cinema.genres ( genre_id int8 NOT NULL, genre varchar(255) NOT NULL, CONSTRAINT genres_genre_check CHECK (((genre)::text <> ''::text)), CONSTRAINT genres_genre_key UNIQUE (genre), CONSTRAINT genres_pkey PRIMARY KEY (genre_id));

-- Permissions

ALTER TABLE cinema.genres OWNER TO postgres;
GRANT ALL ON TABLE cinema.genres TO postgres;


-- cinema.halls определение

-- Drop table

-- DROP TABLE cinema.halls;

CREATE TABLE cinema.halls ( hall_id int4 NOT NULL, number_seats int4 NOT NULL, CONSTRAINT halls_hall_id_check CHECK ((hall_id > 0)), CONSTRAINT halls_number_seats_check CHECK ((number_seats > 0)), CONSTRAINT halls_pkey PRIMARY KEY (hall_id));

-- Permissions

ALTER TABLE cinema.halls OWNER TO postgres;
GRANT ALL ON TABLE cinema.halls TO postgres;


-- cinema.users определение

-- Drop table

-- DROP TABLE cinema.users;

CREATE TABLE cinema.users ( email varchar(100) NOT NULL, "name" varchar(100) NOT NULL, "password" varchar(100) NOT NULL, CONSTRAINT users_email_check CHECK (((email)::text ~~ '%@%.%'::text)), CONSTRAINT users_name_check CHECK (((name)::text <> ''::text)), CONSTRAINT users_password_check CHECK (((length((password)::text) >= 8) AND ((password)::text ~ '[A-Za-z]'::text) AND ((password)::text ~ '[0-9]'::text))), CONSTRAINT users_pkey PRIMARY KEY (email));

-- Permissions

ALTER TABLE cinema.users OWNER TO postgres;
GRANT ALL ON TABLE cinema.users TO postgres;


-- cinema.films_actors определение

-- Drop table

-- DROP TABLE cinema.films_actors;

CREATE TABLE cinema.films_actors ( actor_id int8 NOT NULL, film_id int8 NOT NULL, CONSTRAINT films_actors_pkey PRIMARY KEY (actor_id, film_id), CONSTRAINT films_actors_actor_id_fkey FOREIGN KEY (actor_id) REFERENCES cinema.actors(actor_id) ON DELETE CASCADE, CONSTRAINT films_actors_film_id_fkey FOREIGN KEY (film_id) REFERENCES cinema.films(film_id) ON DELETE CASCADE);

-- Permissions

ALTER TABLE cinema.films_actors OWNER TO postgres;
GRANT ALL ON TABLE cinema.films_actors TO postgres;


-- cinema.films_directors определение

-- Drop table

-- DROP TABLE cinema.films_directors;

CREATE TABLE cinema.films_directors ( director_id int8 NOT NULL, film_id int8 NOT NULL, CONSTRAINT films_directors_pkey PRIMARY KEY (director_id, film_id), CONSTRAINT films_directors_director_id_fkey FOREIGN KEY (director_id) REFERENCES cinema.directors(director_id) ON DELETE CASCADE, CONSTRAINT films_directors_film_id_fkey FOREIGN KEY (film_id) REFERENCES cinema.films(film_id) ON DELETE CASCADE);

-- Permissions

ALTER TABLE cinema.films_directors OWNER TO postgres;
GRANT ALL ON TABLE cinema.films_directors TO postgres;


-- cinema.films_genres определение

-- Drop table

-- DROP TABLE cinema.films_genres;

CREATE TABLE cinema.films_genres ( genre_id int8 NOT NULL, film_id int8 NOT NULL, CONSTRAINT films_genres_pkey PRIMARY KEY (genre_id, film_id), CONSTRAINT films_genres_film_id_fkey FOREIGN KEY (film_id) REFERENCES cinema.films(film_id) ON DELETE CASCADE, CONSTRAINT films_genres_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES cinema.genres(genre_id) ON DELETE CASCADE);

-- Permissions

ALTER TABLE cinema.films_genres OWNER TO postgres;
GRANT ALL ON TABLE cinema.films_genres TO postgres;


-- cinema.sessions определение

-- Drop table

-- DROP TABLE cinema.sessions;

CREATE TABLE cinema.sessions ( session_id uuid DEFAULT gen_random_uuid() NOT NULL, film_id int8 NULL, hall_id int4 NULL, "date" date NOT NULL, start_time time NOT NULL, "cost" numeric(38, 2) NOT NULL, CONSTRAINT sessions_hall_id_date_start_time_key UNIQUE (hall_id, date, start_time), CONSTRAINT sessions_pkey PRIMARY KEY (session_id), CONSTRAINT sessions_film_id_fkey FOREIGN KEY (film_id) REFERENCES cinema.films(film_id) ON DELETE CASCADE, CONSTRAINT sessions_hall_id_fkey FOREIGN KEY (hall_id) REFERENCES cinema.halls(hall_id) ON DELETE CASCADE);

-- Permissions

ALTER TABLE cinema.sessions OWNER TO postgres;
GRANT ALL ON TABLE cinema.sessions TO postgres;


-- cinema.tickets определение

-- Drop table

-- DROP TABLE cinema.tickets;

CREATE TABLE cinema.tickets ( ticket_id uuid DEFAULT gen_random_uuid() NOT NULL, session_id uuid NULL, user_id varchar(255) NULL, "row" int4 NOT NULL, seat int4 NOT NULL, discount varchar(255) DEFAULT 'NO_DISCOUNT'::cinema.discount_type NULL, CONSTRAINT tickets_pkey PRIMARY KEY (ticket_id), CONSTRAINT tickets_row_check CHECK (("row" > 0)), CONSTRAINT tickets_seat_check CHECK ((seat > 0)), CONSTRAINT tickets_session_id_row_seat_key UNIQUE (session_id, "row", seat), CONSTRAINT tickets_session_id_fkey FOREIGN KEY (session_id) REFERENCES cinema.sessions(session_id) ON DELETE CASCADE, CONSTRAINT tickets_user_id_fkey FOREIGN KEY (user_id) REFERENCES cinema.users(email) ON DELETE CASCADE);

-- Permissions

ALTER TABLE cinema.tickets OWNER TO postgres;
GRANT ALL ON TABLE cinema.tickets TO postgres;



-- DROP FUNCTION cinema.armor(bytea);

CREATE OR REPLACE FUNCTION cinema.armor(bytea)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_armor$function$
;

-- Permissions

ALTER FUNCTION cinema.armor(bytea) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.armor(bytea) TO postgres;

-- DROP FUNCTION cinema.armor(bytea, _text, _text);

CREATE OR REPLACE FUNCTION cinema.armor(bytea, text[], text[])
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_armor$function$
;

-- Permissions

ALTER FUNCTION cinema.armor(bytea, _text, _text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.armor(bytea, _text, _text) TO postgres;

-- DROP FUNCTION cinema.crypt(text, text);

CREATE OR REPLACE FUNCTION cinema.crypt(text, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_crypt$function$
;

-- Permissions

ALTER FUNCTION cinema.crypt(text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.crypt(text, text) TO postgres;

-- DROP FUNCTION cinema.dearmor(text);

CREATE OR REPLACE FUNCTION cinema.dearmor(text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_dearmor$function$
;

-- Permissions

ALTER FUNCTION cinema.dearmor(text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.dearmor(text) TO postgres;

-- DROP FUNCTION cinema.decrypt(bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.decrypt(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_decrypt$function$
;

-- Permissions

ALTER FUNCTION cinema.decrypt(bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.decrypt(bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.decrypt_iv(bytea, bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.decrypt_iv(bytea, bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_decrypt_iv$function$
;

-- Permissions

ALTER FUNCTION cinema.decrypt_iv(bytea, bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.decrypt_iv(bytea, bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.digest(bytea, text);

CREATE OR REPLACE FUNCTION cinema.digest(bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_digest$function$
;

-- Permissions

ALTER FUNCTION cinema.digest(bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.digest(bytea, text) TO postgres;

-- DROP FUNCTION cinema.digest(text, text);

CREATE OR REPLACE FUNCTION cinema.digest(text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_digest$function$
;

-- Permissions

ALTER FUNCTION cinema.digest(text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.digest(text, text) TO postgres;

-- DROP FUNCTION cinema.encrypt(bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.encrypt(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_encrypt$function$
;

-- Permissions

ALTER FUNCTION cinema.encrypt(bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.encrypt(bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.encrypt_iv(bytea, bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.encrypt_iv(bytea, bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_encrypt_iv$function$
;

-- Permissions

ALTER FUNCTION cinema.encrypt_iv(bytea, bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.encrypt_iv(bytea, bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.gen_random_bytes(int4);

CREATE OR REPLACE FUNCTION cinema.gen_random_bytes(integer)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_random_bytes$function$
;

-- Permissions

ALTER FUNCTION cinema.gen_random_bytes(int4) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.gen_random_bytes(int4) TO postgres;

-- DROP FUNCTION cinema.gen_random_uuid();

CREATE OR REPLACE FUNCTION cinema.gen_random_uuid()
 RETURNS uuid
 LANGUAGE c
 PARALLEL SAFE
AS '$libdir/pgcrypto', $function$pg_random_uuid$function$
;

-- Permissions

ALTER FUNCTION cinema.gen_random_uuid() OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.gen_random_uuid() TO postgres;

-- DROP FUNCTION cinema.gen_salt(text, int4);

CREATE OR REPLACE FUNCTION cinema.gen_salt(text, integer)
 RETURNS text
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_gen_salt_rounds$function$
;

-- Permissions

ALTER FUNCTION cinema.gen_salt(text, int4) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.gen_salt(text, int4) TO postgres;

-- DROP FUNCTION cinema.gen_salt(text);

CREATE OR REPLACE FUNCTION cinema.gen_salt(text)
 RETURNS text
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_gen_salt$function$
;

-- Permissions

ALTER FUNCTION cinema.gen_salt(text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.gen_salt(text) TO postgres;

-- DROP FUNCTION cinema.hmac(text, text, text);

CREATE OR REPLACE FUNCTION cinema.hmac(text, text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_hmac$function$
;

-- Permissions

ALTER FUNCTION cinema.hmac(text, text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.hmac(text, text, text) TO postgres;

-- DROP FUNCTION cinema.hmac(bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.hmac(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pg_hmac$function$
;

-- Permissions

ALTER FUNCTION cinema.hmac(bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.hmac(bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.pgp_armor_headers(in text, out text, out text);

CREATE OR REPLACE FUNCTION cinema.pgp_armor_headers(text, OUT key text, OUT value text)
 RETURNS SETOF record
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_armor_headers$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_armor_headers(in text, out text, out text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_armor_headers(in text, out text, out text) TO postgres;

-- DROP FUNCTION cinema.pgp_key_id(bytea);

CREATE OR REPLACE FUNCTION cinema.pgp_key_id(bytea)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_key_id_w$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_key_id(bytea) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_key_id(bytea) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_decrypt(bytea, bytea);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_decrypt(bytea, bytea)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_decrypt(bytea, bytea) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_decrypt(bytea, bytea) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text, text);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_decrypt(bytea, bytea, text, text) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text, text);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea, text, text) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_decrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_decrypt_bytea(bytea, bytea) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_encrypt(text, bytea);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_encrypt(text, bytea)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_encrypt(text, bytea) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_encrypt(text, bytea) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_encrypt(text, bytea, text);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_encrypt(text, bytea, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_encrypt(text, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_encrypt(text, bytea, text) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea, text);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea, text) TO postgres;

-- DROP FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea);

CREATE OR REPLACE FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_pub_encrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_pub_encrypt_bytea(bytea, bytea) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_decrypt(bytea, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_decrypt(bytea, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_decrypt(bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_decrypt(bytea, text) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_decrypt(bytea, text, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_decrypt(bytea, text, text)
 RETURNS text
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_decrypt(bytea, text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_decrypt(bytea, text, text) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text, text) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text)
 RETURNS bytea
 LANGUAGE c
 IMMUTABLE PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_decrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_decrypt_bytea(bytea, text) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_encrypt(text, text, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_encrypt(text, text, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_encrypt(text, text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_encrypt(text, text, text) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_encrypt(text, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_encrypt(text, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_text$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_encrypt(text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_encrypt(text, text) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text, text) TO postgres;

-- DROP FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text);

CREATE OR REPLACE FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text)
 RETURNS bytea
 LANGUAGE c
 PARALLEL SAFE STRICT
AS '$libdir/pgcrypto', $function$pgp_sym_encrypt_bytea$function$
;

-- Permissions

ALTER FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text) OWNER TO postgres;
GRANT ALL ON FUNCTION cinema.pgp_sym_encrypt_bytea(bytea, text) TO postgres;


-- Permissions

GRANT ALL ON SCHEMA cinema TO postgres;
