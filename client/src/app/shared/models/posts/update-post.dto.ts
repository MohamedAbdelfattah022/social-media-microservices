import { Privacy } from "./privacy.enum";

export type UpdatePostDto = {
  content?: string;
  mediaUrls?: string[];
  privacy?: Privacy;
};
