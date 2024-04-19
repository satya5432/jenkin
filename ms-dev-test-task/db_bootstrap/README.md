#Readme:
This folder contains the database initialization script. 

Create database "fortumotest" and include the structure & data definitions in there from the bootstrap.sql

Example bootstrap process :
```
-- connect to your postgresql at first
template1=# create database fortumo_test_task;
  CREATE DATABASE
template1=# create user fortumo_test_user superuser password 'fortumo_test_password';
  CREATE ROLE
template1=# grant all privileges on database fortumo_test_task to fortumo_test_user;
  GRANT
```

Now connect to the database as fortumo_test_user

```
$ psql -h 127.0.0.1 -U fortumo_test_user fortumo_test_task
Password for user fortumo_test_user: 
psql (9.6.5, server 9.3.19)
SSL connection (protocol: TLSv1.2, cipher: DHE-RSA-AES256-GCM-SHA384, bits: 256, compression: off)
Type "help" for help.

fortumo_test_task=# \i bootstrap.sql 
CREATE TABLE
CREATE TABLE
CREATE TABLE
CREATE TABLE

```

**Note**: this will take some time

In the SQL file I have introduced SQL for creating index on some columns to improve performance.
