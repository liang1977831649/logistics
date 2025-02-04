<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getRoomCostListServer } from "@/api/roomCost"
import { detailGoodsServer } from "@/api/goodsApi"
import { detailDeliveryServer } from "@/api/deliveryApi"
import { detailUserServer } from "@/api/userApi"
import { showRoomCostCompute, updateRoomCostCompute } from "@/api/roomCostComputeApi"
import { updateRoomCostServer } from "@/api/roomCost"
import { ElMessage, ElNotification, ElMessageBox } from "element-plus"
import { getMoneyServer, updateMoneyServer, passwordValidServer,withdrawMoney } from "@/api/moneyApi"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(3)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
  status: '',
  deliId: '',
  goodsName: '',
  userId: '',
  pageNum: 1,
  pageSize: 8
})

const rules = {
  weightPrice: [
    { required: true, message: '请输入重量价格', trigger: 'blur' },
  ],
  volumePrice: [
    { required: true, message: '请输入提及价格', trigger: 'blur' },
  ],
  other: [
    { required: true, message: '请输入服务费', trigger: 'blur' },
  ],
  cost: [
    { required: true, message: '请输入价格', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入价格', trigger: 'blur' },
  ],
  carNum: [
    { required: true, message: '请输入价格', trigger: 'blur' },
  ]
}
const moneyModel = ref({
  id: '',
  carNum: '',
  balance: ''
})
const tableData = ref([])
const dialogVisibleOfGoods = ref(false);
const goodsModel = ref(false);
const dialogVisibleOfDelivery = ref(false);
const deliveryModel = ref({})
const userModel = ref({});
const dialogVisibleOfUser = ref(false)
const roomCostComputeBody = ref({
  weightPrice: '',
  volumePrice: '',
  other: '',
  areaId: ''
});
const dialogVisibleOfPrice = ref(false);
const formPrice = ref(null);
const roomCostModel = ref({});
const dialogVisibleOfUpdateDeliPrice = ref(false);
const formUpdate = ref(null);

const dialogVisibleOUpdateCarWindows = ref(false);
const formPassword = ref(null)
const formCar = ref(null)
const passwordValid = ref(false);
// =====================================方法区 =====================================
const search = async () => {
  searchBody.value.type = 1;
  const result = await getRoomCostListServer(searchBody);
  tableData.value = result.data.items;
  allNumber.value = result.data.total;
}
const getRoomCostCompute = async () => {
  const result = await showRoomCostCompute();
  if (result.data) {
    roomCostComputeBody.value = result.data;
  }
}
const getMoney = async () => {
  const result = await getMoneyServer();
  if (result.data != null) {
    moneyModel.value = result.data;
  } else {
    moneyModel.value = {}
  }
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
getRoomCostCompute();
getMoney();

//细节类方法
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
const detailUser = async (row) => {
  const result = await detailUserServer(row.userId);
  userModel.value = result.data;
  dialogVisibleOfUser.value = true;
}

// 修改价格策略窗口和方法
const updateRoomCostComputeWindows = () => {
  if (formPrice.value != null) {
    formPrice.value.resetFields();
  }
  dialogVisibleOfPrice.value = true
}
const confirmPrice = async () => {
  formPrice.value.validate(async valid => {
    if (valid) {
      await updateRoomCostCompute(roomCostComputeBody);
      dialogVisibleOfPrice.value = false;
    } else {
      ElMessage.error('数据错误');
    }
  })
}

//修改定价方法和窗口
const updatePriceWindows = (row) => {
  if (formUpdate.value != null) {
    formUpdate.value.resetFields();
  }
  roomCostModel.value.id = row.id;
  roomCostModel.value.cost = row.cost;
  dialogVisibleOfUpdateDeliPrice.value = true;
}
const confirmDeliPrice = () => {
  formUpdate.value.validate(async valid => {
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
          await updateRoomCostServer(roomCostModel);
          dialogVisibleOfUpdateDeliPrice.value = false;
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

//修改卡号以及提现窗口
const updateCarWindows = () => {
  if (formPassword.value) {
    formPassword.value.resetFields()
  }
  //如果卡号不存在的话
  if (moneyModel.value.carNum == null) {
    moneyModel.value.carNum = ''
  }
  dialogVisibleOUpdateCarWindows.value = true;
}

const withdrewWindows = async () => {
  console.log("moneyModel.value.balance", moneyModel.value.balance);
  if (moneyModel.value.balance == 0) {
    ElMessage.error("账户余额为0,不能提现")
    return;
  }
  if(!moneyModel.value.carNum){
    ElMessage.error('无银行卡，无法体现');
  }
  ElMessageBox.confirm(
    '你确认要提现吗?',
    '温馨提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: "warning",
    })
    .then(async () => {
      await withdrawMoney();
      await search();
      await getMoney();
      ElNotification.success("提现成功,24内到账")
    })
    .catch(() => {
    })
}
const confirmCar = async () => {
  if (passwordValid.value == false) {
    //验证密码
    await passwordValidServer(userModel);
    passwordValid.value = true;
    ElNotification.success("验证成功");
  } else {
    await updateMoneyServer(moneyModel);
    ElNotification.success("修改卡号成功");
    passwordValid.value = false;
    dialogVisibleOUpdateCarWindows.value = false;
    userModel.value = {}
  }
  getMoney()
}
const cancelUpdateWindows = () => {
  dialogVisibleOUpdateCarWindows.value = false;
  userModel.value = {};
  passwordValid.value = false;
  getMoney();
}

</script>

<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;height: 200px;">
          <h2>冷链室定价策略</h2>
          <span>重量价格：</span>
          <span>{{ roomCostComputeBody.weightPrice ? roomCostComputeBody.weightPrice : 0 }}</span>
          <span>元/吨*天 </span>
          <br />
          <span>体积价格：</span>
          <span>{{ roomCostComputeBody.volumePrice ? roomCostComputeBody.volumePrice : 0 }}</span>
          <span>元/立方米*天</span>
          <br>
          <span>服务费：</span>
          <span>{{ roomCostComputeBody.other ? roomCostComputeBody.other : 0  }}</span>
          <span>元/车次</span>
          <br />
          <template #footer>
            <el-link type="primary" @click="updateRoomCostComputeWindows()">我要修改</el-link>
          </template>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="margin-bottom: 20px;height: 200px;">
          <h2>{{ "账户余额：" + moneyModel.balance + "元" }}</h2>
          <h3 v-if="moneyModel.carNum">{{ "我的卡号：" + moneyModel.carNum }}</h3>
          <h3 v-else>我的卡号：暂无卡号</h3>
          <template #footer>
            <div v-if="moneyModel.carNum">
              <el-link size="small" type="primary" @click="withdrewWindows()">我要提现</el-link>
              <el-link style="margin-left: 20px;" size="small" type="success" @click="updateCarWindows()">修改卡号</el-link>
            </div>
            <div v-else>
              <el-button size="small" type="warning" @click="updateCarWindows()">立即创建</el-button>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div>

          <el-input v-model="searchBody.deliId" style="width: 200px" placeholder="配送单号" :prefix-icon="Search" />
          <el-input v-model="searchBody.goodsName" style="width: 200px;margin-left:10px" placeholder="商品名称"
            :prefix-icon="Search" />
          <el-input v-model="searchBody.userId" style="width: 200px;margin-left:10px" placeholder="商家ID"
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
          <el-table-column prop="id" label="编号" />
          <el-table-column prop="goodsName" label="商品">
            <template #default="{ row }">
              <el-link type="primary" @click="detailGoods(row)">{{ row.goodsName }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="userName" label="商家">
            <template #default="{ row }">
              <el-link type="primary" @click="detailUser(row)">{{ row.userName }}</el-link>
            </template>
          </el-table-column>

          <el-table-column prop="day" label="冷藏时长">
            <template #default="{ row }">
              <el-text tag="b">{{ row.day + "天" }}</el-text>
            </template>
          </el-table-column>

          <el-table-column prop="day" label="体积和重量">
            <template #default="{ row }">
              <el-text tag="b">{{ row.weight + "KG/" + row.volume + "m³" }}</el-text>
            </template>
          </el-table-column>

          <el-table-column prop="deliId" label="配送单号">
            <template #default="{ row }">
              <el-link type="primary" @click="detailDelivery(row)">{{ row.deliId }}</el-link>
            </template>
          </el-table-column>

          <el-table-column prop="cost" label="费用">
            <template #default="{ row }">
              <el-text>{{ "+" + row.cost + "元" }}</el-text>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status == 1 ? 'danger' : 'success'">
                {{ row.status == 1 ? '对方未支付' : '对方已支付' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="updateTime" label="时间" />

          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default="{ row }">
              <el-button v-if="row.status == 1" type="primary" size="small" @click="updatePriceWindows(row)">
                修改价格
              </el-button>
              <el-button v-else type="primary" size="small" disabled>
                修改价格
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
            :page-sizes="[3, 5, 8]" :disabled="disabled" :background="background"
            layout="prev, pager, next, jumper,total, sizes" :total="allNumber" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </template>
    </el-card>

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
      <el-dialog v-model="dialogVisibleOfDelivery" title="配送单详情" width="320px">
        <el-form>
          <el-form-item label="Id">
            <el-text tag="b">{{ deliveryModel.id }}</el-text>
          </el-form-item>

          <el-form-item label="地址">
            <el-text tag="b">{{ deliveryModel.destination }}</el-text>
          </el-form-item>

          <el-form-item label="状态">
            <el-text tag="b">{{ deliveryModel.status == 1 ? '待发车' : (deliveryModel.status == 2 ? '运输中' :
              (deliveryModel.status == 3 ? '已送达' : '对方已收货')) }}</el-text>
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

    <!-- 修改价格策略窗口 -->
    <div>
      <el-dialog v-model="dialogVisibleOfPrice" title="修改车辆温湿度" width="300px">
        <el-form :model="roomCostComputeBody" :rules="rules" ref="formPrice">
          <el-form-item label="重量价格" prop="weightPrice">
            <el-input type="number" v-model="roomCostComputeBody.weightPrice" />
          </el-form-item>
          <el-form-item label="体积价格" prop="volumePrice">
            <el-input type="number" v-model="roomCostComputeBody.volumePrice" />
          </el-form-item>
          <el-form-item label="服务价格" prop="other">
            <el-input type="number" v-model="roomCostComputeBody.other" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="getRoomCostCompute(); dialogVisibleOfPrice = false">取消</el-button>
            <el-button type="primary" @click="confirmPrice()">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>

    <!-- 修改价格窗口 -->
    <div>
      <el-dialog v-model="dialogVisibleOfUpdateDeliPrice" title="修改车辆温湿度" width="300px">
        <el-form :model="roomCostModel" :rules="rules" ref="formUpdate">
          <el-form-item label="价格" prop="cost">
            <el-input type="number" v-model="roomCostModel.cost" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click=" dialogVisibleOfUpdateDeliPrice = false">取消</el-button>
            <el-button type="primary" @click="confirmDeliPrice()">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>

    <!-- 修改卡号窗口 -->
    <div>
      <el-dialog v-model="dialogVisibleOUpdateCarWindows" title="修改卡号" width="300px">
        <el-form v-if="!passwordValid" :model="userModel" :rules="rules" ref="formPassword">
          <span>请先校验密码</span>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="userModel.password" />
          </el-form-item>
        </el-form>
        <el-form v-else :model="moneyModel" :rules="rules" ref="formCar">
          <el-form-item label="新卡号" prop="carNum">
            <el-input type="number" v-model="moneyModel.carNum" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click=" cancelUpdateWindows()">取消</el-button>
            <el-button type="primary" @click="confirmCar()">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>

  </div>
</template>

<style scoped></style>

