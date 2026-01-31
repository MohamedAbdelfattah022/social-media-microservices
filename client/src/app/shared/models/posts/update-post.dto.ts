import { Privacy } from './privacy.enum';

export type UpdatePostDto = {
  content?: string;
  fileIds?: string[];
  privacy?: Privacy;
};
