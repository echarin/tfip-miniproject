# Implementing Security

## Introduction to JWT

- JSON Web Tokens (JWT) are a popular way of handling authentication and authorisation in APIs
- Provides a way to authenticate a user once, then use the generated token to validate the user's identity in subsequent requests

## User Creation/Login

- Assuming email and password are used for login
- Store email and password in the `User` model in the database
  - Hash the password before storing
  - Can use libraries like BCrypt
- When a user logs in
  - Take the email and password
  - Hash the password the same way
  - Compare to the hashed password in the database

## Token Management

- Once user's credentials have been verified, generate a JWT which contains user's ID and other user info
- Send the token back to the client
- Client includes this token in the `Authorization` header of subsequent requests
- When server receives a request, it checks for the presence of this token
  - Decodes it and verifies it
  - When valid, the server will trust the client and the request is allowed to proceed
- Tokens should expire after a certain amount of time