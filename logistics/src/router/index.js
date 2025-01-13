import { createRouter, createWebHistory } from "vue-router";
import Login from "@/components/Login.vue";
import Home from "@/components/Home.vue";
import routerStorage from "@/storage/routerStorage.js";
import useToken from "@/storage/tokenStorage.js";
import userInfoServer from "@/storage/userStorage";
import { ElMessage } from "element-plus";

const routers = [
  {
    path: "/login",
    component: Login,
    name: "login",
  },
  {
    path: "/",
    component: Home,
    name: "home",
  },
  {
    path: "/404",
    name: "404",
    component: () => import("../components/404.vue"),
  },
];
const router = createRouter({
  history: createWebHistory(),
  routes: routers,
});

//前置路由器
router.beforeEach((to, from, next) => {
  //如果是登陆页面，直接跳转
  if(to.path=='/login'){
    next();
  }
  console.log(to.path);
  if (!useToken().token) {
    //如果token等于空
    //清空剩余本地存储数据
    routerStorage().removeRouters();
    userInfoServer().removeUser();
    ElMessage.error("请先登录");
    next("/login");
    return;
  }
  //获取所有的路由以及子路由
  const allRouters = router.getRoutes();
  //获取当前Storage路由列表
  const routersList = routerStorage().routers;

  //循环遍历,检查现有路由是否存在
  allRouters.forEach((item) => {
    if (item.path == to.path) {
      //存在就直接跳转
      next();
      return;
    }
  });

  //如果现有的路由不存在,那就去本地存储路由表查找,按需添加
  routersList.forEach((item) => {
    if (item.path == to.path) {
      router.addRoute("home", {
        path: item.path,
        component: () =>
          import("../components/view/" + item.componentName + ".vue"),
      });
      next(to.path); //添加之后直接跳转
      return;
    }
  });
  //如果还没有跳转，那就说明没有找到
  //给他一个404
  next("/404");
});

export default router;
