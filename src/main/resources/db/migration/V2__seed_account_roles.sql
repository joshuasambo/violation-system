INSERT INTO roles (role_name)
VALUES ('Administrator'), ('Teacher')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);
