package com.logistics.server;

import java.util.List;

public interface AreasServer {
    String getAreaCodeByName(String name);


    List<String> getProvinceIdAndCityIdAndAreaIdByAreaId(String id);
}
