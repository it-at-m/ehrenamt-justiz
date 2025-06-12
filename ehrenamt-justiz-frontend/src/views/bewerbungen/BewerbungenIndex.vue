<template>
  <v-container>
    <v-card flat>
      <v-card-title>Bewerbungsliste anzeigen</v-card-title>
      <bewerbungen-table />
    </v-card>
    <online-help-dialog-component
      :component="t('views.applicant.index.onlinehelp')"
    />
  </v-container>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { VCard, VCardTitle, VContainer } from "vuetify/components";

import BewerbungenTable from "@/components/bewerbungen/BewerbungenTable.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";

const { t } = useI18n();
</script>
<style scoped>
@media (min-width: 1280px) {
  .v-container {
    max-width: 2000px; /* Überschrieben, damit die Konflikte-Table nicht winzig angzeigt wird und der Platz im Container ausgenutzt wird */
  }
}
</style>
