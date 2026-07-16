<template>
  <v-app>
    <div v-html="mucIconsSprite"></div>
    <div v-html="customIconsSprite"></div>
    <v-main style="background-color: #f5f8fa">
      <v-container
        class="main-container"
        fluid
      >
        <muc-banner
          v-if="keineAktiveKonfiguration"
          :title="t('app.keineAktiveKonfiguration.title')"
          type="emergency"
          variant="header"
        >
          <div>
            {{
              t("app.keineAktiveKonfiguration.text", {
                cause: keineAktiveKonfiguration,
              })
            }}
          </div>
        </muc-banner>
        <muc-banner
          v-if="keineTechnischeKonfiguration"
          :title="t('app.keineTechnischeKonfiguration.title')"
          type="emergency"
          variant="header"
        >
          <div>
            {{
              t("app.keineTechnischeKonfiguration.text", {
                cause: keineTechnischeKonfiguration,
              })
            }}
          </div>
        </muc-banner>
        <main-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import type KonfigurationData from "@/types/KonfigurationData";
import type TechnischeKonfigurationData from "@/types/TechnischeKonfigurationData.ts";

import { MucBanner } from "@muenchen/muc-patternlab-vue";
import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { VApp, VContainer, VMain } from "vuetify/components";

import { EhrenamtJustizOnlineServiceClass } from "@/api/EhrenamtJustizOnlineService";
import { useActiveKonfigurationStore } from "@/stores/activeconfig";
import MainView from "@/views/MainView.vue";

const { t } = useI18n();
const ehrenamtJustizOnlineService = new EhrenamtJustizOnlineServiceClass(t);

const activeConfigStore = useActiveKonfigurationStore();
const keineAktiveKonfiguration = ref("");
const keineTechnischeKonfiguration = ref("");

onMounted(() => {
  loadTechnischeKonfiguration();

  loadActiveKonfiguration();
});

/**
 * Loads technical configuration from the backend and sets it in the store.
 */
function loadTechnischeKonfiguration(): void {
  ehrenamtJustizOnlineService
    .getTechnischeKonfiguration()
    .then((technischeKonfigurationData: TechnischeKonfigurationData) => {
      activeConfigStore.setTechnischeKonfiguration(technischeKonfigurationData);
    })
    .catch((reason) => {
      keineTechnischeKonfiguration.value = reason;
    });
}

/**
 * Loads active configuration from the backend and sets it in the store.
 */
function loadActiveKonfiguration(): void {
  ehrenamtJustizOnlineService
    .getAktiveKonfiguration()
    .then((konfiguration: KonfigurationData) => {
      activeConfigStore.setKonfiguration(konfiguration);
    })
    .catch((reason) => {
      keineAktiveKonfiguration.value = reason;
    });
}
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.1.23/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";

.main {
  background-color: white;
}

.main-container {
  padding: 0;
}
</style>
