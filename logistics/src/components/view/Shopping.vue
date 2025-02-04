<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getGoodsListServerShopping } from "@/api/goodsApi.js"
import { detailUserServer } from "@/api/userApi"
import { ElMessage, ElMessageBox, ElNotification } from "element-plus";
import { addDeliveryServer } from "@/api/deliveryApi";
// =====================================数据区 =====================================
const tempUrl = ref("https://images.wondershare.com/repairit/aticle/2021/07/resolve-images-not-showing-problem-1.jpg")
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    id: "",
    name: "",
    pageNum: 1,
    pageSize: 8,
    status: 4
})
const tableData = ref([])
const goodsModel = ref({
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
    userId: "",
    rmId: ''
})
const deliveryModel = ref({
    goodsId: '',
    goodsName: '',
    weight: null,
    destination: ''
})
const rules = {
    weight: [
        { required: true, message: '请输入购买量', trigger: 'blur' },
        { type: 'number', min: 200, message: '最少容量是200Kg', trigger: 'blur' }
    ],
    destination: [
        { required: true, message: '请输入收货地址', trigger: 'blur' },
    ]
}
const visibleDrawer = ref(false)
const userModel = ref({});
const dialogVisibleOfUser = ref(false)
const dialogVisibleOfBuying = ref(false);
const formDeli = ref(null);
// =====================================方法区 =====================================
const search = async () => {
    searchBody.value.status = 4;
    const result = await getGoodsListServerShopping(searchBody);
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
    console.log("row=", row);
    goodsModel.value = row;
    visibleDrawer.value = true;
}

const detailUser = async (userId) => {
    const result = await detailUserServer(userId);
    userModel.value = result.data;
    dialogVisibleOfUser.value = true;
}
const buyWindows = (row) => {
    if (formDeli.value) {
        formDeli.value.resetFields()
    }
    goodsModel.value.weight = parseInt(row.weight)
    deliveryModel.value.goodsId = row.id;
    deliveryModel.value.goodsName = row.name
    dialogVisibleOfBuying.value = true
}
const confirmBuying = () => {
    if (deliveryModel.value.weight > goodsModel.value.weight) {
        ElMessage.error("超出购买范围");
        return
    }
    formDeli.value.validate(async valid => {
        if (valid) {
            ElMessageBox.confirm(
                '你确认要购买吗?',
                '温馨提示',
                {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: "warning",
                })
                .then(async () => {
                    console.log("deliveryModel.value=", deliveryModel.value);
                    await addDeliveryServer(deliveryModel)
                    dialogVisibleOfBuying.value = false;
                    ElNotification.success('购买成功！')
                    await search()
                })
                .catch(() => {
                    ElMessage.info('取消购买')
                })
        } else {
            ElMessage.error('数据错误');
        }
    })
}
const cancelBuying = () => {
    dialogVisibleOfBuying.value = false
    deliveryModel.value = {}
    
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="商品编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.name" style="width: 200px;margin-left:10px" placeholder="商品名称"
                        :prefix-icon="Search" />

                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="编号" />
                    <el-table-column prop="name" label="商品名" />
                    <el-table-column prop="price" label="价格">
                        <template #default="{ row }">
                            <el-text tag="b">{{ row.price + "元/kg" }}</el-text>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            <el-tag
                                :type="row.status == 1 ? 'primary' : (row.status==2?'success':(row.status==3?'warning':(row.status==4?'danger':'info')))">
                                {{ row.status == 1 ? '空闲' : (row.status == 2 ? '运输中' : (row.status == 3 ? '在库' : (row.status == 4 ? '出售' :'售罄' ) )) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="weight" label="现货库存">
                        <template #default="scope">
                            <el-text tag="b">
                                {{ scope.row.weight + "KG" }}
                            </el-text>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userName" label="商户" />
                    <el-table-column prop="user_pic" label="图片">
                        <template #default="{ row }">
                            <el-image :src="row.goodsPic ? row.goodsPic : tempUrl" style="width: 60px;" />
                        </template>
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="success" size="small" @click="detail(row)">
                                详情
                            </el-button>
                            <el-button type="danger" v-if="row.status==4" size="small" @click="buyWindows(row)">
                                购买
                            </el-button>
                            <el-button type="danger" v-if="row.status==5" disabled size="small"  >
                                购买
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
            <el-form :model="goodsModel">
                <el-form-item label="编号">
                    <el-text tag="b">{{ goodsModel.id }}</el-text>
                </el-form-item>

                <el-form-item label="名称">
                    <el-text tag="b">{{ goodsModel.name }}</el-text>
                </el-form-item>

                <el-form-item label="现有重量">
                    <el-text tag="b">{{ goodsModel.weight + "KG" }}</el-text>
                </el-form-item>

                <el-form-item label="商家">
                    <el-link type="primary" @click="detailUser(goodsModel.userId)">{{ goodsModel.userName }}</el-link>
                </el-form-item>

                <el-form-item label="舱室">
                    <el-text tag="b">{{ goodsModel.rmName }}</el-text>
                </el-form-item>

                <el-form-item label="对外售价">
                    <el-text tag="b">{{ goodsModel.price + "元/KG" }}</el-text>
                </el-form-item>

                <el-form-item label="图片">
                    <img :src="goodsModel.goodsPic ? goodsModel.goodsPic : tempUrl" width="300px" />
                </el-form-item>
            </el-form>
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
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="primary" @click="dialogVisibleOfUser = false">我已知晓</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

        <!-- 购买农产品弹窗-->
        <div>
            <el-dialog v-model="dialogVisibleOfBuying" title="商品详情" width="340px">
                <el-form :model="deliveryModel" :rules="rules" ref="formDeli">
                    <el-form-item label="农产品ID">
                        <el-text tag="b">{{ deliveryModel.goodsId }}</el-text>
                    </el-form-item>
                    <el-form-item label="农产品名称">
                        <el-text tag="b">{{ deliveryModel.goodsName }}</el-text>
                    </el-form-item>
                    <el-form-item label="购买重量" prop="weight">
                        <el-input type="number" v-model.number="deliveryModel.weight" />
                    </el-form-item>
                    <el-form-item label="配送地址" prop="destination">
                        <el-input v-model="deliveryModel.destination" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="primary" @click="confirmBuying">确认购买</el-button>
                        <el-button type="info" @click="cancelBuying">取消</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>

    </div>
</template>

<style scoped></style>

