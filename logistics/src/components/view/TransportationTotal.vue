<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getTsCarList, addTsCarServer, editServer, deleteServer, departureServer } from "@/api/tsCar.js"
import { ElMessage, ElMessageBox, ElNotification } from "element-plus";
import ClodChainCarChoose from "@/components/view/ColdChainCarChoose.vue"
import DriverChoose from "@/components/view/DriverChoose.vue";
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const allNumber = ref(100)

const searchBody = ref({
    id: "",
    carId: "",
    driverName: "",
    driverId: "",
    status: "",
    pageNum: 1,
    pageSize: 8
})
const tableData = ref([])
const tsCarModel = ref({
    carId: '',
    driverId: '',
    carName: '',
    driverName: ''
})
const dialogVisible = ref(false)
const innerDialog = ref(false)
const carOrDriver = ref(true);//true表示选择车辆，false表示选择司机

const form = ref(null);
const rules = ref({
    carName: [
        { required: true, message: '请输入名称', trigger: 'blur' },
    ],
    driverName: [
        { required: true, message: '请输入名称', trigger: 'blur' },
    ]
})
const title = ref("");
const childCar = ref(null)
const childDri = ref(null)
// =====================================方法区 =====================================
const search = async () => {
    const result = await getTsCarList(searchBody);
    tableData.value = result.data.items;
    allNumber.value = result.data.total;
}
search()
const handleSizeChange = async (number) => {
    searchBody.value.pageSize = number;
    await search()
}
const handleCurrentChange = async (number) => {
    searchBody.value.pageNum = number
    await search()
}
const reset = () => {
    searchBody.value = {}
}

const clearTsCarModel = () => {
    tsCarModel.value = {};
}
//添加弹窗
const addWindows = () => {
    title.value = '添加'
    clearTsCarModel();
    dialogVisible.value = true;
}
const AddOrEdit = async () => {
    if (title.value == '添加') {
        await addTsCarServer(tsCarModel);
        ElNotification({
            title: '添加',
            message: '添加运输单号成功',
            type: 'success',
        })
    }
    else {
        console.log("tsCarModel.value=", tsCarModel.value);
        await editServer(tsCarModel);
        ElNotification({
            title: '修改',
            message: '修改运输单号成功',
            type: 'success',
        })
    }
    dialogVisible.value = false;
    await search();
    //重新刷新，子组件Dri和Car的信息
    await childCar.value.search()
    await childDri.value.search()
}
const departure = (row) => {
    tsCarModel.value = JSON.parse(JSON.stringify(row));
    ElMessageBox.confirm(
        '你确认要发车吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '算了',
            type: "warning",
        })
        .then(async () => {
            await departureServer(tsCarModel);
            await search()
            ElMessage.success("发车成功")
        })
        .catch(() => {
        })
}
const deleteButton = (row) => {
    ElMessageBox.confirm(
        '你确认要删除该运输单吗?',
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
        })
}
const receiveCar = (e) => {
    tsCarModel.value.carId = e.id;
    tsCarModel.value.carName = e.name
}
const receiveDri = (e) => {
    tsCarModel.value.driverId = e.id;
    tsCarModel.value.driverName = e.name
}
const innerDialogCancel = () => {
    if (carOrDriver.value == true) {
        tsCarModel.value.carId = ''
        tsCarModel.value.carName = ''
    } else {
        tsCarModel.value.driverId = ''
        tsCarModel.value.driverName = ''
    }
    innerDialog.value = false;
    childCar.value.search();
    childDri.value.search();
}
//更改，弹窗
const edit = (row) => {
    tsCarModel.value.id = row.id;
    tsCarModel.value.carId = row.carId;
    tsCarModel.value.carName = row.carName;
    tsCarModel.value.driverId = row.driverId;
    tsCarModel.value.driverName = row.driverName;
    title.value = '修改';
    dialogVisible.value = true;
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 150px" placeholder="编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.carId" style="width: 150px;margin-left:10px" placeholder="车辆Id"
                        :prefix-icon="Search" />
                    <el-input v-model="searchBody.driverId" style="width: 150px;margin-left:10px" placeholder="司机Id"
                        :prefix-icon="Search" />
                    <el-input v-model="searchBody.driverName" style="width: 150px;margin-left:10px" placeholder="司机"
                        :prefix-icon="Search" />
                    <el-select v-model="searchBody.status" style="width: 150px;margin-left:10px" placeholder="状态">
                        <el-option label="空闲" value="1" />
                        <el-option label="运输中" value="2" />
                        <el-option label="已结束" value="3" />
                    </el-select>
                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
                <div>
                    <el-button type="primary" style="margin: 10px 0 10px 0;" @click="addWindows()">新增运输单</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="编号" />
                    <el-table-column prop="carId" label="车辆Id" />
                    <el-table-column prop="carName" label="车辆" />
                    <el-table-column prop="driverId" label="司机Id" />
                    <el-table-column prop="driverName" label="司机" />
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            <el-tag :type="row.status == 1 ? 'success' : (row.status == 2 ? 'primary' : 'warning')">
                                {{ row.status == 1 ? '空闲' : (row.status == 2 ? '运输中' : '已结束') }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="updateTime" label="更新时间" />

                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button v-if="row.status == 1" type="primary" size="small" @click="departure(row)">
                                发车
                            </el-button>
                            <el-button v-if="row.status == 1" type="success" size="small" @click="edit(row)">
                                更改
                            </el-button>
                            <el-button v-if="row.status == 1" type="danger" size="small" @click="deleteButton(row)">
                                删除
                            </el-button>
                            <el-button v-else type="danger" size="small" @click="deleteButton(row)" disabled>
                                删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <template #footer>
                <div>
                    <!--分页插件-->
                    <el-pagination style="float: right;margin-top: 5px;margin-bottom: 10px;"
                        v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[8, 10, 20]"
                        :background="background" layout="prev, pager, next, jumper,total, sizes" :total="allNumber"
                        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                </div>
            </template>
        </el-card>
        <!-- 添加弹窗或者更改 -->
        <div>
            <el-dialog v-model="dialogVisible" :title="title" width="350px">
                <el-form :model="tsCarModel" :rules="rules" ref="form">
                    <el-form-item label="车辆" prop="carName">
                        <el-input v-model="tsCarModel.carName" @click="carOrDriver = true; innerDialog = true;"></el-input>
                    </el-form-item>

                    <el-form-item label="司机" prop="driverName">
                        <el-input v-model="tsCarModel.driverName"
                            @click="carOrDriver = false; innerDialog = true;"></el-input>
                    </el-form-item>

                    <el-dialog v-model="innerDialog" width="1000" append-to-body>
                        <ClodChainCarChoose v-if="carOrDriver" @event="receiveCar" ref="childCar"></ClodChainCarChoose>
                        <DriverChoose v-else @event="receiveDri" ref="childDri"></DriverChoose>
                        <template #footer>
                            <span class="dialog-footer">
                                <el-button @click="innerDialogCancel">取消</el-button>
                                <el-button type="primary" @click="innerDialog = false">确认</el-button>
                            </span>
                        </template>
                    </el-dialog>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="clearTsCarModel(); dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="AddOrEdit()">确定</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<style scoped></style>

