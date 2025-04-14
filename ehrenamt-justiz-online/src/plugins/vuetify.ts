// @ts-expect-error: "TS2307 cannot find module" is a false positive here
import "vuetify/styles";
import "@mdi/font/css/materialdesignicons.css";

import { createVuetify } from "vuetify";

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
});
