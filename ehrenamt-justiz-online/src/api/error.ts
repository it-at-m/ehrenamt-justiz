export const enum Levels {
  INFO = "info",
  WARNING = "warning",
  ERROR = "error",
}

export class ApiError extends Error {
  level: string;
  constructor(level: string, message: string) {
    super(message);

    this.stack = new Error().stack;

    this.level = level;
    this.message = message;
  }
}
