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
        <v-tooltip
          :text="t('components.konfigurationListItem.tooltip.aktivSetzen')"
        >
          <template #activator="{ props }">
            <span v-bind="props">
              <v-btn
                class="listitem"
                v-bind="props"
                @click="anfordernsetActive"
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
  <yes-no-dialog
      v-model="yesNoDialogVisible"
      :dialogtitle="
        t(
          'components.konfigurationListItem.activSetzen.dialogtitle'
        )
      "
      :dialogtext="
        t(
          'components.konfigurationListItem.activSetzen.dialogtext'
        )
      "
      @no="abbruchSetActive"
      @yes="setActive"
  />
</template>

<script setup lang="ts">
import type KonfigurationData from "@/types/KonfigurationData";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { mdiLightbulbOnOutline } from "@mdi/js";
import { computed, ref, useAttrs } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import {
  VBtn,
  VListItem,
  VListItemSubtitle,
  VListItemTitle,
  VTooltip,
} from "vuetify/components";

import AuthService from "@/api/AuthService";
import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import DeleteDialog from "@/components/common/DeleteDialog.vue";
import ListItemActions from "@/components/common/ListItemActions.vue";
import { BEARBEIGUNGS_MODUS } from "@/Constants";
import { useSnackbarStore } from "@/stores/snackbar";

const props = defineProps<{
  konfiguration: KonfigurationData;
}>();
const emits = defineEmits<{
  deleted: [p: KonfigurationData];
}>();
const snackbarStore = useSnackbarStore();
const router = useRouter();
const attrs = useAttrs();

const deleteDialogVisible = ref(false);
const animationAktiv = ref(false);
const yesNoDialogVisible = ref(false);

const title = computed(
  () =>
    ` ${props.konfiguration.ehrenamtjustizart} / ${
      props.konfiguration.bezeichnung
    } ${t("components.konfigurationListItem.title.amtsperiode")} ${new Date(
      props.konfiguration.amtsperiodevon
    ).toLocaleDateString()} - ${new Date(
      props.konfiguration.amtsperiodebis
    ).toLocaleDateString()} ${t("components.konfigurationListItem.title.aktiv")} ${props.konfiguration.aktiv}`
);
const subtitle = computed(
  () =>
    t("components.konfigurationListItem.subTitle.staatsangehoerigkeit") +
    props.konfiguration.staatsangehoerigkeit +
    t("components.konfigurationListItem.subTitle.wohnsitz") +
    props.konfiguration.wohnsitz +
    t("components.konfigurationListItem.subTitle.alterVon") +
    props.konfiguration.altervon +
    t("components.konfigurationListItem.subTitle.alterBis") +
    props.konfiguration.alterbis
);
const { t } = useI18n();

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


async function anfordernsetActive(): Promise<void> {
  yesNoDialogVisible.value = true;
}


function setActive(): void {
  KonfigurationApiService.setActive(props.konfiguration)
    .then(() => {
      // Complete reload of the current page and application
      window.location.reload();
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
    });
}

function abbruchSetActive() {
  yesNoDialogVisible.value = false;
}

const showDelete = computed(() => attrs.onRemoved != null);
</script>

<style scoped>
.listitem {
  background: rgb(var(--v-theme-secondary));
}
</style>
