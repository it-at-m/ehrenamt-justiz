<template>
  <v-container>
    <v-card flat>
      <v-card-title>Vorschläge anzeigen</v-card-title>
      <vorschlaege-table />
    </v-card>
    <online-help-dialog-component
      component="Das ist die Onlinehilfe für die Übersicht der Vorschläge (Under Construction)"
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
