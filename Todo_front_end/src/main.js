import {
	createSSRApp
} from "vue";
import App from "./App.vue";

// 不再直接导入组件
// import uniPopup from '@dcloudio/uni-ui/lib/uni-popup/uni-popup.vue'
// import uniPopupDialog from '@dcloudio/uni-ui/lib/uni-popup-dialog/uni-popup-dialog.vue'

// 导入API和工具
import api from './utils/api.js';
import storage from './utils/storage.js';
import * as utils from './utils/index.js';

export function createApp() {
	const app = createSSRApp(App);
	
	// 不再直接注册组件
	// app.component('uni-popup', uniPopup);
	// app.component('uni-popup-dialog', uniPopupDialog);
	
	// 将API和工具挂载到全局属性
	app.config.globalProperties.$api = api;
	app.config.globalProperties.$storage = storage;
	app.config.globalProperties.$utils = utils;
	
	return {
		app,
	};
}
