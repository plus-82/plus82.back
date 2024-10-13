package com.etplus.service;

import com.etplus.repository.CountryRepository;
import com.etplus.vo.CountryVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CountryService {

  private final CountryRepository countryRepository;

  public List<CountryVO> getAllCountries() {
    return countryRepository.findAllCountry();
  }
}
