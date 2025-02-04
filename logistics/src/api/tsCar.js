import request from "@/utils/request.js";


export const detailServer=async (id)=>{
    const result=await request.get("/tsCar/detail/"+id);
    return result;
}

export const getTsCarList=async (searchBody)=>{
    const result= await request.get("/tsCar/list",{
        params:{
            id:searchBody.value.id,
            carId:searchBody.value.carId,
            driverName:searchBody.value.driverName,
            driverId:searchBody.value.driverId,
            status:searchBody.value.status,
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
        }
     });
     return result;
}
export const addTsCarServer=async (tsCarModel)=>{
    await request.post("/tsCar",tsCarModel.value);
}

export const editServer=async(tsCarModel)=>{
   await request.put("/tsCar",tsCarModel.value);
}
export const deleteServer=async(id)=>{
    await request.delete("/tsCar/"+id);
}
export const departureServer= async(tsCarModel)=>{
    await request.post("/tsCar/departure",tsCarModel.value)
}