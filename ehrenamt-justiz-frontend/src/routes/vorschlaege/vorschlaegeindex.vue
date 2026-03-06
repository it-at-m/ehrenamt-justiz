<template>
  <v-container>
    <v-card flat>
      <v-card-title>{{ t("views.vorschlaegeIndex.title") }}</v-card-title>
      <vorschlaege-table />
    </v-card>
    <online-help-dialog-component
      :helptext="t('views.vorschlaegeIndex.onlineHelp')"
    />
  </v-container>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { VCard, VCardTitle, VContainer } from "vuetify/components";

import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import VorschlaegeTable from "@/components/vorschlaege/VorschlaegeTable.vue";

const { t } = useI18n();
</script>
<style scoped>
@media (min-width: 1280px) {
  .v-container {
    max-width: 2000px; /* Overwritten so that the conflict table is not displayed in a tiny size and the space in the container is used */
  }
}
</style>
