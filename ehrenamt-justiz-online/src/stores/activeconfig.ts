import type KonfigurationData from "@/types/KonfigurationData";
import type TechnischeKonfigurationData from "@/types/TechnischeKonfigurationData";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export interface ActiveKonfigurationState {
  konfiguration: KonfigurationData | null;
  technischeKonfiguration: TechnischeKonfigurationData | null;
}

/**
 * activeconfig is a store in which the active configuration is saved. The active configuration is defined in the core application.
 */
export const useActiveKonfigurationStore = defineStore("activeconfig", () => {
  const konfiguration = ref<KonfigurationData | null>(null);
  const technischeKonfiguration = ref<TechnischeKonfigurationData | null>(null);

  const getKonfiguration = computed((): KonfigurationData | null => {
    return konfiguration.value;
  });

  function setKonfiguration(payload: KonfigurationData): void {
    konfiguration.value = payload;
  }

  function setTechnischeKonfiguration(
    payload: TechnischeKonfigurationData
  ): void {
    technischeKonfiguration.value = payload;
  }

  const getTechnischeKonfiguration = computed(
    (): TechnischeKonfigurationData | null => {
      return technischeKonfiguration.value;
    }
  );

  return {
    getKonfiguration,
    setKonfiguration,
    getTechnischeKonfiguration,
    setTechnischeKonfiguration,
  };
});
