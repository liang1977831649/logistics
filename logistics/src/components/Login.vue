<script setup>
//------------------------引用区----------------------------
import { ref } from "vue"
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from "element-plus"
import { useRouter } from "vue-router";
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import { loginServer, registerServer } from "@/api/loginAndRegister"
import useToken from "@/storage/tokenStorage.js"
//------------------------数据定义区--------------------------
const router = useRouter()
const isRegister = ref(false)
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;
const options = ref([
    {
        value: '0',
        label: '管理员',
    },
    {
        value: '1',
        label: '用户',
    },
    {
        value: '2',
        label: '司机',
    },
])

const registerBody = ref({
    id: '',
    password: '',
    rePassword: '',
    area: '',
    areaId: '',
    areaModel: []//替身
})
const loginBody = ref({
    id: '',
    password: '',
    role: ''
})

const form = ref(null)
const loginRules = {
    id: [
        { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    role: [
        { required: true, message: '请选择身份', trigger: 'change' },
    ]
}
const checkedPwd = (rules, value, callback) => {
    if (value !== registerBody.value.password) {
        callback(new Error('前后密码不一致'))
    } else {
        callback()
    }
}
const registerRules = {
    id: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { max: 10, min: 6, message: '账号字符长度在6-10之间', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码字符长度最少是6', trigger: 'blur' }
    ],
    rePassword: [
        { required: true, message: '请选择身份', trigger: 'blur' },
        { validator: checkedPwd, trigger: 'blur' }
    ],
    area: [
        { required: true, message: '请选择身份', trigger: 'blur' },
    ],
    areaModel: [
        { required: true, message: '请选择地区', trigger: 'blur' },
    ]
}
// ---------------------==============方法区-----------------------
const clearRegisterBody = () => {
    registerBody.value = {};
}

const onChange = (e) => {
    console.log("e=",e);
    const three = chinaData[e[2]];
    registerBody.value.areaModel = JSON.parse(JSON.stringify(e));
    registerBody.value.area = three.label;
    registerBody.value.areaId = three.value;
}

const login = () => {
    form.value.validate(async valid => {
        if (valid) {
            // router.push("/")
            const result = await loginServer(loginBody)
            useToken().setToken(result.data)
            router.push('/')
        } else {
            ElMessage.error('数据错误');
        }
    })
}

const register = () => {
    form.value.validate(async valid => {
        if (valid) {
            isRegister.value = false;
            console.log("registerBody=", registerBody.value);
            await registerServer(registerBody);
            clearRegisterBody();
            ElMessage.success('注册成功,请登录')
        } else {
            ElMessage.error('数据错误');
        }
    })
}
</script>

<template>
    <el-row class="login-page">
        <el-col :span="13" class="bg"></el-col>
        <el-col :span="7" :offset="3" class="form">
            <!-- 注册表单 -->
            <el-card v-if="isRegister">
                <el-form autocomplete="off" :model="registerBody" :rules="registerRules" ref="form">
                    <el-form-item class="title_font">
                        <span>农产品冷链物流系统</span>
                    </el-form-item>
                    <el-form-item class="title_font">
                        <span>注册</span>
                    </el-form-item>
                    <el-form-item prop="id">
                        <el-input :prefix-icon="User" placeholder="请输入账号" v-model="registerBody.id"></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码"
                            v-model="registerBody.password"></el-input>
                    </el-form-item>
                    <el-form-item prop="rePassword">
                        <el-input :prefix-icon="Lock" type="password" placeholder="请输入再次密码"
                            v-model="registerBody.rePassword"></el-input>
                    </el-form-item>
                    <el-form-item prop="areaModel">
                        <elui-china-area-dht placeholder="请选择地区" v-model="registerBody.areaModel"
                            @change="onChange"></elui-china-area-dht>
                    </el-form-item>
                    <!-- 注册按钮 -->
                    <el-form-item>
                        <el-button class="button" type="primary" auto-insert-space @click="register"> 注册 </el-button>
                    </el-form-item>
                    <el-form-item class="flex">
                        <el-link type="info" :underline="false" @click="isRegister = false;">
                            ← 返回
                        </el-link>
                    </el-form-item>
                </el-form>
            </el-card>
            <!-- 登录表单 -->
            <el-card v-else>
                <el-form autocomplete="off" :model="loginBody" :rules="loginRules" ref="form">
                    <el-form-item class="title_font">
                        <span>农产品物流冷链系统</span>
                    </el-form-item>
                    <el-form-item class="title_font">
                        <span>登录</span>
                    </el-form-item>
                    <el-form-item prop="id">
                        <el-input :prefix-icon="User" placeholder="请输入账号" v-model="loginBody.id"></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码"
                            v-model="loginBody.password"></el-input>
                    </el-form-item>
                    <el-form-item prop="role">
                        <el-select placeholder="请选择身份" v-model="loginBody.role">
                            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </el-form-item>
                    <el-form-item class="flex">
                        <div class="flex">
                            <el-checkbox>记住我</el-checkbox>
                            <el-link type="primary" :underline="false">忘记密码？</el-link>
                        </div>
                    </el-form-item>
                    <!-- 登录按钮 -->
                    <el-form-item>
                        <el-button class="button" type="primary" auto-insert-space @click="login">登录</el-button>
                    </el-form-item>
                    <el-form-item class="flex">
                        <el-link type="info" :underline="false" @click="isRegister = true;">
                            注册 →
                        </el-link>
                    </el-form-item>
                </el-form>
            </el-card>
        </el-col>
    </el-row>
</template>

<style lang="scss" scoped>
/* 样式 */

.login-page {
    height: 100vh;
    background: url('@/assets/image/logo_font2.png') no-repeat 0.1% center / 1000px auto,
        url('@/assets/image/bj.jpg') no-repeat center / cover;

    // .bg {
    //     background: url('@/assets/image/logo_font2.png') no-repeat 70% center / 850px auto,
    //         url('@/assets/image/bj.jpg') no-repeat center / cover;
    //     border-radius: 0 20px 20px 0;
    // }

    .form {
        opacity: 0.92;
        display: flex;
        flex-direction: column;
        justify-content: center;
        user-select: none;

        .title {
            margin: 0 auto;
        }

        .button {
            width: 100%;
        }

        .flex {
            width: 100%;
            display: flex;
            justify-content: space-between;
        }
    }
}

@font-face {
    font-family: 'font_one';
    src: url(../assets/font/JiangHuFengti.ttf);
}

.title_font span {
    width: 500px;
    font-family: 'font_one';
    font-size: 34px;
}
</style>