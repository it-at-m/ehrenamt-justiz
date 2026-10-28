interface ViteTypeOptions {
  strictImportMetaEnv: unknown;
}

interface ImportMetaEnv {
  readonly VITE_VUE_APP_API_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
