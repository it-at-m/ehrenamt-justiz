export interface UserInfo {
  sub: string;

  // LHM
  displayName: string;
  surname: string;
  telephoneNumber: string;
  email: string;
  username: string;
  givenname: string;
  family_name: string;
  department: string;
  lhmObjectID: string;

  // LHM_Extended
  preferred_username: string;
  memberof: string[];
  user_roles: string[];
  authorities: string[];
}

export const USERINFO_LOCAL_DEVELOPMENT: UserInfo = {
  sub: "",
  displayName: "Local Development UserInfo",
  surname: "",
  telephoneNumber: "",
  email: "",
  username: "Local Development UserInfo",
  givenname: "",
  family_name: "",
  department: "",
  lhmObjectID: "",
  preferred_username: "",
  memberof: [],
  user_roles: [
    // todo add user roles
  ],
  authorities: [
    "READ_KONFIGURATION",
    "WRITE_KONFIGURATION",
    "DELETE_KONFIGURATION",
    "READ_EWOBUERGER",
    "WRITE_EWOBUERGER",
    "DELETE_EWOBUERGER",
    "READ_EHRENAMTJUSTIZDATEN",
    "WRITE_EHRENAMTJUSTIZDATEN",
    "DELETE_EHRENAMTJUSTIZDATEN",
    "READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE",
    "EWOSUCHE",
    "EWOSUCHEMITOM",
    "ONLINEBEWERBEN",
  ],
};

export const USERINFO_EMPTY: UserInfo = {
  sub: "",

  // LHM
  displayName: "",
  surname: "",
  telephoneNumber: "",
  email: "",
  username: "",
  givenname: "",
  family_name: "",
  department: "",
  lhmObjectID: "",

  // LHM_Extended
  preferred_username: "",
  memberof: [],
  user_roles: [],
  authorities: [],
};
