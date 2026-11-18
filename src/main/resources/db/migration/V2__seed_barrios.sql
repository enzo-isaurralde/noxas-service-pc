-- V2__seed_barrios.sql

INSERT INTO barrios_cobertura (nombre, comuna, costo_visita, activo) VALUES
-- Gratis: Comuna 4 (base)
('La Boca',                4, 0.00, TRUE),
('Barracas',                4, 0.00, TRUE),
('Parque Patricios',        4, 0.00, TRUE),
('Nueva Pompeya',           4, 0.00, TRUE),

-- Anillo 1: $5.000 (comunas 1, 3, 5, 7, 8)
('Retiro',                  1, 5000.00, TRUE),
('San Nicolás',             1, 5000.00, TRUE),
('Puerto Madero',           1, 5000.00, TRUE),
('San Telmo',               1, 5000.00, TRUE),
('Montserrat',              1, 5000.00, TRUE),
('Constitución',            1, 5000.00, TRUE),
('Balvanera',               3, 5000.00, TRUE),
('San Cristóbal',           3, 5000.00, TRUE),
('Almagro',                 5, 5000.00, TRUE),
('Boedo',                   5, 5000.00, TRUE),
('Flores',                  7, 5000.00, TRUE),
('Parque Chacabuco',        7, 5000.00, TRUE),
('Villa Soldati',           8, 5000.00, TRUE),
('Villa Riachuelo',         8, 5000.00, TRUE),
('Villa Lugano',            8, 5000.00, TRUE),

-- Anillo 2: $7.000 (comunas 2, 6, 9, 10, 13)
('Recoleta',                2, 7000.00, TRUE),
('Caballito',                6, 7000.00, TRUE),
('Liniers',                  9, 7000.00, TRUE),
('Mataderos',                9, 7000.00, TRUE),
('Parque Avellaneda',        9, 7000.00, TRUE),
('Villa Real',              10, 7000.00, TRUE),
('Monte Castro',            10, 7000.00, TRUE),
('Versalles',               10, 7000.00, TRUE),
('Floresta',                10, 7000.00, TRUE),
('Vélez Sarsfield',         10, 7000.00, TRUE),
('Villa Luro',              10, 7000.00, TRUE),
('Núñez',                   13, 7000.00, TRUE),
('Belgrano',                13, 7000.00, TRUE),
('Colegiales',              13, 7000.00, TRUE),

-- Anillo 3: $8.000 (comunas 11, 12, 14, 15)
('Villa General Mitre',     11, 8000.00, TRUE),
('Villa Devoto',            11, 8000.00, TRUE),
('Villa del Parque',        11, 8000.00, TRUE),
('Villa Santa Rita',        11, 8000.00, TRUE),
('Coghlan',                 12, 8000.00, TRUE),
('Saavedra',                12, 8000.00, TRUE),
('Villa Urquiza',           12, 8000.00, TRUE),
('Villa Pueyrredón',        12, 8000.00, TRUE),
('Palermo',                 14, 8000.00, TRUE),
('Chacarita',               15, 8000.00, TRUE),
('Villa Crespo',            15, 8000.00, TRUE),
('La Paternal',             15, 8000.00, TRUE),
('Villa Ortúzar',           15, 8000.00, TRUE),
('Agronomía',               15, 8000.00, TRUE),
('Parque Chas',             15, 8000.00, TRUE);