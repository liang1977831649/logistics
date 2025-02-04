import request from "@/utils/request.js";

export const getDriverListServer=async(searchBody)=>{
    const result= await request.get("/driver/list",{
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

export const addServer=async (driverModel)=>{
    
    await request.post("/driver",driverModel.value)
}

export const updateServer=async(driverModel)=>{
    await request.put("/driver",driverModel.value);
}

export const deleteServer =async (id)=>{
    await request.delete("/driver/"+id);
}

