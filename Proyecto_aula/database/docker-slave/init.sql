CREATE TABLE `usuarios` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`nombre` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	`apellido` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `directorios` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`nombre` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_general_ci',
	`ruta` VARCHAR(200) NOT NULL COLLATE 'utf8mb4_general_ci',
	`usuario_id` INT(11) NOT NULL,
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `usuario_id` (`usuario_id`) USING BTREE,
	CONSTRAINT `FK__usuarios` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;


CREATE TABLE `archivos` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`nombre` VARCHAR(100) NOT NULL,
	`ruta` VARCHAR(200) NOT NULL,
	`tamaño` FLOAT NOT NULL,
	`checksum` VARCHAR(50) NOT NULL,
	`usuario_id` INT NOT NULL,
	PRIMARY KEY (`id`) USING BTREE,,
	INDEX `usuario_id` (`usuario_id`) USING BTREE,,
	CONSTRAINT `FK__usuarios` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `compartidos` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `archivo_id` INT NOT NULL,
    `usuario_id` INT NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,,
    INDEX `archivo_id` (`archivo_id`) USING BTREE,
	INDEX `usuario_id` (`usuario_id`) USING BTREE,
    CONSTRAINT `FK__archivo` FOREIGN KEY (`archivo_id`) REFERENCES `archivos` (`id`)
    CONSTRAINT `FK__usuarios` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
)
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
;
