package com.etplus.util;

import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;

public class UuidProvider {

  private UuidProvider() {
  }

  public static String generateUuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static String generateCode() {
    return RandomStringUtils.randomAlphanumeric(10);
  }
}