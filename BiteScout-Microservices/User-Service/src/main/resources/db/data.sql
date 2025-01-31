-- Enable UUID extension (Must run first)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Restaurant Owners and Regular Users Insert Statements
INSERT INTO users (
    id,
    username,
    password,
    email,
    enabled,
    profile_picture,
    role,
    -- UserDetails embedded fields
    first_name,
    last_name,
    phone_number,
    country,
    city,
    postal_code,
    address,
    creation_timestamp,
    update_timestamp
)
VALUES
-- Restaurant Owners
('dc1f322a-b5f6-48f8-8d67-39803ac0a8f6', 'nusret_gokce', 'hashed_password', 'nusretgokce@example.com', true, null, 'RESTAURANT_OWNER', 'Nusret', 'Gökçe', '+905321234567', 'Turkey', 'Istanbul', '34000', 'Etiler, Nispetiye Mah.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('7b953496-6492-4748-8f2a-398cf8a34644', 'burak_ozdemir', 'hashed_password', 'burakozdemir@example.com', true, null, 'RESTAURANT_OWNER', 'Burak', 'Özdemir', '+905324445566', 'Turkey', 'Istanbul', '34000', 'Spice Street, Fatih', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('2c54b51d-cbe2-47a7-9a4d-acfa943eda6c', 'tuncel_kurtiz', 'hashed_password', 'tuncel.kurtiz@example.com', true, null, 'RESTAURANT_OWNER', 'Tuncel', 'Kurtiz', '+905324567890', 'Turkey', 'Istanbul', '34000', 'Historical Avenue', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1112d6b7-f71c-4f24-9fbd-337df252717d', 'selena_gomez', 'hashed_password', 'selenagomez@example.com', true, null, 'RESTAURANT_OWNER', 'Selena', 'Gomez', '+905321112233', 'Turkey', 'Istanbul', '34000', 'Selena Street', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('c0b1a0d5-ce4c-4a35-88ba-8643d62acb2b', 'arda_guler', 'hashed_password', 'ardaguler@example.com', true, null, 'RESTAURANT_OWNER', 'Arda', 'Güler', '+905324445566', 'Turkey', 'Istanbul', '34000', 'Arda Avenue', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Regular Users
(gen_random_uuid(), 'john_doe', 'hashed_password', 'johndoe@example.com', true, null, 'CUSTOMER', 'John', 'Doe', '+905321234567', 'Turkey', 'Istanbul', '34000', 'Main Street', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'jake_black', 'hashed_password', 'jakeblack@example.com', true, null, 'CUSTOMER', 'Jake', 'Black', '+905329876543', 'Turkey', 'Istanbul', '34000', 'Another Street', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'barack_obama', 'hashed_password', 'barackobama@example.com', true, null, 'CUSTOMER', 'Barack', 'Obama', '+1 202-456-1111', 'USA', 'Washington, D.C.', '20500', 'Pennsylvania Avenue', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'leonel_messi', 'hashed_password', 'leonelmessi@example.com', true, null, 'CUSTOMER', 'Leonel', 'Messi', '+905321112233', 'Turkey', 'Istanbul', '34000', 'Barcelona Street', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(gen_random_uuid(), 'cristiano_rolando', 'hashed_password', 'cristianorolando@example.com', true, null, 'CUSTOMER', 'Cristiano', 'Rolando', '+905322223344', 'Turkey', 'Istanbul', '34000', 'Real Street', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);