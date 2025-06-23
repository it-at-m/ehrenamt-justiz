<template>
  <v-dialog v-model="visible">
    <v-card>
      <v-card-title class="text-h5">{{
        t("views.invalidePersonenSelect.title")
      }}</v-card-title>
      <v-card-text> {{ t("views.invalidePersonenSelect.text") }}</v-card-text>
      <v-data-table
        :items="invalidePersonen"
        :headers="headers"
        multi-sort
        class="elevation-1"
        :items-per-page-text="
          t('views.invalidePersonenSelect.table.itemsPerPageText')
        "
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
        <v-btn @click="cancelInvalidePersonenSelect">{{
          t("views.invalidePersonenSelect.buttons.abbrechen")
        }}</v-btn>
        <v-btn
          class="bg-accent"
          :loading="vorschlagsListeAnimationAktiv"
          @click="invalidePersonenSelect"
        >
          {{ t("views.invalidePersonenSelect.buttons.validierungUmgehen") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type PersonenTableData from "@/types/PersonenTableData";

import { computed } from "vue";
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

type ReadonlyHeaders = VDataTable["$props"]["headers"];

const headers: ReadonlyHeaders = computed(() => [
  {
    title: t("views.invalidePersonenSelect.table.familienname"),
    value: "familienname",
    align: "start",
    sortable: true,
  },
  {
    title: t("views.invalidePersonenSelect.table.vorname"),
    value: "vorname",
    align: "start",
    sortable: true,
  },
  {
    title: t("views.invalidePersonenSelect.table.geburtsdatum"),
    value: "geburtsdatum",
    align: "start",
    sortable: true,
  },
]) as unknown as ReadonlyHeaders;
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
