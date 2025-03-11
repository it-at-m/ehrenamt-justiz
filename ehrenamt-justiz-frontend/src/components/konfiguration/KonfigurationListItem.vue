<template>
  <v-list-item :class="props.konfiguration.aktiv ? 'listitem' : ''">
    <template #default>
      <v-list-item-title>{{ title }}</v-list-item-title>
      <v-list-item-subtitle>{{ subtitle }}</v-list-item-subtitle>
    </template>
    <template #append>
      <div
        v-if="
          !props.konfiguration.aktiv &&
          AuthService.checkAuth('WRITE_KONFIGURATION')
        "
      >
        <v-tooltip text="Aktiv setzen">
          <template #activator="{ props }">
            <span v-bind="props">
              <v-btn
                class="listitem"
                v-bind="props"
                @click="setActive"
                :icon="mdiLightbulbOnOutline"
              />
            </span>
          </template>
        </v-tooltip>
      </div>
      <list-item-actions
        v-if="showDelete"
        :show-set-active="true"
        :show-open="true"
        :show-edit="AuthService.checkAuth('WRITE_KONFIGURATION')"
        :show-delete="AuthService.checkAuth('DELETE_KONFIGURATION')"
        @open="displayKonfiguration"
        @edit="editKonfiguration"
        @delete="deleteRequested"
      />
      <list-item-actions
        v-else
        :show-set-active="true"
        :show-open="false"
        :show-edit="AuthService.checkAuth('WRITE_KONFIGURATION')"
        :show-delete="AuthService.checkAuth('DELETE_KONFIGURATION')"
        @open="displayKonfiguration"
        @edit="editKonfiguration"
      />
    </template>
  </v-list-item>
  <delete-dialog
    v-model="deleteDialogVisible"
    :is-animation="animationAktiv"
    :descriptor-string="`${props.konfiguration.ehrenamtjustizart}/${
      props.konfiguration.bezeichnung
    }/${new Date(
      props.konfiguration.amtsperiodevon
    ).toLocaleDateString()} - ${new Date(
      props.konfiguration.amtsperiodebis
    ).toLocaleDateString()}`"
    @delete="deleteConfirmed"
    @cancel="deleteCanceled"
  />
</template>

<script setup lang="ts">
import type Konfiguration from "@/types/Konfiguration";

import { mdiLightbulbOnOutline } from "@mdi/js";
import { computed, ref, useAttrs } from "vue";
import { useRouter } from "vue-router";
import {
  VBtn,
  VListItem,
  VListItemSubtitle,
  VListItemTitle,
  VTooltip,
} from "vuetify/components";

import { BEARBEIGUNGS_MODUS } from "@/Constants";
import AuthService from "@/api/AuthService";
import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import DeleteDialog from "@/components/common/DeleteDialog.vue";
import ListItemActions from "@/components/common/ListItemActions.vue";
import { useSnackbarStore } from "@/stores/snackbar";

const props = defineProps<{
  konfiguration: Konfiguration;
}>();
const emits = defineEmits<{
  (e: "deleted", p: Konfiguration): void;
  (e: "reloadItems"): void;
}>();
const snackbarStore = useSnackbarStore();
const router = useRouter();
const attrs = useAttrs();

const deleteDialogVisible = ref(false);
const animationAktiv = ref(false);

const title = computed(
  () =>
    ` ${props.konfiguration.ehrenamtjustizart} / ${
      props.konfiguration.bezeichnung
    } / Amtsperiode: ${new Date(
      props.konfiguration.amtsperiodevon
    ).toLocaleDateString()} - ${new Date(
      props.konfiguration.amtsperiodebis
    ).toLocaleDateString()} / Aktiv: ${props.konfiguration.aktiv}`
);
const subtitle = computed(
  () =>
    "Staatsangehörigkeit: " +
    props.konfiguration.staatsangehoerigkeit +
    " / Wohnsitz: " +
    props.konfiguration.wohnsitz +
    " / Alter von: " +
    props.konfiguration.altervon +
    " bis " +
    props.konfiguration.alterbis
);

function displayKonfiguration() {
  router.push({
    name: "konfiguration.display",
    params: {
      id: props.konfiguration.id?.toString() ?? "",
      action: BEARBEIGUNGS_MODUS.DISPLAY_MODUS,
    },
  });
}

function editKonfiguration() {
  router.push({
    name: "konfiguration.edit",
    params: {
      id: props.konfiguration.id?.toString() ?? "",
      action: BEARBEIGUNGS_MODUS.EDIT_MODUS,
    },
  });
}

function deleteRequested(): void {
  deleteDialogVisible.value = true;
}

function deleteCanceled(): void {
  deleteDialogVisible.value = false;
}

function deleteConfirmed(): void {
  animationAktiv.value = true;
  KonfigurationApiService.delete(props.konfiguration.id || "")
    .then(() => {
      emits("deleted", props.konfiguration);
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
    })
    .finally(() => {
      animationAktiv.value = false;
      deleteDialogVisible.value = false;
    });
}

function setActive(): void {
  KonfigurationApiService.setActive(props.konfiguration)
    .then(() => {
      emits("reloadItems");
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
    });
}
const showDelete = computed(() => attrs.onRemoved != null);
</script>

<style scoped>
.listitem {
  background: rgb(var(--v-theme-secondary));
}
</style>
