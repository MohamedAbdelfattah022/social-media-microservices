import { PostData } from "./posts/post-data.dto";

export type CursorPaginationResponse<T> = {
  data: T[];
  nextCursor: string | null;
  hasNext: boolean;
  pageSize: number;
};
