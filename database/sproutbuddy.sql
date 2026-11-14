-- =====================================================
-- SproutBuddy Development Database Setup
-- =====================================================
-- WARNING:
-- Running this script will delete the existing
-- sproutbuddy database and recreate it from scratch.
--
-- Use this script to set up/reset the development
-- database for the SproutBuddy project.
-- =====================================================
 
 
-- Delete existing development database
DROP DATABASE IF EXISTS sproutbuddy;
 
 
-- Create SproutBuddy database
CREATE DATABASE sproutbuddy;
 
 
-- Select database
USE sproutbuddy;
 
 
-- =====================================================
-- USERS
-- Stores SproutBuddy user accounts.
-- =====================================================
 
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
) ENGINE=InnoDB;
 
 
-- =====================================================
-- PLANT SPECIES
-- Stores general plant information used by the
-- SproutBuddy plant catalog.
--
-- Built-in species:
--      is_custom = FALSE
--      created_by_user_id = NULL
--
-- User-created species:
--      is_custom = TRUE
--      created_by_user_id = user's ID
-- =====================================================
 
CREATE TABLE plant_species (
    species_id INT AUTO_INCREMENT PRIMARY KEY,
    common_name VARCHAR(100) NOT NULL,
    scientific_name VARCHAR(150) UNIQUE,
    light_requirement VARCHAR(50),
    water_requirement VARCHAR(50),
    care_instructions TEXT,
    is_custom BOOLEAN NOT NULL DEFAULT FALSE,
    created_by_user_id INT NULL,
 
    FOREIGN KEY (created_by_user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
) ENGINE=InnoDB;
 
 
-- =====================================================
-- USER PLANTS
-- Stores individual plants owned by SproutBuddy users.
--
-- Each plant belongs to one user and references one
-- species from the plant_species table.
-- =====================================================
 
CREATE TABLE user_plants (
    plant_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    species_id INT NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    date_added DATE NOT NULL DEFAULT (CURRENT_DATE),
    location VARCHAR(100),
    photo VARCHAR(255),
 
    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
 
    FOREIGN KEY (species_id)
        REFERENCES plant_species(species_id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;


 
 
-- =====================================================
-- CARE TEMPLATES
-- Stores the default care schedule for each species.
-- One row per species per task type, written once and
-- shared by every user who owns that species.
--
-- Example: Pothos -> water every 7 days
--
-- Works for built-in and user-created species alike.
-- =====================================================
 
CREATE TABLE care_templates (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    species_id INT NOT NULL,
    task_type ENUM('water', 'fertilize', 'prune', 'repot', 'mist') NOT NULL,
    interval_days INT NOT NULL,
 
    UNIQUE (species_id, task_type),
 
    FOREIGN KEY (species_id)
        REFERENCES plant_species(species_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
 
 
-- =====================================================
-- CARE TASKS
-- Stores the actual reminders for each user plant.
--
-- Open task:
--      completed_at = NULL
--
-- Completed task:
--      completed_at = time the user marked it done
--
-- Completed rows are kept so they can be used for
-- history, streaks, and game elements.
-- =====================================================
 
CREATE TABLE care_tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    plant_id INT NOT NULL,
    task_type ENUM('water', 'fertilize', 'prune', 'repot', 'mist') NOT NULL,
    due_date DATE NOT NULL,
    completed_at TIMESTAMP NULL DEFAULT NULL,
 
    INDEX idx_tasks_due (due_date, completed_at),
 
    FOREIGN KEY (plant_id)
        REFERENCES user_plants(plant_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
 
 
-- =====================================================
-- TRIGGER: CREATE FIRST CARE TASKS
-- When a user adds a plant, automatically create the
-- first reminder for each task in that species'
-- care template.
--
-- Next tasks (after a task is completed) are created
-- by the backend using completed_at + interval_days.
-- =====================================================
 
CREATE TRIGGER trg_user_plants_create_tasks
AFTER INSERT ON user_plants
FOR EACH ROW
    INSERT INTO care_tasks (plant_id, task_type, due_date)
    SELECT NEW.plant_id,
           ct.task_type,
           DATE_ADD(NEW.date_added, INTERVAL ct.interval_days DAY)
    FROM care_templates ct
    WHERE ct.species_id = NEW.species_id;
 
 
-- =====================================================
-- DEVELOPMENT / SAMPLE PLANT DATA
-- Used to test the Plant Catalog during development.
-- =====================================================
 
INSERT INTO plant_species (
    common_name,
    scientific_name,
    light_requirement,
    water_requirement,
    care_instructions
)
VALUES
(
    'Pothos',
    'Epipremnum aureum',
    'Bright indirect light',
    'Moderate',
    'Allow the soil to dry slightly between waterings.'
),
(
    'Snake Plant',
    'Dracaena trifasciata',
    'Low to bright indirect light',
    'Low',
    'Allow the soil to dry between waterings.'
),
(
    'Monstera',
    'Monstera deliciosa',
    'Bright indirect light',
    'Moderate',
    'Keep in well-draining soil.'
);
 
 
-- =====================================================
-- DEVELOPMENT / SAMPLE CARE TEMPLATE DATA
-- Default care schedules for the sample species.
-- =====================================================
 
INSERT INTO care_templates (species_id, task_type, interval_days)
SELECT species_id, 'water', 7
FROM plant_species WHERE common_name = 'Pothos';
 
INSERT INTO care_templates (species_id, task_type, interval_days)
SELECT species_id, 'fertilize', 30
FROM plant_species WHERE common_name = 'Pothos';
 
INSERT INTO care_templates (species_id, task_type, interval_days)
SELECT species_id, 'water', 14
FROM plant_species WHERE common_name = 'Snake Plant';
 
INSERT INTO care_templates (species_id, task_type, interval_days)
SELECT species_id, 'fertilize', 60
FROM plant_species WHERE common_name = 'Snake Plant';
 
INSERT INTO care_templates (species_id, task_type, interval_days)
SELECT species_id, 'water', 7
FROM plant_species WHERE common_name = 'Monstera';
 
INSERT INTO care_templates (species_id, task_type, interval_days)
SELECT species_id, 'fertilize', 30
FROM plant_species WHERE common_name = 'Monstera';
 
 
-- =====================================================
-- DEVELOPMENT / SAMPLE USER AND PLANTS
-- Used to test reminders. The trigger above creates
-- the first care_tasks automatically.
-- Dated in the past so some tasks are already due.
-- =====================================================
 
INSERT INTO users (username, email, password_hash)
VALUES ('testuser', 'testuser@example.com', 'ypC+laRiAYUqoR2BSbab5w==:gZztPNPyhEVb1Xdyofnu/OEySloLp1N2JCovjYtAGMs='); 
-- username testuser and password password123.
 
INSERT INTO user_plants (user_id, species_id, nickname, date_added, location)
VALUES
(1, 1, 'Penny the Pothos', DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), 'Bedroom window'),
(1, 2, 'Sir Snake',        DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY),  'Living room');
 
 
-- =====================================================
-- SETUP VERIFICATION
-- =====================================================
 
-- Display all tables created by this script.
SHOW TABLES;
 
-- Display sample plant catalog data.
SELECT * FROM plant_species;
 
-- Display default care schedules.
SELECT * FROM care_templates;
 
-- Display reminders that are due (what the app shows
-- for a user, with the care info for that plant).
SELECT
    p.nickname,
    s.common_name,
    t.task_type,
    t.due_date,
    s.care_instructions
FROM care_tasks t
JOIN user_plants p ON p.plant_id = t.plant_id
JOIN plant_species s ON s.species_id = p.species_id
WHERE p.user_id = 1
  AND t.completed_at IS NULL
  AND t.due_date <= CURRENT_DATE
ORDER BY t.due_date;