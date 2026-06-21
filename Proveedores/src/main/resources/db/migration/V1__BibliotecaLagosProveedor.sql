create table proveedores(
    id integer not null auto_increment,
    nombre varchar(100) not null,
    telefono varchar(20) not null,
    correo varchar(100) not null,
    direccion varchar(255) not null,
    estado varchar(20) not null,
    primary key (id)
);