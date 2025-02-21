<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getColdChainCarListServer, addServer, updateServer, deleteServer } from "@/api/coldChainCarApi.js"
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import locationStorage from "@/storage/locationStorage.js"
import { ElMessage, ElMessageBox, ElNotification } from "element-plus"

// =====================================数据区 =====================================
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
    name: '',
    id: '',
    pageSize: 8,
    pageNum: 1,
    status: ''
})
const dialogVisible = ref(false);
const tableData = ref([])
const title = ref('');
const coldChainCarModel = ref({
    id: '',
    name: '',
    hum: '',
    tem: '',
    volume: '',
    weight: '',
    area: '',
    areaId: '',
    status: '',
    areaModel: []
})

const rules = {
    id: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { pattern: /^cc.+$/, message: '编号以cc开头', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { max: 20, min: 4, message: '姓名字符长度在4-20之间', trigger: 'blur' }
    ],
    areaModel: [
        { required: true, message: '请选择地区', trigger: 'blur' },
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'change' },
    ],
    hum: [
        { required: true, message: '请输入湿度', trigger: 'blur' },
    ],
    tem: [
        { required: true, message: '请输入温度', trigger: 'blur' },
    ],
    weight: [
        { required: true, message: '请输入载重', trigger: 'blur' },
    ],
    volume: [
        { required: true, message: '请输入体积', trigger: 'blur' },
    ]
}
const form = ref(null);
const dialogHumTem = ref(false);
const formHumTem = ref(null);
// =====================================方法区 =====================================

//加载当前的位置信息
const loadingAreaModel = () => {
    coldChainCarModel.value.areaModel = locationStorage().location;
}
const search = async () => {
    const result = await getColdChainCarListServer(searchBody);
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


const add = () => {
    cleanModel();
    title.value = '新增';
    dialogVisible.value = true;
}
const editButton = (row) => {
    if(row.status!=1){
        ElMessage.error('该车辆状态不是空闲，不可修改')
        return ;
    }
    coldChainCarModel.value = JSON.parse(JSON.stringify(row));
    loadingAreaModel();
    coldChainCarModel.value.status = coldChainCarModel.value.status + ""
    console.log(coldChainCarModel.value);
    title.value = "编辑"
    dialogVisible.value = true;
}

const cleanModel = () => {
    coldChainCarModel.value = {}
}

const onChange = (e) => {
    const three = chinaData[e[2]];
    coldChainCarModel.value.areaModel = JSON.parse(JSON.stringify(e));
    coldChainCarModel.value.area = three.label;
    coldChainCarModel.value.areaId = three.value;
}

const editAndAdd = () => {
    form.value.validate(async valid => {
        if (valid) {
            if (title.value == '新增') {
                await addServer(coldChainCarModel)
                ElMessage.success("新增成功!")
                dialogVisible.value = false;
                cleanModel();
                search()
            } else {
                await updateServer(coldChainCarModel);
                ElMessage.success("修改成功");
                dialogVisible.value = false;
                cleanModel();
                search()
            }
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

const deleteButton = async (row) => {
    ElMessageBox.confirm(
        '你确认要删除该车辆信息吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            if (row.status != 1) {
                ElMessage.error("该车状态不是空闲，不可删除")
                return;
            }
            await deleteServer(row.id);
            await search()
            ElMessage.success("删除成功")
        })
        .catch(() => {
            ElMessage.info('用户取消了删除')
        })
}

const updateHumTemWindows = (row) => {
    coldChainCarModel.value = {};
    coldChainCarModel.value.id=row.id;
    coldChainCarModel.value.name=row.name
    coldChainCarModel.value.hum=row.hum
    coldChainCarModel.value.tem=row.tem
    dialogHumTem.value = true;
}

const updateHumTem = () => {
    formHumTem.value.validate(async valid => {
        if (valid) {
            await updateServer(coldChainCarModel)
            dialogHumTem.value = false;
            await search()
            ElNotification.success("已修改温湿度")
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
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="车辆编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.name" style="width: 200px;margin-left:10px" placeholder="车辆名称"
                        :prefix-icon="Search" />

                    <el-select v-model="searchBody.status" style="width: 200px;margin-left: 10px;" placeholder="请选择状态">
                        <el-option label="空闲" value="1" />
                        <el-option label="待发车" value="2" />
                        <el-option label="运输中" value="3" />
                    </el-select>

                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
                <div>
                    <el-button type="primary" style="margin: 10px 0 10px 0;" @click="add()">新增</el-button>
                </div>
            </template>
            <div>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="id" label="车编号" />
                    <el-table-column prop="name" label="名称" />
                    <el-table-column prop="tem" label="当前温度" >
                        <template #default="{row}">
                            {{ row.tem+"℃" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="hum" label="当前湿度" >
                        <template #default="{row}">
                            {{ row.hum+"%" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="volume" label="体积">
                        <template #default="scope">
                            {{ scope.row.curVolume + "/" + scope.row.volume+"m³" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="weight" label="载重">
                        <template #default="scope">
                            {{ scope.row.curWeight + "/" + scope.row.weight+"kg" }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="当前状态">
                        <template #default="scope">
                            <el-tag :type="scope.row.status == 1 ? 'primary' : (scope.row.status == 2 ? 'warning' : 'danger')"
                                disable-transitions>
                                {{ scope.row.status == 1 ? '空闲' : (scope.row.status == 2 ? '待发车' : '运输中') }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column fixed="right" label="操作" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" size="small" @click="updateHumTemWindows(row)">
                                温湿度
                            </el-button>
                            <el-button type="success" size="small" @click="editButton(row)">
                                编辑
                            </el-button>
                            <el-button type="danger" size="small" @click="deleteButton(row)">
                                删除
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

        <!-- 添加或者编辑弹窗 -->
        <div>
            <el-drawer v-model="dialogVisible" :title="title" direction="rtl" size="30%">
                <el-form :model="coldChainCarModel" :rules="rules" ref="form">
                    <el-form-item v-if="title == '新增'" label="编号" prop="id">
                        <el-input v-model="coldChainCarModel.id" />
                    </el-form-item>
                    <el-form-item v-else label="编号">
                        <el-input v-model="coldChainCarModel.id" disabled />
                    </el-form-item>

                    <el-form-item label="名称" prop="name">
                        <el-input v-model="coldChainCarModel.name" />
                    </el-form-item>

                    <el-form-item label="当前体积" prop="volume" v-if="title == '编辑'">
                        <el-input type="number" v-model="coldChainCarModel.curVolume" />
                    </el-form-item>

                    <el-form-item label="体积" prop="volume">
                        <el-input type="number" v-model="coldChainCarModel.volume" />
                    </el-form-item>

                    <el-form-item label="当前载重" prop="weight" v-if="title == '编辑'">
                        <el-input type="number" v-model="coldChainCarModel.curWeight" />
                    </el-form-item>

                    <el-form-item label="载重" prop="weight">
                        <el-input type="number" v-model="coldChainCarModel.weight" />
                    </el-form-item>

                    <el-form-item v-if="title == '编辑'" label="温度" prop="tem">
                        <el-input type="number" v-model="coldChainCarModel.tem" />
                    </el-form-item>

                    <el-form-item v-if="title == '编辑'" label="湿度" prop="hum">
                        <el-input type="number" v-model="coldChainCarModel.hum" />
                    </el-form-item>

                    <el-form-item v-if="title == '编辑'" label="状态" prop="status">
                        <el-radio-group v-model="coldChainCarModel.status">
                            <el-radio value=1>空闲</el-radio>
                            <el-radio value=2>待发车</el-radio>
                            <el-radio value=3>运输中</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item v-if="title == '编辑'" label="地区" prop="areaModel">
                        <elui-china-area-dht placeholder="请选择" v-model="coldChainCarModel.areaModel"
                            @change="onChange"></elui-china-area-dht>
                    </el-form-item>

                    <el-form-item label="注册时间">
                        <el-input v-model="coldChainCarModel.createTime" disabled />
                    </el-form-item>
                </el-form>

                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="cancelButton()">取消</el-button>
                        <el-button type="primary" @click="editAndAdd()">
                            确定
                        </el-button>
                    </div>
                </template>
            </el-drawer>
        </div>

        <div>
            <el-dialog v-model="dialogHumTem" title="修改车辆温湿度" width="300px">
                <el-form :model="coldChainCarModel" :rules="rules" ref="formHumTem">
                    <el-form-item label="Id">
                        <el-text tag="b">{{ coldChainCarModel.id }}</el-text>
                    </el-form-item>
                    <el-form-item label="车名">
                        <el-text tag="b">{{ coldChainCarModel.name }}</el-text>
                    </el-form-item>
                    <el-form-item label="温度" prop="tem">
                        <el-input type="number" v-model="coldChainCarModel.tem" />
                    </el-form-item>
                    <el-form-item label="湿度" prop="hum">
                        <el-input type="number" v-model="coldChainCarModel.hum" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="coldChainCarModel = {}; dialogHumTem = false">取消</el-button>
                        <el-button type="primary" @click="updateHumTem">确定</el-button>
                    </div>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<style scoped></style>

