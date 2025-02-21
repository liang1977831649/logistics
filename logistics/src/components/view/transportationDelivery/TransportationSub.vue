<script setup >
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getTransportationListServer, deleteServer, distributionServer } from "@/api/transportationApi.js"
import { ElMessage, ElMessageBox, ElNotification } from "element-plus"
import userInfoServer from "@/storage/userStorage"
import { detailServer } from "@/api/tsCar";
import TransportationTotalChoose from "@/components/view/TransportationTotalChoose.vue"
import refrigerateChoose from "@/components/view/RefrigerateChoose.vue"
import { inRoomServer } from "@/api/refrigerateApi"
import { detailGoodsServer } from "@/api/goodsApi"
import { detailUserServer } from "@/api/userApi"
import { detailCarServer } from "@/api/coldChainCarApi"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    id: "",
    userId: "",
    goodsId: "",
    pageNum: 1,
    pageSize: 8,
    status: '',
})
const tableData = ref([])
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

const visibleDrawer = ref(false)
const visibleDrawerTsCarDetail = ref(false)
const tsCarModel = ref({})
const dialogVisible = ref(false)
const title = ref('');
const ChildTsCar = ref(null)
const dialogVisibleInRoom = ref(false);
const rmGs = ref({});
const ChildRefrigerate = ref(null);
const dialogVisibleOfGoods = ref(false);
const goodsModel = ref({})
const dialogVisibleOfUser = ref(false);
const userModel = ref({});
const dialogVisibleOfCar = ref(false)
const carModel = ref({})
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

const detail = (row) => {
    transportationModel.value = row;
    visibleDrawer.value = true;
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
            ElMessage.info('用户取消了删除')
        })
}

const detailTsCar = async (carDriId) => {
    if (carDriId) {
        const result = await detailServer(carDriId);
        if (result.data) {
            tsCarModel.value = result.data
            visibleDrawerTsCarDetail.value = true;
        }else{
            ElMessage.error("该运输配送单已不存在");
        }
    } else {
        console.log("没有啊");
    }
}

// 点击装配，弹出的窗口
const distribution = (row) => {
    title.value = '装配'
    transportationModel.value = JSON.parse(JSON.stringify(row))
    dialogVisible.value = true;
}
//更改装配
const modifyDistribution = async (row) => {
    transportationModel.value = JSON.parse(JSON.stringify(row))
    title.value = '更改装配'
    dialogVisible.value = true;
}
// 货车司机组件传过来的值调用的函数，来接收
const receive = (e) => {
    if (e) {
        transportationModel.value.carDriId = e.id;
    }
}
// 装配时，点击取消
const cancelDistribution = () => {
    dialogVisible.value = false;
    ChildTsCar.value.search()
}
// 装配时，点击确定
const confirmDistribution = async () => {

    await distributionServer(transportationModel);
    ElNotification.success('装配成功');

    dialogVisible.value = false;
    await search()
    await ChildTsCar.value.search()
}

//入室操作
const inRoom = (row) => {
    rmGs.value.gdId = row.goodsId
    dialogVisibleInRoom.value = true;
}
//取消装配车辆按钮
const cleanDistributionButton = () => {
    transportationModel.value.carDriId = '';
    title.value = '更改配置'
    confirmDistribution()
}
//接收冷链库传来的值
const receiveInRoom = (e) => {
    if (e) {
        rmGs.value.rmId = e.id;
    }
}
//确认入库按钮
const confirmInRoom = () => {
    ElMessageBox.confirm(
        '你确认要入库吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            console.log("rmGs.value=", rmGs.value);
            await inRoomServer(rmGs);
            await search()
            dialogVisibleInRoom.value = false;
            ChildRefrigerate.value.search();
        })
        .catch(() => {
        })
}
const detailGoods = async (goodsId) => {
    const result = await detailGoodsServer(goodsId);
    goodsModel.value = result.data;
    console.log("goodsModel.value=", goodsModel.value);
    dialogVisibleOfGoods.value = true;
}
const detailUser = async (userId) => {
    const result = await detailUserServer(userId);
    userModel.value = result.data;
    console.log("userModel.value=", userModel.value);
    dialogVisibleOfUser.value = true;
}
const detailCar = async (carId) => {
    if (carId) {
        const result = await detailCarServer(carId);
        carModel.value = result.data;
        dialogVisibleOfCar.value = true;
    }
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 150px" placeholder="运输单号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.userId" style="width: 150px;margin-left:10px" placeholder="用户Id"
                        :prefix-icon="Search" />
                    <el-input v-model="searchBody.goodsId" style="width: 150px;margin-left:10px" placeholder="农产品编号"
                        :prefix-icon="Search" />
                    <el-select v-model="searchBody.status" style="width: 150px;margin-left:10px" placeholder="订单状态">
                        <el-option label="待发车" value="1" />
                        <el-option label="运输中" value="2" />
                        <el-option label="已入室" value="3" />
                    </el-select>
                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="编号" />
                    <el-table-column prop="goodsName" label="商品名称">
                        <template #default="{ row }">
                            <el-link type="primary" @click="detailGoods(row.goodsId)">{{ row.goodsName }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column prop="origin" label="运输起始地" />
                    <el-table-column prop="updateTime" label="更新时间" />
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            <el-tag :type="row.status == 1 ? 'success' : (row.status == 2 ? 'primary' : 'warning')">
                                {{ row.status == 1 ? '待发车' : (row.status == 2 ? '运输中' : '已入库') }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="carDriId" label="运输车单号">
                        <template #default="{ row }">
                            <el-link type="primary" @click="detailTsCar(row.carDriId)">{{ row.carDriId ? row.carDriId : '暂无'
                            }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="success" size="small" @click="detail(row)">
                                详情
                            </el-button>
                            <el-button v-if="row.status == 1 && !row.carDriId" type="warning" size="small"
                                @click="distribution(row)">
                                装配
                            </el-button>
                            <el-button v-if="row.status == 1 && row.carDriId" type="warning" size="small"
                                @click="modifyDistribution(row)">
                                更改装配
                            </el-button>
                            <el-button v-if="row.status == 2" type="warning" size="small" @click="inRoom(row)">
                                入库
                            </el-button>
                            <el-button v-if="row.status == 1" type="danger" size="small" @click="deleteButton(row)">
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

        <!-- 右侧详情弹窗 -->
        <el-drawer v-model="visibleDrawer" title="农产品详情" direction="rtl" size="35%">
            <el-form :model="transportationModel">
                <el-form-item label="运输ID">
                    <el-text tag="b">{{ transportationModel.id }}</el-text>
                </el-form-item>

                <el-form-item label="农产品ID">
                    <el-text tag="b">{{ transportationModel.goodsId }}</el-text>
                </el-form-item>

                <el-form-item label="农产品名">
                    <el-link type="primary" @click="detailGoods(transportationModel.goodsId)">{{
                        transportationModel.goodsName }}</el-link>
                </el-form-item>

                <el-form-item label="商家ID">
                    <el-text tag="b">{{ transportationModel.userId }}</el-text>
                </el-form-item>

                <el-form-item label="商家">
                    <el-link type="primary" @click="detailUser(transportationModel.userId)">{{ transportationModel.userName
                    }}</el-link>
                </el-form-item>

                <el-form-item label="始发地">
                    <el-text tag="b">{{ transportationModel.origin }}</el-text>
                </el-form-item>

                <el-form-item label="当前状态">
                    <el-text tag="b">{{ transportationModel.status == 1 ? '空闲' : (transportationModel.status == 2 ? '运输中' :
                        '已入库')
                    }}</el-text>
                </el-form-item>

                <el-form-item label="运输单号">
                    <el-text tag="b">{{ transportationModel.carDriId }}</el-text>
                </el-form-item>

                <el-form-item label="更新时间">
                    <el-input v-model="transportationModel.updateTime" disabled />
                </el-form-item>
            </el-form>
            <el-button type="primary" @click="visibleDrawer = false">我已知晓</el-button>
        </el-drawer>

        <!-- 运输司机与货车详情弹窗 -->
        <el-drawer v-model="visibleDrawerTsCarDetail" :title="title" direction="rtl" size="35%">
            <el-form :model="tsCarModel">
                <el-form-item label="运输单号Id">
                    <el-text tag="b">{{ tsCarModel.id }}</el-text>
                </el-form-item>
                <el-form-item label="冷链车辆Id">
                    <el-text tag="b">{{ tsCarModel.carId }}</el-text>
                </el-form-item>
                <el-form-item label="冷链车名称">
                    <el-link type="primary" @click="detailCar(tsCarModel.carId)">{{ tsCarModel.carName }}</el-link>
                </el-form-item>
                <el-form-item label="司机ID">
                    <el-text tag="b">{{ tsCarModel.driverId }}</el-text>
                </el-form-item>
                <el-form-item label="司机姓名">
                    <el-text tag="b">{{ tsCarModel.driverName }}</el-text>
                </el-form-item>
                <el-form-item label="司机电话">
                    <el-text tag="b">{{ tsCarModel.driverPhone }}</el-text>
                </el-form-item>
                <el-form-item label="订单状态">
                    <el-text tag="b">{{ tsCarModel.status == 1 ? '空闲' : '运输中' }}</el-text>
                </el-form-item>
                <el-form-item label="更新时间">
                    <el-input v-model="tsCarModel.updateTime" disabled />
                </el-form-item>
            </el-form>
            <el-button type="primary" @click="visibleDrawerTsCarDetail = false">我已知晓</el-button>
        </el-drawer>

        <!-- 装配和更改装配弹窗 -->
        <div>
            <el-dialog v-model="dialogVisible" title="装配运输单" width="1020px">
                <TransportationTotalChoose @event="receive" ref="ChildTsCar" />
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="danger" @click="cleanDistributionButton()">取消装配</el-button>
                        <el-button @click="cancelDistribution()">取消</el-button>
                        <el-button type="primary" @click="confirmDistribution()">确定</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

        <!-- 入库弹窗 -->
        <div>
            <el-dialog v-model="dialogVisibleInRoom" title="装配运输单" width="1020px">
                <refrigerateChoose @event="receiveInRoom" ref="ChildRefrigerate" />
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="dialogVisibleInRoom = false">取消</el-button>
                        <el-button type="primary" @click="confirmInRoom()">确定入库</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

        <!-- 农产品详情弹窗 -->
        <div>
            <el-dialog v-model="dialogVisibleOfGoods" title="农产品详情" width="320px">
                <el-form>
                    <el-form-item label="Id">
                        <el-text tag="b">{{ goodsModel.id }}</el-text>
                    </el-form-item>
                    <el-form-item label="名称">
                        <el-text tag="b">{{ goodsModel.name }}</el-text>
                    </el-form-item>
                    <el-form-item label="温度">
                        <el-text tag="b">{{ goodsModel.tem + "°C" }}</el-text>
                    </el-form-item>
                    <el-form-item label="湿度">
                        <el-text tag="b">{{ goodsModel.hum + "%" }}</el-text>
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-text tag="b">{{ goodsModel.status == 1 ? '空闲' : (goodsModel.status == 2 ? '运输中' :
                            (goodsModel.status == 3 ? '在库' : '出售')) }}</el-text>
                    </el-form-item>
                    <el-form-item label="冷藏室ID">
                        <el-text tag="b">{{ goodsModel.rmId ? goodsModel.rmId : '暂无' }}</el-text>
                    </el-form-item>
                    <el-form-item label="体积">
                        <el-text tag="b">{{ goodsModel.volume + "m³" }}</el-text>
                    </el-form-item>
                    <el-form-item label="重量">
                        <el-text tag="b">{{ goodsModel.weight + "kg" }}</el-text>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="primary" @click="dialogVisibleOfGoods = false">我已知晓</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

        <!-- 用户详情弹出 -->
        <div>
            <el-dialog v-model="dialogVisibleOfUser" title="用户详情" width="280px">
                <el-form>
                    <el-form-item label="Id">
                        <el-text tag="b">{{ userModel.id }}</el-text>
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-text tag="b">{{ userModel.name }}</el-text>
                    </el-form-item>
                    <el-form-item label="姓别">
                        <el-text tag="b">{{ userModel.sex == 1 ? '男' : '女' }}</el-text>
                    </el-form-item>
                    <el-form-item label="电话">
                        <el-text tag="b">{{ userModel.phone }}</el-text>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="primary" @click="dialogVisibleOfUser = false">我已知晓</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>
        <!-- 冷链货车详情 -->
        <div>
            <el-dialog v-model="dialogVisibleOfCar" title="农产品详情" width="280px">
                <el-form>
                    <el-form-item label="Id">
                        <el-text tag="b">{{ carModel.id }}</el-text>
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-text tag="b">{{ carModel.name }}</el-text>
                    </el-form-item>
                    <el-form-item label="体积">
                        <el-text tag="b">{{ carModel.curVolume + "/" + carModel.volume + "m³" }}</el-text>
                    </el-form-item>
                    <el-form-item label="载重">
                        <el-text tag="b">{{ carModel.curWeight + "/" + carModel.weight + "kg" }}</el-text>
                    </el-form-item>
                    <el-form-item label="温度/湿度">
                        <el-text tag="b">{{ carModel.tem + "°C/" + carModel.hum + "%" }}</el-text>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="primary" @click="dialogVisibleOfCar = false">我已知晓</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<style scoped></style>

