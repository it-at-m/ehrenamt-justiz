<template>
  <v-app-bar color="primary">
    <v-row class="align-center">
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
import type { HealthState } from "@/types/HealthState";

import { mdiApps, mdiCircle, mdiHelp } from "@mdi/js";
import { AppSwitcher } from "@muenchen/appswitcher-vue";
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
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

import { EhrenamtJustizService } from "@/api/EhrenamtJustizService";
import { EWOBuergerApiService } from "@/api/EWOBuergerApiService";
import { checkHealth } from "@/api/HealthService";
import { APPSWITCHER_URL } from "@/Constants";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useUserInfoStore } from "@/stores/userinfo";
import { formattedEhrenamtjustizart } from "@/tools/Helper";

const globalSettingsStore = useGlobalSettingsStore();
const userInfoStore = useUserInfoStore();
const { t } = useI18n();
const userinfo = ref<string | undefined>("");
const labelApp = ref(t("app.aktiveKonfigurationFehlt"));
const ehrenamtjustizart = ref("");
const STATUS_UNKNOWN = "UNKNOWN";
const STATUS_DOWN = "DOWN";
const gatewayStatus = ref(STATUS_DOWN);
const backendStatus = ref(STATUS_DOWN);
const eaiStatus = ref(STATUS_DOWN);
const aenderungsserviceStatus = ref(STATUS_DOWN);
const appswitcherBaseUrl = APPSWITCHER_URL;
let intervalId: ReturnType<typeof setInterval>;
const user = computed(() => userInfoStore.userInfo);

onMounted(() => {
  getUserInfo();
  getDataFromConfiguration();
  healthCheckTimer();
  intervalId = setInterval(healthCheckTimer, 60000);
});

onBeforeUnmount(() => {
  clearInterval(intervalId);
});

/**
 * Get UserInfo from the store
 */
function getUserInfo(): void {
  userinfo.value = user.value?.preferred_username;
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
}

function checkGatewayHealth(): void {
  checkHealth()
    .then((content: HealthState) => {
      gatewayStatus.value = content.status;
    })
    .catch(() => {
      gatewayStatus.value = STATUS_UNKNOWN;
    });
}
function checkBackendStatus(): void {
  EhrenamtJustizService.checkBackendStatus()
    .then((content: string) => {
      backendStatus.value = content;
    })
    .catch(() => {
      backendStatus.value = STATUS_UNKNOWN;
    });
}
function checkEAIStatus(): void {
  EWOBuergerApiService.checkEwoEaiStatus()
    .then((content: string) => {
      eaiStatus.value = content;
    })
    .catch(() => {
      eaiStatus.value = STATUS_UNKNOWN;
    });
}
function checkAenderungsserviceStatus(): void {
  EWOBuergerApiService.checkAenderungsserviceStatus()
    .then((content: string) => {
      aenderungsserviceStatus.value = content;
    })
    .catch(() => {
      aenderungsserviceStatus.value = STATUS_UNKNOWN;
    });
}
function hilfeAnzeigen(): void {
  globalSettingsStore.setOnlineHelpDialogComponentVisible(true);
}

const emit = defineEmits<{
  clickedNavIcon: [];
}>();
</script>

<style scoped>
.UP {
  color: limegreen;
}

.UNKNOWN {
  color: black;
}

.DOWN {
  color: lightcoral;
}
</style>
