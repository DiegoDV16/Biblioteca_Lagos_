CREATE TABLE libros (
    id integer not null auto_increment,
    titulo varchar(255) not null,
    autor varchar(255) not null,
    isbn varchar(100) UNIQUE not null,
    editorial varchar(100) not null,
    anio_publicacion integer not null,
    cantidad_disponible integer not null,
    cantidad_total integer not null,
    categoria_id integer not null,
    proveedor_id integer not null,
    estado varchar(50) not null,
    primary key (id)
);