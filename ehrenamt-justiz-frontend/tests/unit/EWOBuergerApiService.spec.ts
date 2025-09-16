/**
 * Unit tests for EWOBuergerApiService.
 * Framework: Detected from repository (Jest/Vitest). These tests use describe/it style and jest-compatible mocks.
 * If using Vitest, the jest APIs are compatible via the provided globals; otherwise make sure to configure test environment accordingly.
 *
 * Focus: Behavior in the diffed service methods:
 * - getEwoSuche
 * - ewoSucheMitOM
 * - pruefenNeuePerson
 * - vorbereitenUndSpeichernPerson
 * - checkEwoEaiStatus
 * - checkAenderungsserviceStatus
 *
 * We mock:
 * - global fetch
 * - @/api/EntityApiService (static methods + base URL)
 * - @/api/FetchUtils (getGETConfig, getPOSTConfig)
 * - @/Constants.ts (STATUS_INDICATORS)
 */

import { beforeEach, afterEach, describe, it, expect, vi } from "vitest"; // Vitest preferred; in Jest, replace with jest globals or enable jest-compat
// If project uses Jest, comment the line above and rely on global jest/describe/it/expect.

vi.mock("@/api/FetchUtils", () => ({
  getGETConfig: vi.fn(() => ({ method: "GET", headers: { Accept: "application/json" } })),
  getPOSTConfig: vi.fn((body: unknown) => ({ method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) })),
}));

vi.mock("@/Constants.ts", () => ({
  STATUS_INDICATORS: { ERROR: "error" },
}));

// EntityApiService mock: instance base URL via instance.getBaseUrl stubbing; static getBaseUrl; error/response handlers
const handleWrongResponse = vi.fn();
const handleError = vi.fn((e: unknown) => e);
const staticBaseUrl = "http://static-base";
vi.mock("@/api/EntityApiService", () => {
  class MockEntityApiService<T> {
    protected entity = "";
    protected entities = "";
    constructor(entity: string, entities: string) {
      this.entity = entity; this.entities = entities;
    }
    // Real service calls this.getBaseUrl() on instance methods; we override per-test via spy.
    protected getBaseUrl(): string {
      return "http://instance-base";
    }
    static getBaseUrl(): string {
      return staticBaseUrl;
    }
    static handleWrongResponse = handleWrongResponse;
    static handleError = handleError;
  }
  return { default: MockEntityApiService };
});

// Import after mocks to ensure they take effect
import { EWOBuergerApiService } from "@/api/EWOBuergerApiService"; // Path alias '@' must be configured in ts/jest/vitest config

// Bring mocked helpers for assertions
import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";

type FetchResponseInit = {
  status?: number;
  ok?: boolean;
  headers?: Record<string, string>;
  jsonData?: any;
  textData?: string;
  jsonRejectsWith?: any;
  textRejectsWith?: any;
};

function makeFetchResponse(init: FetchResponseInit = {}) {
  const {
    status = 200,
    ok = status >= 200 && status < 300,
    headers = {},
    jsonData = undefined,
    textData = "",
    jsonRejectsWith,
    textRejectsWith,
  } = init;

  const headersMap = new Map<string, string>(Object.entries(headers));
  const res: any = {
    status,
    ok,
    headers: { get: (k: string) => headersMap.get(k) ?? null },
    json: vi.fn(() => {
      if (jsonRejectsWith !== undefined) return Promise.reject(jsonRejectsWith);
      return Promise.resolve(jsonData);
    }),
    text: vi.fn(() => {
      if (textRejectsWith !== undefined) return Promise.reject(textRejectsWith);
      return Promise.resolve(textData);
    }),
  };
  return res;
}

declare global {
  // eslint-disable-next-line no-var
  var fetch: any;
}

describe("EWOBuergerApiService", () => {
  const instanceBaseUrl = "http://instance-base-override";

  beforeEach(() => {
    // Reset mocks
    vi.clearAllMocks();
    // Provide global fetch mock
    global.fetch = vi.fn();
    // Override instance getBaseUrl used by most methods
    // @ts-expect-error Accessing protected for test by casting
    vi.spyOn((EWOBuergerApiService as any).__proto__ ?? Object.getPrototypeOf(EWOBuergerApiService), "getBaseUrl")
      .mockReturnValue(instanceBaseUrl);
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe("getEwoSuche", () => {
    it("POSTs to /ehrenamtjustiz/ewoSuche and resolves data on 200", async () => {
      const payload = { query: "abc" } as any;
      const data = [{ id: 1 }, { id: 2 }];
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonData: data }));

      const result = await EWOBuergerApiService.getEwoSuche(payload as any);

      expect(getPOSTConfig).toHaveBeenCalledWith(payload);
      expect(global.fetch).toHaveBeenCalledWith(
        `${instanceBaseUrl}/ehrenamtjustiz/ewoSuche`,
        expect.objectContaining({ method: "POST" })
      );
      expect(result).toEqual(data);
      expect(handleWrongResponse).not.toHaveBeenCalled();
    });

    it("rejects with ApiError via handleError when non-200 and errormessage header present", async () => {
      const payload = { query: "x" } as any;
      // Mimic backend header payload containing message field
      const errHeader = '{"timestamp":"2025-09-16","message":"Kaboom happened","other":true}';
      (global.fetch as any).mockResolvedValue(
        makeFetchResponse({ status: 500, ok: false, headers: { errormessage: errHeader } })
      );

      await expect(EWOBuergerApiService.getEwoSuche(payload as any)).rejects.toMatchObject({
        // handleError returns the same error instance in our mock
        message: expect.stringContaining("Kaboom happened - HTTP Status Code: 500"),
      });

      expect(handleWrongResponse).not.toHaveBeenCalled();
    });

    it("calls handleWrongResponse and rejects when non-200 and no errormessage header", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 403, ok: false }));

      await expect(EWOBuergerApiService.getEwoSuche({} as any)).rejects.toBeUndefined();
      expect(handleWrongResponse).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({ status: 403 }));
    });

    it("rejects via handleError when response.json() throws", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonRejectsWith: new Error("bad json") }));

      await expect(EWOBuergerApiService.getEwoSuche({} as any)).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });

    it("rejects via handleError on fetch network error", async () => {
      (global.fetch as any).mockRejectedValue(new Error("network down"));

      await expect(EWOBuergerApiService.getEwoSuche({} as any)).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });
  });

  describe("ewoSucheMitOM", () => {
    it("GETs with query param and resolves JSON on ok", async () => {
      const om = "OM123";
      const data = { id: 42 };
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonData: data }));

      const result = await EWOBuergerApiService.ewoSucheMitOM(om);

      expect(getGETConfig).toHaveBeenCalled();
      expect(global.fetch).toHaveBeenCalledWith(
        `${instanceBaseUrl}/ehrenamtjustiz/ewoSucheMitOM?om=${om}`,
        expect.objectContaining({ method: "GET" })
      );
      expect(result).toEqual(data);
    });

    it("handles non-ok by calling handleWrongResponse and rejecting", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 400, ok: false, jsonData: {} }));

      await expect(EWOBuergerApiService.ewoSucheMitOM("X")).rejects.toBeUndefined();
      expect(handleWrongResponse).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({ status: 400 }));
    });

    it("rejects via handleError when json parsing fails", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonRejectsWith: new Error("oops") }));

      await expect(EWOBuergerApiService.ewoSucheMitOM("Y")).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });

    it("rejects via handleError on network error", async () => {
      (global.fetch as any).mockRejectedValue(new Error("down"));

      await expect(EWOBuergerApiService.ewoSucheMitOM("Z")).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });
  });

  describe("pruefenNeuePerson", () => {
    it("resolves null on 404 without parsing JSON", async () => {
      const res = makeFetchResponse({ status: 404, ok: false });
      (global.fetch as any).mockResolvedValue(res);

      const result = await EWOBuergerApiService.pruefenNeuePerson({} as any);

      expect(result).toBeNull();
      expect(res.json).not.toHaveBeenCalled();
    });

    it("resolves JSON on ok", async () => {
      const person = { id: 7 };
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonData: person }));

      const result = await EWOBuergerApiService.pruefenNeuePerson({} as any);

      expect(result).toEqual(person);
    });

    it("calls handleWrongResponse and rejects when not ok (non-404)", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 500, ok: false, jsonData: {} }));

      await expect(EWOBuergerApiService.pruefenNeuePerson({} as any)).rejects.toBeUndefined();
      expect(handleWrongResponse).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({ status: 500 }));
    });

    it("rejects via handleError when json parsing fails", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonRejectsWith: new Error("bad") }));

      await expect(EWOBuergerApiService.pruefenNeuePerson({} as any)).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });

    it("rejects via handleError on network error", async () => {
      (global.fetch as any).mockRejectedValue(new Error("netz"));

      await expect(EWOBuergerApiService.pruefenNeuePerson({} as any)).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });
  });

  describe("vorbereitenUndSpeichernPerson", () => {
    it("parses JSON and resolves on ok", async () => {
      const person = { id: 12 };
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonData: person }));

      const result = await EWOBuergerApiService.vorbereitenUndSpeichernPerson({} as any);

      expect(getPOSTConfig).toHaveBeenCalled();
      expect(global.fetch).toHaveBeenCalledWith(
        `${instanceBaseUrl}/ehrenamtjustiz/vorbereitenUndSpeichernPerson`,
        expect.objectContaining({ method: "POST" })
      );
      expect(result).toEqual(person);
    });

    it("calls handleWrongResponse and rejects when not ok after JSON", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 500, ok: false, jsonData: { err: true } }));

      await expect(EWOBuergerApiService.vorbereitenUndSpeichernPerson({} as any)).rejects.toBeUndefined();
      expect(handleWrongResponse).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({ status: 500 }));
    });

    it("rejects via handleError when JSON parsing fails", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, jsonRejectsWith: new Error("parse") }));

      await expect(EWOBuergerApiService.vorbereitenUndSpeichernPerson({} as any)).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });

    it("rejects via handleError on network error", async () => {
      (global.fetch as any).mockRejectedValue(new Error("offline"));

      await expect(EWOBuergerApiService.vorbereitenUndSpeichernPerson({} as any)).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });
  });

  describe("checkEwoEaiStatus", () => {
    it("uses static base URL and resolves text on ok", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, textData: "OK" }));

      const result = await EWOBuergerApiService.checkEwoEaiStatus();

      expect(getGETConfig).toHaveBeenCalled();
      expect(global.fetch).toHaveBeenCalledWith(
        `${staticBaseUrl}/ehrenamtjustiz/ewoEaiStatus`,
        expect.objectContaining({ method: "GET" })
      );
      expect(result).toBe("OK");
    });

    it("calls handleWrongResponse and rejects when not ok", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 502, ok: false, textData: "Bad" }));

      await expect(EWOBuergerApiService.checkEwoEaiStatus()).rejects.toBeUndefined();
      expect(handleWrongResponse).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({ status: 502 }));
    });

    it("rejects via handleError when text() fails", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, textRejectsWith: new Error("nope") }));

      await expect(EWOBuergerApiService.checkEwoEaiStatus()).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });

    it("rejects via handleError on network error", async () => {
      (global.fetch as any).mockRejectedValue(new Error("cut"));

      await expect(EWOBuergerApiService.checkEwoEaiStatus()).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });
  });

  describe("checkAenderungsserviceStatus", () => {
    it("uses static base URL and resolves text on ok", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, textData: "RUNNING" }));

      const result = await EWOBuergerApiService.checkAenderungsserviceStatus();

      expect(getGETConfig).toHaveBeenCalled();
      expect(global.fetch).toHaveBeenCalledWith(
        `${staticBaseUrl}/ehrenamtjustiz/aenderungsserviceStatus`,
        expect.objectContaining({ method: "GET" })
      );
      expect(result).toBe("RUNNING");
    });

    it("calls handleWrongResponse and rejects when not ok", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 404, ok: false, textData: "NF" }));

      await expect(EWOBuergerApiService.checkAenderungsserviceStatus()).rejects.toBeUndefined();
      expect(handleWrongResponse).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({ status: 404 }));
    });

    it("rejects via handleError when text() fails", async () => {
      (global.fetch as any).mockResolvedValue(makeFetchResponse({ status: 200, textRejectsWith: new Error("fail") }));

      await expect(EWOBuergerApiService.checkAenderungsserviceStatus()).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });

    it("rejects via handleError on network error", async () => {
      (global.fetch as any).mockRejectedValue(new Error("drop"));

      await expect(EWOBuergerApiService.checkAenderungsserviceStatus()).rejects.toEqual(expect.any(Error));
      expect(handleError).toHaveBeenCalledWith(expect.any(Error));
    });
  });
});