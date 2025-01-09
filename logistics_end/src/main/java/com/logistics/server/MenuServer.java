package com.logistics.server;

import com.logistics.entity.Menu;

import java.util.List;

public interface MenuServer {
    List<Menu> getMenuListByRole();
}
