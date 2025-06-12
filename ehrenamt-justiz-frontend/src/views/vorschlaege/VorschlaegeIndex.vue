<template>
  <v-container>
    <v-card flat>
      <v-card-title>Vorschläge anzeigen</v-card-title>
      <vorschlaege-table />
    </v-card>
    <online-help-dialog-component
      component="Hier finden Sie die Vorschläge für Schöffen oder ehrenamtliche Richter am Verwaltungsgericht. Je nach Ihren Berechtigungen können Sie hier folgende Aktionen durchführen: Die vorgeschlagenen Bewerber ändern, anzeigen oder löschen. Darüber hinaus haben Sie die Möglichkeit, einen Vorschlag auf die Bewerberliste zu setzen oder sie als CSV-Datei zu speichern."
    />
  </v-container>
</template>

<script setup lang="ts">
import { VCard, VCardTitle, VContainer } from "vuetify/components";

import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import VorschlaegeTable from "@/components/vorschlaege/VorschlaegeTable.vue";
</script>
<style scoped>
@media (min-width: 1280px) {
  .v-container {
    max-width: 2000px; /* Überschrieben, damit die Bewerber-Table nicht winzig angzeigt wird und der Platz im Container ausgenutzt wird */
  }
}
</style>
