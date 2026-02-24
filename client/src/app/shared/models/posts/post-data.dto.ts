export type PostData = {
  id: number;
  userId: string;
  username: string;
  firstName: string | null;
  lastName: string | null;
  profilePictureUrl?: string;
  content: string;
  privacy: string;
  mediaUrls?: string[];
  edited: boolean;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
};
