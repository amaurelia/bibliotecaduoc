-- V3: Datos de prueba — autores y libros asociados

INSERT INTO autores (nombre, edad, nacionalidad) VALUES
    ('Gabriel García Márquez', 87,  'Colombiana'),
    ('Isabel Allende',         81,  'Chilena'),
    ('Jorge Luis Borges',      86,  'Argentina'),
    ('Pablo Neruda',           69,  'Chilena'),
    ('Mario Vargas Llosa',     87,  'Peruana');

INSERT INTO libros (isbn, titulo, editorial, fecha_publicacion, autor_id) VALUES
    ('9780060883287', 'Cien años de soledad',              'Editorial Sudamericana',  1967, 1),
    ('9780060883294', 'El amor en los tiempos del cólera', 'Editorial Oveja Negra',   1985, 1),
    ('9780553383287', 'La casa de los espíritus',          'Plaza & Janés',           1982, 2),
    ('9780553380348', 'Eva Luna',                          'Plaza & Janés',           1987, 2),
    ('9780142437889', 'Ficciones',                         'Editorial Sur',           1944, 3),
    ('9780142440704', 'El Aleph',                          'Editorial Losada',        1949, 3),
    ('9780374529291', 'Veinte poemas de amor',             'Editorial Nascimento',    1924, 4),
    ('9780060838683', 'La ciudad y los perros',            'Editorial Seix Barral',   1963, 5),
    ('9780374529308', 'Conversación en La Catedral',       'Editorial Seix Barral',   1969, 5);
