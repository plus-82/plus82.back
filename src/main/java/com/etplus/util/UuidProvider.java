package com.etplus.util;

import java.util.UUID;

public class UuidProvider {

  private UuidProvider() {
  }

  public static String generateUuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}