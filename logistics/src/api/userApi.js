import request from "@/utils/request.js";

export const getUserListServer=async(searchBody)=>{
    const result= await request.get("/user/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            name:searchBody.value.name,
            id:searchBody.value.id
        }
     });
     return result;
}

export const addServer=async (userModel)=>{
    //因为在后端的User类中的password字段加上了Password，所以如果直接传递对象的话，会把JSON格式的password忽略掉
    //从而创建不了对象，报错
    //因此使用参数的方式，调用添加
    const param=new URLSearchParams;
    for(var key in userModel.value){
        param.append(key,userModel.value[key])
    }
    await request.post("/user",param)
}

export const updateServer=async(userModel)=>{
    await request.put("/user",userModel.value);
}

export const deleteServer =async (id)=>{
    await request.delete("/user/"+id);
}

export const updateUserPassword=async(passwordBody)=>{
    const param=new URLSearchParams;
    for(var key in passwordBody.value){
        param.append(key,passwordBody.value[key])
    }
    await request.post("/user/password",param)
}

export const detailUserServer=async (id)=>{
    return await request.get("/user/detail/"+id);
}