import type Konfiguration from "@/types/Konfiguration";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

export interface ActiveKonfigurationState {
  konfiguration: Konfiguration | null;
}

export const useActiveKonfigurationStore = defineStore("activeconfig", () => {
  const konfiguration = ref<Konfiguration | null>(null);

  const getKonfiguration = computed((): Konfiguration | null => {
    return konfiguration.value;
  });

  function setKonfiguration(payload: Konfiguration): void {
    konfiguration.value = payload;
  }
  return { getKonfiguration, setKonfiguration };
});
