import { Notification } from '@arco-design/web-vue'
// 切换主题
export const changeTheme = function(href:string):any {
  Notification.info({
    title: '正在切换主题',
    content: '',
    id: 'your_id'
  })
  const themeLink = document.createElement('link');
  themeLink.setAttribute('id','pro-custom-theme')
  themeLink.setAttribute('type','text/css');
  themeLink.setAttribute('rel','stylesheet');
  themeLink.setAttribute('href',href);
  // 获取标签
  const labDom =<HTMLLinkElement> document.querySelector('#pro-custom-theme')
  // 如果有标签 直接替换href
  if (labDom) {
    labDom.href = href
    Notification.success({
      id: 'your_id',
      title: '切换完毕',
      content: '',
    });
    return
  }
  // 没有新增一个
  document.head.appendChild(themeLink);
  themeLink.onload = () => {
    Notification.success({
      id: 'your_id',
      title: '切换完毕',
      content: '',
    });
  }
  themeLink.onerror = () => {
    Notification.error({
      id: 'your_id',
      title: '切换失败',
      content: '',
    })
  }
}
