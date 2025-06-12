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
      <v-icon
        @click="closeOnlineHelp()"
        :icon="mdiClose"
      />
    </v-toolbar>
    <v-card flat>
      <v-card-text>
        <p
          v-for="(line, index) in formattedLines"
          :key="index"
        >
          {{ line }}
        </p>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { mdiClose } from "@mdi/js";
import { computed } from "vue";
import {
  VCard,
  VCardText,
  VDialog,
  VIcon,
  VSpacer,
  VToolbar,
  VToolbarTitle,
} from "vuetify/components";

import { useGlobalSettingsStore } from "@/stores/globalsettings";

const formattedLines = computed(() => {
  return props.component.split("\n");
});

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
