import request from "@/utils/request.js";


export const getMoneyServer=async ()=>{
    return await request.get("/money/get");
}

export const updateMoneyServer=async (moneyBody)=>{
    await request.put("/money",moneyBody.value);
}
export const withdrawMoney=async()=>{
    await request.get("/money/withdraw")
}

export const passwordValidServer=async(userModel)=>{
    await request.post("/money/passwordValid",userModel.value);
}
export const addMoneyAccountServer=async()=>{
    await request.post("/money")
}