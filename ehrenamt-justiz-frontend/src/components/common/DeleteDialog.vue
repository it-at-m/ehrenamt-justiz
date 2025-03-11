<template>
  <v-dialog
    :model-value="modelValue"
    persistent
    max-width="290"
  >
    <v-card>
      <v-card-title class="text-h5"> Löschen? </v-card-title>
      <v-card-text v-if="descriptorString">
        Soll(en)
        <span class="font-weight-bold">{{ descriptorString }}</span>
        wirklich gelöscht werden?
      </v-card-text>
      <v-card-text v-else>
        Sollen die Daten wirklich gelöscht werden?
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="text"
          @click="cancel"
        >
          Abbrechen
        </v-btn>
        <v-btn
          color="error"
          variant="text"
          :loading="isAnimation"
          @click="deleteItem"
        >
          Löschen
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
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

const emit = defineEmits<{
  cancel: [];
  delete: [];
}>();

function cancel(): void {
  emit("cancel");
}
function deleteItem(): void {
  emit("delete");
}
</script>
