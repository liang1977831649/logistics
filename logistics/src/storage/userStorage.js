import { ref } from "vue";
import { defineStore } from "pinia";

 const userInfoServer=defineStore("userInfo",()=>{
    const userInfo=ref({})

    const setUser=(newUser)=>{
        userInfo.value=newUser
    }
    const removeUser=()=>{
        userInfo.value={}
    }

    return {userInfo ,setUser,removeUser}
},{persist:true})

export default userInfoServer

