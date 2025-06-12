<template>
  <v-container>
    <v-card flat>
      <v-card-title>Konflikte anzeigen</v-card-title>
      <konflikte-table />
    </v-card>
    <online-help-dialog-component
      component="Hier erhalten Sie einen Überblick über die Bewerber für Schöffen oder ehrenamtliche Richter, die Konflikte aufweisen. Je nach Ihren Berechtigungen haben Sie die Möglichkeit, die Konfliktbearbeitung zu starten oder einen Bewerber zu löschen."
    />
  </v-container>
</template>

<script setup lang="ts">
import { VCard, VCardTitle, VContainer } from "vuetify/components";

import KonflikteTable from "@/components/konflikte/KonflikteTable.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
</script>

<style scoped>
@media (min-width: 1280px) {
  .v-container {
    max-width: 2000px; /* Überschrieben, damit die Konflikte-Table nicht winzig angzeigt wird und der Platz im Container ausgenutzt wird */
  }
}
</style>
