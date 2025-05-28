import type KonfigurationData from "@/types/KonfigurationData";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useGlobalSettingsStore = defineStore("globalSetting", () => {
  const konfiguration = ref<KonfigurationData | null>(null);
  const konfigurationLoadingAttempt = ref(false);
  const onlineHelpDialogComponentVisible = ref(false);

  const getKonfiguration = computed((): KonfigurationData | null => {
    return konfiguration.value;
  });

  function setKonfiguration(payload: KonfigurationData | null): void {
    konfiguration.value = payload;
  }

  function setKonfigurationLoadingAttempt(newValue: boolean): void {
    konfigurationLoadingAttempt.value = newValue;
  }
  function isKonfigurationLoadingAttempt() {
    return konfigurationLoadingAttempt.value;
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
    setKonfigurationLoadingAttempt,
    isKonfigurationLoadingAttempt: isKonfigurationLoadingAttempt,
    setOnlineHelpDialogComponentVisible,
    isOnlineHelpDialogComponentVisible: isOnlineHelpDialogComponentVisible,
  };
});
