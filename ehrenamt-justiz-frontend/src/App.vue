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
import type TechnischeKonfigurationData from "@/types/TechnischeKonfigurationData";

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
import { TechnischeKonfigurationApiService } from "@/api/TechnischeKonfigurationApiService.ts";
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
    globalSettingsStore.isKonfigurationLoadingAttempt() &&
    globalSettingsStore.isTechnischeKonfigurationLoadingAttempt()
  );
});

onMounted(() => {
  loadTechnischeKonfiguration();

  loadActiveKonfiguration();

  // display drawer at once
  toggleNavigation();
});

/**
 * Loads technical configuration from the backend and sets it in the store.
 */
function loadTechnischeKonfiguration(): void {
  TechnischeKonfigurationApiService.getTechnischeKonfiguration()
    .then((technischeKonfigurationData: TechnischeKonfigurationData) => {
      globalSettingsStore.setTechnischeKonfiguration(
        technischeKonfigurationData
      );
      globalSettingsStore.setTechnischeKonfigurationLoadingAttempt(true);
    })
    .catch(() => {
      snackbarStore.push({
        text: t("app.keineTechnischeKonfiguration"),
      });
      globalSettingsStore.setTechnischeKonfigurationLoadingAttempt(true);
    });
}

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
</style>
