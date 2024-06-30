import { Notification } from "@arco-design/web-vue";

/**
 * 二次封装-消息提示框
 * @param res
 * @param content
 */
type ResponseType = { code: number, msg: string };

export const notification = (res: ResponseType | string, content?: string) => {
  const notificationType = typeof res === 'string' ? res : res.code === 200 ? 'success' : 'error';
  const messageContent = content || (typeof res === 'object' ? res.msg : '');

  Notification[notificationType]({
    title: '提示',
    content: messageContent,
    duration: 2000,
  });
};
