<template>
  <v-container>
    <v-card flat>
      <v-card-title> {{ t("routes.bewerbungenindex.title") }}</v-card-title>
      <bewerbungen-table />
    </v-card>
    <online-help-dialog-component
      :helptext="t('routes.bewerbungenindex.onlineHelp')"
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
