export type UserProfileData = {
  id: string;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  bio?: string;
  profilePictureUrl: string;
  followerCount: number;
  followingCount: number;
};
