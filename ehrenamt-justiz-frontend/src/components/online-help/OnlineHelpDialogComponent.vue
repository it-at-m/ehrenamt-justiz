<template>
  <v-dialog
    v-model="visible"
    max-width="800"
    @click:outside="closeOnlineHelp()"
    @keydown.esc.prevent="closeOnlineHelp()"
    @keydown.enter.prevent="closeOnlineHelp()"
  >
    <v-toolbar>
      <v-toolbar-title>{{
        t("components.onlineHelpDialogComponent.onlineHilfe")
      }}</v-toolbar-title>
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
import { useI18n } from "vue-i18n";
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
  return props.helptext
    .split(/\r?\n/)
    .map((l) => l.trim())
    .filter(Boolean);
});

const { t } = useI18n();
const props = defineProps<{
  helptext: string;
}>();

const visible = computed(() => {
  return useGlobalSettingsStore().isOnlineHelpDialogComponentVisible();
});

function closeOnlineHelp(): void {
  useGlobalSettingsStore().setOnlineHelpDialogComponentVisible(false);
}
</script>

<style scoped></style>
