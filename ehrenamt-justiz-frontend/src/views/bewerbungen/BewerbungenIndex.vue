<template>
  <v-container>
    <v-card flat>
      <v-card-title>Bewerbungsliste anzeigen</v-card-title>
      <bewerbungen-table />
    </v-card>
    <online-help-dialog-component
      component="In dieser Ansicht finden Sie alle Bewerber, die entweder über den Menüpunkt 'Bewerbung erstellen' oder über die Online-Anwendung im Internet erfasst wurden.

Je nach Ihren Berechtigungen können Sie hier folgende Aktionen durchführen: die Daten eines Bewerbers ändern, anzeigen oder löschen. Darüber hinaus haben Sie die Möglichkeit, Bewerber für die weitere Bearbeitung auf die Vorschlagsliste zu setzen oder sie als CSV-Datei zu speichern."
    />
  </v-container>
</template>

<script setup lang="ts">
import { VCard, VCardTitle, VContainer } from "vuetify/components";

import BewerbungenTable from "@/components/bewerbungen/BewerbungenTable.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
</script>
<style scoped>
@media (min-width: 1280px) {
  .v-container {
    max-width: 2000px; /* Überschrieben, damit die Konflikte-Table nicht winzig angzeigt wird und der Platz im Container ausgenutzt wird */
  }
}
</style>
