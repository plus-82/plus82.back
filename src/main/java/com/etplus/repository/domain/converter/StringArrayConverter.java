package com.etplus.repository.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringArrayConverter implements AttributeConverter<List<String>, String> {

  private static final String SPLIT_CHAR = ",";

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    if (attribute == null) {
      return null;
    }
    return String.join(SPLIT_CHAR, attribute);
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Arrays.asList(dbData.split(SPLIT_CHAR));
  }
}