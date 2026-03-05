<template>
  <v-app>
    <the-snackbar />
    <the-app-bar
      @clicked-nav-icon="toggleNavigation"
      v-if="isConfigLoaded"
    />
    <the-navigation-drawer
      v-model="isNavigationShown"
      v-if="isConfigLoaded"
    />
    <v-main>
      <v-container fluid>
        <v-progress-circular
          v-if="!isConfigLoaded"
          indeterminate
        ></v-progress-circular>
        <router-view
          v-slot="{ Component }"
          v-else
        >
          <v-fade-transition mode="out-in">
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
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
  VContainer,
  VFadeTransition,
  VMain,
  VProgressCircular,
} from "vuetify/components";

import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import { getUser } from "@/api/user-client";
import TheAppBar from "@/components/TheAppBar.vue";
import TheNavigationDrawer from "@/components/TheNavigationDrawer.vue";
import TheSnackbar from "@/components/TheSnackbar.vue";
import { useGlobalSettingsStore } from "@/stores/globalsettings.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";
import User, { UserLocalDevelopment } from "@/types/User";

const { t } = useI18n();
const globalSettingsStore = useGlobalSettingsStore();
const userStore = useUserStore();
const [isNavigationShown, toggleNavigation] = useToggle();
const snackbarStore = useSnackbarStore();
const isConfigLoaded = computed(() => {
  return (
    userStore.getUser && globalSettingsStore.isKonfigurationLoadingAttempt()
  );
});

onMounted(() => {
  loadUser();
  loadActiveKonfiguration();

  // display drawer at once
  toggleNavigation();
});

/**
 * Loads UserInfo from the backend and sets it in the store.
 */
function loadUser(): void {
  getUser()
    .then((user: User) => {
      userStore.setUser(user);
    })
    .catch(() => {
      // No user info received, so fallback
      if (import.meta.env.DEV) {
        userStore.setUser(UserLocalDevelopment());
      } else {
        userStore.setUser(null);
      }
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
      snackbarStore.showMessage({
        message: t("app.keineAktiveKonfiguration"),
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
