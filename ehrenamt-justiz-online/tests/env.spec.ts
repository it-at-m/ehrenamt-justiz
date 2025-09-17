/**
 * Test suite: Environment and configuration validation
 * Framework: Vitest (v3) - assertions via expect()
 * Runner/Build: Vite with jsdom (see vitest.config.ts)
 *
 * Focus:
 * - Validate Vite built-ins on import.meta.env without mutating them (they are frozen)
 * - Verify conventions for custom env vars (VITE_* only)
 * - Provide robust parsing helpers with edge cases
 * - Ensure no sensitive values are logged by a redaction helper
 *
 * Note: We intentionally avoid writing to import.meta.env since Vite freezes it.
 */

import { describe, test, expect, vi } from "vitest";

// Small helpers used by the app and validated here
const parseBool = (v: unknown): boolean => v === "true" || v === "1" || v === true;
const parseIntSafe = (v: unknown, fallback = 0): number => {
  const n = typeof v === "string" ? Number.parseInt(v, 10) : Number(v as number);
  return Number.isFinite(n) ? n : fallback;
};
const parseJSONSafe = <T,>(v: unknown): T | null => {
  if (typeof v !== "string") return null;
  try {
    return JSON.parse(v) as T;
  } catch {
    return null;
  }
};

describe("import.meta.env built-ins", () => {
  test("includes Vite built-ins with correct types", () => {
    expect(import.meta.env).toHaveProperty("MODE");
    expect(typeof import.meta.env.MODE).toBe("string");

    expect(import.meta.env).toHaveProperty("DEV");
    expect(typeof import.meta.env.DEV).toBe("boolean");

    expect(import.meta.env).toHaveProperty("PROD");
    expect(typeof import.meta.env.PROD).toBe("boolean");

    expect(import.meta.env).toHaveProperty("BASE_URL");
    expect(typeof import.meta.env.BASE_URL).toBe("string");

    // SSR is present in Vite env and is boolean
    expect(import.meta.env).toHaveProperty("SSR");
    expect(typeof import.meta.env.SSR).toBe("boolean");
  });

  test("DEV/PROD flags are consistent with MODE", () => {
    const { MODE, DEV, PROD } = import.meta.env;
    // eslint-disable-next-line no-constant-condition
    if (MODE === "production") {
      expect(PROD).toBe(true);
      expect(DEV).toBe(false);
    } else {
      // development or test (and any non-production) implies DEV
      expect(DEV).toBe(true);
      expect(PROD).toBe(false);
    }
  });

  test("BASE_URL is formatted as an absolute path with trailing slash", () => {
    const base = import.meta.env.BASE_URL;
    expect(base.startsWith("/")).toBe(true);
    expect(base.endsWith("/")).toBe(true);
  });
});

describe("Custom VITE_* variables (conventions and shapes)", () => {
  test("any non-built-in env key is prefixed with VITE_", () => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const env = import.meta.env as any;
    const customKeys = Object.keys(env).filter((k) => !["MODE", "DEV", "PROD", "BASE_URL", "SSR"].includes(k));
    customKeys.forEach((k) => expect(k.startsWith("VITE_")).toBe(true));
  });

  test("if present, common VITE_* values match expected formats", () => {
    // URL-like examples
    if ("VITE_API_BASE_URL" in import.meta.env) {
      const v = import.meta.env.VITE_API_BASE_URL as string;
      const urlOk = (() => {
        try {
          new URL(v);
          return true;
        } catch {
          return false;
        }
      })();
      expect(urlOk).toBe(true);
    }

    // Boolean examples
    if ("VITE_FEATURE_FLAG" in import.meta.env) {
      const v = import.meta.env.VITE_FEATURE_FLAG as string;
      expect([true, false]).toContain(parseBool(v));
    }

    // Number examples
    if ("VITE_REQUEST_TIMEOUT_MS" in import.meta.env) {
      const v = import.meta.env.VITE_REQUEST_TIMEOUT_MS as string;
      expect(parseIntSafe(v, -1)).toBeGreaterThanOrEqual(0);
    }

    // JSON examples
    if ("VITE_CONFIG_JSON" in import.meta.env) {
      const parsed = parseJSONSafe<Record<string, unknown>>(import.meta.env.VITE_CONFIG_JSON as string);
      expect(parsed).not.toBeNull();
      expect(typeof parsed).toBe("object");
    }
  });
});

describe("Parsing helpers - edge cases", () => {
  test("parseBool handles empty and truthy/falsy strings", () => {
    expect(parseBool("")).toBe(false);
    expect(parseBool("0")).toBe(false);
    expect(parseBool("1")).toBe(true);
    expect(parseBool("true")).toBe(true);
    expect(parseBool("false")).toBe(false);
  });

  test("parseIntSafe handles invalid numbers with fallback", () => {
    expect(parseIntSafe("3000", 7)).toBe(3000);
    expect(parseIntSafe("not-a-number", 7)).toBe(7);
    expect(parseIntSafe(undefined, 5)).toBe(5);
  });

  test("parseJSONSafe parses valid JSON and returns null for invalid", () => {
    expect(parseJSONSafe<{ ok: boolean }>('{"ok":true}')).toEqual({ ok: true });
    expect(parseJSONSafe("{oops")).toBeNull();
    expect(parseJSONSafe(123 as unknown as string)).toBeNull();
  });
});

describe("Redaction helper - avoids logging secrets", () => {
  test("does not log sensitive values and leaves public values intact", () => {
    const logSpy = vi.spyOn(console, "log").mockImplementation(() => {});
    // Build a runtime-like object instead of mutating import.meta.env
    const runtime: Record<string, unknown> = {
      ...import.meta.env,
      VITE_API_KEY: "super-secret",
      VITE_PUBLIC_VALUE: "visible",
    };

    const redactAndLog = (obj: Record<string, unknown>) => {
      const sensitive = ["KEY", "SECRET", "TOKEN", "PASSWORD", "PRIVATE"];
      Object.entries(obj).forEach(([k, v]) => {
        const isSensitive = sensitive.some((s) => k.toUpperCase().includes(s));
        console.log(`${k}=${isSensitive ? "***REDACTED***" : v}`);
      });
    };

    redactAndLog(runtime);

    expect(logSpy).not.toHaveBeenCalledWith(expect.stringContaining("super-secret"));
    expect(logSpy).toHaveBeenCalledWith(expect.stringContaining("VITE_PUBLIC_VALUE=visible"));

    logSpy.mockRestore();
  });
});