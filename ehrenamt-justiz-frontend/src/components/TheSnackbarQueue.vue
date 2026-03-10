<template>
  <v-snackbar-queue
    v-model="snackbarStore.queue"
    :timeout="5000"
    :color="STATUS_INDICATORS.INFO"
  >
    <template #text="textData">
      <v-layout class="align-center">
        <v-icon
          class="mr-3"
          size="25"
          :icon="textData.item.icon"
        />
        <p class="text-body-1">{{ textData.item.text }}</p>
      </v-layout>
    </template>
    <template #actions="{ props, item }">
      <v-btn
        v-if="item.color === STATUS_INDICATORS.ERROR"
        color="primary"
        variant="text"
        v-bind="props"
      >
        {{ t("components.theSnackbar.schliessen") }}
      </v-btn>
    </template>
  </v-snackbar-queue>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { VBtn, VIcon, VLayout, VSnackbarQueue } from "vuetify/components";

import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();

const { t } = useI18n();
</script>
