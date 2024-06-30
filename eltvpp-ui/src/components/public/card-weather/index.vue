<template>
    <iframe src="https://weather.seniverse.com/?token=011588dd-af7b-4d1c-bae5-0d87b390e91a" :style="iframeStyle"></iframe>
    <!--  <div id="tp-weather-widget" style="width: 100%;height: 400px"></div>-->
  </template>
  <script lang="ts" setup>
  const iframeStyle = {
    width: '100%',
    height: '430px',
    border: 'none',
    borderRadius: '5px'
  }
  </script>
  
  <style lang="less" scoped>
  
  </style>
  