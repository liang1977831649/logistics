import { ref } from "vue";
import { defineStore } from "pinia";

const routerStorage = defineStore("routers", () => {
  const routers = ref([]);

  const pushRoute = (route) => {
     let flag=false;
    for(let i=0;i<routers.value.length;i++){
        let itemRoute=routers.value[i];
        if(itemRoute.path==route.path&&itemRoute.componentName==route.componentName){
            flag=true;
            break;
        }
    }
    if(flag==false){
        routers.value.push(route);
    }
  }

  const removeRouters = () => {
    routers.value = [];
  }

  return { routers, pushRoute, removeRouters }
  
},{persist:true});

export default routerStorage;
