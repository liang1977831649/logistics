<script lang="ts">
export default {
    name: "Aside",
};
</script>

<script lang="ts" setup>
//=======================引用区==========================
import { ref ,shallowRef} from 'vue'
import Aside from "@/components/Aside.vue"
import {Magnet,
     Avatar, 
     HomeFilled,
     UserFilled,
     User,
     House,
     Watermelon,
     Connection,
     CirclePlus,
     Goods,
     Paperclip,
     Money,
     CircleCheck,
     Crop} from '@element-plus/icons-vue'
//=======================数据区==========================
//图标
const iconList = shallowRef({
    "Avatar": Avatar,
    "HomeFilled":HomeFilled,
    "UserFilled":UserFilled,
    "User":User,
    "Magnet":Magnet,
    "House":House,
    "Watermelon":Watermelon,
    "Connection":Connection,
    "CirclePlus":CirclePlus,
    "Goods":Goods,
    "Paperclip":Paperclip,
    "Money":Money,
    "CircleCheck":CircleCheck,
    "Crop":Crop
})
// 回应了父类组件传过来的tabs
const props = defineProps({
    tabs: {
        type: Array,
        required: true,
    },
});

//=======================方法区==========================
</script>

<template>
   <div v-for="item in tabs">
        <!-- 如果是一级菜单 -->
        <div v-if="item.menuType == 1">
            <el-menu-item :index=item.path>
                <el-icon>
                    <component :is='iconList[item.icon]' />
                </el-icon>
                <span>{{ item.menuName }}</span>
            </el-menu-item>
        </div>
        <!-- 如果是二级菜单 -->
        <div v-else>
            <el-sub-menu :index="item.path">
                <template #title>
                    <el-icon>
                        <component :is='iconList[item.icon]' />
                    </el-icon>
                    <span>{{ item.menuName }}</span>
                </template>
                <Aside v-if=item.menuList :tabs="item.menuList" />
            </el-sub-menu>
        </div>
    </div>
</template>

<style scoped>
.el-aside {
    background-color: #333744;
}
</style>