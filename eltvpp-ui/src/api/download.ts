import { blobValidate, tansParams } from '@/utils/ruoyi';
import { Message } from '@arco-design/web-vue';
import { saveAs } from 'file-saver'
import axios from 'axios'
import errorCode from '@/utils/errorCode';

// 通用下载方法
export function download(url:any, params:any, filename:any) {  
  Message.loading({
    content:"正在下载数据，请稍候",
    duration:6000 * 1000
  });
  return axios.post(url, params, {
    transformRequest: [(params) => { return tansParams(params) }],
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    responseType: 'blob',
  }).then(async (data:any) => {
    const isBlob = await blobValidate(data);
    if (isBlob) {
      Message.clear();
      Message.success("导出成功");
      const blob = new Blob([data])
      saveAs(blob, filename)
    } else {
      const resText = await data.text();
      const rspObj:any = JSON.parse(resText);
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
      Message.error(errMsg);
    }
    
  }).catch((r) => {
    Message.clear();
    console.error(r)
    Message.error('下载文件出现错误，请联系管理员！')
  })
}