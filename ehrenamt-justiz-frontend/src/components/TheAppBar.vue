<template>
  <v-app-bar color="primary">
    <v-row align="center">
      <v-col
        cols="5"
        class="d-flex align-center justify-start"
      >
        <v-app-bar-nav-icon
          class="mx-2"
          @click="emit('clickedNavIcon')"
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
          <h3>{{ labelApp }}</h3></span
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
                :icon="mdiHelp"
                @click="hilfeAnzeigen"
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
import { mdiApps, mdiCircle, mdiHelp } from "@mdi/js";
import { AppSwitcher } from "@muenchen/appswitcher-vue";
import { computed, onMounted, onUnmounted, ref } from "vue";
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
import { APPSWITCHER_URL, STATUS_INDICATORS } from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import { useUserStore } from "@/stores/user.ts";
import { formattedEhrenamtjustizart } from "@/tools/Helper";
import HealthState from "@/types/HealthState.ts";

const globalSettingsStore = useGlobalSettingsStore();
const userStore = useUserStore();
const snackbarStore = useSnackbarStore();
const { t } = useI18n();
const userinfo = ref<string | undefined>("");
const labelApp = ref(t("app.aktiveKonfigurationFehlt"));
const ehrenamtjustizart = ref("");
const gatewayStatus = ref("DOWN");
const backendStatus = ref("DOWN");
const eaiStatus = ref("DOWN");
const aenderungsserviceStatus = ref("DOWN");
const appswitcherBaseUrl = APPSWITCHER_URL;
let healthCheckTimeout: ReturnType<typeof setTimeout> | null = null;
const user = computed(() => userStore.getUser);

onMounted(() => {
  getUserInfo();
  getDataFromConfiguration();
  healthCheckTimer();
});

onUnmounted(() => {
  if (healthCheckTimeout) {
    clearTimeout(healthCheckTimeout);
    healthCheckTimeout = null;
  }
});

/**
 * Get UserInfo from the store
 */
function getUserInfo(): void {
  const givenName = user.value?.given_name;
  const familyName = user.value?.family_name;
  if (givenName || familyName) {
    userinfo.value = [givenName, familyName].filter(Boolean).join(" ");
  } else {
    userinfo.value = user.value?.username;
    if (!userinfo.value) {
      userinfo.value = user.value?.preferred_username;
    }
  }
}

/**
 * Get data from active configuration
 */
function getDataFromConfiguration(): void {
  labelApp.value = t("app.aktiveKonfigurationFehlt");
  ehrenamtjustizart.value = "";
  if (globalSettingsStore && globalSettingsStore.getKonfiguration) {
    labelApp.value = globalSettingsStore.getKonfiguration.bezeichnung;

    ehrenamtjustizart.value = formattedEhrenamtjustizart(
      t,
      globalSettingsStore.getKonfiguration.ehrenamtjustizart,
      2
    );
  }
}

function healthCheckTimer(): void {
  checkGatewayHealth();
  checkBackendStatus();
  checkEAIStatus();
  checkAenderungsserviceStatus();

  healthCheckTimeout = setTimeout(() => {
    healthCheckTimer();
  }, 60000);
}

function checkGatewayHealth(): void {
  gatewayStatus.value = "DOWN";
  checkHealth()
    .then((content: HealthState) => {
      gatewayStatus.value = content.status;
    })
    .catch((error: Error) => {
      snackbarStore.push({
        text: error.message,
        color: STATUS_INDICATORS.ERROR,
      });
    });
}
function checkBackendStatus(): void {
  backendStatus.value = "DOWN";
  EhrenamtJustizService.checkBackendStatus()
    .then((content: string) => {
      backendStatus.value = content;
    })
    .catch((error: Error) => {
      snackbarStore.push({
        text: error.message,
        color: STATUS_INDICATORS.ERROR,
      });
    });
}
function checkEAIStatus(): void {
  eaiStatus.value = "DOWN";
  EWOBuergerApiService.checkEwoEaiStatus()
    .then((content: string) => {
      eaiStatus.value = content;
    })
    .catch((error: Error) => {
      snackbarStore.push({
        text: error.message,
        color: STATUS_INDICATORS.ERROR,
      });
    });
}
function checkAenderungsserviceStatus(): void {
  aenderungsserviceStatus.value = "DOWN";
  EWOBuergerApiService.checkAenderungsserviceStatus()
    .then((content: string) => {
      aenderungsserviceStatus.value = content;
    })
    .catch((error: Error) => {
      snackbarStore.push({
        text: error.message,
        color: STATUS_INDICATORS.ERROR,
      });
    });
}
function hilfeAnzeigen(): void {
  globalSettingsStore.setOnlineHelpDialogComponentVisible(true);
}

const emit = defineEmits<{
  clickedNavIcon: [];
}>();
</script>
