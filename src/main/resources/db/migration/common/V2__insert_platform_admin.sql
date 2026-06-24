INSERT INTO users (id, username, email, password, first_name, last_name, role, enabled, deleted, created_at, created_by)
VALUES (
    gen_random_uuid(),
    'admin',
    'admin@saas.com',
    '$2a$12$6RSY6dtPJyOZFoZSXAiGjevzSl6MFV7dsINvw3PTDM85vIbjsz15W',
    'Admin',
    'User',
    'ROLE_PLATFORM_ADMIN',
    true,
    false,
    NOW(),
    'SYSTEM'
);