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
      <online-help-dialog-component :helptext="t('routes.index.onlineHelp')" />
      <v-col class="mb-4">
        <h1 class="text-h3 font-weight-bold mb-3">
          {{ t("routes.index.welcome") }}
        </h1>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { VCol, VContainer, VImg, VRow } from "vuetify/components";

import { checkHealth } from "@/api/health-client.ts";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";
import HealthState from "@/types/HealthState.ts";

const snackbarStore = useSnackbarStore();
const status = ref("DOWN");

const { t } = useI18n();
onMounted(() => {
  checkHealth()
    .then((content: HealthState) => (status.value = content.status))
    .catch((error: Error) => {
      snackbarStore.push({
        text: error.message,
        color: STATUS_INDICATORS.ERROR,
      });
    });
});
</script>

<style scoped>
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
