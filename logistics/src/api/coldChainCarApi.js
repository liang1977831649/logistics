import request from "@/utils/request.js";

export const getColdChainCarListServer=async(searchBody)=>{
    const result= await request.get("/coldChainCar/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            name:searchBody.value.name,
            id:searchBody.value.id,
            status:searchBody.value.status
        }
     });

     return result;
}

export const addServer=async (coldChainCarModel)=>{
    await request.post("/coldChainCar",coldChainCarModel.value)
}

export const updateServer=async(coldChainCarModel)=>{
    await request.put("/coldChainCar",coldChainCarModel.value);
}

export const deleteServer =async (id)=>{
    await request.delete("/coldChainCar/"+id);
}