-- V2: catálogo de comisarías (SCRUM-11)
CREATE TABLE comisaria (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo         VARCHAR(20)  NOT NULL,
    nombre         VARCHAR(120) NOT NULL,
    direccion      VARCHAR(200),
    telefono       VARCHAR(30),
    correo         VARCHAR(120),
    activa         BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    actualizado_en TIMESTAMPTZ  NOT NULL DEFAULT now(),
    -- La llave foránea hacia usuario(id) se agrega en V3 (SCRUM-12),
    -- cuando exista la tabla usuario.
    creado_por_id  BIGINT,
    CONSTRAINT uq_comisaria_codigo UNIQUE (codigo)
);

-- Catálogo inicial de comisarías (SCRUM-11).
-- Dirección, teléfono y correo quedan vacíos hasta contar con los datos oficiales.
INSERT INTO comisaria (codigo, nombre) VALUES
    ('COM-01', 'Comisaría Primera'),
    ('COM-02', 'Comisaría Segunda'),
    ('COM-03', 'Comisaría Tercera'),
    ('CJ-01',  'Casa de Justicia');