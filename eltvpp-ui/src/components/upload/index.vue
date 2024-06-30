<template>
  <!--  :action="(`${baseUrl}/file/upload`)"-->
  <a-upload
      :action="(`${baseUrl}/file/upload`)"
      :fileList="file ? [file] : []"
      :show-file-list="false"
      :headers="headers"
      @before-upload="beforeUpload"
      @change="onChange"
      @progress="onProgress"
      @success="handleUploadSuccess"
  >
    <template #upload-button>
      <div :class="`arco-upload-list-item${ file && file.status === 'error' ? ' arco-upload-list-item-error' : ''}`">
        <div v-if="imgUrl">
          <div class="arco-upload-list-picture custom-upload-avatar">
            <img :src="imgUrl" width="80" height="80" alt=""/>
            <div class="arco-upload-list-picture-mask">
              <IconEdit/>
            </div>
          </div>
        </div>
        <div class="arco-upload-picture-card" v-else>
          <div class="arco-upload-picture-card-text">
            <IconPlus/>
            <div style="margin-top: 10px; font-weight: 600">上传</div>
          </div>
        </div>
      </div>
    </template>
  </a-upload>
</template>

<script setup lang="ts">
import {IconEdit, IconPlus} from '@arco-design/web-vue/es/icon';
import {FileItem} from '@arco-design/web-vue'
import {notification} from "@/hooks/my-design";
import {ref} from 'vue';
import {getToken} from "@/utils/auth";

const props = defineProps({
  imgUrl: {
    type: String,
    default: '',
  },
  upLoadType: {
    type: Number,
    default: 0,
  },
  // 限制图片大小-默认2兆
  size: {
    type: Number,
    default: 2048,
  },
  // 限制图片类型-默认jpg,png,jpeg
  imgType: {
    type: Array,
    default: () => {
      return ['jpg', 'png', 'jpeg']
    },
  },
  // 是否限制图片宽高
  limitWH: {
    type: Boolean,
    default: false,
  },
  // 限制图片宽-默认1024
  maxWidth: {
    type: Number,
    default: 1024,
  },
  // 限制图片高-默认1024
  maxHeight: {
    type: Number,
    default: 1024,
  }
});
const emit = defineEmits(['upload']);
//路径
const baseUrl = import.meta.env.VITE_API_BASE_URL;
//请求头
const headers: Record<string, string> = {
  'Authorization': `Bearer ${getToken()}`
}
const file = ref();

const onChange = (_, currentFile) => {
  file.value = {
    ...currentFile,
    // url: URL.createObjectURL(currentFile.file),
  };
};
const onProgress = (currentFile) => {
  file.value = currentFile;
};
// 上传前校验
const beforeUpload = (file: any): boolean => {
  console.log(file);
  if (!file) return false;
  const fileExtension = file.type.slice(file.type.indexOf("/") + 1);
  console.log(fileExtension)
  const isAllowedType = props.imgType.includes(fileExtension);

  if (!isAllowedType) {
    notification('error', '图片格式错误');
    return false
  }

  const maxSizeInMB = props.size;
  const fileSizeInMB = file.size / (1024 * 1024);

  if (fileSizeInMB > maxSizeInMB) {
    notification('error', `文件大小不得超过${maxSizeInMB}M`);
    return false
  }
  return true
};
const handleUploadSuccess = (fileItem: FileItem) => {
  emit('upload', fileItem.response)
}

</script>

<style scoped>

</style>
