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
      :helptext="t('views.eWOBuergerCreate.onlinehelp')"
    />
  </v-container>
</template>

<script setup lang="ts">
import type EWOBuergerData from "@/types/EWOBuergerData";
import type EWOBuergerSuche from "@/types/EWOBuergerSuche";

import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import { VCard, VContainer } from "vuetify/components";

import { EWOBuergerApiService } from "@/api/EWOBuergerApiService";
import EWOBuergerForm from "@/components/ewobuerger/EWOBuergerForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { BEARBEIGUNGS_MODUS } from "@/Constants";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";
import EWOBuergerSelect from "@/views/ewobuerger/EWOBuergerSelect.vue";

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
    }).then((ewoBuergers) => {
      if (ewoBuergers.length == 0) {
        // No citizen found
        snackbarStore.showMessage({
          level: STATUS_INDICATORS.WARNING,
          message: "Keine Person im Einwohnermeldewesen gefunden!",
          show: true,
        });
      } else if (ewoBuergers.length > 1) {
        // More than one citizen: Select citizen by User
        ewoBuergerSelect.value = ewoBuergers;
        ewoBuergerSelectVisible.value = true;
      } else {
        // Check, if person already exist
        EWOBuergerApiService.pruefenNeuePerson(ewoBuergers[0]).then(
          (person) => {
            if (person == null) {
              // Prepare person data from EWO und insert data in DB
              EWOBuergerApiService.vorbereitenUndSpeichernPerson(
                ewoBuergers[0]
              ).then(() => {
                router.push({
                  name: "bewerbung.edit",
                  params: {
                    id: ewoBuergers[0].id ? ewoBuergers[0].id : "",
                    action: BEARBEIGUNGS_MODUS.EDIT_MODUS,
                  },
                });
              });
            } else {
              snackbarStore.showMessage({
                level: STATUS_INDICATORS.ERROR,
                message: `Ein Bürger mit dem Ordnungsmerkmal ${ewoBuergers[0].ordnungsmerkmal} ist bereits als ${person.status} im System vorhanden`,
              });
            }
          }
        );
      }
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

async function einBuergerAusgewaehlt(ewoBuerger: EWOBuergerData) {
  // Check if person alread exist
  await EWOBuergerApiService.pruefenNeuePerson(ewoBuerger).then((person) => {
    if (person == null) {
      ewoBuergerSelectVisible.value = false;
      // Prepare person data from EWO and insert data in DB
      EWOBuergerApiService.vorbereitenUndSpeichernPerson(ewoBuerger).then(
        () => {
          router.push({
            name: "bewerbung.edit",
            params: {
              id: ewoBuerger.id ? ewoBuerger.id : "",
              action: BEARBEIGUNGS_MODUS.EDIT_MODUS,
            },
          });
        }
      );
    } else {
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: `Ein Bürger mit dem Ordnungsmerkmal ${ewoBuerger.ordnungsmerkmal} ist bereits als ${person.status} im System vorhanden`,
      });
    }
  });
}

async function cancelBuergerSelect() {
  ewoBuergerSelectVisible.value = false;
}
</script>
