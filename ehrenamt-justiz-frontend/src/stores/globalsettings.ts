import type KonfigurationData from "@/types/KonfigurationData";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useGlobalSettingsStore = defineStore("globalSetting", () => {
  const konfiguration = ref<KonfigurationData | null>(null);
  const onlineHelpDialogComponentVisible = ref(false);

  const getKonfiguration = computed((): KonfigurationData | null => {
    return konfiguration.value;
  });

  function setKonfiguration(payload: KonfigurationData | null): void {
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
