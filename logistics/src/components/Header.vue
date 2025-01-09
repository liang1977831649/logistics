<script setup>
//=======================引用区==========================
import { ElMessage, ElMessageBox } from "element-plus"
import userInfoServer from "@/storage/user";
import {ref} from "vue"
import logoHeader from "../assets/image/logo_header.png"
//=======================数据区==========================
const user=userInfoServer();
const tempUrl=ref("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png")
//头像的下拉菜单
const handleCommand = (command) => {
    console.log("command=", command);
    if (command === 'logout') {
        ElMessageBox.confirm(
            '你确认要退出登录吗?',
            '温馨提示',
            {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                type: "warning",
            })
            .then(async () => {
                token.removeToken();//清除token
                user.removeUser();//清除user
                useDynamicRouter.removeRouter();//清楚路由信息
                router.push('/')
            })
            .catch(() => {
                ElMessage.info('取消了退出')
            })
    }
    else {
        router.push('/' + command);
    }
}
//=======================方法区==========================


</script>

<template>
    <div >
        <el-image :src="logoHeader" style="width: 38px;height: 38px;vertical-align:middle;margin-left: 20px;"></el-image>
        <span style="margin-left: 10px;">农产品冷链物流系统</span>
    </div>
    <div>
        <div style="float: left; margin-right: 20px;margin-top: 5px;"><strong>{{ user.userInfo.name?user.userInfo.name:'张XX' }}</strong></div>
        <el-dropdown style="float: right;" placement="bottom-end" @command="handleCommand">
            <el-avatar :src="tempUrl ? tempUrl : defaultAvatar" />
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item v-if="user.userInfo.role==1" command="userInfo" :icon="User">基本资料</el-dropdown-item>
                    <el-dropdown-item command="password" :icon="EditPen">重置密码</el-dropdown-item>
                    <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>
</template>

<style lang="less" scoped>
.el-dropdown__box {
    display: flex;
    align-items: center;

    .el-icon {
        color: #999;
        margin-left: 10px;
    }

    &:active,
    &:focus {
        outline: none;
    }
}
</style>