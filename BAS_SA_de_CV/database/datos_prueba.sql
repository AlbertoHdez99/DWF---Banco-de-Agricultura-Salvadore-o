-- --------------------------------------------------------
-- DATOS DE PRUEBA
-- Todas las contraseñas son: password123
-- --------------------------------------------------------

USE banco_de_agricultura;

-- 1. Insertar Roles (si no existen en tu DDL, asegúrate de insertarlos)
-- Asumimos que los roles ya están o se insertan aquí (1=Cliente, 2=Dependiente, 3=Cajero, 4=Limpieza, 5=Secretaria, 6=Asesor, 7=Gerente Sucursal, 8=Gerente General)
INSERT IGNORE INTO roles (id_rol, nombre_rol) VALUES 
(1, 'Cliente'), (2, 'Dependiente'), (3, 'Cajero'), (4, 'Personal de Limpieza'), 
(5, 'Secretaria'), (6, 'Asesor Financiero'), (7, 'Gerente de Sucursal'), (8, 'Gerente General');

-- 2. Insertar Usuarios
-- Gerente General
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (8, 'Carlos', 'General', '00000000-8', 'ggeneral@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');

-- Gerentes de Sucursal
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (7, 'Ana', 'Gómez', '11111111-7', 'agomez@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (7, 'Luis', 'Pérez', '22222222-7', 'lperez@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');

-- Cajeros
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (3, 'Marta', 'Cajero A', '33333333-3', 'mcajero@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (3, 'Jorge', 'Cajero B', '44444444-3', 'jcajero@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');

-- Dependiente
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (2, 'Pedro', 'Dependiente', '55555555-2', 'pdependiente@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');

-- Clientes
-- Cliente Alto (id=7) -> salario $1200
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, salario, estado_usuario) 
VALUES (1, 'Juan', 'Cliente Alto', '66666666-1', 'jalto@mail.com', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 1200.00, 'Activo');

-- Cliente Medio (id=8) -> salario $700
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, salario, estado_usuario) 
VALUES (1, 'María', 'Cliente Medio', '77777777-1', 'mmedio@mail.com', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 700.00, 'Activo');

-- Cliente Bajo (id=9) -> sin salario formal
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, salario, estado_usuario) 
VALUES (1, 'José', 'Cliente Bajo', '88888888-1', 'jbajo@mail.com', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', NULL, 'Activo');

-- Empleados extra
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (4, 'Rosa', 'Limpieza', '99999999-4', 'rlimpieza@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (5, 'Laura', 'Secretaria', '10101010-5', 'lsecretaria@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');
INSERT INTO usuarios (id_rol, nombres, apellidos, dui, email, password, estado_usuario) 
VALUES (6, 'Mario', 'Asesor', '12121212-6', 'masesor@banco.sv', '$2a$10$.Mnl9iRrSKi2VUzCY4xcVe/64XyzeasyZu0N27kNPbvI0fvN8Sb02', 'Activo');

-- 3. Insertar Sucursales
INSERT INTO sucursales (nombre_sucursal, direccion, id_gerente) 
VALUES ('Sucursal Central San Salvador', 'Centro Histórico', 2);
INSERT INTO sucursales (nombre_sucursal, direccion, id_gerente) 
VALUES ('Sucursal Santa Ana', 'Metrocentro Santa Ana', 3);

-- 4. Asociar Empleados a Sucursales
-- Cajero 1, Secretaria, Limpieza -> Sucursal Central (id=1)
INSERT INTO empleados (id_usuario, id_sucursal, cargo, estado_contratacion) VALUES (4, 1, 'Cajero', 'Activo');
INSERT INTO empleados (id_usuario, id_sucursal, cargo, estado_contratacion) VALUES (10, 1, 'Personal de Limpieza', 'Activo');
INSERT INTO empleados (id_usuario, id_sucursal, cargo, estado_contratacion) VALUES (11, 1, 'Secretaria', 'Activo');
-- Cajero 2, Asesor -> Sucursal Santa Ana (id=2)
INSERT INTO empleados (id_usuario, id_sucursal, cargo, estado_contratacion) VALUES (5, 2, 'Cajero', 'Activo');
INSERT INTO empleados (id_usuario, id_sucursal, cargo, estado_contratacion) VALUES (12, 2, 'Asesor Financiero', 'Activo');
-- Los gerentes también están atados a su sucursal respectiva
INSERT INTO empleados (id_usuario, id_sucursal, cargo, estado_contratacion) VALUES (2, 1, 'Gerente de Sucursal', 'Activo');
INSERT INTO empleados (id_usuario, id_sucursal, cargo, estado_contratacion) VALUES (3, 2, 'Gerente de Sucursal', 'Activo');

-- 5. Insertar Dependiente (comercio afiliado)
-- id_usuario del dependiente es 6
INSERT INTO dependientes (id_usuario, nombre_comercio) VALUES (6, 'Tienda La Bendición');

-- 6. Insertar Cuentas para los clientes
-- Cliente Alto (id=7) -> salario $1200
INSERT INTO cuentas (numero_cuenta, id_usuario, saldo) VALUES ('BA1000000001', 7, 500.00);

-- Cliente Medio (id=8) -> salario $700
INSERT INTO cuentas (numero_cuenta, id_usuario, saldo) VALUES ('BA2000000002', 8, 250.00);

-- Cliente Bajo (id=9) -> sin salario formal
INSERT INTO cuentas (numero_cuenta, id_usuario, saldo) VALUES ('BA3000000003', 9, 50.00);
