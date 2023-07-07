<script setup lang="ts">

import { request } from "~/utils/request";
import { ElNotification } from "element-plus";
import { h, onMounted, reactive, watch } from "vue";
import { useStationsStore } from "~/stores/stations";
import { parseDate } from "~/utils/date";
import { useRouter } from "vue-router";
import { OrderDetailData } from "~/utils/interfaces";

const router = useRouter()
const stations = useStationsStore()

const props = defineProps({
  id: Number,
})

let orderDetail = reactive<{ data: OrderDetailData }>({
  data: {
    id: 0,
    train_id: 0,
    seat: '',
    status: '',
    created_at: '',
    start_station_id: 0,
    end_station_id: 0,
    departure_time: '',
    arrival_time: '',
    price:0
  },
})


let train = reactive<{ data: { name?: string } }>({
  data: {}
});

let discount=0;
let isChecked=false;
let integral = 0;

const getOrderDetail = () => {
  request({
    url: `/order/${props.id}`,
    method: 'GET',
  }).then(res => {
    orderDetail.data = res.data.data
    console.log(orderDetail.data)
  }).catch(err => {
    console.log(err)
    if (err.response?.data.code == 100003) {
      router.push('/login')
    }
    ElNotification({
      offset: 70,
      title: 'getOrder错误',
      message: h('i', { style: 'color: teal' }, err.response?.data.msg),
    })
  })
}

const getTrain = () => {
  console.log("getTrain")
  if (orderDetail.data) {
    request({
      url: `/train/${orderDetail.data.train_id}`,
      method: 'GET'
    }).then((res) => {
      train.data = res.data.data
      console.log(train)
    }).catch((error) => {
      ElNotification({
        offset: 70,
        title: 'getTrain错误(orderDetail)',
        message: h('error', { style: 'color: teal' }, error.response?.data.msg),
      })
      console.log(error)
    })
  }
}


const pay = (orderId: number) => {
  request({
    url: `/order/${orderId}/${isChecked}`,
    method: 'PATCH',
    data: {
      status: '已支付',
    },
  }).then((res) => {
    ElNotification({
      offset: 70,
      title: '支付成功',
      message: h('success', { style: 'color: teal' }, res.data.msg),
    })
    getOrderDetail()
    console.log(res)
  }).catch((error) => {
    if (error.response?.data.code == 100003) {
      router.push('/login')
    }
    ElNotification({
      offset: 70,
      title: '支付失败',
      message: h('error', { style: 'color: teal' }, error.response?.data.msg),
    })
    console.log(error)
  })
}

const calDiscount = (id:number,isChecked:boolean)=> {
    if (isChecked) {
        request({
            url: `/order/calPrice`,
            method: "GET",
            params: {
                orderId: id,
                isChecked: isChecked
            }
        })
        .then((res) => {
            // 处理请求成功的响应
            discount = res.data.data[0];
            integral = res.data.data[1];
            getOrderDetail();
            // 在这里使用 price_integ 进行后续操作
        }).catch((error) => {
            // 处理请求失败的情况
            console.error(error);
        });
    }else{
        discount=0;
        integral = 0;
        getOrderDetail();
    }
}

const cancel = (id: number) => {
    request({
    url: `/order/${id}/${isChecked}`,
    method: 'PATCH',
    data: {
      status: '已取消',
    }
  }).then((res) => {
    ElNotification({
      offset: 70,
      title: '取消成功',
      message: h('success', { style: 'color: teal' }, res.data.msg),
    })
    getOrderDetail()
    console.log(res)
  }).catch((error) => {
    if (error.response?.data.code == 100003) {
      router.push('/login')
    }
    ElNotification({
      offset: 70,
      title: '取消失败',
      message: h('error', { style: 'color: teal' }, error.response?.data.msg),
    })
    console.log(error)
  })
}




watch(orderDetail, () => {
  getTrain()
})

onMounted(() => {
  stations.fetch()
  getOrderDetail()
})

getOrderDetail()

</script>

<template>
  <div style="display: flex; flex-direction: column">

    <div style="margin-bottom: 2vh;">
      <el-button style="float:right" @click="getOrderDetail">
        刷新
      </el-button>
    </div>

    <div style="display: flex; justify-content: space-between;">
      <div>
        <el-text size="large" tag="b" type="primary">
          订单号:&nbsp;&nbsp;
        </el-text>
        <el-text size="large" tag="b">
          {{ props.id }}
        </el-text>
      </div>
      <div>
        <el-text size="large" tag="b" type="primary">
          创建日期:&nbsp;&nbsp;
        </el-text>
        <el-text size="large" tag="b" v-if="orderDetail.data">
          {{ parseDate(orderDetail.data.created_at) }}
        </el-text>
      </div>
    </div>

    <div>
      <el-text size="large" tag="b" type="primary">
        订单状态:&nbsp;&nbsp;
      </el-text>
      <el-text size="large" tag="b" v-if="orderDetail.data">
        {{ orderDetail.data.status }}
      </el-text>
    </div>
    <div style="margin-bottom: 2vh">
      <el-text size="large" tag="b" type="primary">
        车次信息:
      </el-text>
    </div>

    <el-descriptions :column="4" border>
      <el-descriptions-item :span="2" width="25%" align="center">
        <template #label>
          <el-text type="primary" tag="b" size="large">
            车次
          </el-text>
        </template>
        <el-text type="primary" tag="b" size="large">
          {{ train?.data?.name }}
        </el-text>
      </el-descriptions-item>
      <el-descriptions-item label="席位信息" :span="2" width="25%" align="center" v-if="orderDetail.data">
        {{ orderDetail.data.seat }}
      </el-descriptions-item>
      <el-descriptions-item label="出发站" :span="2" width="25%" align="center" v-if="orderDetail.data">
        {{ stations.idToName[orderDetail.data.start_station_id] ?? '未知站点' }}
      </el-descriptions-item>
      <el-descriptions-item label="到达站" :span="2" width="25%" align="center" v-if="orderDetail.data">
        {{ stations.idToName[orderDetail.data.end_station_id] ?? '未知站点' }}
      </el-descriptions-item>
      <el-descriptions-item label="出发时间" :span="2" width="25%" align="center" v-if="orderDetail.data">
        {{ parseDate(orderDetail.data.departure_time) }}
      </el-descriptions-item>
      <el-descriptions-item label="到达时间" :span="2" width="25%" align="center" v-if="orderDetail.data">
        {{ parseDate(orderDetail.data.arrival_time) }}
      </el-descriptions-item>
      <el-descriptions-item label="价格" :span="2" width="25%" align="center" v-if="orderDetail.data">
            {{ orderDetail.data.price*(1-discount)}}
        </el-descriptions-item>
      </el-descriptions>


    <div style="margin-top: 2vh" v-if="orderDetail.data && orderDetail.data.status === '等待支付'">
      <div>
          <el-checkbox v-model="isChecked" @change=calDiscount(orderDetail.data.id,isChecked)>使用积分</el-checkbox>
          <br/>
          <span v-if="isChecked">所需积分: {{ integral }}</span>
          <br/>
      </div>
      <div style="float:right;">
        <el-button type="danger" @click="cancel(orderDetail.data.id ?? -1)">
          取消订单
        </el-button>
        <el-button type="primary" @click="pay(orderDetail.data.id ?? -1)">
          支付订单
        </el-button>
      </div>
    </div>
    <div v-else-if="orderDetail.data && orderDetail.data.status === '已支付'" style="margin-top: 2vh">
      <div style="float:right;">
        <el-button @click="cancel(id ?? -1)">
          取消订单
        </el-button>
      </div>
    </div>

  </div>
</template>

<style scoped></style>