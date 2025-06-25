import jsEslintConfig from "@eslint/js";
import vueI18nEslintConfig from "@intlify/eslint-plugin-vue-i18n";
import vuePrettierEslintConfigSkipFormatting from "@vue/eslint-config-prettier/skip-formatting";
import {
  defineConfigWithVueTs,
  vueTsConfigs,
} from "@vue/eslint-config-typescript";
import { ESLint } from "eslint";
import vueEslintConfig from "eslint-plugin-vue";

export default defineConfigWithVueTs(
  ESLint.defaultConfig,
  jsEslintConfig.configs.recommended,
  vueEslintConfig.configs["flat/essential"],
  vueTsConfigs.strict,
  vueTsConfigs.stylistic,
  vueI18nEslintConfig.configs.recommended,
  vuePrettierEslintConfigSkipFormatting,
  {
    ignores: ["dist", "target", "node_modules", "env.d.ts"],
  },
  {
    linterOptions: {
      reportUnusedDisableDirectives: "error",
      reportUnusedInlineConfigs: "error",
    },
    rules: {
      "no-console": ["error", { allow: ["debug"] }],
      "vue/component-name-in-template-casing": [
        "error",
        "kebab-case",
        { registeredComponentsOnly: false },
      ],
      // Enforce i18n best practices manually as no stylistic ruleset exists yet
      "@intlify/vue-i18n/key-format-style": ["error"], // enforce camelCase for message keys
      "@intlify/vue-i18n/no-duplicate-keys-in-locale": ["error"],
      "@intlify/vue-i18n/no-missing-keys-in-other-locales": ["error"],
      "@intlify/vue-i18n/no-unknown-locale": ["error"],
      "@intlify/vue-i18n/no-unused-keys": [
        "error",
        {
          src: "./src",
          extensions: [".ts", ".vue"],
          ignores: [
            // These keys are used in EhrenamtJustizOnlineService.ts and cannot be detected by static analysis:
            "ehrenamtJustizOnlineService.fehlermeldungen.default",
            "ehrenamtJustizOnlineService.fehlermeldungen.http404",
            "ehrenamtJustizOnlineService.fehlermeldungen.http409",
            "ehrenamtJustizOnlineService.fehlermeldungen.http500",
            "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodeGetAktiveKonfiguration",
            "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodeBewerbungSpeichern",
          ],
        },
      ],
    },
    settings: {
      "vue-i18n": {
        localeDir: "./src/locales/*.json",
        messageSyntaxVersion: "^11.0.0",
      },
    },
  }
);
