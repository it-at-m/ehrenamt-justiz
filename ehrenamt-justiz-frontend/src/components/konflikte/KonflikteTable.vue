<template>
  <v-container>
    <v-card flat>
      <v-card-title>
        <v-row>
          <v-col
            class="col"
            cols="4"
          >
            <v-text-field
              v-model="search"
              prepend-inner-icon="mdi-magnify"
              label="Suche"
              single-line
              hide-details
              select-strategy="all"
              clearable
              autofocus
              @keydown.enter.prevent="enableReload && reload()"
            ></v-text-field>
          </v-col>
          <v-col
            class="col"
            cols="2"
          >
            <!--            <v-btn
              :disabled="
                !user ||
                !user.authorities.includes('DELETE_EHRENAMTJUSTIZDATEN') ||
                selectedUUIDs.length == 0
              "
              color="error"
              @click="deleteRequested"
              >Löschen</v-btn
            >-->
            <v-btn
              color="error"
              @click="deleteRequested"
              >Löschen</v-btn
            >
          </v-col>
        </v-row>
      </v-card-title>
      <v-data-table-server
        v-model="selectedUUIDs"
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="personenTableData"
        :items-length="totalItems"
        show-select
        items-per-page-text="Bewerber je Seite"
        :row-props="getRowProps"
        :loading="loadingAnimationAktiv"
        :multi-sort="true"
        @update:options="loadItems"
      >
        <template #top>
          <delete-dialog
            v-model="deleteDialogVisible"
            :is-animation="deleteAnimationAktiv"
            :descriptor-string="selectedUUIDs.length + ' Konflikt'"
            :type-string="2"
            @delete="deleteConfirmed"
            @cancel="deleteCanceled"
          />
        </template>

        <template #[`item.konfliktfeld`]="{ item }">
          <span
            v-for="konflikt in item.konfliktfeld"
            :key="konflikt"
          >
            {{ konflikt }} &nbsp;
          </span>
        </template>

        <template #[`item.geburtsdatum`]="{ item }">
          <span v-if="isAuskunftssperreSichtbar(item)">{{
            new Date(item.geburtsdatum).toLocaleDateString()
          }}</span>
        </template>
        <template #[`item.derzeitausgeuebterberuf`]="{ item }">
          <span v-if="isAuskunftssperreSichtbar(item)">{{
            item.derzeitausgeuebterberuf
          }}</span>
        </template>
        <template #[`item.mailadresse`]="{ item }">
          <span v-if="isAuskunftssperreSichtbar(item)">{{
            item.mailadresse
          }}</span>
        </template>
        <template #[`item.actions`]="{ item }">
          <v-tooltip location="bottom">
            <template #activator="{ props }">
              <v-icon
                v-if="
                  AuthService.checkAuth('WRITE_EHRENAMTJUSTIZDATEN') &&
                  (item.auskunftssperre == undefined ||
                    item.auskunftssperre.length == 0 ||
                    AuthService.checkAuth(
                      'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
                    ))
                "
                v-bind="props"
                @click="konfliktLoesen(item)"
                :icon="mdiPencil"
              />
            </template>
            <span>Konflikte lösen</span>
          </v-tooltip>
        </template>
        <template
          #[`header.data-table-select`]="{
            allSelected,
            selectAll,
            someSelected,
          }"
        >
          <v-checkbox-btn
            v-if="
              AuthService.checkAuth('READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE')
            "
            :indeterminate="someSelected && !allSelected"
            :model-value="allSelected"
            color="primary"
            @update:model-value="selectAll(!allSelected)"
          ></v-checkbox-btn>
        </template>
        <template #no-data>
          {{ TABELLEN.NO_RESULTS_TEXT }}
        </template>
        <template #loading>
          {{ TABELLEN.LOADING_ITEMS }}
        </template>
      </v-data-table-server>
    </v-card>
  </v-container>
</template>

<script setup lang="ts">
import type PersonenTableData from "@/types/PersonenTableData";

import { mdiPencil } from "@mdi/js";
import { ref } from "vue";
import { useRouter } from "vue-router";
import {
  VBtn,
  VCard,
  VCardTitle,
  VCheckboxBtn,
  VCol,
  VContainer,
  VDataTable,
  VDataTableServer,
  VIcon,
  VRow,
  VTextField,
  VTooltip,
} from "vuetify/components";

import AuthService from "@/api/AuthService";
import { PersonApiService } from "@/api/PersonApiService";
import DeleteDialog from "@/components/common/DeleteDialog.vue";
import { PERSONENSTATUS, TABELLEN } from "@/Constants";
import { useSnackbarStore } from "@/stores/snackbar";

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
    align: "end",
    sortable: true,
  },
  {
    title: "Derzeitiger Beruf",
    value: "derzeitausgeuebterberuf",
    align: "start",
    sortable: true,
  },
  {
    title: "Arbeitgeber",
    value: "arbeitgeber",
    align: "start",
    sortable: true,
  },
  {
    title: "Mailadresse",
    value: "mailadresse",
    align: "start",
    sortable: true,
  },
  {
    title: "Ausgeübte Ehrenämter",
    value: "ausgeuebteehrenaemter",
    align: "start",
    sortable: true,
  },
  {
    title: "Konflikte",
    value: "konfliktfeld",
    align: "start",
    sortable: false,
  },
  {
    title: "Aktionen",
    value: "actions",
    align: "start",
    sortable: false,
  },
];

const snackbarStore = useSnackbarStore();
const router = useRouter();
const personenTableData = ref<PersonenTableData[]>([]);
const totalItems = ref(0);
const itemsPerPage = ref(10);
const itemsSort = ref();
const selectedUUIDs = ref<string[]>([]);
const search = ref("");
// Vermeidet mehrfaches Einlesen der Table, wenn während des reload() die Taste Enter gedrückt wird:
const enableReload = ref(true);
type ReadonlyHeaders = VDataTable["$props"]["headers"];
const deleteDialogVisible = ref(false);
const loadingAnimationAktiv = ref(false);
const deleteAnimationAktiv = ref(false);

/* eslint-disable @typescript-eslint/no-explicit-any */
function loadItems(options: any) {
  itemsSort.value = options.sortBy;
  loadingAnimationAktiv.value = true;
  PersonApiService.getSelectionPaged(
    search.value,
    options.page - 1,
    options.itemsPerPage,
    PERSONENSTATUS.STATUS_KONFLIKT,
    options.sortBy
  )
    .then((pagedData) => {
      personenTableData.value = [];
      personenTableData.value.push(...pagedData.data);
      totalItems.value = pagedData.totalElements;
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
    })
    .finally(() => {
      loadingAnimationAktiv.value = false;
      enableReload.value = true;
    });
}
/* eslint-enable @typescript-eslint/no-explicit-any */

function konfliktLoesen(item: { id: { toString: () => string } }) {
  router.push({
    name: "konfliktloesen.edit",
    params: {
      id: item.id.toString(),
    },
  });
}

function deleteRequested() {
  deleteDialogVisible.value = true;
}

// Personen löschen
async function deleteConfirmed() {
  deleteAnimationAktiv.value = true;
  await PersonApiService.deletePersons(selectedUUIDs.value)
    .then(() => {
      inTabelleEntfernen();
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
    })
    .finally(() => {
      deselectAll();
      deleteAnimationAktiv.value = false;
      deleteDialogVisible.value = false;
    });
}

// Löschen der selektierten Zeilen
function deselectAll() {
  selectedUUIDs.value = selectedUUIDs.value.filter((item) => item !== item);
}

function deleteCanceled() {
  deleteDialogVisible.value = false;
}

// Gelöschte Personen in Table entfernen
function inTabelleEntfernen(): void {
  personenTableData.value = personenTableData.value.filter(
    (ar) => !selectedUUIDs.value.find((rm) => rm == ar.id)
  );
}

function reload() {
  enableReload.value = false;
  personenTableData.value = [];
  deselectAll();
  loadItems({
    page: 1,
    itemsPerPage: itemsPerPage.value,
    sortBy: itemsSort.value,
  });
}
/* eslint-disable @typescript-eslint/no-explicit-any */
function getRowProps(data: any) {
  if (
    data.item.auskunftssperre != undefined &&
    data.item.auskunftssperre.length > 0
  ) {
    if (AuthService.checkAuth("READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE")) {
      return { class: "auskunftssperre" };
    } else {
      return { class: "auskunftssperre v-selection-control--disabled" };
    }
  }
}
/* eslint-enable @typescript-eslint/no-explicit-any */
function isAuskunftssperreSichtbar(person: PersonenTableData): boolean {
  return (
    person.auskunftssperre.length == 0 ||
    AuthService.checkAuth("READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE")
  );
}
</script>

<style scoped>
:deep(.auskunftssperre) {
  background: lightcoral;
}
</style>
