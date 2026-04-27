-- V2: Crear tabla libros con clave foránea hacia autores
CREATE TABLE libros (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    isbn                VARCHAR(255) NOT NULL,
    titulo              VARCHAR(255) NOT NULL,
    editorial           VARCHAR(255) NOT NULL,
    fecha_publicacion   INT          NOT NULL,
    autor_id            INT,
    CONSTRAINT fk_libros_autor FOREIGN KEY (autor_id) REFERENCES autores(id)
);
