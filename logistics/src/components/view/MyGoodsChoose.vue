<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getGoodsListServer, detailGoodsServer } from "@/api/goodsApi.js"

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
    pageNum:1,
    pageSize:8
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
    userId: ""
})

const visibleDrawer = ref(false)
// =====================================方法区 =====================================
const search = async () => {
    const result = await getGoodsListServer(searchBody);
    const itemTableData= result.data.items;
    allNumber.value = result.data.total;

    let arr =new Array();
    for(let i=0;i<itemTableData.length;i++){
        if(itemTableData[i].status==1){
            arr.push(itemTableData[i])
        }
    }
    tableData.value=arr;
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

const detail = async (row) => {
    const result = await detailGoodsServer(row.id);
    goodsModel.value = result.data
    visibleDrawer.value = true;
}

const emit=defineEmits(['event'])
const handleClick=(row)=>{
    emit('event',row)
}

defineExpose({search})//将方法暴露出来，便于父组件调用

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
                <el-table :data="tableData" stripe style="width: 100%"
                highlight-current-row
                @current-change="handleClick">
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
            <el-form :model="goodsModel" >
                <el-form-item label="编号" prop="id">
                    <el-input v-model="goodsModel.id" disabled />
                </el-form-item>

                <el-form-item label="名称" prop="name">
                    <el-input v-model="goodsModel.name" disabled />
                </el-form-item>

                <el-form-item label="重量" prop="weight">
                    <el-input type="number" v-model="goodsModel.weight" disabled />
                </el-form-item>

                <el-form-item label="体积" prop="volume">
                    <el-input type="number" v-model="goodsModel.volume" disabled />
                </el-form-item>

                <el-form-item label="冷链温度" prop="tem">
                    <el-input type="number" v-model="goodsModel.tem" disabled />
                </el-form-item>

                <el-form-item label="冷链温度" prop="hum">
                    <el-input type="number" v-model="goodsModel.hum" disabled />
                </el-form-item>

                <el-form-item label="对外售价" prop="price">
                    <el-input type="number" v-model="goodsModel.price" disabled />
                </el-form-item>

                <el-form-item label="图片">
                    <img :src="goodsModel.goodsPic ? goodsModel.goodsPic : tempUrl" width="300px" />
                </el-form-item>
            </el-form>
        </el-drawer>
    </div>
</template>

<style scoped></style>

