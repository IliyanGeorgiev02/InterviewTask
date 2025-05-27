package com.example.trading212Task.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRegisterDTO {
    @NotNull(message = "Required field")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotNull(message = "Required field")
    @Size(min = 2, max = 20, message = "First name must be between 3 and 20 characters")
    private String firstName;
    @NotNull(message = "Required field")
    @Size(min = 2, max = 20, message = "Last name must be between 3 and 20 characters")
    private String lastName;
    @NotNull(message = "Must contain email")
    @Email(message = "Must be a valid email address")
    private  String  email;
    @NotNull(message = "Required field")
    @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters")
    private String password;

    public UserRegisterDTO(String username, String firstName, String lastName, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public UserRegisterDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
