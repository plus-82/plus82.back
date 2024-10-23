package com.etplus.controller;

import com.etplus.common.CommonResponse;
import com.etplus.common.CommonResponseCode;
import com.etplus.service.CountryService;
import com.etplus.vo.CountryVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

  private final CountryService countryService;

  @GetMapping
  public CommonResponse<List<CountryVO>> getAllCountries() {
    List<CountryVO> vo = countryService.getAllCountries();
    return new CommonResponse(vo, CommonResponseCode.SUCCESS);
  }

}
