<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getAdminListServer,addAdminServer,updateAdminServer } from "@/api/adminApi.js"
import { ElMessage } from "element-plus"
import { EluiChinaAreaDht } from 'elui-china-area-dht'

// =====================================数据区 =====================================
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
const adminModel = ref({
    id: '',
    name: '',
    phone: '',
    areaId: '',
    password: '',
    areaModel: []//替身
})

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
    phone: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { max: 11, min: 11, message: '账号字符长度11', trigger: 'blur' }
    ],
}
const form = ref(null);

const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;

// =====================================方法区 =====================================

const search = async () => {
    const result = await getAdminListServer(searchBody);
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

const onChange = (e) => {
    console.log("e=", e);
    const three = chinaData[e[2]];
    adminModel.value.areaModel = JSON.parse(JSON.stringify(e));
    adminModel.value.area = three.label;
    adminModel.value.areaId=three.value;
}

const add = () => {
    cleanModel();
    title.value = '新增';
    dialogVisible.value = true;
}
const editButton = (row) => {
    adminModel.value = JSON.parse(JSON.stringify(row));
    console.log(adminModel.value);
    title.value = "编辑"
    dialogVisible.value = true;
}
const cleanModel = () => {
    adminModel.value = {}
}
const editAndAdd = () => {
    form.value.validate(async valid => {
        if (valid) {
            if (title.value == '新增') {
                await addAdminServer(adminModel)
                ElMessage.success("新增成功!")
                dialogVisible.value = false;
                cleanModel();
                await search()
            } else {
                await updateAdminServer(adminModel);
                ElMessage.success("修改成功");
                dialogVisible.value = false;
                cleanModel();
                await search()
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
                    <el-table-column prop="id" label="管理员编号" />
                    <el-table-column prop="name" label="管理员名称" />
                    <el-table-column prop="phone" label="管理员电话" />
                    <el-table-column prop="areaId" label="县域代码" />
                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" size="small" @click="editButton(row)">
                                编辑
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <template #footer>
                <div>
                    <!--分页插件-->
                    <el-pagination style="float: right;
      margin-top: 5px;margin-bottom: 10px;" v-model:current-page="currentPage" v-model:page-size="pageSize"
                        :page-sizes="[8, 10, 20]" :disabled="disabled" :background="background"
                        layout="prev, pager, next, jumper,total, sizes" :total="allNumber" @size-change="handleSizeChange"
                        @current-change="handleCurrentChange" />
                </div>
            </template>
        </el-card>
        <!-- 添加或者编辑弹窗 -->
        <div>
            <el-dialog v-model="dialogVisible" :title="title" width="350px">
                <el-form :model="adminModel" :rules="rules" ref="form">
                    <el-form-item v-if="title == '新增'" label="编号" prop="id">
                        <el-input v-model="adminModel.id" />
                    </el-form-item>
                    <el-form-item v-else label="编号">
                        <el-input v-model="adminModel.id" disabled />
                    </el-form-item>

                    <el-form-item label="名称" prop="name">
                        <el-input v-model="adminModel.name" />
                    </el-form-item>

                    <el-form-item  label="电话" prop="phone">
                        <el-input type="number" v-model="adminModel.phone" />
                    </el-form-item>

                    <el-form-item v-if="title == '新增'" label="密码" prop="password">
                        <el-input type="password" v-model="adminModel.password" />
                    </el-form-item>
                    <el-form-item v-else label="密码">
                        <el-input type="password" v-model="adminModel.password" />
                    </el-form-item>

                    <el-form-item v-if="title == '新增'" prop="areaModel">
                        <elui-china-area-dht placeholder="请选择地区" v-model="adminModel.areaModel"
                            @change="onChange"></elui-china-area-dht>
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

