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
              :label="t('components.vorschlaegeTable.suche')"
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
            <v-btn
              :disabled="
                !user ||
                !user.authorities.includes('DELETE_EHRENAMTJUSTIZDATEN') ||
                selectedUUIDs.length == 0
              "
              color="error"
              @click="deleteRequested"
              >{{ t("components.vorschlaegeTable.buttons.loeschen") }}</v-btn
            >
          </v-col>
          <v-col
            class="col"
            cols="3"
          >
            <v-btn
              :disabled="
                !user ||
                !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN') ||
                selectedUUIDs.length == 0
              "
              color="accent"
              :loading="bewerberListeAnimationAktiv"
              @click="aufBewerberlisteSetzen"
              >{{
                t(
                  "components.vorschlaegeTable.buttons.aufBewerbungsListeSetzen"
                )
              }}</v-btn
            >
          </v-col>
          <v-col
            class="col"
            cols="3"
          >
            <v-btn
              :disabled="
                !user ||
                !user.authorities.includes(
                  'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
                )
              "
              color="accent"
              @click="datenHerunterladen"
              >{{
                t("components.vorschlaegeTable.buttons.datenHerunterladen")
              }}</v-btn
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
        :items-per-page-text="
          t('components.vorschlaegeTable.table.itemsperpagetext')
        "
        :row-props="getRowProps"
        :loading="loadingAnimationAktiv"
        :multi-sort="true"
        @update:options="loadItems"
      >
        <template #top>
          <delete-dialog
            v-model="deleteDialogVisible"
            :is-animation="deleteAnimationAktiv"
            :descriptor-string="
              t('components.vorschlaegeTable.table.delete.dialogtext', {
                count: selectedUUIDs.length,
              })
            "
            :type-string="2"
            @delete="deleteConfirmed"
            @cancel="deleteCanceled"
          />
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
          <v-tooltip
            v-if="AuthService.checkAuth('WRITE_EHRENAMTJUSTIZDATEN')"
            location="bottom"
          >
            <template #activator="{ props }">
              <span v-bind="props">
                <v-icon
                  v-bind="props"
                  @click="editItem(item)"
                  :icon="mdiPencil"
              /></span>
            </template>
            <span>{{
              t("components.vorschlaegeTable.table.actions.bewerberAendern")
            }}</span>
          </v-tooltip>
          <v-tooltip
            v-if="AuthService.checkAuth('READ_EHRENAMTJUSTIZDATEN')"
            location="bottom"
          >
            <template #activator="{ props }">
              <span v-bind="props">
                <v-icon
                  v-bind="props"
                  @click="displayItem(item)"
                  :icon="mdiEye"
              /></span>
            </template>
            <span>{{
              t("components.vorschlaegeTable.table.actions.bewerberAnzeigen")
            }}</span>
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
          {{ t("general.listeIstLeer") }}
        </template>
        <template #loading>
          {{ t("general.datenWerdenGeladen") }}
        </template>
      </v-data-table-server>
    </v-card>
    <yes-no-dialog
      v-model="yesNoDialogVisible"
      :dialogtitle="
        t(
          'components.vorschlaegeTable.table.aufBewerberlisteSetzen.dialogtitle'
        )
      "
      :dialogtext="
        t(
          'components.vorschlaegeTable.table.aufBewerberlisteSetzen.dialogtext',
          { count: selectedUUIDs.length }
        )
      "
      :is-animation="bewerberListeAnimationAktiv"
      @no="abbruchAufBewerbungslisteSetzen"
      @yes="aufBewerbungslisteUebernehmen"
    />
  </v-container>
</template>

<script setup lang="ts">
import type PersonenTableData from "@/types/PersonenTableData";

import { mdiEye, mdiPencil } from "@mdi/js";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
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
import { EhrenamtJustizService } from "@/api/EhrenamtJustizService";
import { PersonApiService } from "@/api/PersonApiService";
import DeleteDialog from "@/components/common/DeleteDialog.vue";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { BEARBEIGUNGS_MODUS, PERSONENSTATUS } from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";

const { t } = useI18n();
const headers: ReadonlyHeaders = computed(() => [
  {
    title: t("components.vorschlaegeTable.table.familienname"),
    value: "familienname",
    align: "start",
    sortable: true,
  },
  {
    title: t("components.vorschlaegeTable.table.vorname"),
    value: "vorname",
    align: "start",
    sortable: true,
  },
  {
    title: t("components.vorschlaegeTable.table.geburtsdatum"),
    value: "geburtsdatum",
    align: "end",
    sortable: true,
  },
  {
    title: t("components.vorschlaegeTable.table.derzeitigerBeruf"),
    value: "derzeitausgeuebterberuf",
    align: "start",
    sortable: true,
  },
  {
    title: t("components.vorschlaegeTable.table.arbeitgeber"),
    value: "arbeitgeber",
    align: "start",
    sortable: true,
  },
  {
    title: t("components.vorschlaegeTable.table.mailAdresse"),
    value: "mailadresse",
    align: "start",
    sortable: true,
  },
  {
    title: t("components.vorschlaegeTable.table.ausgeuebteEhrenaemter"),
    value: "ausgeuebteehrenaemter",
    align: "start",
    sortable: true,
  },
  {
    title: t("components.vorschlaegeTable.table.actions.header"),
    value: "actions",
    align: "start",
    sortable: false,
  },
]) as unknown as ReadonlyHeaders;
const snackbarStore = useSnackbarStore();
const router = useRouter();
const personenTableData = ref<PersonenTableData[]>([]);
const totalItems = ref(0);
const itemsPerPage = ref(10);
const itemsSort = ref();
const selectedUUIDs = ref<string[]>([]);
const search = ref("");
// Avoids multiple reading of the table if the Enter key is pressed during reload():
const enableReload = ref(true);
type ReadonlyHeaders = VDataTable["$props"]["headers"];
const deleteDialogVisible = ref(false);
const loadingAnimationAktiv = ref(false);
const deleteAnimationAktiv = ref(false);
const bewerberListeAnimationAktiv = ref(false);
const yesNoDialogVisible = ref(false);
const userStore = useUserStore();
const user = userStore.getUser;

/* eslint-disable @typescript-eslint/no-explicit-any */
function loadItems(options: any) {
  itemsSort.value = options.sortBy;
  loadingAnimationAktiv.value = true;
  PersonApiService.getSelectionPaged(
    search.value,
    options.page - 1,
    options.itemsPerPage,
    PERSONENSTATUS.STATUS_VORSCHLAG,
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

function editItem(item: { id: { toString: () => string } }) {
  router.push({
    name: "bewerbung.edit",
    params: {
      id: item.id.toString(),
      action: BEARBEIGUNGS_MODUS.EDIT_MODUS,
    },
  });
}

function displayItem(item: { id: { toString: () => string } }) {
  router.push({
    name: "bewerbung.display",
    params: {
      id: item.id.toString(),
      action: BEARBEIGUNGS_MODUS.DISPLAY_MODUS,
    },
  });
}

function deleteRequested() {
  deleteDialogVisible.value = true;
}

// Delete person
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

// Remove selected lines
function deselectAll() {
  selectedUUIDs.value = selectedUUIDs.value.filter((item) => item !== item);
}

function deleteCanceled() {
  deleteDialogVisible.value = false;
}

// Remove deleted person from table
function inTabelleEntfernen(): void {
  personenTableData.value = personenTableData.value.filter(
    (ar) => !selectedUUIDs.value.find((rm) => rm == ar.id)
  );
}

async function aufBewerberlisteSetzen(): Promise<void> {
  yesNoDialogVisible.value = true;
}

function abbruchAufBewerbungslisteSetzen() {
  yesNoDialogVisible.value = false;
}

async function aufBewerbungslisteUebernehmen() {
  bewerberListeAnimationAktiv.value = true;
  await PersonApiService.aufBewerberlisteSetzen(selectedUUIDs.value)
    .then(() => {
      inTabelleEntfernen();
      deselectAll();
      yesNoDialogVisible.value = false;
    })
    .finally(() => {
      bewerberListeAnimationAktiv.value = false;
    });
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

function datenHerunterladen() {
  const globalSettingsStore = useGlobalSettingsStore();
  const dateiname =
    (globalSettingsStore.getKonfiguration?.ehrenamtjustizart ?? "") +
    "_" +
    PERSONENSTATUS.STATUS_VORSCHLAG +
    "_";
  EhrenamtJustizService.convertToCSVFile(
    t,
    selectedUUIDs.value,
    dateiname,
    PERSONENSTATUS.STATUS_VORSCHLAG
  );
}
</script>

<style scoped>
:deep(.auskunftssperre) {
  background: lightcoral;
}
</style>
