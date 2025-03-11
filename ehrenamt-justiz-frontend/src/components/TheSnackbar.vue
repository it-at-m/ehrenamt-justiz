<template>
  <v-snackbar
    id="snackbar"
    v-model="show"
    :color="color"
    :timeout="timeout"
  >
    {{ message }}
    <v-btn
      v-if="isError"
      color="primary"
      variant="text"
      @click="hide"
    >
      Schließen
    </v-btn>
  </v-snackbar>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { VBtn, VSnackbar } from "vuetify/components";

import { SNACKBAR_DEFAULT_TIMEOUT, STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const snackbarStore = useSnackbarStore();

const show = ref(false);
const timeout = ref(SNACKBAR_DEFAULT_TIMEOUT);
const message = ref("");
const color = ref(STATUS_INDICATORS.INFO);

const isError = computed(() => color.value === STATUS_INDICATORS.ERROR);

watch(
  () => snackbarStore.message,
  () => (message.value = snackbarStore.message ?? "")
);

watch(
  () => snackbarStore.level,
  () => {
    color.value = snackbarStore.level;
    if (color.value === STATUS_INDICATORS.ERROR) {
      timeout.value = 10000; //TODO: richtig? Bei error und "timeout.value=0" wird Snackbar nur extrem kurz angezeigt!!
    } else {
      timeout.value = SNACKBAR_DEFAULT_TIMEOUT;
    }
  }
);

watch(
  () => snackbarStore.show,
  () => {
    if (snackbarStore.show) {
      show.value = false;
      setTimeout(() => {
        show.value = true;
        snackbarStore.show = false;
      }, 100); // TODO: richtig? Wartezeit bis die Meldung angezeigt wird
    }
  }
);

function hide(): void {
  show.value = false;
}
</script>
