<template>
  <v-list
    id="infinite-scroll"
    lines="two"
    style="height: 420px; overflow-y: auto"
    @scroll.passive="onScroll"
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
    <v-list-item>
      <div class="text-medium-emphasis d-flex justify-center">
        {{
          t("components.konfigurationList.eintraegeInsgesamt", {
            count: totalElements,
          })
        }}
      </div>
    </v-list-item>
    <v-list-subheader
      v-if="!busy && (configuration === undefined || configuration.length < 1)"
      >{{
        t("components.konfigurationList.keineKonfigurationVorhanden")
      }}</v-list-subheader
    >
    <template v-if="busy">
      <v-progress-linear indeterminate />
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
import { STATUS_INDICATORS } from "@/Constants";
import { useSnackbarStore } from "@/stores/snackbar";

const snackbarStore = useSnackbarStore();

const configuration = ref<KonfigurationData[]>([]);

const busy = ref(false);

const currentPage = ref(-1);

const totalPages = ref(Number.MAX_VALUE);

const totalElements = ref(Number.MIN_VALUE);
const hasMore = computed(
  () => configuration.value.length < totalElements.value
);
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
    .catch((error: Error) => {
      snackbarStore.push({
        text: error.message,
        color: STATUS_INDICATORS.ERROR,
      });
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
  configuration.value = [];
  currentPage.value = -1;
  loadMore();
}

/* eslint-disable @typescript-eslint/no-explicit-any */
function onScroll(e: any) {
  const el = e.target;
  const nearBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 80;
  if (nearBottom && hasMore.value && !busy.value) {
    loadMore();
  }
}
</script>
