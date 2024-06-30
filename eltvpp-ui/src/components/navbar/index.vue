<template>
  <div class="navbar">
    <div class="left-side">
      <a-space>
        <template v-if="!topMenu && appStore.device === 'mobile'">
          <component class="menu-icon" size="20" :is="drawerVisible ? 'icon-menu-fold' : 'icon-menu-unfold'"
            @click="toggleDrawerMenu" />
        </template>
        <!-- <a-select v-model="companyValue" :trigger-props="{ autoFitPopupMinWidth: true }" size="large" placeholder="请选择站点"
          @change="companyChange">
          <a-option v-for="item in companyList" :key="item.id" :value="item.id" :label="item.deptName" />
        </a-select> -->
        <a-tree-select :dropdown-style="{}" allow-search :allow-clear="false" v-model="companyValue" :data="companyList"
          @change="companyChange" placeholder="请选择站点" :fieldNames="{
            key: 'id', title: 'deptName', children: 'children'
          }" :filter-tree-node="filterTreeNode"></a-tree-select>
      </a-space>
    </div>
    <div class="center-side">
      <Menu v-if="topMenu" />
    </div>
    <ul class="right-side">
      <!-- <li>
        <a-tooltip :content="$t('settings.search')">
          <a-button class="nav-btn" type="outline" :shape="'circle'">
            <template #icon>
              <icon-search />
            </template>
          </a-button>
        </a-tooltip>
      </li> -->
      <li>
        <a-tooltip :content="$t('settings.language')">
          <a-button
              class="nav-btn"
              type="outline"
              :shape="'circle'"
              @click="setDropDownVisible"
          >
            <template #icon>
              <icon-language/>
            </template>
          </a-button>
        </a-tooltip>
        <a-dropdown trigger="click" @select="changeLocale as any">
          <div ref="triggerBtn" class="trigger-btn"></div>
          <template #content>
            <a-doption
                v-for="item in locales"
                :key="item.value"
                :value="item.value"
            >
              <template #icon>
                <icon-check v-show="item.value === currentLocale"/>
              </template>
              {{ item.label }}
            </a-doption>
          </template>
        </a-dropdown>
      </li>
      <li>
        <a-tooltip :content="theme === 'light'
          ? $t('settings.navbar.theme.toDark')
          : $t('settings.navbar.theme.toLight')
          ">
          <a-button class="nav-btn" type="outline" :shape="'circle'" @click="handleToggleTheme">
            <template #icon>
              <icon-moon-fill v-if="theme === 'dark'" />
              <icon-sun-fill v-else />
            </template>
          </a-button>
        </a-tooltip>
      </li>
      <!-- <li>
        <a-tooltip :content="$t('settings.navbar.alerts')">
          <div class="message-box-trigger">
            <a-badge :count="9" dot>
              <a-button
                  class="nav-btn"
                  type="outline"
                  :shape="'circle'"
                  @click="setPopoverVisible"
              >
                <icon-notification/>
              </a-button>
            </a-badge>
          </div>
        </a-tooltip>
        <a-popover
            trigger="click"
            :arrow-style="{ display: 'none' }"
            :content-style="{ padding: 0, minWidth: '400px' }"
            content-class="message-popover"
        >
          <div ref="refBtn" class="ref-btn"></div>
          <template #content>
            <message-box/>
          </template>
        </a-popover>
      </li> -->
      <li>
        <a-tooltip :content="$t('settings.navbar.toLab')" position="bottom">
          <a-button class="nav-btn" type="outline" :shape="'circle'" @click="toggleLab">
            <template #icon>
              <icon-skin />
            </template>
          </a-button>
        </a-tooltip>
      </li>
      <li>
        <a-tooltip :content="isFullscreen
          ? $t('settings.navbar.screen.toExit')
          : $t('settings.navbar.screen.toFull')
          ">
          <a-button class="nav-btn" type="outline" :shape="'circle'" @click="toggleFullScreen">
            <template #icon>
              <icon-fullscreen-exit v-if="isFullscreen" />
              <icon-fullscreen v-else />
            </template>
          </a-button>
        </a-tooltip>
      </li>
      <!-- <li>
        <a-tooltip :content="$t('settings.title')">
          <a-button
              class="nav-btn"
              type="outline"
              :shape="'circle'"
              @click="setVisible"
          >
            <template #icon>
              <icon-settings/>
            </template>
          </a-button>
        </a-tooltip>
      </li> -->
      <li>
        <a-dropdown trigger="click">
          <a-avatar :size="32" :style="{ marginRight: '8px', cursor: 'pointer' }">
            <img :alt="userName" :src="avatar" />
          </a-avatar>
          <template #content>
            <!-- <a-doption>
              <a-space @click="switchRoles">
                <icon-tag/>
                <span>
                  {{ $t('messageBox.switchRoles') }}
                </span>
              </a-space>
            </a-doption>
            <a-doption>
              <a-space @click="$router.push({ name: 'Info' })">
                <icon-user/>
                <span>
                  {{ $t('messageBox.userCenter') }}
                </span>
              </a-space>
            </a-doption>
            <a-doption>
              <a-space @click="$router.push({ name: 'Setting' })">
                <icon-settings/>
                <span>
                  {{ $t('messageBox.userSettings') }}
                </span>
              </a-space>
            </a-doption> -->
            <a-doption>
              <a-space @click="handleLogout">
                <icon-export />
                <span>
                  {{ $t('messageBox.logout') }}
                </span>
              </a-space>
            </a-doption>
          </template>
        </a-dropdown>
      </li>
    </ul>
  </div>
  <!-- 换肤抽屉-->
  <a-drawer :width="340" :visible="labVisible" @cancel="handleLabCancel" unmountOnClose>
    <template #title>
      {{ $t('settings.nav.lab.themeDrawer.title') }}
    </template>
    <div>
      {{ $t('settings.nav.lab.themeDrawer.content') }}
    </div>
    <div class="lab-box">
      <a-tooltip :content="item.content" v-for="(item, index) in labList" :key="index">
        <div :style="{ background: item.background, color: 'white' }" @click="handleChangeLab(item, index)">
          <icon-check v-if="labRadio === index" />
        </div>
      </a-tooltip>
    </div>
  </a-drawer>
</template>

<script lang="ts" setup>
import Menu from '@/components/menu/index.vue'
import useLocale from '@/hooks/locale'
import useUser from '@/hooks/user'
import { LOCALE_OPTIONS } from '@/locale'
import { useAppStore, useCompanyStore, useUserStore } from '@/store'
import { Message } from '@arco-design/web-vue'
import { useDark, useFullscreen, useToggle } from '@vueuse/core'
import { computed, inject, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { changeTheme } from '@/utils/changeTheme'
import { findById } from '@/utils/ruoyi'

defineProps({
  drawerVisible: {
    type: Boolean,
    default: false
  }
})
const appStore = useAppStore()
const userStore = useUserStore()
const companyStore = useCompanyStore()
// 选择的机构, 机构列表
const { companyValue, companyList } = storeToRefs(companyStore)
const { setCompanyList, updateSelectCompany, updateCompanyValue } = companyStore
// 设置公司列表
setCompanyList()
const { logout } = useUser()
const { changeLocale, currentLocale } = useLocale()
const { isFullscreen, toggle: toggleFullScreen } = useFullscreen()
const locales = [...LOCALE_OPTIONS]
const avatar = computed(() => {
  return loadImage();
})
const userName = computed(() => {
  return userStore.userInfo.user.nickName;
})
/**
 * 加载图片
 */
const loadImage = () => {
  const result = userStore.userInfo.user.id % 80;
  const path = `/src/assets/heads/head_${result + 1}.jpg`;
  const modules: Record<string, any> = import.meta.glob("@/assets/heads/*.{png,svg,jpg,jpeg}", { eager: true });
  if (modules[path]) {
    return modules[path].default;
  } else {
    // 地址错误
    console.error("头像地址错误");
  }
}
/**
 * 左补零
 * @param val 值
 * @param targetLength 长度
 */
const padLeft = (val: number, targetLength: number) => {
  return val.toString().padStart(targetLength, '0');
}
const theme = computed(() => {
  return appStore.theme
})
const topMenu = computed(() => appStore.topMenu && appStore.menu)
const isDark = useDark({
  selector: 'body',
  attribute: 'arco-theme',
  valueDark: 'dark',
  valueLight: 'light',
  storageKey: 'arco-theme',
  onChanged(dark: boolean) {
    // overridden default behavior
    appStore.toggleTheme(dark)
  },
})
const toggleTheme = useToggle(isDark)
const handleToggleTheme = () => {
  toggleTheme()
}
const setVisible = () => {
  appStore.updateSettings({ globalSettings: true })
}
const refBtn = ref()
const triggerBtn = ref()
const setPopoverVisible = () => {
  const event = new MouseEvent('click', {
    view: window,
    bubbles: true,
    cancelable: true,
  })
  refBtn.value.dispatchEvent(event)
}


/**
 * 站点搜索
 * @param searchValue
 * @param nodeData
 */
const filterTreeNode = (searchValue: any, nodeData: any) => {
  return nodeData.deptName.toLowerCase().indexOf(searchValue.toLowerCase()) > -1;
}

const reload = inject('reload') as () => void

// 机构下拉框change
const companyChange = (val: any) => {
  const findData = findById(companyList.value, val);
  updateCompanyValue(val)
  updateSelectCompany({ ...findData })
  reload();
}

const handleLogout = () => {
  logout()
}
// 切换主题 弹框
const labVisible = ref<boolean>(false)
const labRadio = ref<number>(0)
// 主题列表
const labList = ref([{
  content: '默认',
  url: 'https://unpkg.com/@arco-themes/vue-ykite-front/css/arco.css',
  background: '#015a45'
}, {
  content: '橘色',
  url: 'https://unpkg.com/@arco-themes/vue-chengsezhuti/css/arco.css',
  background: '#FF9047'
}])
// 打开主题弹窗
const toggleLab = () => {
  labVisible.value = true
}
// 确认切换主题
const handleChangeLab = (item: any, index: number) => {
  if (index === labRadio.value) return
  labRadio.value = index
  changeTheme(item.url)
}
// 关闭主题弹窗
const handleLabCancel = () => {
  labVisible.value = false
}
const setDropDownVisible = () => {
  const event = new MouseEvent('click', {
    view: window,
    bubbles: true,
    cancelable: true,
  })
  triggerBtn.value.dispatchEvent(event)
}
const switchRoles = async () => {
  const res = await userStore.switchRoles()
  Message.success(res as string)
}
const toggleDrawerMenu = inject('toggleDrawerMenu') as () => void


</script>

<style scoped lang="less">
.navbar {
  display: flex;
  justify-content: space-between;
  height: 100%;
  background-color: var(--color-bg-2);
  border-bottom: 1px solid var(--color-border);
}

.left-side {
  display: flex;
  align-items: center;
  padding-left: 10px;
}

.center-side {
  flex: 1;
}

.right-side {
  display: flex;
  padding-right: 20px;
  list-style: none;

  :deep(.locale-select) {
    border-radius: 20px;
  }

  li {
    display: flex;
    align-items: center;
    padding: 0 10px;
  }

  a {
    color: var(--color-text-1);
    text-decoration: none;
  }

  .nav-btn {
    color: rgb(var(--gray-8));
    font-size: 16px;
    border-color: rgb(var(--gray-2));
  }

  .trigger-btn,
  .ref-btn {
    position: absolute;
    bottom: 14px;
  }

  .trigger-btn {
    margin-left: 14px;
  }
}

.message-popover {
  .arco-popover-content {
    margin-top: 0;
  }
}

:deep(.arco-select-view-single) {
  border: none;
  background-color: transparent;
  border-radius: 0;

  .arco-select-view-suffix .arco-select-view-icon svg {
    font-size: 18px
  }
}

.lab-box {
  margin-top: 10px;
  display: flex;
  align-items: center;

  div {
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    margin-right: 10px;
    width: 20px;
    height: 20px;
    border-radius: 3px;
  }
}

.menu-icon {
  cursor: pointer;
  color: var(--color-text-1)
}

</style>
