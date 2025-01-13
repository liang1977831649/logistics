import request from "@/utils/request.js";

export const updateAdminPassword=async(passwordBody)=>{
    const param=new URLSearchParams;
    for(var key in passwordBody.value){
        param.append(key,passwordBody.value[key])
    }
    await request.post("/admin/password",param)
}