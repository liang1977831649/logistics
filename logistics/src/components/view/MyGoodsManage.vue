<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getGoodsListServer, addServer, updateServer, deleteServer, detailGoodsServer } from "@/api/goodsApi.js"
import { ElMessage, ElMessageBox } from "element-plus"
import useToken from "@/storage/tokenStorage"
import userInfoServer from "@/storage/userStorage"
import { detailRefrigerateServer } from "@/api/refrigerateApi"

import {
  Warning,
} from '@element-plus/icons-vue'
import { showRoomCostCompute} from "@/api/roomCostComputeApi"

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
    pageSize: 8
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
    userId: ""
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
    ]
}
const form = ref(null);
const visibleDrawer = ref(false)
const editFlag = ref(true);
const dialogVisibleOfCar = ref(false);
const carModel = ref({})
const roomCostComputeBody=ref({})
// =====================================方法区 =====================================
const search = async () => {
    const result = await getGoodsListServer(searchBody);
    tableData.value = result.data.items;
    allNumber.value = result.data.total;
    console.log("tableData.value=", tableData.value);
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
    editFlag.value = true;
    dialogVisible.value = true;
}
const editButton = (row) => {
    if (row.status != 1) {
        // ElMessage.error("农产品状态不在空闲状态中，不能修改")
        // return
        editFlag.value = false;
    } else {
        editFlag.value = true;
    }
    row.tem = row.tem + ""
    row.hum = row.hum + ""
    goodsModel.value = JSON.parse(JSON.stringify(row));
    goodsModel.value.status=goodsModel.value.status+"";
    console.log(goodsModel.value);
    title.value = "编辑"
    dialogVisible.value = true;
}

const cleanModel = () => {
    goodsModel.value = {}
}

const editAndAdd = () => {
    form.value.validate(async valid => {
        if (valid) {
            if (title.value == '新增') {
                goodsModel.value.userId = userInfoServer().userInfo.id
                console.log("goodsModel.value=", goodsModel.value);
                await addServer(goodsModel)
                ElMessage.success("新增成功!")
                dialogVisible.value = false;
                cleanModel();
                search()
            } else {
                goodsModel.value.userId = userInfoServer().userInfo.id
                await updateServer(goodsModel);
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
        '你确认要删除该农产品信息吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: "warning",
        })
        .then(async () => {
            if (row.status != 1) {
                ElMessage.error("农产品状态不在空闲中，不能删除")
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

const detail = async (row) => {
    const result = await detailGoodsServer(row.id);
    goodsModel.value = result.data
    visibleDrawer.value = true;
}

const loadAvatar = (res) => {
    goodsModel.value.goodsPic = res.data
}
const detailRefrigerate = async (id) => {
    console.log("id="+id);
    if (id) {
        const result = await detailRefrigerateServer(id);
        carModel.value = result.data;
        dialogVisibleOfCar.value = true;
    }
}

// 加载定价策略
const getRoomCostCompute = async () => {
  const result = await showRoomCostCompute();
  if (result.data) {
    roomCostComputeBody.value = result.data;
  }
}
getRoomCostCompute();
</script>

<template>
    <div>
        <!-- 有关定价策略 -->
        <el-statistic :value="'重量:'+roomCostComputeBody.weightPrice+'元/天*吨|体积:'+roomCostComputeBody.volumePrice+'元/天*m³|服务费:'+roomCostComputeBody.other+'元/次'">
          <template #title>
            <div style="display: inline-flex; align-items: center">
              官方冷藏室使用定价策略
              <el-tooltip
                effect="dark"
                :content="'重量:'+roomCostComputeBody.weightPrice+'元/天*吨|体积:'+roomCostComputeBody.volumePrice+'元/天*m³|服务费:'+roomCostComputeBody.other+'元/次'"
                placement="top"
              >
                <el-icon style="margin-left: 4px" :size="12">
                  <Warning />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
        </el-statistic>

        <el-card>
            <template #header>
                <div>
                    <el-input v-model="searchBody.id" style="width: 200px" placeholder="商品编号" :prefix-icon="Search" />
                    <el-input v-model="searchBody.name" style="width: 200px;margin-left:10px" placeholder="商品名称"
                        :prefix-icon="Search" />

                    <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
                    <el-button type="info" @click="reset()">重置</el-button>
                </div>
                <div>
                    <el-button type="primary" style="margin: 10px 0 10px 0;" @click="add()">新增</el-button>
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
                    <el-table-column prop="rmName" label="舱室">
                        <template #default="scope">
                            <el-link type="primary"  @click="detailRefrigerate(scope.row.rmId)">
                                {{ scope.row.rmName ? scope.row.rmName : "暂无" }}
                            </el-link>
                        </template>
                    </el-table-column>
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
                            <el-button type="primary" size="small" @click="editButton(row)">
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
            <el-dialog v-model="dialogVisible" :title="title" width="350px">
                <el-form :model="goodsModel" :rules="rules" ref="form">

                    <el-form-item label="名称" prop="name">
                        <el-input v-model="goodsModel.name" />
                    </el-form-item>

                    <el-form-item v-if="editFlag" label="重量" prop="weight">
                        <el-input type="number" v-model="goodsModel.weight" />
                    </el-form-item>

                    <el-form-item v-if="editFlag" label="体积" prop="volume">
                        <el-input type="number" v-model="goodsModel.volume" />
                    </el-form-item>

                    <el-form-item v-if="editFlag" label="温度" prop="tem">
                        <el-input type="number" v-model="goodsModel.tem" />
                    </el-form-item>

                    <el-form-item v-if="editFlag" label="湿度" prop="hum">
                        <el-input type="number" v-model="goodsModel.hum" />
                    </el-form-item>

                    <el-form-item label="售价" prop="price">
                        <el-input type="number" v-model="goodsModel.price" />
                    </el-form-item>


                    <el-form-item label="图片">
                        <el-upload action="http://localhost:8080/upload" :show-file-list="false" :on-success="loadAvatar"
                            :headers="{ 'Authorization': useToken().token }" name="multipartFile" :auto-upload="true">
                            <el-button type="primary">点击上传</el-button>
                            <!-- 上传头像 -->
                            <img :src="goodsModel.goodsPic ? goodsModel.goodsPic : tempUrl" width="100px" />
                        </el-upload>
                    </el-form-item>

                    <el-form-item v-if="goodsModel.status==3||goodsModel.status==4" label="是否出售" prop="price">
                        <!-- <el-input type="number" v-model="goodsModel.status" /> -->
                         <el-radio-group v-model="goodsModel.status">
                            <el-radio value="3">停售</el-radio>
                            <el-radio value="4">出售</el-radio>
                         </el-radio-group>
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
            </el-dialog>
        </div>

        <!-- 右侧详情弹窗 -->
        <el-drawer v-model="visibleDrawer" title="农产品详情" direction="rtl" size="30%">
            <el-form :model="goodsModel" :rules="rules" ref="form">
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

                <el-form-item label="冷链湿度" prop="hum">
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

        <!-- 冷链室详情 -->
        <div>
            <el-dialog v-model="dialogVisibleOfCar" title="冷链室详情" width="250px">
                <el-form>
                    <el-form-item label="Id">
                        <el-text tag="b">{{ carModel.id }}</el-text>
                    </el-form-item>
                    <el-form-item label="名称">
                        <el-text tag="b">{{ carModel.name }}</el-text>
                    </el-form-item>
                    <el-form-item label="温度湿度">
                        <el-text tag="b">{{ carModel.tem + "°C/" + carModel.hum+"%" }}</el-text>
                    </el-form-item>
                    <el-form-item label="所在冷链中心">
                        <el-text tag="b">{{ carModel.centreName }}</el-text>
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

