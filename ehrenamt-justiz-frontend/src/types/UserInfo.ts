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
