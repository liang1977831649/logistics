<script setup>
//=========================================引用区========================================
import { ref } from "vue";
import { getMoneyServer } from "@/api/moneyApi"
import payPng from "@/assets/image/pay2.png"
import { ElNotification } from "element-plus";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus"
import { payRecordServer } from "@/api/paymentRecord"
//=========================================数据区========================================
const moneyModel = ref({})
const payMentRecord = ref({})
const dialogVisibleOfPay = ref(false)

const emit = defineEmits(['event']) //子组件调用父组件方法

//=========================================方法区========================================


//支付弹窗和服务
const payWindows = async () => {
  payMentRecord.value = JSON.parse(JSON.stringify(prop.payMentRecord));
  payMentRecord.value.payType = 1 + "";
  dialogVisibleOfPay.value = true
}

const confirmPay = () => {

  if (payMentRecord.value.payType == "2") {
    if (moneyModel.value.balance < payMentRecord.value.cost) {
      ElNotification.error('支付失败，余额不足');
      return
    }
    ElMessageBox.confirm(
      '你确认要支付吗?',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: "warning",
      })
      .then(async () => {
        await payRecordServer(payMentRecord)
        emit('event')//将成功的信号传到父组件
        ElNotification.success('支付成功')
      })
      .catch(() => {
        ElMessage.info('取消支付')
      })
    dialogVisibleOfPay.value = false;
  }


}

const cancelPay = () => {
  payMentRecord.value = {};
  dialogVisibleOfPay.value = false;
}

//获取金额
const getMoney = async () => {
  const result = await getMoneyServer();
  moneyModel.value = result.data;
}

const prop = defineProps({
  payMentRecord: {
    type: Object
  }
})

defineExpose({ payWindows })//将方法暴露出来，便于父组件调用

//切换支付状态
const changePayType = () => {
  getMoney()
}
//我已付款函数
const payAlready = () => {
  const loading = ElLoading.service({
    lock: true,
    text: '加载校验中...',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  setTimeout(async () => {
    console.log("payMentRecord.value=", payMentRecord.value);
    await payRecordServer(payMentRecord)
    loading.close();
    ElNotification.success("支付成功");
    dialogVisibleOfPay.value = false;
    emit('event')//将成功的信号传到父组件
  }, 2000)
}

</script>

<template>
  <div>
    <el-dialog v-model="dialogVisibleOfPay" title="支付" width="320px">
      <el-form-item>
        <el-text tag="b">{{ "所付金额：" + payMentRecord.cost + "元" }}</el-text>
      </el-form-item>
      <el-form :model="payMentRecord">
        <el-form-item prop="payType">
          <el-radio-group v-model="payMentRecord.payType" @change="changePayType">
            <el-radio value=1>微信付款</el-radio>
            <el-radio value=2>账户余额</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <el-image v-if="payMentRecord.payType == 1" :src="payPng" style="width: 240px;margin-left: 20px;" />
      <el-text v-else>{{ "账户金额：" + moneyModel.balance }}</el-text>

      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="payMentRecord.payType == '2'" type="primary" @click="confirmPay()">确认付款</el-button>
          <el-button v-else type="success" @click="payAlready()">我已付款</el-button>
          <el-button type="info" @click="cancelPay()">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style></style>