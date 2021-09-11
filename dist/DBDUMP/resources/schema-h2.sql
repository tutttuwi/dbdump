/*
-- DROP TABLES
drop table if exists fruit;
*/

/*
-- DROP SCHEMA
drop schema if exists dev;

-- CREATE DATABASE
create schema dev;

*/

--------------------------------------------------------------------------------

-- DROP TABLES
drop table if exists fruit;

-- Table : fruit
create table fruit (
  id integer not null
  , name character varying(10)
  , price integer
  , primary key (id)
);


-- INSERT文
MERGE INTO FRUIT KEY (ID) VALUES (1,'APPLE',300);
MERGE INTO FRUIT KEY (ID) VALUES (2,'ORANGE',200);
MERGE INTO FRUIT KEY (ID) VALUES (3,'BANANA',100);
MERGE INTO FRUIT KEY (ID) VALUES (4,'PINEAPPLE',456);
MERGE INTO FRUIT KEY (ID) VALUES (5,'PPAP',100000000);
-- insert into fruit(id,name,price) values (1,'APPLE',300);
-- insert into fruit(id,name,price) values (2,'ORANGE',200);
-- insert into fruit(id,name,price) values (3,'BANANA',100);
-- insert into fruit(id,name,price) values (4,'PINEAPPLE',456);
-- insert into fruit(id,name,price) values (5,'PPAP',100000000);


