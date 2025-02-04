import request from "@/utils/request.js";

export const payRecordServer=async (payMentRecord)=>{
    await request.post("/paymentRecord",payMentRecord.value)
}

export const getPaymentRecordListServer= async (searchBody)=>{
    const result=await request.get("/paymentRecord/list",{
        params:{
            pageNum:searchBody.value.pageNum,
            pageSize:searchBody.value.pageSize,
            userName:searchBody.value.userName,
            transactionId:searchBody.value.transactionId,
            transaction:searchBody.value.transaction
        }
     })
     return result;
}