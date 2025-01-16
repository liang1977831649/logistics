<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getRefrigerateListServer, detailServer } from "@/api/refrigerateApi.js"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    id: "",
    name: "",
})
const tableData = ref([])
const refrigerateModel = ref({
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
const form = ref(null);
const visibleDrawer = ref(false)
// =====================================方法区 =====================================
const search = async () => {
    const result = await getRefrigerateListServer(searchBody);
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
    if (form.value != null) {
        form.value.resetFields();
    }
    const result = await detailServer(row.id);
    refrigerateModel.value = result.data
    console.log("refrigerateModel.value=", refrigerateModel.value);
    visibleDrawer.value = true;
}
const emit=defineEmits(['event'])
const handleClick=(row)=>{
    emit('event',row)
}
</script>

<template>
    <div>
        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.name" style="width: 200px;margin-left:10px" placeholder="名称"
                        :prefix-icon="Search" />
                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
            </template>
            <div>
                <el-table highlight-current-row
                @current-change="handleClick" :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="编号" />
                    <el-table-column prop="name" label="名称" />
                    <el-table-column prop="volume" label="容量">
                        <template #default="scope">
                            {{ scope.row.curVolume + "/" + scope.row.maxVolume }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="centreName" label="仓库">
                        <template #default="scope">
                            <el-tag type="primary">
                                {{ scope.row.centreName }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态">
                        <template #default="{ row }">
                            {{ row.status == 1 ? '空闲' : '装载' }}
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
        <el-drawer v-model="visibleDrawer" title="冷链室详情" direction="rtl" size="35%">
            <el-form :model="refrigerateModel" >
                <el-form-item label="编号" prop="id">
                    <el-input v-model="refrigerateModel.id" disabled />
                </el-form-item>

                <el-form-item label="名称" prop="name">
                    <el-input v-model="refrigerateModel.name" disabled/>
                </el-form-item>

                <el-form-item label="当前体积" prop="curVolume">
                    <el-input type="number" v-model="refrigerateModel.curVolume" disabled />
                </el-form-item>

                <el-form-item label="体积" prop="maxVolume">
                    <el-input type="number" v-model="refrigerateModel.maxVolume" disabled/>
                </el-form-item>

                <el-form-item label="室内温度" prop="tem">
                    <el-input type="number" v-model="refrigerateModel.tem" disabled/>
                </el-form-item>

                <el-form-item label="室内湿度" prop="hum">
                    <el-input type="number" v-model="refrigerateModel.hum" disabled/>
                </el-form-item>

                <el-form-item label="当前状态" prop="status">
                    <el-input v-model="refrigerateModel.status" disabled
                        :label="refrigerateModel.status == 1 ? '空闲' : '装载'" />
                </el-form-item>

                <el-form-item label="所属冷链中心">
                    <el-input v-model="refrigerateModel.centreName" disabled />
                </el-form-item>

                <el-form-item label="创建时间">
                    <el-input v-model="refrigerateModel.createTime" disabled />
                </el-form-item>
            </el-form>

        </el-drawer>
    </div>
</template>

<style scoped></style>

