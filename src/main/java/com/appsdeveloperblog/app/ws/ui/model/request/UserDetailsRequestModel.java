package com.appsdeveloperblog.app.ws.ui.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDetailsRequestModel {
  @NotNull(message = "First Name can't be null")
  private String firstName;

  @NotNull(message = "Last Name can't be null")
  private String lastName;

  @NotNull(message = "Email can't be null")
  @Email
  private String email;

  @NotNull(message = "Password can't be null")
  @Size(min = 8,max = 12,message = "Password can't be less than 8 characters and more than 12 characters.")
  private String password;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
