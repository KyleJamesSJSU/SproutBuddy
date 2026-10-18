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
-- SETUP VERIFICATION
-- =====================================================

-- Display all tables created by this script.
SHOW TABLES;

-- Display sample plant catalog data.
SELECT * FROM plant_species;