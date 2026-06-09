<template>
  <div id="app">
    <AppHeader v-if="showSiteChrome" />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <AISearch v-if="showSiteChrome" />
    <AppFooter v-if="showSiteChrome" />
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import AppHeader from "./components/AppHeader.vue";
import AppFooter from "./components/AppFooter.vue";
import AISearch from "@/components/AISearch.vue";

const route = useRoute();
const showSiteChrome = computed(
  () => !["/login", "/register"].includes(route.path)
);
</script>

<style>
#app {
  width: 100%;
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
