/*import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";
import {STATUS_INDICATORS} from "@/Constants.ts";*/

// eslint-disable-next-line @typescript-eslint/no-extraneous-class
export default class AuthService {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  static checkAuth(authority: string): boolean {
    return true;

    /*    const snackbarStore = useSnackbarStore();
    const userStore = useUserStore();

    if (!userStore.getUser || !userStore.getUser.authorities) {
      // user not yet read from database
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
