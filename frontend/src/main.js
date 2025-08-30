// src/main.js

import Vue from 'vue'
import App from './App.vue'

// 1. 导入你的插件包
import ImageEditorPlugin from 'vue-pic-tech-editor';

// 2. 【重要】导入插件打包好的CSS
// 这个路径会指向 node_modules/vue-pic-tech-editor/dist/vue-pic-tech-editor.css
import 'vue-pic-tech-editor/dist/vue-pic-tech-editor.css';

// 3. （可选）准备你的自定义API配置
// 用户需要根据他们自己的后端服务地址来填写这里
const userApiConfig = {
  UPLOAD_API: '/api/translate/upload',
  URL_API: '/api/translate/url',
  RESULT_API: '/api/translate/result',
  UPLOAD_EXPORT_IMG_API: '/api/translate/uploadExportedImage',
  SAVE_API: '/api/translate/save',
  IO_IN_PAINT_API: '/api/translate/iopaint',
  UPLOAD_IO_IN_PAINT_IMG_API: '/api/translate/uploadIoInpaintImage',
};

// 4. 使用 Vue.use() 来安装插件
// 并将用户的API配置作为选项传入
Vue.use(ImageEditorPlugin, {
  apiConfig: userApiConfig
});

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
}).$mount('#app')