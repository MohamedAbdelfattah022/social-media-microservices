import { PostData } from "./post-data";

export type CursorPaginationResponse = {
  data: PostData[];
  nextCursor: string | null;
  hasNext: boolean;
  pageSize: number;
};
