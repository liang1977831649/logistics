import request from "@/utils/request.js";

export const getAreaList= async(areaId)=>{
    const result=request.get("/area/getArea/"+areaId);
    return result;
}