import { ElMessage } from "element-plus";
import axios from "axios";
import  useToken  from "@/storage/tokenStorage.js";
import router from "@/router";

const baseURL = "http://localhost:8080"; //代表的就是：http://ip地址:端口号/api
const instance = axios.create({ baseURL });

//请求拦截器
instance.interceptors.request.use((config) => {
  const tokenObj = useToken();
  if (tokenObj.token != null) {
    config.headers.Authorization = tokenObj.token;
  }
  return config;
});

//响应拦截器
instance.interceptors.response.use(
  (result) => {
    if (result.data.code === 0) {
      return result.data;
    } else {
      //return result.fail()，已经将状态码code设置成其他状态了
      ElMessage.error(result.data.message ? result.data.message : "服务器异常");
      return Promise.reject(result.data);
    }
  },
  (err) => {
    //后端抛出RuntimeException
    if (err.response.status === 401) {
      ElMessage.error("登陆已过期");
      router.push("/login");
    } else {
      ElMessage.error("服务器异常");
    }
    return Promise.reject(err);
  }
);

export default instance;