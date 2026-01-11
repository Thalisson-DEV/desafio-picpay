INSERT INTO roles (id, name) VALUES (1, 'BASIC');
INSERT INTO roles (id, name) VALUES (2, 'ADMIN');

-- Ajusta a sequence para que os próximos inserts não entrem em conflito com os IDs fixos
SELECT setval(pg_get_serial_sequence('roles', 'id'), 2);
