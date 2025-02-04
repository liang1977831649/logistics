<script setup>
//======================================引用区====================================
import userInfoServer from "@/storage/userStorage"
import locationStorage from "@/storage/locationStorage"
import { EluiChinaAreaDht } from 'elui-china-area-dht'
import { ref } from "vue"
import { getGoodsListServer } from "@/api/goodsApi.js"
import { getMoneyServer} from "@/api/moneyApi"
import {getIncomeRoomCostServer} from "@/api/roomCost";
import {
  ArrowRight,
  CaretBottom,
  CaretTop,
  Warning,
} from '@element-plus/icons-vue'
import { getDriverListServer } from "@/api/driverApi.js"
import { getColdChainCarListServer } from "@/api/coldChainCarApi.js"
import { getTransportationListServer } from "@/api/transportationApi.js"
import { getDeliverList } from "@/api/deliveryApi"

//======================================数据区====================================
const user = userInfoServer().userInfo
const location = locationStorage().location
const chinaData = new EluiChinaAreaDht.ChinaArea().chinaAreaflat;
const areaList = ref({
    province: '',
    city: '',
    county: ''
})

const goodsList=ref([])
const goodsListSearch=ref({})
const goodsInRoomCount=ref(0);

const moneyModel = ref({
  id:'',
  userId:'',
  balance:0,
  carNum:''
})

const balance=ref(0);

const driverSearCh=ref({})
const driverNum=ref(0);

const carSearCh=ref({});
const carNum=ref(0)

const transportationSearch=ref({})
const transportationNum=ref(0)

const deliverySearch=ref({})
const deliveryNum=ref(0)
//======================================引用区====================================
//个人服务地区
const loadingLocation = () => {
    areaList.value.county = chinaData[location[2]];
    areaList.value.city = chinaData[location[1]];
    areaList.value.province = chinaData[location[0]];
    console.log(areaList.value);
}
loadingLocation()

//商品总量
const loadingGoods=async()=>{
   const result=await getGoodsListServer(goodsListSearch) ;
   goodsList.value=result.data.items;
   goodsList.value.forEach(function(item,index,array){
        if(item.status==3||item.status==4){
            goodsInRoomCount.value++;
        }
   })
   console.log("goodsInRoomCount=",goodsInRoomCount.value);
}
loadingGoods()

//个人资产
const getMoney = async () => {
    const result = await getMoneyServer();
  if(result.data!=null){
    moneyModel.value = result.data;
  }
}
getMoney()

//昨日收益
const getIncome=async()=>{
    const result=await getIncomeRoomCostServer();
    result.data.forEach(function(item,index,array){
        balance.value+=item.cost;
    })
    
}
getIncome()

//司机
const getDriver=async()=>{
    const result = await getDriverListServer(driverSearCh);
    driverNum.value=result.data.total;
}
getDriver()

//冷链车数量
const getCar=async()=>{
    const result=await getColdChainCarListServer(carSearCh);
    carNum.value=result.data.total;
}
getCar()

//运输单余量
const getTransportation=async()=>{
    transportationSearch.value.status=1;
    const result = await getTransportationListServer(transportationSearch);
    transportationNum.value=result.data.total;
}
getTransportation();

//待配送余量
const getDelivery=async()=>{
    deliverySearch.value.status=1;
    const result = await getDeliverList(deliverySearch);
    deliveryNum.value=result.data.total;
}
getDelivery();
</script>

<template>
    <div>
        <el-card>
            <div>
                <el-descriptions title="管理员个人信息" direction="vertical" :column="4" :size="size" border>
                    <el-descriptions-item label="用户名">{{ user.name }}</el-descriptions-item>
                    <el-descriptions-item label="入网Id">{{ user.id }}</el-descriptions-item>
                    <el-descriptions-item label="位置Id" :span="2">{{areaList.county.value}}</el-descriptions-item>
                    <el-descriptions-item label="服务地址">
                        {{ areaList.province.label + areaList.province.value + "-" + areaList.city.label +
                            areaList.city.value + "-" +
                            areaList.county.label + areaList.county.value }}
                    </el-descriptions-item>
                </el-descriptions>
            </div>
        </el-card>

        <!-- 个人细节信息展示的产品 -->
        <el-card style="margin-top: 20px;">
            <div>
                <el-row :gutter="16">
                    <el-col :span="6">
                        <div class="statistic-card">
                            <el-statistic :value=" goodsList.length+ '种'">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        农产品数量
                                        <el-tooltip effect="dark" content="系统所挂的所有农产品数量" placement="top">
                                            <el-icon style="margin-left: 4px" :size="12">
                                                <Warning />
                                            </el-icon>
                                        </el-tooltip>
                                    </div>
                                </template>
                            </el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item">
                                    <span>系统所挂的所有农产品数量</span>
                                </div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="statistic-card">
                            <el-statistic :value="goodsInRoomCount+ '种'">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        在库农产品
                                        <el-tooltip effect="dark" content="冷链库中农产品的数量" placement="top">
                                            <el-icon style="margin-left: 4px" :size="12">
                                                <Warning />
                                            </el-icon>
                                        </el-tooltip>
                                    </div>
                                </template>
                            </el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item">
                                    <span>冷链库中农产品的数量</span>
                                </div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="statistic-card">
                            <el-statistic :value="moneyModel.balance+ '元'">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        账户余额
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
                            <el-statistic :value="balance+'元'" title="New transactions today">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        昨日收益
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

        <!-- 其余信息 -->
        <el-card style="margin-top: 20px;">
            <div>
                <el-row :gutter="16">
                    <el-col :span="6">
                        <div class="statistic-card">
                            <el-statistic :value=" driverNum+ '人'">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        司机人数
                                        <el-tooltip effect="dark" content="该县域中心司机的人数" placement="top">
                                            <el-icon style="margin-left: 4px" :size="12">
                                                <Warning />
                                            </el-icon>
                                        </el-tooltip>
                                    </div>
                                </template>
                            </el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item">
                                    <span>该县域中心司机的人数</span>
                                </div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="statistic-card">
                            <el-statistic :value="carNum+ '辆'">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        冷链车数量
                                        <el-tooltip effect="dark" content="该县域中心冷链车数量" placement="top">
                                            <el-icon style="margin-left: 4px" :size="12">
                                                <Warning />
                                            </el-icon>
                                        </el-tooltip>
                                    </div>
                                </template>
                            </el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item">
                                    <span>该县域中心冷链车数量</span>
                                </div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="statistic-card">
                            <el-statistic :value="transportationNum+ '单'">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        待运输单量
                                        <el-tooltip effect="dark" content="客户已经下单但未运输" placement="top">
                                            <el-icon style="margin-left: 4px" :size="12">
                                                <Warning />
                                            </el-icon>
                                        </el-tooltip>
                                    </div>
                                </template>
                            </el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item">
                                    <span>客户已经下单但未运输</span>
                                </div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="6">
                        <div class="statistic-card">
                            <el-statistic :value="deliveryNum+'单'" title="New transactions today">
                                <template #title>
                                    <div style="display: inline-flex; align-items: center">
                                        待配送单量
                                        <el-tooltip effect="dark" content="客户已经下单但未配送" placement="top">
                                            <el-icon style="margin-left: 4px" :size="12">
                                                <Warning />
                                            </el-icon>
                                        </el-tooltip>
                                    </div>
                                </template>
                            </el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item">
                                    <span>客户已经下单但未配送</span>
                                </div>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
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