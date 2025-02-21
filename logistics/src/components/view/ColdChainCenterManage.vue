<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from "element-plus"
import { getColdChainCenterListAdmin,addColdChainCenterServer,updateColdChainCenterServer,deleteColdChainCenterServer } from "@/api/ColdChainCenterApi.js"
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
const coldChainCenterModel = ref({
    id: "",
    name: "",
    areaId: null,
    location: null
})
const rules = {
    id: [
        { required: true, message: '请输入编号', trigger: 'blur' },
        { pattern: /^ccc.+$/, message: '账号以ccc开头', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入冷链中心名称', trigger: 'blur' },
    ],
    location: [
        { required: true, message: '请输入冷链中心位置', trigger: 'blur' },
    ],
    
}
const form = ref(null);
const visibleDrawer = ref(false)
// =====================================方法区 =====================================
const search = async () => {
    const result = await getColdChainCenterListAdmin(searchBody);
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

const editButton = async () => {
    await updateColdChainCenterServer(coldChainCenterModel);
    search()
    visibleDrawer.value = false;
    ElMessage.success("修改成功")
}

const cleanModel = () => {
    coldChainCenterModel.value = {}
}

const Add = () => {
    console.log("coldChainCenterModel.value=", coldChainCenterModel.value);
    form.value.validate(async valid => {
        if (valid) {
            await addColdChainCenterServer(coldChainCenterModel)
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
        '你确认要删除该冷链中心信息吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            await deleteColdChainCenterServer(row.id);
            await search()
            ElMessage.success("删除成功")
        })
        .catch(() => {
            ElMessage.info('用户取消了删除')
        })
}

const detail = async (row) => {
    coldChainCenterModel.value = JSON.parse(JSON.stringify(row))
    visibleDrawer.value = true;
}
const addWindows=()=>{
    coldChainCenterModel.value={};
    dialogVisible.value=true
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
                    <el-table-column prop="location" label="地理位置"/>
                     
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
                <el-form :model="coldChainCenterModel" :rules="rules" ref="form">
                    <el-form-item label="编号" prop="id">
                        <el-input v-model="coldChainCenterModel.id" />
                    </el-form-item>
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="coldChainCenterModel.name" />
                    </el-form-item>
                    <el-form-item label="所属位置" prop="location">
                        <el-input v-model="coldChainCenterModel.location" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="cancelButton()">取消</el-button>
                        <el-button type="primary" @click="Add">
                            确定
                        </el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

        <!-- 右侧详情和编辑弹窗 -->
        <el-drawer v-model="visibleDrawer" title="冷链室详情" direction="rtl" size="35%">
            <el-form :model="coldChainCenterModel" :rules="rules" ref="form">
                <el-form-item label="编号" prop="id">
                    <el-input v-model="coldChainCenterModel.id" disabled />
                </el-form-item>

                <el-form-item label="名称" prop="name">
                    <el-input v-model="coldChainCenterModel.name" />
                </el-form-item>

                <el-form-item label="名称" prop="location">
                    <el-input v-model="coldChainCenterModel.location" />
                </el-form-item>
            </el-form>
            <el-button type="success" @click="editButton">保存</el-button>

        </el-drawer>
    </div>
</template>

<style scoped></style>

