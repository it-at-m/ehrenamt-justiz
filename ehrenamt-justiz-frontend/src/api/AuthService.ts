import { STATUS_INDICATORS } from "@/Constants";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserInfoStore } from "@/stores/userinfo";

// eslint-disable-next-line @typescript-eslint/no-extraneous-class
export default class AuthService {
  static checkAuth(authority: string, t: (key: string) => string): boolean {
    const snackbarStore = useSnackbarStore();
    const userInfoStore = useUserInfoStore();
    const userInfo = userInfoStore.getUserInfo;

    if (!userInfo || !userInfo.authorities) {
      // user not yet read from database
      snackbarStore.push({
        color: STATUS_INDICATORS.WARNING,
        text: t("api.message.keinUserObject"),
      });
      return false;
    } else {
      return userInfo.authorities.includes(authority);
    }
  }
}
