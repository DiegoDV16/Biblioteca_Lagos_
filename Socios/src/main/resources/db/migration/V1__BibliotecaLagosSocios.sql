create table socios(
    id integer not null auto_increment,
    nombre varchar(100) not null,
    apellido varchar(100) not null,
    rut varchar(20) not null unique,
    correo varchar(100) not null unique,
    telefono varchar(20),
    idTipoSocio integer not null,
    primary key (id)
);