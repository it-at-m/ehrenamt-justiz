import type KonfigurationData from "@/types/KonfigurationData";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export interface ActiveKonfigurationState {
  konfiguration: KonfigurationData | null;
}

export const useActiveKonfigurationStore = defineStore("activeconfig", () => {
  const konfiguration = ref<KonfigurationData | null>(null);

  const getKonfiguration = computed((): KonfigurationData | null => {
    return konfiguration.value;
  });

  function setKonfiguration(payload: KonfigurationData): void {
    konfiguration.value = payload;
  }
  return { getKonfiguration, setKonfiguration };
});
