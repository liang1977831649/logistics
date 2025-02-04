import request from "@/utils/request.js";

export const showRoomCostCompute=async ()=>{
    return await request.get("/roomCostCompute/show")
}
export const updateRoomCostCompute=async(roomCostComputeBody)=>{
    await request.put("/roomCostCompute",roomCostComputeBody.value);
}