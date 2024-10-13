package com.etplus.repository;

import com.etplus.vo.CountryVO;
import java.util.List;

interface CountryRepositoryCustom {

  List<CountryVO> findAllCountry();
}
