-- Insert admin user (password: admin123)
MERGE INTO users (email, password, full_name, email_verified, two_factor_enabled)
KEY(email)
VALUES ('admin@flightmanagement.com', '$2a$10$rDkPvvAFV6GgJjHh1QXdkO1Y.PiDRRu1hJdfkPBZ0q3P7GhHD2Z6O', 'System Admin', true, false);

-- Insert roles if not present
MERGE INTO roles (name)
KEY(name)
VALUES ('ROLE_ADMIN');

-- Assign ROLE_ADMIN to admin user
MERGE INTO user_roles (user_id, role_id)
KEY(user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@flightmanagement.com' AND r.name = 'ROLE_ADMIN';

-- Insert sample airplanes
MERGE INTO airplanes (model, capacity, manufacturer)
KEY(model)
VALUES 
    ('Boeing 737', 180, 'Boeing'),
    ('Airbus A320', 150, 'Airbus'),
    ('Boeing 787', 330, 'Boeing');

-- Insert sample pilots
MERGE INTO pilots (rank)
KEY(rank)
VALUES 
    ('Captain'),
    ('First Officer'),
    ('Second Officer');

-- Insert sample flights
MERGE INTO flights (departure_time, arrival_time, origin, destination, airplane_id)
KEY(departure_time, origin, destination)
SELECT 
    DATEADD('DAY', 1, CURRENT_TIMESTAMP),
    DATEADD('HOUR', 2, DATEADD('DAY', 1, CURRENT_TIMESTAMP)),
    'New York',
    'London',
    airplane_id
FROM airplanes
WHERE model = 'Boeing 787'
LIMIT 1;

-- Associate pilots with flights
MERGE INTO flight_pilots (flight_id, pilot_id)
KEY(flight_id, pilot_id)
SELECT f.flight_id, p.license_number
FROM flights f
CROSS JOIN pilots p
WHERE f.origin = 'New York'
LIMIT 2; 