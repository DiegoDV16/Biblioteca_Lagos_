CREATE TABLE multas (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    prestamo_id INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    dias_retraso INT NOT NULL,
    pagada BIT DEFAULT 0
);
