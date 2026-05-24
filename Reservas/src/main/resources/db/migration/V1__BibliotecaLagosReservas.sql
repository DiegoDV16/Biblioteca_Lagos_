create table reservas(
    id integer not null auto_increment,
    socio_id integer not null,
    libro_id integer not null,
    fecha_reserva date not null,
    estado varchar(50) not null,
    primary key (id)
);