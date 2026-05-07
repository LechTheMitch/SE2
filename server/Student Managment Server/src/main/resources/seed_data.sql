-- SE2 Student Management System Seed Data
-- Database: SE2

USE SE2;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE attendance_record;
TRUNCATE TABLE student;
TRUNCATE TABLE parent;
TRUNCATE TABLE user_direct_permissions;
TRUNCATE TABLE user;
TRUNCATE TABLE role_permissions;
TRUNCATE TABLE role;
TRUNCATE TABLE permission;
TRUNCATE TABLE hall;
TRUNCATE TABLE session;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Insert Roles
INSERT INTO role (id, name) VALUES (1, 'ADMIN');
INSERT INTO role (id, name) VALUES (2, 'PARENT');
INSERT INTO role (id, name) VALUES (3, 'STUDENT');
INSERT INTO role (id, name) VALUES (4, 'TEACHING_ASSISTANT');

-- 2. Insert Users (Password is 'password123')
INSERT INTO user (user_id, first_name, last_name, email, password, phone_number, role_id, force_password_change, force_email_change) VALUES
(1, 'Ahmed', 'Nagah', 'ahmed@nagah.com', '$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '010009565355', 1, 0, 0),
(2, 'Ahmed', 'Mostafa', 'ahmed@mostafa.com', '$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '01212156535', 2, 0, 0),
(3, 'Luka', 'Mostafa', 'luka@mostafa.com', '$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '01512156535', 3, 0, 0),
(4, 'Admin', 'User', 'admin@sms.com', '$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '1234567890', 1, 0, 0),
(5, 'Parent', 'One', 'parent1@gmail.com', '$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '5550101', 2, 0, 0),
(6, 'Parent', 'Two', 'parent2@gmail.com', '$$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '5550102', 2, 0, 0),
(7, 'Alice', 'Smith', 'alice.smith@student.com', '$$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '5551001', 3, 0, 0),
(8, 'Bob', 'Jones', 'bob.jones@student.com', '$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '5551002', 3, 0, 0),
(9, 'Charlie', 'Brown', 'charlie.brown@student.com', '$$2a$10$m2eerUilXrYYfsOva2b31./RFpvYTeVERvjyDAP9ZWVk8rdnb4LoS', '5551003', 3, 0, 0);

-- 3. Insert Parents
INSERT INTO parent (user_id) VALUES (2);
INSERT INTO parent (user_id) VALUES (5);
INSERT INTO parent (user_id) VALUES (6);

-- 4. Insert Students
INSERT INTO student (user_id, parent_id, parent_phone_number, qr_code) VALUES 
(3, 2, '01212156535', 'QR_LUKA_MOSTAFA_2026_001'),
(7, 5, '5550101', 'QR_ALICE_SMITH_2026_001'),
(8, 5, '5550101', 'QR_BOB_JONES_2026_002'),
(9, 6, '5550102', 'QR_CHARLIE_BROWN_2026_003');

-- 5. Insert Sessions
INSERT INTO session (id, title, description) VALUES 
(1, 'Mathematics 101', 'Introductory algebra and calculus'),
(2, 'Physics Advanced', 'Quantum mechanics and relativity basics');

-- 6. Insert Halls
INSERT INTO hall (id, name, location, session_time, session_id) VALUES 
(1, 'Grand Hall A', 'Building 1, Floor 1', '2026-05-10 09:00:00', 1),
(2, 'Science Lab 3', 'Building 2, Floor 3', '2026-05-10 11:30:00', 1),
(3, 'Physics Theatre', 'Building 3, Floor 2', '2026-05-11 14:00:00', 2);
