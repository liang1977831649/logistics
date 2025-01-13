<script setup>
// =====================================引用区 =====================================

import { ref } from "vue"
import { Lock } from "@element-plus/icons-vue"
import userInfoServer from "@/storage/userStorage.js"
import { updateUserPassword } from "@/api/userApi.js"
import { updateAdminPassword } from "@/api/adminApi.js"
import routerStorage from "@/storage/routerStorage";
import useToken from "@/storage/tokenStorage.js";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus"

// =====================================数据区 =====================================
const router = useRouter();
const password = ref({
    oldPwd: '',
    newPwd: '',
    rePwd: ''
})
const checkPassword = (rule, value, callback) => {
    if (value !== password.value.newPwd) {
        callback(new Error('前后密码不一致'))
    } else {
        callback();
    }
}

const rules = {
    oldPwd: [
        { required: true, message: "请输入原密码", trigger: 'blur' }
    ],
    newPwd: [
        { required: true, message: "请输入新密码", trigger: 'blur' },
        { pattern: /^(\d){6,}$/, message: '密码长度最少在6位', trigger: 'blur' }
    ],
    rePwd: [
        { required: true, message: "请输入新密码", trigger: 'blur' },
        { validator: checkPassword, trigger: 'blur' }
    ]
}
const form = ref(null);
// ===================================== 方法区 =====================================
const updatePassword = () => {
    ElMessageBox.confirm(
        '你确认要修改您的信息吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            form.value.validate((async valid => {
                if (valid) {
                    //如果是用户
                    if (userInfoServer().userInfo.role === 1) {
                        await updateUserPassword(password);
                    }
                    //如果是管理员
                    else if (userInfoServer().userInfo.role === 0) {
                        await updateAdminPassword(password);
                    }
                    ElMessage.success("修改密码成功！")
                    //清空
                    password.value = {}
                    //清除所有数据，并返回到登录页
                    userInfoServer().removeUser();
                    routerStorage().removeRouters();
                    useToken().removeToken();
                    //返回到登录页面
                    router.push('/login')
                } else {
                    ElMessage.error("数据格式错误")
                }
            }))
        })
        .catch(() => {
            ElMessage.info('用户取消了删除')
        })


}


</script>

<template>
    <div>
        <el-card style="width: 500px;">
            <template #header>
                <h1>修改密码</h1>
            </template>
            <el-form :model="password" :rules="rules" ref="form" style="width: 400px;">
                <el-form-item prop="oldPwd" label="原来密码">
                    <el-input :prefix-icon="Lock" type="password" v-model="password.oldPwd" placeholder="输入原密码" />
                </el-form-item>

                <el-form-item prop="newPwd" label="新设密码">
                    <el-input :prefix-icon="Lock" type="password" v-model="password.newPwd" placeholder="输入新密码" />
                </el-form-item>

                <el-form-item prop="rePwd" label="重复密码">
                    <el-input :prefix-icon="Lock" type="password" v-model="password.rePwd" placeholder="输入新密码" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-form-item>
                    <el-button type="primary" @click="updatePassword()">确认修改</el-button>
                </el-form-item>
            </template>
        </el-card>
    </div>
</template>

<style></style>