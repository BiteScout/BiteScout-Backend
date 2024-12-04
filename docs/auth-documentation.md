When a login or register request comes from the gateway, the following process occurs in your Spring Boot application:

### 1. **Request Handling**

- **Login Request**: The request is mapped to a controller method that handles login.
- **Register Request**: The request is mapped to a controller method that handles registration.

### 2. **Controller Layer**

- **LoginController**: Handles the `/login` endpoint.
- **RegisterController**: Handles the `/register` endpoint.

### 3. **Request Validation**

- **LoginRequest**: Contains the username and password fields for login.
- **RegisterRequest**: Contains the necessary fields for registration (e.g., username, password, email).

### 4. **Service Layer**

- **AuthService**: Contains the business logic for authentication and registration.
    - **login**: Validates the user credentials and generates a token if valid.
    - **register**: Creates a new user and generates a verification token.

### 5. **Repository Layer**

- **UserRepository**: Interacts with the database to fetch or save user details.
- **VerificationTokenRepository**: Interacts with the database to save verification tokens.

### 6. **Security Configuration**

- **SecurityFilterChain**: Configures the security settings, allowing unauthenticated access to `/login` and `/register` endpoints.

### 7. **Authentication Manager and Provider**

- **AuthenticationManager**: Delegates the authentication request to the `CustomAuthenticationProvider`.
- **CustomAuthenticationProvider**: Validates the user credentials against the database.
- **CustomUserDetailsService**: This class implements UserDetailsService and is responsible for loading user-specific data. It fetches user details from your UserServiceClient and returns a CustomUserDetails object. This is used by Spring Security to authenticate and authorize users.  
- **CustomUserDetails**: This class implements UserDetails and provides the necessary user information to Spring Security. It includes methods to get the username, password, and authorities (roles) of the user, as well as account status (enabled, locked, etc.).  
- **JwtTokenProvider**: This class is responsible for generating and validating JWT tokens. It uses the secret key, token expiration time, and other configurations to create and verify tokens.
- **JwtAuthenticationFilter**: This filter intercepts requests to protected endpoints, extracts the JWT token from the Authorization header, and validates it using the JwtTokenProvider. If the token is valid, it sets the authentication in the SecurityContext.

### 8. **Email Service**

- **EmailService**: Sends a verification email upon successful registration. If user clicks on the verification link, the get request is sent to the `/verify` endpoint and the user is verified. because verifyToken() method enables the user.

### 9. **Exception Handling**

- **GeneralExceptionHandler**: Handles exceptions that occur during the request processing.

### Sequence of Events for Login Request

1. **Gateway**: Forwards the login request to the `/login` endpoint.
2. **LoginController**: Receives the request and maps it to the `login` method.
3. **AuthService**: Validates the credentials using the `login` method.
4. **UserRepository**: Fetches the user details from the database.
5. **AuthService**: Generates a token if the credentials are valid.
6. **Response**: Returns the token to the client.

### Sequence of Events for Register Request

1. **Gateway**: Forwards the register request to the `/register` endpoint.
2. **RegisterController**: Receives the request and maps it to the `register` method.
3. **AuthService**: Processes the registration using the `register` method.
4. **UserRepository**: Saves the new user details to the database.
5. **VerificationTokenRepository**: Saves the verification token to the database.
6. **EmailService**: Sends a verification email to the user.
7. **Response**: Returns a success message to the client.

### Example Code

#### LoginRequest.java
```java
package com.bitescout.app.authservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor  // Generates a constructor with all fields
@NoArgsConstructor   // Generates a no-args constructor
public class LoginRequest {
    private String username;
    private String password;
}
```

#### RegisterRequest.java
```java
package com.bitescout.app.authservice.request;

import com.bitescout.app.authservice.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "Password must be at least 8 characters and contain at least one letter and one number")
    @NotNull(message = "Password is required")
    private String password;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private Role role;
}
```

#### AuthService.java
```java
package com.bitescout.app.authservice.service;

import com.bitescout.app.authservice.request.LoginRequest;
import com.bitescout.app.authservice.request.RegisterRequest;
import com.bitescout.app.authservice.repository.UserRepository;
import com.bitescout.app.authservice.repository.VerificationTokenRepository;
import com.bitescout.app.authservice.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    public String login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            // Generate token
            return "token";
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public void register(RegisterRequest registerRequest) {
        // Save user and generate verification token
        // ...
        emailService.sendVerificationEmail(registerRequest.getEmail(), "verificationToken");
    }
}
```

#### CustomAuthenticationProvider.java
```java
package com.bitescout.app.authservice.security;

import com.bitescout.app.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Validate credentials against the database
        // ...

        if (/* valid credentials */) {
            return new UsernamePasswordAuthenticationToken(username, password, /* authorities */);
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
```

#### EmailService.java
```java
package com.bitescout.app.authservice.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendVerificationEmail(String email, String token) {
        // Logic to send email
    }
}
```

#### GeneralExceptionHandler.java
```java
package com.bitescout.app.authservice.exc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GenericErrorResponse.class)
    public ResponseEntity<?> genericError(GenericErrorResponse exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return new ResponseEntity<>(errors, exception.getHttpStatus());
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public ResponseEntity<?> usernameOrPasswordInvalidException(WrongCredentialsException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationException(ValidationException exception) {
        return ResponseEntity.badRequest().body(exception.getValidationErrors());
    }
}
```