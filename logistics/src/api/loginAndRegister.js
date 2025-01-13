import request from "@/utils/request.js";

export const loginServer=async (loginBody)=>{
    const param=new URLSearchParams;
    for(var key in loginBody.value){
        param.append(key,loginBody.value[key])
    }
    const result=await request.post('/login',param)
    return result;
}

export const registerServer=async(registerBody)=>{
    const param=new URLSearchParams;
    for(var key in registerBody.value){
        param.append(key,registerBody.value[key])
    }
     await request.post("/register",param)
}