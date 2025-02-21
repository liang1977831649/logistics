import request from "@/utils/request.js";

export const getColdChainCenterList=async ()=>{
    const result= await request.get("/coldChainCenter/list");
    return result;
}

export const getColdChainCenterListAdmin=async ()=>{
    const result= await request.get("/coldChainCenter/listAdmin");
    return result;
}
export const addColdChainCenterServer=async(coldChainCenterModel)=>{
    await request.post("/coldChainCenter",coldChainCenterModel.value);
}

export const updateColdChainCenterServer=async(coldChainCenterModel)=>{
    await request.put("/coldChainCenter",coldChainCenterModel.value);
}

export const deleteColdChainCenterServer=async(id)=>{
    await request.delete("/coldChainCenter/"+id);
}