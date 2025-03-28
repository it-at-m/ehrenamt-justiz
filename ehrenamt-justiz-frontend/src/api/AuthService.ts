import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";

// eslint-disable-next-line @typescript-eslint/no-extraneous-class
export default class AuthService {
  static checkAuth(authority: string): boolean {
    const snackbarStore = useSnackbarStore();
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
    }
  }
}
