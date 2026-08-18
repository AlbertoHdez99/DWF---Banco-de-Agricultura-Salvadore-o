# Sistema Web Bancario - Banco de Agricultura Salvadoreño

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

Aplicación web para la gestión integral de operaciones bancarias y financieras desarrollada para la cátedra de **Desarrollo Web con Frameworks (DWF)** en la **Universidad Don Bosco**. El sistema simula las operaciones transaccionales y administrativas de una entidad bancaria, priorizando la integridad de los datos, la seguridad en el manejo de cuentas y la separación de roles.

---

## Funcionalidades Principales

* **Autenticación y Control de Roles:** Gestión de accesos para administradores, cajeros/empleados y clientes.
* **Gestión de Cuentas y Clientes:** Registro, consulta y administración de cuentas de ahorro/corrientes y expedientes de usuarios.
* **Módulo de Transacciones:** Depósitos, retiros, transferencias entre cuentas y consulta de saldos en tiempo real.
* **Historial y Reportes:** Registro detallado de movimientos transaccionales y generación de comprobantes/estados de cuenta.
* **Validación y Reglas de Negocio:** Validación en servidor de balances disponibles, transacciones concurrentes y control de integridad referencial.

---

## Stack Tecnológico

* **Backend:** Java, Spring Boot (Spring MVC, Spring Data JPA / Hibernate)
* **Frontend:** HTML5, CSS3, JavaScript, Bootstrap, Thymeleaf (motor de plantillas)
* **Base de Datos:** MySQL
* **Gestor de Dependencias y Build:** Maven
* **Control de Versiones:** Git & GitHub

---

## Requisitos Previos

Antes de ejecutar el proyecto localmente, asegúrate de contar con:

* **JDK 17** o superior instalado.
* **MySQL Server** (versión 8.0 o superior) corriendo localmente o en contenedor.
* **Maven** instalado (o usar el wrapper `./mvnw` incluido).
* Un IDE de preferencia (**IntelliJ IDEA**, **Eclipse** o **VS Code** con extensiones de Java).

---

## Instalación y Ejecución Local

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/AlbertoHdez99/DWF---Banco-de-Agricultura-Salvadore-o.git](https://github.com/AlbertoHdez99/DWF---Banco-de-Agricultura-Salvadore-o.git)
   cd DWF---Banco-de-Agricultura-Salvadore-o
