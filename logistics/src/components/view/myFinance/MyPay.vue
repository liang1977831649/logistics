<script setup>
// =====================================引用区 =====================================
import { ref } from "vue";
import { Search } from '@element-plus/icons-vue'
import { getGoodsCostListServer } from "@/api/GoodsCostApi"
import { detailGoodsServer } from "@/api/goodsApi"
import { detailDeliveryServer } from "@/api/deliveryApi"
import payCommon from "@/components/view/myFinance/payCommon.vue"

import { passwordValidServer } from "@/api/moneyApi"
import { ElMessage, ElNotification, ElMessageBox } from "element-plus"
// =====================================数据区 =====================================
const currentPage = ref(1)
const pageSize = ref(8)
const background = ref(false)
const disabled = ref(false)
const allNumber = ref(100)
const searchBody = ref({
  salesName: '',
  goodsName: '',
  status: '',
  deliId: '',
  pageNum: 1,
  pageSize: 8
})
const tableData = ref([])
const goodsCostModel = ref({
  id: "",
  buyerId: '',
  salesId: '',
  deliId: '',
  cost: '',
  status: '',
  salesName: '',
  goodsName: '',
  updateTime: ''
})

const visibleDrawer = ref(false)
const dialogVisibleOfUser = ref(false);
const dialogVisibleOfGoods = ref(false);
const goodsModel = ref(false);
const dialogVisibleOfDelivery = ref(false);
const deliveryModel = ref({})
const payMentRecord = ref({});

const childPayCommon = ref(null);

const passwordValid = ref(false);

const rules = ref({
  password: [
    { required: true, message: '请输入价格', trigger: 'blur' },
  ],
})
const formPassword = ref(null);
const dialogVisiblePassword = ref(false);
const userModel = ref({
  password: ''
})
// =====================================方法区 =====================================
const search = async () => {
  searchBody.value.type = 1;
  const result = await getGoodsCostListServer(searchBody);
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
  goodsCostModel.value = row;
  visibleDrawer.value = true;
}

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

//payCommon组件在支付成功后，子组件调用父组件MyPay的函数
const paySuccess = async () => {
  console.log("传到父组件成功");
  await search();
  dialogVisiblePassword.value=false
}

//支付弹窗和服务
const payWindows = async (row) => {
  userModel.value={}
  payMentRecord.value.transaction = 1;
  payMentRecord.value.transactionId = row.id;
  payMentRecord.value.cost = row.cost
  payMentRecord.value.collectId = row.salesId;
  //校验密码
  dialogVisiblePassword.value = true;
}

//跳到子组件支付弹窗
const confirmPay = async () => {
  if (passwordValid.value == false) {
    await passwordValidServer(userModel);
    passwordValid.value = true;
    dialogVisiblePassword.value = false;
    ElNotification.success("验证成功");
  }
  if (passwordValid.value == true) {
    childPayCommon.value.payWindows();
  }
}

const cancelPay = () => {
  //验证密码成功过后，无需再验证
  dialogVisiblePassword.value = false;
  userModel.value = ref({})
}
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div>
          <el-input v-model="searchBody.deliId" style="width: 200px" placeholder="配送单号" :prefix-icon="Search" />
          <el-input v-model="searchBody.goodsName" style="width: 200px;margin-left:10px" placeholder="商品名称"
            :prefix-icon="Search" />
          <el-input v-model="searchBody.userName" style="width: 200px;margin-left:10px" placeholder="商家"
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
          <el-table-column prop="id" label="订单编号" />
          <el-table-column prop="userName" label="商家" />
          <el-table-column prop="goodsName" label="商品">
            <template #default="{ row }">
              <el-link type="primary" @click="detailGoods(row)">{{ row.goodsName }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="deliId" label="配送单号">
            <template #default="{ row }">
              <el-link type="primary" @click="detailDelivery(row)">{{ row.deliId }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="cost" label="费用">
            <template #default="{ row }">
              <el-text>{{ row.cost + "元" }}</el-text>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status == 1 ? 'danger' : 'success'">
                {{ row.status == 1 ? '未支付' : '已支付' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default="{ row }">
              <el-button v-if="row.status == 1" type="primary" size="small" @click="payWindows(row)">
                支付
              </el-button>
              <el-button v-else type="success" size="small" disabled>
                已支付
              </el-button>
              <el-button type="info" size="small" @click="detail(row)">
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
      <el-form :model="goodsCostModel">
        <el-form-item label="编号">
          <el-text tag="b">{{ goodsCostModel.id }}</el-text>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-text tag="b">{{ goodsCostModel.salesName }}</el-text>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-text tag="b">{{ goodsCostModel.goodsName }}</el-text>
        </el-form-item>
        <el-form-item label="配送单号">
          <el-link type="primary" @click="detailDelivery(goodsCostModel.deliId)">{{ goodsCostModel.deliId }}</el-link>
        </el-form-item>
        <el-form-item label="费用">
          <el-text tag="b">{{ goodsCostModel.cost + "元" }}</el-text>
        </el-form-item>
        <el-form-item label="状态">
          <el-text tag="b">{{ goodsCostModel.status == 1 ? '未支付' : '已支付' }}</el-text>
        </el-form-item>
        <el-form-item label="最新时间">
          <el-text tag="b">{{ goodsCostModel.updateTime }}</el-text>
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
      <el-dialog v-model="dialogVisibleOfDelivery" title="农产品详情" width="320px">
        <el-form>
          <el-form-item label="Id">
            <el-text tag="b">{{ deliveryModel.id }}</el-text>
          </el-form-item>

          <el-form-item label="地址">
            <el-text tag="b">{{ deliveryModel.destination }}</el-text>
          </el-form-item>

          <el-form-item label="状态">
            <el-text tag="b">{{ deliveryModel.status == 1 ? '待发车' : (deliveryModel.status == 2 ? '运输中' :
              (deliveryModel.status == 3 ? '已送达' : '已收货')) }}</el-text>
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

    <!-- 支付窗口 -->
    <div>
      <payCommon :payMentRecord="payMentRecord" ref="childPayCommon" @event="paySuccess" />
    </div>

    <!-- 校验密码窗口 -->
    <div>
      <el-dialog v-model="dialogVisiblePassword" title="修改卡号" width="300px">
        <el-form :model="userModel" :rules="rules" ref="formPassword">
          <el-form-item label="密码验证" prop="password">
            <el-input type="password" v-model="userModel.password" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click=" cancelPay()">取消</el-button>
            <el-button type="primary" @click="confirmPay()">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>

  </div>
</template>

<style scoped></style>

