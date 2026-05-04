<template>
  <muc-card-container class="d-flex align-center flex-column">
    <muenchen-banner />
    <muc-card
      v-if="useActiveKonfigurationStore().getKonfiguration"
      class="d-flex align-center flex-column"
      title=""
      :disabled="false"
    >
      <template #headerPrefix
        ><div id="headerOnlineBewerbung">
          <h2>{{ t("mainView.header") }}</h2>
          <logo-l-h-m />
        </div>
      </template>
      <template #content>
        <div ref="hinweise">
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'OK'"
            :title="t('mainView.hinweise.ok.title')"
            type="success"
            variant="header"
          >
            <div>
              {{ t("mainView.hinweise.ok.text") }}
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'ERROR'"
            type="emergency"
            variant="header"
          >
            <div>
              {{ t("mainView.hinweise.error.text") }}
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'TOO_MANY_REQUESTS'"
            type="emergency"
            variant="header"
          >
            <div>
              {{ t("mainView.hinweise.toManyRequests.text") }}
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'EINGABEFEHLER'"
            type="emergency"
            variant="header"
          >
            <div>{{ t("mainView.hinweise.eingabeFehler.text") }}</div>
          </muc-banner>
          <muc-banner
            v-if="technischerfehler != ''"
            type="emergency"
            variant="header"
          >
            <div>
              {{
                t("mainView.hinweise.technischerFehler.text", {
                  cause: technischerfehler,
                })
              }}
            </div>
          </muc-banner>
        </div>
        <v-container class="v-card-main-container">
          <muc-stepper
            ref="stepperRef"
            :step-items="STEPPER_ITEMS"
            :active-item="activeStep"
            :disable-previous-steps="true"
            @change-step="changeStep"
          />
          <muc-divider />
          <div v-if="currentView === 0">
            <allgemeine-info @next="increaseCurrentView" />
          </div>
          <div v-if="currentView === 1">
            <bewerber-daten
              v-model="onlineBewerbungFormData"
              @previous-step="decreaseCurrentView"
              @check-and-next-step="checkAndIncreaseCurrentView"
            />
          </div>
          <div v-if="currentView === 2">
            <verfassungstreue-bestaetigen
              v-model="onlineBewerbungFormData"
              @previous-step="decreaseCurrentView"
              @save="speichern"
              @erstellen-verfassungstreue-muster="
                erstellenVerfassungstreueMuster
              "
            />
          </div>
          <div v-if="currentView === 3">
            <online-bewerbung-erfolg />
          </div>
          <muc-percentage-spinner
            v-if="isAnimation"
            size="40"
          />
        </v-container>
      </template>
    </muc-card>
    <muc-percentage-spinner
      v-else
      size="100"
    />
  </muc-card-container>
</template>

<script setup lang="ts">
import type OnlineBewerbungData from "@/types/OnlineBewerbungData";
import type { StepperItem } from "@/types/StepperTypes";
import type { ComponentPublicInstance } from "vue";

import {
  MucBanner,
  MucCard,
  MucCardContainer,
  MucDivider,
  MucPercentageSpinner,
  MucStepper,
} from "@muenchen/muc-patternlab-vue";
import { nextTick, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { VContainer } from "vuetify/components";

import { EhrenamtJustizOnlineServiceClass } from "@/api/EhrenamtJustizOnlineService";
import AllgemeineInfo from "@/components/AllgemeineInfo.vue";
import BewerberDaten from "@/components/BewerberDaten.vue";
import LogoLHM from "@/components/LogoLHM.vue";
import MuenchenBanner from "@/components/MuenchenBanner.vue";
import OnlineBewerbungErfolg from "@/components/OnlineBewerbungErfolg.vue";
import VerfassungstreueBestaetigen from "@/components/VerfassungstreueBestaetigen.vue";
import { useActiveKonfigurationStore } from "@/stores/activeconfig";

const { t } = useI18n();
const ehrenamtJustizOnlineService = new EhrenamtJustizOnlineServiceClass(t);
const hinweise = ref<HTMLInputElement | null>(null);
const isAnimation = ref(false);
const bewerbunggespeichertergebbnis = ref("");
const technischerfehler = ref("");
const onlineBewerbungFormData = ref<OnlineBewerbungData>({
  nachname: "",
  vorname: "",
  beruf: "",
  geburtsdatum: "",
  mail: "",
  telefonnummer: "",
  dateiVerfassungstreue: null,
});
const activeStep = ref<string>("0");
const currentView = ref<number>(0);
const decreaseCurrentView = () => currentView.value--;
const increaseCurrentView = () => currentView.value++;
const STEPPER_ITEMS: StepperItem[] = [
  {
    id: "0",
    label: t("mainView.stepper.allgemeineInfo"),
    icon: "shopping-cart",
  },
  {
    id: "1",
    label: t("mainView.stepper.bewerberDaten"),
    icon: "calendar",
  },
  {
    id: "2",
    label: t("mainView.stepper.verfassungsteueDaten"),
    icon: "mail",
  },
  {
    id: "3",
    label: t("mainView.stepper.bewerbungUebertragen"),
    icon: "mail",
  },
];

/**
 * Adjusts the current view to the active step in the stepper
 */
const changeStep = (step: string) => {
  if (parseInt(step) < parseInt(activeStep.value)) {
    currentView.value = parseInt(step);
  }
};
type StepperInstance = ComponentPublicInstance | HTMLElement | null;
const stepperRef = ref<StepperInstance>(null);
const focusActiveStepperItem = async () => {
  await nextTick();

  // Zugriff auf das gerenderte DOM des Steppers
  const rootEl =
    (stepperRef.value as ComponentPublicInstance | null)?.$el ??
    (stepperRef.value as HTMLElement | null);

  if (!rootEl) return;

  const activeIcon = rootEl.querySelector<HTMLElement>(
    ".m-form-step__icon[aria-current='step']"
  );

  activeIcon?.focus();
};
/**
 * Adjusts the active step in the stepper to the current view
 */
watch(currentView, (newCurrentView) => {
  activeStep.value = newCurrentView.toString();
  goToTop();
  focusActiveStepperItem();
});

/**
 * Sets the view to the top of the page after change the current view
 */
const goToTop = async () => {
  await nextTick();
  window.scrollTo({ top: 0, behavior: "instant" });
};

function checkAndIncreaseCurrentView(): void {
  isAnimation.value = true;
  ehrenamtJustizOnlineService
    .pruefen({
      vorname: onlineBewerbungFormData.value.vorname,
      nachname: onlineBewerbungFormData.value.nachname,
      geburtsdatum: onlineBewerbungFormData.value.geburtsdatum,
      telefonnummer: onlineBewerbungFormData.value.telefonnummer,
      beruf: onlineBewerbungFormData.value.beruf,
      mail: onlineBewerbungFormData.value.mail,
      dateiVerfassungstreue: null,
    })
    .then((bewerbunggespeichert) => {
      if (bewerbunggespeichert == "OK") {
        bewerbunggespeichertergebbnis.value = "";
        increaseCurrentView();
      } else {
        bewerbunggespeichertergebbnis.value = bewerbunggespeichert;
      }
      technischerfehler.value = "";
    })
    .catch((err) => (technischerfehler.value = err.toString()))
    .finally(() => {
      isAnimation.value = false;
      if (hinweise.value) {
        hinweise.value.scrollIntoView();
      }
    });
}

function speichern(): void {
  isAnimation.value = true;
  ehrenamtJustizOnlineService
    .bewerbungSpeichern({
      vorname: onlineBewerbungFormData.value.vorname,
      nachname: onlineBewerbungFormData.value.nachname,
      geburtsdatum: onlineBewerbungFormData.value.geburtsdatum,
      telefonnummer: onlineBewerbungFormData.value.telefonnummer,
      beruf: onlineBewerbungFormData.value.beruf,
      mail: onlineBewerbungFormData.value.mail,
      dateiVerfassungstreue:
        onlineBewerbungFormData.value.dateiVerfassungstreue,
    })
    .then((bewerbunggespeichert) => {
      bewerbunggespeichertergebbnis.value = bewerbunggespeichert;
      technischerfehler.value = "";
      increaseCurrentView();
    })
    .catch((err) => (technischerfehler.value = err.toString()))
    .finally(() => {
      isAnimation.value = false;
      if (hinweise.value) {
        hinweise.value.scrollIntoView();
      }
    });
}

/**
 * creates pattern of Verfassungstreue
 */
function erstellenVerfassungstreueMuster(): void {
  ehrenamtJustizOnlineService
    .lesenVerfassungstreueMuster()
    .then((dateiVerfassungstreue) => {
      // 1) Base64 → Binary → Blob
      const binary = atob(dateiVerfassungstreue);
      const bytes = new Uint8Array(binary.length);
      for (let i = 0; i < binary.length; i++) {
        bytes[i] = binary.charCodeAt(i);
      }
      const blob = new Blob([bytes], { type: "application/pdf" });

      // 2) display PDF in browser
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "VerfassungstreueMuster.pdf";
      link.click();

      // Cleanup
      URL.revokeObjectURL(url);
    })
    .catch((err) => (technischerfehler.value = err.toString()));
}
</script>

<style scoped>
.v-card-main-container {
  padding-left: 3%;
  padding-right: 3%;
}

.red-star {
  color: red;
  font-size: 20px;
}

.button-row {
  padding-top: 15px;
  padding-left: 15px;
  padding-bottom: 50px;
}

.button-wrapper {
  margin: 0;
  margin-top: 0;
}
#headerOnlineBewerbung {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.m-form-group {
  width: 100%;
}
:deep(.container) {
  /*Stepper with:*/
  width: 1350px;
}
:deep(.m-form-step) {
  width: 100%;
}
</style>
