import request from "@/utils/request.js";

export const getRefrigerateListServer=async(searchBody)=>{
    const result= await request.get("/refrigerate/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            name:searchBody.value.name,
            id:searchBody.value.id
        }
     });

     return result;
}

export const addServer=async (refrigerateModel)=>{
    await request.post("/refrigerate",refrigerateModel.value)
}

export const updateServer=async(refrigerateModel)=>{
    await request.put("/refrigerate",refrigerateModel.value);
}

export const deleteServer =async (id)=>{
    await request.delete("/refrigerate/"+id);
}

export const detailRefrigerateServer=async(id)=>{
    const result=await request.get("/refrigerate/detail/"+id);
    return result
}
export const inRoomServer= async(rmGs)=>{
    await request.post("/refrigerate/inRoom",rmGs.value);
}