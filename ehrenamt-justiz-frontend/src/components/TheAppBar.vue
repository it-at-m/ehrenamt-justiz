<template>
  <v-app-bar color="primary">
    <v-row align="center">
      <v-col
        cols="5"
        class="d-flex align-center justify-start"
      >
        <v-app-bar-nav-icon
          @click="emit('clickedNavIcon')"
          class="mx-2"
        />
        <router-link to="/">
          <v-toolbar-title class="font-weight-bold">
            <v-tooltip location="top">
              <template #activator="{ props }">
                <span
                  :class="gatewayStatus"
                  v-bind="props"
                >
                  <v-icon
                    size="mdi-18px"
                    :icon="mdiCircle"
                /></span>
              </template>
              <span>{{ t("app.state.gateway") }}</span>
            </v-tooltip>
            <v-tooltip location="top">
              <template #activator="{ props }">
                <span
                  :class="backendStatus"
                  v-bind="props"
                >
                  <v-icon
                    size="mdi-18px"
                    :icon="mdiCircle"
                /></span>
              </template>
              <span>{{ t("app.state.backend") }}</span>
            </v-tooltip>
            <v-tooltip location="top">
              <template #activator="{ props }">
                <span
                  :class="eaiStatus"
                  v-bind="props"
                >
                  <v-icon
                    size="mdi-18px"
                    :icon="mdiCircle"
                /></span>
              </template>
              <span>{{ t("app.state.eai") }}</span>
            </v-tooltip>
            <v-tooltip location="top">
              <template #activator="{ props }">
                <span
                  :class="aenderungsserviceStatus"
                  v-bind="props"
                >
                  <v-icon
                    size="mdi-18px"
                    :icon="mdiCircle"
                /></span>
              </template>
              <span>{{ t("app.state.aenderungsservice") }}</span>
            </v-tooltip>
            <span class="text-white">{{ t("app.head") }}</span>
            <span class="text-secondary">{{ ehrenamtjustizart }}</span>
          </v-toolbar-title>
        </router-link>
      </v-col>
      <v-col
        cols="4"
        class="d-flex justify-start"
      >
        <span class="text-secondary font-weight-bold">
          <h3>{{ bezeichnungApp }}</h3></span
        >
      </v-col>
      <v-col
        cols="3"
        class="d-flex align-center justify-end"
      >
        <span>{{ userinfo }}</span>
        <v-tooltip location="top">
          <template #activator="{ props }">
            <span v-bind="props">
              <v-btn
                style="font-size: 12px"
                :onclick="hilfeAnzeigen"
                :icon="mdiHelp"
            /></span>
          </template>
          <span>{{ t("app.onlineHelp") }}</span>
        </v-tooltip>
        <app-switcher
          v-if="appswitcherBaseUrl"
          :base-url="appswitcherBaseUrl"
          :tags="['global']"
          :icon="mdiApps"
        />
      </v-col>
    </v-row>
  </v-app-bar>
</template>
<script setup lang="ts">
import type KonfigurationData from "@/types/KonfigurationData";

import { mdiApps, mdiCircle, mdiHelp } from "@mdi/js";
import { AppSwitcher } from "@muenchen/appswitcher-vue";
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import {
  VAppBar,
  VAppBarNavIcon,
  VBtn,
  VCol,
  VIcon,
  VRow,
  VToolbarTitle,
  VTooltip,
} from "vuetify/components";

import { EhrenamtJustizService } from "@/api/EhrenamtJustizService.ts";
import { EWOBuergerApiService } from "@/api/EWOBuergerApiService.ts";
import { checkHealth } from "@/api/HealthService.ts";
import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import { getUser } from "@/api/user-client";
import { APPSWITCHER_URL } from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";
import { formattedEhrenamtjustizart } from "@/tools/Helper";
import HealthState from "@/types/HealthState.ts";
import User, { UserLocalDevelopment } from "@/types/User";

const globalSettingsStore = useGlobalSettingsStore();
const userStore = useUserStore();
const snackbarStore = useSnackbarStore();
const { t } = useI18n();
const userinfo = ref<string>("");
const bezeichnungApp = ref(t("app.aktiveKonfigurationFehlt"));
const ehrenamtjustizart = ref("");
const gatewayStatus = ref("DOWN");
const backendStatus = ref("DOWN");
const eaiStatus = ref("DOWN");
const aenderungsserviceStatus = ref("DOWN");
const appswitcherBaseUrl = APPSWITCHER_URL;

onMounted(() => {
  loadUser();
  healthCheckTimer();
  loadActiveKonfiguration();
});

/**
 * Loads UserInfo from the backend and sets it in the store.
 */
function loadUser(): void {
  getUser()
    .then((user: User) => {
      userinfo.value = user.given_name + " " + user.family_name;
      if (userinfo.value.trim() === "") {
        userinfo.value = user.username;
        if (userinfo.value.trim() === "") {
          userinfo.value = user.preferred_username;
        }
      }
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
      ehrenamtjustizart.value = formattedEhrenamtjustizart(
        t,
        konfigurationData.ehrenamtjustizart,
        2
      );
      bezeichnungApp.value = konfigurationData.bezeichnung;
      globalSettingsStore.setKonfigurationLoadingAttempt(true);
    })
    .catch(() => {
      snackbarStore.showMessage({
        message: t("app.keineAktiveKonfiguration"),
      });
      globalSettingsStore.setKonfigurationLoadingAttempt(true);
    });
}

function healthCheckTimer(): void {
  checkGatewayHealth();
  checkBackendStatus();
  checkEAIStatus();
  checkAenderungsserviceStatus();

  setTimeout(() => {
    healthCheckTimer();
  }, 60000);
}

function checkGatewayHealth(): void {
  gatewayStatus.value = "DOWN";
  checkHealth()
    .then((content: HealthState) => {
      gatewayStatus.value = content.status;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}
function checkBackendStatus(): void {
  backendStatus.value = "DOWN";
  EhrenamtJustizService.checkBackendStatus()
    .then((content: string) => {
      backendStatus.value = content;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}
function checkEAIStatus(): void {
  eaiStatus.value = "DOWN";
  EWOBuergerApiService.checkEwoEaiStatus()
    .then((content: string) => {
      eaiStatus.value = content;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}
function checkAenderungsserviceStatus(): void {
  aenderungsserviceStatus.value = "DOWN";
  EWOBuergerApiService.checkAenderungsserviceStatus()
    .then((content: string) => {
      aenderungsserviceStatus.value = content;
    })
    .catch((error) => {
      snackbarStore.showMessage(error);
    });
}
function hilfeAnzeigen(): void {
  globalSettingsStore.setOnlineHelpDialogComponentVisible(true);
}

const emit = defineEmits<{
  clickedNavIcon: [];
}>();
</script>
