# Implementing Security

## Introduction to JWT

- JSON Web Tokens (JWT) are a popular way of handling authentication and authorisation in APIs
- Provides a way to authenticate a user once, then use the generated token to validate the user's identity in subsequent requests
- Consists of three parts
  - **Header**: two parts: the type of the token (JWT) and the signing algorithm used
  - **Payload**: contains the claims (statements about an entity, usually the user) and additional data
    - Claims can be registered, public, or private
    - Registered claims are predefined claims which are not mandatory but recommended; they include iss (issuer), exp (expiration time), sub (subject), aud (audience) and others
  - **Signature**: created by taking the encoded header, the encoded payload, a secret, the algorithm specified in the header, then signing it
    - Verifies that the signature was not altered and if the token is signed with a private key, that the sender of the JWT is who it claims to be
  - Each part is Base64Url encoded to form the three parts
  - Then the JWT is output with the three Base64-URL strings separated by dots, allowing for easy transmission through HTML and HTTP environments

### Decoding and Extracting Claims from JWTs

-

## Spring Boot Dependencies

```xml
  <!-- Spring Boot Starter Security -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <!-- JWT stuff for Security -->
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
  </dependency>
```

## User Creation/Login

- Assuming email and password are used for login
- Store email and password in the `User` model in the database
  - Client sends plaintext password to server over secure HTTPS connection
  - Server hashes the password before storing
  - Never hash the password on the client side!
    - Means you are storing and comparing hashes, not the password
    - An attacker with access to your database will already have valid login credentials via the hashes
    - And therefore not need to know the real password
  - Can use the Spring Security library
    - `BCryptPasswordEncoder` class implements password hashing using BCrypt algorithm

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashingExample {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "my-raw-password";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println(encodedPassword);
    }
}
```

- When a user logs in
  - Take the email and password
  - Hash the password the same way
  - Compare to the hashed password in the database

```java
boolean isMatch = passwordEncoder.matches(rawPassword, encodedPassword);
```

## Token Management

- Once user's credentials have been verified, generate a JWT which contains user's ID and other user info
- Send the token back to the client
- Example JSON

```json
{
    "user": {
        "id": 1,
        "email": "user@example.com"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

- Client includes this token in the `Authorization` header of subsequent requests
- When server receives a request, it checks for the presence of this token
  - Decodes it and verifies it
  - When valid, the server will trust the client and the request is allowed to proceed
- Example of decoded token payload

```json
{
  "sub": "1",
  "email": "user@example.com",
  "exp": 1623208800
}
```

- Where `sub` is the subject of the token (usually user's ID), `email` is the user's email, `exp` is the expiration time of the token in Unix timestamp format
- Tokens should expire after a certain amount of time
- Allow for password resets
  - Generate a unique, temporary link that can be used to reset the password
  - Email this link to the user
  - Ensuers that if a user forgets their password, they can still regain access to their account
