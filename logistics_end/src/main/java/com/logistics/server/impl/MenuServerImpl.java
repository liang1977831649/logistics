package com.logistics.server.impl;

import com.logistics.entity.Menu;
import com.logistics.mapper.MenuMapper;
import com.logistics.server.MenuServer;
import com.logistics.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
@Service
public class MenuServerImpl implements MenuServer {
    @Autowired
    private MenuMapper menuMapper;
    @Override
    public List<Menu> getMenuListByRole() {

        String role = ((HashMap<String, Object>) ThreadLocalUtils.get()).get("role")+"";

        List<Menu> menuList = menuMapper.getMenuByRole(role);
        recursionMenu(menuList,0);
        cleanHasFatherMenu(menuList);
        return menuList;

    }

    public void recursionMenu(List<Menu> menuList, int index) {
        if (index+1 > menuList.size()) {
            return;
        }
        Menu menu = menuList.get(index);
        if (menu.getFather() != 0) {
            int fatherIndex = getFatherIndex(menu.getFather(), menuList);
            if(fatherIndex==-1){//如果找不到父节点
                recursionMenu(menuList,++index);
                return;
            }
            Menu menuFather = menuList.get(fatherIndex);
            List<Menu> menuListSub = menuFather.getMenuList();
            if(menuListSub==null){
                menuListSub=new ArrayList<>();
            }
            menuListSub.add(menu);
            menuFather.setMenuList(menuListSub);
        }
        recursionMenu(menuList,++index);
    }

    public int getFatherIndex(int fatherId,List<Menu> list){
        for(int i=0;i<=list.size();i++){
            Menu menu = list.get(i);
            if(menu.getId()==fatherId){
                return i;
            }
        }
        return -1;
    }

    public void cleanHasFatherMenu(List<Menu> menuList){
        Iterator<Menu> iterator = menuList.iterator();
        while (iterator.hasNext()){
            Menu next = iterator.next();
            if(next.getFather()!=0){
                iterator.remove();
            }
        }
    }
}
