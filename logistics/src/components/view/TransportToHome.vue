<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getTransportationListServer, addServer, deleteServer } from "@/api/transportationApi.js"
import { ElMessage, ElMessageBox } from "element-plus"
import userInfoServer from "@/storage/userStorage"
import MyGoodsChoose from "./MyGoodsChoose.vue";
import { detailServer } from "@/api/tsCar"
import { detailCarServer } from "@/api/coldChainCarApi"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    id: "",
    userId: "",
    goodsName: "",
    pageNum: 1,
    pageSize: 8
})
const dialogVisible = ref(false);
const tableData = ref([])
const title = ref('新增');
const transportationModel = ref({
    id: '',
    userId: '',
    userName: '',
    goodsId: '',
    goodsName: '',
    origin: '',
    status: '',
    carDriId: '',
    updateTime: ''
})

const tsCarModel = ref({
    id: '',
    carId: '',
    carName: '',
    driverId: '',
    driverName: '',
    driverPhone: '',
    status: '',
    updateTime: ''
})
const rules = {
    goodsId: [
        { required: true, message: '请输入冷链室名称', trigger: 'blur' },
    ],
    goodsName: [
        { required: true, message: '请选择农产品', trigger: 'blur' },
    ],
    origin: [
        { required: true, message: '请输入始发地', trigger: 'blur' },
    ]
}
const form = ref(null);
const innerDialog = ref(false)
const visibleDrawer = ref(false)
const dialogVisibleOfCar = ref(false);
const carModel = ref({})
const chidGoodsChoose = ref(null);
// =====================================方法区 =====================================
const search = async () => {
    if (userInfoServer().userInfo.role == 1) {
        //如果是普通用户，则需要携带上user_id
        searchBody.value.userId = userInfoServer().userInfo.id;
    }
    const result = await getTransportationListServer(searchBody);
    tableData.value = result.data.items;
    allNumber.value = result.data.total;
}
search();
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
const addWindows = async () => {
    cleanModel();
    dialogVisible.value = true;
}

const cleanModel = () => {
    transportationModel.value = {}
}

const Add = () => {
    form.value.validate(async valid => {
        if (valid) {
            console.log("transportationModel.value=", transportationModel.value);
            await addServer(transportationModel)
            ElMessage.success("添加运输订单成功!")
            dialogVisible.value = false;
            cleanModel();
            search();
            chidGoodsChoose.value.search()
        } else {
            ElMessage.error('数据错误');
        }
    })
}
const cancelButton = () => {
    dialogVisible.value = false;
    form.value.resetFields();
    cleanModel();
    chidGoodsChoose.value.search()
}

const deleteButton = async (row) => {
    ElMessageBox.confirm(
        '你确认要删除该运输单吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            if (row.status != 1) {
                ElMessage.error("该订单状态在运输或在仓库中，不可删除")
                return
            } else if (row.status == 1) {
                await deleteServer(row.id);
                await search()
                ElMessage.success("删除成功")
            }
        })
        .catch(() => {

        })
}
const receive = (e) => {
    if (e) {
        transportationModel.value.goodsId = e.id;
        transportationModel.value.goodsName = e.name
    }
}

const innerDialogCancel = () => {
    innerDialog.value = false;
    transportationModel.value.goodsId = null
    transportationModel.value.goodsName = null
    chidGoodsChoose.value.search()
}
const detailCarDri = async (carDriId) => {
    if (carDriId != null) {
        const result = await detailServer(carDriId);
        tsCarModel.value = result.data
        visibleDrawer.value = true;
    } else {
        console.log("没有啊");
    }
}

const detailCarModel = async () => {
    const result = await detailCarServer(tsCarModel.value.carId);
    carModel.value = result.data;
    dialogVisibleOfCar.value = true
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.goodsName" style="width: 200px;margin-left:10px" placeholder="农产品名"
                        :prefix-icon="Search" />
                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
                <div>
                    <el-button type="primary" style="margin: 10px 0 10px 0;" @click="addWindows()">我要运输</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="编号" />
                    <el-table-column prop="goodsName" label="商品名称" />
                    <el-table-column prop="origin" label="运输起始地" />
                    <el-table-column prop="updateTime" label="更新时间" />
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            <el-tag :type="row.status == 1 ? 'success' : (row.status == 2 ? 'primary' : 'warning')">
                                {{ row.status == 1 ? '空闲' : (row.status == 2 ? '运输中' : '已入库') }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="carDriId" label="运输车单号">
                        <template #default="{ row }">
                            <el-button size="small" type="success" @click="detailCarDri(row.carDriId)">
                                {{ row.carDriId ? row.carDriId : '暂无' }}
                            </el-button>
                        </template>
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button v-if="row.status == 1" type="danger" size="small" @click="deleteButton(row)">
                                删除
                            </el-button>
                            <el-button v-else type="danger" size="small" disabled>
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
        <!-- 添加弹窗 -->
        <div>
            <el-dialog v-model="dialogVisible" :title="title" width="350px">
                <el-form :model="transportationModel" :rules="rules" ref="form">
                    <el-form-item label="农产品" prop="goodsName">
                        <el-input v-model="transportationModel.goodsName" @click="innerDialog = true" />
                    </el-form-item>

                    <el-dialog v-model="innerDialog" width="1000" title="请选择你的农产品" append-to-body>
                        <MyGoodsChoose @event="receive" ref="chidGoodsChoose"></MyGoodsChoose>
                        <template #footer>
                            <span class="dialog-footer">
                                <el-button @click="innerDialogCancel">取消</el-button>
                                <el-button type="primary" @click="innerDialog = false">确认</el-button>
                            </span>
                        </template>
                    </el-dialog>
                    <el-form-item label="起始地" prop="origin">
                        <el-input v-model="transportationModel.origin" />
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

        <!-- 运输司机与货车弹窗 -->
        <el-drawer v-model="visibleDrawer" title="运输详情" direction="rtl" size="35%">
            <el-form :model="tsCarModel">
                <el-form-item label="运输单号Id">
                    <el-input v-model="tsCarModel.id" disabled />
                </el-form-item>
                <el-form-item label="冷链车名称">
                    <el-link type="primary" @click="detailCarModel">{{ tsCarModel.carName }}</el-link>
                </el-form-item>
                <el-form-item label="司机姓名">
                    <el-link type="primary">{{ tsCarModel.driverName }}</el-link>
                </el-form-item>
                <el-form-item label="司机电话">
                    <el-text tag="b">{{ tsCarModel.driverPhone }}</el-text>
                </el-form-item>
                <el-form-item label="订单状态">
                    <el-text tag="b">{{ tsCarModel.status == 1 ? '空闲' : (tsCarModel.status == 2 ? '运输中' : '已入库')
                    }}</el-text>
                </el-form-item>
                <el-form-item label="更新时间">
                    <el-text tag="b">{{ tsCarModel.updateTime }}</el-text>
                </el-form-item>
            </el-form>
            <el-button type="primary" @click="visibleDrawer = false">我已知晓</el-button>
        </el-drawer>

        <!-- 货车详情窗口 -->
        <el-dialog v-model="dialogVisibleOfCar" title="冷链车详细信息" width="350px">
            <el-form>
                <el-form-item label="车名:">
                    <el-text tag="b">{{ carModel.name }}</el-text>
                </el-form-item>
                <el-form-item label="车辆状态:">
                    <el-text tag="b">{{ carModel.status == 1 ? '空闲' : (carModel.status == 2 ? '待发车' : '运输中') }}</el-text>
                </el-form-item>
                <el-form-item label="温度:">
                    <el-text tag="b">{{ carModel.tem + "℃" }}</el-text>
                </el-form-item>
                <el-form-item label="湿度:">
                    <el-text tag="b">{{ carModel.hum + "%" }}</el-text>
                </el-form-item>
                <el-form-item label="注册时间:">
                    <el-text tag="b">{{ carModel.createTime }}</el-text>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="dialogVisibleOfCar = false">
                        确定
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped></style>

