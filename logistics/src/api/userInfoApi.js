import request from "@/utils/request.js";

export const loadingUserInfoServer=async ()=>{
    const result=await request.get("/getPersonInfo");
    return result
}