<template>
  <v-app>
    <the-snackbar />
    <v-app-bar color="primary">
      <v-row align="center">
        <v-col
          cols="5"
          class="d-flex align-center justify-start"
        >
          <v-app-bar-nav-icon @click.stop="toggleDrawer()" />
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
                <span>Status Gateway</span>
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
                <span>Status Backend</span>
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
                <span>Status EWO-EAI</span>
              </v-tooltip>
              <span class="text-white"> KVR-</span>
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
            <span>Online-Hilfe</span>
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
    <v-navigation-drawer v-model="drawer">
      <v-list>
        <v-list-item :to="{ name: ROUTES_GETSTARTED }">
          <v-list-item-title>Get started</v-list-item-title>
        </v-list-item>
        <v-list-item :to="{ name: 'bewerbung.create' }">
          <!-- :disabled="
         !user ||
         !user.authorities.includes('READ_EWOBUERGER') ||
         !globalSettingsStore ||
         !globalSettingsStore.getKonfiguration" -->

          <v-list-item-title>Bewerbung erstellen</v-list-item-title>
        </v-list-item>
        <v-list-item :to="{ name: 'bewerbung.index' }">
          <!-- :disabled="
       !user ||
       !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN') ||
       !globalSettingsStore ||
       !globalSettingsStore.getKonfiguration" -->

          <v-list-item-title>Bewerbungen</v-list-item-title>
        </v-list-item>
        <v-list-item :to="{ name: 'konflikte.index' }">
          <!-- :disabled="
         !user ||
         !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN') ||
         !globalSettingsStore ||
         !globalSettingsStore.getKonfiguration" -->
          <v-list-item-title>Konflikte</v-list-item-title>
        </v-list-item>
        <v-list-item :to="{ name: 'vorschlaege.index' }">
          <!-- :disabled="!user || !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN')"-->

          <v-list-item-title>Vorschläge</v-list-item-title>
        </v-list-item>
        <v-list-item :to="{ name: 'konfiguration.index' }">
          <!-- :disabled="!user || !user.authorities.includes('READ_KONFIGURATION')"-->
          <v-list-item-title>Konfiguration</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <v-container fluid>
        <router-view v-slot="{ Component }">
          <v-fade-transition mode="out-in">
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import type Konfiguration from "@/types/Konfiguration";

import { mdiApps, mdiCircle, mdiHelp } from "@mdi/js";
import { AppSwitcher } from "@muenchen/appswitcher-vue";
import { useToggle } from "@vueuse/core";
import { onMounted, ref } from "vue";
import {
  VApp,
  VAppBar,
  VAppBarNavIcon,
  VBtn,
  VCol,
  VContainer,
  VFadeTransition,
  VIcon,
  VList,
  VListItem,
  VListItemTitle,
  VMain,
  VNavigationDrawer,
  VRow,
  VToolbarTitle,
  VTooltip,
} from "vuetify/components";

import { EhrenamtJustizService } from "@/api/EhrenamtJustizService";
import { EWOBuergerApiService } from "@/api/EWOBuergerApiService";
import { checkHealth } from "@/api/HealthService";
import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import { getUser } from "@/api/user-client";
import TheSnackbar from "@/components/TheSnackbar.vue";
import { APPSWITCHER_URL, ROUTES_GETSTARTED } from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";
import HealthState from "@/types/HealthState";
import User, { UserLocalDevelopment } from "@/types/User";

const appswitcherBaseUrl = APPSWITCHER_URL;
const globalSettingsStore = useGlobalSettingsStore();
const snackbarStore = useSnackbarStore();
const userStore = useUserStore();
const [drawer, toggleDrawer] = useToggle();
const userinfo = ref<string>("");
const bezeichnungApp = ref("Aktive Konfiguration fehlt");
const ehrenamtjustizart = ref("");
const gatewayStatus = ref("DOWN");
const backendStatus = ref("DOWN");
const eaiStatus = ref("DOWN");

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
    .then((konfiguration: Konfiguration) => {
      globalSettingsStore.setKonfiguration(konfiguration);
      ehrenamtjustizart.value = konfiguration.ehrenamtjustizart;
      bezeichnungApp.value = konfiguration.bezeichnung;
    })
    .catch(() => {
      snackbarStore.showMessage({
        message:
          "Es konnte keine aktive Konfiguration gefunden werden! Bitte nur eine aktive Konfiguration anlegen und dann die Anwenndung neu laden!",
      });
    });
}

function healthCheckTimer(): void {
  checkGatewayHealth();
  checkBackendStatus();
  checkEAIStatus();

  loadActiveKonfiguration();

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
function hilfeAnzeigen(): void {
  globalSettingsStore.setOnlineHelpDialogComponentVisible(true);
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
