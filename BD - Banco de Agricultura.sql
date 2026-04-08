-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS `Banco_de_Agricultura`;
USE `Banco_de_Agricultura`;

-- 1. Tabla de Roles
CREATE TABLE `roles` (
  `id_rol` INT NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(50) NOT NULL UNIQUE,
  PRIMARY KEY (`id_rol`)
);

-- 2. Tabla de Usuarios
CREATE TABLE `usuarios` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `dui` VARCHAR(10) NOT NULL UNIQUE,
  `nombres` VARCHAR(100) NOT NULL,
  `apellidos` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `salario` DECIMAL(10, 2) DEFAULT NULL,
  `estado_usuario` ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo',
  `id_rol` INT NOT NULL,
  PRIMARY KEY (`id_usuario`),
  FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON DELETE CASCADE
);

-- CORRECCIÓN 4: Tabla de Dependientes
CREATE TABLE `dependientes` (
  `id_dependiente` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL UNIQUE,
  `nombre_comercio` VARCHAR(150) NOT NULL,
  PRIMARY KEY (`id_dependiente`),
  FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE
);

-- 3. Tabla de Sucursales
CREATE TABLE `sucursales` (
  `id_sucursal` INT NOT NULL AUTO_INCREMENT,
  `nombre_sucursal` VARCHAR(100) NOT NULL,
  `direccion` VARCHAR(255) NOT NULL,
  `id_gerente` INT UNIQUE, 
  PRIMARY KEY (`id_sucursal`),
  FOREIGN KEY (`id_gerente`) REFERENCES `usuarios` (`id_usuario`) ON DELETE SET NULL
);

-- 4. Tabla de Empleados
CREATE TABLE `empleados` (
  `id_empleado` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL UNIQUE,
  `id_sucursal` INT NOT NULL,
  `cargo` VARCHAR(100) NOT NULL,
  `estado_contratacion` ENUM('Activo', 'Inactivo', 'En espera') NOT NULL DEFAULT 'En espera',
  PRIMARY KEY (`id_empleado`),
  FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  FOREIGN KEY (`id_sucursal`) REFERENCES `sucursales` (`id_sucursal`) ON DELETE CASCADE
);

-- CORRECCIÓN 2: Tabla acciones_personal
CREATE TABLE `acciones_personal` (
  `id_accion` INT NOT NULL AUTO_INCREMENT,
  `id_empleado` INT NOT NULL,
  `tipo_accion` ENUM('Contratación', 'Baja') NOT NULL,
  `estado_accion` ENUM('En espera', 'Aprobada', 'Rechazada') NOT NULL DEFAULT 'En espera',
  `fecha_solicitud` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_gerente_sucursal` INT NOT NULL,
  `id_gerente_general` INT NULL,
  PRIMARY KEY (`id_accion`),
  FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`) ON DELETE CASCADE,
  FOREIGN KEY (`id_gerente_sucursal`) REFERENCES `usuarios` (`id_usuario`),
  FOREIGN KEY (`id_gerente_general`) REFERENCES `usuarios` (`id_usuario`)
);

-- 5. Tabla de Cuentas
CREATE TABLE `cuentas` (
  `id_cuenta` INT NOT NULL AUTO_INCREMENT,
  `numero_cuenta` VARCHAR(20) NOT NULL UNIQUE,
  `saldo` DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
  `id_usuario` INT NOT NULL,
  PRIMARY KEY (`id_cuenta`),
  FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE
);

-- 6. Tabla de Movimientos (CORRECCIÓN 5: Campo id_dependiente agregado)
CREATE TABLE `movimientos` (
  `id_movimiento` INT NOT NULL AUTO_INCREMENT,
  `tipo_movimiento` ENUM('Depósito', 'Retiro', 'Transferencia') NOT NULL,
  `monto` DECIMAL(15, 2) NOT NULL,
  `fecha_movimiento` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_cuenta_origen` INT NOT NULL,
  `id_cuenta_destino` INT NULL,
  `id_usuario_ejecutor` INT NOT NULL, -- Usuario que realiza la acción (Cliente, Cajero o Dependiente)
  `id_dependiente` INT NULL, -- Llave foránea si el movimiento fue en un comercio
  PRIMARY KEY (`id_movimiento`),
  FOREIGN KEY (`id_cuenta_origen`) REFERENCES `cuentas` (`id_cuenta`),
  FOREIGN KEY (`id_cuenta_destino`) REFERENCES `cuentas` (`id_cuenta`),
  FOREIGN KEY (`id_usuario_ejecutor`) REFERENCES `usuarios` (`id_usuario`),
  FOREIGN KEY (`id_dependiente`) REFERENCES `dependientes` (`id_dependiente`)
);

-- CORRECCIÓN 3: Tabla Comisiones
CREATE TABLE `comisiones` (
  `id_comision` INT NOT NULL AUTO_INCREMENT,
  `id_movimiento` INT NOT NULL UNIQUE,
  `id_dependiente` INT NOT NULL,
  `monto_comision` DECIMAL(15, 2) NOT NULL, -- Aquí se guarda el 5% del monto del movimiento
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_comision`),
  FOREIGN KEY (`id_movimiento`) REFERENCES `movimientos` (`id_movimiento`),
  FOREIGN KEY (`id_dependiente`) REFERENCES `dependientes` (`id_dependiente`)
);

-- 7. Tabla de Préstamos
CREATE TABLE `prestamos` (
  `id_prestamo` INT NOT NULL AUTO_INCREMENT,
  `monto_solicitado` DECIMAL(15, 2) NOT NULL,
  `interes` DECIMAL(5, 2) NOT NULL,
  `anios_plazo` INT NOT NULL,
  `cuota_mensual` DECIMAL(15, 2) NOT NULL,
  `fecha_solicitud` DATE NOT NULL,
  `estado_prestamo` ENUM('En espera', 'Aprobado', 'Rechazado') NOT NULL DEFAULT 'En espera',
  `id_cliente` INT NOT NULL,
  `id_cajero` INT NOT NULL,
  `id_gerente_aprobador` INT NULL,
  PRIMARY KEY (`id_prestamo`),
  FOREIGN KEY (`id_cliente`) REFERENCES `usuarios` (`id_usuario`),
  FOREIGN KEY (`id_cajero`) REFERENCES `usuarios` (`id_usuario`),
  FOREIGN KEY (`id_gerente_aprobador`) REFERENCES `usuarios` (`id_usuario`)
);

-- Inserción de roles iniciales
INSERT INTO `roles` (`nombre_rol`) VALUES
('Cliente'), ('Dependiente'), ('Cajero'), ('Personal de Limpieza'), 
('Secretaria'), ('Asesor Financiero'), ('Gerente de Sucursal'), ('Gerente General');