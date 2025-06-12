<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="12">
        <v-img
          src="@/assets/logo.png"
          class="my-3"
          height="200"
        />
      </v-col>
      <online-help-dialog-component
        component="Willkommen bei der Anwendung, in der Sie Schöffen und ehrenamtliche Richter verwalten können.
Die Anwendung besteht aus folgenden Bereichen:
-------------------------------------------------------------------------------------------------------------------------------------------------------
Im oberen Bereich:
- 3 horizontale Linien: Menü links auf- und zuklappen
- Ampel (rot oder grün) zeigt an, ob Gateway, Backend und EWO-EAI aktiv sind. Die Anwendung ist erst dann vollständig verfügbar wenn alle drei Ampeln grün anzeigen
- Art der Ehrenamt-Justiz aus der aktiven Konfiguration: Schöffen oder ehrenamtliche Verwaltungsrichter
- Bezeichnung aus der aktiven Konfiguration
- Name des angemeldeten Users
- ? um Onlinehilfe für das jeweilige Unterprogramm zu öffnen
- App-Switcher um weitere Anwendungen zu starten
-------------------------------------------------------------------------------------------------------------------------------------------------------
Im linken Bereich:
- Menü um Unterprogramme zu öffnen
-------------------------------------------------------------------------------------------------------------------------------------------------------
Im mittleren Bereich:
- Bereich für die Unterprogramme, die über das Menü geöffnet werden können
-------------------------------------------------------------------------------------------------------------------------------------------------------"
      />
      <v-col class="mb-4">
        <h1 class="text-h3 font-weight-bold mb-3">
          Willkommen bei Ehrenamt-Justiz
        </h1>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { VCol, VContainer, VImg, VRow } from "vuetify/components";

import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
</script>

<style scoped>
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
