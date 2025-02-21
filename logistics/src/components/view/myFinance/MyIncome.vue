<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getGoodsCostListServer, updateGoodsCostServer } from "@/api/GoodsCostApi"
import { detailGoodsServer } from "@/api/goodsApi"
import { detailDeliveryServer } from "@/api/deliveryApi"
import { ElMessage, ElNotification, ElMessageBox } from "element-plus"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    salesName: '',
    goodsName: '',
    status: '',
    id: '',
    pageNum: 1,
    pageSize: 8
})
const tableData = ref([])
const goodsCostModel = ref({
    id: "",
    buyerId: '',
    salesId: '',
    deliId: '',
    cost: '',
    status: '',
    salesName: '',
    goodsName: '',
    updateTime: ''
})

const rules = {
    cost: [
        { required: true, message: '请输入价格', trigger: 'blur' },
    ]
}

const visibleDrawer = ref(false)
const userModel = ref({});
const dialogVisibleOfUser = ref(false);
const dialogVisibleOfGoods = ref(false);
const goodsModel = ref(false);
const dialogVisibleOfDelivery = ref(false);
const deliveryModel = ref({})
const dialogVisibleOfUpdateGoodsCostPrice = ref(false);
const formUpdatePrice = ref(null);
// =====================================方法区 =====================================
const search = async () => {
    searchBody.value.type = 2;
    const result = await getGoodsCostListServer(searchBody);
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

const detail = async (row) => {
    goodsCostModel.value = row;
    visibleDrawer.value = true;
}

const detailGoods = async (row) => {
    const result = await detailGoodsServer(row.goodsId);
    goodsModel.value = result.data;
    dialogVisibleOfGoods.value = true;
}
const detailDelivery = async (row) => {
    const result = await detailDeliveryServer(row.deliId);
    deliveryModel.value = result.data;
    dialogVisibleOfDelivery.value = true
}
const changeGoodsCostPriceWindows = async (row) => {
    goodsCostModel.value = {};
    goodsCostModel.value.id = row.id;
    goodsCostModel.value.cost = row.cost;
    goodsCostModel.value.salesId=row.salesId;
    dialogVisibleOfUpdateGoodsCostPrice.value = true;
}
const confirmGoodsCostPrice = async () => {
    formUpdatePrice.value.validate(async valid => {
        if (valid) {
            ElMessageBox.confirm(
                '你确认要修改吗?',
                '温馨提示',
                {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: "warning",
                })
                .then(async () => {
                    await updateGoodsCostServer(goodsCostModel);
                    dialogVisibleOfUpdateGoodsCostPrice.value = false;
                    await search()
                    ElNotification.success('改价成功！');
                })
                .catch(() => {
                })
        } else {
            ElMessage.error('数据错误');
        }
    })
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="订单单号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.goodsName" style="width: 200px;margin-left:10px" placeholder="商品名称"
                        :prefix-icon="Search" />
                    <el-select v-model="searchBody.status" style="width: 200px;margin-left: 10px;" placeholder="请选择状态">
                        <el-option label="未支付" value="1" />
                        <el-option label="已支付" value="2" />
                    </el-select>
                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="订单编号" />
                    <el-table-column prop="userName" label="买家" />
                    <el-table-column prop="goodsName" label="商品">
                        <template #default="{ row }">
                            <el-link type="primary" @click="detailGoods(row)">{{ row.goodsName }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column prop="deliId" label="配送单号">
                        <template #default="{ row }">
                            <el-link type="primary" @click="detailDelivery(row)">{{ row.deliId }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column prop="cost" label="费用">
                        <template #default="{ row }">
                            <el-text>{{ row.cost + "元" }}</el-text>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            <el-tag :type="row.status == 1 ? 'danger' : 'success'">
                                {{ row.status == 1 ? '对方未支付' : '对方已支付' }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="info" size="small" @click="detail(row)">
                                详情
                            </el-button>
                            <el-button v-if="row.status == 1" type="danger" size="small"
                                @click="changeGoodsCostPriceWindows(row)">
                                改价
                            </el-button>
                            <el-button v-else disabled type="danger" size="small">
                                改价
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


        <!-- 右侧详情弹窗 -->
        <el-drawer v-model="visibleDrawer" title="农产品详情" direction="rtl" size="30%">
            <el-form :model="goodsCostModel">
                <el-form-item label="编号">
                    <el-text tag="b">{{ goodsCostModel.id }}</el-text>
                </el-form-item>
                <el-form-item label="商品名称">
                    <el-text tag="b">{{ goodsCostModel.salesName }}</el-text>
                </el-form-item>
                <el-form-item label="商品名称">
                    <el-text tag="b">{{ goodsCostModel.goodsName }}</el-text>
                </el-form-item>
                <el-form-item label="配送单号">
                    <el-link type="primary" @click="detailDelivery(goodsCostModel.deliId)">{{ goodsCostModel.deliId }}</el-link>
                </el-form-item>
                <el-form-item label="费用">
                    <el-text tag="b">{{ goodsCostModel.cost + "元" }}</el-text>
                </el-form-item>
                <el-form-item label="状态">
                    <el-text tag="b">{{ goodsCostModel.status == 1 ? '对方未支付' : '对方已支付' }}</el-text>
                </el-form-item>
                <el-form-item label="最新时间">
                    <el-text tag="b">{{ goodsCostModel.updateTime }}</el-text>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="visibleDrawer = false">我已知晓</el-button>
                </div>
            </template>
        </el-drawer>

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
                    <el-form-item label="电话">
                        <el-button type="primary" @click="dialogVisibleOfUser = false">我已知晓</el-button>
                    </el-form-item>
                </el-form>
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

        <!-- 配送单详情 -->
        <div>
            <el-dialog v-model="dialogVisibleOfDelivery" title="农产品详情" width="320px">
                <el-form>
                    <el-form-item label="Id">
                        <el-text tag="b">{{ deliveryModel.id }}</el-text>
                    </el-form-item>

                    <el-form-item label="地址">
                        <el-text tag="b">{{ deliveryModel.destination }}</el-text>
                    </el-form-item>

                    <el-form-item label="状态">
                        <el-text tag="b">{{ deliveryModel.status == 1 ? '待发车' : (deliveryModel.status == 2 ? '运输中' :
                            (deliveryModel.status == 3 ? '已送达' : '已收货')) }}</el-text>
                    </el-form-item>

                    <el-form-item label="购买体积/数量">
                        <el-text tag="b">{{ deliveryModel.volume + "m³/" + deliveryModel.weight + "KG" }}</el-text>
                    </el-form-item>

                    <el-form-item label="发车时间">
                        <el-text tag="b">{{ deliveryModel.departTime }}</el-text>
                    </el-form-item>

                    <el-form-item label="更新时间">
                        <el-text tag="b">{{ deliveryModel.updateTime }}</el-text>
                    </el-form-item>

                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="primary" @click="dialogVisibleOfDelivery = false">我已知晓</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

        <!-- 修改价格窗口 -->
        <div>
            <el-dialog v-model="dialogVisibleOfUpdateGoodsCostPrice" title="修改车辆温湿度" width="300px">
                <el-form :model="goodsCostModel" :rules="rules" ref="formUpdatePrice">
                    <el-form-item label="价格" prop="cost">
                        <el-input type="number" v-model="goodsCostModel.cost" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click=" dialogVisibleOfUpdateGoodsCostPrice = false">取消</el-button>
                        <el-button type="primary" @click="confirmGoodsCostPrice()">确定</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<style scoped></style>

