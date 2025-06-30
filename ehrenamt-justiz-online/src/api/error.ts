export const enum Levels {
  INFO = "info",
  WARNING = "warning",
  ERROR = "error",
}

/**
 * This class extends the Error class by one level
 */
export class ApiError extends Error {
  level: string;
  constructor(level: string, message: string) {
    super(message);

    this.stack = new Error().stack;

    this.level = level;
    this.message = message;
  }
}
