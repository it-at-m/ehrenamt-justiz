<template>
  <v-dialog v-model="visible">
    <v-card>
      <v-card-title class="text-h5">Invalide Datensätze</v-card-title>
      <v-card-text
        >Folgende Datensätze entsprechen nicht den Vorgaben</v-card-text
      >
      <v-data-table
        :items="invalidePersonen"
        :headers="headers"
        multi-sort
        class="elevation-1"
      >
        <template #item="{ item }">
          <tr>
            <td>{{ item.familienname }}</td>
            <td>{{ item.vorname }}</td>
            <td>{{ new Date(item.geburtsdatum).toLocaleDateString() }}</td>
          </tr>
        </template>
      </v-data-table>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="cancelInvalidePersonenSelect"> Abbrechen </v-btn>
        <v-btn
          class="bg-accent"
          :loading="vorschlagsListeAnimationAktiv"
          @click="invalidePersonenSelect"
        >
          Validierung umgehen
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type PersonenTableData from "@/types/PersonenTableData";

import { computed } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VDataTable,
  VDialog,
  VSpacer,
} from "vuetify/components";

const props = defineProps<{
  modelValue: boolean;
  vorschlagsListeAnimationAktiv: boolean;
  invalidePersonen: PersonenTableData[];
}>();

type ReadonlyHeaders = VDataTable["$props"]["headers"];

const headers: ReadonlyHeaders = [
  {
    title: "Familienname",
    value: "familienname",
    align: "start",
    sortable: true,
  },
  {
    title: "Vorname",
    value: "vorname",
    align: "start",
    sortable: true,
  },
  {
    title: "Geburtsdatum",
    value: "geburtsdatum",
    align: "start",
    sortable: true,
  },
];
const emits = defineEmits<{
  "update:modelValue": [v: boolean];
  invalidePersonenSelect: [];
  cancelInvalidePersonenSelect: [];
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});

function invalidePersonenSelect() {
  emits("invalidePersonenSelect");
}
function cancelInvalidePersonenSelect() {
  emits("cancelInvalidePersonenSelect");
}
</script>
