<template>
  <v-container>
    <v-card flat>
      <e-w-o-buerger-form
        v-model="ewobuergerSuche"
        :is-animation="animationAktiv"
        @save="save"
      />
      <e-w-o-buerger-select
        :model-value="ewoBuergerSelectVisible"
        :e-w-o-buerger-data="ewoBuergerSelect"
        @cancel-buerger-select="cancelBuergerSelect"
        @select-buerger="einBuergerAusgewaehlt"
      ></e-w-o-buerger-select>
    </v-card>
    <online-help-dialog-component
      :helptext="t('routes.ewobuergercreate.onlineHelp')"
    />
  </v-container>
</template>

<script setup lang="ts">
import type EWOBuergerData from "@/types/EWOBuergerData.ts";
import type EWOBuergerSuche from "@/types/EWOBuergerSuche.ts";

import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { VCard, VContainer } from "vuetify/components";

import { EWOBuergerApiService } from "@/api/EWOBuergerApiService.ts";
import EWOBuergerForm from "@/components/ewobuerger/EWOBuergerForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { BEARBEIGUNGS_MODUS, STATUS_INDICATORS } from "@/Constants.ts";
import EWOBuergerSelect from "@/routes/ewobuerger/ewobuergerselect.vue";
import { useSnackbarStore } from "@/stores/snackbar.ts";

const snackbarStore = useSnackbarStore();

const router = useRouter();

const { t } = useI18n();

const ewoBuergerSelect = ref<EWOBuergerData[]>([]);

const ewoBuergerSelectVisible = ref(false);

const ewobuergerSuche = ref<EWOBuergerSuche>({
  familienname: "",
  vorname: "",
  geburtsdatum: "",
  validierungdeaktivieren: false,
});

const animationAktiv = ref(false);

async function save() {
  animationAktiv.value = true;
  try {
    await EWOBuergerApiService.getEwoSuche({
      familienname: ewobuergerSuche.value.familienname.trim(),
      vorname: ewobuergerSuche.value.vorname.trim(),
      geburtsdatum: ewobuergerSuche.value.geburtsdatum.trim(),
      validierungdeaktivieren: ewobuergerSuche.value.validierungdeaktivieren,
    })
      .then((ewoBuergers) => {
        if (ewoBuergers.length == 0) {
          // No citizen found
          snackbarStore.push({
            color: STATUS_INDICATORS.WARNING,
            text: t("routes.ewobuergercreate.keinePersonGefundenMessage"),
          });
        } else if (ewoBuergers.length > 1) {
          // More than one citizen: Select citizen by User
          ewoBuergerSelect.value = ewoBuergers;
          ewoBuergerSelectVisible.value = true;
        } else if (ewoBuergers[0]) {
          // Check, if person already exist
          return EWOBuergerApiService.pruefenNeuePerson(ewoBuergers[0]).then(
            (person) => {
              if (person == null && ewoBuergers[0]) {
                // Prepare person data from EWO und insert data in DB
                return EWOBuergerApiService.vorbereitenUndSpeichernPerson(
                  ewoBuergers[0]
                ).then(() => {
                  router.push({
                    name: "/bewerbungen/bewerbungedit/[id][action]",
                    params: {
                      id:
                        ewoBuergers[0] && ewoBuergers[0].id
                          ? ewoBuergers[0].id
                          : "",
                      action: BEARBEIGUNGS_MODUS.EDIT_MODUS,
                    },
                  });
                });
              } else if (person && ewoBuergers[0]) {
                snackbarStore.push({
                  color: STATUS_INDICATORS.ERROR,
                  text: t("routes.ewobuergercreate.bereitsVorhandenMessage", {
                    ordnungsmerkmal: ewoBuergers[0].ordnungsmerkmal,
                    status: person.status,
                  }),
                });
                return;
              }
            }
          );
        } else {
          // No citizen found
          snackbarStore.push({
            color: STATUS_INDICATORS.WARNING,
            text: t("routes.ewobuergercreate.keinePersonGefundenMessage"),
          });
        }
      })
      .catch((error: Error) => {
        snackbarStore.push({
          text: error.message,
          color: STATUS_INDICATORS.ERROR,
        });
      });
  } finally {
    animationAktiv.value = false;
  }
}

async function einBuergerAusgewaehlt(ewoBuerger: EWOBuergerData) {
  // Check if person alread exist
  await EWOBuergerApiService.pruefenNeuePerson(ewoBuerger).then((person) => {
    if (person == null) {
      ewoBuergerSelectVisible.value = false;
      // Prepare person data from EWO and insert data in DB
      EWOBuergerApiService.vorbereitenUndSpeichernPerson(ewoBuerger).then(
        () => {
          router.push({
            name: "/bewerbungen/bewerbungedit/[id][action]",
            params: {
              id: ewoBuerger.id ? ewoBuerger.id : "",
              action: BEARBEIGUNGS_MODUS.EDIT_MODUS,
            },
          });
        }
      );
    } else {
      snackbarStore.push({
        color: STATUS_INDICATORS.ERROR,
        text: t("routes.ewobuergercreate.bereitsVorhandenMessage", {
          ordnungsmerkmal: ewoBuerger.ordnungsmerkmal,
          status: person.status,
        }),
      });
    }
  });
}

async function cancelBuergerSelect() {
  ewoBuergerSelectVisible.value = false;
}
</script>
