create table Users(
    id int primary key generated by default as identity,
    email VARCHAR not null UNIQUE,
    password VARCHAR not null
);