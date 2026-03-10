import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";

// eslint-disable-next-line @typescript-eslint/no-extraneous-class
export default class AuthService {
  static checkAuth(authority: string, t: (key: string) => string): boolean {
    const snackbarStore = useSnackbarStore();
    const userStore = useUserStore();

    if (!userStore.getUser || !userStore.getUser.authorities) {
      // user not yet read from database
      snackbarStore.push({
        color: STATUS_INDICATORS.WARNING,
        text: t("api.message.keinUserObject"),
      });
      return false;
    } else {
      return userStore.getUser.authorities.includes(authority);
    }
  }
}
