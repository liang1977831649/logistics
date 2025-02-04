<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getTsCarList } from "@/api/tsCar.js"
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

// =====================================方法区 =====================================
const search = async () => {
    const result = await getTsCarList(searchBody);
    allNumber.value = result.data.total;

    let arr=new Array();
    for(let i=0;i<result.data.items.length;i++){
        if(result.data.items[i].status==1){
            arr.push(result.data.items[i])
        }
    }
    tableData.value = arr;
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

const emit=defineEmits(['event'])
const handleClick=(row)=>{
    emit('event',row)
}
defineExpose({search})
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
            </template>
            <div>
                <el-table highlight-current-row
                @current-change="handleClick" :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="编号" />
                    <el-table-column prop="carId" label="车辆Id" />
                    <el-table-column prop="carName" label="车辆" />
                    <el-table-column prop="driverId" label="司机Id" />
                    <el-table-column prop="driverName" label="司机" />
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            {{ row.status == 1 ? '空闲' : (row.status == 2 ? '运输中' : '已结束') }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="updateTime" label="更新时间" />
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
        
    </div>
</template>

<style scoped></style>

