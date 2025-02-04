<script setup>
//======================================引用区====================================
import userInfoServer from "@/storage/userStorage"
import locationStorage from "@/storage/locationStorage"
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import { ref } from "vue"
import {
  ArrowRight,
  CaretBottom,
  CaretTop,
  Warning,
} from '@element-plus/icons-vue'
import { getGoodsListServer } from "@/api/goodsApi.js"
import { getMoneyServer } from "@/api/moneyApi"
import { getTransportationListServer } from "@/api/transportationApi.js"
import { getDeliverList } from "@/api/deliveryApi"
import { computed } from "vue"

//======================================数据区====================================
const user = userInfoServer().userInfo
const location = locationStorage().location
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;
const areaList = ref({
  province: '',
  city: '',
  county: ''
})

const searchBodyMyGoods = ref({})
const myGoodsList = ref([]);

const moneyModel = ref({
  id:'',
  userId:'',
  balance:0,
  carNum:''
})

const searchMyTransportation = ref({})
const myTransportation = ref([])

const searchMyDelivery = ref({})
const myDelivery = ref([])


//======================================引用区====================================
//个人服务地区
const loadingLocation = () => {
  areaList.value.county = chinaData[location[2]];
  areaList.value.city = chinaData[location[1]];
  areaList.value.province = chinaData[location[0]];
  console.log(areaList.value);
}
loadingLocation()

//加载我的商品
const loadingMyGoods = async () => {
  const result = await getGoodsListServer(searchBodyMyGoods);
  myGoodsList.value = result.data.items;
}
loadingMyGoods();

//加载金币
const getMoney = async () => {
  const result = await getMoneyServer();
  if(result.data!=null){
    moneyModel.value = result.data;
  }
}
getMoney()

//加载运输
const loadingTransportation = async () => {
  searchMyTransportation.value.userId = user.id;
  const result = await getTransportationListServer(searchMyTransportation);
  myTransportation.value = result.data.items
}
loadingTransportation()

//加载配送
const loadingDelivery = async () => {
  const result = await getDeliverList(searchMyDelivery);
  myDelivery.value = result.data.items;
}

const formatData = computed(() => {
  const oDate = new Date()
  const year = oDate.getFullYear()
  const month = oDate.getMonth() + 1
  const day = oDate.getDate()
  const hour = oDate.getHours()
  const min = oDate.getMinutes();
  const second = oDate.getSeconds();
  return year + '-' + month + '-' + day + ' ' + hour + ':' + min + ':' + second
})
loadingDelivery()


</script>

<template>
  <div>
    <!-- 个人信息列表 -->
    <el-card>
      <div>
        <el-descriptions title="我的个人信息" border>
          <el-descriptions-item :rowspan="2" :width="140" label="头像" align="center">
            <el-image style="width: 100px; height: 100px" :src="user.userPic" />
          </el-descriptions-item>
          <el-descriptions-item label="用户名">{{ user.name }}</el-descriptions-item>
          <el-descriptions-item label="手机">{{ user.phone }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ user.sex == 1 ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ user.createTime }}</el-descriptions-item>
          <el-descriptions-item label="所在服务区" >
            {{ areaList.province.label + areaList.province.value + "-" + areaList.city.label + areaList.city.value + "-" +
              areaList.county.label + areaList.county.value }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 个人细节信息展示的产品 -->
    <el-card style="margin-top: 30px;">
      <div>
        <el-row :gutter="16">
          <el-col :span="6">
            <div class="statistic-card">
              <el-statistic :value="myGoodsList.length + '种'">
                <template #title>
                  <div style="display: inline-flex; align-items: center">
                    我的商品
                    <el-tooltip effect="dark" content="你所有商品的种类数" placement="top">
                      <el-icon style="margin-left: 4px" :size="12">
                        <Warning />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>
              </el-statistic>
              <div class="statistic-footer">
                <div class="footer-item">
                  <span>你所有商品的种类数</span>
                </div>
              </div>
            </div>
          </el-col>

          <el-col :span="6">
            <div class="statistic-card">
              <el-statistic :value="moneyModel.balance + '元'">
                <template #title>
                  <div style="display: inline-flex; align-items: center">
                    我的资产
                    <el-tooltip effect="dark" content="在该系统中你的账户余额" placement="top">
                      <el-icon style="margin-left: 4px" :size="12">
                        <Warning />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>
              </el-statistic>
              <div class="statistic-footer">
                <div class="footer-item">
                  <span>在该系统中你的账户余额</span>
                </div>
              </div>
            </div>
          </el-col>

          <el-col :span="6">
            <div class="statistic-card">
              <el-statistic :value="myTransportation.length + '次'">
                <template #title>
                  <div style="display: inline-flex; align-items: center">
                    我的运输
                    <el-tooltip effect="dark" content="你下单运输的次数" placement="top">
                      <el-icon style="margin-left: 4px" :size="12">
                        <Warning />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>
              </el-statistic>
              <div class="statistic-footer">
                <div class="footer-item">
                  <span>你下单运输的次数</span>
                </div>
              </div>
            </div>
          </el-col>

          <el-col :span="6">
            <div class="statistic-card">
              <el-statistic :value="myDelivery.length + '次'" title="New transactions today">
                <template #title>
                  <div style="display: inline-flex; align-items: center">
                    我的配送
                    <el-tooltip effect="dark" content="你下单配送的次数" placement="top">
                      <el-icon style="margin-left: 4px" :size="12">
                        <Warning />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>
              </el-statistic>
              <div class="statistic-footer">
                <div class="footer-item">
                  <span>你下单配送的次数</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <h1 style="margin-left:26%;font-size: 36px;">{{ '北京时间：' + formatData }}</h1>
    </el-card>
  </div>
</template>



<style scoped>
:global(h2#card-usage ~ .example .example-showcase) {
  background-color: var(--el-fill-color) !important;
}

.el-statistic {
  --el-statistic-content-font-size: 28px;
}

.statistic-card {
  height: 100%;
  padding: 20px;
  border-radius: 4px;
  background-color: var(--el-bg-color-overlay);
}

.statistic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  font-size: 12px;
  color: var(--el-text-color-regular);
  margin-top: 16px;
}

.statistic-footer .footer-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistic-footer .footer-item span:last-child {
  display: inline-flex;
  align-items: center;
  margin-left: 4px;
}

.green {
  color: var(--el-color-success);
}

.red {
  color: var(--el-color-error);
}
</style>