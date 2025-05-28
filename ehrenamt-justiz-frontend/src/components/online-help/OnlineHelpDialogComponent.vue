<template>
  <v-dialog
    v-model="visible"
    max-width="800"
    persistent
    @keydown.esc.prevent="closeOnlineHelp()"
    @keydown.enter.prevent="closeOnlineHelp()"
  >
    <v-toolbar>
      <v-toolbar-title>Online-Hilfe</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn
        icon=""
        @click="closeOnlineHelp()"
        ><v-icon>mdi-close</v-icon></v-btn
      >
    </v-toolbar>
    <v-card flat>
      <v-card-text>
        {{ props.component }}
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from "vue";
import {
  VBtn,
  VCard,
  VCardText,
  VDialog,
  VIcon,
  VSpacer,
  VToolbar,
  VToolbarTitle,
} from "vuetify/components";

import { useGlobalSettingsStore } from "@/stores/globalsettings";

const props = defineProps<{
  component: string;
}>();

const visible = computed(() => {
  return useGlobalSettingsStore().isOnlineHelpDialogComponentVisible();
});

function closeOnlineHelp(): void {
  useGlobalSettingsStore().setOnlineHelpDialogComponentVisible(false);
}
</script>

<style scoped></style>
