package com.logistics.server;

import com.logistics.entity.Rm_Gs;
import org.apache.ibatis.annotations.Update;

public interface Rm_GsServer {
    Rm_Gs getRm_GsByGoodsId(String id);


}
