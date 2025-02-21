import request from "@/utils/request.js";

export const updateAdminPassword=async(passwordBody)=>{
    const param=new URLSearchParams;
    for(var key in passwordBody.value){
        param.append(key,passwordBody.value[key])
    }
    await request.post("/admin/password",param)
}
export const updateInfo=async(userModel)=>{
    const param=new URLSearchParams;
    for(var key in userModel.value){
        param.append(key,userModel.value[key])
    }
    await request.put("/admin",param)
}

export const getAdminListServer=async(searchBody)=>{
    const result= await request.get("/admin/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            name:searchBody.value.name,
            id:searchBody.value.id
        }
     });
     return result;
}
export const addAdminServer= async(adminModel)=>{
    request.post("/admin",adminModel.value);
}
export const updateAdminServer=async(adminModel)=>{
    await request.put("/admin/updateByManager",adminModel.value);
}