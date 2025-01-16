<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getGoodsListServer, updateServer } from "@/api/goodsApi.js"
import { ElMessage } from "element-plus"
import RefrigerateChoose from "./RefrigerateChoose.vue";
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
})
const dialogVisible = ref(false);
const tableData = ref([])
const title = ref('');
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

const rules = {
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
    ],
    hum: [
        { required: true, message: '请输入湿度', trigger: 'blur' },
    ],
    tem: [
        { required: true, message: '请输入温度', trigger: 'blur' },
    ],
    weight: [
        { required: true, message: '请输入重量', trigger: 'blur' },
    ],
    volume: [
        { required: true, message: '请输入体积', trigger: 'change' },
    ],
    price: [
        { required: true, message: '请输入售价', trigger: 'change' },
    ],
    rmName: [
        { required: true, message: '请选择冷链室', trigger: 'change' },
    ]
}
const form = ref(null);
const visibleDrawer = ref(false)
const innerDialog = ref(false);
const rmChoose=ref(false);
// =====================================方法区 =====================================
const search = async () => {
    const result = await getGoodsListServer(searchBody);
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


const editButton = (row) => {
    row.tem = row.tem + ""
    row.hum = row.hum + ""
    row.status==3?rmChoose.value=true:rmChoose.value=false;
    goodsModel.value = JSON.parse(JSON.stringify(row));
    title.value = "编辑"
    dialogVisible.value = true;
}

const cleanModel = () => {
    goodsModel.value = {}
}

const editAndAdd = () => {
    form.value.validate(async valid => {
        if (valid) {
            await updateServer(goodsModel);
            ElMessage.success("修改成功");
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

const detail = async (row) => {
    console.log("row=", row);
    goodsModel.value = row;
    visibleDrawer.value = true;
}

const innerDialogCancel = () => {
    goodsModel.value.rmId = '';
    innerDialog.value = false;
}


const receive = (e) => {
    goodsModel.value.rmId=e.id;
    goodsModel.value.rmName=e.name;
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
                    <el-table-column prop="price" label="价格" />
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            {{ row.status == 1 ? '空闲' : (row.status == 2 ? '运输' : (row.status == 3 ? '在库' : '出售')) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="rmName" label="舱室">
                        <template #default="scope">
                            <el-tag type="success" disable-transitions>
                                {{ scope.row.rmName ? scope.row.rmName : "暂无" }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="userName" label="商户" />
                    <el-table-column prop="user_pic" label="头像">
                        <template #default="{ row }">
                            <el-image :src="row.goodsPic ? row.goodsPic : tempUrl" style="width: 60px;" />
                        </template>
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="success" size="small" @click="detail(row)">
                                详情
                            </el-button>
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
        <!-- 编辑弹窗 -->
        <div>
            <el-dialog v-model="dialogVisible" :title="title" width="350px">
                <el-form :model="goodsModel" :rules="rules" ref="form">

                    <el-form-item label="名称" prop="id">
                        <el-input v-model="goodsModel.id" disabled />
                    </el-form-item>

                    <el-form-item label="名称" prop="name">
                        <el-input v-model="goodsModel.name" disabled />
                    </el-form-item>

                    <el-form-item label="重量" prop="weight">
                        <el-input type="number" v-model="goodsModel.weight" />
                    </el-form-item>

                    <el-form-item label="体积" prop="volume">
                        <el-input type="number" v-model="goodsModel.volume" />
                    </el-form-item>

                    <el-form-item label="温度" prop="tem">
                        <el-input type="number" v-model="goodsModel.tem" />
                    </el-form-item>

                    <el-form-item label="湿度" prop="hum">
                        <el-input type="number" v-model="goodsModel.hum" />
                    </el-form-item>

                    <el-form-item label="售价" prop="price">
                        <el-input type="number" v-model="goodsModel.price" disabled />
                    </el-form-item>

                    <!-- 修改冷链舱室 -->
                    <el-form-item v-if="rmChoose" label="冷链室" prop="rmName">
                        <el-input v-model="goodsModel.rmName" @click="innerDialog = true" />
                    </el-form-item>

                    <el-dialog v-model="innerDialog" width="1000" title="请选择冷链室" append-to-body>
                        <RefrigerateChoose @event="receive"></RefrigerateChoose>
                        <template #footer>
                            <span class="dialog-footer">
                                <el-button @click="innerDialogCancel">取消</el-button>
                                <el-button type="primary" @click="innerDialog=false">确认</el-button>
                            </span>
                        </template>
                    </el-dialog>
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

        <!-- 右侧详情弹窗 -->
        <el-drawer v-model="visibleDrawer" title="农产品详情" direction="rtl" size="30%">
            <el-form :model="goodsModel">
                <el-form-item label="编号">
                    <el-input v-model="goodsModel.id" disabled />
                </el-form-item>

                <el-form-item label="名称">
                    <el-input v-model="goodsModel.name" disabled />
                </el-form-item>

                <el-form-item label="重量">
                    <el-input v-model="goodsModel.weight" disabled />
                </el-form-item>

                <el-form-item label="体积">
                    <el-input v-model="goodsModel.volume" disabled />
                </el-form-item>

                <el-form-item label="冷链温度">
                    <el-input v-model="goodsModel.tem" disabled />
                </el-form-item>

                <el-form-item label="冷链湿度">
                    <el-input v-model="goodsModel.hum" disabled />
                </el-form-item>

                <el-form-item label="商家">
                    <el-input v-model="goodsModel.userName" disabled />
                </el-form-item>

                <el-form-item label="舱室">
                    <el-input v-model="goodsModel.rmName" disabled />
                </el-form-item>

                <el-form-item label="对外售价">
                    <el-input v-model="goodsModel.price" disabled />
                </el-form-item>

                <el-form-item label="图片">
                    <img :src="goodsModel.goodsPic ? goodsModel.goodsPic : tempUrl" width="300px" />
                </el-form-item>
            </el-form>
        </el-drawer>


        <!-- 选择冷链中新的弹窗 -->

    </div>
</template>

<style scoped></style>

