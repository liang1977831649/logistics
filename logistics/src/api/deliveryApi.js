import request from "@/utils/request.js";

export const addDeliveryServer=async (deliveryBody)=>{
    await request.post("/delivery",deliveryBody.value)
}
export const getDeliverList=async(searchBody)=>{
    const result= await request.get("/delivery/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            goodsName:searchBody.value.goodsName,
            id:searchBody.value.id,
            userId:searchBody.value.userId,
            status:searchBody.value.status
        }
     });
     return result;
}

export const deleteDeliveryServer=async (id)=>{
    await request.delete("/delivery/"+id);
}

export const changeLoadingDelivery=async(deliveryModel)=>{
    await request.put("/delivery/loading",deliveryModel.value);
}

export const arrivalServer=async (id)=>{
    await request.get("/delivery/arrival/"+id);
}
export const receiptGoodsServer=async (id)=>{
    await request.get("/delivery/receipt/"+id);
}
export const detailDeliveryServer=async(id)=>{
    return await request.get("/delivery/detail/"+id);
}