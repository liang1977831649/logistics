<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getUserListServer, addServer, updateServer, deleteServer } from "@/api/userApi.js"
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import useToken from "@/storage/tokenStorage.js";
import locationStorage from "@/storage/locationStorage.js"
import { ElMessage, ElMessageBox } from "element-plus"

// =====================================数据区 =====================================
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;
const tempUrl = ref("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png")
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
  name: '',
  id: '',
  pageSize: 8,
  pageNum: 1
})
const dialogVisible = ref(false);
const tableData = ref([])
const title = ref('');
const userModel = ref({
  id: '',
  name: '',
  phone: '',
  sex: '',
  area: '',
  areaId: '',
  password: '',
  userPic: '',
  areaModel: []
})


const checkedPwd = (rules, value, callback) => {
  if (value !== registerBody.value.password) {
    callback(new Error('前后密码不一致'))
  } else {
    callback()
  }
}
const rules = {
  id: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { max: 10, min: 6, message: '账号字符长度在6-10之间', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 6, min: 2, message: '姓名字符长度在2-6之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码字符长度最少是6位数', trigger: 'blur' }
  ],
  rePassword: [
    { required: true, message: '请选择身份', trigger: 'blur' },
    { validator: checkedPwd, trigger: 'blur' }
  ],
  areaModel: [
    { required: true, message: '请选择地区', trigger: 'blur' },
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' },
  ]
}
const form = ref(null);
// =====================================方法区 =====================================

//加载当前的位置信息
const loadingAreaModel = () => {
  userModel.value.areaModel = locationStorage().location;
}
const search = async () => {
  const result = await getUserListServer(searchBody);
  tableData.value = result.data.items;
  allNumber.value = result.data.total;
}
const reset = () => {
  searchBody.value = {}
}
const handleSizeChange = async (number) => {
  searchBody.value.pageSize = number;
  await search()
}
const handleCurrentChange = async (number) => {
  searchBody.value.pageNum = number
  await search()
}
search();


const add = () => {
  cleanModel();
  title.value = '新增';
  dialogVisible.value = true;
}
const editButton = (row) => {
  if (row.userPic == null) {//如果头像为空，就设置为“”,否则深拷贝会报错
    row.userPic = ""
  }
  userModel.value = JSON.parse(JSON.stringify(row));
  loadingAreaModel();
  userModel.value.sex = userModel.value.sex + "";//转成字符串，否则表达加载不出来
  userModel.value.createTime = row.createTime;
  console.log(userModel.value);
  title.value = "编辑"
  dialogVisible.value = true;
}


const cleanModel = () => {
  userModel.value = {}
}

const onChange = (e) => {
  const three = chinaData[e[2]];
  userModel.value.areaModel = JSON.parse(JSON.stringify(e));
  userModel.value.area = three.label;
  userModel.value.areaId = three.value;
}
const loadAvatar = (res) => {
  userModel.value.userPic=res.data;
}
const editAndAdd = () => {
  form.value.validate(async valid => {
    if (valid) {
      if (title.value == '新增') {
        await addServer(userModel)
        ElMessage.success("新增成功!")
        dialogVisible.value = false;
        cleanModel();
        search()
      } else {

        await updateServer(userModel);
        ElMessage.success("修改成功");
        dialogVisible.value = false;
        cleanModel();
        search()
      }
    } else {
      ElMessage.error('数据错误');
    }
  })
}
const cancelButton = () => {
  dialogVisible.value = false;
  form.value.resetFields();
  cleanModel();
}

const deleteButton = async (row) => {
  ElMessageBox.confirm(
    '你确认要删除该用户信息吗?',
    '温馨提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: "warning",
    })
    .then(async () => {
      await deleteServer(row.id);
      await search()
      ElMessage.success("删除成功")
    })
    .catch(() => {
      ElMessage.info('用户取消了删除')
    })
}
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div>
          <el-input v-model="searchBody.id" style="width: 220px" placeholder="用户编号" :prefix-icon="Search" />
          <el-input v-model="searchBody.name" style="width: 220px;margin-left:10px" placeholder="用户姓名"
            :prefix-icon="Search" />
          <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
          <el-button type="info" @click="reset()">重置</el-button>
        </div>
        <div>
          <el-button type="primary" style="margin: 10px 0 10px 0;" @click="add()">新增</el-button>
        </div>
      </template>
      <div>
        <el-table :data="tableData" stripe style="width: 100%">
          <el-table-column prop="id" label="用户编号" />
          <el-table-column prop="name" label="用户名称" />
          <el-table-column prop="phone" label="用户电话" />
          <el-table-column prop="sex" label="性别">
            <template #default="scope">
              <el-tag :type="scope.row.sex == 1 ? 'success' : 'primary'" disable-transitions>
                {{ scope.row.sex == 1 ? '男' : '女' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sex" label="地区">
            <template #default="scope">
              <el-tag type="primary" disable-transitions>
                {{ scope.row.area }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" />
          <el-table-column prop="user_pic" label="用户头像">
            <template #default="{ row }">
              <el-image :src="row.userPic ? row.userPic : tempUrl" style="width: 40px;" />
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="editButton(row)">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="deleteButton(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <div>
          <!--分页插件-->
          <el-pagination style="float: right;
      margin-top: 5px;margin-bottom: 10px;" v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[8, 10, 20]"
            :disabled="disabled" :background="background" layout="prev, pager, next, jumper,total, sizes"
            :total="allNumber" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </template>
    </el-card>
    <!-- 添加或者编辑弹窗 -->
    <div>
      <el-dialog v-model="dialogVisible" :title="title" width="350px">
        <el-form :model="userModel" :rules="rules" ref="form">
          <el-form-item v-if="title == '新增'" label="编号" prop="id">
            <el-input v-model="userModel.id" />
          </el-form-item>
          <el-form-item v-else label="编号">
            <el-input v-model="userModel.id" disabled />
          </el-form-item>

          <el-form-item label="姓名" prop="name">
            <el-input v-model="userModel.name" />
          </el-form-item>

          <el-form-item label="电话" prop="phone">
            <el-input type="number" v-model="userModel.phone" />
          </el-form-item>

          <el-form-item v-if="title == '新增'" label="密码" prop="password">
            <el-input type="password" v-model="userModel.password" />
          </el-form-item>
          <el-form-item v-else label="密码">
            <el-input type="password" v-model="userModel.password" />
          </el-form-item>

          <el-form-item label="性别" prop="sex">
            <el-radio-group v-model="userModel.sex">
              <el-radio value=0>女</el-radio>
              <el-radio value=1>男</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="title == '编辑'" label="地区" prop="areaModel">
            <elui-china-area-dht placeholder="请选择" v-model="userModel.areaModel" @change="onChange"></elui-china-area-dht>
          </el-form-item>

          <el-form-item label="头像">
            <el-upload action="http://localhost:8080/upload" :show-file-list="false" :on-success="loadAvatar"
              :headers="{ 'Authorization': useToken().token }" name="multipartFile" :auto-upload="true">
              <el-button type="primary">点击上传</el-button>
              <!-- 上传头像 -->
              <img :src="userModel.userPic?userModel.userPic:tempUrl" width="100px" />
            </el-upload>
          </el-form-item>
        </el-form>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="cancelButton()">取消</el-button>
            <el-button type="primary" @click="editAndAdd()">
              确定
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<style scoped></style>

