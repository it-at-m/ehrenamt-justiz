<template>
  <v-dialog v-model="visible">
    <v-card>
      <v-card-title class="text-headline-small">{{
        t("routes.ewobuergerselect.title")
      }}</v-card-title>
      <v-card-text>{{ t("routes.ewobuergerselect.text") }}</v-card-text>
      <v-data-table
        :items="eWOBuergerData"
        :headers="headers"
        multi-sort
        class="elevation-1"
        :row-props="getRowProps"
      >
        <template #[`item.actions`]="{ internalItem }">
          <v-tooltip location="bottom">
            <template #activator="{ props: tooltipProps }">
              <v-icon
                  v-bind="tooltipProps"
                  :icon="mdiAccountPlus"
                  @click="selectBuerger(internalItem.raw)"
                  @keydown.enter.prevent="selectBuerger(internalItem.raw)"
              />
            </template>
            <span>{{
                t("routes.ewobuergerselect.table.actions.tooltip")
              }}</span>
          </v-tooltip>
        </template>

        <template #[`item.geburtsdatum`]="{ internalItem }">
          {{ new Date(internalItem.raw.geburtsdatum).toLocaleDateString() }}
        </template>

        <template #[`item.auskunftssperre`]="{ internalItem }">
          {{
            internalItem.raw.auskunftssperre.length > 0
                ? t("routes.ewobuergerselect.table.auskunftssperre.content")
                : ""
          }}
        </template>

        <template #[`item.ewoidbereitserfasst`]="{ internalItem }">
          {{
            internalItem.raw.ewoidbereitserfasst
                ? t("routes.ewobuergerselect.table.bereitsErfasst.contentYes")
                : t("routes.ewobuergerselect.table.bereitsErfasst.contentNo")
          }}
        </template>
      </v-data-table>
      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="outlined"
          @click="cancelBuergerSelect"
        >
          {{ t("routes.ewobuergerselect.buttons.abbrechen") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import type EWOBuergerData from "@/types/EWOBuergerData";

import { mdiAccountPlus } from "@mdi/js";
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
  VIcon,
  VSpacer,
  VTooltip,
} from "vuetify/components";

const props = defineProps<{
  modelValue: boolean;
  eWOBuergerData: EWOBuergerData[];
}>();

const { t } = useI18n();
const headers = ref([
  {
    title: t("routes.ewobuergerselect.table.actions.title"),
    key: "actions",
    align: "start",
    sortable: false,
  },
  {
    title: t("routes.ewobuergerselect.table.familienname"),
    key: "familienname",
    align: "start",
    sortable: true,
  },
  {
    title: t("routes.ewobuergerselect.table.vorname"),
    key: "vorname",
    align: "start",
    sortable: true,
  },
  {
    title: t("routes.ewobuergerselect.table.geburtsdatum"),
    key: "geburtsdatum",
    align: "start",
    sortable: true,
  },
  {
    title: t("routes.ewobuergerselect.table.auskunftssperre.title"),
    key: "auskunftssperre",
    align: "start",
    sortable: true,
  },
  {
    title: t("routes.ewobuergerselect.table.bereitsErfasst.title"),
    key: "ewoidbereitserfasst",
    align: "start",
    sortable: true,
  },
] as const);
const emits = defineEmits<{
  "update:modelValue": [v: boolean];
  selectBuerger: [v: EWOBuergerData];
  cancelBuergerSelect: [];
}>();
const visible = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});
/* eslint-disable @typescript-eslint/no-explicit-any */
function getRowProps(data: any) {
  return data.item.auskunftssperre.length > 0
      ? { class: "auskunftssperre" }
      : {};
}
function selectBuerger(eWOBuergerData: EWOBuergerData) {
  emits("selectBuerger", eWOBuergerData);
}
function cancelBuergerSelect() {
  emits("cancelBuergerSelect");
}
</script>

