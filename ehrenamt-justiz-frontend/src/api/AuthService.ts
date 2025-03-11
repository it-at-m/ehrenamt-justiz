/*import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";
import {STATUS_INDICATORS} from "@/Constants.ts";*/

export default class AuthService {
  static checkAuth(authority: string): boolean {
    return true;

    /*    const snackbarStore = useSnackbarStore();
    const userStore = useUserStore();

    if (!userStore.getUser || !userStore.getUser.authorities) {
      //user (noch) nicht geladen
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.WARNING,
        message: "Kein User-Objekt. Navigiere zu Startseite.",
      });
      return false;
    } else {
      return userStore.getUser.authorities.includes(authority);
    }*/
  }
}
