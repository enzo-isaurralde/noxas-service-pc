-- V1__init.sql

CREATE TABLE usuarios (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          username        VARCHAR(50)  NOT NULL UNIQUE,
                          password        VARCHAR(255) NOT NULL,
                          rol             VARCHAR(20)  NOT NULL DEFAULT 'ADMIN',
                          creado_en       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE horarios_atencion (
                                   id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   dia_semana      ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO') NOT NULL,
                                   hora_inicio     TIME NOT NULL,
                                   hora_fin        TIME NOT NULL,
                                   activo          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Barrios de CABA que cubrís, cada uno con su costo de visita
CREATE TABLE barrios_cobertura (
                                   id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   nombre          VARCHAR(80)   NOT NULL UNIQUE,
                                   comuna          INT           NOT NULL,
                                   costo_visita    DECIMAL(10,2) NOT NULL,
                                   activo          BOOLEAN       NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE consultas (
                           id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
                           numero_comprobante  VARCHAR(20)   NOT NULL UNIQUE,
                           nombre_cliente      VARCHAR(100)  NOT NULL,
                           telefono            VARCHAR(30)   NOT NULL,
                           email               VARCHAR(150),
                           barrio_id           BIGINT        NOT NULL,
                           direccion           VARCHAR(255)  NOT NULL,
                           costo_estimado      DECIMAL(10,2) NOT NULL,
                           equipo              VARCHAR(150),
                           descripcion_problema TEXT         NOT NULL,
                           estado              ENUM('PENDIENTE','CONTACTADO','RESUELTA') NOT NULL DEFAULT 'PENDIENTE',
                           creado_en           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           actualizado_en      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           CONSTRAINT fk_consulta_barrio FOREIGN KEY (barrio_id) REFERENCES barrios_cobertura(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_consultas_estado ON consultas(estado);
CREATE INDEX idx_consultas_creado_en ON consultas(creado_en);