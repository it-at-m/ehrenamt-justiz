<template>
  <v-list
    id="infinite-scroll"
    lines="two"
    infinite-scroll-disabled="busy"
  >
    <div
      v-for="konfiguration in configuration"
      :key="`konfiguration-${konfiguration.id}`"
    >
      <konfiguration-list-item
        :konfiguration="konfiguration"
        @deleted="itemDeleted"
        @reload-items="reloadItems"
        @removed="false"
      />
      <v-divider class="divider" />
    </div>

    <v-list-item v-if="allPagesLoaded">
      <div class="text--secondary d-flex justify-center">
        {{
          t("components.konfigurationList.eintraegeInsgesamt", {
            count: totalElements,
          })
        }}
      </div>
    </v-list-item>
    <template
      v-if="!busy && (configuration === undefined || configuration.length < 1)"
    >
      <v-list-subheader>{{
        t("components.konfigurationList.keineKonfigurationVorhanden")
      }}</v-list-subheader>
    </template>
    <template v-if="busy">
      <v-progress-linear
        indeterminate
        color="accent"
      />
    </template>
  </v-list>
</template>

<script setup lang="ts">
import type KonfigurationData from "@/types/KonfigurationData";

import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import {
  VDivider,
  VList,
  VListItem,
  VListSubheader,
  VProgressLinear,
} from "vuetify/components";

import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import KonfigurationListItem from "@/components/konfiguration/KonfigurationListItem.vue";
import { useSnackbarStore } from "@/stores/snackbar";

const snackbarStore = useSnackbarStore();

let configuration = ref<KonfigurationData[]>([]);

const busy = ref(false);

const currentPage = ref(-1);

const totalPages = ref(Number.MAX_VALUE);

const totalElements = ref<number | null>(null);
const { t } = useI18n();
onMounted(() => {
  loadMore();
});

function loadMore(): void {
  busy.value = true;
  KonfigurationApiService.getAllPaged(
    currentPage.value + 1,
    20,
    "konfigurationListItem"
  )
    .then((pagedConfiguration) => {
      configuration.value.push(...pagedConfiguration.data);
      currentPage.value = pagedConfiguration.page;
      totalPages.value = pagedConfiguration.totalPages;
      totalElements.value = pagedConfiguration.totalElements;
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
    })
    .finally(() => {
      busy.value = false;
    });
}

function itemDeleted(konfiguration: KonfigurationData): void {
  configuration.value = configuration.value.filter(
    (p) => p.id != konfiguration.id
  );
}

function reloadItems(): void {
  configuration = ref<KonfigurationData[]>([]);
  currentPage.value = -1;
  loadMore();
}

const allPagesLoaded = computed(() => {
  return currentPage.value >= totalPages.value - 1;
});
</script>
