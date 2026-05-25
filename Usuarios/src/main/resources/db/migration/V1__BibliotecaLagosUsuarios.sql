create table usuarios (
    id integer not null auto_increment,
    usuario varchar(100) not null unique,
    contrasena varchar(20) not null,
    rol_id integer not null,
    PRIMARY KEY (id)
);