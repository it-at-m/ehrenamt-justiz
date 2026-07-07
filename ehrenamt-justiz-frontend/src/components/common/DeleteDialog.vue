<template>
  <v-dialog
    v-model="modelValue"
    max-width="290"
    persistent
    @click:outside="cancel"
  >
    <v-card>
      <v-card-title class="text-headline-small">
        {{ t("components.deleteDialog.title") }}
      </v-card-title>
      <v-card-text v-if="descriptorString">
        {{
          t("components.deleteDialog.text", { descriptor: descriptorString })
        }}
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
  isAnimation: boolean;
  descriptorString: string;
}>();

const modelValue = defineModel<boolean>({ required: true });

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
