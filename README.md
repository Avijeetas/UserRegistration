### User Registration and Verification System

This project implements a secure user registration and verification system using Spring Boot, Redis, and Java. It follows industry best practices for user authentication and data security.

## Features:

# User registration with basic details
* OTP (One-Time Password) generation and verification

* Temporary OTP storage in Redis with a Time-To-Live (TTL) for security

* Password creation for secure account access

* Registration completion and system access

Implementation Flow:

* User Creation (/create): Users initiate registration by providing required details.

* OTP Generation and Verification: A unique OTP is generated and sent to the user's registered contact for verification.

* OTP Storage: Valid OTPs are securely stored in Redis with a 15-minute TTL for limited access.

* User Verification and Registration (/registration): Verified users are redirected to set a password and complete registration.

* Registration Completion: Upon password creation, users complete registration and gain system access.


## Technologies:

Spring Boot: Backend application development

Redis: Temporary OTP storage

Java: Backend programming language

RESTful API: Frontend-backend communication

Token based User authentication and authorization

