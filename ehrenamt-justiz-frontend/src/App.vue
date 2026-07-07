<template>
  <v-app>
    <the-snackbar-queue />
    <the-app-bar
      v-if="isConfigLoaded"
      @clicked-nav-icon="toggleNavigation"
    />
    <the-navigation-drawer
      v-if="isConfigLoaded"
      v-model="isNavigationShown"
    />
    <v-main>
      <v-progress-circular
          v-if="!isConfigLoaded"
          indeterminate
      ></v-progress-circular>
      <router-view v-slot="{ Component }">
        <v-fade-transition mode="out-in">
          <component :is="Component" />
        </v-fade-transition>
      </router-view>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import type KonfigurationData from "@/types/KonfigurationData";

import { useToggle } from "@vueuse/core";
import { computed, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import {
  VApp,
  VFadeTransition,
  VMain,
  VProgressCircular,
} from "vuetify/components";

import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import TheAppBar from "@/components/TheAppBar.vue";
import TheNavigationDrawer from "@/components/TheNavigationDrawer.vue";
import TheSnackbarQueue from "@/components/TheSnackbarQueue.vue";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserInfoStore } from "@/stores/userinfo";

const userInfoStore = useUserInfoStore();
const { t } = useI18n();
const globalSettingsStore = useGlobalSettingsStore();
const [isNavigationShown, toggleNavigation] = useToggle();
const snackbarStore = useSnackbarStore();
const isConfigLoaded = computed(() => {
  return (
    userInfoStore.userInfo &&
    globalSettingsStore.isKonfigurationLoadingAttempt()
  );
});

onMounted(() => {
  loadActiveKonfiguration();
  // display drawer at once
  toggleNavigation();
});

/**
 * Loads active configuration from the backend and sets it in the store.
 */
function loadActiveKonfiguration(): void {
  KonfigurationApiService.getAktiveKonfiguration()
    .then((konfigurationData: KonfigurationData) => {
      globalSettingsStore.setKonfiguration(konfigurationData);
      globalSettingsStore.setKonfigurationLoadingAttempt(true);
    })
    .catch(() => {
      snackbarStore.push({
        text: t("app.keineAktiveKonfiguration"),
      });
      globalSettingsStore.setKonfigurationLoadingAttempt(true);
    });
}
</script>

<style>
.main {
  background-color: white;
}
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
