import type { App } from "vue";

import i18n from "@/plugins/i18n";
import pinia from "./pinia";
import vuetify from "./vuetify";

export function registerPlugins(app: App) {
  app.use(vuetify).use(pinia).use(i18n);
}
