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
        >
          <div>
            {{
              t("app.keineAktiveKonfiguration.text", {
                cause: keineAktiveKonfiguration,
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
const ehrenamtJustizOnlineService = ref(
  new EhrenamtJustizOnlineServiceClass(t)
);
const activeConfigStore = useActiveKonfigurationStore();
const keineAktiveKonfiguration = ref("");

onMounted(() => {
  loadActiveKonfiguration();
});

/**
 * Lädt aktive Konfiguration vom Backend und setzt diese im Store.
 */
function loadActiveKonfiguration(): void {
  ehrenamtJustizOnlineService.value
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
@import url("https://assets.muenchen.de/mde/1.0.7/css/muenchende-style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";

.main {
  background-color: white;
}

.main-container {
  padding: 0%;
}
</style>
