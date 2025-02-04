package com.logistics.server;

import com.logistics.entity.PageBean;
import com.logistics.entity.Refrigerate;
import com.logistics.entity.Rm_Gs;

public interface RefrigerateServer {
    PageBean<Refrigerate> getRefrigerateListByAreaId(Integer pageNum, Integer pageSize, String areaId, String name, String id, Integer status);

    void updateRefrigerate(Refrigerate refrigerate);

    void addRefrigerate(Refrigerate refrigerate);

    void deleteRefrigerateById(String id);

   Refrigerate getRefrigerateById(String id);

    void inRoom(Rm_Gs rmGs);
}
