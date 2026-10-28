// see https://vite.dev/guide/env-and-mode#intellisense-for-typescript
interface ViteTypeOptions {
  strictImportMetaEnv: unknown;
}

interface ImportMetaEnv {
  readonly VITE_AD2IMAGE_URL: string;
  readonly VITE_APPSWITCHER_URL: string;
  readonly  VITE_VUE_APP_API_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
