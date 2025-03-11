export const API_BASE: string | undefined = import.meta.env
  .VITE_VUE_APP_API_URL;
export const ROUTES_HOME = "home";
export const ROUTES_GETSTARTED = "getstarted";

export const AD2IMAGE_URL = import.meta.env.VITE_AD2IMAGE_URL;
export const APPSWITCHER_URL = import.meta.env.VITE_APPSWITCHER_URL;

export const SNACKBAR_DEFAULT_TIMEOUT = 5000;

export const enum BEARBEIGUNGS_MODUS {
  EDIT_MODUS = "edit_modus",
  DISPLAY_MODUS = "display_modus",
}

export const enum STATUS_INDICATORS {
  SUCCESS = "success",
  INFO = "info",
  WARNING = "warning",
  ERROR = "error",
}
