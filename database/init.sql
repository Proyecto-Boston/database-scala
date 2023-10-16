CREATE DATABASE IF NOT EXISTS `proyecto_aula2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `proyecto_aula2`;

CREATE TABLE IF NOT EXISTS `administradores` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `archivos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `ruta` varchar(200) NOT NULL,
  `tamano` double NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `habilitado` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `nodo_id` int(11) NOT NULL,
  `directorio_id` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `usuario_id` (`usuario_id`) USING BTREE,
  KEY `FK_nodos_archivos` (`nodo_id`),
  KEY `FK_directorios_archivos` (`directorio_id`),
  CONSTRAINT `FK_directorios_archivos` FOREIGN KEY (`directorio_id`) REFERENCES `directorios` (`id`),
  CONSTRAINT `FK_nodos_archivos` FOREIGN KEY (`nodo_id`) REFERENCES `nodos` (`id`),
  CONSTRAINT `FK_usuarios_archivos` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `auditoria` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tabla` varchar(64) NOT NULL,
  `id_tabla` int(11) NOT NULL,
  `operacion` enum('INSERT','UPDATE') NOT NULL,
  `fecha` timestamp NULL DEFAULT current_timestamp(),
  `usuario_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_usuarios_auditoria` (`usuario_id`),
  CONSTRAINT `FK_usuarios_auditoria` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `compartidos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `archivo_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`) USING BTREE,
  KEY `archivo_id` (`archivo_id`) USING BTREE,
  KEY `usuario_id` (`usuario_id`) USING BTREE,
  CONSTRAINT `FK_archivo_compartidos` FOREIGN KEY (`archivo_id`) REFERENCES `archivos` (`id`),
  CONSTRAINT `FK_usuarios_compartidos` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `directorios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `ruta` varchar(200) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `tamano` double NOT NULL,
  `nodo_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `FK_nodos_directorios` (`nodo_id`),
  CONSTRAINT `FK_nodos_directorios` FOREIGN KEY (`nodo_id`) REFERENCES `nodos` (`id`),
  CONSTRAINT `FK_usuarios_directorios` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `nodos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `respaldos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ruta` varchar(200) NOT NULL,
  `archivo_id` int(11) NOT NULL,
  `nodo_id` int(11) NOT NULL,
  `usuario_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_archivos_respaldos` (`archivo_id`),
  KEY `FK_nodos_respaldos` (`nodo_id`),
  KEY `FK_usuarios_respaldos` (`usuario_id`),
  CONSTRAINT `FK_archivos_respaldos` FOREIGN KEY (`archivo_id`) REFERENCES `archivos` (`id`),
  CONSTRAINT `FK_nodos_respaldos` FOREIGN KEY (`nodo_id`) REFERENCES `nodos` (`id`),
  CONSTRAINT `FK_usuarios_respaldos` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`auth_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `habilitado` tinyint(1) DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` int(11) DEFAULT NULL,
  `auth_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `auth_id` (`auth_id`),
  CONSTRAINT `usuarios_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `administradores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `archivos_after_insert` AFTER INSERT ON `archivos` FOR EACH ROW BEGIN
  UPDATE directorios
  SET tamano = (SELECT SUM(tamano) FROM archivos WHERE directorio_id = NEW.directorio_id)
  WHERE id = NEW.directorio_id;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `archivos_after_update` AFTER UPDATE ON `archivos` FOR EACH ROW BEGIN
   INSERT INTO auditoria (tabla, id_tabla, operacion, usuario_id) 
   VALUES ('archivos', NEW.id, 'UPDATE', NEW.usuario_id);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `compartidos_after_update` AFTER UPDATE ON `compartidos` FOR EACH ROW BEGIN
   INSERT INTO auditoria (tabla, id_tabla, operacion, usuario_id)
   VALUES ('compartidos', NEW.id, 'UPDATE', NEW.usuario_id);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `directorios_after_update` AFTER UPDATE ON `directorios` FOR EACH ROW BEGIN
   INSERT INTO auditoria (tabla, id_tabla, operacion, usuario_id)
   VALUES ('directorios', NEW.id, 'UPDATE', NEW.usuario_id);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `respaldos_after_update` AFTER UPDATE ON `respaldos`
FOR EACH ROW
BEGIN
   INSERT INTO auditoria (tabla, id_tabla, operacion, usuario_id)
   VALUES ('respaldos', NEW.id, 'UPDATE', NEW.usuario_id);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `usuarios_after_update` AFTER UPDATE ON `usuarios` FOR EACH ROW BEGIN
   INSERT INTO auditoria (tabla, id_tabla, operacion, usuario_id)
   VALUES ('usuarios', NEW.id, 'UPDATE', NEW.updated_by);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;