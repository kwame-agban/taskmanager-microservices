package com.example.auth_service.dto;

public class LoginResponse {
  private String token;
  private String tokenType = "Bearer";

  public LoginResponse() {
  }

  public LoginResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

}
