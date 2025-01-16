import request from "@/utils/request.js";

export const getGoodsListServer=async(searchBody)=>{
    const result= await request.get("/goods/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            name:searchBody.value.name,
            id:searchBody.value.id
        }
     });

     return result;
}

export const addServer=async (goodsModel)=>{
    await request.post("/goods",goodsModel.value)
}

export const updateServer=async(goodsModel)=>{
    await request.put("/goods",goodsModel.value);
}

export const deleteServer =async (id)=>{
    await request.delete("/goods/"+id);
}

export const detailServer=async(id)=>{
    const result=await request.get("goods/detail/"+id);
    return result
}