<template>
  <v-dialog v-model="visible">
    <v-card>
      <v-card-title class="text-h5">Bewerbung erstellen</v-card-title>
      <v-card-text> Bitte wählen Sie den gewünschten Eintrag aus </v-card-text>
      <v-data-table
        :items="eWOBuergerDaten"
        :headers="headers"
        multi-sort
        class="elevation-1"
      >
        <template #item="{ item }">
          <tr :class="item.auskunftssperre.length > 0 ? 'auskunftssperre' : ''">
            <td>
              <v-tooltip location="bottom">
                <template #activator="{ props }">
                  <v-icon
                    v-bind="props"
                    @click="selectBuerger(item)"
                    @keydown.enter.prevent="selectBuerger(item)"
                    :icon="mdiAccountPlus"
                  />
                </template>
                <span>Auswählen und übernehmen</span>
              </v-tooltip>
            </td>
            <td>{{ item.familienname }}</td>
            <td>{{ item.vorname }}</td>
            <td>{{ new Date(item.geburtsdatum).toLocaleDateString() }}</td>
            <td>
              {{ item.auskunftssperre.length > 0 ? "Auskunftssperre" : "" }}
            </td>
            <td>{{ item.ewoidbereitserfasst ? "Ja" : "Nein" }}</td>
          </tr>
        </template>
      </v-data-table>
      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="outlined"
          @click="cancelBuergerSelect"
        >
          Abbrechen
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type EWOBuergerDaten from "@/types/EWOBuergerDaten";

import { mdiAccountPlus } from "@mdi/js";
import { computed } from "vue";
import {
  VBtn,
  VCard,
  VCardActions,
  VCardText,
  VCardTitle,
  VDataTable,
  VDialog,
  VIcon,
  VSpacer,
  VTooltip,
} from "vuetify/components";

const props = defineProps<{
  modelValue: boolean;
  eWOBuergerDaten: EWOBuergerDaten[];
}>();

type ReadonlyHeaders = VDataTable["$props"]["headers"];

const headers: ReadonlyHeaders = [
  {
    title: "Auswählen",
    value: "actions",
    align: "start",
    sortable: false,
  },
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
  {
    title: "Auskunftssperre",
    value: "auskunftsssperre",
    align: "start",
    sortable: true,
  },
  {
    title: "Bereits erfasst",
    value: "ewo_id_bereitserfasst",
    align: "start",
    sortable: true,
  },
];
const emits = defineEmits<{
  (e: "update:modelValue", v: boolean): void;
  (e: "selectBuerger", v: EWOBuergerDaten): void;
  (e: "cancelBuergerSelect"): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});
function selectBuerger(eWOBuergerDaten: EWOBuergerDaten) {
  emits("selectBuerger", eWOBuergerDaten);
}
function cancelBuergerSelect() {
  emits("cancelBuergerSelect");
}
</script>

<style scoped>
.auskunftssperre {
  background: lightcoral;
}
</style>
