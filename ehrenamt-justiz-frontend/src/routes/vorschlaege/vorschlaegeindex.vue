<template>
  <v-container>
    <v-card flat>
      <v-card-title>{{ t("routes.vorschlaegeindex.title") }}</v-card-title>
      <vorschlaege-table />
    </v-card>
    <online-help-dialog-component
      :helptext="t('routes.vorschlaegeindex.onlineHelp')"
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
