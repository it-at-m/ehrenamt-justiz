<template>
  <v-container>
    <v-card flat>
      <v-progress-linear
        v-if="isLoading"
        indeterminate
      />
      <template v-else>
        <h1
          class="text-h5"
          style="margin-bottom: 1em"
        >
          Konfiguration
          {{
            konfigurationData.action == BEARBEIGUNGS_MODUS.EDIT_MODUS
              ? "bearbeiten"
              : "anzeigen"
          }}
          ({{ konfigurationData.aktiv ? "Aktiv" : "Inaktiv" }})
        </h1>
        <konfiguration-form
          v-model="konfigurationData"
          :is-animation="animationAktiv"
          @save="save"
        />
      </template>
    </v-card>
    <online-help-dialog-component
      component="Das ist die Onlinehilfe für die Bearbeitung einer Konfiguration (Under Construction)"
    />
  </v-container>
</template>

<script setup lang="ts">
import type KonfigurationFormData from "@/types/KonfigurationFormData";

import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import { KonfigurationApiService } from "@/api/KonfigurationApiService";
import KonfigurationForm from "@/components/konfiguration/KonfigurationForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { BEARBEIGUNGS_MODUS } from "@/Constants";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const route = useRoute();
const router = useRouter();
const snackbarStore = useSnackbarStore();

const konfigurationData = ref<KonfigurationFormData>({
  aktiv: false,
  ehrenamtjustizart: "Verwaltungsrichter",
  bezeichnung: "",
  amtsperiodevon: "",
  amtsperiodebis: "",
  staatsangehoerigkeit: "",
  wohnsitz: "",
  altervon: 0,
  alterbis: 0,
  action: "",
});
const isLoading = ref(true);
const animationAktiv = ref(false);
const konfigurationId = ref("");
const action = ref("");

onMounted(() => {
  konfigurationId.value = route.params.id as string;
  action.value = route.params.action as string;
  loadKonfiguration();
});

function loadKonfiguration(): void {
  KonfigurationApiService.get(konfigurationId.value)
    .then((konfiguration) => {
      konfigurationData.value = {
        aktiv: konfiguration.aktiv,
        ehrenamtjustizart: konfiguration.ehrenamtjustizart,
        bezeichnung: konfiguration.bezeichnung,
        amtsperiodevon: konfiguration.amtsperiodevon.trim(),
        amtsperiodebis: konfiguration.amtsperiodebis.trim(),
        staatsangehoerigkeit: konfiguration.staatsangehoerigkeit.trim(),
        wohnsitz: konfiguration.wohnsitz.trim(),
        altervon: konfiguration.altervon,
        alterbis: konfiguration.alterbis,
        action: action.value,
      };
      isLoading.value = false;
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
      router.push({
        name: "konfiguration.index",
      });
    });
}

function save(): void {
  animationAktiv.value = true;
  KonfigurationApiService.update({
    id: konfigurationId.value,
    ehrenamtjustizart: konfigurationData.value.ehrenamtjustizart,
    bezeichnung: konfigurationData.value.bezeichnung,
    aktiv: konfigurationData.value.aktiv,
    amtsperiodevon: konfigurationData.value.amtsperiodevon,
    amtsperiodebis: konfigurationData.value.amtsperiodebis,
    staatsangehoerigkeit: konfigurationData.value.staatsangehoerigkeit,
    wohnsitz: konfigurationData.value.wohnsitz,
    altervon: konfigurationData.value.altervon,
    alterbis: konfigurationData.value.alterbis,
  })
    .then(() => {
      router.push({
        name: "konfiguration.index",
      });
    })
    .catch((err) =>
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: err,
      })
    )
    .finally(() => (animationAktiv.value = false));
}
</script>
