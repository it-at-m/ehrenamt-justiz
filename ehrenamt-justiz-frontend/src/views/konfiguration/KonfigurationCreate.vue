<template>
  <v-container>
    <v-card flat>
      <h1
        class="text-h5"
        style="margin-bottom: 1em"
      >
        Konfiguration erstellen
      </h1>
      <konfiguration-form
        v-model="konfigurationData"
        :is-animation="animationAktiv"
        @save="save"
      />
    </v-card>
    <online-help-dialog-component
      component="Das ist die Onlinehilfe für die Erfassung einer Konfiguration (Under Construction)"
    />
  </v-container>
</template>

<script setup lang="ts">
import type KonfigurationFormData from "@/types/KonfigurationFormData";

import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { VCard, VContainer } from "vuetify/components";

import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import KonfigurationForm from "@/components/konfiguration/KonfigurationForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { BEARBEIGUNGS_MODUS } from "@/Constants";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const snackbarStore = useSnackbarStore();
const router = useRouter();

const konfigurationData: KonfigurationFormData = reactive({
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
});
const animationAktiv = ref(false);

async function save() {
  animationAktiv.value = true;
  try {
    await KonfigurationApiService.updateKonfiguration({
      id: undefined,
      ehrenamtjustizart: konfigurationData.ehrenamtjustizart,
      bezeichnung: konfigurationData.bezeichnung,
      aktiv: konfigurationData.aktiv,
      amtsperiodevon: konfigurationData.amtsperiodevon.trim(),
      amtsperiodebis: konfigurationData.amtsperiodebis.trim(),
      staatsangehoerigkeit: konfigurationData.staatsangehoerigkeit.trim(),
      wohnsitz: konfigurationData.wohnsitz.trim(),
      altervon: konfigurationData.altervon,
      alterbis: konfigurationData.alterbis,
    });
    await router.push({
      name: "konfiguration.index",
    });
  } catch (err) {
    snackbarStore.showMessage({
      level: STATUS_INDICATORS.ERROR,
      message: err as string | undefined,
    });
  } finally {
    animationAktiv.value = false;
  }
}
</script>
