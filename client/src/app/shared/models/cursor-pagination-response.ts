import { PostData } from "./post-data";

export type CursorPaginationResponse<T> = {
  data: T[];
  nextCursor: string | null;
  hasNext: boolean;
  pageSize: number;
};
