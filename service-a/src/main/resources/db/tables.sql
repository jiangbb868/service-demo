CREATE TABLE IF NOT EXISTS tbl_user(
   user_id serial,
   user_name character varying NOT NULL,
   age integer NOT NULL,
   birthday date,
   CONSTRAINT tbl_user_pk PRIMARY KEY (user_id)
);