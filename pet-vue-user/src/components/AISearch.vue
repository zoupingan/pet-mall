<template>
  <div class="ai-chat-fab" @click="openPanel" v-show="!visible">
    <el-icon :size="24" color="white">
      <ChatDotRound />
    </el-icon>
    <span v-if="unread > 0" class="fab-badge">{{ unread }}</span>
  </div>

  <!-- 面板改用 v-show，不再销毁 DOM -->
  <div
    :class="[
      'ai-chat-panel',
      {
        fullscreen: isFullscreen,
        'panel-enter': panelEnter,
        'panel-leave': panelLeave,
      },
    ]"
    v-show="panelVisible"
  >
    <div class="panel-header">
      <div class="header-avatar">
        <el-icon :size="18" color="white">
          <Avatar />
        </el-icon>
      </div>
      <div class="header-info">
        <span class="header-title">AI智能在线搜索助手</span>
        <span class="header-sub"><i class="online-dot" />在线</span>
      </div>
      <div class="header-actions">
        <el-icon class="action-icon" @click="clearMessages">
          <Delete />
        </el-icon>
        <el-icon class="action-icon" @click="toggleFullscreen">
          <FullScreen v-if="!isFullscreen" />
          <Aim v-else />
        </el-icon>
        <el-icon class="action-icon" @click.stop="closePanel">
          <Close />
        </el-icon>
      </div>
    </div>

    <div class="message-list" ref="messageListRef">
      <div
        v-for="(msg, index) in messages"
        v-show="
          !(msg.role === 'assistant' && !msg.content && (waiting || streaming))
        "
        :key="index"
        :class="['msg-row', msg.role]"
      >
        <div class="msg-avatar">
          <el-icon v-if="msg.role === 'assistant'">
            <Cpu />
          </el-icon>
          <el-icon v-else>
            <User />
          </el-icon>
        </div>
        <!-- assistant 用 v-html 渲染 md，user 保持纯文本 -->
        <div v-if="msg.role === 'assistant'" class="bubble assistant">
          <div class="markdown-body" v-html="renderMd(msg.content)"></div>
          <span
            v-if="index === messages.length - 1 && streaming"
            class="cursor-blink"
            >|</span
          >
        </div>
        <div v-else class="bubble user">
          {{ msg.content }}
        </div>
      </div>
      <div
        class="msg-row assistant"
        v-if="waiting || (streaming && !messages[messages.length - 1]?.content)"
      >
        <div class="msg-avatar">
          <el-icon>
            <Cpu />
          </el-icon>
        </div>
        <div class="bubble assistant typing-bubble">
          <span class="typing-text">联网搜索中，请稍后</span>
          <span class="typing-dots" aria-hidden="true">
            <span class="dot" />
            <span class="dot" />
            <span class="dot" />
          </span>
        </div>
      </div>
      <div class="msg-row assistant" v-if="suggesting">
        <div class="msg-avatar">
          <el-icon>
            <Cpu />
          </el-icon>
        </div>
        <div class="bubble assistant typing-bubble">
          <span class="typing-text">正在生成推荐问题</span>
          <span class="typing-dots" aria-hidden="true">
            <span class="dot" />
            <span class="dot" />
            <span class="dot" />
          </span>
        </div>
      </div>
    </div>

    <div class="composer-area">
      <div class="input-area">
        <el-input
          v-model="inputText"
          type="textarea"
          :autosize="{ minRows: 1, maxRows: 4 }"
          placeholder="输入消息，Enter 发送"
          resize="none"
          @keydown.enter.exact.prevent="sendMessage"
          :disabled="streaming || waiting || suggesting"
          class="chat-input"
        />
        <el-button
          type="primary"
          :icon="Promotion"
          circle
          :disabled="!inputText.trim() || streaming || waiting || suggesting"
          @click="sendMessage"
          class="send-btn"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onBeforeUnmount } from "vue";
import {
  ChatDotRound,
  Avatar,
  Delete,
  Close,
  Cpu,
  User,
  Promotion,
  FullScreen,
  Aim,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import MarkdownIt from "markdown-it";
import { getApiUrl } from "@/config/api";

const API_URL = getApiUrl("/user/agent");
const GREETING =
  "你好！我是智能在线搜索助手，可以帮你搜索最新信息并总结回答 😊，输入你想了解的内容吧！";
const DEFAULT_SUGGESTIONS = [
  "今天有什么新闻",
  "今天合肥天气怎么样",
  "最新招聘会信息",
  "最新体育赛事",
  "北京时间",
];
// 本地闲聊/脏话场景的兜底回复，命中后直接在前端返回，不走后端搜索。
const LOCAL_CHAT_REPLIES = {
  greeting:
    "你好呀，我在呢。你可以直接告诉我想搜索的内容，比如新闻、天气、招聘会这些。",
  casual: "我在，想查什么直接发我就行。",
  profanity: "我们换种表达也没关系，你直接说想搜什么，我继续帮你查。",
};

const visible = ref(false); // 控制 FAB 显隐
const panelVisible = ref(false); // 控制面板 v-show（包含动画期间）
const panelEnter = ref(false);
const panelLeave = ref(false);
const isFullscreen = ref(false);
const inputText = ref("");
const streaming = ref(false);
const waiting = ref(false);
const suggesting = ref(false);
const unread = ref(0);
const messageListRef = ref(null);
const messages = ref([{ role: "assistant", content: GREETING }]);
const suggestions = ref([...DEFAULT_SUGGESTIONS]);
const md = new MarkdownIt();
let activeStream = null;

function toggleFullscreen() {
  isFullscreen.value = !isFullscreen.value;
  nextTick(scrollToBottom);
}

function openPanel() {
  visible.value = true;
  panelVisible.value = true;
  unread.value = 0;
  // 触发入场动画
  nextTick(() => {
    panelEnter.value = true;
    panelLeave.value = false;
    nextTick(scrollToBottom);
  });
}

onMounted(() => {
  window.addEventListener("open-ai-search", openPanel);
});

function closePanel() {
  // 1. 立即让 FAB 不可点击（pointer-events），避免穿透
  visible.value = false;
  // 2. 触发离场动画
  panelEnter.value = false;
  panelLeave.value = true;
  // 3. 动画结束后才真正隐藏面板
  setTimeout(() => {
    panelVisible.value = false;
    panelLeave.value = false;
    isFullscreen.value = false;
    // 动画彻底结束后才让 FAB 重新可见
    visible.value = false;
  }, 200);
}

function clearMessages() {
  // 清空会话时，同时恢复默认欢迎语和默认快捷问题。
  closeActiveStream();
  suggesting.value = false;
  messages.value = [{ role: "assistant", content: GREETING }];
  suggestions.value = [...DEFAULT_SUGGESTIONS];
}

function sendSuggestion(text) {
  // 快捷问题复用统一发送逻辑，避免维护两套入口。
  inputText.value = text;
  sendMessage();
}

async function sendMessage() {
  const text = inputText.value.trim();
  if (!text || streaming.value || waiting.value) return;

  closeActiveStream();
  messages.value.push({ role: "user", content: text });
  inputText.value = "";
  // 本地先拦截没有搜索意图的内容，避免出现“联网搜索中”的等待态。
  const localIntent = detectLocalIntent(text);
  if (localIntent) {
    messages.value.push({
      role: "assistant",
      content: LOCAL_CHAT_REPLIES[localIntent],
    });
    suggestions.value = [...DEFAULT_SUGGESTIONS];
    await nextTick(scrollToBottom);
    return;
  }

  waiting.value = true;
  streaming.value = true;
  suggesting.value = false;
  suggestions.value = [];
  await nextTick(scrollToBottom);

  messages.value.push({ role: "assistant", content: "" });
  const lastIdx = messages.value.length - 1;

  try {
    const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
    const sessionId = userInfo.id || "";
    const url = `${API_URL}?query=${encodeURIComponent(
      text
    )}&sessionId=${encodeURIComponent(sessionId)}&userId=${encodeURIComponent(
      sessionId
    )}`;
    const eventSource = new EventSource(url);
    activeEventSource = eventSource;
    let hasContent = false;

    eventSource.onmessage = (event) => {
      waiting.value = false;
      const data = event.data;

      if (data === "[DONE]" || data === "") {
        eventSource.close();
        activeEventSource = null;
        streaming.value = false;
        waiting.value = false;
        suggestions.value = [...DEFAULT_SUGGESTIONS];
        nextTick(scrollToBottom);
        return;
      }

      const processedData = data.replace(/\\n/g, "\n");
      messages.value[lastIdx].content += processedData;
      hasContent = true;
      nextTick(scrollToBottom);
    };

    eventSource.onerror = async (error) => {
      console.error("SSE Error:", error);
      eventSource.close();
      activeEventSource = null;

      const auth = localStorage.getItem("authentication");
      if (auth) {
        try {
          const checkRes = await fetch(
            `${API_URL}?query=${encodeURIComponent(text)}`,
            {
              method: "HEAD",
              headers: { Authorization: auth },
            }
          );
          if (checkRes.status === 401) {
            localStorage.removeItem("authentication");
            localStorage.removeItem("userInfo");
            ElMessage.error("登录已过期，请重新登录");
            setTimeout(() => {
              window.location.href = "/login";
            }, 1500);
            return;
          }
        } catch (e) {
          console.error("Error checking auth:", e);
        }
      }

      if (!hasContent && !messages.value[lastIdx].content) {
        messages.value[lastIdx].content = "暂无回复，请稍后再试";
      }
      suggestions.value = [...DEFAULT_SUGGESTIONS];
      waiting.value = false;
      streaming.value = false;
      nextTick(scrollToBottom);
    };
  } catch (error) {
    if (!messages.value[lastIdx].content) {
      messages.value[lastIdx].content = `请求失败: ${error.message}`;
    }
    suggestions.value = [...DEFAULT_SUGGESTIONS];
    waiting.value = false;
    streaming.value = false;
  }
}

function scrollToBottom() {
  // 每次消息变化后都滚动到底部，保证最新消息可见。
  if (messageListRef.value)
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
}

function renderMd(content) {
  if (!content) return "";

  let recommendationIndex = 0;
  const processed = content
    .split("\n")
    .map((line) => {
      const trimmed = line.trim();

      // 后端常用独占一行的加粗文本表示推荐项，将其改为编号标题。
      const recommendationTitle = trimmed.match(
        /^(?:(?:📌|🔹|🔸|💡|✨|✅|⭐)\s*)?\*{1,2}\s*(.+?)\s*\*{1,2}$/,
      );
      if (recommendationTitle) {
        recommendationIndex += 1;
        return `### ${recommendationIndex}. ${recommendationTitle[1].trim()}`;
      }

      // 保留后端已有的数字标题，并统一为三级标题。
      const emojiTitle = trimmed.match(/^(\d+)️⃣\s*(.+)$/);
      if (emojiTitle) {
        const title = emojiTitle[2].replace(/^\*{1,2}\s*|\s*\*{1,2}$/g, "");
        return `### ${emojiTitle[1]}. ${title}`;
      }

      // 统一常见项目符号，数字列表仍保留为有序列表。
      return line
        .replace(/^\s*[●○•]\s*/, "- ")
        .replace(/^\s*-(?!-)\s*/, "- ")
        .replace(/^\s*(📌|🔹|🔸|💡|✨|✅|⭐)\s*/, "#### ");
    })
    .join("\n");

  return md
    .render(processed)
    .replace(/<table>/g, '<div class="table-scroll"><table>')
    .replace(/<\/table>/g, "</table></div>");
}

function parseStreamEvent(raw) {
  try {
    return JSON.parse(raw || "{}");
  } catch {
    return {};
  }
}

let activeEventSource = null;
let activeStreamReader = null;

function closeActiveStream() {
  if (activeEventSource) {
    activeEventSource.close();
    activeEventSource = null;
  }
  if (activeStreamReader) {
    activeStreamReader.cancel();
    activeStreamReader = null;
  }
}

onBeforeUnmount(() => {
  window.removeEventListener("open-ai-search", openPanel);
  closeActiveStream();
});

function normalizeSuggestions(items) {
  // 推荐问题统一限制为 3 条；后端异常时回退到默认推荐。
  if (!Array.isArray(items)) {
    return [...DEFAULT_SUGGESTIONS];
  }

  const normalized = items
    .map((item) => String(item || "").trim())
    .filter(Boolean)
    .slice(0, 3);

  return normalized.length === 3 ? normalized : [...DEFAULT_SUGGESTIONS];
}

function detectLocalIntent(text) {
  // 轻量本地意图识别：把闲聊、寒暄和脏话在前端直接消化掉。
  const normalized = text
    .trim()
    .toLowerCase()
    .replace(/\s+/g, "")
    .replace(/[!！?？,，。~～…]+/g, "");

  if (!normalized) {
    return "casual";
  }

  // 问候语单独回复，比直接提示“请输入问题”更自然。
  const greetingPatterns = [/^(你好|您好|嗨|hi|hello|哈喽|在吗|在嘛|有人吗)$/];
  // 纯语气词或低信息量输入，不值得发到后端做联网搜索。
  const casualPatterns = [
    /^(哈)+$/,
    /^(哈哈)+$/,
    /^(呵)+$/,
    /^(呵呵)+$/,
    /^(呼)+$/,
    /^(呼呼)+$/,
    /^(嘿)+$/,
    /^(嘿嘿)+$/,
    /^(嗯)+$/,
    /^(哦)+$/,
    /^(噢)+$/,
    /^(啊)+$/,
    /^(呀)+$/,
    /^(好哦)+$/,
    /^(好噢)+$/,
    /^(收到)+$/,
    /^(ok)+$/,
    /^(okay)+$/,
    /^(okk)+$/,
    /^(hhh)+$/,
  ];
  // 常见脏话关键词，命中后给柔和引导，但不进入搜索流程。
  const profanityKeywords = [
    "傻逼",
    "煞笔",
    "沙比",
    "傻b",
    "sb",
    "脑残",
    "智障",
    "去死",
    "妈的",
    "他妈",
    "操你",
    "草你",
    "草泥马",
    "操泥马",
    "曹尼玛",
    "滚蛋",
    "废物",
  ];

  if (greetingPatterns.some((pattern) => pattern.test(normalized))) {
    return "greeting";
  }

  if (casualPatterns.some((pattern) => pattern.test(normalized))) {
    return "casual";
  }

  if (profanityKeywords.some((keyword) => normalized.includes(keyword))) {
    return "profanity";
  }

  return "";
}
</script>

<style scoped>
.ai-chat-fab {
  position: fixed;
  bottom: 28px;
  right: 28px;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
  box-shadow: 0 4px 20px rgba(255, 107, 53, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 9997;
  transition: transform 0.2s, box-shadow 0.2s;
}

.ai-chat-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 28px rgba(255, 107, 53, 0.65);
}

.fab-badge {
  position: absolute;
  top: 0;
  right: 0;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  border-radius: 9px;
  background: #f56c6c;
  border: 2px solid white;
  font-size: 11px;
  color: white;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-chat-panel {
  position: fixed;
  bottom: 96px;
  right: 28px;
  width: 600px;
  height: min(640px, calc(100vh - 112px));
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 48px rgba(255, 107, 53, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 9998;
  transform: scale(0.6) translateY(30px);
  opacity: 0;
  transform-origin: bottom right;
  pointer-events: none;
  transition: width 0.3s ease, height 0.3s ease, bottom 0.3s ease,
    right 0.3s ease, border-radius 0.3s ease;
}

/* 入场 */
.ai-chat-panel.panel-enter {
  animation: chatPopIn 0.28s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
  pointer-events: auto;
}

/* 离场 */
.ai-chat-panel.panel-leave {
  animation: chatPopOut 0.2s ease-in forwards;
  pointer-events: none;
}

.ai-chat-panel.fullscreen {
  bottom: 0;
  right: 0;
  width: 100vw;
  height: 100vh;
  border-radius: 0;
}

@keyframes chatPopIn {
  from {
    transform: scale(0.6) translateY(30px);
    opacity: 0;
    transform-origin: bottom right;
  }

  to {
    transform: scale(1) translateY(0);
    opacity: 1;
    transform-origin: bottom right;
  }
}

@keyframes chatPopOut {
  from {
    transform: scale(1) translateY(0);
    opacity: 1;
    transform-origin: bottom right;
  }

  to {
    transform: scale(0.6) translateY(20px);
    opacity: 0;
    transform-origin: bottom right;
  }
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
}

.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.header-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 14px;
  font-weight: 600;
  color: white;
}

.header-sub {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.75);
  display: flex;
  align-items: center;
  gap: 4px;
}

.online-dot {
  display: inline-block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #67c23a;
  box-shadow: 0 0 5px #67c23a;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-icon {
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  font-size: 16px;
  transition: color 0.15s;
}

.action-icon:hover {
  color: white;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 14px 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #fafafa;
}

.message-list::-webkit-scrollbar {
  width: 4px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #ff8c42;
  border-radius: 2px;
}

.msg-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.msg-row.user {
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #fff5f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 14px;
  color: #ff6b35;
}

.bubble {
  max-width: min(720px, 78%);
  padding: 10px 13px;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.bubble.assistant {
  background: white;
  color: #30343b;
  border-radius: 2px 12px 12px 12px;
  box-shadow: 0 5px 18px rgba(75, 45, 25, 0.06);
  border: 1px solid #f5dfd0;
}

.bubble.user {
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
  color: white;
  border-radius: 12px 2px 12px 12px;
}

.typing-bubble {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
}

.typing-text {
  color: #606266;
}

.typing-dots {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  min-width: 29px;
}

.dot {
  display: inline-block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #ff8c42;
  animation: dotBounce 1.4s infinite ease-in-out;
}

.dot:nth-child(2) {
  animation-delay: 0.16s;
}

.dot:nth-child(3) {
  animation-delay: 0.32s;
}

@keyframes dotBounce {
  0%,
  80%,
  100% {
    transform: translateY(0);
    background: #ff8c42;
  }

  40% {
    transform: translateY(-7px);
    background: #ff6b35;
  }
}

.cursor-blink {
  color: #ff6b35;
  animation: cursorBlink 0.7s infinite;
}

@keyframes cursorBlink {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0;
  }
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 10px 12px;
  background: white;
  flex-shrink: 0;
  border-top: 1px solid #ffe8d6;
}

.composer-area {
  background: white;
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.5;
  padding: 8px 10px;
  resize: none;
  border-color: #ffe8d6;
}

.chat-input :deep(.el-textarea__inner:focus) {
  border-color: #ff8c42;
}

.send-btn {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
  border: none;
}

.send-btn:hover {
  background: linear-gradient(135deg, #ff6b35, #ff5722);
}

@media (max-width: 480px) {
  .ai-chat-panel {
    right: 0;
    bottom: 0;
    width: 100vw;
    height: 70vh;
    border-radius: 16px 16px 0 0;
  }

  .ai-chat-fab {
    bottom: 20px;
    right: 20px;
  }
}

.markdown-body {
  min-width: 0;
}

.bubble.assistant :deep(p) {
  margin: 0 0 10px;
  line-height: 1.8;
  color: #42464d;
}

.bubble.assistant :deep(p:last-child) {
  margin-bottom: 0;
}

/* Markdown 渲染样式优化 */
.bubble.assistant :deep(h1) {
  font-size: 21px;
  line-height: 1.35;
  font-weight: 750;
  color: #24272c;
  margin: 4px 0 14px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f2d9c9;
}

.bubble.assistant :deep(h2) {
  font-size: 18px;
  line-height: 1.4;
  font-weight: 700;
  color: #30343b;
  margin: 18px 0 10px;
}

.bubble.assistant :deep(h3) {
  position: relative;
  font-size: 16px;
  line-height: 1.45;
  font-weight: 700;
  color: #d95421;
  margin: 18px 0 9px;
  padding: 9px 12px;
  border-left: 4px solid #ff753a;
  border-radius: 0 8px 8px 0;
  background: linear-gradient(90deg, #fff3eb 0%, rgba(255, 247, 242, 0.3) 100%);
}

.bubble.assistant :deep(h3:first-child) {
  margin-top: 2px;
}

.bubble.assistant :deep(h4) {
  font-size: 14px;
  line-height: 1.5;
  font-weight: 700;
  color: #c95b2b;
  margin: 14px 0 7px;
}

.bubble.assistant :deep(strong) {
  font-weight: 700;
  color: #c94e20;
}

.bubble.assistant :deep(em) {
  font-style: italic;
  color: #ff8c42;
}

.bubble.assistant :deep(ul),
.bubble.assistant :deep(ol) {
  margin: 7px 0 13px;
  padding-left: 22px;
}

.bubble.assistant :deep(li) {
  margin: 6px 0;
  padding-left: 3px;
  line-height: 1.75;
  color: #3d4249;
}

.bubble.assistant :deep(li::marker) {
  color: #ff6b35;
  font-weight: 700;
}

.bubble.assistant :deep(code) {
  background: #fff5f0;
  color: #ff6b35;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  font-family: Consolas, Monaco, "Courier New", monospace;
}

.bubble.assistant :deep(pre) {
  background: #fff5f0;
  border: 1px solid #ffe8d6;
  border-radius: 8px;
  padding: 10px;
  overflow-x: auto;
  margin: 8px 0;
}

.bubble.assistant :deep(pre code) {
  background: none;
  padding: 0;
  font-size: 12px;
  line-height: 1.5;
}

.bubble.assistant :deep(blockquote) {
  border-left: 4px solid #ff8c42;
  background: #fff5f0;
  margin: 12px 0;
  padding: 10px 13px;
  border-radius: 0 8px 8px 0;
  color: #606266;
}

.bubble.assistant :deep(hr) {
  border: none;
  border-top: 2px solid #ffe8d6;
  margin: 12px 0;
}

.bubble.assistant :deep(a) {
  color: #ff6b35;
  text-decoration: none;
  border-bottom: 1px solid #ff9b7a;
}

.bubble.assistant :deep(a:hover) {
  color: #ff8c42;
  border-bottom-color: #ff8c42;
}

.bubble.assistant :deep(table) {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.bubble.assistant :deep(.table-scroll) {
  width: 100%;
  overflow-x: auto;
  margin: 10px 0;
  border-radius: 8px;
}

.bubble.assistant :deep(th),
.bubble.assistant :deep(td) {
  border: 1px solid #ffe8d6;
  padding: 6px 10px;
  text-align: left;
}

.bubble.assistant :deep(th) {
  background: #fff5f0;
  font-weight: 600;
  color: #ff6b35;
}

.bubble.assistant :deep(tr:nth-child(even)) {
  background: #fafafa;
}

.bubble.assistant :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 8px 0;
}

.bubble.assistant :deep(.emoji) {
  font-size: 16px;
  vertical-align: middle;
}

.bubble.assistant :deep(.highlight) {
  background: linear-gradient(135deg, #ff8c42, #ff6b35);
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}
</style>
