import { Privacy } from "./privacy.enum";

export type CreatePostDto = {
  content?: string;
  mediaUrls?: string[];
  privacy?: Privacy;
};
