class User {
  sub = "";

  // LHM
  displayName = "";
  surname = "";
  telephoneNumber = "";
  email = "";
  username = "";
  given_name = "";
  family_name = "";
  department = "";
  lhmObjectID = "";
  // LHM_Extended
  preferred_username = "";
  memberof: string[] = [];
  user_roles: string[] = [];
  authorities: string[] = [];
}

export function UserLocalDevelopment(): User {
  const u = new User();
  u.username = "Local Development User";
  u.displayName = "Local Development User";
  u.authorities = [
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
  ];
  u.user_roles = [
    // todo add user roles
  ];
  return u;
}

export default User;
