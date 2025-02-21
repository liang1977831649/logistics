<script setup>
//==============================================引用区=================================================
import { ref } from "vue";
import Header from "@/components/Header.vue"
import Aside from "@/components/Aside.vue";
import { getMenuServer } from "@/api/menu.js"
import routerStorage from "@/storage/routerStorage.js"
import {loadingUserInfoServer} from "@/api/userInfoApi"
import userInfoServer from "@/storage/userStorage.js"
import {getAreaList} from "@/api/areaApi.js"
import localStorage from "@/storage/locationStorage.js"
import {addMoneyAccountServer} from "@/api/moneyApi"
//==============================================数据区=================================================
const routers=routerStorage();
const tabs = ref([])
const moneyModel=ref({});
//==============================================方法区=================================================
const getMenu = async () => {
  const result = await getMenuServer();
  // console.log(result.data);
  tabs.value = result.data;
  loadingRoutes(tabs.value,0)
}
getMenu()

//加载路由
const loadingRoutes=(itemList, index) => {
  if (index + 1 > itemList.length) {
    return;
  }
  var item = itemList[index]
  if (item.menuType == 2) {//如果是一个二级路由
    //获取到他的子项
    var itemSub = item.menuList;
    console.log(itemSub);
    if (itemSub.length>0) {//如果存在子项
      loadingRoutes(itemSub, 0);
    }
  } else {//如果是一级路由
    const route={
      path:item.path,
      componentName:item.component
    }
    routers.pushRoute(route)
  }
  loadingRoutes(itemList,++index);
}

//加载个人信息
const loadingUserInfo=async()=>{
  const result=await loadingUserInfoServer();
  userInfoServer().setUser(result.data);
  console.log("user=",result);
  loadingLocation(result.data.areaId)//加载个人地理位置信息，存储到pinia
}
loadingUserInfo();

const loadingLocation= async(areaId)=>{
  const result= await getAreaList(areaId);
  localStorage().setLocation(result.data)
}

//加载是否具备银行账户
const loadingMoney=async()=>{
  await addMoneyAccountServer(moneyModel);
}
loadingMoney()

</script>

<template>
  <el-container class="home-container">
    <!-- 头部区 -->
    <el-header>
      <!-- ref="childHeader"就是将Header这个对象在js中什么，以便调用其中的方法 -->
      <Header ref="childHeader" />
    </el-header>
    <!-- 页面主体区域 -->
    <el-container>
      <!-- 侧边栏 -->
      <el-aside style="height: 100vh;width: 200px;">
        <el-menu active-text-color="#ffd04b" background-color="#545c64" default-active="2" text-color="#fff"
           router>
          <Aside :tabs="tabs" />
        </el-menu>
      </el-aside>
      <!-- 右侧内容主体 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style lang="less" scoped>
.home-container {
  height: 100%;
}

.el-header {
  background-color: #373d41;
  display: flex; //设置显示为flex布局
  justify-content: space-between; //设置为flex左右布局
  padding-left: 0; //左内边距为0（Logo贴左边）
  align-items: center; //元素上下居中（防止右边按钮贴上下边）
  color: #fff;
  font-size: 20px;

  >div {
    //内嵌的div样式
    display: flex;
    align-items: center; //Logo和文字上下居中

    span {
      margin-left: 15px; //文字左侧设置间距，防止与Logo紧贴
    }
  }
}

.el-main {
  background-color: #eaedf1;
  width: 100%;
  height: 100vh;
}

.el-aside {
  background-color: #333744;
}
</style>