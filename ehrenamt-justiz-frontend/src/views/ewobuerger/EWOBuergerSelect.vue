<template>
  <v-dialog v-model="visible">
    <v-card>
      <v-card-title class="text-h5">{{
        t("views.eWOBuergerSelect.title")
      }}</v-card-title>
      <v-card-text>{{ t("views.eWOBuergerSelect.text") }}</v-card-text>
      <v-data-table
        :items="eWOBuergerData"
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
                <span>{{
                  t("views.eWOBuergerSelect.table.actions.tooltip")
                }}</span>
              </v-tooltip>
            </td>
            <td>{{ item.familienname }}</td>
            <td>{{ item.vorname }}</td>
            <td>{{ new Date(item.geburtsdatum).toLocaleDateString() }}</td>
            <td>
              {{
                item.auskunftssperre.length > 0
                  ? t("views.eWOBuergerSelect.table.auskunftssperre.content")
                  : ""
              }}
            </td>
            <td>
              {{
                item.ewoidbereitserfasst
                  ? t("views.eWOBuergerSelect.table.bereitsErfasst.contentYes")
                  : t("views.eWOBuergerSelect.table.bereitsErfasst.contentNo")
              }}
            </td>
          </tr>
        </template>
      </v-data-table>
      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="outlined"
          @click="cancelBuergerSelect"
        >
          {{ t("views.eWOBuergerSelect.buttons.abbrechen") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type EWOBuergerData from "@/types/EWOBuergerData";

import { mdiAccountPlus } from "@mdi/js";
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
  VIcon,
  VSpacer,
  VTooltip,
} from "vuetify/components";

const props = defineProps<{
  modelValue: boolean;
  eWOBuergerData: EWOBuergerData[];
}>();

type ReadonlyHeaders = VDataTable["$props"]["headers"];
const { t } = useI18n();
const headers: ReadonlyHeaders = computed(() => [
  {
    title: t("views.eWOBuergerSelect.table.actions.title"),
    value: "actions",
    align: "start",
    sortable: false,
  },
  {
    title: t("views.eWOBuergerSelect.table.familienname"),
    value: "familienname",
    align: "start",
    sortable: true,
  },
  {
    title: t("views.eWOBuergerSelect.table.vorname"),
    value: "vorname",
    align: "start",
    sortable: true,
  },
  {
    title: t("views.eWOBuergerSelect.table.geburtsdatum"),
    value: "geburtsdatum",
    align: "start",
    sortable: true,
  },
  {
    title: t("views.eWOBuergerSelect.table.auskunftssperre.title"),
    value: "auskunftssperre",
    align: "start",
    sortable: true,
  },
  {
    title: t("views.eWOBuergerSelect.table.bereitsErfasst.title"),
    value: "ewoidbereitserfasst",
    align: "start",
    sortable: true,
  },
]) as unknown as ReadonlyHeaders;
const emits = defineEmits<{
  "update:modelValue": [v: boolean];
  selectBuerger: [v: EWOBuergerData];
  cancelBuergerSelect: [];
}>();
const visible = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});
function selectBuerger(eWOBuergerData: EWOBuergerData) {
  emits("selectBuerger", eWOBuergerData);
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
