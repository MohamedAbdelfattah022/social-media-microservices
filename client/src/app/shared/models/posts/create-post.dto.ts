import { Privacy } from './privacy.enum';

export type CreatePostDto = {
  content?: string;
  fileIds?: string[];
  privacy?: Privacy;
};
