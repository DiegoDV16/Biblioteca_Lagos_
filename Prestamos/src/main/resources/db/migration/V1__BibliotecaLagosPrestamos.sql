create table prestamos(
    id integer not null auto_increment,
    socio_id integer not null,
    libro_id integer not null,
    fecha_prestamo date not null,
    fecha_devolucion date not null,
    fecha_entrega date,
    estado varchar(50),
    primary key (id)
);
