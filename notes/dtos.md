# Data Transfer Objects (DTOs)

- They are plain old Java objects (POJOs) that map to your entity but only contains the fields you want to expose
  - Therefore they allow you to precisely control what data you send to the client
  - Enhances security and simplicity, as well as performance if you don't send the entire entity
  - Also gives you flexibility to send exactly what you want

## Example implementation for entity/DTO interconversion using MapStruct

- Your DTOs should be serializable
  - However, since JSON is the ain data interchange format for REST APIs, serialisation is usually handled by libraries such as Jackson which convert objects to JSON and so on
  - As such, your classes do not need to implement `Serializable`

- Have interfaces that handle conversions to DTO, and conversions to entity

```java
@Mapper
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
}
```

- In the service layer, use the mapper to interconvert entities and DTOs

```java
@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  // constructors, etc.

  public UserDTO getUser(Long id) {
    User user = userRepository.findById(id).orElseThrow(...);
    return userMapper.toDTO(user);
  }
}
```

- On Angular, create similar TypeScript interfaces to represent these DTOs
  - For logging in, you might need a separate `LoginDTO` (sending to Spring Boot backend) that is different from `UserDTO` (receiving from backend)

```typescript
export interface UserDTO {
  id: number;
  username: string;
  // other fields...
}

this.http.get<UserDTO>(`${apiUrl}/users/1`).subscribe(user => console.log(user.username));

public class LoginDTO {
    private String username;
    private String password;
    // getters and setters
}
```

## ModelMapper

- A library that simplifies the process of transforming source objects into destination objects
- Automates the mapping process between diffeent object models in your application
  - Saves time and reduces risk of errors

## Current DTOs

### UserDTO

- Exclude `password`