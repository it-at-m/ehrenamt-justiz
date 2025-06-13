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
        :component="t('views.homeview.onlinehelp')"
      />
      <v-col class="mb-4">
        <h1 class="text-h3 font-weight-bold mb-3">
          {{ t("views.homeview.welcome") }}
        </h1>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { VCol, VContainer, VImg, VRow } from "vuetify/components";

import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";

const { t } = useI18n();
</script>

<style scoped>
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
