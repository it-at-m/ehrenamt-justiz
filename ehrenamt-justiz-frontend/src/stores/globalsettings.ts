import type KonfigurationData from "@/types/KonfigurationData";
import type TechnischeKonfigurationData from "@/types/TechnischeKonfigurationData";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useGlobalSettingsStore = defineStore("globalSetting", () => {
  const konfiguration = ref<KonfigurationData | null>(null);
  const technischeKonfiguration = ref<TechnischeKonfigurationData | null>(null);
  const konfigurationLoadingAttempt = ref(false);
  const technischeKonfigurationLoadingAttempt = ref(false);
  const onlineHelpDialogComponentVisible = ref(false);

  const getKonfiguration = computed((): KonfigurationData | null => {
    return konfiguration.value;
  });

  function setKonfiguration(payload: KonfigurationData | null): void {
    konfiguration.value = payload;
  }

  const getTechnischeKonfiguration = computed(
    (): TechnischeKonfigurationData | null => {
      return technischeKonfiguration.value;
    }
  );

  function setTechnischeKonfiguration(
    payload: TechnischeKonfigurationData | null
  ): void {
    technischeKonfiguration.value = payload;
  }

  function setKonfigurationLoadingAttempt(newValue: boolean): void {
    konfigurationLoadingAttempt.value = newValue;
  }
  function isKonfigurationLoadingAttempt() {
    return konfigurationLoadingAttempt.value;
  }

  function setTechnischeKonfigurationLoadingAttempt(newValue: boolean): void {
    technischeKonfigurationLoadingAttempt.value = newValue;
  }
  function isTechnischeKonfigurationLoadingAttempt() {
    return technischeKonfigurationLoadingAttempt.value;
  }

  function setOnlineHelpDialogComponentVisible(newValue: boolean): void {
    onlineHelpDialogComponentVisible.value = newValue;
  }
  function isOnlineHelpDialogComponentVisible() {
    return onlineHelpDialogComponentVisible.value;
  }

  return {
    getKonfiguration,
    setKonfiguration,
    getTechnischeKonfiguration,
    setTechnischeKonfiguration,
    setKonfigurationLoadingAttempt,
    isKonfigurationLoadingAttempt: isKonfigurationLoadingAttempt,
    setTechnischeKonfigurationLoadingAttempt,
    isTechnischeKonfigurationLoadingAttempt:
      isTechnischeKonfigurationLoadingAttempt,
    setOnlineHelpDialogComponentVisible,
    isOnlineHelpDialogComponentVisible: isOnlineHelpDialogComponentVisible,
  };
});
