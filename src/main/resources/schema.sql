CREATE TABLE tecnologic (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tecnologic (name, description) VALUES
('Angular', 'Framework para desarrollo frontend basado en TypeScript'),
('Spring', 'Framework para aplicaciones Java con soporte para microservicios'),
('JavaScript', 'Lenguaje de programación dinámico usado en desarrollo web'),
('Python', 'Lenguaje versátil para desarrollo backend, data science e IA'),
('React', 'Biblioteca para interfaces de usuario en aplicaciones frontend'),
('Node.js', 'Entorno de ejecución de JavaScript para backend'),
('PostgreSQL', 'Sistema de gestión de bases de datos relacional'),
('MongoDB', 'Base de datos NoSQL orientada a documentos');

CREATE TABLE capability_technology (
    id SERIAL PRIMARY KEY,
    id_capability INTEGER NOT NULL,
    id_technology BIGINT NOT NULL,
    CONSTRAINT fk_capability
      FOREIGN KEY (id_technology) REFERENCES technology(id));