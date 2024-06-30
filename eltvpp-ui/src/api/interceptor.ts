import { useUserStore } from "@/store";
import { getToken, getTokenHandler } from "@/utils/auth";
import { Button, Message, Modal } from "@arco-design/web-vue";
import type { AxiosRequestConfig, AxiosResponse } from "axios";
import axios from "axios";
import { h } from "vue";

// 拦截器
export interface HttpResponse<T = unknown> {
  status: number;
  msg: string;
  code: number;
  data: T;
}

const ModalContent = {
  setup() {
    return () =>
      h("div", { class: "info-modal-content" }, [
        h(
          "span",
          { style: "margin-bottom: 10px;" },
          "登录状态已经过期，请重新登录！"
        ),
      ]);
  },
};

if (import.meta.env.VITE_API_BASE_URL) {
  axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL;
}

axios.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 在拦截器中添加Token信息
    const tokenHandler = getTokenHandler();
    const token = getToken();
    if (token && tokenHandler) {
      if (!config.headers) {
        config.headers = {};
      }
      config.headers[tokenHandler] = `Bearer ${token}`;
    }
    //30秒超时
    config.timeout = 30 * 1000 ;
    config.timeoutErrorMessage = "接口超时";
    return config;
  },
  (error) => {
    // do something
    return Promise.reject(error);
  }
);
// add response interceptors
axios.interceptors.response.use(
  (response: AxiosResponse<HttpResponse>) => {
    
    if (response.status == 401) {
      Modal.error({
        title: "系统提示",
        content: () => h(ModalContent),
        okText: "重新登录",
        async onOk() {
          const userStore = useUserStore();
          await userStore.logout();
          window.location.reload();
        },
      });
      return;
    }
    if(response.config.responseType ===  'blob' || response.config.responseType ===  'arraybuffer'){
      return response.data;
    }
    const res = response.data;
    if (res.code !== 200) {
      if (res.code == 401) {
        // Modal.error({
        //   title: "系统提示",
        //   content: () => h(ModalContent),
        //   okText: "重新登录",
        //   async onOk() {
        //     const userStore = useUserStore();
        //     await userStore.logoutCallBack();
        //     window.location.reload();
        //   },
        // });
        const userStore = useUserStore();
        userStore.logoutCallBack();
        window.location.reload();
        return;
      } else {
        // Message.error({
        //   content: res.msg || "系统未知错误，请反馈给管理员",
        //   duration: 5 * 1000,
        // });
        // return Promise.reject(
        //   new Error(res.msg || "系统未知错误，请反馈给管理员")
        // );
      }
    }

    return res;
  },
  (error) => {
    Message.error({
      content: error.msg || "后端接口异常",
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  }
);
