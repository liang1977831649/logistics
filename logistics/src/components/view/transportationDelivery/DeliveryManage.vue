<script setup >
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, ElNotification } from "element-plus"
import { detailServer } from "@/api/tsCar";
import TransportationTotalChoose from "@/components/view/TransportationTotalChoose.vue"
import { detailGoodsServer } from "@/api/goodsApi"
import { detailUserServer } from "@/api/userApi"
import { detailCarServer } from "@/api/coldChainCarApi"
import { getDeliverList, changeLoadingDelivery, deleteDeliveryServer, arrivalServer } from "@/api/deliveryApi"

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
    pageSize: 8,
    status: null,
})
const tableData = ref([])
const deliveryModel = ref({
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
const dialogVisibleOfGoods = ref(false);
const goodsModel = ref({})
const dialogVisibleOfUser = ref(false);
const userModel = ref({});
const dialogVisibleOfCar = ref(false)
const carModel = ref({})
// =====================================方法区 =====================================
const search = async () => {
    const result = await getDeliverList(searchBody);
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
    deliveryModel.value = JSON.parse(JSON.stringify(row));
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
                ElMessage.error("该订单状态在运输或在已送达，不可删除")
                return
            } else if (row.status == 1) {
                await deleteDeliveryServer(row.id);
                await search()
                ElMessage.success("删除成功")
            }
        })
        .catch(() => {
            ElMessage.info('用户取消了删除')
        })
}

const detailTsCar = async (carDriId) => {
    if (carDriId != null) {
        const result = await detailServer(carDriId);
        tsCarModel.value = result.data
        visibleDrawerTsCarDetail.value = true;
    } else {
        console.log("没有啊");
    }
}

// 点击装车，弹出的窗口
const distribution = (row) => {
    title.value = '装车'
    deliveryModel.value = JSON.parse(JSON.stringify(row))
    dialogVisible.value = true;
}
//更改装车
const modifyDistribution = async (row) => {
    deliveryModel.value = JSON.parse(JSON.stringify(row))
    title.value = '更改装车'
    dialogVisible.value = true;
}
// 货车司机组件传过来的值调用的函数，来接收
const receive = (e) => {
    if (e) {
        deliveryModel.value.carDriId = e.id;
    }
}
// 装车时，点击取消
const cancelDistribution = () => {
    dialogVisible.value = false;
}
// 装车时，点击确定
const confirmDistribution = async () => {
    console.log("deliveryModel.value=", deliveryModel.value);
    if (title.value == '装车') {
        if (deliveryModel.value.carDriId == '') {
            ElMessage.error("装车的总订单号不能为空");
            return;
        }
        await changeLoadingDelivery(deliveryModel);
        ElNotification.success('装车成功');
    } else {
        await changeLoadingDelivery(deliveryModel);
        ElNotification.success("更改装车成功")
    }
    dialogVisible.value = false;
    await search()
    await ChildTsCar.value.search()
}

//取消装车车辆按钮
const cleanDistributionButton = () => {
    deliveryModel.value.carDriId = '';
    title.value = '更改'
    confirmDistribution()
}

const detailGoods = async (row) => {
    const result = await detailGoodsServer(row.goodsId);
    goodsModel.value = result.data;
    goodsModel.value.buyVolume = row.volume;
    goodsModel.value.buyWeight = row.weight
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
const arrival = async (row) => {
    ElMessageBox.confirm(
        '你确认该产品已抵达吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            if (row.status != 2) {
                ElMessage.error("该订单状态在待发车中，不可抵达")
                return
            } else if (row.status == 2) {
                await arrivalServer(row.id);
                await search()
                ElMessage.success("成功抵达")
            }
        })
        .catch(() => {
            ElMessage.info('用户取消')
        })
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 150px" placeholder="运输单号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.userId" style="width: 150px;margin-left:10px" placeholder="买家Id"
                        :prefix-icon="Search" />
                    <el-input v-model="searchBody.goodsName" style="width: 150px;margin-left:10px" placeholder="农产品编号"
                        :prefix-icon="Search" />
                    <el-select v-model="searchBody.status" style="width: 150px;margin-left:10px" placeholder="订单状态">
                        <el-option label="待发车" value="1" />
                        <el-option label="运输中" value="2" />
                        <el-option label="已送达" value="3" />
                        <el-option label="已收获" value="4" />
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
                            <el-link type="primary" @click="detailGoods(row)">{{ row.goodsName }}</el-link>
                        </template>
                    </el-table-column>

                    <el-table-column prop="userName" label="买家姓名">
                        <template #default="{ row }">
                            <el-link type="primary" @click="detailUser(row.userId)">{{ row.userName }}</el-link>
                        </template>
                    </el-table-column>

                    <el-table-column prop="destination" label="配送目的地" />
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            <el-tag
                                :type="row.status == 1 ? 'success' : (row.status == 2 ? 'primary' : (row.status == 3 ? 'warning' : 'danger'))">
                                {{ row.status == 1 ? '待发车' : (row.status == 2 ? '运输中' : (row.status == 3 ? '已送达' : '已收货'))
                                }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="carDriId" label="运输车单号">
                        <template #default="{ row }">
                            <el-link type="primary" @click="detailTsCar(row.carDriId)">{{ row.carDriId ? row.carDriId : '暂无'
                            }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column prop="updateTime" label="更新时间" />
                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" size="small" @click="detail(row)">
                                详情
                            </el-button>
                            <el-button v-if="row.status == 1 && !row.carDriId" type="warning" size="small"
                                @click="distribution(row)">
                                配车
                            </el-button>
                            <el-button v-if="row.status == 1 && row.carDriId" type="warning" size="small"
                                @click="modifyDistribution(row)">
                                更改配车
                            </el-button>
                            <el-button v-if="row.status == 2" type="warning" size="small" @click="arrival(row)">
                                抵达
                            </el-button>
                            <el-button v-if="row.status == 3" type="info" size="small" disabled>
                                未收货
                            </el-button>
                            <el-button v-if="row.status == 4" type="success" size="small" disabled>
                                已收货
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

        <!-- 右侧详情弹窗 -->
        <el-drawer v-model="visibleDrawer" title="农产品详情" direction="rtl" size="35%">
            <el-form :model="deliveryModel">
                <el-form-item label="配送ID">
                    <el-text tag="b">{{ deliveryModel.id }}</el-text>
                </el-form-item>

                <el-form-item label="农产品ID">
                    <el-text tag="b">{{ deliveryModel.goodsId }}</el-text>
                </el-form-item>

                <el-form-item label="农产品名">
                    <el-link type="primary" @click="detailGoods(deliveryModel)">{{
                        deliveryModel.goodsName }}</el-link>
                </el-form-item>

                <el-form-item label="买家ID">
                    <el-text tag="b">{{ deliveryModel.userId }}</el-text>
                </el-form-item>

                <el-form-item label="买家">
                    <el-link type="primary" @click="detailUser(deliveryModel.userId)">{{ deliveryModel.userName
                    }}</el-link>
                </el-form-item>

                <el-form-item label="目的地">
                    <el-text tag="b">{{ deliveryModel.destination }}</el-text>
                </el-form-item>

                <el-form-item label="当前状态">
                    <el-text tag="b">{{ deliveryModel.status == 1 ? '空闲' : (deliveryModel.status == 2 ? '运输中' :
                        '已入库')
                    }}</el-text>
                </el-form-item>

                <el-form-item label="配送单号">
                    <el-text tag="b">{{ deliveryModel.carDriId != null ? deliveryModel.carDriId : '暂无' }}</el-text>
                </el-form-item>

                <el-form-item label="更新时间">
                    <el-input v-model="deliveryModel.updateTime" disabled />
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

        <!-- 装车和更改装车弹窗 -->
        <div>
            <el-dialog v-model="dialogVisible" title="装车运输单" width="1020px">
                <TransportationTotalChoose @event="receive" ref="ChildTsCar" />
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="danger" @click="cleanDistributionButton()">取消装车</el-button>
                        <el-button @click="cancelDistribution()">取消</el-button>
                        <el-button type="primary" @click="confirmDistribution()">确定</el-button>
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

                    <el-form-item label="温度/湿度">
                        <el-text tag="b">{{ goodsModel.tem + "°C/" + goodsModel.hum + "%" }}</el-text>
                    </el-form-item>

                    <el-form-item label="购买量/体积">
                        <el-text tag="b">{{ goodsModel.buyWeight + "KG/" + goodsModel.buyVolume + "m³" }}</el-text>
                    </el-form-item>

                    <el-form-item label="商家编号/姓名">
                        <el-text tag="b">{{ goodsModel.userId + "/" + goodsModel.userName }}</el-text>
                    </el-form-item>

                    <el-form-item label="库存剩余">
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

