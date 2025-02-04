import request from "@/utils/request.js";

export const getRoomCostListServer=(searchBody)=>{
    return request.get("/roomCost/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            goodsName:searchBody.value.goodsName,
            deliId:searchBody.value.deliId,
            status:searchBody.value.status,
            userId:searchBody.value.userId
        }
     })
}

export const updateRoomCostServer=async (roomCostModel)=>{
    await request.put("/roomCost",roomCostModel.value);
}

export const detailRoomCostServer=async(id)=>{
    return await request.get("/roomCost/detail/"+id);
}

export const getIncomeRoomCostServer=async()=>{
    return await request.get('/roomCost/incomeLastDay');
}