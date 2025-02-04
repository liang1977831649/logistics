<script setup>
//=========================================引用区=========================================
import userInfoServer from "@/storage/userStorage"
import { ref } from "vue"
import locationStorage from "@/storage/locationStorage"
import { ElMessage, ElMessageBox } from "element-plus"
import useToken from "@/storage/tokenStorage"
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import {updateServer} from "@/api/userApi"
import {loadingUserInfoServer} from "@/api/userInfoApi.js"

//=========================================数据区=========================================
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;
const user = userInfoServer();
const userModel = ref({
  id: '',
  name: '',
  phone: '',
  sex: '',
  area: '',
  areaId: '',
  role: '',
  password: '',
  userPic: '',
  areaModel: []
})

const form = ref()
const rules = {
  id: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { max: 10, min: 6, message: '账号字符长度在6-10之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 6, min: 2, message: '姓名字符长度在2-6之间', trigger: 'blur' }
  ],
  areaModel: [
    { required: true, message: '请选择地区', trigger: 'blur' },
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' },
  ],
  phone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^\d{11,12}$/, message: '电话格式错误', trigger: 'blur' }
  ]
}
//=========================================方法区=========================================
const loadInfo = () => {
  userModel.value = JSON.parse(JSON.stringify(user.userInfo))
  userModel.value.role = userModel.value.role + "";
  userModel.value.sex = userModel.value.sex + "";
  userModel.value.areaModel = locationStorage().location;
  console.log("userModel.value=", userModel.value);
}
loadInfo();

const save = () => {
  ElMessageBox.confirm(
    '你确认要修改您的信息吗?',
    '温馨提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: "warning",
    })
    .then(async () => {
      form.value.validate(async valid => {
        if (valid) {
          console.log("userModel.value=", userModel.value);
          await updateServer(userModel)
          const result=await loadingUserInfoServer();
          user.userInfo=result.data
          ElMessage.success("修改成功")
        } else {
          ElMessage.error('数据格式有误');
        }
      })
    })
    .catch(() => {
      ElMessage.info('用户取消了删除')
    })
}

const loadAvatar = (res) => {
  userModel.value.userPic = res.data;
}

const onChange = (e) => {
  console.log("e=", e);
  const three = chinaData[e[2]];
  userModel.value.areaModel = JSON.parse(JSON.stringify(e));
  userModel.value.area = three.label;
  userModel.value.areaId = three.value;
}
</script>

<template>
  <div>
    <el-card style="width: 500px;">
      <template #header>
        <h1>您的信息如下</h1>
      </template>
      <el-form :model="userModel" :rules="rules" ref="form" style="width: 400px;">
        <el-form-item label="编号">
          <el-input v-model="userModel.id" disabled />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="userModel.name" />
        </el-form-item>

        <el-form-item label="电话" prop="phone" v-if="user.userInfo.role==1">
          <el-input type="number" v-model="userModel.phone" />
        </el-form-item>

        <el-form-item label="性别" prop="sex" v-if="user.userInfo.role==1">
          <el-radio-group v-model="userModel.sex">
            <el-radio value=0>女</el-radio>
            <el-radio value=1>男</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="地区" prop="areaModel" v-if="user.userInfo.role==1">
          <elui-china-area-dht placeholder="请选择" v-model="userModel.areaModel" @change="onChange"></elui-china-area-dht>
        </el-form-item>

        <el-form-item label="头像" v-if="user.userInfo.role==1">
          <el-upload action="http://localhost:8080/upload" :show-file-list="false" :on-success="loadAvatar"
            :headers="{ 'Authorization': useToken().token }" name="multipartFile" :auto-upload="true">
            <el-button type="primary">点击上传</el-button>
            <img :src="userModel.userPic ? userModel.userPic : tempUrl" width="100px" />
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="save">确认保存</el-button>
      </template>
    </el-card>
  </div>
</template>



<style></style>