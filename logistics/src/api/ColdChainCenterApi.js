import request from "@/utils/request.js";

export const getColdChainCenterList=async ()=>{
    const result= await request.get("/coldChainCenter/list");
    return result;
}