create table multas(
    id integer not null auto_increment,
    prestamo_id integer not null unique,
    monto decimal(10,2) not null,
    dias_retraso integer not null,
    pagada boolean default false,
    primary key (id)
);