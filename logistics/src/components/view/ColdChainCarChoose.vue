<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getColdChainCarListServer } from "@/api/coldChainCarApi.js"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    name: '',
    id: '',
    pageSize: 8,
    pageNum: 1
})
const tableData = ref([])

// =====================================方法区 =====================================
const search = async () => {
    const result = await getColdChainCarListServer(searchBody);
    let arr=new Array()
    allNumber.value=result.data.total;
    for(let i=0;i<result.data.items.length;i++){
        if(result.data.items[i].status==1){
            arr.push(result.data.items[i])
        }
    }
    tableData.value = arr;
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
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="车辆编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.name" style="width: 200px;margin-left:10px" placeholder="车辆名称"
                        :prefix-icon="Search" />

                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
            </template>
            <div>
                <el-table highlight-current-row
                @current-change="handleClick"  :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="车编号" />
                    <el-table-column prop="name" label="名称" />
                    <el-table-column prop="tem" label="当前温湿度" >
                        <template #default="scope">
                            {{ scope.row.tem+"℃/"+scope.row.hum+"%" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="volume" label="体积" >
                        <template #default="scope">
                            {{ scope.row.curVolume+"/"+scope.row.volume+"m³" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="weight" label="载重" >
                        <template #default="scope">
                            {{ scope.row.curWeight+"/"+scope.row.weight+"kg" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="当前状态">
                        <template #default="scope">
                            <el-tag :type="scope.row.status == 1 ? 'primary' :(scope.row.status == 2?'warning':'danger')" disable-transitions>
                                {{ scope.row.status == 1 ? '空闲' :( scope.row.status == 2?'繁忙':'停用' )}}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="area" label="地区">
                        <template #default="scope">
                            <el-tag type="primary" disable-transitions>
                                {{ scope.row.area }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="注册时间" />
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
        
    </div>
</template>

<style scoped></style>

