import request from "@/utils/request.js";

export const getMenuServer=async ()=>{
     const result=await request.get("/menu");
    return result;
}