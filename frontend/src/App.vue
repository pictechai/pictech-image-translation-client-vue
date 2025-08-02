<!-- src/App.vue -->

<template>
  <div id="app-container">
    <!-- 【Vue改造】左侧翻译记录栏 -->
    <aside id="history-sidebar">
      <h2>翻译记录</h2>
      <ul id="history-list">
        <!-- 【Vue改造】使用 v-for 动态渲染历史记录 -->
        <li
          v-for="(item, index) in translationHistory"
          :key="item.RequestId + '-' + index"
          :class="{ active: index === activeHistoryIndex }"
          @click="handleHistoryClick(index)"
        >
          <img :src="item.Data.SourceUrl" :alt="'历史记录' + index" />
        </li>
      </ul>
    </aside>

    <!-- 【Vue改造】右侧主内容区域，直接使用 ImageEditor 组件 -->
    <main id="main-content">
      <div id="editor-container-wrapper">
        <!--
          【Vue改造-组件通信】
          1. 使用 :initial-data="currentEditorData" 将当前选中的历史数据通过 prop 传递给子组件。
          2. 使用 @new-result="handleNewResult" 监听子组件触发的 newTranslationResult 事件。
          3. 使用 @save-result="handleSaveResult" 监听子组件触发的 saveTranslationResult 事件。
        -->
        <ImageEditor
          v-if="currentEditorData"
          :key="currentEditorData.RequestId"
          :initial-data="currentEditorData"
          @new-result="handleNewResult"
          @save-result="handleSaveResult"
        />
      </div>
    </main>
  </div>
</template>

<script>
// 【Vue改造】导入 ImageEditor 组件

export default {
  name: 'App',
  data() {
    return {
      // 【Vue改造-状态管理】将 translationHistory 和 activeHistoryIndex 作为组件的状态
      translationHistory: [],
      activeHistoryIndex: -1,
      // 【Vue改造-状态管理】用于传递给子组件的数据
      currentEditorData: null,
      // 【Vue改造-状态管理】页面加载时使用的初始数据
      initialApiResponse: {
        RequestId: "D774D33D-F1CB-5A2C-A787-E0A2179239CE",
        Code: 200,
        Message: "Translation completed",
        Data: {
          FinalImageUrl: "http://39.170.17.212:9000/pictech-api/7d2c06fb52e2a0e6c405dd9a6a2467fc/20250717/1752731972699/20250717135932_169_final.png",
          InPaintingUrl: "http://39.170.17.212:9000/pictech-api/afc19672d337880aa0a30360d058859f/20250717/1752731972699/20250717135932_169_inpaint.png",
          SourceUrl: "http://39.170.17.212:9000/pictech-api/pic_9DD88A6D/2025/07/17/20250717135932_169.png",
          TemplateJson: "{\"version\": \"5.3.0\", \"fabritor_schema_version\": 3, \"clipPath\": {\"type\": \"rect\", \"version\": \"5.3.0\", \"left\": 0, \"top\": 0, \"width\": 950, \"height\": 1017, \"fill\": \"#ffffff\", \"selectable\": \"true\", \"hasControls\": \"true\"}, \"objects\": [{\"type\": \"rect\", \"version\": \"5.3.0\", \"originX\": \"left\", \"originY\": \"top\", \"left\": 100, \"top\": 100, \"width\": 750, \"height\": 817, \"fill\": \"#ffffff\", \"strokeWidth\": 1, \"strokeDashArray\": \"null\", \"strokeLineCap\": \"butt\", \"strokeDashOffset\": 0, \"strokeLineJoin\": \"miter\", \"strokeUniform\": \"true\", \"strokeMiterLimit\": 4, \"scaleX\": 1, \"scaleY\": 1, \"angle\": 0, \"flipX\": \"false\", \"flipY\": \"false\", \"opacity\": 1, \"shadow\": \"null\", \"visible\": \"true\", \"backgroundColor\": \"\", \"fillRule\": \"nonzero\", \"paintFirst\": \"stroke\", \"globalCompositeOperation\": \"source-over\", \"skewX\": 0, \"skewY\": 0, \"rx\": 0, \"ry\": 0, \"id\": \"pictech\", \"fabritor_desc\": \"pictech_3c6b4f07-804d-4759-9b5e-f22726555986\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-image\", \"version\": \"5.3.0\", \"left\": 100, \"top\": 100, \"width\": 750, \"height\": 817, \"id\": \"pictech_3c6b4f07-804d-4759-9b5e-f22726555986\", \"selectable\": \"true\", \"hasControls\": \"true\", \"imageBorder\": {}, \"objects\": [{\"type\": \"image\", \"version\": \"5.3.0\", \"originX\": \"center\", \"originY\": \"center\", \"left\": 0, \"top\": 0, \"width\": 750, \"height\": 817, \"clipPath\": {\"type\": \"rect\", \"version\": \"5.3.0\", \"originX\": \"center\", \"originY\": \"center\", \"left\": 0, \"top\": 0, \"width\": 750, \"height\": 817, \"fill\": \"rgb(0,0,0)\", \"stroke\": \"null\", \"strokeWidth\": 1, \"strokeDashArray\": \"null\", \"strokeLineCap\": \"butt\", \"strokeDashOffset\": 0, \"strokeLineJoin\": \"miter\", \"strokeUniform\": \"true\", \"strokeMiterLimit\": 4, \"scaleX\": 1, \"scaleY\": 1, \"angle\": 0, \"flipX\": \"false\", \"flipY\": \"false\", \"opacity\": 1, \"shadow\": \"null\", \"visible\": \"true\", \"backgroundColor\": \"\", \"fillRule\": \"nonzero\", \"paintFirst\": \"stroke\", \"globalCompositeOperation\": \"source-over\", \"skewX\": 0, \"skewY\": 0, \"rx\": 0, \"ry\": 0, \"selectable\": \"true\", \"hasControls\": \"true\", \"inverted\": \"false\", \"absolutePositioned\": \"false\"}, \"selectable\": \"true\", \"hasControls\": \"true\", \"src\": \"http://39.170.17.212:9000/pictech-api/afc19672d337880aa0a30360d058859f/20250717/1752731972699/20250717135932_169_inpaint.png\", \"crossOrigin\": \"null\", \"filters\": []}, {\"type\": \"rect\", \"version\": \"5.3.0\", \"originX\": \"center\", \"originY\": \"center\", \"left\": 0, \"top\": 0, \"width\": 750, \"height\": 817, \"fill\": \"#00000000\", \"paintFirst\": \"fill\", \"selectable\": \"true\", \"hasControls\": \"true\"}]}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 149, \"top\": 151, \"width\": 82, \"height\": 25, \"fill\": \"#f9f9f8\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 8, \"text\": \"4. Bottle mouth brush\", \"textAlign\": \"left\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left149_top143_width82_height25***39770900-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 128, \"top\": 191, \"width\": 262, \"height\": 24, \"fill\": \"#0e0f0c\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 8, \"text\": \"Nylon bristles clean cup mouth threads without dead corners\", \"textAlign\": \"left\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left128_top183_width262_height24***39770cca-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 112, \"top\": 208, \"width\": 269, \"height\": 22, \"fill\": \"#171a19\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 8, \"text\": \"The gap can be filled with a bottle brush to turn it into milk\", \"textAlign\": \"left\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left112_top201_width269_height22***39770e1e-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 133, \"top\": 224, \"width\": 92, \"height\": 22, \"fill\": \"#252628\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 8, \"text\": \"Bottle brush handle.\", \"textAlign\": \"left\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left133_top217_width92_height22***39770f2c-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 670, \"top\": 272, \"width\": 133, \"height\": 23, \"fill\": \"#fbfcfa\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 8, \"text\": \"1. Storage box (base)\", \"textAlign\": \"left\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left670_top265_width133_height23***39771026-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 668, \"top\": 312, \"width\": 143, \"height\": 20, \"fill\": \"#14130f\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 8, \"text\": \"Partition storage for quick access\", \"textAlign\": \"left\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left668_top306_width143_height20***39771134-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 613, \"top\": 328, \"width\": 189, \"height\": 22, \"fill\": \"#1c1d1f\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 8, \"text\": \"Drain and store for easy cleaning.\", \"textAlign\": \"right\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left613_top321_width189_height22***39771224-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 175, \"top\": 495, \"width\": 341, \"height\": 26, \"fill\": \"#313132\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 15, \"text\": \"Partition storage, detachable cleaning\", \"textAlign\": \"left\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left175_top490_width341_height26***39771314-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 465, \"top\": 559, \"width\": 290, \"height\": 30, \"fill\": \"#343334\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 15, \"text\": \"Area 2: Bottle brush\", \"textAlign\": \"right\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left465_top552_width290_height30***39771404-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 465, \"top\": 600, \"width\": 290, \"height\": 30, \"fill\": \"#343335\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 15, \"text\": \"Zone 3: Nipper brush\", \"textAlign\": \"right\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left465_top593_width290_height30***397714f4-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 464, \"top\": 642, \"width\": 292, \"height\": 31, \"fill\": \"#343332\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 15, \"text\": \"Area 4: Bottle mouth brush\", \"textAlign\": \"right\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left464_top634_width292_height31***397715da-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 464, \"top\": 683, \"width\": 292, \"height\": 31, \"fill\": \"#313031\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 15, \"text\": \"Area 5: Straw brush\", \"textAlign\": \"right\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left464_top675_width292_height31***397716c0-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 463, \"top\": 724, \"width\": 296, \"height\": 30, \"fill\": \"#323132\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 15, \"text\": \"Area 6: Drain rod\", \"textAlign\": \"right\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left463_top717_width296_height30***397717a6-62d3-11f0-9c84-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}]}"
        }
      }
    };
  },
  // 【Vue改造-生命周期】在 mounted 钩子中加载初始数据
  mounted() {
    // 页面加载时，将初始数据添加到历史记录中
    this.addHistoryItem(this.initialApiResponse, true);
  },
  methods: {
    /**
     * 【Vue改造】添加或更新历史记录项
     * @param {object} apiResponse - API 响应数据
     * @param {boolean} isNewTranslation - 标记是否为一次全新的翻译（需要将数据加载到编辑器）
     */
    addHistoryItem(apiResponse, isNewTranslation = false) {
      const newRequestId = apiResponse.RequestId;
      const existingIndex = this.translationHistory.findIndex(item => item.RequestId === newRequestId);

      if (existingIndex !== -1) {
        // 如果 RequestId 已存在，则用新数据更新它（通常发生在保存操作后）
        // 使用 Vue.set 确保响应式更新
        this.$set(this.translationHistory, existingIndex, apiResponse);
        console.log(`[App.vue] 历史记录更新于索引 #${existingIndex}`);
      } else {
        // 如果是新的 RequestId，则添加到历史记录数组的开头
        this.translationHistory.unshift(apiResponse);
        this.activeHistoryIndex = 0; // 自动激活新项
        console.log(`[App.vue] 新增历史记录，当前激活索引: ${this.activeHistoryIndex}`);
      }

      // 如果是一次新的翻译（或首次加载），则更新需要传递给子组件的数据
      if (isNewTranslation) {
          this.currentEditorData = apiResponse;
      }
    },

    /**
     * 【Vue改造】处理左侧历史记录的点击事件
     * @param {number} index - 被点击项的索引
     */
    handleHistoryClick(index) {
      if (index !== this.activeHistoryIndex) {
        console.log(`[App.vue] 切换到历史记录 #${index}`);
        this.activeHistoryIndex = index;
        // 更新 currentEditorData，Vue 的响应式系统会自动将新 prop 传递给子组件
        this.currentEditorData = this.translationHistory[index];
      }
    },

    /**
     * 【Vue改造-组件通信】处理子组件发出的“新结果”事件
     * @param {object} payload - 从子组件传递过来的新翻译结果
     */
    handleNewResult(payload) {
        console.log('[App.vue] 从 ImageEditor 收到 new-result 事件:', payload);
        // 调用 addHistoryItem，并标记为新翻译，以加载到编辑器
        this.addHistoryItem(payload, true);
    },

    /**
     * 【Vue改造-组件通信】处理子组件发出的“保存结果”事件
     * @param {object} payload - 从子组件传递过来的保存后的数据
     */
    handleSaveResult(payload) {
        console.log('[App.vue] 从 ImageEditor 收到 save-result 事件:', payload);
        // 调用 addHistoryItem，但不标记为新翻译，仅更新历史记录中的数据
        this.addHistoryItem(payload, false);
    }
  },
};
</script>

<!-- src/App.vue -->

<style>
/* 【Vue改造】从原 index.html 复制过来的样式 */
body,
html {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
    'Helvetica Neue', Arial, sans-serif;
  background-color: #f0f2f5;
  overflow: hidden;
}

#app-container {
  display: flex;
  height: 100vh;
}

#history-sidebar {
  width: 200px;
  flex-shrink: 0;
  background-color: #ffffff;
  border-right: 1px solid #dcdfe6;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

#history-sidebar h2 {
  font-size: 16px;
  color: #303133;
  margin: 0;
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

#history-list {
  list-style-type: none;
  padding: 8px;
  margin: 0;
  overflow-y: auto;
  flex-grow: 1;
}

#history-list li {
  padding: 5px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 8px;
  border: 2px solid transparent;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80px;
}

#history-list li.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

#history-list li:hover {
  background-color: #f5f7fa;
}

#history-list li img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 4px;
  background-color: #e4e7ed;
}

#main-content {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f0f2f5;
}

#editor-container-wrapper {
  width: 100%;
  height: 100%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  /* 【Vue改造】让子组件能100%填充 */
  position: relative;

  /* 【关键修复】将 wrapper 自身设为 flex 容器 */
  /* 这会强制其唯一的子元素 <ImageEditor> 自动撑满它的所有空间 */
  display: flex;
}
</style>