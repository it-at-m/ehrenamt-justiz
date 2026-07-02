<template>
  <v-dialog v-model="visible">
    <v-card>
      <v-card-title class="text-headline-small">{{
        t("routes.invalidepersonenselect.title")
      }}</v-card-title>
      <v-card-text> {{ t("routes.invalidepersonenselect.text") }}</v-card-text>
      <v-data-table
        :items="invalidePersonen"
        :headers="headers"
        :multi-sort="true"
        class="elevation-1"
        :items-per-page-text="
          t('routes.invalidepersonenselect.table.itemsPerPageText')
        "
      >
        <template #item="{ internalItem }">
          <tr>
            <td>{{ internalItem.raw.familienname }}</td>
            <td>{{ internalItem.raw.vorname }}</td>
            <td>
              {{ new Date(internalItem.raw.geburtsdatum).toLocaleDateString() }}
            </td>
          </tr>
        </template>
      </v-data-table>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="cancelInvalidePersonenSelect">{{
          t("routes.invalidepersonenselect.buttons.abbrechen")
        }}</v-btn>
        <v-btn
          class="bg-accent"
          :loading="vorschlagsListeAnimationAktiv"
          @click="invalidePersonenSelect"
        >
          {{ t("routes.invalidepersonenselect.buttons.validierungUmgehen") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type PersonenTableData from "@/types/PersonenTableData";

import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
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

const { t } = useI18n();

const props = defineProps<{
  modelValue: boolean;
  vorschlagsListeAnimationAktiv: boolean;
  invalidePersonen: PersonenTableData[];
}>();

const headers = ref([
  {
    title: t("routes.invalidepersonenselect.table.familienname"),
    key: "familienname",
    align: "start",
    sortable: true,
  },
  {
    title: t("routes.invalidepersonenselect.table.vorname"),
    key: "vorname",
    align: "start",
    sortable: true,
  },
  {
    title: t("routes.invalidepersonenselect.table.geburtsdatum"),
    key: "geburtsdatum",
    align: "start",
    sortable: true,
  },
] as const);
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
