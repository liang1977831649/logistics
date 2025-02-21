<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getRefrigerateListServer, addServer, updateServer, deleteServer, detailRefrigerateServer } from "@/api/refrigerateApi.js"
import { ElMessage, ElMessageBox } from "element-plus"
import userInfoServer from "@/storage/userStorage"
import { getColdChainCenterList } from "@/api/ColdChainCenterApi.js"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    id: "",
    name: "",
    pageNum: 1,
    pageSize: 8
})
const dialogVisible = ref(false);
const tableData = ref([])
const title = ref('新增');
const refrigerateModel = ref({
    id: "",
    name: "",
    weight: null,
    volume: null,
    tem: null,
    hum: null,
    price: null,
    userId: "",
    goodsPic: "",
    status: "",
    rmName: "",
    userId: ""
})
const coldChainCenterList = ref([])
const rules = {
    id: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { pattern: /^rm.+$/, message: '账号以rm开头', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入冷链室名称', trigger: 'blur' },
    ],
    hum: [
        { required: true, message: '请输入湿度', trigger: 'blur' },
    ],
    tem: [
        { required: true, message: '请输入温度', trigger: 'blur' },
    ],
    maxVolume: [
        { required: true, message: '请输入体积', trigger: 'change' },
    ],
    curVolume: [
        { required: true, message: '请输入当前', trigger: 'blur' },
    ],
    cccId: [
        { required: true, message: '请输入当前', trigger: 'change' },
    ]
}
const form = ref(null);
const visibleDrawer = ref(false)
// =====================================方法区 =====================================
const search = async () => {
    const result = await getRefrigerateListServer(searchBody);
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

const addWindows = async () => {
    coldChainCenterList.value = (await getColdChainCenterList()).data
    cleanModel();
    dialogVisible.value = true;
}
const editButton = async () => {
    await updateServer(refrigerateModel);
    search()
    visibleDrawer.value = false;
    ElMessage.success("修改成功")
}

const cleanModel = () => {
    refrigerateModel.value = {}
}

const Add = () => {
    form.value.validate(async valid => {
        if (valid) {
            console.log("refrigerateModel.value=", refrigerateModel.value);
            await addServer(refrigerateModel)
            ElMessage.success("新增成功!")
            dialogVisible.value = false;
            cleanModel();
            search()
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
        '你确认要删除该冷链室信息吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            if (row.status != 1) {
                ElMessage.error("冷链室状态不在空闲中，不能删除")
                return
            } else if (row.status == 1) {
                await deleteServer(row.id);
                await search()
                ElMessage.success("删除成功")
            }
        })
        .catch(() => {
            ElMessage.info('用户取消了删除')
        })
}

const detail = async (row) => {
    if (form.value != null) {
        form.value.resetFields();
    }
    const result = await detailRefrigerateServer(row.id);
    refrigerateModel.value = result.data
    refrigerateModel.value.status=refrigerateModel.value.status+''
    visibleDrawer.value = true;
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.name" style="width: 200px;margin-left:10px" placeholder="名称"
                        :prefix-icon="Search" />
                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
                <div>
                    <el-button type="primary" style="margin: 10px 0 10px 0;" @click="addWindows()">新增</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="编号" />
                    <el-table-column prop="name" label="名称" />
                    <el-table-column prop="volume" label="容量">
                        <template #default="scope">
                            {{ scope.row.curVolume + "/" + scope.row.maxVolume+"m³" }}
                        </template>
                    </el-table-column>
                    <el-table-column label="温湿度">
                        <template #default="scope">
                            {{ scope.row.tem + "°C/" + scope.row.hum + "%" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="centreName" label="冷链中心">
                        <template #default="scope">
                            <el-tag type="primary">
                                {{ scope.row.centreName }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            {{ row.status == 1 ? '空闲' : '装载' }}
                        </template>
                    </el-table-column>

                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="success" size="small" @click="detail(row)">
                                详情
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
        <!-- 添加弹窗 -->
        <div>
            <el-dialog v-model="dialogVisible" :title="title" width="350px">
                <el-form :model="refrigerateModel" :rules="rules" ref="form">
                    <el-form-item label="编号" prop="id">
                        <el-input v-model="refrigerateModel.id" />
                    </el-form-item>

                    <el-form-item label="名称" prop="name">
                        <el-input v-model="refrigerateModel.name" />
                    </el-form-item>

                    <el-form-item label="最大体积" prop="maxVolume">
                        <el-input type="number" v-model="refrigerateModel.maxVolume" />
                    </el-form-item>

                    <el-form-item label="所属中心" prop="cccId">
                        <el-select placeholder="请选择" v-model="refrigerateModel.cccId">
                            <el-option v-for="c in coldChainCenterList" :value="c.id" :label="c.name">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>

                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="cancelButton()">取消</el-button>
                        <el-button type="primary" @click="Add()">
                            确定
                        </el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

        <!-- 右侧详情和编辑弹窗 -->
        <el-drawer v-model="visibleDrawer" title="冷链室详情" direction="rtl" size="35%">
            <el-form :model="refrigerateModel" :rules="rules" ref="form">
                <el-form-item label="编号" prop="id">
                    <el-input v-model="refrigerateModel.id" disabled />
                </el-form-item>

                <el-form-item label="名称" prop="name">
                    <el-input v-model="refrigerateModel.name" />
                </el-form-item>

                <el-form-item label="当前体积" prop="curVolume">
                    <el-input type="number" v-model="refrigerateModel.curVolume" />
                </el-form-item>

                <el-form-item label="体积" prop="maxVolume">
                    <el-input type="number" v-model="refrigerateModel.maxVolume" />
                </el-form-item>

                <el-form-item label="室内温度" prop="tem">
                    <el-input type="number" v-model="refrigerateModel.tem" />
                </el-form-item>

                <el-form-item label="室内湿度" prop="hum">
                    <el-input type="number" v-model="refrigerateModel.hum" />
                </el-form-item>

                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="refrigerateModel.status">
                        <el-radio value=1>空闲</el-radio>
                        <el-radio value=2>装载</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="所属冷链中心">
                    <el-input v-model="refrigerateModel.centreName" />
                </el-form-item>

                <el-form-item label="创建时间">
                    <el-input v-model="refrigerateModel.createTime" disabled />
                </el-form-item>
            </el-form>
            <el-button type="success" @click="editButton">保存</el-button>

        </el-drawer>
    </div>
</template>

<style scoped></style>

