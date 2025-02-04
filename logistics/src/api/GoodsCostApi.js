import request from "@/utils/request.js";

export const getGoodsCostListServer= async (searchBody)=>{
    const result=await request.get("/goodsCost/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            goodsName:searchBody.value.goodsName,
            userName:searchBody.value.userName,
            id:searchBody.value.id,
            status:searchBody.value.status,
            type:searchBody.value.type
        }
     })
     return result;
}

export const getDetailGoodsCostServer=async (id)=>{
    return await request.get("goodsCost/detail/"+id);
}

export const updateGoodsCostServer=async(goodsCostModel)=>{
    await request.put("/goodsCost",goodsCostModel.value)
}