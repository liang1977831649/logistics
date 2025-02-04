<script setup>
// ==============================引入区================================
import { getMoneyServer, updateMoneyServer, passwordValidServer,withdrawMoney } from "@/api/moneyApi"
import { ElMessage, ElNotification, ElMessageBox } from "element-plus"
import { ref } from "vue"
import { Search } from '@element-plus/icons-vue'
import userInfoServer from "@/storage/userStorage"
import { getPaymentRecordListServer } from "@/api/paymentRecord"
import { getDetailGoodsCostServer } from "@/api/GoodsCostApi"
import { detailRoomCostServer } from "@/api/roomCost"
import { detailUserServer } from "@/api/userApi"

// ==============================数据区================================
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

const tableData = ref([])
const moneyModel = ref({
  id: '',
  userId: '',
  balance: 0,
  carNum: ''
})
const dialogVisibleOUpdateCarWindows = ref(false);
const formPassword = ref(null)
const formCar = ref(null)
const passwordValid = ref(false);
const userModel = ref({})
const user = userInfoServer();
const roomCostModel = ref({})
const goodsCostModel = ref({})
const visibleDrawerOfGoodsCost = ref(false)
const visibleDrawerOfRoomCost = ref(false)
const dialogVisibleOfUser=ref(false);
// ==============================方法区================================
const search = async () => {
  const result = await getPaymentRecordListServer(searchBody);
  tableData.value = result.data.items;
  allNumber.value = result.data.total;
}
search()
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

const rules = {
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  carNum: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const getMoney = async () => {
  const result = await getMoneyServer();
  if (result.data != null) {
    moneyModel.value = result.data;
  }
}
getMoney()
//更新卡号
const updateCarWindows = () => {
  if (formPassword.value) {
    formPassword.value.resetFields()
  }
  dialogVisibleOUpdateCarWindows.value = true;
}
//提现
const withdrewWindows = async () => {
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
      ElMessage.info('已取消')
    })
}
//更新卡号
const confirmCar = async () => {
  if (passwordValid.value == false) {
    //验证密码
    await passwordValidServer(userModel);
    passwordValid.value = true;
    ElNotification.success("验证成功");
  } else {
    //修改卡号
    await updateMoneyServer(moneyModel);
    ElNotification.success("修改卡号成功");
    passwordValid.value = false;
    dialogVisibleOUpdateCarWindows.value = false;
    userModel.value = {}
  }
}
//取消操作
const cancelUpdateWindows = () => {
  dialogVisibleOUpdateCarWindows.value = false;
  userModel.value = {};
  passwordValid.value = false;
  moneyModel.value = ref({})
  getMoney();
}

const detailGoodsCost = async (row) => {
  const result = await getDetailGoodsCostServer(row.transactionId)
  goodsCostModel.value = result.data
  visibleDrawerOfGoodsCost.value = true;
}
const detailRoomCost = async (row) => {
  const result = await detailRoomCostServer(row.transactionId);
  roomCostModel.value = result.data;
  visibleDrawerOfRoomCost.value = true
}

const detailUser = async (row) => {
  const result = await detailUserServer(row.userId);
  console.log("result=",result.data);
  userModel.value = result.data;

  dialogVisibleOfUser.value = true;
}
</script>

<template>
  <div>
    <el-card style="margin-bottom: 20px;">
      <div>
        <h2>{{ "账户余额：" + moneyModel.balance+"元" }}</h2>
        <div v-if="moneyModel.carNum">
          <h3>{{ "我的卡号：" + moneyModel.carNum }}</h3>
          <el-button size="small" type="primary" @click="withdrewWindows">我要提现</el-button>
          <el-button style="margin-left: 20px;" size="small" type="success" @click="updateCarWindows()">修改卡号</el-button>
        </div>
        <div v-else>
          <h3>我的卡号：暂无卡号</h3>
          <el-button size="small" type="warning" @click="updateCarWindows()">立即创建</el-button>
        </div>
      </div>
      <div style="margin-top: 10px;">

      </div>
    </el-card>


    <!-- 有关我交易来往的窗口 -->
    <el-card>
      <template #header>
        <div>
          <el-input v-model="searchBody.userName" style="width: 200px" placeholder="姓名" :prefix-icon="Search" />
          <el-select v-model="searchBody.transaction" style="width: 200px;margin-left: 10px;" placeholder="交易类型">
            <el-option label="农产品交易" value="1" />
            <el-option label="冷藏室服务交易" value="2" />
            <el-option label="提现业务" value="3" />
          </el-select>
          <el-input v-model="searchBody.transactionId" style="width: 200px;;margin-left: 10px;" placeholder="交易ID"
            :prefix-icon="Search" />

          <el-button type="primary" style="margin-left: 10px;" @click="search()">搜索</el-button>
          <el-button type="info" @click="reset()">重置</el-button>
        </div>
      </template>
      <div>
        <el-table :data="tableData" stripe style="width: 100%">
          <el-table-column prop="payId" label="支付方">
            <template #default="{ row }">
              <el-text v-if="row.payId == user.userInfo.id">我</el-text>
              <el-link v-else @click="detailUser(row)">{{ row.userName }}</el-link>
            </template>
          </el-table-column>

          <el-table-column prop="collectId" label="收款方">
            <template #default="{ row }">
              <el-text v-if="row.collectId == user.userInfo.id">我</el-text>
              <el-link v-else @click="detailUser(row)">{{ row.userName }}</el-link>
            </template>
          </el-table-column>

          <el-table-column prop="payType" label="支付方式">
            <template #default="{ row }">
              <el-text>{{ row.payType == 1 ? '微信支付' :(row.payType==2? '账户支付':'提现') }}</el-text>
            </template>
          </el-table-column>

          <el-table-column prop="cost" label="交易金额">
            <template #default="{ row }">
              <el-text v-if="row.transaction!=3&&row.collectId == user.userInfo.id">{{ "+" + row.cost + "元" }}</el-text>
              <el-text v-else>{{ "-" + row.cost + "元" }}</el-text>
            </template>
          </el-table-column>

          <el-table-column prop="transaction" label="交易类型">
            <template #default="{ row }">
              <el-tag :type="row.transaction == 1 ? 'success' :(row.transaction==2?'warning':'danger') ">
                {{ row.transaction == 1 ? '农产品交易' :(row.transaction==2?'冷链室交易':'提现业务')  }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="transactionId" label="订单编号">
            <template #default="{ row }">
              <el-link v-if="row.transaction == 1" type="primary" @click="detailGoodsCost(row)">
                {{ row.transactionId }}
              </el-link>
              <el-link v-else type="primary" @click="detailRoomCost(row)">
                {{ row.transactionId }}
              </el-link>
            </template>
          </el-table-column>

          <el-table-column prop="createTime" label="支付时间" />

          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default="{ row }">
              <el-button size="small">无操作</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <div>
          <el-pagination style="float: right;
      margin-top: 5px;margin-bottom: 10px;" v-model:current-page="currentPage" v-model:page-size="pageSize"
            :page-sizes="[3, 5, 8]" :disabled="disabled" :background="background"
            layout="prev, pager, next, jumper,total, sizes" :total="allNumber" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
        </div>
      </template>
    </el-card>

    <!-- 创建和修改卡号窗口 -->
    <div>
      <el-dialog v-model="dialogVisibleOUpdateCarWindows" title="修改银行卡号" width="300px">
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

    <!-- 右侧产品费用详情弹窗 -->
    <el-drawer v-model="visibleDrawerOfGoodsCost" title="农产品购买详情" direction="rtl" size="30%">
      <el-form :model="goodsCostModel">

        <el-form-item label="单号">
          <el-text tag="b">{{ goodsCostModel.id }}</el-text>
        </el-form-item>

        <el-form-item v-if="goodsCostModel.salesId == user.userInfo.id" label="买家Id">
          <el-text tag="b">{{ goodsCostModel.buyerId }}</el-text>
        </el-form-item>

        <el-form-item v-else label="卖家Id">
          <el-text tag="b">{{ goodsCostModel.salesId }}</el-text>

        </el-form-item>

        <el-form-item label="交易人">
          <el-text tag="b">{{ goodsCostModel.userName }}</el-text>
        </el-form-item>

        <el-form-item label="运输单号">
          <el-text tag="b">{{ goodsCostModel.deliId }}</el-text>
        </el-form-item>

        <el-form-item label="农产品ID">
          <el-text tag="b">{{ goodsCostModel.goodsId }}</el-text>
        </el-form-item>

        <el-form-item label="农产品">
          <el-text tag="b">{{ goodsCostModel.goodsName }}</el-text>
        </el-form-item>

        <el-form-item label="购买重量">
          <el-text tag="b">{{ goodsCostModel.weight + "kg" }}</el-text>
        </el-form-item>

        <el-form-item label="产品费用">
          <el-text tag="b">{{ goodsCostModel.cost + "元" }}</el-text>
        </el-form-item>

        <el-form-item label="产生时间">
          <el-text tag="b">{{ goodsCostModel.updateTime }}</el-text>
        </el-form-item>

      </el-form>
    </el-drawer>

    <!-- 右侧产品费用详情弹窗 -->
    <el-drawer v-model="visibleDrawerOfRoomCost" title="农产品购买详情" direction="rtl" size="30%">
      <el-form :model="roomCostModel">

        <el-form-item label="单号">
          <el-text tag="b">{{ roomCostModel.id }}</el-text>
        </el-form-item>

        <el-form-item label="配送单号">
          <el-text tag="b">{{ roomCostModel.deliId }}</el-text>
        </el-form-item>

        <el-form-item label="农产品ID">
          <el-text tag="b">{{ roomCostModel.goodsId }}</el-text>
        </el-form-item>

        <el-form-item label="农产品">
          <el-text tag="b">{{ roomCostModel.goodsName }}</el-text>
        </el-form-item>

        <el-form-item label="产品重量">
          <el-text tag="b">{{ roomCostModel.weight + "kg" }}</el-text>
        </el-form-item>

        <el-form-item label="产品体积">
          <el-text tag="b">{{ roomCostModel.volume + "m³" }}</el-text>
        </el-form-item>

        <el-form-item label="冷藏时间">
          <el-text tag="b">{{ roomCostModel.day + "天" }}</el-text>
        </el-form-item>

        <el-form-item label="冷藏费用">
          <el-text tag="b">{{ roomCostModel.cost + "元" }}</el-text>
        </el-form-item>

        <el-form-item label="产生时间">
          <el-text tag="b">{{ roomCostModel.updateTime }}</el-text>
        </el-form-item>

      </el-form>
    </el-drawer>

    <!-- 用户详情 -->
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
  </div>
</template>

<style></style>