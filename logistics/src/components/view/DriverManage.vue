<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getDriverListServer, addServer, updateServer, deleteServer } from "@/api/driverApi.js"
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import locationStorage from "@/storage/locationStorage.js"
import { ElMessage, ElMessageBox } from "element-plus"

// =====================================数据区 =====================================
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;
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
const driverModel = ref({
    id: '',
    name: '',
    phone: '',
    sex: '',
    area: '',
    areaId: '',
    status: '',
    areaModel: []
})



const rules = {
    id: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { pattern: /^sj.+$/, message: '账号以sj开头', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { max: 6, min: 2, message: '姓名字符长度在2-6之间', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入电话', trigger: 'blur' },
        { max: 11, min: 11, message: '电话格式错误', trigger: 'blur' }
    ],
    areaModel: [
        { required: true, message: '请选择地区', trigger: 'blur' },
    ],
    sex: [
        { required: true, message: '请选择性别', trigger: 'change' },
    ],
    status: [
        { required: true, message: '请选择性别', trigger: 'change' },
    ]
}
const form = ref(null);
// =====================================方法区 =====================================

//加载当前的位置信息
const loadingAreaModel = () => {
    driverModel.value.areaModel = locationStorage().location;
}
const search = async () => {
    console.log("searchBody.value=",searchBody.value);
    const result = await getDriverListServer(searchBody);
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
    driverModel.value = JSON.parse(JSON.stringify(row));
    loadingAreaModel();
    driverModel.value.sex = driverModel.value.sex + "";//转成字符串，否则表达加载不出来
    driverModel.value.status = driverModel.value.status + ""
    console.log(driverModel.value);
    title.value = "编辑"
    dialogVisible.value = true;
}

const cleanModel = () => {
    driverModel.value = {}
}

const onChange = (e) => {
    const three = chinaData[e[2]];
    driverModel.value.areaModel = JSON.parse(JSON.stringify(e));
    driverModel.value.area = three.label;
    driverModel.value.areaId = three.value;
}

const editAndAdd = () => {
    form.value.validate(async valid => {
        if (valid) {
            if (title.value == '新增') {
                await addServer(driverModel)
                ElMessage.success("新增成功!")
                dialogVisible.value = false;
                cleanModel();
                search()
            } else {
                await updateServer(driverModel);
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
        '你确认要删除该司机信息吗?',
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
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="用户编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.name" style="width: 200px;margin-left:10px" placeholder="用户姓名"
                        :prefix-icon="Search" />
                    
                    <el-select v-model="searchBody.status" style="width: 200px;margin-left: 10px;" placeholder="请选择状态" >
                        <el-option label="空闲" value="1" />
                        <el-option label="繁忙" value="2" />
                    </el-select>

                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
                <div>
                    <el-button type="primary" style="margin: 10px 0 10px 0;" @click="add()">新增</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="司机编号" />
                    <el-table-column prop="name" label="司机姓名" />
                    <el-table-column prop="phone" label="司机电话" />
                    <el-table-column prop="sex" label="性别">
                        <template #default="scope">
                            <el-tag :type="scope.row.sex == 1 ? 'success' : 'primary'" disable-transitions>
                                {{ scope.row.sex == 1 ? '男' : '女' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="当前状态">
                        <template #default="scope">
                            <el-tag :type="scope.row.status == 1 ? 'primary' :(scope.row.status == 2?'warning':'danger')" disable-transitions>
                                {{ scope.row.status == 1 ? '空闲' :( scope.row.status == 2?'繁忙':'禁职' )}}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="area" label="地区">
                        <template #default="scope">
                            <el-tag type="primary" disable-transitions>
                                {{ scope.row.area }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="注册时间" />
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
                <el-form :model="driverModel" :rules="rules" ref="form">
                    <el-form-item v-if="title == '新增'" label="编号" prop="id">
                        <el-input v-model="driverModel.id" />
                    </el-form-item>
                    <el-form-item v-else label="编号">
                        <el-input v-model="driverModel.id" disabled />
                    </el-form-item>

                    <el-form-item label="姓名" prop="name">
                        <el-input v-model="driverModel.name" />
                    </el-form-item>

                    <el-form-item label="电话" prop="phone">
                        <el-input type="number" v-model="driverModel.phone" />
                    </el-form-item>

                    <el-form-item label="性别" prop="sex">
                        <el-radio-group v-model="driverModel.sex">
                            <el-radio value=0>女</el-radio>
                            <el-radio value=1>男</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="driverModel.status">
                            <el-radio value=1>空闲</el-radio>
                            <el-radio value=2>繁忙</el-radio>
                            <el-radio value=3>禁用</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item v-if="title == '编辑'" label="地区" prop="areaModel">
                        <elui-china-area-dht placeholder="请选择" v-model="driverModel.areaModel"
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

