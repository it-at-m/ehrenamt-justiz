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
          title="Keine aktive Konfiguration"
          type="emergency"
        >
          <div>
            "Es konnte keine aktive Konfiguration gefunden werden! Bitte nur
            eine aktive Konfiguration anlegen und dann die Anwendung neu laden!
            Ursache: "
            {{ keineAktiveKonfiguration }} ,
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
import { VApp, VContainer, VMain } from "vuetify/components";

import { EhrenamtJustizOnlineService } from "@/api/EhrenamtJustizOnlineService";
import { useActiveKonfigurationStore } from "@/stores/activeconfig";
import MainView from "@/views/MainView.vue";

const activeConfigStore = useActiveKonfigurationStore();
const keineAktiveKonfiguration = ref("");

onMounted(() => {
  loadActiveKonfiguration();
});

/**
 * Lädt aktive Konfiguration vom Backend und setzt diese im Store.
 */
function loadActiveKonfiguration(): void {
  EhrenamtJustizOnlineService.getAktiveKonfiguration()
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
