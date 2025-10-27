CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    rango VARCHAR(20) NOT NULL DEFAULT 'NOVATO',
    experiencia INTEGER DEFAULT 0,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(50) NOT NULL,
    descripcion TEXT
);

CREATE TABLE tipos (
    id_tipo SERIAL PRIMARY KEY,
    nombre_tipo VARCHAR(50) NOT NULL,
    experiencia_asociada INTEGER,
    descripcion_base TEXT,
    categoria_id INTEGER REFERENCES categorias(id_categoria)
);

CREATE TABLE misiones (
    id_mision SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    rango_requerido VARCHAR(20) NOT NULL,
    recompensa INTEGER,
    experiencia INTEGER,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_limite TIMESTAMP,
    fecha_tomada TIMESTAMP,
    fecha_completada TIMESTAMP,
    categoria_id INTEGER REFERENCES categorias(id_categoria),
    id_aventurero INTEGER REFERENCES usuarios(id_usuario)
);

CREATE TABLE historial_misiones (
    id_historial SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES usuarios(id_usuario),
    id_mision INTEGER REFERENCES misiones(id_mision),
    estado_anterior VARCHAR(20) NOT NULL,
    estado_nuevo VARCHAR(20) NOT NULL,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar un usuario administrador por defecto (password: admin)
INSERT INTO usuarios (nombre, nombre_usuario, email, password, rol, rango) VALUES
    ('Admin', 'admin', 'admin@dungeonquest.com', '$2a$10$rJf1bfNitQmrHX1qLsw9r.QE1CrFPjhd3PXNRqkpy8VqL.LPqKsMe', 'ADMINISTRADOR', 'LEGENDARIO');

-- Insertar una misión de ejemplo
INSERT INTO misiones (nombre, descripcion, estado, rango_requerido, recompensa, experiencia, categoria_id)
VALUES ('La Primera Aventura', 'Una misión simple para empezar tu camino como aventurero', 'DISPONIBLE', 'NOVATO', 100, 50, 1);