-- Crear tabla usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    vigencia BOOLEAN
);

-- Crear tabla clientes
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(255),
    apellidos VARCHAR(255),
    telefono VARCHAR(10),
    tipo_doc VARCHAR(20),
    num_doc VARCHAR(15),
    direccion VARCHAR(500),
    provincia VARCHAR(255),
    capital VARCHAR(255),
    fecha DATE,
    usuario_id BIGINT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Crear tabla roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    fecha DATE
);

-- Crear tabla intermedia users_role
CREATE TABLE users_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES usuarios(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

