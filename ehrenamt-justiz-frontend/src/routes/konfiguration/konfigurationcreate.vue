<template>
  <v-container>
    <v-card flat>
      <h1
        class="text-headline-small"
        style="margin-bottom: 1em"
      >
        {{ t("routes.konfigurationcreate.header") }}
      </h1>
      <konfiguration-form
        v-model="konfigurationData"
        :is-animation="animationAktiv"
        @save="save"
      />
    </v-card>
    <online-help-dialog-component
      :helptext="t('routes.konfigurationcreate.onlineHelp')"
    />
  </v-container>
</template>

<script setup lang="ts">
import type KonfigurationFormData from "@/types/KonfigurationFormData";

import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { VCard, VContainer } from "vuetify/components";

import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import KonfigurationForm from "@/components/konfiguration/KonfigurationForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { BEARBEIGUNGS_MODUS, STATUS_INDICATORS } from "@/Constants";
import { useSnackbarStore } from "@/stores/snackbar";

const snackbarStore = useSnackbarStore();
const router = useRouter();
const { t } = useI18n();

const konfigurationData = ref<KonfigurationFormData>({
  aktiv: false,
  ehrenamtjustizart: "VERWALTUNGSRICHTER",
  bezeichnung: "",
  amtsperiodevon: "",
  amtsperiodebis: "",
  staatsangehoerigkeit: "",
  wohnsitz: "",
  altervon: 0,
  alterbis: 0,
  action: BEARBEIGUNGS_MODUS.EDIT_MODUS,
  vorlageBestaetigungverfassungstreue_file: undefined,
});

const animationAktiv = ref(false);

async function save() {
  animationAktiv.value = true;
  try {
    await KonfigurationApiService.updateKonfiguration(
      {
        id: undefined,
        ehrenamtjustizart: konfigurationData.value.ehrenamtjustizart,
        bezeichnung: konfigurationData.value.bezeichnung,
        aktiv: konfigurationData.value.aktiv,
        amtsperiodevon: konfigurationData.value.amtsperiodevon.trim(),
        amtsperiodebis: konfigurationData.value.amtsperiodebis.trim(),
        staatsangehoerigkeit:
          konfigurationData.value.staatsangehoerigkeit.trim(),
        wohnsitz: konfigurationData.value.wohnsitz.trim(),
        altervon: konfigurationData.value.altervon,
        alterbis: konfigurationData.value.alterbis,
      },
      konfigurationData.value.vorlageBestaetigungverfassungstreue_file
    );
    await router.push({
      name: "/konfiguration/konfigurationindex",
    });
  } catch (err: unknown) {
    snackbarStore.push({
      text: err instanceof Error ? err.message : String(err),
      color: STATUS_INDICATORS.ERROR,
    });
  } finally {
    animationAktiv.value = false;
  }
}
</script>
