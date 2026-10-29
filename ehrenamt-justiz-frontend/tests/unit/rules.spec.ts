import { assert, describe, test } from "vitest";

import {
  birthDayRule,
  commitmentToTheConstitutionRule,
  maxRule,
  minRule,
  nationalityRule,
  placeOfResidenceRule,
  uniqueRule,
} from "@/util/custom-rules";

const valid = (result: unknown) => assert.isTrue(result);
const error = (result: unknown) => assert.isString(result);

describe("min rule tests", () => {
  test.concurrent.for([
    { num: 5, expected: error },
    { num: 10, expected: valid },
    { num: undefined, expected: valid },
  ])("validates $num as $expected", ({ num, expected }) => {
    // given
    const validationRule = minRule(10);

    // when
    const result = validationRule(num);

    // then
    expected(result);
  });
});

describe("max rule tests", () => {
  test.concurrent.for([
    { num: 15, expected: error },
    { num: 10, expected: valid },
    { num: undefined, expected: valid },
  ])("validates $num as $expected", ({ num, expected }) => {
    // given
    const validationRule = maxRule(10);

    // when
    const result = validationRule(num);

    // then
    expected(result);
  });
});

describe("unique rule tests", () => {
  test.concurrent.for([
    {
      newValue: 10,
      existingValues: [5, 8, 10],
      initialValue: undefined,
      expected: error,
    },
    {
      newValue: 10,
      existingValues: [5, 8],
      initialValue: undefined,
      expected: valid,
    },
    {
      newValue: 10,
      existingValues: [5, 8, 10],
      initialValue: 10,
      expected: valid,
    },
    {
      newValue: undefined,
      existingValues: [5, 8, 10],
      initialValue: undefined,
      expected: valid,
    },
  ])(
    "validates $newValue as $expected",
    ({ newValue, existingValues, initialValue, expected }) => {
      // given
      const validationRule = uniqueRule(existingValues, initialValue);

      // when
      const result = validationRule(newValue);

      // then
      expected(result);
    }
  );
});

const errorMessageBirthDayRule = "Fehler in Birthday-Rule";

describe("birthday rule tests", () => {
  test.concurrent.for([
    { date: new Date("1964-10-09"), expected: true },
    { date: new Date("1901-10-09"), expected: errorMessageBirthDayRule },
    { date: new Date("2200-10-09"), expected: errorMessageBirthDayRule },
    { date: undefined, expected: errorMessageBirthDayRule },
  ])("validates date $date as $expected", ({ date, expected }, { expect }) => {
    // given
    const validationRule = birthDayRule(
      "2028-01-01",
      18,
      120,
      errorMessageBirthDayRule
    );

    // when
    const result = validationRule(date as unknown as Date);

    // then
    expect(result).toBe(expected);
  });
});

const errorMessageNationalityRule = "Fehler in nationality-Rule";

describe("Nationality rule tests", () => {
  test.concurrent.for([
    { staatsangehoerigkeitConf: ["deutsch"], expected: true },
    { staatsangehoerigkeitConf: ["deutsch", "englisch"], expected: true },
    {
      staatsangehoerigkeitConf: ["englisch"],
      expected: errorMessageNationalityRule,
    },
    {
      staatsangehoerigkeitConf: undefined,
      expected: errorMessageNationalityRule,
    },
  ])(
    "validates date $date as $expected",
    ({ staatsangehoerigkeitConf, expected }, { expect }) => {
      // given
      const validationRule = nationalityRule(
        "deutsch",
        errorMessageNationalityRule
      );

      // when
      const result = validationRule(staatsangehoerigkeitConf);

      // then
      expect(result).toBe(expected);
    }
  );
});

const errorMessagePlaceOfResidenceRule = "Fehler in nationality-Rule";

describe("Nationality rule tests", () => {
  test.concurrent.for([
    { wohnsitzConf: "München", expected: true },
    { wohnsitzConf: "Hamburg", expected: errorMessagePlaceOfResidenceRule },
    { wohnsitzConf: undefined, expected: errorMessagePlaceOfResidenceRule },
  ])(
    "validates date $date as $expected",
    ({ wohnsitzConf, expected }, { expect }) => {
      // given
      const validationRule = placeOfResidenceRule(
        "München",
        errorMessagePlaceOfResidenceRule
      );

      // when
      const result = validationRule(wohnsitzConf);

      // then
      expect(result).toBe(expected);
    }
  );
});

const errZuGross = "Die Datei ist zu gross.";
const errFalscheExtension = "Die Datei hat eine nicht erlaubte Endung.";

const maxSizeInMb = 2;
const allowedExtension = "pdf";

const KB_IN_BYTES = 1024;

const createFile = (name: string, sizeInBytes: number): File =>
  new File([new Uint8Array(sizeInBytes)], name);

describe("commitmentToTheConstitutionRule", () => {
  test.concurrent.for([
    {
      name: "kein File ausgewählt",
      file: undefined,
      expected: true,
    },
    {
      name: "gültiges PDF innerhalb der maximalen Grösse",
      file: createFile("bestaetigung.pdf", 1024),
      expected: true,
    },
    {
      name: "PDF genau an der maximalen Grösse",
      file: createFile(
        "bestaetigung.pdf",
        maxSizeInMb * KB_IN_BYTES * KB_IN_BYTES
      ),
      expected: true,
    },
    {
      name: "PDF grösser als die maximale Grösse",
      file: createFile(
        "bestaetigung.pdf",
        maxSizeInMb * KB_IN_BYTES * KB_IN_BYTES + 1
      ),
      expected: errZuGross,
    },
    {
      name: "Datei mit falscher Endung",
      file: createFile("bestaetigung.txt", 1024),
      expected: errFalscheExtension,
    },
  ])("$name", ({ file, expected }, { expect }) => {
    // given
    const rule = commitmentToTheConstitutionRule(
      maxSizeInMb,
      allowedExtension,
      errZuGross,
      errFalscheExtension
    );
    // when
    const result = rule(file);

    // then
    expect(result).toBe(expected);
  });
});
