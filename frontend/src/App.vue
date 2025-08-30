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
          :button-config="myButtonConfig"
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
      myButtonConfig: {
          upload: true,
          addText: true,
          restore: true, // 禁用局部恢复
          undo: true,
          redo: true,
          erase: true,   // 禁用擦除
          export: true,
          save: true,
          reset: true,
        },
      initialApiResponse: {
        RequestId: "D774D33D-F1CB-5A2C-A787-E0A2179239CE",
        Code: 200,
        Message: "Translation completed",
        Data: {
            FinalImageUrl: "http://39.170.17.212:9000/pictech-api/7d2c06fb52e2a0e6c405dd9a6a2467fc/20250827/1756276507625/20250827143507_191_final.png",
            InPaintingUrl: "http://39.170.17.212:9000/pictech-api/afc19672d337880aa0a30360d058859f/20250827/1756276507625/20250827143507_191_inpaint.png",
            SourceUrl: "http://39.170.17.212:9000/pictech-api/pic_9DD88A6D/2025/08/27/20250827143507_191_2aea6572.jpeg",
            TemplateJson: "{\"version\": \"5.3.0\", \"fabritor_schema_version\": 3, \"clipPath\": {\"type\": \"rect\", \"version\": \"5.3.0\", \"left\": 0, \"top\": 0, \"width\": 1000, \"height\": 1000, \"fill\": \"#ffffff\", \"selectable\": \"true\", \"hasControls\": \"true\"}, \"objects\": [{\"type\": \"rect\", \"version\": \"5.3.0\", \"originX\": \"left\", \"originY\": \"top\", \"left\": 100, \"top\": 100, \"width\": 800, \"height\": 800, \"fill\": \"#ffffff\", \"strokeWidth\": 1, \"strokeDashArray\": \"null\", \"strokeLineCap\": \"butt\", \"strokeDashOffset\": 0, \"strokeLineJoin\": \"miter\", \"strokeUniform\": \"true\", \"strokeMiterLimit\": 4, \"scaleX\": 1, \"scaleY\": 1, \"angle\": 0, \"flipX\": \"false\", \"flipY\": \"false\", \"opacity\": 1, \"shadow\": \"null\", \"visible\": \"true\", \"backgroundColor\": \"\", \"fillRule\": \"nonzero\", \"paintFirst\": \"stroke\", \"globalCompositeOperation\": \"source-over\", \"skewX\": 0, \"skewY\": 0, \"rx\": 0, \"ry\": 0, \"id\": \"pictech\", \"fabritor_desc\": \"pictech_108931c8-b72a-492d-a34c-5e0056c6d288\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-image\", \"version\": \"5.3.0\", \"left\": 100, \"top\": 100, \"width\": 800, \"height\": 800, \"id\": \"pictech_108931c8-b72a-492d-a34c-5e0056c6d288\", \"selectable\": \"true\", \"hasControls\": \"true\", \"imageBorder\": {}, \"objects\": [{\"type\": \"image\", \"version\": \"5.3.0\", \"originX\": \"center\", \"originY\": \"center\", \"left\": 0, \"top\": 0, \"width\": 800, \"height\": 800, \"clipPath\": {\"type\": \"rect\", \"version\": \"5.3.0\", \"originX\": \"center\", \"originY\": \"center\", \"left\": 0, \"top\": 0, \"width\": 800, \"height\": 800, \"fill\": \"rgb(0,0,0)\", \"stroke\": \"null\", \"strokeWidth\": 1, \"strokeDashArray\": \"null\", \"strokeLineCap\": \"butt\", \"strokeDashOffset\": 0, \"strokeLineJoin\": \"miter\", \"strokeUniform\": \"true\", \"strokeMiterLimit\": 4, \"scaleX\": 1, \"scaleY\": 1, \"angle\": 0, \"flipX\": \"false\", \"flipY\": \"false\", \"opacity\": 1, \"shadow\": \"null\", \"visible\": \"true\", \"backgroundColor\": \"\", \"fillRule\": \"nonzero\", \"paintFirst\": \"stroke\", \"globalCompositeOperation\": \"source-over\", \"skewX\": 0, \"skewY\": 0, \"rx\": 0, \"ry\": 0, \"selectable\": \"true\", \"hasControls\": \"true\", \"inverted\": \"false\", \"absolutePositioned\": \"false\"}, \"selectable\": \"true\", \"hasControls\": \"true\", \"src\": \"http://39.170.17.212:9000/pictech-api/afc19672d337880aa0a30360d058859f/20250827/1756276507625/20250827143507_191_inpaint.png\", \"crossOrigin\": \"null\", \"filters\": []}, {\"type\": \"rect\", \"version\": \"5.3.0\", \"originX\": \"center\", \"originY\": \"center\", \"left\": 0, \"top\": 0, \"width\": 800, \"height\": 800, \"fill\": \"#00000000\", \"paintFirst\": \"fill\", \"selectable\": \"true\", \"hasControls\": \"true\"}]}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 177, \"top\": 168, \"width\": 643, \"height\": 64, \"fill\": \"#000000\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 40, \"text\": \"\\u017baby i liczby o r\\u00f3wnej wadze\", \"textAlign\": \"center\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left177_top156_width643_height64***62ae8642-8310-11f0-bac2-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}, {\"type\": \"f-text\", \"version\": \"5.3.0\", \"left\": 271, \"top\": 251, \"width\": 454, \"height\": 31, \"fill\": \"#000303\", \"angle\": 0, \"scaleX\": 1, \"scaleY\": 1, \"fontFamily\": \"SourceHanSans\", \"fontWeight\": \"bold\", \"fontSize\": 22, \"text\": \"Operacje matematyczne od 1 do 10\", \"textAlign\": \"center\", \"lineHeight\": 1.3, \"styles\": [], \"pathAlign\": \"center\", \"minWidth\": 20, \"splitByGrapheme\": false, \"id\": \"left271_top247_width454_height31***62ae88ea-8310-11f0-bac2-141877572994\", \"selectable\": \"false\", \"hasControls\": \"false\"}]}"
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