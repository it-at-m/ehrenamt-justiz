import type Konfiguration from "@/types/Konfiguration";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useGlobalSettingsStore = defineStore("globalSetting", () => {
  const konfiguration = ref<Konfiguration | null>(null);
  const onlineHelpDialogComponentVisible = ref(false);

  const getKonfiguration = computed((): Konfiguration | null => {
    return konfiguration.value;
  });

  function setKonfiguration(payload: Konfiguration | null): void {
    konfiguration.value = payload;
  }

  function setOnlineHelpDialogComponentVisible(newValue: boolean): void {
    onlineHelpDialogComponentVisible.value = newValue;
  }
  function getOnlineHelpDialogComponentVisible() {
    return onlineHelpDialogComponentVisible.value;
  }

  return {
    getKonfiguration,
    setKonfiguration,
    setOnlineHelpDialogComponentVisible,
    getOnlineHelpDialogComponentVisible,
  };
});
