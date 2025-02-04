import request from "@/utils/request.js";

export const getTransportationListServer=async(searchBody)=>{
    const result= await request.get("/transportation/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            goodsName:searchBody.value.goodsName,
            id:searchBody.value.id,
            userId:searchBody.value.userId,
            goodsId:searchBody.value.goodsId,
            status:searchBody.value.status
        }
     });

     return result;
}

export const addServer=async (transportationModel)=>{
    await request.post("/transportation",transportationModel.value)
}

export const updateServer=async(transportationModel)=>{
    await request.put("/transportation",transportationModel.value);
}

export const deleteServer =async (id)=>{
    await request.delete("/transportation/"+id);
}

export const detailServer=async(id)=>{
    const result=await request.get("/transportation/detail/"+id);
    return result
}
export const distributionServer=async(transportationModel)=>{
    await request.put("/transportation/distribution",transportationModel.value)
}

// export const modifyDistributionServer=async(transportationModel)=>{
//     await request.put("/transportation/distribution",transportationModel.value);
// }