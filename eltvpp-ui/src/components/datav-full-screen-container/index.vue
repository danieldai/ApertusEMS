<template>
  <div id="ykite-full-screen-container" ref="fullScreenContainer">
    <template v-if="state.ready">
      <slot/>
    </template>
  </div>
</template>

<script lang="ts" setup>
import autoResize from '../../utils/autoresize/autoResize'
import {reactive, ref} from 'vue';

const fullScreenContainer = ref<HTMLElement | null>(null)

const state = reactive({
  allWidth: 0,
  scale: 0,
  datavRoot: '',
  ready: false,
})

const initConfig = () => {
  const {width, height} = screen

  state.allWidth = width

  if (fullScreenContainer.value) {
    fullScreenContainer.value.style.width = `${width}px`
    fullScreenContainer.value.style.height = `${height}px`
  }
}

const setAppScale = () => {
  const currentWidth = document.body.clientWidth
  const currentHeight = document.body.clientHeight
  if (fullScreenContainer.value){
    fullScreenContainer.value.style.height = currentHeight + 'px'
    fullScreenContainer.value.style.transform = `scale(${currentWidth / state.allWidth})`;
  }
}

const onResize = () => {
  setAppScale()
}

const afterAutoResizeMixinInit = () => {
  initConfig()
  setAppScale()
  state.ready = true
}

autoResize(fullScreenContainer, onResize, afterAutoResizeMixinInit)

</script>

<style lang="less">
#ykite-full-screen-container {
  position: fixed;
  top: 0px;
  left: 0px;
  overflow: hidden;
  transform-origin: left top;
  z-index: 999;
}
</style>
