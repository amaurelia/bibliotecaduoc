-- V1: Crear tabla autores
CREATE TABLE autores (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(255) NOT NULL,
    edad            INT          NOT NULL,
    nacionalidad    VARCHAR(255) NOT NULL
);
