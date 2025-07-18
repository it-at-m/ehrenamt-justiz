// @ts-expect-error: "TS2307 cannot find module" is a false positive here
import "vuetify/styles";
import "@mdi/font/css/materialdesignicons.css";

import type { VueI18nAdapterParams } from "vuetify/locale/adapters/vue-i18n";

import { useI18n } from "vue-i18n";
import { createVuetify } from "vuetify";
import { createVueI18nAdapter } from "vuetify/locale/adapters/vue-i18n";

import i18n from "@/plugins/i18n";

export default createVuetify({
  theme: {
    themes: {
      light: {
        colors: {
          primary: "#333333",
          secondary: "#FFCC00",
          accent: "#0D47A1",
          success: "#69BE28",
          error: "#FF0000",
        },
      },
    },
  },
  locale: {
    adapter: createVueI18nAdapter({ i18n, useI18n } as VueI18nAdapterParams),
  },
});
