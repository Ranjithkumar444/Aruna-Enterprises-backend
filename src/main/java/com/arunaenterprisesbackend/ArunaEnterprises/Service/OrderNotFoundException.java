package com.arunaenterprisesbackend.ArunaEnterprises.Service;

public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException(String message) {
    super(message);
  }
}