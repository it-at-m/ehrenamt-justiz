<template>
  <v-dialog
    :model-value="modelValue"
    persistent
    max-width="290"
  >
    <v-card>
      <v-card-title class="text-h5">
        {{ t("components.deleteDialog.title") }}
      </v-card-title>
      <v-card-text v-if="descriptorString">
        {{ t("components.deleteDialog.text", { descriptor: descriptorString }) }}
      </v-card-text>
      <v-card-text v-else>
        {{ t("components.deleteDialog.textNoDescriptor") }}
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="text"
          @click="cancel"
        >
          {{ t("components.deleteDialog.buttons.abbrechen") }}
        </v-btn>
        <v-btn
          color="error"
          variant="text"
          :loading="isAnimation"
          @click="deleteItem"
        >
          {{ t("components.deleteDialog.buttons.loeschen") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VDialog,
  VSpacer,
} from "vuetify/components";

defineProps<{
  modelValue: boolean;
  isAnimation: boolean;
  descriptorString: string;
}>();

const emits = defineEmits<{
  cancel: [];
  delete: [];
}>();

const { t } = useI18n();

function cancel(): void {
  emits("cancel");
}
function deleteItem(): void {
  emits("delete");
}
</script>
